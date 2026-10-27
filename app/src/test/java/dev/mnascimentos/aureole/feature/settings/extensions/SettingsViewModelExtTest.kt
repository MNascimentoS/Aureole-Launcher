package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
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
class SettingsViewModelExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel
    private var processTerminated = false

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        processTerminated = false
        settingsRepository.terminateProcess = { processTerminated = true }
        viewModel = SettingsViewModel(
            settingsRepository = settingsRepository,
            appRepository = AppRepository(context),
            folderRepository = FolderRepository(context),
            gridRepository = GridRepository(context)
        )
    }

    @Test
    fun `checkDefaultLauncher should update isDefaultLauncher in uiState`() {
        viewModel.checkDefaultLauncher()
        val expected = settingsRepository.checkDefaultLauncher()
        assertEquals(expected, viewModel.uiState.value.isDefaultLauncher)
    }

    @Test
    fun `restoreDefaultWallpaper should update custom wallpaper fields in uiState`() {
        viewModel.restoreDefaultWallpaper()
        assertFalse(viewModel.uiState.value.isCustomWallpaperSet)
        assertNull(viewModel.uiState.value.customWallpaperPath)
        assertFalse(viewModel.uiState.value.showRestoreWallpaperDialog)
    }

    @Test
    fun `resetGridLayout should set reset flags and message in uiState`() {
        viewModel.resetGridLayout()
        assertFalse(viewModel.uiState.value.showResetGridDialog)
        assertTrue(viewModel.uiState.value.shouldFinishActivity)
        assertEquals("Layout da tela inicial resetado para o padrão", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `performFactoryReset should dismiss factory reset dialog in uiState`() {
        viewModel.performFactoryReset()
        assertFalse(viewModel.uiState.value.showFactoryResetDialog)
    }

    @Test
    fun `performFactoryReset should clear settings and terminate the process`() {
        settingsRepository.isLeftHandedMode = true

        settingsRepository.performFactoryReset()

        assertTrue(processTerminated)
        assertFalse(settingsRepository.isLeftHandedMode)
    }
}
