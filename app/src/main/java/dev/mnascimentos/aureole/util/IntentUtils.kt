package dev.mnascimentos.aureole.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import timber.log.Timber
import java.lang.reflect.InvocationTargetException

object IntentUtils {
    private const val TAG = "IntentUtils"

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

    fun uninstallApp(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                @Suppress("DEPRECATION") // ACTION_UNINSTALL_PACKAGE required as fallback for older Android versions
                val fallbackIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
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

    fun expandNotificationShade(context: Context) {
        try {
            val statusBarService = context.getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expandMethod = statusBarManager.getMethod("expandNotificationsPanel")
            expandMethod.invoke(statusBarService)
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).w(e, "StatusBarManager class not found")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).w(e, "expandNotificationsPanel method not found")
        } catch (e: IllegalAccessException) {
            Timber.tag(TAG).w(e, "Illegal access expanding notifications panel")
        } catch (e: InvocationTargetException) {
            Timber.tag(TAG).w(e, "Invocation target exception expanding notifications panel")
        } catch (e: IllegalArgumentException) {
            Timber.tag(TAG).w(e, "Illegal argument expanding notifications panel")
        } catch (e: SecurityException) {
            Timber.tag(TAG).w(e, "Security exception expanding notifications panel")
        }
    }
}
