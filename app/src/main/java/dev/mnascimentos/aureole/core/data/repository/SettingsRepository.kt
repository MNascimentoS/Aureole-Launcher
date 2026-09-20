package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.os.Build
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.designsystem.theme.HazeUtils
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

    var showFolderLabels: Boolean
        get() = prefs.getBoolean(KEY_SHOW_FOLDER_LABELS, false)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_FOLDER_LABELS, value) }

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

    var isWidgetRowEnabled: Boolean
        get() = prefs.getBoolean(KEY_WIDGET_ROW_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_WIDGET_ROW_ENABLED, value) }

    var showWidgetDots: Boolean
        get() = prefs.getBoolean(KEY_SHOW_WIDGET_DOTS, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_WIDGET_DOTS, value) }

    var isInAppUpdateEnabled: Boolean
        get() = prefs.getBoolean(KEY_IN_APP_UPDATE_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_IN_APP_UPDATE_ENABLED, value) }

    val isHazeSupported: Boolean
        get() = HazeUtils.isDeviceHazeSupported(context)

    var isHazeEnabled: Boolean
        get() {
            if (!isHazeSupported) return false
            return prefs.getBoolean(KEY_HAZE_ENABLED, true)
        }
        set(value) = prefs.edit { putBoolean(KEY_HAZE_ENABLED, value) }

    var hazeOpacity: Float
        get() = prefs.getFloat(KEY_HAZE_OPACITY, DEFAULT_HAZE_OPACITY)
        set(value) = prefs.edit { putFloat(KEY_HAZE_OPACITY, value) }

    var isDynamicWallpaperEnabled: Boolean
        get() = prefs.getBoolean(KEY_USE_WALLPAPER_COLORS, Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        set(value) = prefs.edit { putBoolean(KEY_USE_WALLPAPER_COLORS, value) }

    var manualSeedColor: Int
        get() = prefs.getInt(KEY_MANUAL_SEED_COLOR, DEFAULT_SEED_COLOR)
        set(value) = prefs.edit { putInt(KEY_MANUAL_SEED_COLOR, value) }

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
        private const val KEY_SHOW_FOLDER_LABELS = "show_folder_labels"
        private const val KEY_FAVORITE_APPS = "favorite_apps"
        private const val KEY_HOME_OPENS_ALL_APPS = "home_opens_all_apps"
        private const val KEY_SHOW_ALL_APPS_ON_HOME = "show_all_apps_on_home"
        private const val KEY_WIDGET_ROW_ENABLED = "widget_row_enabled"
        private const val KEY_IS_CUSTOM_WALLPAPER_SET = "is_custom_wallpaper_set"
        private const val KEY_CUSTOM_WALLPAPER_PATH = "custom_wallpaper_path"
        private const val KEY_USE_WALLPAPER_COLORS = "use_wallpaper_colors"
        private const val KEY_MANUAL_SEED_COLOR = "manual_seed_color"
        private const val KEY_HAZE_ENABLED = "haze_enabled"
        private const val KEY_HAZE_OPACITY = "haze_opacity"
        private const val KEY_SHOW_WIDGET_DOTS = "show_widget_dots"
        private const val KEY_IN_APP_UPDATE_ENABLED = "in_app_update_enabled"
        private const val DEFAULT_HAZE_OPACITY = 0.5f
        const val DEFAULT_SEED_COLOR = 0xFF6650A4.toInt()
    }
}
