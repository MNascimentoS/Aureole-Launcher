package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setPendingWidgetId
import timber.log.Timber

class WidgetHostManager(
    private val viewModel: HomeViewModel,
    private val appWidgetHost: AppWidgetHost,
    private val appWidgetManager: AppWidgetManager,
    private val launchWidgetConfigure: (Intent) -> Unit = {},
    private val launchWidgetBind: (Intent) -> Unit = {}
) {
    companion object {
        private const val TAG = "WidgetHostManager"
        const val REQUEST_PICK_APPWIDGET = 1001
        const val REQUEST_BIND_APPWIDGET = 1002
    }

    fun handleWidgetSelected(provider: AppWidgetProviderInfo?) {
        if (provider != null) {
            val appWidgetId = appWidgetHost.allocateAppWidgetId()
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                processSelectedWidget(appWidgetId, provider)
            }
        }
    }

    private fun processSelectedWidget(appWidgetId: Int, provider: AppWidgetProviderInfo) {
        val canBind = try {
            appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, provider.provider)
        } catch (e: IllegalArgumentException) {
            Timber.tag(TAG).w(e, "IllegalArgumentException checking bind for id $appWidgetId")
            false
        } catch (e: IllegalStateException) {
            Timber.tag(TAG).w(e, "IllegalStateException checking bind for id $appWidgetId")
            false
        } catch (e: SecurityException) {
            Timber.tag(TAG).w(e, "SecurityException checking bind for id $appWidgetId")
            false
        }

        if (!canBind) {
            handleBindIntent(appWidgetId, provider)
        } else if (provider.configure != null) {
            handleConfigureIntent(appWidgetId, provider)
        } else {
            completeWidgetConfiguration(appWidgetId, provider)
        }
    }

    private fun handleBindIntent(appWidgetId: Int, provider: AppWidgetProviderInfo) {
        viewModel.setPendingWidgetId(appWidgetId)
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
        }
        try {
            launchWidgetBind(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.tag(TAG).e(e, "Activity not found for widget bind intent for id $appWidgetId")
            appWidgetHost.deleteAppWidgetId(appWidgetId)
            viewModel.setPendingWidgetId(-1)
        } catch (e: SecurityException) {
            Timber.tag(TAG).e(e, "Security exception for widget bind intent for id $appWidgetId")
            appWidgetHost.deleteAppWidgetId(appWidgetId)
            viewModel.setPendingWidgetId(-1)
        }
    }

    private fun handleConfigureIntent(appWidgetId: Int, provider: AppWidgetProviderInfo) {
        viewModel.setPendingWidgetId(appWidgetId)
        val intent = Intent(
            AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
        ).apply {
            component = provider.configure
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        try {
            launchWidgetConfigure(intent)
        } catch (e: ActivityNotFoundException) {
            Timber.tag(TAG).e(e, "Activity not found for widget configure")
            completeWidgetConfiguration(appWidgetId, provider)
        } catch (e: SecurityException) {
            Timber.tag(TAG).e(e, "Security exception for widget configure")
            completeWidgetConfiguration(appWidgetId, provider)
        }
    }

    fun completeWidgetConfiguration(
        widgetId: Int,
        provider: AppWidgetProviderInfo?
    ) {
        updateWidgetOptions(widgetId, provider)
        viewModel.addWidgetId(widgetId)
        viewModel.setPendingWidgetId(-1)
    }

    private fun updateWidgetOptions(widgetId: Int, provider: AppWidgetProviderInfo?) {
        if (provider == null || widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) return
        try {
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, provider.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, provider.minHeight)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, provider.minWidth * 2)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, provider.minHeight * 2)
            }
            appWidgetManager.updateAppWidgetOptions(widgetId, options)
        } catch (e: IllegalArgumentException) {
            Timber.tag(TAG).e(e, "Failed to update widget options for id $widgetId")
        } catch (e: IllegalStateException) {
            Timber.tag(TAG).e(e, "Failed to update widget options for id $widgetId")
        } catch (e: SecurityException) {
            Timber.tag(TAG).e(e, "Failed to update widget options for id $widgetId")
        }
    }

    fun removeWidget(widgetId: Int, targetStackId: String? = null) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId, targetStackId)
    }
}
