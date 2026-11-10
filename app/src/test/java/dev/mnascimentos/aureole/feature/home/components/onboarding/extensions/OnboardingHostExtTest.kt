package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingHostExtTest {

    @Test
    fun `each stage should map to its surface`() {
        val expected = mapOf(
            OnboardingStage.WELCOME to OnboardingSurface.WELCOME,
            OnboardingStage.DEFAULT_HOME to OnboardingSurface.DEFAULT_HOME,
            OnboardingStage.GESTURES to OnboardingSurface.GESTURE_COACH,
            OnboardingStage.CHECKLIST to OnboardingSurface.CHECKLIST,
            OnboardingStage.COMPLETED to OnboardingSurface.NONE
        )

        expected.forEach { (stage, surface) -> assertEquals(surface, OnboardingUiState(stage = stage).visibleSurface) }
    }

    @Test
    fun `checklist should step aside when minimized or while a picker is open`() {
        val checklist = OnboardingUiState(stage = OnboardingStage.CHECKLIST)

        assertEquals(OnboardingSurface.NONE, checklist.copy(isChecklistExpanded = false).visibleSurface)
        assertEquals(OnboardingSurface.NONE, checklist.copy(activePicker = AppearancePicker.FONT).visibleSurface)
    }

    @Test
    fun `completed should celebrate once and then be idle`() {
        val celebrating = OnboardingUiState(stage = OnboardingStage.COMPLETED, isCelebrating = true)

        assertEquals(OnboardingSurface.CELEBRATION, celebrating.visibleSurface)
        assertFalse(celebrating.isIdle)
        assertTrue(celebrating.copy(isCelebrating = false).isIdle)
        assertFalse(OnboardingUiState(stage = OnboardingStage.CHECKLIST).isIdle)
    }

    @Test
    fun `success count should add done gestures and tasks`() {
        val state = OnboardingUiState(
            completedSteps = setOf(GestureCoachStep.OPEN_APPS),
            completedTasks = setOf(OnboardingTask.CHOOSE_FONT, OnboardingTask.ADD_WIDGET)
        )

        assertEquals(3, state.successCount)
    }

    @Test
    fun `host params should carry the home settings the onboarding reads`() {
        val hazeState = HazeState()
        val uiState = MainUiState(
            onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES),
            isHazeEnabled = false,
            hazeOpacity = 0.3f,
            isAlphabetScrubberDisabled = true,
            isLeftHandedMode = true,
            selectedThemeName = "Abyss",
            selectedFontName = "Inter",
            isAllAppsDrawerOpen = true
        )

        val params = uiState.toOnboardingHostParams(hazeState = hazeState, isTouchExplorationEnabled = true)

        assertEquals(OnboardingStage.GESTURES, params.onboarding.stage)
        assertSame(hazeState, params.glass.hazeState)
        assertFalse(params.glass.isHazeEnabled)
        assertEquals(0.3f, params.glass.hazeOpacity)
        assertTrue(params.coachConfig.isAlphabetScrubberDisabled)
        assertTrue(params.coachConfig.isLeftHandedMode)
        assertTrue(params.coachConfig.isTouchExplorationEnabled)
        assertEquals("Abyss", params.selection.paletteName)
        assertEquals("Inter", params.selection.fontName)
        assertTrue(params.isHomeOverlayActive)
    }

    @Test
    fun `edit hint should show only while a grid task waits in edit mode with no editor on top`() {
        val pending = OnboardingUiState(stage = OnboardingStage.CHECKLIST, pendingTask = OnboardingTask.ADD_CLOCK)
        fun hint(state: MainUiState) = state.toOnboardingHostParams(hazeState = null, isTouchExplorationEnabled = false)
            .showEditHint

        assertTrue(hint(MainUiState(onboarding = pending, isGridEditMode = true)))
        assertFalse(hint(MainUiState(onboarding = pending, isGridEditMode = true, showEditClockBottomSheet = true)))
        assertFalse(hint(MainUiState(onboarding = pending)))
        assertFalse(hint(MainUiState(onboarding = pending.copy(pendingTask = null), isGridEditMode = true)))
    }
}
