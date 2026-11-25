package dev.mnascimentos.aureole.core.data.model

/**
 * The ready-made layouts the welcome offers (onboarding-redesenho RF-F1, D-13). The names are persisted with the
 * onboarding progress (`onboarding_applied_preset`): never rename.
 */
enum class LayoutPreset { MINIMAL, PRODUCTIVE, WIDGETS }

/** One launchable app as the presets see it; [category] is `ApplicationInfo.category` (-1 when undefined). */
data class PresetApp(val packageName: String, val label: String, val category: Int)

/**
 * What the presets read from the device with no permission (D-14): the launchable apps, the package that answers
 * each system role in role order (dialer, SMS, camera, browser, e-mail, calendar; null when none does) and the
 * translated folder name of each app category.
 */
data class PresetCatalog(
    val apps: List<PresetApp> = emptyList(),
    val rolePackages: List<String?> = emptyList(),
    val categoryNames: Map<Int, String> = emptyMap()
)
