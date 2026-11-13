package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTaskEvidenceTest {

    private val appsList = LauncherItemState(id = "list", type = LauncherItemType.APPS_LIST)
    private val container = LauncherItemState(id = "c", type = LauncherItemType.SHORTCUTS_CONTAINER)
    private val widgetList = LauncherItemState(id = "stack", type = LauncherItemType.WIDGET_LIST)

    @Test
    fun `when mapping tasks to grid items, should cover only the grid tasks`() {
        assertEquals(LauncherItemType.CLOCK, OnboardingTask.ADD_CLOCK.gridItemType)
        assertEquals(LauncherItemType.APPS_LIST, OnboardingTask.ADD_FAVORITE_APPS.gridItemType)
        assertEquals(LauncherItemType.SHORTCUTS_CONTAINER, OnboardingTask.FILL_CONTAINER.gridItemType)
        assertEquals(LauncherItemType.WIDGET_LIST, OnboardingTask.ADD_WIDGET.gridItemType)
        assertNull(OnboardingTask.CHOOSE_FONT.gridItemType)
    }

    @Test
    fun `when an editor or the edit mode is open, should count the task as in progress`() {
        val states = listOf(
            MainUiState(isGridEditMode = true),
            MainUiState(showEditClockBottomSheet = true),
            MainUiState(showFavoritePickerDialog = true),
            MainUiState(isEditContainerDialogVisible = true),
            MainUiState(showWidgetPicker = true),
            MainUiState(pendingWidgetId = 3)
        )

        states.forEach { assertTrue(it.isTaskInProgress) }
        assertFalse(MainUiState().isTaskInProgress)
        assertFalse(MainUiState(isGridEditMode = true).isTaskEditorOpen)
    }

    @Test
    fun `when a clock is on the grid, should meet the clock task`() {
        val clock = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK)

        assertTrue(MainUiState(gridItems = listOf(clock)).meetsRequirement(OnboardingTask.ADD_CLOCK))
        assertFalse(MainUiState().meetsRequirement(OnboardingTask.ADD_CLOCK))
    }

    @Test
    fun `when the home has new clocks and not the default one, should still meet the clock task`() {
        val first = LauncherItemState(id = "0b6f6c1e-uuid-a", type = LauncherItemType.CLOCK)
        val second = LauncherItemState(id = "0b6f6c1e-uuid-b", type = LauncherItemType.CLOCK)

        assertTrue(MainUiState(gridItems = listOf(first, second)).meetsRequirement(OnboardingTask.ADD_CLOCK))
        assertTrue(MainUiState(gridItems = listOf(second, appsList)).meetsRequirement(OnboardingTask.ADD_CLOCK))
        assertFalse(MainUiState(gridItems = listOf(appsList)).meetsRequirement(OnboardingTask.ADD_CLOCK))
    }

    @Test
    fun `when the apps list has its own or global favorites, should meet the favorites task`() {
        val own = MainUiState(gridItems = listOf(appsList), containerFavorites = mapOf("list" to listOf("a")))
        val legacyList = LauncherItemState(id = GridDefaults.APPS_LIST_ITEM_ID, type = LauncherItemType.APPS_LIST)
        val global = MainUiState(gridItems = listOf(legacyList), favoriteAppPackages = listOf("a"))
        val newListNoInherit = MainUiState(gridItems = listOf(appsList), favoriteAppPackages = listOf("a"))
        val emptyOwn = MainUiState(
            gridItems = listOf(appsList),
            favoriteAppPackages = listOf("a"),
            containerFavorites = mapOf("list" to emptyList())
        )
        val noList = MainUiState(favoriteAppPackages = listOf("a"))

        assertTrue(own.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
        assertTrue(global.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
        assertFalse(newListNoInherit.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
        assertFalse(emptyOwn.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
        assertFalse(noList.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
    }

    @Test
    fun `when an apps list inside a scroll group has favorites, should meet the favorites task`() {
        val group = LauncherItemState(id = "group", type = LauncherItemType.SCROLL_VIEW, children = listOf(appsList))
        val withFavorites = MainUiState(gridItems = listOf(group), containerFavorites = mapOf("list" to listOf("a")))
        val withoutFavorites = MainUiState(gridItems = listOf(group))

        assertTrue(withFavorites.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
        assertFalse(withoutFavorites.meetsRequirement(OnboardingTask.ADD_FAVORITE_APPS))
    }

    @Test
    fun `when the container editor saved, should meet the container task even empty, else not`() {
        val saved = MainUiState(
            gridItems = listOf(container),
            onboarding = OnboardingUiState(pendingTask = OnboardingTask.FILL_CONTAINER, isPendingTaskSaved = true)
        )
        val unsaved = saved.copy(onboarding = saved.onboarding.copy(isPendingTaskSaved = false))

        assertTrue(saved.meetsRequirement(OnboardingTask.FILL_CONTAINER))
        assertFalse(unsaved.meetsRequirement(OnboardingTask.FILL_CONTAINER))
    }

    @Test
    fun `when the widget list or a bound widget holds a widget, should meet the widget task`() {
        val single = LauncherItemState(id = "w", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = 7)
        val nested = LauncherItemState(id = "s", type = LauncherItemType.SCROLL_VIEW, children = listOf(single))
        val states = listOf(
            MainUiState(gridItems = listOf(widgetList), widgetStacks = mapOf("stack" to listOf(2))),
            MainUiState(gridItems = listOf(single)),
            MainUiState(gridItems = listOf(nested))
        )

        states.forEach { assertTrue(it.meetsRequirement(OnboardingTask.ADD_WIDGET)) }
    }

    @Test
    fun `when the widget list is empty or a widget is unbound, should not meet the widget task`() {
        val unbound = LauncherItemState(id = "u", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = -1)
        val state = MainUiState(gridItems = listOf(widgetList, unbound), widgetStacks = mapOf("stack" to emptyList()))

        assertFalse(state.meetsRequirement(OnboardingTask.ADD_WIDGET))
        assertFalse(state.meetsRequirement(OnboardingTask.CHOOSE_FONT))
    }
}
