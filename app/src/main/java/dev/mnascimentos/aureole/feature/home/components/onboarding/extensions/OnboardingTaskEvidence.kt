package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.favoritesOf

/** The home item a checklist task places and edits; null for tasks that do not touch the grid. */
val OnboardingTask.gridItemType: LauncherItemType?
    get() = when (this) {
        OnboardingTask.ADD_CLOCK -> LauncherItemType.CLOCK
        OnboardingTask.ADD_FAVORITE_APPS -> LauncherItemType.APPS_LIST
        OnboardingTask.FILL_CONTAINER -> LauncherItemType.SHORTCUTS_CONTAINER
        OnboardingTask.ADD_WIDGET -> LauncherItemType.WIDGET_LIST
        else -> null
    }

/** Tasks judged when the grid edit mode or their editor closes: the item tasks and the layout step. */
internal val OnboardingTask.isJudgedOnSave: Boolean
    get() = gridItemType != null || this == OnboardingTask.ADJUST_LAYOUT

/** An editor a grid task opens is on screen, including the widget bind/configure screens. */
internal val MainUiState.isTaskEditorOpen: Boolean
    get() = showEditClockBottomSheet ||
        showFavoritePickerDialog ||
        isEditContainerDialogVisible ||
        showWidgetPicker ||
        pendingWidgetId != -1

/** The task's editor or the grid edit mode is still open: the user has not saved yet. */
internal val MainUiState.isTaskInProgress: Boolean
    get() = isGridEditMode || isTaskEditorOpen

/**
 * Whether the saved home shows what [task] asks for. The container task has its own confirmation,
 * the editor's Save, so leaving its editor any other way fails it.
 */
internal fun MainUiState.meetsRequirement(task: OnboardingTask): Boolean = when (task) {
    OnboardingTask.ADD_CLOCK -> gridItems.any { it.safeType == LauncherItemType.CLOCK }
    OnboardingTask.ADD_FAVORITE_APPS -> gridItems.any { hasAppsListWithFavorites(it) }
    OnboardingTask.ADD_WIDGET -> gridItems.any { hasWidget(it) }
    OnboardingTask.ADJUST_LAYOUT -> true
    else -> false
}

/** Same lookup as the home's favorites list: the list's own favorites, else the global ones for the legacy list. */
internal fun MainUiState.hasFavorites(item: LauncherItemState): Boolean =
    favoritesOf(item.id).isNotEmpty()

/** An apps list with at least one favorite, on the home or inside a scroll group. */
private fun MainUiState.hasAppsListWithFavorites(item: LauncherItemState): Boolean =
    (item.safeType == LauncherItemType.APPS_LIST && hasFavorites(item)) ||
        item.safeChildren.any { hasAppsListWithFavorites(it) }

private fun MainUiState.hasWidget(item: LauncherItemState): Boolean {
    val hasStackedWidget = item.safeType == LauncherItemType.WIDGET_LIST && !widgetStacks[item.id].isNullOrEmpty()
    val isBoundWidget = item.safeType == LauncherItemType.SINGLE_APP_WIDGET && (item.widgetId ?: 0) > 0
    return hasStackedWidget || isBoundWidget || item.safeChildren.any { hasWidget(it) }
}
