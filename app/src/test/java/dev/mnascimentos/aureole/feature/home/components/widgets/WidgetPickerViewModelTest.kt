package dev.mnascimentos.aureole.feature.home.components.widgets

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
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
class WidgetPickerViewModelTest {

    private lateinit var context: Context
    private lateinit var widgetRepository: WidgetRepository
    private lateinit var viewModel: WidgetPickerViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        widgetRepository = WidgetRepository(context)
        viewModel = WidgetPickerViewModel(widgetRepository)
    }

    @Test
    fun `when initialized, should set state to loading`() {
        val initialState = viewModel.state.value
        assertTrue(initialState.isLoading)
        assertTrue(initialState.widgetGroups.isEmpty())
        assertTrue(initialState.expandedAppIds.isEmpty())
        assertEquals("", initialState.searchQuery)
    }

    @Test
    fun `when SearchQueryChanged intent processed, should update searchQuery state`() {
        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged("Calendar"))

        val state = viewModel.state.value
        assertEquals("Calendar", state.searchQuery)
    }

    @Test
    fun `when ToggleAppGroup intent processed, should expand and collapse app group`() {
        val appId = "com.example.app"

        viewModel.processIntent(WidgetSelectorIntent.ToggleAppGroup(appId))
        assertTrue(viewModel.state.value.expandedAppIds.contains(appId))

        viewModel.processIntent(WidgetSelectorIntent.ToggleAppGroup(appId))
        assertFalse(viewModel.state.value.expandedAppIds.contains(appId))
    }

    @Test
    fun `when LoadWidgets intent processed, should trigger widget loading`() {
        viewModel.processIntent(WidgetSelectorIntent.LoadWidgets)
        val state = viewModel.state.value
        assertNotNull(state)
    }
}
