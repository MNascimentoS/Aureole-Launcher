package dev.mnascimentos.aureole.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsHelper(context: Context) {

    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(eventName: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                putString(key, value)
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun logAppLaunch(packageName: String) {
        logEvent(EVENT_APP_LAUNCHED, mapOf(PARAM_PACKAGE_NAME to packageName))
    }

    fun logFolderCreated(folderName: String) {
        logEvent(EVENT_FOLDER_CREATED, mapOf(PARAM_FOLDER_NAME to folderName))
    }

    fun logSettingToggled(settingName: String, enabled: Boolean) {
        logEvent(
            EVENT_SETTING_TOGGLED,
            mapOf(PARAM_SETTING_NAME to settingName, PARAM_ENABLED to enabled.toString())
        )
    }

    companion object {
        private const val EVENT_APP_LAUNCHED = "app_launched"
        private const val EVENT_FOLDER_CREATED = "folder_created"
        private const val EVENT_SETTING_TOGGLED = "setting_toggled"

        private const val PARAM_PACKAGE_NAME = "package_name"
        private const val PARAM_FOLDER_NAME = "folder_name"
        private const val PARAM_SETTING_NAME = "setting_name"
        private const val PARAM_ENABLED = "enabled"
    }
}
