package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Glass
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.BlurBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickBorderRadiusBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickFontBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickPaletteBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val OPACITY_NONE_THRESHOLD = 0.05f
private const val OPACITY_LOW_THRESHOLD = 0.35f
private const val OPACITY_MEDIUM_THRESHOLD = 0.70f

@Composable
internal fun MenuColorRow(
    manualSeedColor: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.ColorFill(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = AureoleDS.dimens.iconMedium).size(AureoleDS.dimens.iconMedium)
        )
        AureoleText(
            text = "Menu background color",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
        )
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconLarge)
                .clip(CircleShape)
                .background(Color(manualSeedColor))
        )
    }
}

@Composable
internal fun GlassAndBlurRow(
    uiState: SettingsUiState,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Glass(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = AureoleDS.dimens.iconMedium).size(AureoleDS.dimens.iconMedium)
        )
        AureoleText(
            text = "Glass effect",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
        )
        val opacityText = when {
            (!uiState.isHazeEnabled || uiState.hazeOpacity <= OPACITY_NONE_THRESHOLD) -> "None"
            uiState.hazeOpacity <= OPACITY_LOW_THRESHOLD -> "Low"
            uiState.hazeOpacity <= OPACITY_MEDIUM_THRESHOLD -> "Medium"
            else -> "Max"
        }
        AureoleText(
            text = opacityText,
            color = AureoleDS.colors.onSurfaceLow,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
    }
}

@Composable
internal fun ChangeBorderRow(
    cornerRadiusDp: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Corners(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = AureoleDS.dimens.iconMedium).size(AureoleDS.dimens.iconMedium)
        )
        AureoleText(
            text = "Change Border",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
        )
        AureoleText(
            text = "$cornerRadiusDp dp",
            color = AureoleDS.colors.onSurfaceLow,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
    }
}

@Composable
internal fun AppearanceDialogs(
    flags: AppearanceDialogFlags,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onDismissDialog: () -> Unit
) {
    if (flags.showPalette) {
        PickPaletteBottomSheet(
            selectedPaletteName = uiState.selectedThemeName,
            onPaletteSelected = { palette ->
                onDismissDialog()
                actions.onSelectTheme(palette.name)
            },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showFont) {
        PickFontBottomSheet(
            selectedFontName = uiState.selectedFontName,
            onFontSelected = { fontName ->
                onDismissDialog()
                actions.onSelectFont(fontName)
            },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showSetBg) {
        SetBackgroundBottomSheet(
            onSolidColorClick = {
                onDismissDialog()
                actions.onOpenColorPickerDialog()
            },
            onCustomImageClick = {
                onDismissDialog()
                actions.onChangeWallpaperClick()
            },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showBlur) {
        BlurBottomSheet(
            currentOpacity = uiState.hazeOpacity,
            isHazeEnabled = uiState.isHazeEnabled,
            onOptionSelected = { opacity, enableHaze ->
                onDismissDialog()
                handleBlurOptionSelected(opacity, enableHaze, uiState, actions)
            },
            onDismissRequest = onDismissDialog
        )
    }

    if (flags.showBorderRadius) {
        PickBorderRadiusBottomSheet(
            selectedRadiusDp = uiState.cornerRadiusDp,
            onRadiusSelected = { radius ->
                onDismissDialog()
                actions.onSelectCornerRadius(radius)
            },
            onDismissRequest = onDismissDialog
        )
    }
}

private fun handleBlurOptionSelected(
    opacity: Float,
    enableHaze: Boolean,
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    if (enableHaze) {
        if (!uiState.isHazeEnabled) {
            actions.onToggleHaze()
        }
        actions.onHazeOpacitySelected(opacity)
    } else {
        if (uiState.isHazeEnabled) {
            actions.onToggleHaze()
        }
        actions.onHazeOpacitySelected(0.0f)
    }
}

@AureolePreview
@Composable
private fun SettingsAppearanceControlsPreview() {
    AureoleLauncherTheme {
        MenuColorRow(
            manualSeedColor = 0xFF1B65C0.toInt(),
            onClick = {}
        )
    }
}
