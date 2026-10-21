package dev.mnascimentos.aureole.feature.home.components.widgets

import android.content.Context
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
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

    private fun setAllGroups(groups: List<WidgetAppGroup>) {
        val field = WidgetPickerViewModel::class.java.getDeclaredField("allGroups")
        field.isAccessible = true
        field[viewModel] = groups
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
        assertTrue(state.isLoading)
    }

    @Test
    fun `when search query matches app name, should return full group`() {
        val groups = listOf(
            WidgetAppGroup(
                appId = "calendar_app",
                appName = "Calendar",
                availableWidgets = listOf(
                    WidgetVariant(widgetId = "w1", title = "Month View"),
                    WidgetVariant(widgetId = "w2", title = "Agenda View"),
                ),
            ),
            WidgetAppGroup(
                appId = "notes_app",
                appName = "Notes",
                availableWidgets = listOf(WidgetVariant(widgetId = "w3", title = "Quick Note")),
            ),
        )
        setAllGroups(groups)

        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged("cal"))

        val state = viewModel.state.value
        assertEquals(1, state.widgetGroups.size)
        assertEquals("calendar_app", state.widgetGroups[0].appId)
        assertEquals(2, state.widgetGroups[0].availableWidgets.size)
    }

    @Test
    fun `when search query matches widget title but not app name, should return group with filtered widgets`() {
        val groups = listOf(
            WidgetAppGroup(
                appId = "notes_app",
                appName = "Notes",
                availableWidgets = listOf(
                    WidgetVariant(widgetId = "w1", title = "Calendar Widget"),
                    WidgetVariant(widgetId = "w2", title = "Todo List"),
                ),
            )
        )
        setAllGroups(groups)

        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged("calendar"))

        val state = viewModel.state.value
        assertEquals(1, state.widgetGroups.size)
        assertEquals(1, state.widgetGroups[0].availableWidgets.size)
        assertEquals("Calendar Widget", state.widgetGroups[0].availableWidgets[0].title)
    }

    @Test
    fun `when search query matches neither app name nor widget title, should return empty list`() {
        val groups = listOf(
            WidgetAppGroup(
                appId = "notes_app",
                appName = "Notes",
                availableWidgets = listOf(WidgetVariant(widgetId = "w1", title = "Todo List"))
            )
        )
        setAllGroups(groups)

        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged("xyz"))

        val state = viewModel.state.value
        assertTrue(state.widgetGroups.isEmpty())
    }

    @Test
    fun `when search query is blank, should return all groups`() {
        val groups = listOf(
            WidgetAppGroup(
                appId = "notes_app",
                appName = "Notes",
                availableWidgets = listOf(WidgetVariant(widgetId = "w1", title = "Todo List"))
            )
        )
        setAllGroups(groups)

        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged("xyz"))
        assertEquals(0, viewModel.state.value.widgetGroups.size)

        viewModel.processIntent(WidgetSelectorIntent.SearchQueryChanged(""))
        assertEquals(1, viewModel.state.value.widgetGroups.size)
    }
}
