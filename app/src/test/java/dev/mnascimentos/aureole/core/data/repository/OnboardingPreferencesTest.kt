package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class OnboardingPreferencesTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var preferences: OnboardingPreferences

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        preferences = settingsRepository.onboarding
    }

    @Test
    fun `when nothing was stored, should return null`() {
        assertNull(preferences.read())
    }

    @Test
    fun `when progress is written, should read the same progress back`() {
        val progress = OnboardingProgress(
            stage = OnboardingStage.CHECKLIST,
            completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.CUSTOMIZE),
            completedTasks = setOf(OnboardingTask.CHOOSE_PALETTE, OnboardingTask.ADD_WIDGET),
            isChecklistCollapsed = true
        )

        preferences.write(progress)

        assertEquals(progress, preferences.read())
    }

    @Test
    fun `when progress has empty sets, should read empty sets back`() {
        preferences.write(OnboardingProgress(stage = OnboardingStage.GESTURES))

        val progress = preferences.read()

        assertEquals(OnboardingStage.GESTURES, progress?.stage)
        assertTrue(progress?.completedSteps.orEmpty().isEmpty())
        assertTrue(progress?.completedTasks.orEmpty().isEmpty())
    }

    @Test
    fun `when the stored stage is unknown, should fall back to completed`() {
        settingsPrefs().edit { putString("onboarding_stage", "SOMETHING_REMOVED") }

        assertEquals(OnboardingStage.COMPLETED, preferences.read()?.stage)
    }

    @Test
    fun `when stored sets contain unknown names, should ignore them`() {
        settingsPrefs().edit {
            putString("onboarding_stage", OnboardingStage.CHECKLIST.name)
            putString("onboarding_completed_steps", "OPEN_APPS,OLD_STEP,,")
            putString("onboarding_completed_tasks", "OLD_TASK,CHOOSE_FONT")
        }

        val progress = preferences.read()

        assertEquals(setOf(GestureCoachStep.OPEN_APPS), progress?.completedSteps)
        assertEquals(setOf(OnboardingTask.CHOOSE_FONT), progress?.completedTasks)
    }

    @Test
    fun `when restarted, should go back to welcome with no progress`() {
        preferences.write(
            OnboardingProgress(
                stage = OnboardingStage.COMPLETED,
                completedSteps = GestureCoachStep.entries.toSet(),
                completedTasks = OnboardingTask.entries.toSet(),
                isChecklistCollapsed = true
            )
        )

        preferences.restart()

        assertEquals(OnboardingProgress(stage = OnboardingStage.WELCOME), preferences.read())
    }

    @Test
    fun `when the launcher is factory reset, should clear the onboarding progress`() {
        settingsRepository.terminateProcess = {}
        preferences.write(OnboardingProgress(stage = OnboardingStage.COMPLETED))

        settingsRepository.performFactoryReset()

        assertNull(preferences.read())
    }

    private fun settingsPrefs() = context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE)
}
