package dev.mnascimentos.aureole.feature.home.widget

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.extensions.setPendingWidgetId

class WidgetHostManager(
    private val context: Context,
    private val viewModel: HomeViewModel,
    private val appWidgetHost: AppWidgetHost,
    private val appWidgetManager: AppWidgetManager
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
            Log.w(TAG, "IllegalArgumentException checking bind for id $appWidgetId", e)
            false
        } catch (e: IllegalStateException) {
            Log.w(TAG, "IllegalStateException checking bind for id $appWidgetId", e)
            false
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException checking bind for id $appWidgetId", e)
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
            if (context is Activity) {
                context.startActivityForResult(intent, REQUEST_BIND_APPWIDGET)
            } else {
                appWidgetHost.deleteAppWidgetId(appWidgetId)
                viewModel.setPendingWidgetId(-1)
            }
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Activity not found for widget bind intent for id $appWidgetId", e)
            appWidgetHost.deleteAppWidgetId(appWidgetId)
            viewModel.setPendingWidgetId(-1)
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception for widget bind intent for id $appWidgetId", e)
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
            if (context is Activity) {
                context.startActivityForResult(intent, REQUEST_PICK_APPWIDGET)
            }
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "Activity not found for widget configure", e)
            completeWidgetConfiguration(appWidgetId, provider)
        } catch (e: SecurityException) {
            Log.e(TAG, "Security exception for widget configure", e)
            completeWidgetConfiguration(appWidgetId, provider)
        }
    }

    fun completeWidgetConfiguration(
        widgetId: Int,
        provider: AppWidgetProviderInfo?
    ) {
        updateWidgetOptions(widgetId, provider)
        viewModel.addWidgetId(widgetId, provider)
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
            Log.e(TAG, "Failed to update widget options for id $widgetId", e)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Failed to update widget options for id $widgetId", e)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to update widget options for id $widgetId", e)
        }
    }

    fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId)
    }
}
