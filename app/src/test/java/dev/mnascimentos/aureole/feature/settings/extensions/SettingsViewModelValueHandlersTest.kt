package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsViewModelValueHandlersTest {

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
    fun `applySettingValue ContainerPosition should update container position`() {
        viewModel.applySettingValue(SettingValue.ContainerPosition("Center"))
        assertEquals("Center", viewModel.uiState.value.containerPosition)
        assertEquals("Center", settingsRepository.containerPosition)
        assertFalse(viewModel.uiState.value.showContainerPositionDialog)
    }

    @Test
    fun `applySettingValue HazeOpacity should update haze opacity`() {
        viewModel.applySettingValue(SettingValue.HazeOpacity(0.85f))
        assertEquals(0.85f, viewModel.uiState.value.hazeOpacity, 0.001f)
        assertEquals(0.85f, settingsRepository.hazeOpacity, 0.001f)
        assertFalse(viewModel.uiState.value.showHazeOpacityDialog)
    }

    @Test
    fun `applySettingValue ManualSeedColor should update manual seed color and theme`() {
        val colorInt = 0xFF123456.toInt()
        viewModel.applySettingValue(SettingValue.ManualSeedColor(colorInt))
        assertEquals(colorInt, viewModel.uiState.value.manualSeedColor)
        assertEquals("Custom", viewModel.uiState.value.selectedThemeName)
        assertFalse(viewModel.uiState.value.isCustomWallpaperSet)
        assertNull(viewModel.uiState.value.customWallpaperPath)
        assertFalse(viewModel.uiState.value.showColorPickerDialog)
    }

    @Test
    fun `applySettingValue SelectedTheme should update theme and seed color`() {
        viewModel.applySettingValue(SettingValue.SelectedTheme("OLED Dark"))
        assertEquals("OLED Dark", viewModel.uiState.value.selectedThemeName)
        assertEquals("OLED Dark", settingsRepository.selectedThemeName)
    }

    @Test
    fun `applySettingValue SelectedFont should update font name`() {
        viewModel.applySettingValue(SettingValue.SelectedFont("Roboto"))
        assertEquals("Roboto", viewModel.uiState.value.selectedFontName)
        assertEquals("Roboto", settingsRepository.selectedFontName)
    }

    @Test
    fun `applySettingValue SettingsButtonPosition should update settings button position`() {
        viewModel.applySettingValue(SettingValue.SettingsButtonPosition("Right"))
        assertEquals("Right", viewModel.uiState.value.settingsButtonPosition)
        assertEquals("Right", settingsRepository.settingsButtonPosition)
        assertFalse(viewModel.uiState.value.showSettingsButtonPositionDialog)
    }

    @Test
    fun `applySettingValue SearchIconPosition should update search icon position`() {
        viewModel.applySettingValue(SettingValue.SearchIconPosition("Left"))
        assertEquals("Left", viewModel.uiState.value.searchIconPosition)
        assertEquals("Left", settingsRepository.searchIconPosition)
        assertFalse(viewModel.uiState.value.showSearchIconPositionDialog)
    }

    @Test
    fun `applySettingValue WallpaperScaleType should update wallpaper scale type`() {
        viewModel.applySettingValue(SettingValue.WallpaperScaleType("CENTER_CROP"))
        assertEquals("CENTER_CROP", viewModel.uiState.value.wallpaperScaleType)
        assertEquals("CENTER_CROP", settingsRepository.wallpaperScaleType)
    }

    @Test
    fun `applySettingValue CornerRadius should update corner radius`() {
        viewModel.applySettingValue(SettingValue.CornerRadius(18))
        assertEquals(18, viewModel.uiState.value.cornerRadiusDp)
        assertEquals(18, settingsRepository.cornerRadiusDp)
    }
}
