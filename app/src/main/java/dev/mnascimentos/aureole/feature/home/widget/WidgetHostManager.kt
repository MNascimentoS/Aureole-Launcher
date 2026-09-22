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
    }

    fun handleWidgetSelected(provider: AppWidgetProviderInfo?) {
        if (provider == null) return
        val appWidgetId = appWidgetHost.allocateAppWidgetId()
        if (provider.configure != null) {
            viewModel.setPendingWidgetId(appWidgetId)
            val intent = Intent(
                AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            ).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            try {
                if (context is Activity) {
                    context.startActivityForResult(intent, 1001)
                }
            } catch (e: ActivityNotFoundException) {
                Log.e(TAG, "Activity not found for widget configure", e)
                completeWidgetConfiguration(appWidgetId, provider)
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception for widget configure", e)
                completeWidgetConfiguration(appWidgetId, provider)
            }
        } else {
            completeWidgetConfiguration(appWidgetId, provider)
        }
    }

    private fun completeWidgetConfiguration(
        widgetId: Int,
        provider: AppWidgetProviderInfo?
    ) {
        updateWidgetOptions(widgetId, provider)
        viewModel.addWidgetId(widgetId, provider)
        viewModel.setPendingWidgetId(-1)
    }

    private fun updateWidgetOptions(widgetId: Int, provider: AppWidgetProviderInfo?) {
        if (provider == null) return
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
        }
    }

    fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId)
    }
}
