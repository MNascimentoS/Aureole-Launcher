package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import timber.log.Timber

object SettingsIntentUtils {
    private const val TAG = "SettingsIntentUtils"

    fun openDefaultLauncherSettings(context: Context) {
        if (!tryLaunchIntent(context, Intent(Settings.ACTION_HOME_SETTINGS)) &&
            !tryLaunchIntent(context, Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"))
        ) {
            tryLaunchIntent(context, Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun tryLaunchIntent(context: Context, intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Timber.tag(TAG).w(e, "Activity not found for intent action: ${intent.action}")
            false
        } catch (e: SecurityException) {
            Timber.tag(TAG).w(e, "SecurityException launching intent action: ${intent.action}")
            false
        }
    }
}
