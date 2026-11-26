package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsIntentUtils
import dev.mnascimentos.aureole.feature.settings.extensions.changeFont
import dev.mnascimentos.aureole.feature.settings.extensions.changeManualSeedColor
import dev.mnascimentos.aureole.feature.settings.extensions.changeTheme
import dev.mnascimentos.aureole.feature.settings.extensions.createFolder
import dev.mnascimentos.aureole.feature.settings.extensions.finishCardsGlass
import dev.mnascimentos.aureole.feature.settings.extensions.finishWallpaperEditor
import dev.mnascimentos.aureole.feature.settings.extensions.onSecretGameOpened
import dev.mnascimentos.aureole.feature.settings.extensions.onSecretGameOver
import dev.mnascimentos.aureole.feature.settings.extensions.onSecretSoundToggled
import dev.mnascimentos.aureole.feature.settings.extensions.onUndo
import dev.mnascimentos.aureole.feature.settings.extensions.onUndoExpired
import dev.mnascimentos.aureole.feature.settings.extensions.performFactoryReset
import dev.mnascimentos.aureole.feature.settings.extensions.resetGridLayout
import dev.mnascimentos.aureole.feature.settings.extensions.restoreDefaultWallpaper
import dev.mnascimentos.aureole.feature.settings.extensions.selectPosition
import dev.mnascimentos.aureole.feature.settings.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.settings.extensions.turnLockScreenOff
import dev.mnascimentos.aureole.feature.settings.extensions.turnLockScreenOn
import dev.mnascimentos.aureole.feature.settings.extensions.wallpaperEditorOpened
import dev.mnascimentos.aureole.feature.settings.model.PositionKind
import dev.mnascimentos.aureole.feature.settings.model.SettingToggle
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

@Composable
fun SettingsRoute(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel,
    onFinish: () -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        uri?.let { viewModel.setCustomWallpaper(it.toString()) }
    }

    val actions = remember(context, photoPickerLauncher, onFinish) {
        buildSettingsScreenActions(
            viewModel = viewModel,
            photoPickerLauncher = photoPickerLauncher,
            onFinish = onFinish,
            openDefaultLauncherSettings = { SettingsIntentUtils.openDefaultLauncherSettings(context) }
        )
    }

    SettingsScreen(
        uiState = uiState,
        actions = actions
    )
}

private fun buildSettingsScreenActions(
    viewModel: SettingsViewModel,
    photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>,
    onFinish: () -> Unit,
    openDefaultLauncherSettings: () -> Unit
): SettingsScreenActions {
    val baseActions = SettingsScreenActions(
        onBackClick = onFinish,
        onSecretGameOver = viewModel::onSecretGameOver,
        onSecretGameOpened = viewModel::onSecretGameOpened,
        onSecretSoundToggled = viewModel::onSecretSoundToggled,
        onSetDefaultLauncherClick = openDefaultLauncherSettings,
        onChangeWallpaperClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onSubmitCreateFolder = viewModel::createFolder,
        onRestoreDefaultWallpaperClick = { viewModel.restoreDefaultWallpaper() },
        onUndo = viewModel::onUndo,
        onUndoExpired = viewModel::onUndoExpired,
        onClearErrorMessage = { viewModel.clearErrorMessage() },
        onSelectBackgroundColor = { color ->
            viewModel.changeManualSeedColor(color, R.string.undo_background_changed)
        },
        onSelectTheme = viewModel::changeTheme,
        onSelectFont = viewModel::changeFont,
        onSelectWallpaperScaleType = { scaleType ->
            viewModel.setSettingValue(SettingValue.WallpaperScaleType(scaleType))
        },
        onApplyCardsGlass = { draft ->
            viewModel.setSettingValue(SettingValue.CardsGlass(draft))
        },
        onCardsGlassClosed = viewModel::finishCardsGlass,
        onWallpaperEditorOpened = viewModel::wallpaperEditorOpened,
        onLockScreenTurnOn = viewModel::turnLockScreenOn,
        onLockScreenTurnOff = viewModel::turnLockScreenOff,
        onWallpaperEditorClosed = viewModel::finishWallpaperEditor,
        onHeaderOffsetChanged = viewModel::setHeaderOffsetPercent,
        onConfirmResetGrid = { viewModel.resetGridLayout() },
    )
    return applyTogglesAndDialogs(viewModel, baseActions)
}

private fun applyTogglesAndDialogs(viewModel: SettingsViewModel, base: SettingsScreenActions): SettingsScreenActions {
    val withToggles = applyToggleActions(viewModel, base)
    return applyDialogActions(viewModel, withToggles)
}

private fun applyToggleActions(viewModel: SettingsViewModel, base: SettingsScreenActions): SettingsScreenActions {
    return base.copy(
        onToggleHomeButtonOpensAllApps = { viewModel.toggleSetting(SettingToggle.HOME_OPENS_ALL_APPS) },
        onToggleWidgetRow = { viewModel.toggleSetting(SettingToggle.WIDGET_ROW) },
        onToggleContainerBackground = { viewModel.toggleSetting(SettingToggle.CONTAINER_BACKGROUND) },
        onToggleContainerExpandCell = { viewModel.toggleSetting(SettingToggle.CONTAINER_EXPAND_CELL) },
        onToggleClockBackground = { viewModel.toggleSetting(SettingToggle.CLOCK_BACKGROUND) },
        onToggleShowContainerAddFolderButton = {
            viewModel.toggleSetting(SettingToggle.SHOW_CONTAINER_ADD_FOLDER_BUTTON)
        },
        onToggleShowFolderLabels = { viewModel.toggleSetting(SettingToggle.SHOW_FOLDER_LABELS) },
        onToggleLeftHandedMode = { viewModel.toggleSetting(SettingToggle.LEFT_HANDED_MODE) },
        onToggleDynamicWallpaper = { viewModel.toggleSetting(SettingToggle.DYNAMIC_WALLPAPER) },
        onToggleHaze = { viewModel.toggleSetting(SettingToggle.HAZE) },
        onToggleInAppUpdate = { viewModel.toggleSetting(SettingToggle.IN_APP_UPDATE) },
        onToggleThemedAppIcons = { viewModel.toggleSetting(SettingToggle.THEMED_APP_ICONS) },
        onToggleHideSheetHandle = { viewModel.toggleSetting(SettingToggle.HIDE_SHEET_HANDLE) },
        onToggleDisableAlphabetScrubber = { viewModel.toggleSetting(SettingToggle.DISABLE_ALPHABET_SCRUBBER) },
        onToggleSwipeDownNotifications = { viewModel.toggleSetting(SettingToggle.SWIPE_DOWN_NOTIFICATIONS) },
        onToggleSwipeUpAllApps = { viewModel.toggleSetting(SettingToggle.SWIPE_UP_ALL_APPS) },
        onToggleShowSettingsButtonInAllApps = {
            viewModel.toggleSetting(
                SettingToggle.SHOW_SETTINGS_BUTTON_IN_ALL_APPS
            )
        },
        onToggleShowSearchBarInAllApps = { viewModel.toggleSetting(SettingToggle.SHOW_SEARCH_BAR_IN_ALL_APPS) }
    )
}

private fun applyDialogActions(viewModel: SettingsViewModel, base: SettingsScreenActions): SettingsScreenActions {
    return base.copy(
        onOpenContainerPositionDialog = { viewModel.setDialogVisible(SettingsDialog.CONTAINER_POSITION, true) },
        onDismissContainerPositionDialog = {
            viewModel.setDialogVisible(
                SettingsDialog.CONTAINER_POSITION,
                false
            )
        },
        onContainerPositionSelected = { pos -> viewModel.selectPosition(PositionKind.CONTAINER, pos) },
        onOpenResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, true) },
        onDismissResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, false) },
        onConfirmFactoryReset = { viewModel.performFactoryReset() },
        onOpenFavoritePickerClick = { viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, true) },
        onAddFolderClick = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, true) },
        onDismissCreateFolderDialog = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, false) },
        onOpenSettingsButtonPositionDialog = {
            viewModel.setDialogVisible(
                SettingsDialog.SETTINGS_BUTTON_POSITION,
                true
            )
        },
        onDismissSettingsButtonPositionDialog = {
            viewModel.setDialogVisible(
                SettingsDialog.SETTINGS_BUTTON_POSITION,
                false
            )
        },
        onSettingsButtonPositionSelected = { pos -> viewModel.selectPosition(PositionKind.SETTINGS_BUTTON, pos) },
        onOpenSearchIconPositionDialog = { viewModel.setDialogVisible(SettingsDialog.SEARCH_ICON_POSITION, true) },
        onDismissSearchIconPositionDialog = {
            viewModel.setDialogVisible(
                SettingsDialog.SEARCH_ICON_POSITION,
                false
            )
        },
        onSearchIconPositionSelected = { pos -> viewModel.selectPosition(PositionKind.SEARCH_ICON, pos) },
        onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
        onDismissFavoritePickerDialog = { viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, visible = false) }
    )
}
