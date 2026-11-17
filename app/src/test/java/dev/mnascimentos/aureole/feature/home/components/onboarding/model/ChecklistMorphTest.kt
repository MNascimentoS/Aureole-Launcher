package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.checklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.hostSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ChecklistMorphTest {

    private val checklist = OnboardingUiState(stage = OnboardingStage.CHECKLIST)

    @Test
    fun `when the list is open, should show it and leave the crossfade empty`() {
        val open = checklist.copy(isChecklistExpanded = true)

        assertEquals(ChecklistMorph.EXPANDED, open.checklistMorph(isOtherOverlayActive = false))
        assertEquals(OnboardingSurface.NONE, open.hostSurface)
    }

    @Test
    fun `when minimized, should show the pill unless another overlay is open`() {
        val minimized = checklist.copy(isChecklistExpanded = false)

        assertEquals(ChecklistMorph.MINIMIZED, minimized.checklistMorph(isOtherOverlayActive = false))
        assertEquals(ChecklistMorph.HIDDEN, minimized.checklistMorph(isOtherOverlayActive = true))
    }

    @Test
    fun `when a picker is open or the home peeks, should hide the layer`() {
        assertEquals(
            ChecklistMorph.HIDDEN,
            checklist.copy(activePicker = AppearancePicker.FONT).checklistMorph(isOtherOverlayActive = false)
        )
        assertEquals(
            ChecklistMorph.HIDDEN,
            checklist.copy(isChecklistExpanded = false, isHomePeeking = true).checklistMorph(false)
        )
    }

    @Test
    fun `when another stage is on, should hide the layer and keep its surface`() {
        val gestures = OnboardingUiState(stage = OnboardingStage.GESTURES)

        assertEquals(ChecklistMorph.HIDDEN, gestures.checklistMorph(isOtherOverlayActive = false))
        assertEquals(OnboardingSurface.GESTURE_COACH, gestures.hostSurface)
    }

    @Test
    fun `when the list reopens from the pill, should remember it came from the pill`() {
        val pill = nextMorphTarget(previous = null, morph = ChecklistMorph.MINIMIZED)

        val reopened = nextMorphTarget(previous = pill, morph = ChecklistMorph.EXPANDED)

        assertTrue(reopened.cameFromPill)
    }

    @Test
    fun `when the list opens from nothing, should rise as usual`() {
        val hidden = nextMorphTarget(previous = null, morph = ChecklistMorph.HIDDEN)

        assertFalse(nextMorphTarget(previous = hidden, morph = ChecklistMorph.EXPANDED).cameFromPill)
        assertFalse(nextMorphTarget(previous = null, morph = ChecklistMorph.EXPANDED).cameFromPill)
    }

    @Test
    fun `when the morph does not change, should keep the same target`() {
        val target = ChecklistMorphTarget(ChecklistMorph.EXPANDED, cameFromPill = true)

        assertSame(target, nextMorphTarget(previous = target, morph = ChecklistMorph.EXPANDED))
    }
}
