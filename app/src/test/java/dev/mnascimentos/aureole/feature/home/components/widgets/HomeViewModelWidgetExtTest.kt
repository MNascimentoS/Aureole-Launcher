package dev.mnascimentos.aureole.feature.home.components.widgets

import android.content.Context
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.loadWidgetSettings
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.reorderWidgets
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setPendingWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setTargetWidgetStackId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack
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
class HomeViewModelWidgetExtTest {

    private lateinit var context: Context
    private lateinit var widgetRepository: WidgetRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        widgetRepository = WidgetRepository(context)
        val settingsRepository = SettingsRepository(context)

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
    }

    @Test
    fun `when loadWidgetSettings is called, should populate uiState with repository values`() {
        widgetRepository.saveWidgetIdsForStack("top_widgets", listOf(10, 20))
        widgetRepository.saveWidgetRowHeight(180f)
        widgetRepository.setWidgetDotsState("top_widgets", showDots = false)

        viewModel.loadWidgetSettings()

        val state = viewModel.uiState.value
        assertEquals(listOf(10, 20), state.topWidgetIds)
        assertEquals(180.dp, state.widgetRowHeight)
        assertEquals(false, state.widgetStackDots["top_widgets"])
    }

    @Test
    fun `when setWidgetRowHeight is called, should update state and save to repository`() {
        viewModel.setWidgetRowHeight(200.dp)

        assertEquals(200.dp, viewModel.uiState.value.widgetRowHeight)
        assertEquals(200f, widgetRepository.getSavedWidgetRowHeight(), 0.01f)
    }

    @Test
    fun `when setPendingWidgetId is called, should update state`() {
        viewModel.setPendingWidgetId(42)

        assertEquals(42, viewModel.uiState.value.pendingWidgetId)
    }

    @Test
    fun `when setIsAddingSingleWidget is called, should update state`() {
        viewModel.setIsAddingSingleWidget(true)

        assertTrue(viewModel.uiState.value.isAddingSingleWidget)
    }

    @Test
    fun `when setTargetWidgetStackId is called, should update state`() {
        viewModel.setTargetWidgetStackId("custom_stack")

        assertEquals("custom_stack", viewModel.uiState.value.targetWidgetStackId)
    }

    @Test
    fun `when addWidgetId is called with target stack, should add widget to stack and repository`() {
        val stackId = "custom_stack_1"
        viewModel.setTargetWidgetStackId(stackId)

        viewModel.addWidgetId(101)

        val state = viewModel.uiState.value
        assertEquals(listOf(101), state.getWidgetIdsForStack(stackId))
        assertEquals(listOf(101), widgetRepository.getWidgetIdsForStack(stackId))
        assertNull(state.targetWidgetStackId)
        assertFalse(state.isAddingSingleWidget)
    }

    @Test
    fun `when addWidgetId is called for top_widgets, should update topWidgetIds`() {
        viewModel.addWidgetId(1, targetStackId = "top_widgets")
        viewModel.addWidgetId(2, targetStackId = "top_widgets")

        val state = viewModel.uiState.value
        assertEquals(listOf(1, 2), state.topWidgetIds)
        assertEquals(listOf(1, 2), widgetRepository.getWidgetIdsForStack("top_widgets"))
    }

    @Test
    fun `when addWidgetId is called with duplicate id, should not add duplicate`() {
        viewModel.addWidgetId(1, targetStackId = "top_widgets")
        viewModel.addWidgetId(1, targetStackId = "top_widgets")

        val state = viewModel.uiState.value
        assertEquals(listOf(1), state.topWidgetIds)
    }

    @Test
    fun `when removeWidgetId is called, should remove widget from stack and repository`() {
        viewModel.addWidgetId(1, targetStackId = "top_widgets")
        viewModel.addWidgetId(2, targetStackId = "top_widgets")

        viewModel.removeWidgetId(1, targetStackId = "top_widgets")

        val state = viewModel.uiState.value
        assertEquals(listOf(2), state.topWidgetIds)
        assertEquals(listOf(2), widgetRepository.getWidgetIdsForStack("top_widgets"))
    }

    @Test
    fun `when reorderWidgets is called, should update stack with new list order`() {
        viewModel.addWidgetId(1, targetStackId = "top_widgets")
        viewModel.addWidgetId(2, targetStackId = "top_widgets")

        viewModel.reorderWidgets(listOf(2, 1), targetStackId = "top_widgets")

        val state = viewModel.uiState.value
        assertEquals(listOf(2, 1), state.topWidgetIds)
        assertEquals(listOf(2, 1), widgetRepository.getWidgetIdsForStack("top_widgets"))
    }

    @Test
    fun `when openWidgetPopup and closeWidgetPopup are called, should update state correctly`() {
        viewModel.openWidgetPopup(widgetId = 55, topYPx = 150f)

        var state = viewModel.uiState.value
        assertEquals(55, state.activeWidgetId)
        assertEquals(150f, state.activeWidgetTopYPx)
        assertTrue(state.showWidgetPopup)

        viewModel.closeWidgetPopup()

        state = viewModel.uiState.value
        assertNull(state.activeWidgetId)
        assertEquals(0f, state.activeWidgetTopYPx)
        assertFalse(state.showWidgetPopup)
        assertFalse(state.showWidgetResizeDialog)
    }

    @Test
    fun `when setShowWidgetResizeDialog is called, should update state`() {
        viewModel.setShowWidgetResizeDialog(true)

        val state = viewModel.uiState.value
        assertTrue(state.showWidgetResizeDialog)
        assertFalse(state.showWidgetPopup)
    }

    @Test
    fun `when setShowWidgetPicker is called, should update state`() {
        viewModel.setShowWidgetPicker(true)

        assertTrue(viewModel.uiState.value.showWidgetPicker)
    }
}
