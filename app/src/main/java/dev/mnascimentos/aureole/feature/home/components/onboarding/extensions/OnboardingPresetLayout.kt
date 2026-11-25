package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetGroupKind
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.spec
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

/** Rows the dock of the "Widgets" preset takes at the bottom of the portrait home. */
internal const val PRESET_DOCK_ROW_SPAN = 4

private const val GROUP_ORIENTATION_DOCK = "Horizontal"

/** The grid a preset leaves empty: only one that is empty in both orientations can take a preset (D-22). */
internal fun canTakeLayoutPreset(portrait: List<LauncherItemState>, landscape: List<LauncherItemState>): Boolean =
    portrait.isEmpty() && landscape.isEmpty()

/**
 * The portrait home of [plan], on the default layout (RF-F1): the clock on top; the widget rows left free, so the
 * widget step drops its group there (D-34); the favorites below, with the group on the side or as a dock under
 * them, or across the whole width without a group. Every item gets a fresh id, so nothing an earlier item with a
 * stable id left behind comes back. Mirrored for left-handed.
 */
internal fun presetPortraitItems(
    plan: LayoutPresetPlan,
    isLeftHanded: Boolean,
    newId: () -> String = ::randomItemId
): List<LauncherItemState> {
    val defaults = GridDefaults.getDefaultGridItems(isLandscape = false, isLeftHanded = false)
    fun slot(type: LauncherItemType) = checkNotNull(defaults.firstOrNull { it.safeType == type })
    val fullWidth = GridEngineUtils.PORTRAIT_MAX_COLS
    val clock = slot(LauncherItemType.CLOCK).copy(id = newId())
    val appsSlot = slot(LauncherItemType.APPS_LIST).copy(id = newId(), showAllApps = plan.favorites.isEmpty())
    val groupSlot = slot(LauncherItemType.SHORTCUTS_CONTAINER).copy(id = newId())
    val items = when (plan.groupKind) {
        PresetGroupKind.NONE -> listOf(clock, appsSlot.copy(colSpan = fullWidth))
        PresetGroupKind.SIDE -> listOf(clock, appsSlot, groupSlot)
        PresetGroupKind.DOCK -> {
            val apps = appsSlot.copy(colSpan = fullWidth, rowSpan = appsSlot.rowSpan - PRESET_DOCK_ROW_SPAN)
            val dock = groupSlot.copy(
                col = 0,
                row = apps.row + apps.rowSpan,
                colSpan = fullWidth,
                rowSpan = PRESET_DOCK_ROW_SPAN
            )
            listOf(clock, apps, dock)
        }
    }
    if (!isLeftHanded) return items
    return items.map { it.copy(col = fullWidth - it.col - it.colSpan) }
}

/** The item of [type] among [items], for the ids the preset saves its content under. */
internal fun List<LauncherItemState>.idOf(type: LauncherItemType): String? = firstOrNull { it.safeType == type }?.id

/**
 * The group of [plan], saved under [id]: the onboarding's own group look (no background, grid folders), lying down
 * for the dock, with the category folders first and then the apps.
 */
internal fun presetContainerModel(
    plan: LayoutPresetPlan,
    id: String,
    position: String,
    isExpandCell: Boolean
): ContainerModel {
    val base = onboardingContainerModel(id = id, position = position, isExpandCell = isExpandCell)
    return base.copy(
        orientation = if (plan.groupKind == PresetGroupKind.DOCK) GROUP_ORIENTATION_DOCK else base.orientation,
        folders = plan.folders.map { folder ->
            AppFolder(
                id = newFolderId(id, folder.category),
                panelId = id,
                name = folder.name,
                appPackageNames = folder.packageNames,
                displayAsGrid = base.isGridFolderEnabled
            )
        },
        appPackageNames = plan.groupApps
    )
}

private fun newFolderId(containerId: String, category: Int) = "$containerId-category-$category"

private const val THUMBNAIL_PLACEHOLDER = "thumbnail"

/**
 * The mini home a preset card draws: the plan for this device when it is ready, else the preset's own shape (its
 * group, as if the device had apps for it). Ids are not needed, so they stay blank.
 */
internal fun LayoutPreset.thumbnailItems(plan: LayoutPresetPlan?, isLeftHanded: Boolean): List<LauncherItemState> {
    val shape = plan ?: LayoutPresetPlan(
        preset = this,
        favorites = listOf(THUMBNAIL_PLACEHOLDER),
        groupApps = if (spec.groupKind == PresetGroupKind.NONE) emptyList() else listOf(THUMBNAIL_PLACEHOLDER)
    )
    return presetPortraitItems(shape, isLeftHanded) { "" }
}
