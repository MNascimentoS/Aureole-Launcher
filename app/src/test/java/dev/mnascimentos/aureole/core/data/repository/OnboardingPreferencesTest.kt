package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
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
            completedTasks = setOf(OnboardingTask.CHOOSE_VISUAL, OnboardingTask.ADD_WIDGET),
            isChecklistCollapsed = true
        )

        preferences.write(progress)

        assertEquals(progress, preferences.read())
    }

    @Test
    fun `when a preset was applied, should read it back and forget it once cleared`() {
        val progress = OnboardingProgress(stage = OnboardingStage.GESTURES, appliedPreset = LayoutPreset.WIDGETS)

        preferences.write(progress)
        assertEquals(progress, preferences.read())

        preferences.write(progress.copy(appliedPreset = null))
        assertNull(preferences.read()?.appliedPreset)
    }

    @Test
    fun `when tasks are skipped, should read the skipped tasks back and keep them apart from the done ones`() {
        val progress = OnboardingProgress(
            stage = OnboardingStage.CHECKLIST,
            completedTasks = setOf(OnboardingTask.ADD_CLOCK),
            skippedTasks = setOf(OnboardingTask.ADD_WIDGET, OnboardingTask.ADJUST_LAYOUT)
        )

        preferences.write(progress)

        assertEquals(progress, preferences.read())
        preferences.write(progress.copy(skippedTasks = emptySet()))
        assertTrue(preferences.read()?.skippedTasks.orEmpty().isEmpty())
    }

    @Test
    fun `when progress was stored before skipping existed, should read no skipped tasks`() {
        preferences.write(OnboardingProgress(stage = OnboardingStage.CHECKLIST))
        settingsPrefs().edit { remove("onboarding_skipped_tasks") }

        assertTrue(preferences.read()?.skippedTasks.orEmpty().isEmpty())
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
            putString("onboarding_completed_tasks", "OLD_TASK,ADD_CLOCK")
        }

        val progress = preferences.read()

        assertEquals(setOf(GestureCoachStep.OPEN_APPS), progress?.completedSteps)
        assertEquals(setOf(OnboardingTask.ADD_CLOCK), progress?.completedTasks)
    }

    @Test
    fun `when progress was saved with the palette and font steps, should read them as the visual step`() {
        settingsPrefs().edit {
            putString("onboarding_stage", OnboardingStage.CHECKLIST.name)
            putString("onboarding_completed_tasks", "ADD_CLOCK,CHOOSE_FONT,CHOOSE_PALETTE,SET_DEFAULT_HOME")
            putString("onboarding_skipped_tasks", "ADD_WIDGET")
        }

        val progress = preferences.read()

        assertEquals(
            setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL, OnboardingTask.ADD_CLOCK),
            progress?.completedTasks
        )
        assertEquals(setOf(OnboardingTask.ADD_WIDGET), progress?.skippedTasks)
        progress?.let(preferences::write)
        assertEquals(
            "ADD_CLOCK,CHOOSE_VISUAL,SET_DEFAULT_HOME",
            settingsPrefs().getString("onboarding_completed_tasks", null)
        )
    }

    @Test
    fun `when progress was saved before the reorder, should keep the same completed tasks`() {
        // Stored by name in the previous order (ADD_FAVORITE_APPS before ADD_WIDGET).
        // The order is not part of the data, so the same set comes back.
        settingsPrefs().edit {
            putString("onboarding_stage", OnboardingStage.CHECKLIST.name)
            putString(
                "onboarding_completed_tasks",
                "ADD_CLOCK,ADD_FAVORITE_APPS,ADD_WIDGET,SET_DEFAULT_HOME"
            )
        }

        val progress = preferences.read()

        assertEquals(
            setOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.ADD_CLOCK,
                OnboardingTask.ADD_FAVORITE_APPS,
                OnboardingTask.ADD_WIDGET
            ),
            progress?.completedTasks
        )
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
