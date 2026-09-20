package dev.mnascimentos.aureole.data.repository

import android.content.Context
import androidx.core.content.edit
import java.io.File

class SettingsRepository(private val context: Context) {

    var isLeftHandedMode: Boolean
        get() = prefs.getBoolean(KEY_LEFT_HANDED_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_LEFT_HANDED_MODE, value) }

    var isSidePanelEnabled: Boolean
        get() = prefs.getBoolean(KEY_SIDE_PANEL_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_SIDE_PANEL_ENABLED, value) }

    var sidePanelPosition: String
        get() = prefs.getString(KEY_SIDE_PANEL_POSITION, "Center") ?: "Center"
        set(value) = prefs.edit { putString(KEY_SIDE_PANEL_POSITION, value) }

    var favoriteAppPackages: List<String>
        get() {
            val saved = prefs.getString(KEY_FAVORITE_APPS, "") ?: ""
            return saved.split(",").filter { it.isNotBlank() }
        }
        set(value) = prefs.edit { putString(KEY_FAVORITE_APPS, value.joinToString(",")) }

    var homeButtonOpensAllApps: Boolean
        get() = prefs.getBoolean(KEY_HOME_OPENS_ALL_APPS, true)
        set(value) = prefs.edit { putBoolean(KEY_HOME_OPENS_ALL_APPS, value) }

    var showAllAppsOnHome: Boolean
        get() = prefs.getBoolean(KEY_SHOW_ALL_APPS_ON_HOME, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_ALL_APPS_ON_HOME, value) }

    var isCustomWallpaperSet: Boolean
        get() {
            val isSet = prefs.getBoolean(KEY_IS_CUSTOM_WALLPAPER_SET, false)
            val path = customWallpaperPath
            return isSet && !path.isNullOrEmpty() && File(path).exists()
        }
        set(value) = prefs.edit { putBoolean(KEY_IS_CUSTOM_WALLPAPER_SET, value) }

    var customWallpaperPath: String?
        get() = prefs.getString(KEY_CUSTOM_WALLPAPER_PATH, null)
        set(value) = prefs.edit { putString(KEY_CUSTOM_WALLPAPER_PATH, value) }

    fun clearCustomWallpaper() {
        val path = customWallpaperPath
        if (!path.isNullOrEmpty()) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
        prefs.edit {
            putBoolean(KEY_IS_CUSTOM_WALLPAPER_SET, false)
            remove(KEY_CUSTOM_WALLPAPER_PATH)
        }
    }

    private val prefs
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "aureole_settings_prefs"
        private const val KEY_LEFT_HANDED_MODE = "left_handed_mode"
        private const val KEY_SIDE_PANEL_ENABLED = "side_panel_enabled"
        private const val KEY_SIDE_PANEL_POSITION = "side_panel_position"
        private const val KEY_FAVORITE_APPS = "favorite_apps"
        private const val KEY_HOME_OPENS_ALL_APPS = "home_opens_all_apps"
        private const val KEY_SHOW_ALL_APPS_ON_HOME = "show_all_apps_on_home"
        private const val KEY_IS_CUSTOM_WALLPAPER_SET = "is_custom_wallpaper_set"
        private const val KEY_CUSTOM_WALLPAPER_PATH = "custom_wallpaper_path"
    }
}
