package dev.mnascimentos.aureole.core.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.migrateOnboardingTasks

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
        // The 9-step format (palette and font apart) is read into the 8 steps; the next write saves the new one.
        val tasks = migrateOnboardingTasks(readNames(KEY_COMPLETED_TASKS), readNames(KEY_SKIPPED_TASKS))
        return OnboardingProgress(
            stage = parseStage(rawStage),
            completedSteps = readEnumSet(KEY_COMPLETED_STEPS, GestureCoachStep.entries),
            completedTasks = tasks.completed,
            skippedTasks = tasks.skipped,
            isChecklistCollapsed = prefs.getBoolean(KEY_CHECKLIST_COLLAPSED, false),
            appliedPreset = prefs.getString(KEY_APPLIED_PRESET, null)
                ?.let { name -> LayoutPreset.entries.firstOrNull { it.name == name } }
        )
    }

    fun write(progress: OnboardingProgress) {
        prefs.edit {
            putString(KEY_STAGE, progress.stage.name)
            putString(KEY_COMPLETED_STEPS, progress.completedSteps.toCsv())
            putString(KEY_COMPLETED_TASKS, progress.completedTasks.toCsv())
            putString(KEY_SKIPPED_TASKS, progress.skippedTasks.toCsv())
            putBoolean(KEY_CHECKLIST_COLLAPSED, progress.isChecklistCollapsed)
            val preset = progress.appliedPreset
            if (preset == null) remove(KEY_APPLIED_PRESET) else putString(KEY_APPLIED_PRESET, preset.name)
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
        val names = readNames(key)
        return entries.filter { it.name in names }.toSet()
    }

    private fun readNames(key: String): Set<String> = prefs.getString(key, null).orEmpty()
        .split(SEPARATOR)
        .filter { it.isNotBlank() }
        .toSet()

    private fun Collection<Enum<*>>.toCsv(): String = map { it.name }.sorted().joinToString(SEPARATOR)

    companion object {
        private const val SEPARATOR = ","
        private const val KEY_STAGE = "onboarding_stage"
        private const val KEY_COMPLETED_STEPS = "onboarding_completed_steps"
        private const val KEY_COMPLETED_TASKS = "onboarding_completed_tasks"
        private const val KEY_SKIPPED_TASKS = "onboarding_skipped_tasks"
        private const val KEY_CHECKLIST_COLLAPSED = "onboarding_checklist_collapsed"
        private const val KEY_APPLIED_PRESET = "onboarding_applied_preset"
    }
}
