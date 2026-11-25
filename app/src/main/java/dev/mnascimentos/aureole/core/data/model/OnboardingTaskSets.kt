package dev.mnascimentos.aureole.core.data.model

/** The tasks read back from disk: the done ones and the skipped ones. */
data class OnboardingTaskSets(
    val completed: Set<OnboardingTask> = emptySet(),
    val skipped: Set<OnboardingTask> = emptySet()
)

/** The palette and font steps of the 9-step checklist; both became [OnboardingTask.CHOOSE_VISUAL]. */
private const val LEGACY_PALETTE = "CHOOSE_PALETTE"
private const val LEGACY_FONT = "CHOOSE_FONT"

/**
 * Reads the persisted task names, old format included (onboarding-redesenho D-21). The old palette and font steps
 * become the Visual step: skipped if either was skipped, done if both were done, pending otherwise. A Visual step
 * already in the new format wins. Unknown names are ignored, so nothing traps the user in the tour.
 */
fun migrateOnboardingTasks(completedNames: Set<String>, skippedNames: Set<String>): OnboardingTaskSets {
    val completed = OnboardingTask.entries.filter { it.name in completedNames }.toMutableSet()
    val skipped = OnboardingTask.entries.filter { it.name in skippedNames }.toMutableSet()
    val visual = OnboardingTask.CHOOSE_VISUAL
    val hasLegacy = listOf(LEGACY_PALETTE, LEGACY_FONT).any { it in completedNames || it in skippedNames }
    if (hasLegacy && visual !in completed && visual !in skipped) {
        when {
            LEGACY_PALETTE in skippedNames || LEGACY_FONT in skippedNames -> skipped += visual
            LEGACY_PALETTE in completedNames && LEGACY_FONT in completedNames -> completed += visual
        }
    }
    return OnboardingTaskSets(completed = completed, skipped = skipped)
}
