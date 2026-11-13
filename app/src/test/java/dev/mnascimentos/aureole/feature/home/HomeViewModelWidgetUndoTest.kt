package dev.mnascimentos.aureole.feature.home

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.removeWidgetWithUndo
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.reorderWidgetsWithUndo
import dev.mnascimentos.aureole.feature.home.extensions.onUndo
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** Removing and reordering widgets of a stack offer "Undo", which puts the previous list back. */
@RunWith(RobolectricTestRunner::class)
class HomeViewModelWidgetUndoTest {

    private lateinit var context: Context
    private lateinit var widgetRepository: WidgetRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        widgetRepository = WidgetRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context),
            )
        )
        viewModel.updateUiState { it.copy(widgetStacks = it.widgetStacks + (STACK_ID to listOf(1, 2, 3))) }
    }

    @After
    fun tearDown() {
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        widgetRepository.saveWidgetIdsForStack(STACK_ID, emptyList())
        Dispatchers.resetMain()
    }

    @Test
    fun `removing a widget offers undo that restores the stack in the same order`() {
        viewModel.removeWidgetWithUndo(2, STACK_ID)

        assertEquals(listOf(1, 3), stack())
        assertEquals(listOf(1, 3), widgetRepository.getWidgetIdsForStack(STACK_ID))
        assertEquals(
            TextResource.fromResId(R.string.home_undo_widget_removed),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()

        assertEquals(listOf(1, 2, 3), stack())
        assertEquals(listOf(1, 2, 3), widgetRepository.getWidgetIdsForStack(STACK_ID))
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing a widget that is not in the stack offers no undo`() {
        viewModel.removeWidgetWithUndo(9, STACK_ID)

        assertEquals(listOf(1, 2, 3), stack())
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `reordering offers undo that restores the previous order`() {
        viewModel.reorderWidgetsWithUndo(listOf(3, 1, 2), STACK_ID)

        assertEquals(listOf(3, 1, 2), stack())
        assertEquals(
            TextResource.fromResId(R.string.home_undo_widget_order_changed),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()

        assertEquals(listOf(1, 2, 3), stack())
        assertEquals(listOf(1, 2, 3), widgetRepository.getWidgetIdsForStack(STACK_ID))
    }

    @Test
    fun `an unchanged order offers no undo`() {
        viewModel.reorderWidgetsWithUndo(listOf(1, 2, 3), STACK_ID)

        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `the top stack also updates the top widget ids`() {
        viewModel.updateUiState { it.copy(widgetStacks = it.widgetStacks + (TOP_STACK to listOf(5, 6))) }

        viewModel.reorderWidgetsWithUndo(listOf(6, 5), TOP_STACK)
        assertEquals(listOf(6, 5), viewModel.uiState.value.topWidgetIds)

        viewModel.onUndo()
        assertEquals(listOf(5, 6), viewModel.uiState.value.topWidgetIds)
        widgetRepository.saveWidgetIdsForStack(TOP_STACK, emptyList())
    }

    private fun stack() = viewModel.uiState.value.widgetStacks[STACK_ID]

    private companion object {
        const val STACK_ID = "undo_test_stack"
        const val TOP_STACK = "top_widgets"
    }
}
