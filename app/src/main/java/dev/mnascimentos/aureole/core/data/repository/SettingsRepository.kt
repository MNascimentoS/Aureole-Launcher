package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.designsystem.utils.HazeUtils
import java.io.File

class SettingsRepository(private val context: Context) {

    var isLeftHandedMode: Boolean
        get() = prefs.getBoolean(KEY_LEFT_HANDED_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_LEFT_HANDED_MODE, value) }

    var isContainerEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONTAINER_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_CONTAINER_ENABLED, value) }

    var isContainerBackgroundEnabled: Boolean
        get() = prefs.getBoolean(KEY_CONTAINER_BACKGROUND_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_CONTAINER_BACKGROUND_ENABLED, value) }

    var isContainerExpandCell: Boolean
        get() = prefs.getBoolean(KEY_CONTAINER_EXPAND_CELL, false)
        set(value) = prefs.edit { putBoolean(KEY_CONTAINER_EXPAND_CELL, value) }

    var isClockBackgroundEnabled: Boolean
        get() = prefs.getBoolean(KEY_CLOCK_BACKGROUND_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_CLOCK_BACKGROUND_ENABLED, value) }

    var clockStyle: String
        get() = prefs.getString(KEY_CLOCK_STYLE, DEFAULT_CLOCK_STYLE) ?: DEFAULT_CLOCK_STYLE
        set(value) = prefs.edit { putString(KEY_CLOCK_STYLE, value) }

    var clockCustomGreeting: String
        get() = prefs.getString(KEY_CLOCK_CUSTOM_GREETING, "") ?: ""
        set(value) = prefs.edit { putString(KEY_CLOCK_CUSTOM_GREETING, value) }

    var clockAlignment: String
        get() = prefs.getString(KEY_CLOCK_ALIGNMENT, DEFAULT_CLOCK_ALIGNMENT) ?: DEFAULT_CLOCK_ALIGNMENT
        set(value) = prefs.edit { putString(KEY_CLOCK_ALIGNMENT, value) }

    var clockFontFamily: String
        get() = prefs.getString(KEY_CLOCK_FONT_FAMILY, DEFAULT_CLOCK_FONT_FAMILY) ?: DEFAULT_CLOCK_FONT_FAMILY
        set(value) = prefs.edit { putString(KEY_CLOCK_FONT_FAMILY, value) }

    var clockTimeFormat: String
        get() = prefs.getString(KEY_CLOCK_TIME_FORMAT, DEFAULT_CLOCK_TIME_FORMAT) ?: DEFAULT_CLOCK_TIME_FORMAT
        set(value) = prefs.edit { putString(KEY_CLOCK_TIME_FORMAT, value) }

    var clockDateFormat: String
        get() = prefs.getString(KEY_CLOCK_DATE_FORMAT, DEFAULT_CLOCK_DATE_FORMAT) ?: DEFAULT_CLOCK_DATE_FORMAT
        set(value) = prefs.edit { putString(KEY_CLOCK_DATE_FORMAT, value) }

    var clockTextColor: Int
        get() = prefs.getInt(KEY_CLOCK_TEXT_COLOR, 0)
        set(value) = prefs.edit { putInt(KEY_CLOCK_TEXT_COLOR, value) }

    var clockBackgroundColor: Int
        get() = prefs.getInt(KEY_CLOCK_BACKGROUND_COLOR, 0)
        set(value) = prefs.edit { putInt(KEY_CLOCK_BACKGROUND_COLOR, value) }

    fun resetClockSettings() {
        prefs.edit {
            putBoolean(KEY_CLOCK_BACKGROUND_ENABLED, true)
            putString(KEY_CLOCK_STYLE, DEFAULT_CLOCK_STYLE)
            putString(KEY_CLOCK_CUSTOM_GREETING, "")
            putString(KEY_CLOCK_ALIGNMENT, DEFAULT_CLOCK_ALIGNMENT)
            putString(KEY_CLOCK_FONT_FAMILY, DEFAULT_CLOCK_FONT_FAMILY)
            putString(KEY_CLOCK_TIME_FORMAT, DEFAULT_CLOCK_TIME_FORMAT)
            putString(KEY_CLOCK_DATE_FORMAT, DEFAULT_CLOCK_DATE_FORMAT)
            putInt(KEY_CLOCK_TEXT_COLOR, 0)
            putInt(KEY_CLOCK_BACKGROUND_COLOR, 0)
        }
    }

    var showContainerAddFolderButton: Boolean
        get() = prefs.getBoolean(KEY_SHOW_CONTAINER_ADD_FOLDER_BUTTON, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_CONTAINER_ADD_FOLDER_BUTTON, value) }

    var containerPosition: String
        get() = prefs.getString(KEY_CONTAINER_POSITION, "Center") ?: "Center"
        set(value) = prefs.edit { putString(KEY_CONTAINER_POSITION, value) }

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

    var headerOffsetPercent: Int
        get() = prefs.getInt(KEY_HEADER_OFFSET_PERCENT, DEFAULT_HEADER_OFFSET)
            .coerceIn(MIN_HEADER_OFFSET, MAX_HEADER_OFFSET)
        set(value) = prefs.edit {
            putInt(KEY_HEADER_OFFSET_PERCENT, value.coerceIn(MIN_HEADER_OFFSET, MAX_HEADER_OFFSET))
        }

    var cornerRadiusDp: Int
        get() = prefs.getInt(KEY_CORNER_RADIUS_DP, DEFAULT_CORNER_RADIUS_DP)
        set(value) = prefs.edit { putInt(KEY_CORNER_RADIUS_DP, value) }

    var isThemedAppIconsEnabled: Boolean
        get() = prefs.getBoolean(KEY_USE_THEMED_APP_ICONS, false)
        set(value) = prefs.edit { putBoolean(KEY_USE_THEMED_APP_ICONS, value) }

    var isAlphabetScrubberDisabled: Boolean
        get() = prefs.getBoolean(KEY_DISABLE_ALPHABET_SCRUBBER, false)
        set(value) = prefs.edit { putBoolean(KEY_DISABLE_ALPHABET_SCRUBBER, value) }

    var showSettingsButtonInAllApps: Boolean
        get() = prefs.getBoolean(KEY_SHOW_SETTINGS_BUTTON_IN_ALL_APPS, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_SETTINGS_BUTTON_IN_ALL_APPS, value) }

    var settingsButtonPosition: String
        get() = prefs.getString(KEY_SETTINGS_BUTTON_POSITION, "Right") ?: "Right"
        set(value) = prefs.edit { putString(KEY_SETTINGS_BUTTON_POSITION, value) }

    var showSearchBarInAllApps: Boolean
        get() = prefs.getBoolean(KEY_SHOW_SEARCH_BAR_IN_ALL_APPS, true)
        set(value) = prefs.edit { putBoolean(KEY_SHOW_SEARCH_BAR_IN_ALL_APPS, value) }

    var searchIconPosition: String
        get() = prefs.getString(KEY_SEARCH_ICON_POSITION, "Left") ?: "Left"
        set(value) = prefs.edit { putString(KEY_SEARCH_ICON_POSITION, value) }

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
        get() = prefs.getBoolean(KEY_USE_WALLPAPER_COLORS, false)
        set(value) = prefs.edit { putBoolean(KEY_USE_WALLPAPER_COLORS, value) }

    var manualSeedColor: Int
        get() = prefs.getInt(KEY_MANUAL_SEED_COLOR, DEFAULT_SEED_COLOR)
        set(value) = prefs.edit { putInt(KEY_MANUAL_SEED_COLOR, value) }

    var selectedThemeName: String
        get() = prefs.getString(KEY_SELECTED_THEME, "Frostbite") ?: "Frostbite"
        set(value) = prefs.edit { putString(KEY_SELECTED_THEME, value) }

    var selectedFontName: String
        get() = prefs.getString(KEY_SELECTED_FONT, "Istok Web") ?: "Istok Web"
        set(value) = prefs.edit { putString(KEY_SELECTED_FONT, value) }

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

    var wallpaperScaleType: String
        get() = prefs.getString(KEY_WALLPAPER_SCALE_TYPE, "Crop") ?: "Crop"
        set(value) = prefs.edit { putString(KEY_WALLPAPER_SCALE_TYPE, value) }

    var lastSeenVersionCode: Int
        get() = prefs.getInt(KEY_LAST_SEEN_VERSION_CODE, 0)
        set(value) = prefs.edit { putInt(KEY_LAST_SEEN_VERSION_CODE, value) }

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
        private const val KEY_CONTAINER_ENABLED = "container_enabled"
        private const val KEY_CONTAINER_BACKGROUND_ENABLED = "container_background_enabled"
        private const val KEY_CONTAINER_EXPAND_CELL = "container_expand_cell"
        private const val KEY_CLOCK_BACKGROUND_ENABLED = "clock_background_enabled"
        private const val KEY_CLOCK_STYLE = "clock_style"
        private const val KEY_CLOCK_CUSTOM_GREETING = "clock_custom_greeting"
        private const val KEY_CLOCK_ALIGNMENT = "clock_alignment"
        private const val KEY_CLOCK_FONT_FAMILY = "clock_font_family"
        private const val KEY_CLOCK_TIME_FORMAT = "clock_time_format"
        private const val KEY_CLOCK_DATE_FORMAT = "clock_date_format"
        private const val KEY_CLOCK_TEXT_COLOR = "clock_text_color"
        private const val KEY_CLOCK_BACKGROUND_COLOR = "clock_background_color"

        const val DEFAULT_CLOCK_STYLE = "DYNAMIC_GREETING"
        const val DEFAULT_CLOCK_ALIGNMENT = "START"
        const val DEFAULT_CLOCK_FONT_FAMILY = "SANS_SERIF"
        const val DEFAULT_CLOCK_TIME_FORMAT = "SYSTEM"
        const val DEFAULT_CLOCK_DATE_FORMAT = "DEFAULT"
        private const val KEY_SHOW_CONTAINER_ADD_FOLDER_BUTTON = "show_container_add_folder_button"
        private const val KEY_CONTAINER_POSITION = "container_position"
        private const val KEY_SHOW_FOLDER_LABELS = "show_folder_labels"
        private const val KEY_FAVORITE_APPS = "favorite_apps"
        private const val KEY_HOME_OPENS_ALL_APPS = "home_opens_all_apps"
        private const val KEY_SHOW_ALL_APPS_ON_HOME = "show_all_apps_on_home"
        private const val KEY_WIDGET_ROW_ENABLED = "widget_row_enabled"
        private const val KEY_IS_CUSTOM_WALLPAPER_SET = "is_custom_wallpaper_set"
        private const val KEY_CUSTOM_WALLPAPER_PATH = "custom_wallpaper_path"
        private const val KEY_WALLPAPER_SCALE_TYPE = "wallpaper_scale_type"
        private const val KEY_USE_WALLPAPER_COLORS = "use_wallpaper_colors"
        private const val KEY_MANUAL_SEED_COLOR = "manual_seed_color"
        private const val KEY_HAZE_ENABLED = "haze_enabled"
        private const val KEY_HAZE_OPACITY = "haze_opacity"
        private const val KEY_SHOW_WIDGET_DOTS = "show_widget_dots"
        private const val KEY_IN_APP_UPDATE_ENABLED = "in_app_update_enabled"
        private const val KEY_HEADER_OFFSET_PERCENT = "header_offset_percent"
        private const val KEY_CORNER_RADIUS_DP = "corner_radius_dp"
        private const val KEY_USE_THEMED_APP_ICONS = "use_themed_app_icons"
        private const val KEY_DISABLE_ALPHABET_SCRUBBER = "disable_alphabet_scrubber"
        private const val KEY_SHOW_SETTINGS_BUTTON_IN_ALL_APPS = "show_settings_button_in_all_apps"
        private const val KEY_SETTINGS_BUTTON_POSITION = "settings_button_position"
        private const val KEY_SHOW_SEARCH_BAR_IN_ALL_APPS = "show_search_bar_in_all_apps"
        private const val KEY_SEARCH_ICON_POSITION = "search_icon_position"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_SELECTED_FONT = "selected_font"
        private const val KEY_LAST_SEEN_VERSION_CODE = "last_seen_version_code"
        private const val DEFAULT_HAZE_OPACITY = 0.5f
        private const val DEFAULT_HEADER_OFFSET = 10
        private const val MIN_HEADER_OFFSET = 0
        private const val MAX_HEADER_OFFSET = 60
        const val DEFAULT_CORNER_RADIUS_DP = 20
        const val DEFAULT_SEED_COLOR = 0xFF4A5D6B.toInt()
    }
}
