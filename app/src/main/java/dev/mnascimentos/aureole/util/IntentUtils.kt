package dev.mnascimentos.aureole.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
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
            Log.w(TAG, "Activity not found for app info", e)
        } catch (e: SecurityException) {
            Log.w(TAG, "Security exception for app info", e)
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
                @Suppress("DEPRECATION")
                val fallbackIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (e2: ActivityNotFoundException) {
                Log.e(TAG, "Failed to uninstall app $packageName", e2)
            } catch (e2: SecurityException) {
                Log.e(TAG, "Failed to uninstall app $packageName due to security", e2)
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
            Log.w(TAG, "StatusBarManager class not found", e)
        } catch (e: NoSuchMethodException) {
            Log.w(TAG, "expandNotificationsPanel method not found", e)
        } catch (e: IllegalAccessException) {
            Log.w(TAG, "Illegal access expanding notifications panel", e)
        } catch (e: InvocationTargetException) {
            Log.w(TAG, "Invocation target exception expanding notifications panel", e)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Illegal argument expanding notifications panel", e)
        } catch (e: SecurityException) {
            Log.w(TAG, "Security exception expanding notifications panel", e)
        }
    }
}
