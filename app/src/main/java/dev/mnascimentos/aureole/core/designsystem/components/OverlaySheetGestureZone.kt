package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.SheetHandleAction
import dev.mnascimentos.aureole.core.designsystem.model.sheetHandleActions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LongPressViewConfiguration

/** Holding the gesture zone this long opens the Aureole settings. */
private const val GESTURE_ZONE_LONG_PRESS_TIMEOUT_MS = 600L

/** Height of the invisible gesture strip at the top of the sheet. */
private const val GESTURE_ZONE_HEIGHT_DP = 20

/**
 * Invisible gesture strip across the top of the overlay sheet; nothing is drawn.
 *
 * Double tap toggles between the rest and expanded heights; holding it opens the settings. Dragging is
 * handled by the sheet surface around it, so a drag that starts on the strip resizes the sheet too.
 * TalkBack gets the same gestures as custom actions.
 *
 * @param isExpanded Whether the sheet is expanded, to label the resize action.
 * @param isLeftHanded Thumb side; sets the order of the accessibility actions.
 * @param onToggleExpanded Switches between rest and expanded. Owns the segment tick.
 * @param onSettingsClick Opens the Aureole settings; null hides the action and disables holding.
 */
@Composable
internal fun OverlaySheetGestureZone(
    isExpanded: Boolean,
    isLeftHanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSettingsClick: (() -> Unit)?,
    modifier: Modifier = Modifier
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
    val description = stringResource(R.string.overlay_sheet_gesture_zone_description)
    val actions = sheetHandleActions(isExpanded, onSettingsClick != null, isLeftHanded).map { action ->
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
                .height(GESTURE_ZONE_HEIGHT_DP.dp)
                .semantics {
                    contentDescription = description
                    customActions = actions
                }
                .gestureZoneTaps(onDoubleTap = { currentToggle() }, onLongPress = onHold)
        )
    }
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
            isExpanded = false,
            isLeftHanded = false,
            onToggleExpanded = {},
            onSettingsClick = {}
        )
    }
}
