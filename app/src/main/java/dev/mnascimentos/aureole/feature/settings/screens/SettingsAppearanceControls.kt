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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Glass
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.ColorFill(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        AureoleText(
            text = "Menu background color",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(24.dp)
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Glass(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        AureoleText(
            text = "Glass effect",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        val opacityText = when {
            (!uiState.isHazeEnabled || uiState.hazeOpacity <= OPACITY_NONE_THRESHOLD) -> "None"
            uiState.hazeOpacity <= OPACITY_LOW_THRESHOLD -> "Low"
            uiState.hazeOpacity <= OPACITY_MEDIUM_THRESHOLD -> "Medium"
            else -> "Max"
        }
        AureoleText(
            text = opacityText,
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceLow
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
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Corners(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        AureoleText(
            text = "Change Border",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        AureoleText(
            text = "$cornerRadiusDp dp",
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceLow
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
