package dev.mnascimentos.aureole.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.ColorPickerDialog
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val OPACITY_LOW = 0.20f
private const val OPACITY_MEDIUM = 0.50f
private const val OPACITY_HIGH = 0.70f
private const val OPACITY_SOLID = 0.90f

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

    if (uiState.showSidePanelPositionDialog) {
        SidePanelPositionDialog(
            currentPosition = uiState.sidePanelPosition,
            onPositionSelected = { pos ->
                actions.onSidePanelPositionSelected(pos)
                actions.onDismissSidePanelPositionDialog()
            },
            onDismiss = actions.onDismissSidePanelPositionDialog
        )
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
                onClick = { actions.onConfirmFactoryReset() }
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
private fun SidePanelPositionDialog(
    currentPosition: String,
    onPositionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Top" to "Superior (Top)",
        "Center" to "Centro (Center)",
        "Bottom" to "Inferior (Bottom)",
        "Space Evenly" to "Espaçamento Igual (Space Evenly)",
        "Space Between" to "Espaçamento Entre (Space Between)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Alinhamento do Painel Lateral",
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
                            .clickable { onPositionSelected(value) }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentPosition == value),
                            onClick = { onPositionSelected(value) }
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
                Text("Cancelar")
            }
        }
    )
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
