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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.composable.SettingsActionItem
import dev.mnascimentos.aureole.composable.SettingsMenuItem
import dev.mnascimentos.aureole.composable.SettingsMenuParams
import dev.mnascimentos.aureole.composable.dialogs.CreateFolderDialog
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.SettingsNavGraph
import dev.mnascimentos.aureole.feature.settings.components.ContainerPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog
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

    if (uiState.showFavoritePickerDialog) {
        FavoriteAppsDialog(
            config = FavoriteAppsDialogConfig(
                allApps = uiState.allApps,
                favoriteAppPackages = uiState.favoriteAppPackages
            ),
            actions = FavoriteAppsDialogActions(
                onToggleFavorite = actions.onToggleFavorite,
                onDismiss = actions.onDismissFavoritePickerDialog
            )
        )
    }
}

@Composable
private fun SettingsDialogsSecondary(
    uiState: SettingsUiState,
    actions: SettingsScreenActions
) {
    if (uiState.showContainerPositionDialog) {
        ContainerPositionDialog(
            currentPosition = uiState.containerPosition,
            onPositionSelected = { pos ->
                actions.onContainerPositionSelected(pos)
                actions.onDismissContainerPositionDialog()
            },
            onDismiss = actions.onDismissContainerPositionDialog
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
internal fun FactoryResetDialog(
    actions: SettingsScreenActions = SettingsScreenActions()
) {
    SettingsBottomSheet(
        title = stringResource(R.string.settings_advanced_reset_dialog_title),
        onDismissRequest = actions.onDismissFactoryResetDialog
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.core_common_yes),
                    onClick = { actions.onConfirmFactoryReset() }
                )
            )
            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.core_common_no),
                    onClick = { actions.onDismissFactoryResetDialog() }
                )
            )
        }
    }
}

@Composable
internal fun ResetGridDialog(
    actions: SettingsScreenActions = SettingsScreenActions()
) {
    SettingsBottomSheet(
        title = stringResource(R.string.settings_advanced_reset_layout_title),
        onDismissRequest = actions.onDismissResetGridDialog
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
            SettingsActionItem(
                title = stringResource(R.string.settings_advanced_reset_layout_restore),
                leadingContent = { AureoleDS.icons.Check() },
                onClick = {
                    actions.onConfirmResetGrid()
                    actions.onDismissResetGridDialog()
                }
            )
            SettingsActionItem(
                title = stringResource(R.string.core_common_cancel),
                leadingContent = { AureoleDS.icons.Delete() },
                onClick = { actions.onDismissResetGridDialog() }
            )
        }
    }
}

@Composable
internal fun HazeOpacityDialog(
    currentOpacity: Float = OPACITY_MEDIUM,
    onOpacitySelected: (Float) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val options = listOf(
        OPACITY_LOW to stringResource(R.string.settings_appearance_blur_opacity_low),
        OPACITY_MEDIUM to stringResource(R.string.settings_appearance_blur_opacity_medium),
        OPACITY_HIGH to stringResource(R.string.settings_appearance_blur_opacity_high),
        OPACITY_SOLID to stringResource(R.string.settings_appearance_blur_opacity_solid)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AureoleText(
                text = stringResource(R.string.settings_appearance_blur_opacity_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        containerColor = AureoleDS.colors.surface,
        text = {
            Column {
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                            .clickable { onOpacitySelected(value) }
                            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentOpacity == value),
                            onClick = { onOpacitySelected(value) }
                        )
                        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
                        AureoleText(
                            text = label,
                            color = AureoleDS.colors.onSurfaceHigh,
                            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(stringResource(R.string.core_common_cancel), color = AureoleDS.colors.onSurfaceHigh)
            }
        }
    )
}

@Composable
internal fun RestoreWallpaperDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    SettingsBottomSheet(
        title = stringResource(R.string.settings_appearance_restore_background_title),
        onDismissRequest = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.core_common_yes),
                    onClick = onConfirm
                )
            )
            SettingsMenuItem(
                SettingsMenuParams(
                    title = stringResource(R.string.core_common_no),
                    onClick = onDismiss
                )
            )
        }
    }
}

@AureolePreview
@Composable
private fun SettingsScreenPreview() {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = SettingsUiState(),
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
        )
    }
}
