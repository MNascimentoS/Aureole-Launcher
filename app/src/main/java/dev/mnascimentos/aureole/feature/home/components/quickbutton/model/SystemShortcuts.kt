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

/**
 * The intent a system shortcut [target] ("Calendar", "Wi-Fi Settings"...) opens, or null for an unknown target:
 * an unknown target never falls back to Settings. Each one is the most specific intent for what it names.
 */
internal fun systemIntentFor(target: String): Intent? {
    val category = when (target) {
        "Browser" -> Intent.CATEGORY_APP_BROWSER
        "Calendar" -> Intent.CATEGORY_APP_CALENDAR
        "Calculator" -> Intent.CATEGORY_APP_CALCULATOR
        "Contacts" -> Intent.CATEGORY_APP_CONTACTS
        "Email" -> Intent.CATEGORY_APP_EMAIL
        "Messages" -> Intent.CATEGORY_APP_MESSAGING
        "Music Player" -> Intent.CATEGORY_APP_MUSIC
        else -> null
    }
    if (category != null) return Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, category)
    return when (target) {
        "Camera" -> Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        "Clock" -> Intent(AlarmClock.ACTION_SHOW_ALARMS)
        "Dialer" -> Intent(Intent.ACTION_DIAL)
        "Search" -> Intent(Intent.ACTION_WEB_SEARCH)
        "Settings" -> Intent(Settings.ACTION_SETTINGS)
        "Files" -> Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*")
        else -> settingsPageIntentFor(target)
    }
}

private fun settingsPageIntentFor(target: String): Intent? = when (target) {
    "Battery Settings" -> Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
    "Bluetooth Settings" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
    "Device Settings" -> Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
    "Display Settings" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
    "Location Settings" -> Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    "Sound Settings" -> Intent(Settings.ACTION_SOUND_SETTINGS)
    "Wi-Fi Settings" -> Intent(Settings.ACTION_WIFI_SETTINGS)
    else -> null
}

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
    ShortcutType.SYSTEM_INTENT -> systemNameRes(target)
}

@StringRes
private fun systemNameRes(target: String): Int? = when (target) {
    "Browser" -> R.string.home_shortcut_name_browser
    "Calculator" -> R.string.home_shortcut_name_calculator
    "Calendar" -> R.string.home_shortcut_name_calendar
    "Camera" -> R.string.home_shortcut_name_camera
    "Clock" -> R.string.home_shortcut_name_clock
    "Contacts" -> R.string.home_shortcut_name_contacts
    "Dialer" -> R.string.home_shortcut_name_dialer
    "Email" -> R.string.home_shortcut_name_email
    "Search" -> R.string.home_shortcut_name_search
    "Settings" -> R.string.home_shortcut_name_settings
    "Files" -> R.string.home_shortcut_name_files
    "Messages" -> R.string.home_shortcut_name_messages
    "Music Player" -> R.string.home_shortcut_name_music
    "Battery Settings" -> R.string.home_shortcut_name_battery
    "Bluetooth Settings" -> R.string.home_shortcut_name_bluetooth
    "Device Settings" -> R.string.home_shortcut_name_device
    "Display Settings" -> R.string.home_shortcut_name_display
    "Location Settings" -> R.string.home_shortcut_name_location
    "Sound Settings" -> R.string.home_shortcut_name_sound
    "Wi-Fi Settings" -> R.string.home_shortcut_name_wifi
    else -> null
}

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
