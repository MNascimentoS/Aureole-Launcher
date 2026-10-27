package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingReducerTest {

    private fun stateAt(
        stage: OnboardingStage,
        onboarding: OnboardingUiState = OnboardingUiState(stage = stage)
    ) = MainUiState(onboarding = onboarding.copy(stage = stage))

    private fun MainUiState.withOnboarding(transform: OnboardingUiState.() -> OnboardingUiState) =
        copy(onboarding = onboarding.transform())

    private val allTasksButSettings = OnboardingTask.entries.toSet() - OnboardingTask.EXPLORE_SETTINGS

    // region reduce

    @Test
    fun `when the stage is not gestures or checklist, should return next untouched`() {
        listOf(OnboardingStage.COMPLETED, OnboardingStage.WELCOME, OnboardingStage.DEFAULT_HOME).forEach { stage ->
            val previous = stateAt(stage)
            val next = previous.copy(isAllAppsDrawerOpen = true, favoriteAppPackages = listOf("a"))

            assertSame(next, OnboardingReducer.reduce(previous, next))
        }
    }

    @Test
    fun `when nothing related to the onboarding changes, should return next untouched`() {
        val gestures = stateAt(OnboardingStage.GESTURES)
        val checklist = stateAt(OnboardingStage.CHECKLIST)

        val nextGestures = gestures.copy(searchQuery = "a")
        val nextChecklist = checklist.copy(searchQuery = "a")

        assertSame(nextGestures, OnboardingReducer.reduce(gestures, nextGestures))
        assertSame(nextChecklist, OnboardingReducer.reduce(checklist, nextChecklist))
    }

    // endregion

    // region gestures

    @Test
    fun `when the drawer opens, should complete open apps and show success`() {
        val previous = stateAt(OnboardingStage.GESTURES)

        val result = OnboardingReducer.reduce(previous, previous.copy(isAllAppsDrawerOpen = true)).onboarding

        assertEquals(setOf(GestureCoachStep.OPEN_APPS), result.completedSteps)
        assertEquals(GestureCoachStep.OPEN_APPS, result.succeededStep)
        assertEquals(OnboardingStage.GESTURES, result.stage)
    }

    @Test
    fun `when customize opens before other steps, should complete it out of order`() {
        val previous = stateAt(OnboardingStage.GESTURES)

        val result = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = true)).onboarding

        assertEquals(setOf(GestureCoachStep.CUSTOMIZE), result.completedSteps)
        assertEquals(GestureCoachStep.CUSTOMIZE, result.succeededStep)
    }

    @Test
    fun `when a surface opens for an already completed step, should not show success again`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS))
        )
        val next = previous.copy(isAllAppsDrawerOpen = true)

        assertSame(next, OnboardingReducer.reduce(previous, next))
    }

    @Test
    fun `when the surface closes after a success, should advance to the next step`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS),
                succeededStep = GestureCoachStep.OPEN_APPS
            )
        ).copy(isAllAppsDrawerOpen = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(isAllAppsDrawerOpen = false)).onboarding

        assertNull(result.succeededStep)
        assertEquals(OnboardingStage.GESTURES, result.stage)
    }

    @Test
    fun `when customize closes after its success, should clear the success`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.CUSTOMIZE),
                succeededStep = GestureCoachStep.CUSTOMIZE
            )
        ).copy(showCustomizeBottomSheet = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = false)).onboarding

        assertNull(result.succeededStep)
    }

    @Test
    fun `when a surface closes without a success, should return next untouched`() {
        val previous = stateAt(OnboardingStage.GESTURES).copy(isAllAppsDrawerOpen = true)
        val next = previous.copy(isAllAppsDrawerOpen = false)

        assertSame(next, OnboardingReducer.reduce(previous, next))
    }

    @Test
    fun `when the last step succeeds, should wait for the surface to close before the checklist`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB))
        )

        val opened = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = true))
        val closed = OnboardingReducer.reduce(opened, opened.copy(showCustomizeBottomSheet = false))

        assertEquals(OnboardingStage.GESTURES, opened.onboarding.stage)
        assertEquals(OnboardingStage.CHECKLIST, closed.onboarding.stage)
        assertTrue(closed.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the alphabet scrubber is disabled, should finish without the scrub step`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.CUSTOMIZE),
                succeededStep = GestureCoachStep.CUSTOMIZE
            )
        ).copy(showCustomizeBottomSheet = true, isAlphabetScrubberDisabled = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = false))

        assertEquals(OnboardingStage.CHECKLIST, result.onboarding.stage)
    }

    @Test
    fun `when the scrub step is missing and the scrubber is enabled, should keep teaching`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.CUSTOMIZE),
                succeededStep = GestureCoachStep.CUSTOMIZE
            )
        ).copy(showCustomizeBottomSheet = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = false))

        assertEquals(OnboardingStage.GESTURES, result.onboarding.stage)
    }

    @Test
    fun `when gestures finish, should show tasks already done as completed`() {
        val previous = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(
                completedSteps = GestureCoachStep.entries.toSet(),
                succeededStep = GestureCoachStep.CUSTOMIZE,
                isDefaultLauncher = true
            )
        ).copy(showCustomizeBottomSheet = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(showCustomizeBottomSheet = false))

        assertEquals(OnboardingStage.CHECKLIST, result.onboarding.stage)
        assertEquals(setOf(OnboardingTask.SET_DEFAULT_HOME), result.onboarding.completedTasks)
    }

    // endregion

    // region derived tasks

    @Test
    fun `when nothing is set up, should derive no tasks`() {
        assertTrue(OnboardingReducer.deriveCompletedTasks(MainUiState()).isEmpty())
    }

    @Test
    fun `when aureole is the default launcher, should derive the default home task`() {
        val state = MainUiState().withOnboarding { copy(isDefaultLauncher = true) }

        assertEquals(setOf(OnboardingTask.SET_DEFAULT_HOME), OnboardingReducer.deriveCompletedTasks(state))
    }

    @Test
    fun `when there are global or container favorites, should derive the favorites task`() {
        val global = MainUiState(favoriteAppPackages = listOf("a"))
        val container = MainUiState(containerFavorites = mapOf("list" to listOf("a")))
        val emptyContainer = MainUiState(containerFavorites = mapOf("list" to emptyList()))

        assertEquals(setOf(OnboardingTask.ADD_FAVORITE_APPS), OnboardingReducer.deriveCompletedTasks(global))
        assertEquals(setOf(OnboardingTask.ADD_FAVORITE_APPS), OnboardingReducer.deriveCompletedTasks(container))
        assertTrue(OnboardingReducer.deriveCompletedTasks(emptyContainer).isEmpty())
    }

    @Test
    fun `when a container has items or folders, should derive the container task`() {
        val item = ContainerItemEntity(id = "i", panelId = "c", itemType = ContainerItemType.APP, packageName = "a")
        val withItem = MainUiState(containers = mapOf("c" to ContainerModel(id = "c", items = listOf(item))))
        val withFolder = MainUiState(
            containers = mapOf("c" to ContainerModel(id = "c", folders = listOf(AppFolder(name = "f"))))
        )
        val empty = MainUiState(containers = mapOf("c" to ContainerModel(id = "c")))

        assertEquals(setOf(OnboardingTask.FILL_CONTAINER), OnboardingReducer.deriveCompletedTasks(withItem))
        assertEquals(setOf(OnboardingTask.FILL_CONTAINER), OnboardingReducer.deriveCompletedTasks(withFolder))
        assertTrue(OnboardingReducer.deriveCompletedTasks(empty).isEmpty())
    }

    @Test
    fun `when a widget is bound anywhere, should derive the widget task`() {
        val widget = LauncherItemState(id = "w", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = 7)
        val nested = LauncherItemState(id = "s", type = LauncherItemType.SCROLL_VIEW, children = listOf(widget))
        val states = listOf(
            MainUiState(topWidgetIds = listOf(1)),
            MainUiState(widgetStacks = mapOf("stack" to listOf(2))),
            MainUiState(gridItems = listOf(widget)),
            MainUiState(gridItems = listOf(nested))
        )

        states.forEach { assertEquals(setOf(OnboardingTask.ADD_WIDGET), OnboardingReducer.deriveCompletedTasks(it)) }
    }

    @Test
    fun `when a widget is pending or the item is not a widget, should not derive the widget task`() {
        val pending = LauncherItemState(id = "w", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = -1)
        val unbound = LauncherItemState(id = "u", type = LauncherItemType.SINGLE_APP_WIDGET)
        val clock = LauncherItemState(id = "c", type = LauncherItemType.CLOCK, widgetId = 7)
        val state = MainUiState(
            gridItems = listOf(pending, unbound, clock),
            widgetStacks = mapOf("stack" to emptyList())
        )

        assertTrue(OnboardingReducer.deriveCompletedTasks(state).isEmpty())
    }

    // endregion

    // region checklist

    @Test
    fun `when a task becomes done, should mark it and reopen the list`() {
        val previous = stateAt(OnboardingStage.CHECKLIST, OnboardingUiState(isChecklistExpanded = false))

        val result = OnboardingReducer.reduce(previous, previous.copy(favoriteAppPackages = listOf("a"))).onboarding

        assertEquals(setOf(OnboardingTask.ADD_FAVORITE_APPS), result.completedTasks)
        assertTrue(result.isChecklistExpanded)
    }

    @Test
    fun `when a derived task is undone, should keep it completed`() {
        val previous = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(completedTasks = setOf(OnboardingTask.ADD_FAVORITE_APPS), isChecklistExpanded = false)
        ).copy(favoriteAppPackages = listOf("a"))
        val next = previous.copy(favoriteAppPackages = emptyList())

        val result = OnboardingReducer.reduce(previous, next)

        assertSame(next, result)
        assertFalse(result.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the last task is done, should complete and celebrate`() {
        val previous = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(completedTasks = allTasksButSettings)
        )
        val next = OnboardingReducer.applySignal(previous, OnboardingSignal.SETTINGS_OPENED)

        val result = OnboardingReducer.reduce(previous, next).onboarding

        assertEquals(OnboardingStage.COMPLETED, result.stage)
        assertEquals(OnboardingTask.entries.toSet(), result.completedTasks)
        assertTrue(result.isCelebrating)
        assertNull(result.activePicker)
    }

    // endregion

    // region signals

    @Test
    fun `when scrubbed during gestures, should complete the scrub step`() {
        val result = OnboardingReducer.applySignal(
            stateAt(OnboardingStage.GESTURES),
            OnboardingSignal.ALPHABET_SCRUBBED
        ).onboarding

        assertEquals(setOf(GestureCoachStep.ALPHABET_SCRUB), result.completedSteps)
        assertEquals(GestureCoachStep.ALPHABET_SCRUB, result.succeededStep)
    }

    @Test
    fun `when scrubbed again, should keep the state`() {
        val state = stateAt(
            OnboardingStage.GESTURES,
            OnboardingUiState(completedSteps = setOf(GestureCoachStep.ALPHABET_SCRUB))
        )

        assertSame(state, OnboardingReducer.applySignal(state, OnboardingSignal.ALPHABET_SCRUBBED))
    }

    @Test
    fun `when the palette picker is confirmed, should complete the palette task and close the picker`() {
        val state = stateAt(OnboardingStage.CHECKLIST, OnboardingUiState(activePicker = AppearancePicker.PALETTE))

        val result = OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED).onboarding

        assertEquals(setOf(OnboardingTask.CHOOSE_PALETTE), result.completedTasks)
        assertNull(result.activePicker)
    }

    @Test
    fun `when the font picker is confirmed, should complete the font task`() {
        val state = stateAt(OnboardingStage.CHECKLIST, OnboardingUiState(activePicker = AppearancePicker.FONT))

        val result = OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED).onboarding

        assertEquals(setOf(OnboardingTask.CHOOSE_FONT), result.completedTasks)
    }

    @Test
    fun `when confirmed without an open picker, should keep the state`() {
        val state = stateAt(OnboardingStage.CHECKLIST)

        assertSame(state, OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED))
    }

    @Test
    fun `when settings open during the checklist, should complete the settings task`() {
        val result = OnboardingReducer.applySignal(
            stateAt(OnboardingStage.CHECKLIST),
            OnboardingSignal.SETTINGS_OPENED
        ).onboarding

        assertEquals(setOf(OnboardingTask.EXPLORE_SETTINGS), result.completedTasks)
    }

    @Test
    fun `when the default home result arrives, should remember the attempt`() {
        listOf(OnboardingStage.DEFAULT_HOME, OnboardingStage.CHECKLIST).forEach { stage ->
            val result = OnboardingReducer.applySignal(stateAt(stage), OnboardingSignal.DEFAULT_HOME_RESULT)

            assertTrue(result.onboarding.defaultHomeAttempted)
        }
    }

    @Test
    fun `when a signal arrives in the wrong stage, should ignore it`() {
        val wrongStages = mapOf(
            OnboardingSignal.ALPHABET_SCRUBBED to OnboardingStage.CHECKLIST,
            OnboardingSignal.PICKER_CONFIRMED to OnboardingStage.GESTURES,
            OnboardingSignal.SETTINGS_OPENED to OnboardingStage.COMPLETED,
            OnboardingSignal.DEFAULT_HOME_RESULT to OnboardingStage.WELCOME
        )

        wrongStages.forEach { (signal, stage) ->
            val state = stateAt(stage, OnboardingUiState(activePicker = AppearancePicker.PALETTE))

            assertSame(state, OnboardingReducer.applySignal(state, signal))
        }
    }

    // endregion
}
