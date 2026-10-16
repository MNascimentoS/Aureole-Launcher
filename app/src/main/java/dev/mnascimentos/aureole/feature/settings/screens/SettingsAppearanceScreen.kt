@file:Suppress("MagicNumber", "MaxLineLength")

package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsToggleItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.AppSelect
import dev.mnascimentos.aureole.core.designsystem.icons.Corners
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.StrokeColor
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDialogFlags
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val BOTTOM_SPACER_RATIO = 0.15f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearanceScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    @Suppress("UNUSED_PARAMETER") // unused but kept for interface matching
    onNavigateBack: () -> Unit
) {
    var flags by remember {
        mutableStateOf(
            AppearanceDialogFlags(
                showPalette = false,
                showFont = false,
                showSetBg = false,
                showBlur = false,
                showBorderRadius = false
            )
        )
    }

    Scaffold(
        containerColor = AureoleDS.colors.surface
    ) { innerPadding ->
        SettingsAppearanceContent(
            uiState = uiState,
            actions = actions,
            flags = flags,
            onFlagsChanged = { flags = it },
            innerPadding = innerPadding,
        )

        AppearanceDialogs(
            flags = flags,
            uiState = uiState,
            actions = actions,
            onDismissDialog = {
                flags = AppearanceDialogFlags(showPalette = false, showFont = false, showSetBg = false, showBlur = false, showBorderRadius = false)
            }
        )
    }
}

@Composable
private fun SettingsAppearanceContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    flags: AppearanceDialogFlags,
    onFlagsChanged: (AppearanceDialogFlags) -> Unit,
    innerPadding: PaddingValues,
) {
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

            AureoleText(
                text = "Appearance",
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = AureoleDS.colors.onSurfaceMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppearanceMenuItems(
                uiState = uiState,
                actions = actions,
                onShowSetBackgroundDialog = { onFlagsChanged(flags.copy(showSetBg = true)) },
                onShowBlurDialog = { onFlagsChanged(flags.copy(showBlur = true)) },
                onShowPaletteDialog = { onFlagsChanged(flags.copy(showPalette = true)) },
                onShowFontDialog = { onFlagsChanged(flags.copy(showFont = true)) },
                onShowBorderRadiusDialog = { onFlagsChanged(flags.copy(showBorderRadius = true)) }
            )

            Spacer(modifier = Modifier.height(bottomSpacerHeight))
        }
    }
}

@Suppress("LongParameterList") // Menu items composable requires multiple configuration slots
@Composable
private fun AppearanceMenuItems(
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    onShowSetBackgroundDialog: () -> Unit,
    onShowBlurDialog: () -> Unit,
    onShowPaletteDialog: () -> Unit,
    onShowFontDialog: () -> Unit,
    onShowBorderRadiusDialog: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. Set Background
        SettingsMenuItem(
            title = "Set Background",
            leadingContent = { AureoleDS.icons.Edit() },
            onClick = onShowSetBackgroundDialog
        )

        if (uiState.isCustomWallpaperSet) {
            WallpaperScaleTypeRow(
                scaleType = uiState.wallpaperScaleType,
                onSelectScaleType = actions.onSelectWallpaperScaleType
            )
        }

        // 2. Menu background color (placed BEFORE press shortcut!)
        MenuColorRow(
            manualSeedColor = uiState.manualSeedColor,
            onClick = { actions.onOpenColorPickerDialog() }
        )

        // 3. Press shortcut
        ShortcutRow()

        // 4. Glass effect / Glass & Blur
        GlassAndBlurRow(
            uiState = uiState,
            onClick = onShowBlurDialog
        )

        // 5. Change font
        SettingsMenuItem(
            title = "Change font",
            leadingContent = { AureoleDS.icons.Text() },
            onClick = onShowFontDialog
        )

        // 6. Color palette
        SettingsMenuItem(
            title = "Color palette",
            leadingContent = { AureoleDS.icons.Dots() },
            onClick = onShowPaletteDialog
        )

        // 7. Custom colors
        SettingsMenuItem(
            title = "Custom colors",
            leadingContent = { AureoleDS.icons.StrokeColor() },
            onClick = { actions.onOpenColorPickerDialog() }
        )

        // 8. Change Border (radius)
        ChangeBorderRow(
            cornerRadiusDp = uiState.cornerRadiusDp,
            onClick = onShowBorderRadiusDialog
        )

        // 9. Themed icons toggle
        SettingsToggleItem(
            title = "Themed icons",
            checked = uiState.isThemedAppIconsEnabled,
            onCheckedChange = { actions.onToggleThemedAppIcons() },
            leadingContent = { AureoleDS.icons.AppSelect() }
        )
    }
}

@Composable
private fun WallpaperScaleTypeRow(
    scaleType: String,
    onSelectScaleType: (String) -> Unit
) {
    val nextScaleType = when (scaleType) {
        "Crop" -> "Fit"
        "Fit" -> "Fill"
        else -> "Crop"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelectScaleType(nextScaleType) }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Layout(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        AureoleText(
            text = "Image scale",
            style = AureoleDS.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        AureoleText(
            text = scaleType,
            style = AureoleDS.typography.bodyLarge,
            color = AureoleDS.colors.onSurfaceLow
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
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 20.dp).size(20.dp)
        )
        AureoleText(
            text = "Press shortcut",
            style = AureoleDS.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
        AureoleText(
            text = "Rounded",
            style = AureoleDS.typography.bodyLarge,
            color = AureoleDS.colors.onSurfaceLow
        )
    }
}
