package dev.mnascimentos.aureole.core.data.repository

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import androidx.core.net.toUri
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.image.decodeWallpaperBitmap
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.resolveClockLayout
import dev.mnascimentos.aureole.core.designsystem.utils.HazeUtils
import dev.mnascimentos.aureole.core.image.BlurredWallpaperCache
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class SettingsRepository(
    private val context: Context,
    private val database: () -> AppDatabase = { AppDatabase.getInstance(context) }
) {

    /**
     * Ends the app process after a factory reset. Unit tests replace it so the reset
     * does not terminate the test JVM.
     */
    @VisibleForTesting
    internal var terminateProcess: () -> Unit = { Runtime.getRuntime().exit(0) }

    /** The device wallpaper; unit tests replace it so no real WallpaperManager call happens. */
    @VisibleForTesting
    internal var systemWallpaper: SystemWallpaper = PlatformSystemWallpaper(context)

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

    /** Whether the settings clock look has a card: a card color is saved (null is "None", RF-E2). */
    var isClockBackgroundEnabled: Boolean
        get() = clockBackgroundColorRef != null || clockBackgroundColor != 0
        set(value) = setClockCard(value)

    /** The clock layout key; an old style saved by 0.9.59 or older reads as its layout ([resolveClockLayout]). */
    var clockStyle: String
        get() = prefs.savedClockLayout().layout.key
        set(value) = prefs.edit { putString(KEY_CLOCK_STYLE, value) }

    /** What the message line shows; an old style with a greeting reads as its message ([resolveClockLayout]). */
    var clockMessage: String
        get() = prefs.savedClockLayout().message.key
        set(value) = prefs.edit { putString(KEY_CLOCK_MESSAGE, value) }

    var clockCustomGreeting: String
        get() = prefs.getString(KEY_CLOCK_CUSTOM_GREETING, "") ?: ""
        set(value) = prefs.edit { putString(KEY_CLOCK_CUSTOM_GREETING, value) }

    var clockAlignment: String
        get() = prefs.getString(KEY_CLOCK_ALIGNMENT, DEFAULT_CLOCK_ALIGNMENT) ?: DEFAULT_CLOCK_ALIGNMENT
        set(value) = prefs.edit { putString(KEY_CLOCK_ALIGNMENT, value) }

    var clockFontFamily: String
        get() = prefs.getString(KEY_CLOCK_FONT_FAMILY, DEFAULT_CLOCK_FONT_FAMILY) ?: DEFAULT_CLOCK_FONT_FAMILY
        set(value) = prefs.edit { putString(KEY_CLOCK_FONT_FAMILY, value) }

    var clockFontBold: Boolean
        get() = prefs.getBoolean(KEY_CLOCK_FONT_BOLD, DEFAULT_CLOCK_FONT_BOLD)
        set(value) = prefs.edit { putBoolean(KEY_CLOCK_FONT_BOLD, value) }

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
        get() = clockCardPrefs().getInt(KEY_CLOCK_BACKGROUND_COLOR, 0)
        set(value) = prefs.edit { putInt(KEY_CLOCK_BACKGROUND_COLOR, value) }

    /** ThemeColorRef JSON of the clock text color; null until one is picked (then [clockTextColor] rules). */
    var clockTextColorRef: String?
        get() = prefs.getString(KEY_CLOCK_TEXT_COLOR_REF, null)
        set(value) = prefs.edit { putString(KEY_CLOCK_TEXT_COLOR_REF, value) }

    var clockBackgroundColorRef: String?
        get() = clockCardPrefs().getString(KEY_CLOCK_BACKGROUND_COLOR_REF, null)
        set(value) = prefs.edit { putString(KEY_CLOCK_BACKGROUND_COLOR_REF, value) }

    fun resetClockSettings() {
        prefs.edit {
            remove(KEY_CLOCK_BACKGROUND_ENABLED)
            putString(KEY_CLOCK_STYLE, DEFAULT_CLOCK_STYLE)
            putString(KEY_CLOCK_CUSTOM_GREETING, "")
            remove(KEY_CLOCK_MESSAGE)
            putString(KEY_CLOCK_ALIGNMENT, DEFAULT_CLOCK_ALIGNMENT)
            putString(KEY_CLOCK_FONT_FAMILY, DEFAULT_CLOCK_FONT_FAMILY)
            putBoolean(KEY_CLOCK_FONT_BOLD, DEFAULT_CLOCK_FONT_BOLD)
            putString(KEY_CLOCK_TIME_FORMAT, DEFAULT_CLOCK_TIME_FORMAT)
            putString(KEY_CLOCK_DATE_FORMAT, DEFAULT_CLOCK_DATE_FORMAT)
            putInt(KEY_CLOCK_TEXT_COLOR, 0)
            putInt(KEY_CLOCK_BACKGROUND_COLOR, 0)
            remove(KEY_CLOCK_TEXT_COLOR_REF)
            remove(KEY_CLOCK_BACKGROUND_COLOR_REF)
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

    var isWidgetRowEnabled: Boolean
        get() = prefs.getBoolean(KEY_WIDGET_ROW_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_WIDGET_ROW_ENABLED, value) }

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

    /** Swiping down on the home opens the notification shade; on by default, also for updates (D-02). */
    var isSwipeDownNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SWIPE_DOWN_NOTIFICATIONS, DEFAULT_SWIPE_DOWN_NOTIFICATIONS)
        set(value) = prefs.edit { putBoolean(KEY_SWIPE_DOWN_NOTIFICATIONS, value) }

    /** Swiping up on the home opens the all apps drawer; on by default, also for updates. */
    var isSwipeUpAllAppsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SWIPE_UP_ALL_APPS, DEFAULT_SWIPE_UP_ALL_APPS)
        set(value) = prefs.edit { putBoolean(KEY_SWIPE_UP_ALL_APPS, value) }

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

    /** Name of the last glass level the user chose (kept while the blur is switched off). */
    var hazeBlurLevel: String
        get() = prefs.getString(KEY_HAZE_BLUR_LEVEL, DEFAULT_HAZE_BLUR_LEVEL) ?: DEFAULT_HAZE_BLUR_LEVEL
        set(value) = prefs.edit { putString(KEY_HAZE_BLUR_LEVEL, value) }

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

    /** "Also use on lock screen" (RF-13); off until the user turns it on. */
    var applyToLockScreen: Boolean
        get() = prefs.getBoolean(KEY_APPLY_TO_LOCK_SCREEN, false)
        set(value) = prefs.edit { putBoolean(KEY_APPLY_TO_LOCK_SCREEN, value) }

    /** Whether the one-time warning about the lock screen image (RF-15) was already shown. */
    var isLockScreenNoticeSeen: Boolean
        get() = prefs.getBoolean(KEY_LOCK_SCREEN_NOTICE_SEEN, false)
        set(value) = prefs.edit { putBoolean(KEY_LOCK_SCREEN_NOTICE_SEEN, value) }

    var wallpaperScaleType: String
        get() = prefs.getString(KEY_WALLPAPER_SCALE_TYPE, "Crop") ?: "Crop"
        set(value) = prefs.edit { putString(KEY_WALLPAPER_SCALE_TYPE, value) }

    /** How the custom photo is framed, dimmed and blurred; absent means the classic look (RF-11). */
    var wallpaperAdjustments: WallpaperAdjustments
        get() = WallpaperAdjustments.fromJson(prefs.getString(KEY_WALLPAPER_ADJUSTMENTS, null))
        set(value) = prefs.edit {
            val adjustments = value.coerced()
            if (adjustments.isDefault) {
                remove(KEY_WALLPAPER_ADJUSTMENTS)
            } else {
                putString(KEY_WALLPAPER_ADJUSTMENTS, adjustments.toJson())
            }
        }

    /**
     * Makes the blurred copy of the photo on Android 9-11 right after the editor saves (RF-09), so the home finds it
     * ready. Same radius as the home draws it with: the saved blur in px of this screen. Nothing to do on Android 12+,
     * which blurs live, or without a photo or a blur. Runs on the caller's thread: call it off the main one.
     */
    fun prepareBlurredWallpaper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return
        val path = customWallpaperPath?.takeIf { isCustomWallpaperSet && it.isNotEmpty() } ?: return
        val blurPx = (wallpaperAdjustments.blurDp * context.resources.displayMetrics.density).toInt()
        BlurredWallpaperCache.obtain(context, File(path), blurPx)
    }

    var lastSeenVersionCode: Int
        get() = prefs.getInt(KEY_LAST_SEEN_VERSION_CODE, 0)
        set(value) = prefs.edit { putInt(KEY_LAST_SEEN_VERSION_CODE, value) }

    val onboarding: OnboardingPreferences by lazy { OnboardingPreferences { prefs } }

    /** Puts the home background on the lock screen too, when [applyToLockScreen] is on. */
    val lockScreen: LockScreenWallpaper by lazy { LockScreenWallpaper(context, this) { systemWallpaper } }

    /** Keeps the custom wallpaper aside while a "Restore background" can still be undone. */
    val wallpaperStash: WallpaperUndoStash by lazy { WallpaperUndoStash(context, this) { systemWallpaper } }

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
            remove(KEY_WALLPAPER_ADJUSTMENTS)
        }
        BlurredWallpaperCache.clear(context)
    }

    fun saveCustomWallpaper(uriString: String): Boolean = runCatching { writeCustomWallpaper(uriString.toUri()) }
        .onFailure { Timber.tag(TAG).w(it, "Error in saveCustomWallpaper") }
        .getOrDefault(false)

    private fun writeCustomWallpaper(uri: Uri): Boolean {
        val screen = context.resources.displayMetrics
        val bitmap = decodeWallpaperBitmap(
            context.contentResolver,
            uri,
            target = maxOf(screen.widthPixels, screen.heightPixels)
        ) ?: return false
        val file = File(context.filesDir, "custom_wallpaper.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        bitmap.recycle()
        customWallpaperPath = file.absolutePath
        isCustomWallpaperSet = true
        wallpaperAdjustments = WallpaperAdjustments.DEFAULT
        BlurredWallpaperCache.clear(context)
        return true
    }

    fun checkDefaultLauncher(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = context.getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_HOME) ?: false
        } else {
            val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
            val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo?.activityInfo?.packageName == context.packageName
        }
    }

    fun restoreDefaultWallpaper() {
        clearCustomWallpaper()
        systemWallpaper.clear()
    }

    fun performFactoryReset() {
        runCatching { wipeLocalData() }
            .onFailure { Timber.tag(TAG).w(it, "Error performing factory reset") }

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
        if (intent != null) {
            context.startActivity(intent)
        }
        terminateProcess()
    }

    private fun wipeLocalData() {
        runCatching { database().clearAllTables() }
            .onFailure { Timber.tag(TAG).w(it, "Error clearing Room database") }
        context.deleteDatabase("aureole_app_database.db")
        context.deleteDatabase("aureole_folders.db")

        listOf(
            "aureole_grid_prefs",
            ClockConfigRepository.PREFS_NAME,
            "aureole_settings_prefs",
            "aureole_widget_prefs",
            "${context.packageName}_preferences"
        ).forEach { prefName ->
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit(commit = true) { clear() }
        }

        context.cacheDir.deleteRecursively()
        File(context.filesDir, "custom_wallpaper.jpg").delete()
        BlurredWallpaperCache.clear(context)
    }

    internal val prefs
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Best score of the hidden game; only [updateSecretBestScore] raises it. */
    val secretBestScore: Int
        get() = prefs.getInt(KEY_SECRET_BEST_SCORE, 0)

    /** Saves [score] when it beats the stored best; returns whether it did. */
    fun updateSecretBestScore(score: Int): Boolean {
        if (score <= secretBestScore) return false
        prefs.edit { putInt(KEY_SECRET_BEST_SCORE, score) }
        return true
    }

    /** Whether the hidden game was ever opened; anyone with a saved best score counts as having played. */
    var secretGamePlayed: Boolean
        get() = prefs.getBoolean(KEY_SECRET_GAME_PLAYED, secretBestScore > 0)
        set(value) = prefs.edit { putBoolean(KEY_SECRET_GAME_PLAYED, value) }

    /** Sound effects of the hidden game (on by default). */
    var secretSoundOn: Boolean
        get() = prefs.getBoolean(KEY_SECRET_SOUND_ON, true)
        set(value) = prefs.edit { putBoolean(KEY_SECRET_SOUND_ON, value) }

    companion object {
        private const val TAG = "SettingsRepository"
        private const val JPEG_QUALITY = 90
        private const val PREFS_NAME = "aureole_settings_prefs"
        private const val KEY_SECRET_BEST_SCORE = "secret_game_best_score"
        private const val KEY_SECRET_GAME_PLAYED = "secret_game_played"
        private const val KEY_SECRET_SOUND_ON = "secret_game_sound_on"
        private const val KEY_LEFT_HANDED_MODE = "left_handed_mode"
        private const val KEY_CONTAINER_ENABLED = "container_enabled"
        private const val KEY_CONTAINER_BACKGROUND_ENABLED = "container_background_enabled"
        private const val KEY_CONTAINER_EXPAND_CELL = "container_expand_cell"
        private const val KEY_CLOCK_BACKGROUND_ENABLED = "clock_background_enabled"
        private const val KEY_CLOCK_STYLE = "clock_style"
        private const val KEY_CLOCK_CUSTOM_GREETING = "clock_custom_greeting"
        private const val KEY_CLOCK_MESSAGE = "clock_message"

        private fun SharedPreferences.savedClockLayout() = resolveClockLayout(
            style = getString(KEY_CLOCK_STYLE, DEFAULT_CLOCK_STYLE),
            message = getString(KEY_CLOCK_MESSAGE, null)
        )

        private const val KEY_CLOCK_ALIGNMENT = "clock_alignment"
        private const val KEY_CLOCK_FONT_FAMILY = "clock_font_family"
        private const val KEY_CLOCK_FONT_BOLD = "clock_font_bold"
        private const val KEY_CLOCK_TIME_FORMAT = "clock_time_format"
        private const val KEY_CLOCK_DATE_FORMAT = "clock_date_format"
        private const val KEY_CLOCK_TEXT_COLOR = "clock_text_color"
        private const val KEY_CLOCK_TEXT_COLOR_REF = "clock_text_color_ref"
        private const val KEY_CLOCK_BACKGROUND_COLOR_REF = "clock_background_color_ref"
        private const val KEY_CLOCK_BACKGROUND_COLOR = "clock_background_color"

        const val DEFAULT_CLOCK_STYLE = ClockConfig.DEFAULT_STYLE
        const val DEFAULT_CLOCK_ALIGNMENT = ClockConfig.DEFAULT_ALIGNMENT
        const val DEFAULT_CLOCK_FONT_FAMILY = ClockConfig.DEFAULT_FONT_FAMILY
        const val DEFAULT_CLOCK_FONT_BOLD = ClockConfig.DEFAULT_FONT_BOLD
        const val DEFAULT_CLOCK_TIME_FORMAT = ClockConfig.DEFAULT_TIME_FORMAT
        const val DEFAULT_CLOCK_DATE_FORMAT = ClockConfig.DEFAULT_DATE_FORMAT
        private const val KEY_SHOW_CONTAINER_ADD_FOLDER_BUTTON = "show_container_add_folder_button"
        private const val KEY_CONTAINER_POSITION = "container_position"
        private const val KEY_SHOW_FOLDER_LABELS = "show_folder_labels"
        private const val KEY_FAVORITE_APPS = "favorite_apps"
        private const val KEY_HOME_OPENS_ALL_APPS = "home_opens_all_apps"
        private const val KEY_WIDGET_ROW_ENABLED = "widget_row_enabled"
        private const val KEY_IS_CUSTOM_WALLPAPER_SET = "is_custom_wallpaper_set"
        private const val KEY_CUSTOM_WALLPAPER_PATH = "custom_wallpaper_path"
        private const val KEY_WALLPAPER_SCALE_TYPE = "wallpaper_scale_type"
        private const val KEY_WALLPAPER_ADJUSTMENTS = "wallpaper_adjustments"
        private const val KEY_APPLY_TO_LOCK_SCREEN = "apply_to_lock_screen"
        private const val KEY_LOCK_SCREEN_NOTICE_SEEN = "lock_screen_notice_seen"
        private const val KEY_USE_WALLPAPER_COLORS = "use_wallpaper_colors"
        private const val KEY_MANUAL_SEED_COLOR = "manual_seed_color"
        private const val KEY_HAZE_ENABLED = "haze_enabled"
        private const val KEY_HAZE_OPACITY = "haze_opacity"
        private const val KEY_HAZE_BLUR_LEVEL = "haze_blur_level"
        private const val KEY_IN_APP_UPDATE_ENABLED = "in_app_update_enabled"
        private const val KEY_HEADER_OFFSET_PERCENT = "header_offset_percent"
        private const val KEY_CORNER_RADIUS_DP = "corner_radius_dp"
        private const val KEY_USE_THEMED_APP_ICONS = "use_themed_app_icons"
        private const val KEY_DISABLE_ALPHABET_SCRUBBER = "disable_alphabet_scrubber"
        private const val KEY_SWIPE_DOWN_NOTIFICATIONS = "swipe_down_notifications"
        const val DEFAULT_SWIPE_DOWN_NOTIFICATIONS = false
        private const val KEY_SWIPE_UP_ALL_APPS = "swipe_up_all_apps"
        const val DEFAULT_SWIPE_UP_ALL_APPS = false
        private const val KEY_SHOW_SETTINGS_BUTTON_IN_ALL_APPS = "show_settings_button_in_all_apps"
        private const val KEY_SETTINGS_BUTTON_POSITION = "settings_button_position"
        private const val KEY_SHOW_SEARCH_BAR_IN_ALL_APPS = "show_search_bar_in_all_apps"
        private const val KEY_SEARCH_ICON_POSITION = "search_icon_position"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_SELECTED_FONT = "selected_font"
        private const val KEY_LAST_SEEN_VERSION_CODE = "last_seen_version_code"
        private const val DEFAULT_HAZE_OPACITY = 0.5f
        private const val DEFAULT_HAZE_BLUR_LEVEL = "MEDIUM"
        private const val DEFAULT_HEADER_OFFSET = 10
        private const val MIN_HEADER_OFFSET = 0
        private const val MAX_HEADER_OFFSET = 60
        const val DEFAULT_CORNER_RADIUS_DP = 20
        const val DEFAULT_SEED_COLOR = 0xFF4A5D6B.toInt()
    }
}
