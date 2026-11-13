package dev.mnascimentos.aureole.core.data.cache

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppIconHelper
import dev.mnascimentos.aureole.core.data.model.FullBleedIconDrawable
import dev.mnascimentos.aureole.core.data.model.IconCacheNaming
import java.io.File
import java.io.FileOutputStream

private const val DEFAULT_ICON_SIZE = 96
private const val MEMORY_CACHE_SIZE = 100
private const val PNG_QUALITY = 100

class IconCache(private val context: Context) {

    private val packageManager = context.packageManager
    private val memoryCache = LruCache<String, Drawable>(MEMORY_CACHE_SIZE)
    private val cacheDir = File(context.cacheDir, IconCacheNaming.DIR_NAME).apply {
        File(context.cacheDir, IconCacheNaming.LEGACY_DIR_NAME).deleteRecursively()
        if (!exists()) {
            mkdirs()
        }
    }

    fun getIcon(packageName: String, componentName: ComponentName): Drawable {
        val cached = memoryCache[packageName]
        if (cached != null) return cached // Guard clause

        val drawable = loadDrawableFromDisk(packageName)

        val finalDrawable = drawable ?: loadFromPackageManager(packageName, componentName).also {
            saveIconToDisk(packageName, it)
        }
        memoryCache.put(packageName, finalDrawable)
        return finalDrawable
    }

    private fun loadDrawableFromDisk(packageName: String): Drawable? {
        val fullBleedFile = getIconFile(packageName, fullBleed = true)
        val legacyFile = getIconFile(packageName, fullBleed = false)
        return when {
            fullBleedFile.exists() -> decode(fullBleedFile) { FullBleedIconDrawable(context.resources, it) }
            legacyFile.exists() -> decode(legacyFile) { it.toDrawable(context.resources) }
            else -> null
        }
    }

    private fun decode(file: File, toDrawable: (Bitmap) -> Drawable): Drawable? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)?.let(toDrawable)
        } catch (_: Exception) {
            file.delete()
            null
        }
    }

    fun cacheIcon(packageName: String, drawable: Drawable) {
        memoryCache.put(packageName, drawable)
        saveIconToDisk(packageName, drawable)
    }

    fun removeIcon(packageName: String) {
        memoryCache.remove(packageName)
        getIconFile(packageName, fullBleed = true).delete()
        getIconFile(packageName, fullBleed = false).delete()
    }

    private fun getIconFile(packageName: String, fullBleed: Boolean): File {
        return File(cacheDir, IconCacheNaming.fileName(packageName, fullBleed))
    }

    private fun loadFromPackageManager(packageName: String, componentName: ComponentName): Drawable {
        return try {
            packageManager.getActivityIcon(componentName)
        } catch (_: Exception) {
            try {
                packageManager.getApplicationIcon(packageName)
            } catch (_: Exception) {
                packageManager.defaultActivityIcon
            }
        }
    }

    private fun saveIconToDisk(packageName: String, drawable: Drawable) {
        try {
            val fullBleed = drawable is AdaptiveIconDrawable || drawable is FullBleedIconDrawable
            val bitmap = if (drawable is AdaptiveIconDrawable) {
                AppIconHelper.fullBleedBitmap(drawable)
            } else {
                drawableToBitmap(drawable)
            }
            getIconFile(packageName, !fullBleed).delete()
            val file = getIconFile(packageName, fullBleed)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, out)
            }
        } catch (_: Exception) {
            // Ignore disk write errors
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if ((drawable is BitmapDrawable) && (drawable.bitmap != null)) {
            return drawable.bitmap
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else DEFAULT_ICON_SIZE
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else DEFAULT_ICON_SIZE
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
