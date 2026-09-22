package dev.mnascimentos.aureole.feature.settings

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.ColorPickerDialog
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.settings.model.PreferenceItemConfig
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val OPACITY_LOW = 0.20f
private const val OPACITY_MEDIUM = 0.50f
private const val OPACITY_HIGH = 0.70f
private const val OPACITY_SOLID = 0.90f

private const val OPACITY_LOW_THRESHOLD = 0.25f
private const val OPACITY_HIGH_THRESHOLD = 0.70f

private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_SEMI_TRANSPARENT = 0.5f
private const val ALPHA_FULL = 1.0f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            actions.onClearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Launcher Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = actions.onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { AppearanceSettingsGroup(uiState = uiState, actions = actions) }
            item { WallpaperSettingsGroup(uiState = uiState, actions = actions) }
            item { WidgetsSettingsGroup(uiState = uiState, actions = actions) }
            item { FoldersSettingsGroup(uiState = uiState, actions = actions) }
            item { LayoutSettingsGroup(uiState = uiState, actions = actions) }
            item { SystemSettingsGroup(uiState = uiState, actions = actions) }
            item { AboutSettingsGroup() }
        }
    }

    SettingsDialogs(uiState = uiState, actions = actions)
}

@Composable
private fun SettingsDialogs(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    if (uiState.showHazeOpacityDialog) {
        HazeOpacityDialog(
            currentOpacity = uiState.hazeOpacity,
            onOpacitySelected = actions.onHazeOpacitySelected,
            onDismiss = actions.onDismissHazeOpacityDialog
        )
    }

    if (uiState.showColorPickerDialog) {
        ColorPickerDialog(
            initialColor = uiState.manualSeedColor,
            onColorSelected = actions.onSelectManualSeedColor,
            onDismiss = actions.onDismissColorPickerDialog
        )
    }

    if (uiState.showRestoreWallpaperDialog) {
        RestoreWallpaperDialog(
            onConfirm = actions.onConfirmRestoreWallpaper,
            onDismiss = actions.onDismissRestoreWallpaperDialog
        )
    }

    if (uiState.showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = actions.onDismissCreateFolderDialog,
            onSubmit = actions.onSubmitCreateFolder
        )
    }

    if (uiState.showResetGridDialog) {
        ResetGridDialog(actions = actions)
    }

    if (uiState.showFactoryResetDialog) {
        FactoryResetDialog(actions = actions)
    }
}

@Composable
private fun FactoryResetDialog(
    actions: SettingsScreenActions
) {
    AlertDialog(
        onDismissRequest = actions.onDismissFactoryResetDialog,
        title = { Text("Apagar todos os dados?") },
        text = {
            Text(
                "Isso apagará todas as suas configurações, pastas, " +
                    "atalhos e disposição da tela inicial. Deseja continuar?"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    actions.onConfirmFactoryReset()
                }
            ) {
                Text("Apagar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = actions.onDismissFactoryResetDialog) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ResetGridDialog(
    actions: SettingsScreenActions
) {
    AlertDialog(
        onDismissRequest = actions.onDismissResetGridDialog,
        title = { Text("Restaurar Layout Padrão?") },
        text = {
            Text(
                "Esta ação redefinirá o posicionamento e o " +
                    "tamanho de todos os elementos da tela inicial para a configuração padrão."
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    actions.onConfirmResetGrid()
                    actions.onDismissResetGridDialog()
                }
            ) {
                Text("Restaurar")
            }
        },
        dismissButton = {
            TextButton(onClick = actions.onDismissResetGridDialog) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun AppearanceSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "General")
        PreferenceCard {
            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Set as Default Launcher",
                    subtitle = if (uiState.isDefaultLauncher) {
                        "Aureole Launcher is currently set as your default home app"
                    } else {
                        "Tap to select Aureole Launcher as your default home app"
                    },
                    leadingIcon = Icons.Default.Home
                ),
                trailingContent = {
                    if (uiState.isDefaultLauncher) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                onClick = actions.onSetDefaultLauncherClick
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Select Favorite Apps",
                    subtitle = "${uiState.favoriteAppPackages.size} apps selected as favorites",
                    leadingIcon = Icons.Default.Star
                ),
                onClick = actions.onOpenFavoritePickerClick
            )
        }
    }
}

@Composable
private fun WallpaperSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Personalização & Papel de Parede")
        PreferenceCard {
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

            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Fundo do Relógio",
                    subtitle = "Exibe o container com efeito de desfoque (blur) para o relógio quando o desfoque estiver ativo",
                    leadingIcon = Icons.Default.Edit
                ),
                checked = uiState.isClockBackgroundEnabled,
                onCheckedChange = { actions.onToggleClockBackground() }
            )
        }
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
private fun WidgetsSettingsGroup(
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
                    title = "Show All Apps on Home",
                    subtitle = "Display full list of installed apps directly on the home screen below favorites"
                ),
                checked = uiState.showAllAppsOnHome,
                onCheckedChange = { actions.onToggleShowAllAppsOnHome() }
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
private fun FoldersSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Side Panel & Folders")
        PreferenceCard {
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
}

@Composable
private fun LayoutSettingsGroup(
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

@Composable
private fun SystemSettingsGroup(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Updates & System")
        PreferenceCard {
            PreferenceSwitchRow(
                config = PreferenceItemConfig(
                    title = "Buscar atualizações no Google Play",
                    subtitle = "Exibe um aviso no início caso exista uma nova versão do app disponível"
                ),
                checked = uiState.isInAppUpdateEnabled,
                onCheckedChange = { actions.onToggleInAppUpdate() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
            )

            PreferenceRowItem(
                config = PreferenceItemConfig(
                    title = "Apagar todos os dados",
                    subtitle = "Restaurar o aplicativo ao seu estado de instalação inicial",
                    leadingIcon = Icons.Default.Delete
                ),
                onClick = actions.onOpenFactoryResetDialog
            )
        }
    }
}

@Composable
private fun AboutSettingsGroup() {
    val context = LocalContext.current
    val versionName = remember(context) {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }

    Column {
        PreferenceCategoryHeader(title = "About")
        PreferenceCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aureole Launcher",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Version $versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HazeOpacityDialog(
    currentOpacity: Float,
    onOpacitySelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        OPACITY_LOW to "Low (20%)",
        OPACITY_MEDIUM to "Medium (50%)",
        OPACITY_HIGH to "High (70%)",
        OPACITY_SOLID to "Solid (90%)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Blur Opacity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onOpacitySelected(value) }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentOpacity == value),
                            onClick = { onOpacitySelected(value) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun RestoreWallpaperDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Restaurar Papel de Parede",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Deseja restaurar o papel de parede padrão?",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Restaurar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun PreferenceCategoryHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun PreferenceCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun PreferenceRowItem(
    config: PreferenceItemConfig,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val titleColor = if (config.enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
    }

    val subtitleColor = if (config.enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = ALPHA_DISABLED)
    }

    val iconTint = if (config.enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = config.enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (config.leadingIcon != null) {
            Icon(
                imageVector = config.leadingIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (config.subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = config.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}

@Composable
private fun PreferenceSwitchRow(
    config: PreferenceItemConfig,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowItem(
        config = config,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = config.enabled
            )
        },
        onClick = { if (config.enabled) onCheckedChange(!checked) }
    )
}

@AureolePreview
@Composable
fun SettingsScreenPreview() {
    AureoleLauncherTheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions()
        )
    }
}
