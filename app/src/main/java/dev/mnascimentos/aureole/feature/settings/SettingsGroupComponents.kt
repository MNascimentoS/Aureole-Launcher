package dev.mnascimentos.aureole.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.settings.model.PreferenceItemConfig
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val OPACITY_LOW_THRESHOLD = 0.25f
private const val OPACITY_MEDIUM = 0.50f
private const val OPACITY_HIGH_THRESHOLD = 0.70f

private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_SEMI_TRANSPARENT = 0.5f
private const val ALPHA_FULL = 1.0f

@Composable
fun WallpaperSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Personalização & Papel de Parede")
        PreferenceCard {
            WallpaperSettingsContent(uiState = uiState, actions = actions)
        }
    }
}

@Composable
private fun WallpaperSettingsContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Usar cores do papel de parede",
                subtitle = "Extrair paleta de cores dinâmicas do papel de parede do sistema",
                leadingIcon = Icons.Default.Edit
            ),
            checked = uiState.isDynamicWallpaperEnabled,
            onCheckedChange = { actions.onToggleDynamicWallpaper() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        ThemeColorPreferenceRow(
            isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
            manualSeedColor = uiState.manualSeedColor,
            onOpenColorPickerDialog = actions.onOpenColorPickerDialog
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        WallpaperImagePreferenceRows(
            isCustomWallpaperSet = uiState.isCustomWallpaperSet,
            onChangeWallpaperClick = actions.onChangeWallpaperClick,
            onRestoreDefaultWallpaperClick = actions.onRestoreDefaultWallpaperClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Ícones Temáticos",
                subtitle = "Usar cor primária do tema nos ícones de aplicativos",
                leadingIcon = Icons.Default.Star
            ),
            checked = uiState.isThemedAppIconsEnabled,
            onCheckedChange = { actions.onToggleThemedAppIcons() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        val clockSubtitle = "Exibe o container com efeito de desfoque (blur) para o relógio quando o desfoque estiver ativo"
        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Fundo do Relógio",
                subtitle = clockSubtitle,
                leadingIcon = Icons.Default.Edit
            ),
            checked = uiState.isClockBackgroundEnabled,
            onCheckedChange = { actions.onToggleClockBackground() }
        )
    }
}

@Composable
private fun ThemeColorPreferenceRow(
    isDynamicWallpaperEnabled: Boolean,
    manualSeedColor: Int,
    onOpenColorPickerDialog: () -> Unit
) {
    PreferenceRowItem(
        config = PreferenceItemConfig(
            title = "Cor Primária do Tema",
            subtitle = if (isDynamicWallpaperEnabled) {
                "Desabilitado quando as cores do papel de parede estão ativas"
            } else {
                "Toque para escolher uma cor primária personalizada"
            },
            enabled = !isDynamicWallpaperEnabled
        ),
        trailingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (!isDynamicWallpaperEnabled) {
                            Color(manualSeedColor)
                        } else {
                            Color(manualSeedColor).copy(alpha = ALPHA_DISABLED)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(
                            alpha = if (!isDynamicWallpaperEnabled) ALPHA_FULL else ALPHA_DISABLED
                        ),
                        shape = CircleShape
                    )
            )
        },
        onClick = onOpenColorPickerDialog
    )
}

@Composable
private fun WallpaperImagePreferenceRows(
    isCustomWallpaperSet: Boolean,
    onChangeWallpaperClick: () -> Unit,
    onRestoreDefaultWallpaperClick: () -> Unit
) {
    Column {
        PreferenceRowItem(
            config = PreferenceItemConfig(
                title = "Alterar Papel de Parede",
                subtitle = "Escolher uma imagem do seu dispositivo",
                leadingIcon = Icons.Default.Edit
            ),
            onClick = onChangeWallpaperClick
        )

        if (isCustomWallpaperSet) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Restaurar Padrão",
                    subtitle = "Remover imagem customizada e usar o papel de parede padrão",
                    leadingIcon = Icons.Default.Delete
                ),
                onClick = onRestoreDefaultWallpaperClick
            )
        }
    }
}

@Composable
fun WidgetsSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Home Screen & Widgets")
        PreferenceCard {
            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Show Widget Indicator Dots",
                    subtitle = "Display page indicator dots below widget row"
                ),
                checked = uiState.showWidgetDots,
                onCheckedChange = { actions.onToggleShowWidgetDots() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Home Button opens All Apps",
                    subtitle = "Pressing the Home key while on the home screen toggles the app drawer"
                ),
                checked = uiState.homeButtonOpensAllApps,
                onCheckedChange = { actions.onToggleHomeButtonOpensAllApps() }
            )
        }
    }
}

@Composable
fun FoldersSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Side Panel & Folders")
        PreferenceCard {
            FoldersSettingsContent(uiState = uiState, actions = actions)
        }
    }
}

@Composable
private fun FoldersSettingsContent(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Fundo do Painel Lateral",
                subtitle = "Exibe o container de fundo translúcido no painel lateral"
            ),
            checked = uiState.isSidePanelBackgroundEnabled,
            onCheckedChange = { actions.onToggleSidePanelBackground() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Expandir Painel Lateral na Célula",
                subtitle = "Faz o painel lateral ocupar todo o espaço da célula com ícones espaçados uniformemente"
            ),
            checked = uiState.isSidePanelExpandCell,
            onCheckedChange = { actions.onToggleSidePanelExpandCell() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceRowItem(
            config = PreferenceItemConfig(
                title = "Alinhamento do Painel Lateral",
                subtitle = uiState.sidePanelPosition,
                leadingIcon = Icons.Default.Menu
            ),
            onClick = actions.onOpenSidePanelPositionDialog
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Mostrar botão de criar pasta",
                subtitle = "Exibe o botão (+) no painel lateral para adicionar pastas rapidamente"
            ),
            checked = uiState.showSidePanelAddFolderButton,
            onCheckedChange = { actions.onToggleShowSidePanelAddFolderButton() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceSwitchRow(
            config = PreferenceItemConfig(
                title = "Mostrar nome da pasta no painel lateral",
                subtitle = "Exibe o nome da pasta abaixo de cada botão no painel lateral"
            ),
            checked = uiState.showFolderLabels,
            onCheckedChange = { actions.onToggleShowFolderLabels() }
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 56.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceRowItem(
            config = PreferenceItemConfig(
                title = stringResource(id = R.string.add_folder),
                subtitle = stringResource(id = R.string.add_folder_subtitle),
                leadingIcon = Icons.Default.Add
            ),
            onClick = actions.onAddFolderClick
        )
    }
}

@Composable
fun LayoutSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Layout & Accessibility")
        PreferenceCard {
            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Left-Handed Mode",
                    subtitle = "Move side panel and gesture area to the left edge of the screen"
                ),
                checked = uiState.isLeftHandedMode,
                onCheckedChange = { actions.onToggleLeftHandedMode() }
            )

            HazeSettingsSection(uiState = uiState, actions = actions)
            ExtraLayoutSettingsSection(uiState = uiState, actions = actions)
        }
    }
}

@Composable
private fun ExtraLayoutSettingsSection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
    )

    PreferenceSwitchRow(
        config = PreferenceItemConfig(
            title = "Desativar Alphabet Scrubber",
            subtitle = "Ocultar a barra alfabética lateral na lista de aplicativos"
        ),
        checked = uiState.isAlphabetScrubberDisabled,
        onCheckedChange = { actions.onToggleDisableAlphabetScrubber() }
    )

    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
    )

    PreferenceRowItem(
        config = PreferenceItemConfig(
            title = "Restaurar Layout Padrão",
            subtitle = "Redefinir tamanho e posição dos containers da tela inicial"
        ),
        onClick = actions.onOpenResetGridDialog
    )
}

@Composable
private fun HazeSettingsSection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
    )

    PreferenceSwitchRow(
        config = PreferenceItemConfig(
            title = "Haze Blur Effects",
            subtitle = if (!uiState.isHazeSupported) {
                "Not supported on this device (requires Android 12+)"
            } else {
                "Frosted glass blur effect on side panel, folders, and drawers"
            },
            enabled = uiState.isHazeSupported
        ),
        checked = uiState.isHazeEnabled,
        onCheckedChange = { actions.onToggleHaze() }
    )

    if (uiState.isHazeEnabled) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )

        PreferenceRowItem(
            config = PreferenceItemConfig(
                title = "Blur Opacity",
                subtitle = when {
                    uiState.hazeOpacity <= OPACITY_LOW_THRESHOLD -> "Low (20%)"
                    uiState.hazeOpacity <= OPACITY_MEDIUM -> "Medium (50%)"
                    uiState.hazeOpacity <= OPACITY_HIGH_THRESHOLD -> "High (70%)"
                    else -> "Solid (90%)"
                }
            ),
            onClick = actions.onOpenHazeOpacityDialog
        )
    }
}
