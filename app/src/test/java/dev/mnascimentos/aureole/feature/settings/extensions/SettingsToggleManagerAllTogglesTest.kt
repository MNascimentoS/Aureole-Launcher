package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SettingToggle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsToggleManagerAllTogglesTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        val context: Context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            appRepository = AppRepository(context),
            folderRepository = FolderRepository(context),
            gridRepository = GridRepository(context)
        )
    }

    @Test
    fun `when any toggle is flipped, should change the state and flipping again should restore it`() {
        SettingToggle.entries
            .filter { it != SettingToggle.HAZE || settingsRepository.isHazeSupported }
            .forEach { toggle ->
                val before = viewModel.uiState.value

                SettingsToggleManager.toggleSetting(viewModel, toggle)
                assertNotEquals("$toggle should change the state", before, viewModel.uiState.value)

                SettingsToggleManager.toggleSetting(viewModel, toggle)
                assertEquals("$toggle twice should restore the state", before, viewModel.uiState.value)
            }
    }

    @Test
    fun `when toggles are flipped, should persist the new values`() {
        val leftHanded = viewModel.uiState.value.isLeftHandedMode
        val themedIcons = viewModel.uiState.value.isThemedAppIconsEnabled
        val searchBar = viewModel.uiState.value.showSearchBarInAllApps

        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.LEFT_HANDED_MODE)
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.THEMED_APP_ICONS)
        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.SHOW_SEARCH_BAR_IN_ALL_APPS)

        assertEquals(!leftHanded, settingsRepository.isLeftHandedMode)
        assertEquals(!themedIcons, settingsRepository.isThemedAppIconsEnabled)
        assertEquals(!searchBar, settingsRepository.showSearchBarInAllApps)
    }

    @Test
    fun `when haze is not supported, should ignore the haze toggle`() {
        if (settingsRepository.isHazeSupported) return
        val before = viewModel.uiState.value

        SettingsToggleManager.toggleSetting(viewModel, SettingToggle.HAZE)

        assertEquals(before, viewModel.uiState.value)
    }
}
