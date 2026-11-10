package dev.mnascimentos.aureole.feature.home.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.WidgetStackBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.isOverlayActive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MainActivityNavExtTest {

    private lateinit var viewModel: HomeViewModel

    /** One step of the back stack: what is open, and how to tell it was closed. */
    private data class BackCase(
        val name: String,
        val open: (MainUiState) -> MainUiState,
        val isClosed: (MainUiState) -> Boolean
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context: Context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when back is pressed, should close only the topmost overlay`() {
        val cases = listOf(
            BackCase("release notes", { it.copy(showReleaseNotesBottomSheet = true) }) {
                !it.showReleaseNotesBottomSheet
            },
            BackCase("widget picker", { it.copy(showWidgetPicker = true) }) { !it.showWidgetPicker },
            BackCase("widget popup", { it.copy(showWidgetPopup = true) }) { !it.showWidgetPopup },
            BackCase("widget resize", { it.copy(showWidgetResizeDialog = true) }) { !it.showWidgetResizeDialog },
            BackCase("opened folder", { it.copy(activeFolder = AppFolder(id = "f", name = "F")) }) {
                it.activeFolder == null
            },
            BackCase("create folder", { it.copy(isCreateFolderDialogVisible = true) }) {
                !it.isCreateFolderDialogVisible
            },
            BackCase("add app to folder", { it.copy(isAddAppToFolderDialogVisible = true) }) {
                !it.isAddAppToFolderDialogVisible
            },
            BackCase("rename folder", { it.copy(isRenameFolderDialogVisible = true) }) {
                !it.isRenameFolderDialogVisible
            },
            BackCase("all apps drawer", { it.copy(isAllAppsDrawerOpen = true) }) { !it.isAllAppsDrawerOpen },
            BackCase("search", { it.copy(searchQuery = "mail") }) { it.searchQuery.isEmpty() },
            BackCase("widget stack sheet", {
                it.copy(activeWidgetStackBottomSheet = WidgetStackBottomSheetState(widgetId = 1, stackId = "s"))
            }) { it.activeWidgetStackBottomSheet == null },
            BackCase("favorite picker", { it.copy(showFavoritePickerDialog = true) }) {
                !it.showFavoritePickerDialog
            },
            BackCase("add container", { it.copy(showAddContainerDialog = true) }) { !it.showAddContainerDialog },
            BackCase("editing grid item", { it.copy(editingGridItem = LauncherItemState(id = "item")) }) {
                it.editingGridItem == null
            },
            BackCase("edit container", { it.copy(isEditContainerDialogVisible = true) }) {
                !it.isEditContainerDialogVisible
            },
            BackCase("grid edit mode", { it.copy(isGridEditMode = true) }) { !it.isGridEditMode },
        )

        cases.forEach { case ->
            viewModel.updateUiState { case.open(MainUiState()) }

            handleBackNavigation(viewModel.uiState.value, viewModel)

            assertTrue("back should close the ${case.name}", case.isClosed(viewModel.uiState.value))
        }
    }

    @Test
    fun `when release notes and the drawer are both open, should close release notes first`() {
        viewModel.updateUiState { it.copy(showReleaseNotesBottomSheet = true, isAllAppsDrawerOpen = true) }

        handleBackNavigation(viewModel.uiState.value, viewModel)

        assertEquals(false, viewModel.uiState.value.showReleaseNotesBottomSheet)
        assertEquals(true, viewModel.uiState.value.isAllAppsDrawerOpen)
    }

    @Test
    fun `when nothing is open, should not open anything`() {
        viewModel.updateUiState { MainUiState() }

        handleBackNavigation(viewModel.uiState.value, viewModel)

        assertFalse(viewModel.uiState.value.isOverlayActive)
    }

    @Test
    fun `when the onboarding handles back, should step back without touching the home`() {
        viewModel.updateUiState { MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.DEFAULT_HOME)) }

        handleBackNavigation(viewModel.uiState.value, viewModel)

        assertEquals(OnboardingStage.WELCOME, viewModel.uiState.value.onboarding.stage)
    }

    @Test
    fun `when an editor is open over the checklist, should close the editor first`() {
        viewModel.updateUiState {
            MainUiState(
                showFavoritePickerDialog = true,
                onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = true)
            )
        }

        handleBackNavigation(viewModel.uiState.value, viewModel)

        assertFalse(viewModel.uiState.value.showFavoritePickerDialog)
        assertTrue(viewModel.uiState.value.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the onboarding does not handle back, should fall through to the home`() {
        viewModel.updateUiState {
            MainUiState(isAllAppsDrawerOpen = true, onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES))
        }

        handleBackNavigation(viewModel.uiState.value, viewModel)

        assertFalse(viewModel.uiState.value.isAllAppsDrawerOpen)
    }
}
