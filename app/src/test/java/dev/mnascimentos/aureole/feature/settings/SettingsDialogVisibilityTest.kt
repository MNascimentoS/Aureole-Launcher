package dev.mnascimentos.aureole.feature.settings

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.feature.settings.model.SettingsDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SettingsDialogVisibilityTest {

    private val context: Context = RuntimeEnvironment.getApplication()
    private val viewModel = SettingsViewModel(
        settingsRepository = SettingsRepository(context),
        appRepository = AppRepository(context),
        folderRepository = FolderRepository(context),
        gridRepository = GridRepository(context)
    )

    private fun SettingsUiState.isShowing(dialog: SettingsDialog): Boolean = when (dialog) {
        SettingsDialog.FAVORITE_PICKER -> showFavoritePickerDialog
        SettingsDialog.CONTAINER_POSITION -> showContainerPositionDialog
        SettingsDialog.HAZE_OPACITY -> showHazeOpacityDialog
        SettingsDialog.COLOR_PICKER -> showColorPickerDialog
        SettingsDialog.RESTORE_WALLPAPER -> showRestoreWallpaperDialog
        SettingsDialog.CREATE_FOLDER -> showCreateFolderDialog
        SettingsDialog.RESET_GRID -> showResetGridDialog
        SettingsDialog.FACTORY_RESET -> showFactoryResetDialog
        SettingsDialog.SETTINGS_BUTTON_POSITION -> showSettingsButtonPositionDialog
        SettingsDialog.SEARCH_ICON_POSITION -> showSearchIconPositionDialog
    }

    @Test
    fun `when any dialog is shown and hidden, should toggle only that dialog`() {
        SettingsDialog.entries.forEach { dialog ->
            viewModel.setDialogVisible(dialog, true)
            assertTrue("$dialog should be visible", viewModel.uiState.value.isShowing(dialog))
            SettingsDialog.entries.filter { it != dialog }.forEach { other ->
                assertFalse("$other should stay hidden", viewModel.uiState.value.isShowing(other))
            }

            viewModel.setDialogVisible(dialog, false)
            assertFalse("$dialog should be hidden", viewModel.uiState.value.isShowing(dialog))
        }
    }
}
