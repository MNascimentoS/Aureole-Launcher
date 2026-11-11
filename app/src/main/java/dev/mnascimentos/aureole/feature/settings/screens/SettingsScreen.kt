package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import dev.mnascimentos.aureole.composable.undo.AureoleUndoSnackbar
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.SettingsNavGraph
import dev.mnascimentos.aureole.feature.settings.components.ContainerPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SearchIconPositionDialog
import dev.mnascimentos.aureole.feature.settings.components.SettingsButtonPositionDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

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

    Box(modifier = Modifier.fillMaxSize()) {
        SettingsNavGraph(
            uiState = uiState,
            actions = actions
        )

        SettingsDialogs(uiState = uiState, actions = actions)

        AureoleUndoSnackbar(
            state = uiState.undoSnackbar,
            isLeftHanded = uiState.isLeftHandedMode,
            onUndo = actions.onUndo,
            onTimeout = actions.onUndoExpired,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                .padding(AureoleDS.dimens.small)
        )
    }
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
    if (uiState.showColorPickerDialog) {
        ColorPickerDialog(
            initialColor = uiState.manualSeedColor,
            onColorSelected = actions.onSelectManualSeedColor,
            onDismiss = actions.onDismissColorPickerDialog,
            isLeftHanded = uiState.isLeftHandedMode
        )
    }

    if (uiState.showCreateFolderDialog) {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = uiState.isLeftHandedMode)) {
            CreateFolderDialog(
                onDismiss = actions.onDismissCreateFolderDialog,
                onSubmit = actions.onSubmitCreateFolder
            )
        }
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
    CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = uiState.isLeftHandedMode)) {
        SettingsPositionSheets(uiState = uiState, actions = actions)
    }

    if (uiState.showFactoryResetDialog) {
        FactoryResetDialog(actions = actions)
    }
}

@Composable
private fun SettingsPositionSheets(
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
