package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.CATEGORY_ACCESSIBILITY_VALUE
import dev.mnascimentos.aureole.core.data.model.PresetApp
import dev.mnascimentos.aureole.core.data.model.PresetCatalog
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.systemShortcutPackage

/**
 * The system roles the presets look for first, in order (RF-F2): dialer, SMS, camera, browser, e-mail, calendar.
 * They are the targets of the system shortcuts, resolved through the `<queries>` the manifest already declares.
 */
private val PRESET_ROLE_TARGETS = listOf("Dialer", "Messages", "Camera", "Browser", "Email", "Calendar")

/** The folder name of each app category the presets turn into a folder (RF-F3). */
private val PRESET_CATEGORY_NAMES: Map<Int, Int> = mapOf(
    ApplicationInfo.CATEGORY_SOCIAL to R.string.home_onboarding_preset_category_social,
    ApplicationInfo.CATEGORY_PRODUCTIVITY to R.string.home_onboarding_preset_category_productivity,
    ApplicationInfo.CATEGORY_AUDIO to R.string.home_onboarding_preset_category_audio,
    ApplicationInfo.CATEGORY_VIDEO to R.string.home_onboarding_preset_category_video,
    ApplicationInfo.CATEGORY_NEWS to R.string.home_onboarding_preset_category_news,
    ApplicationInfo.CATEGORY_MAPS to R.string.home_onboarding_preset_category_maps,
    ApplicationInfo.CATEGORY_IMAGE to R.string.home_onboarding_preset_category_image,
    ApplicationInfo.CATEGORY_GAME to R.string.home_onboarding_preset_category_game,
    CATEGORY_ACCESSIBILITY_VALUE to R.string.home_onboarding_preset_category_accessibility
)

/**
 * Reads what the presets need with no permission (D-14): the launchable apps with their category, the packages of
 * the system roles and the category names. Blocking: call it off the main thread.
 */
internal fun Context.readPresetCatalog(): PresetCatalog {
    val pm = packageManager
    val launcher = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
    val apps = pm.queryIntentActivities(launcher, 0)
        .filter { it.activityInfo.packageName != packageName }
        .map { info ->
            PresetApp(
                packageName = info.activityInfo.packageName,
                label = info.loadLabel(pm).toString(),
                category = info.activityInfo.applicationInfo?.category ?: ApplicationInfo.CATEGORY_UNDEFINED
            )
        }
    return PresetCatalog(
        apps = apps,
        rolePackages = PRESET_ROLE_TARGETS.map { pm.systemShortcutPackage(it) },
        categoryNames = PRESET_CATEGORY_NAMES.mapValues { (_, res) -> getString(res) }
    )
}
