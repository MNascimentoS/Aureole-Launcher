package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SettingToggle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsToggleManagerTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            appRepository = AppRepository(context),
            folderRepository = FolderRepository(context),
            gridRepository = GridRepository(context)
        )
    }

    @Test
    fun `toggle SHOW_WIDGET_DOTS should flip showWidgetDots state`() {
        val initial = viewModel.uiState.value.showWidgetDots
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_WIDGET_DOTS)
        assertEquals(!initial, viewModel.uiState.value.showWidgetDots)
        assertEquals(!initial, settingsRepository.showWidgetDots)
    }

    @Test
    fun `toggle HAZE when supported should flip isHazeEnabled state`() {
        if (!settingsRepository.isHazeSupported) {
            return
        }
        val initial = viewModel.uiState.value.isHazeEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.HAZE)
        assertEquals(!initial, viewModel.uiState.value.isHazeEnabled)
        assertEquals(!initial, settingsRepository.isHazeEnabled)
    }

    @Test
    fun `toggle LEFT_HANDED_MODE should flip isLeftHandedMode state`() {
        val initial = viewModel.uiState.value.isLeftHandedMode
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.LEFT_HANDED_MODE)
        assertEquals(!initial, viewModel.uiState.value.isLeftHandedMode)
        assertEquals(!initial, settingsRepository.isLeftHandedMode)
    }

    @Test
    fun `toggle CONTAINER_BACKGROUND should flip isContainerBackgroundEnabled state`() {
        val initial = viewModel.uiState.value.isContainerBackgroundEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.CONTAINER_BACKGROUND)
        assertEquals(!initial, viewModel.uiState.value.isContainerBackgroundEnabled)
        assertEquals(!initial, settingsRepository.isContainerBackgroundEnabled)
    }

    @Test
    fun `toggle CONTAINER_EXPAND_CELL should flip isContainerExpandCell state`() {
        val initial = viewModel.uiState.value.isContainerExpandCell
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.CONTAINER_EXPAND_CELL)
        assertEquals(!initial, viewModel.uiState.value.isContainerExpandCell)
        assertEquals(!initial, settingsRepository.isContainerExpandCell)
    }

    @Test
    fun `toggle CLOCK_BACKGROUND should flip isClockBackgroundEnabled state`() {
        val initial = viewModel.uiState.value.isClockBackgroundEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.CLOCK_BACKGROUND)
        assertEquals(!initial, viewModel.uiState.value.isClockBackgroundEnabled)
        assertEquals(!initial, settingsRepository.isClockBackgroundEnabled)
    }

    @Test
    fun `toggle CONTAINER should flip isContainerEnabled state`() {
        val initial = viewModel.uiState.value.isContainerEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.CONTAINER)
        assertEquals(!initial, viewModel.uiState.value.isContainerEnabled)
        assertEquals(!initial, settingsRepository.isContainerEnabled)
    }

    @Test
    fun `toggle SHOW_CONTAINER_ADD_FOLDER_BUTTON should flip showContainerAddFolderButton state`() {
        val initial = viewModel.uiState.value.showContainerAddFolderButton
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_CONTAINER_ADD_FOLDER_BUTTON)
        assertEquals(!initial, viewModel.uiState.value.showContainerAddFolderButton)
        assertEquals(!initial, settingsRepository.showContainerAddFolderButton)
    }

    @Test
    fun `toggle SHOW_FOLDER_LABELS should flip showFolderLabels state`() {
        val initial = viewModel.uiState.value.showFolderLabels
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_FOLDER_LABELS)
        assertEquals(!initial, viewModel.uiState.value.showFolderLabels)
        assertEquals(!initial, settingsRepository.showFolderLabels)
    }

    @Test
    fun `toggle HOME_OPENS_ALL_APPS should flip homeButtonOpensAllApps state`() {
        val initial = viewModel.uiState.value.homeButtonOpensAllApps
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.HOME_OPENS_ALL_APPS)
        assertEquals(!initial, viewModel.uiState.value.homeButtonOpensAllApps)
        assertEquals(!initial, settingsRepository.homeButtonOpensAllApps)
    }

    @Test
    fun `toggle SHOW_ALL_APPS_ON_HOME should flip showAllAppsOnHome state`() {
        val initial = viewModel.uiState.value.showAllAppsOnHome
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_ALL_APPS_ON_HOME)
        assertEquals(!initial, viewModel.uiState.value.showAllAppsOnHome)
        assertEquals(!initial, settingsRepository.showAllAppsOnHome)
    }

    @Test
    fun `toggle WIDGET_ROW should flip isWidgetRowEnabled state`() {
        val initial = viewModel.uiState.value.isWidgetRowEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.WIDGET_ROW)
        assertEquals(!initial, viewModel.uiState.value.isWidgetRowEnabled)
        assertEquals(!initial, settingsRepository.isWidgetRowEnabled)
    }

    @Test
    fun `toggle DISABLE_ALPHABET_SCRUBBER should flip isAlphabetScrubberDisabled state`() {
        val initial = viewModel.uiState.value.isAlphabetScrubberDisabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.DISABLE_ALPHABET_SCRUBBER)
        assertEquals(!initial, viewModel.uiState.value.isAlphabetScrubberDisabled)
        assertEquals(!initial, settingsRepository.isAlphabetScrubberDisabled)
    }

    @Test
    fun `toggle SHOW_SETTINGS_BUTTON_IN_ALL_APPS should flip showSettingsButtonInAllApps state`() {
        val initial = viewModel.uiState.value.showSettingsButtonInAllApps
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_SETTINGS_BUTTON_IN_ALL_APPS)
        assertEquals(!initial, viewModel.uiState.value.showSettingsButtonInAllApps)
        assertEquals(!initial, settingsRepository.showSettingsButtonInAllApps)
    }

    @Test
    fun `toggle SHOW_SEARCH_BAR_IN_ALL_APPS should flip showSearchBarInAllApps state`() {
        val initial = viewModel.uiState.value.showSearchBarInAllApps
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_SEARCH_BAR_IN_ALL_APPS)
        assertEquals(!initial, viewModel.uiState.value.showSearchBarInAllApps)
        assertEquals(!initial, settingsRepository.showSearchBarInAllApps)
    }

    @Test
    fun `toggle DYNAMIC_WALLPAPER should flip isDynamicWallpaperEnabled state`() {
        val initial = viewModel.uiState.value.isDynamicWallpaperEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.DYNAMIC_WALLPAPER)
        assertEquals(!initial, viewModel.uiState.value.isDynamicWallpaperEnabled)
        assertEquals(!initial, settingsRepository.isDynamicWallpaperEnabled)
    }

    @Test
    fun `toggle IN_APP_UPDATE should flip isInAppUpdateEnabled state`() {
        val initial = viewModel.uiState.value.isInAppUpdateEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.IN_APP_UPDATE)
        assertEquals(!initial, viewModel.uiState.value.isInAppUpdateEnabled)
        assertEquals(!initial, settingsRepository.isInAppUpdateEnabled)
    }

    @Test
    fun `toggle THEMED_APP_ICONS should flip isThemedAppIconsEnabled state`() {
        val initial = viewModel.uiState.value.isThemedAppIconsEnabled
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.THEMED_APP_ICONS)
        assertEquals(!initial, viewModel.uiState.value.isThemedAppIconsEnabled)
        assertEquals(!initial, settingsRepository.isThemedAppIconsEnabled)
    }
}
