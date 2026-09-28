@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsToggleItem
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Glass
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.settings.components.BlurBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.PickPaletteBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SetBackgroundBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val BOTTOM_SPACER_RATIO = 0.15f
private const val OPACITY_NONE_THRESHOLD = 0.05f
private const val OPACITY_LOW_THRESHOLD = 0.25f
private const val OPACITY_MEDIUM_THRESHOLD = 0.65f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearanceScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    @Suppress("UNUSED_PARAMETER") onNavigateBack: () -> Unit
) {
    var flags by remember {
        mutableStateOf(AppearanceDialogFlags(showPalette = false, showSetBg = false, showBlur = false))
    }

    Scaffold(
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val topSpacerHeight = (maxHeight * (uiState.headerOffsetPercent / 100f)).coerceAtLeast(16.dp)
            val bottomSpacerHeight = (maxHeight * BOTTOM_SPACER_RATIO).coerceAtLeast(24.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                Text(
                    text = "Appearance",
                    style = AureoleTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = AureoleTheme.colors.onSurfaceMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppearanceMenuItems(
                    uiState = uiState,
                    actions = actions,
                    onShowSetBackgroundDialog = { flags = flags.copy(showSetBg = true) },
                    onShowBlurDialog = { flags = flags.copy(showBlur = true) },
                    onShowPaletteDialog = { flags = flags.copy(showPalette = true) }
                )

                Spacer(modifier = Modifier.height(bottomSpacerHeight))
            }
        }

        AppearanceDialogs(
            flags = flags,
            uiState = uiState,
            actions = actions,
            onDismissDialog = {
                flags = AppearanceDialogFlags(showPalette = false, showSetBg = false, showBlur = false)
            }
        )
    }
}

@Composable
private fun AppearanceMenuItems(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onShowSetBackgroundDialog: () -> Unit,
    onShowBlurDialog: () -> Unit,
    onShowPaletteDialog: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SettingsMenuItem(
            title = "Set Background",
            leadingContent = { AureoleDS.icons.Edit() },
            onClick = onShowSetBackgroundDialog
        )

        ShortcutRow()

        MenuColorRow(
            manualSeedColor = uiState.manualSeedColor,
            onClick = { actions.onOpenColorPickerDialog() }
        )

        GlassAndBlurRow(
            uiState = uiState,
            onClick = onShowBlurDialog
        )

        SettingsToggleItem(
            title = "Themed icons",
            checked = uiState.isThemedAppIconsEnabled,
            onCheckedChange = { actions.onToggleThemedAppIcons() },
            leadingContent = { AureoleDS.icons.AppSelect() }
        )

        SettingsMenuItem(
            title = "Colors",
            leadingContent = { AureoleDS.icons.StrokeColor() },
            onClick = onShowPaletteDialog
        )
    }
}

@Composable
private fun ShortcutRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Corners(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        Text(
            text = "Press shortcut",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Rounded",
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceLow
        )
    }
}

@Composable
private fun MenuColorRow(
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
        Text(
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
private fun GlassAndBlurRow(
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
        Text(
            text = "Glass & Blur",
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
        Text(
            text = opacityText,
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceLow
        )
    }
}

@Composable
private fun AppearanceDialogs(
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
