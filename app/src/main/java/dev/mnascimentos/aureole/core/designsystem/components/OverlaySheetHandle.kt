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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.SheetHandleAction
import dev.mnascimentos.aureole.core.designsystem.model.sheetHandleActions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LongPressViewConfiguration

/** Holding the handle this long opens the Aureole settings. */
private const val HANDLE_LONG_PRESS_TIMEOUT_MS = 600L

private const val HANDLE_PILL_ALPHA = 0.5f

/**
 * Grab handle at the top of the overlay sheet.
 *
 * Double tap toggles between the rest and expanded heights; holding it opens the settings. Dragging is
 * handled by the sheet surface around it. TalkBack gets the same gestures as custom actions.
 *
 * @param isExpanded Whether the sheet is expanded, to label the resize action.
 * @param isLeftHanded Thumb side; sets the order of the accessibility actions.
 * @param onToggleExpanded Switches between rest and expanded. Owns the segment tick.
 * @param onSettingsClick Opens the Aureole settings; null hides the action and disables holding.
 */
@Composable
internal fun OverlaySheetHandle(
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
    val description = stringResource(R.string.overlay_sheet_handle_description)
    val actions = sheetHandleActions(isExpanded, onSettingsClick != null, isLeftHanded).map { action ->
        CustomAccessibilityAction(handleActionLabel(action)) {
            if (action == SheetHandleAction.Settings) onSettingsClick?.invoke() else onToggleExpanded()
            true
        }
    }
    val baseConfiguration = LocalViewConfiguration.current
    val handleConfiguration = remember(baseConfiguration) {
        LongPressViewConfiguration(baseConfiguration, HANDLE_LONG_PRESS_TIMEOUT_MS)
    }

    CompositionLocalProvider(LocalViewConfiguration provides handleConfiguration) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(AureoleDS.dimens.large)
                .semantics {
                    contentDescription = description
                    customActions = actions
                }
                .handleGestures(onDoubleTap = { currentToggle() }, onLongPress = onHold),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = AureoleDS.dimens.xLarge, height = AureoleDS.dimens.xxSmall)
                    .background(
                        color = AureoleDS.colors.onSurfaceMedium.copy(alpha = HANDLE_PILL_ALPHA),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusFull)
                    )
            )
        }
    }
}

private fun Modifier.handleGestures(onDoubleTap: () -> Unit, onLongPress: () -> Unit): Modifier {
    return pointerInput(Unit) {
        detectTapGestures(
            onDoubleTap = { onDoubleTap() },
            onLongPress = { onLongPress() }
        )
    }
}

@Composable
@ReadOnlyComposable
private fun handleActionLabel(action: SheetHandleAction): String {
    return when (action) {
        SheetHandleAction.Expand -> stringResource(R.string.overlay_sheet_action_expand)
        SheetHandleAction.Collapse -> stringResource(R.string.overlay_sheet_action_collapse)
        SheetHandleAction.Settings -> stringResource(R.string.overlay_sheet_action_settings)
    }
}

@AureolePreview
@Composable
private fun OverlaySheetHandlePreview() {
    AureoleLauncherTheme {
        OverlaySheetHandle(
            isExpanded = false,
            isLeftHanded = false,
            onToggleExpanded = {},
            onSettingsClick = {}
        )
    }
}
