package dev.mnascimentos.aureole.feature.settings.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsIntentUtils
import dev.mnascimentos.aureole.feature.settings.extensions.performFactoryReset
import dev.mnascimentos.aureole.feature.settings.extensions.resetGridLayout
import dev.mnascimentos.aureole.feature.settings.extensions.restoreDefaultWallpaper
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
        onSetDefaultLauncherClick = openDefaultLauncherSettings,
        onChangeWallpaperClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        },
        onSubmitCreateFolder = viewModel::createFolder,
        onConfirmRestoreWallpaper = { viewModel.restoreDefaultWallpaper() },
        onClearErrorMessage = { viewModel.clearErrorMessage() },
        onSelectManualSeedColor = { color ->
            viewModel.setSettingValue(SettingValue.ManualSeedColor(color))
        },
        onSelectTheme = { themeName ->
            viewModel.setSettingValue(SettingValue.SelectedTheme(themeName))
        },
        onSelectFont = { fontName ->
            viewModel.setSettingValue(SettingValue.SelectedFont(fontName))
        },
        onHazeOpacitySelected = { opacity ->
            viewModel.setSettingValue(SettingValue.HazeOpacity(opacity))
        },
        onSelectWallpaperScaleType = { scaleType ->
            viewModel.setSettingValue(SettingValue.WallpaperScaleType(scaleType))
        },
        onSelectCornerRadius = { radiusDp ->
            viewModel.setSettingValue(SettingValue.CornerRadius(radiusDp))
        },
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
        onToggleShowAllAppsOnHome = { viewModel.toggleSetting(SettingToggle.SHOW_ALL_APPS_ON_HOME) },
        onToggleHomeButtonOpensAllApps = { viewModel.toggleSetting(SettingToggle.HOME_OPENS_ALL_APPS) },
        onToggleWidgetRow = { viewModel.toggleSetting(SettingToggle.WIDGET_ROW) },
        onToggleShowWidgetDots = { viewModel.toggleSetting(SettingToggle.SHOW_WIDGET_DOTS) },
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
        onToggleDisableAlphabetScrubber = { viewModel.toggleSetting(SettingToggle.DISABLE_ALPHABET_SCRUBBER) },
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
        onContainerPositionSelected = { pos -> viewModel.setSettingValue(SettingValue.ContainerPosition(pos)) },
        onOpenResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, true) },
        onDismissResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, false) },
        onOpenFactoryResetDialog = { viewModel.setDialogVisible(SettingsDialog.FACTORY_RESET, true) },
        onDismissFactoryResetDialog = { viewModel.setDialogVisible(SettingsDialog.FACTORY_RESET, false) },
        onConfirmFactoryReset = { viewModel.performFactoryReset() },
        onOpenFavoritePickerClick = { viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, true) },
        onOpenColorPickerDialog = { viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, true) },
        onDismissColorPickerDialog = { viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, false) },
        onRestoreDefaultWallpaperClick = { viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, true) },
        onDismissRestoreWallpaperDialog = { viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, false) },
        onAddFolderClick = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, true) },
        onDismissCreateFolderDialog = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, false) },
        onOpenHazeOpacityDialog = { viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, true) },
        onDismissHazeOpacityDialog = { viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, false) },
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
        onSettingsButtonPositionSelected = { pos ->
            viewModel.setSettingValue(
                SettingValue.SettingsButtonPosition(pos)
            )
        },
        onOpenSearchIconPositionDialog = { viewModel.setDialogVisible(SettingsDialog.SEARCH_ICON_POSITION, true) },
        onDismissSearchIconPositionDialog = {
            viewModel.setDialogVisible(
                SettingsDialog.SEARCH_ICON_POSITION,
                false
            )
        },
        onSearchIconPositionSelected = { pos -> viewModel.setSettingValue(SettingValue.SearchIconPosition(pos)) },
        onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
        onDismissFavoritePickerDialog = { viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, visible = false) }
    )
}
