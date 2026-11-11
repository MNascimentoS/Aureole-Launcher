package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
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
    fun `applySettingValue CardsGlass should save radius, blur, level and opacity`() {
        val draft = CardsGlassDraft(radiusDp = 18, glass = GlassLevel.MAX, opacity = 0.7f)
        viewModel.applySettingValue(SettingValue.CardsGlass(draft))
        assertEquals(18, viewModel.uiState.value.cornerRadiusDp)
        assertEquals(18, settingsRepository.cornerRadiusDp)
        assertEquals(0.7f, settingsRepository.hazeOpacity, 0.001f)
        assertEquals("MAX", settingsRepository.hazeBlurLevel)
        assertEquals(GlassLevel.MAX, viewModel.uiState.value.hazeBlurLevel)
        assertTrue(viewModel.uiState.value.isHazeEnabled)
    }

    @Test
    fun `applySettingValue CardsGlass with no glass should switch the blur off and keep the level`() {
        settingsRepository.hazeBlurLevel = "LOW"
        viewModel.applySettingValue(SettingValue.CardsGlass(CardsGlassDraft(16, GlassLevel.NONE, 0.5f)))
        assertFalse(viewModel.uiState.value.isHazeEnabled)
        assertEquals("LOW", settingsRepository.hazeBlurLevel)
    }
}
