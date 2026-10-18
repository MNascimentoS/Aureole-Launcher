package dev.mnascimentos.aureole.feature.home

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var appRepository: AppRepository
    private lateinit var widgetRepository: WidgetRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var favoriteContainerRepository: FavoriteContainerRepository
    private lateinit var containerRepository: ContainerRepository
    private lateinit var gridRepository: GridRepository

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        appRepository = AppRepository(context)
        widgetRepository = WidgetRepository(context)
        folderRepository = FolderRepository(context)
        favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository)
        containerRepository = ContainerRepository(context)
        gridRepository = GridRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = appRepository,
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteContainerRepository,
                containerRepository = containerRepository,
                gridRepository = gridRepository
            )
        )
    }

    @Test
    fun `when initialized, uiState is emitted with default settings`() {
        val state = viewModel.uiState.value
        assertNotNull(state)
        assertFalse(state.showUpdateAvailableDialog)
        assertFalse(state.showUpdateDownloadedDialog)
    }

    @Test
    fun `updateUiState updates MainUiState emissions directly`() {
        viewModel.updateUiState {
            it.copy(
                clockStyle = "DATE_ON_TOP",
                isLeftHandedMode = true,
                searchQuery = "Browser"
            )
        }

        val state = viewModel.uiState.value
        assertEquals("DATE_ON_TOP", state.clockStyle)
        assertTrue(state.isLeftHandedMode)
        assertEquals("Browser", state.searchQuery)
    }

    @Test
    fun `setShowUpdateAvailableDialog updates dialog visibility`() {
        viewModel.setShowUpdateAvailableDialog(true)
        assertTrue(viewModel.uiState.value.showUpdateAvailableDialog)

        viewModel.setShowUpdateAvailableDialog(false)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `setShowUpdateDownloadedDialog updates dialog visibility`() {
        viewModel.setShowUpdateDownloadedDialog(true)
        assertTrue(viewModel.uiState.value.showUpdateDownloadedDialog)

        viewModel.setShowUpdateDownloadedDialog(false)
        assertFalse(viewModel.uiState.value.showUpdateDownloadedDialog)
    }

    @Test
    fun `activeVersionCode and isUpdateInProgressOrDismissed properties can be set and read`() {
        viewModel.activeVersionCode = 123
        viewModel.isUpdateInProgressOrDismissed = true

        assertEquals(123, viewModel.activeVersionCode)
        assertTrue(viewModel.isUpdateInProgressOrDismissed)
    }
}
