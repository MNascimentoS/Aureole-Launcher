package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingTask

/** Where a preset puts its group: none, standing on the side (as the default layout) or lying down as a dock. */
enum class PresetGroupKind { NONE, SIDE, DOCK }

/**
 * What a preset promises (RF-F1): the clock look, how many favorites, the group and its content, and whether it has
 * a widget. The widget is never bound by the preset (D-34): the widget step stays to do.
 */
data class LayoutPresetSpec(
    val clockLayout: ClockLayout,
    val favoriteCount: Int,
    val groupKind: PresetGroupKind = PresetGroupKind.NONE,
    val maxFolders: Int = 0,
    val groupAppCount: Int = 0,
    val hasWidget: Boolean = false
)

private const val MINIMAL_FAVORITES = 5
private const val PRODUCTIVE_FAVORITES = 4
private const val PRODUCTIVE_FOLDERS = 3
private const val PRODUCTIVE_GROUP_APPS = 1
private const val WIDGETS_FAVORITES = 3
private const val WIDGETS_DOCK_ITEMS = 4

val LayoutPreset.spec: LayoutPresetSpec
    get() = when (this) {
        LayoutPreset.MINIMAL -> LayoutPresetSpec(clockLayout = ClockLayout.TIME_ONLY, favoriteCount = MINIMAL_FAVORITES)
        LayoutPreset.PRODUCTIVE -> LayoutPresetSpec(
            clockLayout = ClockLayout.DATE_ON_TOP,
            favoriteCount = PRODUCTIVE_FAVORITES,
            groupKind = PresetGroupKind.SIDE,
            maxFolders = PRODUCTIVE_FOLDERS,
            groupAppCount = PRODUCTIVE_GROUP_APPS,
            hasWidget = true
        )
        LayoutPreset.WIDGETS -> LayoutPresetSpec(
            clockLayout = ClockLayout.TIME_ON_TOP,
            favoriteCount = WIDGETS_FAVORITES,
            groupKind = PresetGroupKind.DOCK,
            groupAppCount = WIDGETS_DOCK_ITEMS,
            hasWidget = true
        )
    }

/** One folder a preset creates: the apps of one category, named after it (RF-F3, D-15). */
data class PresetFolder(val category: Int, val name: String, val packageNames: List<String>)

/** A preset worked out for this device: the apps and folders it will place. */
data class LayoutPresetPlan(
    val preset: LayoutPreset,
    val favorites: List<String> = emptyList(),
    val folders: List<PresetFolder> = emptyList(),
    val groupApps: List<String> = emptyList()
) {
    val spec: LayoutPresetSpec get() = preset.spec

    /** The group is placed only with something in it; an empty one would leave the group step undone anyway. */
    val groupKind: PresetGroupKind
        get() = if (folders.isEmpty() && groupApps.isEmpty()) PresetGroupKind.NONE else spec.groupKind
}

/** The checklist steps that are already counted as done ("Layout <name> applied", RF-F4). */
val presetLayoutTasks: Set<OnboardingTask> = setOf(
    OnboardingTask.ADD_CLOCK,
    OnboardingTask.ADD_WIDGET,
    OnboardingTask.ADD_FAVORITE_APPS,
    OnboardingTask.FILL_CONTAINER
)

/**
 * The steps the plan completes (RF-F4): the clock always; favorites and group when the plan placed them, or when
 * the preset has none by design; the widget only when the preset has none, since it never binds one (D-34).
 */
fun LayoutPresetPlan.completedTasks(): Set<OnboardingTask> = buildSet {
    add(OnboardingTask.ADD_CLOCK)
    if (favorites.isNotEmpty()) add(OnboardingTask.ADD_FAVORITE_APPS)
    if (spec.groupKind == PresetGroupKind.NONE || groupKind != PresetGroupKind.NONE) add(OnboardingTask.FILL_CONTAINER)
    if (!spec.hasWidget) add(OnboardingTask.ADD_WIDGET)
}

/** The ids a preset saved its content under, to take it all back on "Undo" (RF-F5). */
data class PresetPlacement(val clockId: String?, val appsListId: String?, val containerId: String?)

/** One line of what the preset brings: a plain string, or a plural with its count. */
sealed interface PresetContentLine {
    data class Text(@StringRes val res: Int) : PresetContentLine
    data class Count(@PluralsRes val res: Int, val count: Int) : PresetContentLine
}
