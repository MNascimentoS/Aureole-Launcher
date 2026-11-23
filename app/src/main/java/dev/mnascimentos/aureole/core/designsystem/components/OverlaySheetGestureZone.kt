package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.GestureZoneState
import dev.mnascimentos.aureole.core.designsystem.model.SheetHandleAction
import dev.mnascimentos.aureole.core.designsystem.model.SheetHandleBarHeight
import dev.mnascimentos.aureole.core.designsystem.model.sheetHandleActions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LongPressViewConfiguration

/** Holding the gesture zone this long opens the Aureole settings. */
private const val GESTURE_ZONE_LONG_PRESS_TIMEOUT_MS = 600L

/**
 * Handle at the top of the overlay sheet (RF-C1, RF-C2, D-05): a short rounded bar, centered in the same thin strip
 * the sheet always had (so no sheet grows), with a touch area as tall as the minimum touch target running from the
 * strip down over the top of the header.
 *
 * Double tap toggles between the rest and expanded heights (a short bounce when the content already fits); holding
 * it opens the settings. Dragging is handled by the sheet surface around it, so a drag that starts on the handle
 * resizes the sheet too.
 * TalkBack reads it as "Close" (activating it dismisses the sheet) and gets resize and settings as custom actions.
 *
 * @param isExpanded Whether the sheet is expanded, to label the resize action.
 * @param isLeftHanded Thumb side; sets the order of the accessibility actions.
 * @param onToggleExpanded Switches between rest and expanded (or bounces when the sheet cannot expand). Owns the
 * segment tick.
 * @param onSettingsClick Opens the Aureole settings; null hides the action and disables holding.
 * @param onDismiss Closes the sheet from TalkBack; null (a locked sheet) leaves the handle without that action.
 * @param canExpand False when the content already fits at rest: TalkBack then gets no "Expand" action.
 */
@Composable
internal fun OverlaySheetGestureZone(
    state: GestureZoneState,
    onToggleExpanded: () -> Unit,
    onSettingsClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    val haptics = LocalHapticFeedback.current
    val currentToggle by rememberUpdatedState(onToggleExpanded)
    val currentSettings by rememberUpdatedState(onSettingsClick)
    val onHold = {
        currentSettings?.let { openSettings ->
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            openSettings()
        }
        Unit
    }
    val description = stringResource(R.string.overlay_sheet_handle_close)
    val actions = sheetHandleActions(
        state.isExpanded,
        onSettingsClick != null,
        state.isLeftHanded,
        state.canExpand
    ).map { action ->
        CustomAccessibilityAction(gestureZoneActionLabel(action)) {
            if (action == SheetHandleAction.Settings) onSettingsClick?.invoke() else onToggleExpanded()
            true
        }
    }
    val baseConfiguration = LocalViewConfiguration.current
    val gestureZoneConfiguration = remember(baseConfiguration) {
        LongPressViewConfiguration(baseConfiguration, GESTURE_ZONE_LONG_PRESS_TIMEOUT_MS)
    }

    CompositionLocalProvider(LocalViewConfiguration provides gestureZoneConfiguration) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(AureoleDS.dimens.smallCompact)
                .touchTargetBelow(AureoleDS.dimens.minTouchTarget)
                .semantics {
                    contentDescription = description
                    customActions = actions
                    if (onDismiss != null) {
                        onClick(label = description) {
                            onDismiss()
                            true
                        }
                        dismiss(label = description) {
                            onDismiss()
                            true
                        }
                    }
                }
                .gestureZoneTaps(onDoubleTap = { currentToggle() }, onLongPress = onHold),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AureoleDS.dimens.smallCompact),
                contentAlignment = Alignment.Center
            ) {
                val colors = AureoleDS.colors
                val barColor = if (state.isDismissArmed) colors.onSurfaceHigh else colors.onSurfaceLow
                Box(
                    modifier = Modifier
                        .size(width = AureoleDS.dimens.xxLarge, height = SheetHandleBarHeight)
                        .background(barColor, RoundedCornerShape(AureoleDS.dimens.radiusFull))
                )
            }
        }
    }
}

/**
 * Keeps the strip as tall as it is in the layout but lets the touch area (and what follows in the chain: semantics,
 * taps) run [target] tall from its top, over the start of the header below. The header is drawn later, so its logo
 * still takes its own taps; the title text has none and the strip answers there. The sheet does not grow.
 */
private fun Modifier.touchTargetBelow(target: Dp): Modifier = layout { measurable, constraints ->
    val height = constraints.maxHeight
    val targetPx = target.roundToPx().coerceAtLeast(height)
    val placeable = measurable.measure(constraints.copy(minHeight = targetPx, maxHeight = targetPx))
    layout(placeable.width, height) { placeable.place(0, 0) }
}

private fun Modifier.gestureZoneTaps(onDoubleTap: () -> Unit, onLongPress: () -> Unit): Modifier {
    return pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { onDoubleTap() },
            onLongPress = { onLongPress() }
        )
    }
}

@Composable
@ReadOnlyComposable
private fun gestureZoneActionLabel(action: SheetHandleAction): String {
    return when (action) {
        SheetHandleAction.Expand -> stringResource(R.string.overlay_sheet_action_expand)
        SheetHandleAction.Collapse -> stringResource(R.string.overlay_sheet_action_collapse)
        SheetHandleAction.Settings -> stringResource(R.string.overlay_sheet_action_settings)
    }
}

@AureolePreview
@Composable
private fun OverlaySheetGestureZonePreview() {
    AureoleLauncherTheme {
        OverlaySheetGestureZone(
            state = GestureZoneState(isExpanded = false, isLeftHanded = false),
            onToggleExpanded = {},
            onSettingsClick = {},
            onDismiss = {}
        )
    }
}
