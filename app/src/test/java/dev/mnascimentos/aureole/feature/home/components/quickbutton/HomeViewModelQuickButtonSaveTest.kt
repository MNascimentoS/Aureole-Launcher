package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenQuickButtonSettings
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelQuickButtonSaveTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var gridRepository: GridRepository

    private val nestedButton = LauncherItemState(id = "qb", type = LauncherItemType.QUICK_BUTTON)
    private val scroll =
        LauncherItemState(id = "scroll", type = LauncherItemType.SCROLL_VIEW, children = listOf(nestedButton))
    private val withoutChildren = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, children = null)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context: Context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        gridRepository = GridRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = gridRepository
            )
        )
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    private fun savedButton(isLandscape: Boolean) =
        gridRepository.getGridItems(isLandscape).first { it.id == "scroll" }.safeChildren.first { it.id == "qb" }

    /** The quick button part of the state; the init's settings load may still touch unrelated fields. */
    private fun MainUiState.gridState() = listOf(
        gridItems,
        portraitGridItems,
        landscapeGridItems,
        activeQuickButtonId,
        activeQuickButtonModel
    )

    @Test
    fun `when opening settings of a nested button without a model, should start from a default model`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(scroll)) }

        viewModel.onOpenQuickButtonSettings("qb")

        assertEquals("qb", viewModel.uiState.value.activeQuickButtonId)
        assertEquals(QuickButtonModel(id = "qb"), viewModel.uiState.value.activeQuickButtonModel)
    }

    @Test
    fun `when changing shortcuts of a nested button in landscape, should save both orientations`() {
        viewModel.updateUiState {
            MainUiState(
                isLandscape = true,
                gridItems = listOf(scroll, withoutChildren),
                portraitGridItems = listOf(scroll, withoutChildren),
                landscapeGridItems = listOf(scroll),
                activeQuickButtonId = "qb",
                activeQuickButtonModel = QuickButtonModel(id = "qb")
            )
        }
        val shortcuts = QuickButtonShortcuts(click = ShortcutAction.Settings)

        viewModel.onUpdateQuickButtonShortcuts(shortcuts)

        assertEquals(shortcuts, savedButton(isLandscape = true).quickButtonModel?.shortcuts)
        assertEquals(shortcuts, savedButton(isLandscape = false).quickButtonModel?.shortcuts)
        assertEquals(viewModel.uiState.value.landscapeGridItems, viewModel.uiState.value.gridItems)
    }

    @Test
    fun `when changing the appearance in portrait, should update the visible grid`() {
        viewModel.updateUiState {
            MainUiState(
                portraitGridItems = listOf(scroll),
                activeQuickButtonId = "qb",
                activeQuickButtonModel = QuickButtonModel(id = "qb")
            )
        }
        val appearance = QuickButtonAppearance(strokeEnabled = true)

        viewModel.onUpdateQuickButtonAppearance(appearance)

        val shown = viewModel.uiState.value.gridItems.first { it.id == "scroll" }.safeChildren.single()
        assertEquals(appearance, shown.quickButtonModel?.appearance)
    }

    @Test
    fun `when nothing is being edited, should not change or save anything`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(scroll)) }
        val before = viewModel.uiState.value

        viewModel.onUpdateQuickButtonShortcuts(QuickButtonShortcuts())
        viewModel.onUpdateQuickButtonAppearance(QuickButtonAppearance())

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when a model is set but no button id, should keep the model without saving`() {
        viewModel.updateUiState { MainUiState(activeQuickButtonModel = QuickButtonModel(id = "orphan")) }

        viewModel.onUpdateQuickButtonShortcuts(QuickButtonShortcuts(click = ShortcutAction.Notifications))

        assertEquals(ShortcutAction.Notifications, viewModel.uiState.value.activeQuickButtonModel?.shortcuts?.click)
    }
}
