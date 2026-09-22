package dev.mnascimentos.aureole.feature.settings

internal object SettingsToggleManager {
    fun toggleSetting(vm: SettingsViewModel, toggle: SettingToggle) {
        when (toggle) {
            SettingToggle.SHOW_WIDGET_DOTS,
            SettingToggle.HAZE,
            SettingToggle.LEFT_HANDED_MODE,
            SettingToggle.SIDE_PANEL_BACKGROUND,
            SettingToggle.SIDE_PANEL_EXPAND_CELL,
            SettingToggle.CLOCK_BACKGROUND,
            SettingToggle.SHOW_SIDE_PANEL_ADD_FOLDER_BUTTON,
            SettingToggle.SIDE_PANEL -> toggleGroupOne(vm, toggle)
            SettingToggle.SHOW_FOLDER_LABELS,
            SettingToggle.HOME_OPENS_ALL_APPS,
            SettingToggle.SHOW_ALL_APPS_ON_HOME,
            SettingToggle.WIDGET_ROW,
            SettingToggle.DISABLE_ALPHABET_SCRUBBER -> toggleGroupTwo(vm, toggle)
            SettingToggle.DYNAMIC_WALLPAPER,
            SettingToggle.IN_APP_UPDATE,
            SettingToggle.THEMED_APP_ICONS -> toggleGroupThree(vm, toggle)
        }
    }

    private fun toggleGroupOne(vm: SettingsViewModel, toggle: SettingToggle) {
        when (toggle) {
            SettingToggle.SHOW_WIDGET_DOTS -> {
                val newValue = !vm.uiState.value.showWidgetDots
                vm.settingsRepository.showWidgetDots = newValue
                vm.updateUiState { it.copy(showWidgetDots = newValue) }
            }
            SettingToggle.HAZE -> {
                if (!vm.settingsRepository.isHazeSupported) return
                val newValue = !vm.uiState.value.isHazeEnabled
                vm.settingsRepository.isHazeEnabled = newValue
                vm.updateUiState { it.copy(isHazeEnabled = newValue) }
            }
            SettingToggle.LEFT_HANDED_MODE -> {
                val newValue = !vm.uiState.value.isLeftHandedMode
                vm.settingsRepository.isLeftHandedMode = newValue
                vm.updateUiState { it.copy(isLeftHandedMode = newValue) }
            }
            SettingToggle.SIDE_PANEL_BACKGROUND -> {
                val newValue = !vm.uiState.value.isSidePanelBackgroundEnabled
                vm.settingsRepository.isSidePanelBackgroundEnabled = newValue
                vm.updateUiState { it.copy(isSidePanelBackgroundEnabled = newValue) }
            }
            SettingToggle.SIDE_PANEL_EXPAND_CELL -> {
                val newValue = !vm.uiState.value.isSidePanelExpandCell
                vm.settingsRepository.isSidePanelExpandCell = newValue
                vm.updateUiState { it.copy(isSidePanelExpandCell = newValue) }
            }
            SettingToggle.CLOCK_BACKGROUND -> {
                val newValue = !vm.uiState.value.isClockBackgroundEnabled
                vm.settingsRepository.isClockBackgroundEnabled = newValue
                vm.updateUiState { it.copy(isClockBackgroundEnabled = newValue) }
            }
            SettingToggle.SIDE_PANEL -> {
                val newValue = !vm.uiState.value.isSidePanelEnabled
                vm.settingsRepository.isSidePanelEnabled = newValue
                vm.updateUiState { it.copy(isSidePanelEnabled = newValue) }
            }
            SettingToggle.SHOW_SIDE_PANEL_ADD_FOLDER_BUTTON -> {
                val newValue = !vm.uiState.value.showSidePanelAddFolderButton
                vm.settingsRepository.showSidePanelAddFolderButton = newValue
                vm.updateUiState { it.copy(showSidePanelAddFolderButton = newValue) }
            }
            else -> {}
        }
    }

    private fun toggleGroupTwo(vm: SettingsViewModel, toggle: SettingToggle) {
        when (toggle) {
            SettingToggle.SHOW_FOLDER_LABELS -> {
                val newValue = !vm.uiState.value.showFolderLabels
                vm.settingsRepository.showFolderLabels = newValue
                vm.updateUiState { it.copy(showFolderLabels = newValue) }
            }
            SettingToggle.HOME_OPENS_ALL_APPS -> {
                val newValue = !vm.uiState.value.homeButtonOpensAllApps
                vm.settingsRepository.homeButtonOpensAllApps = newValue
                vm.updateUiState { it.copy(homeButtonOpensAllApps = newValue) }
            }
            SettingToggle.SHOW_ALL_APPS_ON_HOME -> {
                val newValue = !vm.uiState.value.showAllAppsOnHome
                vm.settingsRepository.showAllAppsOnHome = newValue
                vm.updateUiState { it.copy(showAllAppsOnHome = newValue) }
            }
            SettingToggle.WIDGET_ROW -> {
                val newValue = !vm.uiState.value.isWidgetRowEnabled
                vm.settingsRepository.isWidgetRowEnabled = newValue
                vm.updateUiState { it.copy(isWidgetRowEnabled = newValue) }
            }
            SettingToggle.DISABLE_ALPHABET_SCRUBBER -> {
                val newValue = !vm.uiState.value.isAlphabetScrubberDisabled
                vm.settingsRepository.isAlphabetScrubberDisabled = newValue
                vm.updateUiState { it.copy(isAlphabetScrubberDisabled = newValue) }
            }
            else -> {}
        }
    }

    private fun toggleGroupThree(vm: SettingsViewModel, toggle: SettingToggle) {
        when (toggle) {
            SettingToggle.DYNAMIC_WALLPAPER -> {
                val newValue = !vm.uiState.value.isDynamicWallpaperEnabled
                vm.settingsRepository.isDynamicWallpaperEnabled = newValue
                vm.updateUiState { it.copy(isDynamicWallpaperEnabled = newValue) }
            }
            SettingToggle.IN_APP_UPDATE -> {
                val newValue = !vm.uiState.value.isInAppUpdateEnabled
                vm.settingsRepository.isInAppUpdateEnabled = newValue
                vm.updateUiState { it.copy(isInAppUpdateEnabled = newValue) }
            }
            SettingToggle.THEMED_APP_ICONS -> {
                val newValue = !vm.uiState.value.isThemedAppIconsEnabled
                vm.settingsRepository.isThemedAppIconsEnabled = newValue
                vm.updateUiState { it.copy(isThemedAppIconsEnabled = newValue) }
            }
            else -> {}
        }
    }
}
