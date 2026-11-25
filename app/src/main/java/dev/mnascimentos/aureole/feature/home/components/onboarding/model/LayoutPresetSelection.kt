package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import android.content.pm.ApplicationInfo
import dev.mnascimentos.aureole.core.data.model.CATEGORY_ACCESSIBILITY_VALUE
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.PresetApp
import dev.mnascimentos.aureole.core.data.model.PresetCatalog

/** A category needs this many installed apps to become a folder (RF-F3). */
const val PRESET_FOLDER_MIN_APPS = 2

/** A preset folder holds at most this many apps, in alphabetical order (RF-F3). */
const val PRESET_FOLDER_MAX_APPS = 9

/**
 * The category order of the presets (D-33): the example order of RF-F3, then games and accessibility. Apps with
 * no category come after all of these and never become a folder.
 */
val presetCategoryOrder: List<Int> = listOf(
    ApplicationInfo.CATEGORY_SOCIAL,
    ApplicationInfo.CATEGORY_PRODUCTIVITY,
    ApplicationInfo.CATEGORY_AUDIO,
    ApplicationInfo.CATEGORY_VIDEO,
    ApplicationInfo.CATEGORY_NEWS,
    ApplicationInfo.CATEGORY_MAPS,
    ApplicationInfo.CATEGORY_IMAGE,
    ApplicationInfo.CATEGORY_GAME,
    CATEGORY_ACCESSIBILITY_VALUE
)

private val alphabetical: Comparator<PresetApp> = compareBy<PresetApp>({ it.label.lowercase() }, { it.packageName })

private fun categoryRank(category: Int): Int =
    presetCategoryOrder.indexOf(category).takeIf { it >= 0 } ?: presetCategoryOrder.size

/** Each installed app once (an app with two launcher entries counts once, with its first label). */
private val PresetCatalog.uniqueApps: List<PresetApp>
    get() = apps.distinctBy { it.packageName }

/**
 * Every installed app, best first, always in the same order for the same device (RF-F2): the apps that answer
 * the system roles, in role order (an uninstalled or repeated one is left out), then the rest by category order
 * and alphabetically inside a category.
 */
fun PresetCatalog.rankedPackages(): List<String> {
    val installed = uniqueApps.map { it.packageName }.toSet()
    val roles = rolePackages.filterNotNull().filter { it in installed }.distinct()
    val rest = uniqueApps
        .filter { it.packageName !in roles }
        .sortedWith(compareBy<PresetApp> { categoryRank(it.category) }.then(alphabetical))
        .map { it.packageName }
    return roles + rest
}

/**
 * One folder per category with [PRESET_FOLDER_MIN_APPS] or more installed apps, in category order, each with at
 * most [PRESET_FOLDER_MAX_APPS] apps in alphabetical order and the category's translated name (RF-F3). A category
 * with no name is left out.
 */
fun PresetCatalog.categoryFolders(): List<PresetFolder> = uniqueApps
    .filter { it.category in presetCategoryOrder }
    .groupBy { it.category }
    .filterValues { it.size >= PRESET_FOLDER_MIN_APPS }
    .toSortedMap(compareBy<Int> { categoryRank(it) })
    .mapNotNull { (category, apps) ->
        val name = categoryNames[category]?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
        PresetFolder(
            category = category,
            name = name,
            packageNames = apps.sortedWith(alphabetical).take(PRESET_FOLDER_MAX_APPS).map { it.packageName }
        )
    }

/**
 * The [preset] for this device: the first apps of the ranking as favorites, the first category folders, and the
 * next ranked apps that are neither favorites nor in a chosen folder for the group.
 */
fun PresetCatalog.planFor(preset: LayoutPreset): LayoutPresetPlan {
    val spec = preset.spec
    val ranked = rankedPackages()
    val favorites = ranked.take(spec.favoriteCount)
    val folders = if (spec.groupKind == PresetGroupKind.NONE) emptyList() else categoryFolders().take(spec.maxFolders)
    val inFolders = folders.flatMap { it.packageNames }.toSet()
    val groupApps = if (spec.groupKind == PresetGroupKind.NONE) {
        emptyList()
    } else {
        ranked.filter { it !in favorites && it !in inFolders }.take(spec.groupAppCount)
    }
    return LayoutPresetPlan(preset = preset, favorites = favorites, folders = folders, groupApps = groupApps)
}

/** The three presets for this device, in the order the screen lists them. */
fun PresetCatalog.planAll(): List<LayoutPresetPlan> = LayoutPreset.entries.map { planFor(it) }
