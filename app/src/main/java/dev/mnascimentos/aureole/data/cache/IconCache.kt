package dev.mnascimentos.aureole.data.cache

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

private const val DEFAULT_ICON_SIZE = 96
private const val MEMORY_CACHE_SIZE = 100
private const val PNG_QUALITY = 100

class IconCache(private val context: Context) {

    private val packageManager = context.packageManager
    private val memoryCache = LruCache<String, Drawable>(MEMORY_CACHE_SIZE)
    private val cacheDir = File(context.cacheDir, "app_icons").apply {
        if (!exists()) {
            mkdirs()
        }
    }

    fun getIcon(packageName: String, componentName: ComponentName): Drawable {
        val cached = memoryCache[packageName]
        if (cached != null) return cached

        val diskFile = getIconFile(packageName)
        var drawable: Drawable? = null
        if (diskFile.exists()) {
            try {
                val bitmap = BitmapFactory.decodeFile(diskFile.absolutePath)
                if (bitmap != null) {
                    drawable = BitmapDrawable(context.resources, bitmap)
                }
            } catch (_: Exception) {
                diskFile.delete()
            }
        }

        val finalDrawable = drawable ?: loadFromPackageManager(packageName, componentName).also {
            saveIconToDisk(packageName, it)
        }
        memoryCache.put(packageName, finalDrawable)
        return finalDrawable
    }

    fun cacheIcon(packageName: String, drawable: Drawable) {
        memoryCache.put(packageName, drawable)
        saveIconToDisk(packageName, drawable)
    }

    fun removeIcon(packageName: String) {
        memoryCache.remove(packageName)
        val diskFile = getIconFile(packageName)
        if (diskFile.exists()) {
            diskFile.delete()
        }
    }

    private fun getIconFile(packageName: String): File {
        return File(cacheDir, "$packageName.png")
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
            val bitmap = drawableToBitmap(drawable)
            val file = getIconFile(packageName)
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
