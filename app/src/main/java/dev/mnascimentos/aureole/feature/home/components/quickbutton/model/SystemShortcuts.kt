package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.ShortcutType

private fun mainSelector(category: String): () -> Intent = {
    Intent.makeMainSelectorActivity(
        Intent.ACTION_MAIN,
        category
    )
}

private val SYSTEM_INTENTS: Map<String, () -> Intent> = mapOf(
    "Browser" to mainSelector(Intent.CATEGORY_APP_BROWSER),
    "Calendar" to mainSelector(Intent.CATEGORY_APP_CALENDAR),
    "Calculator" to mainSelector(Intent.CATEGORY_APP_CALCULATOR),
    "Contacts" to mainSelector(Intent.CATEGORY_APP_CONTACTS),
    "Email" to mainSelector(Intent.CATEGORY_APP_EMAIL),
    "Messages" to mainSelector(Intent.CATEGORY_APP_MESSAGING),
    "Music Player" to mainSelector(Intent.CATEGORY_APP_MUSIC),
    "Camera" to { Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA) },
    "Clock" to { Intent(AlarmClock.ACTION_SHOW_ALARMS) },
    "Dialer" to { Intent(Intent.ACTION_DIAL) },
    "Search" to { Intent(Intent.ACTION_WEB_SEARCH) },
    "Settings" to { Intent(Settings.ACTION_SETTINGS) },
    "Files" to { Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*") },
    "Battery Settings" to { Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS) },
    "Bluetooth Settings" to { Intent(Settings.ACTION_BLUETOOTH_SETTINGS) },
    "Device Settings" to { Intent(Settings.ACTION_DEVICE_INFO_SETTINGS) },
    "Display Settings" to { Intent(Settings.ACTION_DISPLAY_SETTINGS) },
    "Location Settings" to { Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) },
    "Sound Settings" to { Intent(Settings.ACTION_SOUND_SETTINGS) },
    "Wi-Fi Settings" to { Intent(Settings.ACTION_WIFI_SETTINGS) }
)

/**
 * The intent a system shortcut [target] ("Calendar", "Wi-Fi Settings"...) opens, or null for an unknown target:
 * an unknown target never falls back to Settings. Each one is the most specific intent for what it names.
 */
internal fun systemIntentFor(target: String): Intent? = SYSTEM_INTENTS[target]?.invoke()

/** Package of the app that answers the system shortcut [target] on this device, or null when none does. */
internal fun PackageManager.systemShortcutPackage(target: String): String? {
    val intent = systemIntentFor(target) ?: return null
    return resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName
        ?: queryIntentActivities(intent, 0).firstOrNull()?.activityInfo?.packageName
}

/** String of the shortcut's name, or null for an "open app" shortcut (named after the app) and unknown targets. */
@StringRes
internal fun ShortcutAction.nameRes(): Int? = when (type) {
    ShortcutType.NONE -> R.string.home_shortcut_name_none
    ShortcutType.ALL_APPS -> R.string.home_shortcut_name_all_apps
    ShortcutType.ASSISTANT -> R.string.home_shortcut_name_assistant
    ShortcutType.SETTINGS -> R.string.home_shortcut_name_aureole_settings
    ShortcutType.NOTIFICATIONS -> R.string.home_shortcut_name_notifications
    ShortcutType.OPEN_APP -> R.string.home_shortcut_name_open_app
    ShortcutType.SYSTEM_INTENT -> SYSTEM_NAME_RES[target]
}

private val SYSTEM_NAME_RES: Map<String, Int> = mapOf(
    "Browser" to R.string.home_shortcut_name_browser,
    "Calculator" to R.string.home_shortcut_name_calculator,
    "Calendar" to R.string.home_shortcut_name_calendar,
    "Camera" to R.string.home_shortcut_name_camera,
    "Clock" to R.string.home_shortcut_name_clock,
    "Contacts" to R.string.home_shortcut_name_contacts,
    "Dialer" to R.string.home_shortcut_name_dialer,
    "Email" to R.string.home_shortcut_name_email,
    "Search" to R.string.home_shortcut_name_search,
    "Settings" to R.string.home_shortcut_name_settings,
    "Files" to R.string.home_shortcut_name_files,
    "Messages" to R.string.home_shortcut_name_messages,
    "Music Player" to R.string.home_shortcut_name_music,
    "Battery Settings" to R.string.home_shortcut_name_battery,
    "Bluetooth Settings" to R.string.home_shortcut_name_bluetooth,
    "Device Settings" to R.string.home_shortcut_name_device,
    "Display Settings" to R.string.home_shortcut_name_display,
    "Location Settings" to R.string.home_shortcut_name_location,
    "Sound Settings" to R.string.home_shortcut_name_sound,
    "Wi-Fi Settings" to R.string.home_shortcut_name_wifi
)

/** The shortcut's name in the user's language; the saved target (never translated) when it has no string. */
internal fun ShortcutAction.localizedName(resources: Resources): String =
    nameRes()?.let(resources::getString) ?: target.ifEmpty { displayName }

/** Where the saved "unavailable" shortcut and the [available] ones show in the list: [label], [app] and flag. */
internal fun defaultShortcutCandidates(
    options: List<ShortcutAction>,
    current: ShortcutAction,
    environment: ShortcutEnvironment
): List<ShortcutSearchCandidate> = options.mapNotNull { action ->
    val isSystem = action.type == ShortcutType.SYSTEM_INTENT
    val resolvedPackage = if (isSystem) environment.resolvePackage(action.target) else null
    val isAvailable = !isSystem || resolvedPackage != null
    // A saved shortcut the device cannot answer stays in the list, marked, so the choice does not vanish.
    if (!isAvailable && action != current) return@mapNotNull null
    val name = environment.nameOf(action)
    ShortcutSearchCandidate(
        action = action,
        label = if (isAvailable) name else environment.unavailableLabel(name),
        app = resolvedPackage?.let { pkg -> environment.apps.find { it.packageName == pkg } },
        isAvailable = isAvailable
    )
}

/** What the list of default shortcuts reads from the device: names, the app that answers a target, the apps. */
internal data class ShortcutEnvironment(
    val apps: List<AppInfo>,
    val nameOf: (ShortcutAction) -> String,
    val unavailableLabel: (String) -> String,
    val resolvePackage: (String) -> String?
)
