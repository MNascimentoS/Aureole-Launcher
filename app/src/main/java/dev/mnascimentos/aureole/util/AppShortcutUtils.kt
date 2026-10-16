package dev.mnascimentos.aureole.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

data class AureoleShortcutItem(
    val id: String,
    val label: String,
    val icon: Drawable?,
    val shortcutInfo: ShortcutInfo
)

object AppShortcutUtils {
    private const val TAG = "AppShortcutUtils"
    private const val MAX_SHORTCUTS = 6

    fun getAppShortcuts(context: Context, packageName: String): List<AureoleShortcutItem> {
        val launcherApps = try {
            context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
        } catch (e: AssertionError) {
            Log.w(TAG, "Unsupported service in preview", e)
            null
        } ?: return emptyList()

        val shortcuts = queryShortcutsForPackage(launcherApps, packageName)
        val density = context.resources.displayMetrics.densityDpi

        return shortcuts.take(MAX_SHORTCUTS).map { shortcut ->
            val label = (shortcut.shortLabel ?: shortcut.longLabel ?: shortcut.id).toString()
            val iconDrawable = try {
                launcherApps.getShortcutIconDrawable(shortcut, density)
            } catch (e: SecurityException) {
                Log.w(TAG, "Security exception fetching shortcut icon", e)
                null
            } catch (e: IllegalStateException) {
                Log.w(TAG, "Illegal state fetching shortcut icon", e)
                null
            }

            AureoleShortcutItem(
                id = shortcut.id,
                label = label,
                icon = iconDrawable,
                shortcutInfo = shortcut
            )
        }
    }

    private fun queryShortcutsForPackage(
        launcherApps: LauncherApps,
        packageName: String
    ): List<ShortcutInfo> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val cachedQuery = LauncherApps.ShortcutQuery().apply {
                    setQueryFlags(
                        LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                            LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                            LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED or
                            LauncherApps.ShortcutQuery.FLAG_MATCH_CACHED
                    )
                    setPackage(packageName)
                }
                val results = launcherApps.getShortcuts(cachedQuery, Process.myUserHandle())
                if (!results.isNullOrEmpty()) return results
            } catch (e: SecurityException) {
                Log.w(TAG, "Security error in cached query for $packageName", e)
            } catch (e: IllegalStateException) {
                Log.w(TAG, "Illegal state in cached query for $packageName", e)
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "Illegal argument in cached query for $packageName", e)
            }
        }

        val standardQuery = LauncherApps.ShortcutQuery().apply {
            setQueryFlags(
                LauncherApps.ShortcutQuery.FLAG_MATCH_DYNAMIC or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_MANIFEST or
                    LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED
            )
            setPackage(packageName)
        }

        return try {
            launcherApps.getShortcuts(standardQuery, Process.myUserHandle()) ?: emptyList()
        } catch (e: SecurityException) {
            Log.w(TAG, "Security error in standard query for $packageName", e)
            emptyList()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Illegal state in standard query for $packageName", e)
            emptyList()
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Illegal argument in standard query for $packageName", e)
            emptyList()
        }
    }

    fun launchShortcut(context: Context, shortcut: AureoleShortcutItem) {
        val launcherApps =
            context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps ?: return
        try {
            launcherApps.startShortcut(shortcut.shortcutInfo, null, null)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Error launching shortcut: ${shortcut.id} (not found)", e)
        } catch (e: SecurityException) {
            Log.e(TAG, "Security error launching shortcut: ${shortcut.id}, trying fallback", e)
            try {
                launcherApps.startShortcut(
                    shortcut.shortcutInfo.`package`,
                    shortcut.shortcutInfo.id,
                    null,
                    null,
                    shortcut.shortcutInfo.userHandle
                )
            } catch (e2: SecurityException) {
                Log.e(TAG, "Fallback launchShortcut security failed: ${shortcut.id}", e2)
            } catch (e2: IllegalStateException) {
                Log.e(TAG, "Fallback launchShortcut illegal state failed: ${shortcut.id}", e2)
            } catch (e2: ActivityNotFoundException) {
                Log.e(TAG, "Fallback launchShortcut not found: ${shortcut.id}", e2)
            }
        }
    }
}

private const val DEFAULT_ICON_SIZE = 96

fun Drawable.toImageBitmap(): ImageBitmap {
    if ((this is BitmapDrawable) && (this.bitmap != null)) {
        return this.bitmap.asImageBitmap()
    }
    val width = if (intrinsicWidth > 0) intrinsicWidth else DEFAULT_ICON_SIZE
    val height = if (intrinsicHeight > 0) intrinsicHeight else DEFAULT_ICON_SIZE
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap.asImageBitmap()
}
