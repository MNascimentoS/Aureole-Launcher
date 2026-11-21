package dev.mnascimentos.aureole.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri
import timber.log.Timber
import java.lang.reflect.InvocationTargetException

object IntentUtils {
    private const val TAG = "IntentUtils"

    // Value of the deprecated Intent.ACTION_UNINSTALL_PACKAGE, kept as the fallback for older devices.
    private const val ACTION_UNINSTALL_PACKAGE_FALLBACK = "android.intent.action.UNINSTALL_PACKAGE"

    // Context.STATUS_BAR_SERVICE and StatusBarManager.expandNotificationsPanel are hidden APIs.
    private const val STATUS_BAR_SERVICE = "statusbar"
    private const val STATUS_BAR_MANAGER_CLASS = "android.app.StatusBarManager"
    private const val EXPAND_METHOD = "expandNotificationsPanel"

    fun openAppInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.tag(TAG).w(e, "Activity not found for app info")
        } catch (e: SecurityException) {
            Timber.tag(TAG).w(e, "Security exception for app info")
        }
    }

    fun openUrl(context: Context, url: String) {
        runCatching {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }.onFailure { Timber.tag(TAG).w(it, "Could not open url $url") }
    }

    fun uninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val fallbackIntent = Intent(ACTION_UNINSTALL_PACKAGE_FALLBACK).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: ActivityNotFoundException) {
                Timber.tag(TAG).e(e2, "Failed to uninstall app $packageName")
            } catch (e2: SecurityException) {
                Timber.tag(TAG).e(e2, "Failed to uninstall app $packageName due to security")
            }
        }
    }

    /** Opens the system notification shade; needs `EXPAND_STATUS_BAR` in the manifest. */
    fun expandNotificationShade(context: Context) {
        runShadeExpansion {
            val statusBarService = context.getSystemService(STATUS_BAR_SERVICE)
                ?: error("No status bar service")
            Class.forName(STATUS_BAR_MANAGER_CLASS).getMethod(EXPAND_METHOD).invoke(statusBarService)
        }
    }

    /**
     * Runs [expand] and logs any failure with `Log.w`, so it shows in release builds too (Timber only logs in debug).
     * The reflective call wraps the real failure, such as the SecurityException thrown without `EXPAND_STATUS_BAR`,
     * in an [InvocationTargetException]: the log carries that cause. True when the shade was asked to open.
     */
    internal fun runShadeExpansion(expand: () -> Unit): Boolean = try {
        expand()
        true
    } catch (e: InvocationTargetException) {
        logShadeFailure(e.targetException ?: e)
    } catch (e: ReflectiveOperationException) {
        logShadeFailure(e)
    } catch (e: IllegalArgumentException) {
        logShadeFailure(e)
    } catch (e: IllegalStateException) {
        logShadeFailure(e)
    } catch (e: SecurityException) {
        logShadeFailure(e)
    }

    private fun logShadeFailure(cause: Throwable): Boolean {
        Log.w(TAG, "Could not expand the notifications panel", cause)
        return false
    }
}
