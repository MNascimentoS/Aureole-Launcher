package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * The layout a cancelled grid edit goes back to, [restored], plus the apps list the favorites task created during
 * the edit, with the same id and "Show all apps" on, so cancelling the task leaves the home with apps and a retry
 * reuses the list. Null when there is nothing to keep: another task, no list created, or its spot taken.
 */
internal fun MainUiState.keepingCreatedAppsList(restored: List<LauncherItemState>): List<LauncherItemState>? {
    if (onboarding.pendingTask != OnboardingTask.ADD_FAVORITE_APPS) return null
    val createdId = onboarding.createdItemId ?: return null
    val created = gridItems.firstOrNull { it.id == createdId } ?: return null
    val isKept = restored.any { it.id == createdId }
    if (isKept || GridEngineUtils.checkCollisionWithOthers(created, restored)) return null
    return restored + created.copy(showAllApps = true)
}

internal fun List<LauncherItemState>.withoutFailedItem(
    task: OnboardingTask,
    createdId: String?
): List<LauncherItemState> = when {
    createdId == null -> this
    task == OnboardingTask.ADD_FAVORITE_APPS -> map { item ->
        if (item.id == createdId) item.copy(showAllApps = true) else item
    }
    else -> filterNot { it.id == createdId }
}
