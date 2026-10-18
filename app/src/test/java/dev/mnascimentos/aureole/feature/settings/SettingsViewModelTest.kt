package dev.mnascimentos.aureole.feature.settings

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsViewModelTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var appRepository: AppRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var gridRepository: GridRepository

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        appRepository = AppRepository(context)
        folderRepository = FolderRepository(context)
        gridRepository = GridRepository(context)

        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            appRepository = appRepository,
            folderRepository = folderRepository,
            gridRepository = gridRepository
        )
    }

    @Test
    fun `when initialized, state is emitted with default settings`() {
        val state = viewModel.uiState.value
        assertEquals(settingsRepository.isLeftHandedMode, state.isLeftHandedMode)
        assertEquals(settingsRepository.isClockBackgroundEnabled, state.isClockBackgroundEnabled)
    }

    @Test
    fun `loadSettings reloads settings from repository`() {
        settingsRepository.isLeftHandedMode = true
        viewModel.loadSettings()

        assertTrue(viewModel.uiState.value.isLeftHandedMode)
    }

    @Test
    fun `toggleSetting LeftHandedMode toggles setting state`() {
        val initial = settingsRepository.isLeftHandedMode
        viewModel.toggleSetting(SettingToggle.LEFT_HANDED_MODE)

        assertEquals(!initial, settingsRepository.isLeftHandedMode)
        assertEquals(!initial, viewModel.uiState.value.isLeftHandedMode)
    }

    @Test
    fun `setDialogVisible updates specific dialog visibility in uiState`() {
        viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, true)
        assertTrue(viewModel.uiState.value.showFavoritePickerDialog)

        viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, false)
        assertFalse(viewModel.uiState.value.showFavoritePickerDialog)
    }

    @Test
    fun `setHeaderOffsetPercent clamps and updates header offset`() {
        viewModel.setHeaderOffsetPercent(25)
        assertEquals(25, viewModel.uiState.value.headerOffsetPercent)

        viewModel.setHeaderOffsetPercent(100) // Above max clamp (60)
        assertEquals(60, viewModel.uiState.value.headerOffsetPercent)

        viewModel.setHeaderOffsetPercent(-10) // Below min clamp (0)
        assertEquals(0, viewModel.uiState.value.headerOffsetPercent)
    }

    @Test
    fun `toggleFavorite adds and removes package from favorites`() {
        val pkg = "com.example.app"

        viewModel.toggleFavorite(pkg)
        assertTrue(viewModel.uiState.value.favoriteAppPackages.contains(pkg))

        viewModel.toggleFavorite(pkg)
        assertFalse(viewModel.uiState.value.favoriteAppPackages.contains(pkg))
    }

    @Test
    fun `onActivityFinishedHandled resets shouldFinishActivity state`() {
        viewModel.updateUiState { it.copy(shouldFinishActivity = true) }
        assertTrue(viewModel.uiState.value.shouldFinishActivity)

        viewModel.onActivityFinishedHandled()
        assertFalse(viewModel.uiState.value.shouldFinishActivity)
    }

    @Test
    fun `clearErrorMessage resets error message to null`() {
        viewModel.updateUiState { it.copy(errorMessage = "Sample Error") }
        assertEquals("Sample Error", viewModel.uiState.value.errorMessage)

        viewModel.clearErrorMessage()
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `setDialogVisible for all dialog types updates visibility correctly`() {
        viewModel.setDialogVisible(SettingsDialog.CONTAINER_POSITION, true)
        assertTrue(viewModel.uiState.value.showContainerPositionDialog)

        viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, true)
        assertTrue(viewModel.uiState.value.showHazeOpacityDialog)

        viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, true)
        assertTrue(viewModel.uiState.value.showColorPickerDialog)

        viewModel.setDialogVisible(SettingsDialog.RESET_GRID, true)
        assertTrue(viewModel.uiState.value.showResetGridDialog)

        viewModel.setDialogVisible(SettingsDialog.FACTORY_RESET, true)
        assertTrue(viewModel.uiState.value.showFactoryResetDialog)
    }

    @Test
    fun `toggleSetting for CONTAINER and HAZE toggles settings state`() {
        val initialContainer = settingsRepository.isContainerEnabled
        viewModel.toggleSetting(SettingToggle.CONTAINER)
        assertEquals(!initialContainer, settingsRepository.isContainerEnabled)

        val initialHaze = settingsRepository.isHazeEnabled
        viewModel.toggleSetting(SettingToggle.HAZE)
        assertEquals(!initialHaze, settingsRepository.isHazeEnabled)
    }

    @Test
    fun `setSettingValue ContainerPosition updates container position`() {
        viewModel.setSettingValue(SettingValue.ContainerPosition("Right"))
        assertEquals("Right", viewModel.uiState.value.containerPosition)
    }
}
