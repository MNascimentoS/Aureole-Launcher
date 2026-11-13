package dev.mnascimentos.aureole.core.data.repository

import android.app.WallpaperManager
import android.content.Context
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException

private const val TAG = "SystemWallpaper"

/**
 * The device wallpaper (home screen / system). Wrapped so unit tests never reach the real WallpaperManager.
 * Every call is best effort: it returns false instead of throwing when the device refuses.
 */
interface SystemWallpaper {
    /** Gives the system wallpaper back to the device default. */
    fun clear(): Boolean

    /** Copies the current system wallpaper into [target]. False when it could not be read. */
    fun save(target: File): Boolean

    /** Sets [source] as the system wallpaper again. False when it could not be applied. */
    fun apply(source: File): Boolean
}

class PlatformSystemWallpaper(private val context: Context) : SystemWallpaper {
    private val manager get() = WallpaperManager.getInstance(context)

    override fun clear(): Boolean = guarded("clearing") {
        manager.clear(WallpaperManager.FLAG_SYSTEM)
        true
    }

    override fun save(target: File): Boolean = guarded("reading") {
        val descriptor = manager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM) ?: return@guarded false
        descriptor.use { fd ->
            FileInputStream(fd.fileDescriptor).use { input ->
                target.outputStream().use { input.copyTo(it) }
            }
        }
        true
    }

    override fun apply(source: File): Boolean = guarded("setting") {
        if (!source.exists()) return@guarded false
        source.inputStream().use { manager.setStream(it, null, true, WallpaperManager.FLAG_SYSTEM) }
        true
    }

    private inline fun guarded(action: String, block: () -> Boolean): Boolean = try {
        block()
    } catch (e: IOException) {
        Timber.tag(TAG).w(e, "IOException $action wallpaper")
        false
    } catch (e: SecurityException) {
        Timber.tag(TAG).w(e, "SecurityException $action wallpaper")
        false
    }
}
