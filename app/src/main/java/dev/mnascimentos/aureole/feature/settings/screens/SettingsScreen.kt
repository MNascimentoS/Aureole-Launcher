package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.composable.SettingsActionItem
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.settings.SettingsNavGraph
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SidePanelPositionDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

private const val OPACITY_LOW = 0.20f
private const val OPACITY_MEDIUM = 0.50f
private const val OPACITY_HIGH = 0.70f
private const val OPACITY_SOLID = 0.90f

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

    SettingsNavGraph(
        uiState = uiState,
        actions = actions
    )

    SettingsDialogs(uiState = uiState, actions = actions)
}

@Composable
private fun SettingsDialogs(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    SettingsDialogsPrimary(uiState = uiState, actions = actions)
    SettingsDialogsSecondary(uiState = uiState, actions = actions)
}

@Composable
private fun SettingsDialogsPrimary(
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
}

@Composable
private fun SettingsDialogsSecondary(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
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

    if (uiState.showSettingsButtonPositionDialog) {
        SettingsButtonPositionDialog(
            currentPosition = uiState.settingsButtonPosition,
            onPositionSelected = { pos ->
                actions.onSettingsButtonPositionSelected(pos)
                actions.onDismissSettingsButtonPositionDialog()
            },
            onDismiss = actions.onDismissSettingsButtonPositionDialog
        )
    }

    if (uiState.showSearchIconPositionDialog) {
        SearchIconPositionDialog(
            currentPosition = uiState.searchIconPosition,
            onPositionSelected = { pos ->
                actions.onSearchIconPositionSelected(pos)
                actions.onDismissSearchIconPositionDialog()
            },
            onDismiss = actions.onDismissSearchIconPositionDialog
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
    SettingsBottomSheet(
        title = "Reset launcher?",
        onDismissRequest = actions.onDismissFactoryResetDialog
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsMenuItem(
                title = "Yes",
                onClick = { actions.onConfirmFactoryReset() }
            )
            SettingsMenuItem(
                title = "No",
                onClick = { actions.onDismissFactoryResetDialog() }
            )
        }
    }
}

@Composable
private fun ResetGridDialog(
    actions: SettingsScreenActions
) {
    SettingsBottomSheet(
        title = "Reset Layout?",
        onDismissRequest = actions.onDismissResetGridDialog
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SettingsActionItem(
                title = "Restore",
                leadingContent = { AureoleDS.icons.Check() },
                onClick = {
                    actions.onConfirmResetGrid()
                    actions.onDismissResetGridDialog()
                }
            )
            SettingsActionItem(
                title = "Cancel",
                leadingContent = { AureoleDS.icons.Delete() },
                onClick = { actions.onDismissResetGridDialog() }
            )
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
        OPACITY_LOW to "Low",
        OPACITY_MEDIUM to "Medium",
        OPACITY_HIGH to "High",
        OPACITY_SOLID to "Solid"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AureoleText(
                text = "Blur Opacity",
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        },
        containerColor = AureoleTheme.colors.surface,
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
                        AureoleText(
                            text = label,
                            style = AureoleTheme.typography.bodyLarge,
                            color = AureoleTheme.colors.onSurfaceHigh
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText("Cancel", color = AureoleTheme.colors.onSurfaceHigh)
            }
        }
    )
}

@Composable
private fun RestoreWallpaperDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SettingsBottomSheet(
        title = "Restore default background?",
        onDismissRequest = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SettingsMenuItem(
                title = "Yes",
                onClick = onConfirm
            )
            SettingsMenuItem(
                title = "No",
                onClick = onDismiss
            )
        }
    }
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
