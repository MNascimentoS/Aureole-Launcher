package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale

/**
 * Phase 2 of onboarding-redesenho in plain JVM: what closing each sheet completes (RF-C1, C4–C6), the highlight of the
 * new item (RF-D1–D3), the return from Settings before "All set" (RF-D5) and the time of the Visual preview (RF-C3).
 */
class OnboardingSheetCloseTest {

    private val checklist = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false)
    private val fillingGroup = checklist.copy(pendingTask = OnboardingTask.FILL_CONTAINER)

    @Test
    fun `when the group closes with items, should count the container task as saved`() {
        assertTrue(fillingGroup.savingContainerTask(hasItems = true).isPendingTaskSaved)
    }

    @Test
    fun `when the group closes empty or another task waits, should not count it`() {
        assertSame(fillingGroup, fillingGroup.savingContainerTask(hasItems = false))
        val clock = checklist.copy(pendingTask = OnboardingTask.ADD_CLOCK)
        assertSame(clock, clock.savingContainerTask(hasItems = true))
    }

    @Test
    fun `when back from Settings, should complete the visit but wait to count the home as shown`() {
        val visiting = checklist.copy(pendingTask = OnboardingTask.EXPLORE_SETTINGS, isHomeInForeground = false)

        val back = visiting.returningToHome()

        assertTrue(OnboardingTask.EXPLORE_SETTINGS in back.completedTasks)
        assertNull(back.pendingTask)
        assertTrue(back.isChecklistExpanded)
        assertFalse(back.isHomeInForeground)
        assertSame(checklist, checklist.returningToHome())
    }

    @Test
    fun `when the home shows again, should play a celebration that waited`() {
        val waiting = OnboardingUiState(
            stage = OnboardingStage.COMPLETED,
            isHomeInForeground = false,
            isCelebrationPending = true
        )

        val shown = waiting.showingHome()

        assertTrue(shown.isHomeInForeground)
        assertTrue(shown.isCelebrating)
        assertFalse(shown.isCelebrationPending)
        assertFalse(checklist.copy(isHomeInForeground = false).showingHome().isCelebrating)
    }

    @Test
    fun `with animations off, the home counts as shown at once`() {
        assertEquals(HOME_RETURN_SETTLE_MS, homeReturnSettleMillis(areAnimatorsEnabled = true))
        assertEquals(0L, homeReturnSettleMillis(areAnimatorsEnabled = false))
    }

    @Test
    fun `when the checklist opens with the widget step to do, should preload the widgets once`() {
        val open = checklist.copy(isChecklistExpanded = true)

        assertTrue(shouldPreloadWidgets(previous = checklist, current = open))
        assertFalse(shouldPreloadWidgets(previous = open, current = open))
        val widgetDone = open.copy(completedTasks = setOf(OnboardingTask.ADD_WIDGET))
        assertFalse(shouldPreloadWidgets(previous = checklist, current = widgetDone))
        assertFalse(shouldPreloadWidgets(previous = checklist, current = open.copy(stage = OnboardingStage.GESTURES)))
    }

    @Test
    fun `while the home peeks after a step, should highlight only the new item`() {
        val peeking = checklist.peekingHome(itemId = "clock")

        assertEquals("clock", peeking.highlightedItemId)
        assertNull(checklist.peekingHome().highlightedItemId)
    }

    @Test
    fun `when the list opens or the next step starts, should drop the highlight`() {
        val peeking = checklist.peekingHome(itemId = "clock")

        assertNull(peeking.copy(isChecklistExpanded = true).highlightedItemId)
        assertNull(peeking.copy(pendingTask = OnboardingTask.ADD_WIDGET).highlightedItemId)
        assertNull(peeking.copy(activePicker = AppearancePicker.PALETTE).highlightedItemId)
        assertNull(peeking.endingHomePeek().highlightedItemId)
        assertNull(peeking.copy(stage = OnboardingStage.COMPLETED).highlightedItemId)
    }

    @Test
    fun `the Visual preview writes the time as the clock does`() {
        val afternoon = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 5, 15, 5, 0) }.timeInMillis

        assertEquals("3:05 PM", visualPreviewTime("12H", isSystem24Hour = true, Locale.US, afternoon))
        assertEquals("15:05", visualPreviewTime("24H", isSystem24Hour = false, Locale.US, afternoon))
        assertEquals("15:05", visualPreviewTime("SYSTEM", isSystem24Hour = true, Locale.US, afternoon))
        assertEquals("3:05 PM", visualPreviewTime("SYSTEM", isSystem24Hour = false, Locale.US, afternoon))
    }
}
