package dev.mnascimentos.aureole.core.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask

/**
 * Stores the first user experience progress. It lives in the settings preferences file,
 * so a factory reset clears it and backups exclude it like every other launcher setting.
 */
class OnboardingPreferences(private val prefsProvider: () -> SharedPreferences) {

    private val prefs: SharedPreferences
        get() = prefsProvider()

    /** Returns null while the onboarding was never resolved for this install. */
    fun read(): OnboardingProgress? {
        val rawStage = prefs.getString(KEY_STAGE, null) ?: return null
        return OnboardingProgress(
            stage = parseStage(rawStage),
            completedSteps = readEnumSet(KEY_COMPLETED_STEPS, GestureCoachStep.entries),
            completedTasks = readEnumSet(KEY_COMPLETED_TASKS, OnboardingTask.entries),
            isChecklistCollapsed = prefs.getBoolean(KEY_CHECKLIST_COLLAPSED, false)
        )
    }

    fun write(progress: OnboardingProgress) {
        prefs.edit {
            putString(KEY_STAGE, progress.stage.name)
            putString(KEY_COMPLETED_STEPS, progress.completedSteps.toCsv())
            putString(KEY_COMPLETED_TASKS, progress.completedTasks.toCsv())
            putBoolean(KEY_CHECKLIST_COLLAPSED, progress.isChecklistCollapsed)
        }
    }

    fun restart() {
        write(OnboardingProgress(stage = OnboardingStage.WELCOME))
    }

    private fun parseStage(rawStage: String): OnboardingStage {
        // An unknown value must never trap the user inside the onboarding.
        return OnboardingStage.entries.firstOrNull { it.name == rawStage } ?: OnboardingStage.COMPLETED
    }

    private fun <T : Enum<T>> readEnumSet(key: String, entries: List<T>): Set<T> {
        val names = prefs.getString(key, null).orEmpty()
            .split(SEPARATOR)
            .filter { it.isNotBlank() }
            .toSet()
        return entries.filter { it.name in names }.toSet()
    }

    private fun Collection<Enum<*>>.toCsv(): String = map { it.name }.sorted().joinToString(SEPARATOR)

    companion object {
        private const val SEPARATOR = ","
        private const val KEY_STAGE = "onboarding_stage"
        private const val KEY_COMPLETED_STEPS = "onboarding_completed_steps"
        private const val KEY_COMPLETED_TASKS = "onboarding_completed_tasks"
        private const val KEY_CHECKLIST_COLLAPSED = "onboarding_checklist_collapsed"
    }
}
