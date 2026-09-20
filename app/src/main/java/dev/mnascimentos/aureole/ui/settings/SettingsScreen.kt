@file:Suppress("LongMethod")

package dev.mnascimentos.aureole.ui.settings

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.ui.components.ColorPickerDialog
import dev.mnascimentos.aureole.ui.components.CreateFolderDialog

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
            item { GeneralCategorySection(uiState = uiState, actions = actions) }
            item { WallpaperCategorySection(uiState = uiState, actions = actions) }
            item { HomeScreenCategorySection(uiState = uiState, actions = actions) }
            item { SidePanelCategorySection(uiState = uiState, actions = actions) }
            item { LayoutCategorySection(uiState = uiState, actions = actions) }
            item { AboutCategorySection() }
        }
    }

    if (uiState.showSidePanelPositionDialog) {
        SidePanelPositionDialog(
            currentPosition = uiState.sidePanelPosition,
            onPositionSelected = actions.onSidePanelPositionSelected,
            onDismiss = actions.onDismissSidePanelPositionDialog
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
        AlertDialog(
            onDismissRequest = actions.onDismissRestoreWallpaperDialog,
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
                TextButton(onClick = actions.onConfirmRestoreWallpaper) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = actions.onDismissRestoreWallpaperDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (uiState.showCreateFolderDialog) {
        CreateFolderDialog(
            onDismiss = actions.onDismissCreateFolderDialog,
            onSubmit = actions.onSubmitCreateFolder
        )
    }
}

data class SettingsScreenActions(
    val onBackClick: () -> Unit = {},
    val onSetDefaultLauncherClick: () -> Unit = {},
    val onOpenFavoritePickerClick: () -> Unit = {},
    val onToggleShowAllAppsOnHome: () -> Unit = {},
    val onToggleHomeButtonOpensAllApps: () -> Unit = {},
    val onToggleWidgetRow: () -> Unit = {},
    val onToggleSidePanel: () -> Unit = {},
    val onToggleShowFolderLabels: () -> Unit = {},
    val onOpenSidePanelPositionDialog: () -> Unit = {},
    val onToggleLeftHandedMode: () -> Unit = {},
    val onSidePanelPositionSelected: (String) -> Unit = {},
    val onDismissSidePanelPositionDialog: () -> Unit = {},
    val onToggleDynamicWallpaper: () -> Unit = {},
    val onOpenColorPickerDialog: () -> Unit = {},
    val onSelectManualSeedColor: (Int) -> Unit = {},
    val onDismissColorPickerDialog: () -> Unit = {},
    val onChangeWallpaperClick: () -> Unit = {},
    val onRestoreDefaultWallpaperClick: () -> Unit = {},
    val onConfirmRestoreWallpaper: () -> Unit = {},
    val onDismissRestoreWallpaperDialog: () -> Unit = {},
    val onClearErrorMessage: () -> Unit = {},
    val onAddFolderClick: () -> Unit = {},
    val onDismissCreateFolderDialog: () -> Unit = {},
    val onSubmitCreateFolder: (String) -> Unit = {},
)

// Convenience overload for backwards compatibility
@Suppress("LongParameterList")
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBackClick: () -> Unit,
    onSetDefaultLauncherClick: () -> Unit,
    onOpenFavoritePickerClick: () -> Unit,
    onToggleShowAllAppsOnHome: () -> Unit,
    onToggleHomeButtonOpensAllApps: () -> Unit,
    onToggleSidePanel: () -> Unit,
    onOpenSidePanelPositionDialog: () -> Unit,
    onToggleLeftHandedMode: () -> Unit,
    onSidePanelPositionSelected: (String) -> Unit,
    onDismissSidePanelPositionDialog: () -> Unit
) {
    SettingsScreen(
        uiState = uiState,
        actions = SettingsScreenActions(
            onBackClick = onBackClick,
            onSetDefaultLauncherClick = onSetDefaultLauncherClick,
            onOpenFavoritePickerClick = onOpenFavoritePickerClick,
            onToggleShowAllAppsOnHome = onToggleShowAllAppsOnHome,
            onToggleHomeButtonOpensAllApps = onToggleHomeButtonOpensAllApps,
            onToggleSidePanel = onToggleSidePanel,
            onOpenSidePanelPositionDialog = onOpenSidePanelPositionDialog,
            onToggleLeftHandedMode = onToggleLeftHandedMode,
            onSidePanelPositionSelected = onSidePanelPositionSelected,
            onDismissSidePanelPositionDialog = onDismissSidePanelPositionDialog
        )
    )
}

@Composable
private fun GeneralCategorySection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "General")
        PreferenceCard {
            PreferenceRowItem(
                title = "Set as Default Launcher",
                subtitle = if (uiState.isDefaultLauncher) {
                    "Aureole Launcher is currently set as your default home app"
                } else {
                    "Tap to select Aureole Launcher as your default home app"
                },
                leadingIcon = Icons.Default.Home,
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
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceRowItem(
                title = "Select Favorite Apps",
                subtitle = "${uiState.favoriteAppPackages.size} apps selected as favorites",
                leadingIcon = Icons.Default.Star,
                onClick = actions.onOpenFavoritePickerClick
            )
        }
    }
}

@Composable
private fun WallpaperCategorySection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Personalização & Papel de Parede")
        PreferenceCard {
            PreferenceSwitchRow(
                title = "Usar cores do papel de parede",
                subtitle = "Extrair paleta de cores dinâmicas do papel de parede do sistema",
                leadingIcon = Icons.Default.Edit,
                checked = uiState.isDynamicWallpaperEnabled,
                onCheckedChange = { actions.onToggleDynamicWallpaper() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceRowItem(
                title = "Cor Primária do Tema",
                subtitle = if (uiState.isDynamicWallpaperEnabled) {
                    "Desabilitado quando as cores do papel de parede estão ativas"
                } else {
                    "Toque para escolher uma cor primária personalizada"
                },
                enabled = !uiState.isDynamicWallpaperEnabled,
                trailingContent = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (!uiState.isDynamicWallpaperEnabled) {
                                    Color(uiState.manualSeedColor)
                                } else {
                                    Color(uiState.manualSeedColor).copy(alpha = 0.38f)
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(
                                    alpha = if (!uiState.isDynamicWallpaperEnabled) 1f else 0.38f
                                ),
                                shape = CircleShape
                            )
                    )
                },
                onClick = actions.onOpenColorPickerDialog
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceRowItem(
                title = "Alterar Papel de Parede",
                subtitle = "Escolher uma imagem do seu dispositivo",
                leadingIcon = Icons.Default.Edit,
                onClick = actions.onChangeWallpaperClick
            )

            if (uiState.isCustomWallpaperSet) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                PreferenceRowItem(
                    title = "Restaurar Padrão",
                    subtitle = "Remover imagem customizada e usar o papel de parede padrão",
                    leadingIcon = Icons.Default.Delete,
                    onClick = actions.onRestoreDefaultWallpaperClick
                )
            }
        }
    }
}

@Composable
private fun HomeScreenCategorySection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Home Screen & Drawer")
        PreferenceCard {
            PreferenceSwitchRow(
                title = "Show Widget Row Container",
                subtitle = "Display widget container on the home screen",
                checked = uiState.isWidgetRowEnabled,
                onCheckedChange = { actions.onToggleWidgetRow() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceSwitchRow(
                title = "Show All Apps on Home",
                subtitle = "Display full list of installed apps directly on the home screen below favorites",
                checked = uiState.showAllAppsOnHome,
                onCheckedChange = { actions.onToggleShowAllAppsOnHome() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceSwitchRow(
                title = "Home Button opens All Apps",
                subtitle = "Pressing the Home key while on the home screen toggles the app drawer",
                checked = uiState.homeButtonOpensAllApps,
                onCheckedChange = { actions.onToggleHomeButtonOpensAllApps() }
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 56.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            PreferenceRowItem(
                title = stringResource(id = R.string.add_folder),
                subtitle = stringResource(id = R.string.add_folder_subtitle),
                leadingIcon = Icons.Default.Add,
                onClick = actions.onAddFolderClick
            )
        }
    }
}

@Composable
private fun SidePanelCategorySection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Side Panel & Folders")
        PreferenceCard {
            PreferenceSwitchRow(
                title = "Enable Side Panel",
                subtitle = "Quick-access side panel for app folders on the edge of the screen",
                checked = uiState.isSidePanelEnabled,
                onCheckedChange = { actions.onToggleSidePanel() }
            )

            if (uiState.isSidePanelEnabled) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                PreferenceSwitchRow(
                    title = "Mostrar nome da pasta no painel lateral",
                    subtitle = "Exibe o nome da pasta abaixo de cada botão no painel lateral",
                    checked = uiState.showFolderLabels,
                    onCheckedChange = { actions.onToggleShowFolderLabels() }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                PreferenceRowItem(
                    title = "Side Panel Position",
                    subtitle = when (uiState.sidePanelPosition) {
                        "Top" -> "Top"
                        "Bottom" -> "Bottom"
                        else -> "Center"
                    },
                    onClick = actions.onOpenSidePanelPositionDialog
                )
            }
        }
    }
}

@Composable
private fun LayoutCategorySection(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    Column {
        PreferenceCategoryHeader(title = "Layout & Accessibility")
        PreferenceCard {
            PreferenceSwitchRow(
                title = "Left-Handed Mode",
                subtitle = "Move side panel and gesture area to the left edge of the screen",
                checked = uiState.isLeftHandedMode,
                onCheckedChange = { actions.onToggleLeftHandedMode() }
            )
        }
    }
}

@Composable
private fun AboutCategorySection() {
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
                    text = "Version 1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun PreferenceRowItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val titleColor = if (enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    val subtitleColor = if (enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
    }

    val iconTint = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
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

@Suppress("LongParameterList")
@Composable
private fun PreferenceSwitchRow(
    title: String,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowItem(
        title = title,
        subtitle = subtitle,
        leadingIcon = leadingIcon,
        enabled = enabled,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        },
        onClick = { if (enabled) onCheckedChange(!checked) }
    )
}

@Composable
private fun SidePanelPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Top" to "Top",
        "Center" to "Center",
        "Bottom" to "Bottom"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Side Panel Position",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                options.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPositionSelected(key) }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == key),
                            onClick = { onPositionSelected(key) }
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
