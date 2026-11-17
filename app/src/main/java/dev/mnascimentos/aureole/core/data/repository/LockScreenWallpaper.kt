package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.view.WindowManager
import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.image.LockRenderParams
import dev.mnascimentos.aureole.core.image.WallpaperRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

private const val TAG = "LockScreenWallpaper"
private const val PNG_QUALITY = 100

/**
 * Puts the home background (without the apps) on the Android lock screen when the user turned
 * [SettingsRepository.applyToLockScreen] on (RF-13, RF-14). Bitmaps are drawn in `Dispatchers.Default` and handed to
 * the system in `Dispatchers.IO`; every call is best effort and reports failure with `false`, never by throwing.
 * The last image applied lives in `lock_wallpaper.png`; the one before it is kept in [WallpaperUndoStash] for Undo.
 */
class LockScreenWallpaper(
    private val context: Context,
    private val settings: SettingsRepository,
    private val systemWallpaper: () -> SystemWallpaper
) {
    private val lock = Mutex()
    private val stash get() = settings.wallpaperStash

    /** False when the device does not let apps change the wallpaper; the switch is then off and disabled. */
    fun canSet(): Boolean = systemWallpaper().canSetWallpaper()

    /** Draws the current background and applies it, when the lock screen option is on. True also when it is off. */
    suspend fun applyCurrent(): Boolean = lock.withLock {
        if (!settings.applyToLockScreen) return@withLock true
        val bitmap = withContext(Dispatchers.Default) { renderCurrent() } ?: return@withLock false
        applyAndRemember(bitmap, keepPrevious = true)
    }

    /**
     * Undo: puts back the image applied before the last change, or redraws the (already restored) background when
     * there was none. True also when the lock screen option is off.
     */
    suspend fun applyPrevious(): Boolean = lock.withLock {
        if (!settings.applyToLockScreen) return@withLock true
        val previous = withContext(Dispatchers.IO) { stash.takeLockImageBack() }
        val bitmap = withContext(Dispatchers.Default) {
            previous?.let { BitmapFactory.decodeFile(it.absolutePath) } ?: renderCurrent()
        } ?: return@withLock false
        applyAndRemember(bitmap, keepPrevious = false)
    }

    /** "Back to system wallpaper": the lock screen follows the system wallpaper again. */
    suspend fun clear(): Boolean = lock.withLock {
        withContext(Dispatchers.IO) {
            systemWallpaper().clearLock().also { if (it) stash.lockImageFile.delete() }
        }
    }

    private suspend fun applyAndRemember(bitmap: Bitmap, keepPrevious: Boolean): Boolean =
        withContext(Dispatchers.IO) {
            if (keepPrevious) stash.keepLockImage()
            val applied = systemWallpaper().applyLock(bitmap)
            if (applied) saveImage(bitmap)
            bitmap.recycle()
            applied
        }

    private fun saveImage(bitmap: Bitmap) {
        runCatching {
            FileOutputStream(stash.lockImageFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, it) }
        }.onFailure { Timber.tag(TAG).w(it, "Could not keep the lock screen image") }
    }

    /** What the home shows behind the apps now, in the size of the screen in portrait; null when it cannot be drawn. */
    private fun renderCurrent(): Bitmap? = runCatching {
        val (width, height) = portraitScreenSize(context)
        val background = homeBackgroundColor()
        val path = settings.customWallpaperPath?.takeIf { settings.isCustomWallpaperSet && it.isNotEmpty() }
        if (path == null) {
            WallpaperRenderer.renderSolid(background, width, height)
        } else {
            val photo = BitmapFactory.decodeFile(path) ?: return@runCatching null
            val params = LockRenderParams(
                adjustments = settings.wallpaperAdjustments,
                width = width,
                height = height,
                density = context.resources.displayMetrics.density,
                scaleType = settings.wallpaperScaleType,
                backgroundColor = background
            )
            WallpaperRenderer.renderLockBitmap(photo, params).also { photo.recycle() }
        }
    }.onFailure { Timber.tag(TAG).w(it, "Could not draw the lock screen image") }.getOrNull()

    /** The color the home paints without a photo: the palette's background, in the current light or dark mode. */
    private fun homeBackgroundColor(): Int {
        val night = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDark = night == Configuration.UI_MODE_NIGHT_YES
        return getPaletteByName(settings.selectedThemeName, isDark, settings.manualSeedColor).colors.background.toArgb()
    }
}

/** The screen as (width, height) in px with the short side first, like a phone held upright. */
internal fun portraitScreenSize(context: Context): Pair<Int, Int> {
    val bounds = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.getSystemService(WindowManager::class.java).maximumWindowMetrics.bounds
            .let { it.width() to it.height() }
    } else {
        context.resources.displayMetrics.let { it.widthPixels to it.heightPixels }
    }
    return min(bounds.first, bounds.second) to max(bounds.first, bounds.second)
}
