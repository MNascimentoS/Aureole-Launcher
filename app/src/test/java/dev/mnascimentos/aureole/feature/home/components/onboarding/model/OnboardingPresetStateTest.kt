package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.visibleSurface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class OnboardingPresetStateTest {

    private val welcome = OnboardingUiState(stage = OnboardingStage.WELCOME, isChoosingPreset = true)
    private val minimal = LayoutPresetPlan(LayoutPreset.MINIMAL, favorites = listOf("a"))

    @Test
    fun `choosing a preset should show the presets screen instead of the welcome`() {
        assertEquals(OnboardingSurface.PRESETS, welcome.visibleSurface)
        assertEquals(OnboardingSurface.WELCOME, welcome.copy(isChoosingPreset = false).visibleSurface)
    }

    @Test
    fun `applying should go on to the gestures with the preset's steps done`() {
        val applied = welcome.copy(isDefaultLauncher = true).applyingPreset(minimal)

        assertEquals(OnboardingStage.GESTURES, applied.stage)
        assertEquals(presetLayoutTasks, applied.completedTasks)
        assertEquals(LayoutPreset.MINIMAL, applied.appliedPreset)
        assertFalse(applied.isChoosingPreset)
    }

    @Test
    fun `applying should pass through the home app step when the Aureole is not the home yet`() {
        assertEquals(OnboardingStage.DEFAULT_HOME, welcome.applyingPreset(minimal).stage)
    }

    @Test
    fun `undo should return to the welcome buttons as they were`() {
        val applied = welcome.applyingPreset(minimal)

        val undone = applied.undoingPreset(before = welcome)

        assertEquals(OnboardingStage.WELCOME, undone.stage)
        assertNull(undone.appliedPreset)
        assertEquals(emptySet<OnboardingTask>(), undone.completedTasks)
        assertFalse(undone.isChoosingPreset)
    }

    @Test
    fun `the applied label should show only on the done steps the preset did`() {
        val applied = welcome.applyingPreset(LayoutPresetPlan(LayoutPreset.PRODUCTIVE, favorites = listOf("a")))

        assertEquals(LayoutPreset.PRODUCTIVE, applied.presetAppliedTo(OnboardingTask.ADD_CLOCK))
        assertNull(applied.presetAppliedTo(OnboardingTask.ADD_WIDGET))
        assertNull(applied.presetAppliedTo(OnboardingTask.CHOOSE_VISUAL))
        assertNull(OnboardingUiState().presetAppliedTo(OnboardingTask.ADD_CLOCK))
    }

    @Test
    fun `the applied preset should be persisted with the progress`() {
        val applied = welcome.applyingPreset(minimal)

        assertEquals(LayoutPreset.MINIMAL, applied.toProgress().toUiState(isDefaultLauncher = false).appliedPreset)
    }
}
