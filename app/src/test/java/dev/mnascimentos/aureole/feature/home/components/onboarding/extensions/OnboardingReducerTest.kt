package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

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

    private val allTasksButFont = OnboardingTask.entries.toSet() - OnboardingTask.CHOOSE_FONT

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
    fun `when favorites, containers or widgets change, should not derive their tasks`() {
        val item = ContainerItemEntity(id = "i", panelId = "c", itemType = ContainerItemType.APP, packageName = "a")
        val state = MainUiState(
            favoriteAppPackages = listOf("a"),
            containers = mapOf("c" to ContainerModel(id = "c", items = listOf(item))),
            widgetStacks = mapOf("stack" to listOf(2)),
            gridItems = listOf(LauncherItemState(id = "w", type = LauncherItemType.SINGLE_APP_WIDGET, widgetId = 7))
        )

        assertTrue(OnboardingReducer.deriveCompletedTasks(state).isEmpty())
    }

    @Test
    fun `when a favorite is marked with the picker open, should not complete or advance the list`() {
        val previous = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(isChecklistExpanded = false, pendingTask = OnboardingTask.ADD_FAVORITE_APPS)
        ).copy(showFavoritePickerDialog = true, isGridEditMode = true)

        val result = OnboardingReducer.reduce(previous, previous.copy(favoriteAppPackages = listOf("a")))

        assertTrue(result.onboarding.completedTasks.isEmpty())
        assertFalse(result.onboarding.isChecklistExpanded)
    }

    // endregion

    // region checklist

    @Test
    fun `when a task becomes done, should mark it and reopen the list`() {
        val previous = stateAt(OnboardingStage.CHECKLIST, OnboardingUiState(isChecklistExpanded = false))

        val next = previous.withOnboarding { copy(isDefaultLauncher = true) }

        val result = OnboardingReducer.reduce(previous, next).onboarding

        assertEquals(setOf(OnboardingTask.SET_DEFAULT_HOME), result.completedTasks)
        assertTrue(result.isChecklistExpanded)
    }

    @Test
    fun `when tasks arrive with the checklist from another stage, should keep the list collapsed`() {
        val previous = MainUiState()
        val next = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(completedTasks = setOf(OnboardingTask.CHOOSE_FONT), isChecklistExpanded = false)
        )

        assertSame(next, OnboardingReducer.reduce(previous, next))
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
            OnboardingUiState(completedTasks = allTasksButFont, activePicker = AppearancePicker.FONT)
        )
        val next = OnboardingReducer.applySignal(previous, OnboardingSignal.PICKER_CONFIRMED)

        val result = OnboardingReducer.reduce(previous, next).onboarding

        assertEquals(OnboardingStage.COMPLETED, result.stage)
        assertEquals(OnboardingTask.entries.toSet(), result.completedTasks)
        assertTrue(result.isCelebrating)
        assertFalse(result.isCelebrationPending)
        assertNull(result.activePicker)
    }

    @Test
    fun `when the last task is done with the home hidden, should hold the celebration`() {
        val previous = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(completedTasks = allTasksButFont, isHomeInForeground = false)
        )
        val next = previous.withOnboarding { copy(completedTasks = OnboardingTask.entries.toSet()) }

        val result = OnboardingReducer.reduce(previous, next).onboarding

        assertEquals(OnboardingStage.COMPLETED, result.stage)
        assertFalse(result.isCelebrating)
        assertTrue(result.isCelebrationPending)
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
    fun `when settings open during the checklist, should only mark the settings task as pending`() {
        val result = OnboardingReducer.applySignal(
            stateAt(OnboardingStage.CHECKLIST),
            OnboardingSignal.SETTINGS_OPENED
        ).onboarding

        assertTrue(result.completedTasks.isEmpty())
        assertEquals(OnboardingTask.EXPLORE_SETTINGS, result.pendingTask)
    }

    @Test
    fun `when settings open after the settings task is done, should keep the state`() {
        val done = stateAt(
            OnboardingStage.CHECKLIST,
            OnboardingUiState(completedTasks = setOf(OnboardingTask.EXPLORE_SETTINGS))
        )

        assertSame(done, OnboardingReducer.applySignal(done, OnboardingSignal.SETTINGS_OPENED))
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
