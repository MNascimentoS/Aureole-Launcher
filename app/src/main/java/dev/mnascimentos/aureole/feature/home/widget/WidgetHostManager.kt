package dev.mnascimentos.aureole.feature.home.widget

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.extensions.setPendingWidgetId

class WidgetHostManager(
    activity: ComponentActivity,
    private val viewModel: HomeViewModel,
    private val appWidgetHost: AppWidgetHost,
    private val appWidgetManager: AppWidgetManager
) {

    private val bindWidgetLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val pendingId = viewModel.pendingWidgetId
            if (result.resultCode == RESULT_OK) {
                val provider = appWidgetManager.getAppWidgetInfo(pendingId)
                configureWidget(pendingId, provider)
            } else {
                if (pendingId != -1) appWidgetHost.deleteAppWidgetId(pendingId)
                viewModel.setPendingWidgetId(-1)
            }
        }

    private val configureWidgetLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val pendingId = viewModel.pendingWidgetId
            if (result.resultCode == RESULT_OK) {
                val widgetId = result.data?.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    pendingId,
                ) ?: pendingId
                if (widgetId != -1) {
                    val provider = appWidgetManager.getAppWidgetInfo(widgetId)
                    viewModel.addWidgetId(widgetId, provider)
                }
            } else {
                if (pendingId != -1) appWidgetHost.deleteAppWidgetId(pendingId)
            }
            viewModel.setPendingWidgetId(-1)
        }

    fun handleWidgetSelected(provider: AppWidgetProviderInfo) {
        val id = appWidgetHost.allocateAppWidgetId()
        viewModel.setPendingWidgetId(id)

        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, provider.minWidth)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, provider.minHeight)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, provider.minWidth * 2)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, provider.minHeight * 2)
        }

        val allowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && provider.profile != null) {
            appWidgetManager.bindAppWidgetIdIfAllowed(id, provider.profile, provider.provider, options)
        } else {
            appWidgetManager.bindAppWidgetIdIfAllowed(id, provider.provider, options)
        }

        if (allowed) {
            configureWidget(id, provider)
        } else {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && provider.profile != null) {
                    putExtra(Intent.EXTRA_USER, provider.profile)
                }
            }
            bindWidgetLauncher.launch(intent)
        }
    }

    private fun configureWidget(widgetId: Int, provider: AppWidgetProviderInfo?) {
        updateWidgetOptions(widgetId, provider)
        if (provider?.configure != null) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            configureWidgetLauncher.launch(intent)
        } else {
            viewModel.addWidgetId(widgetId, provider)
            viewModel.setPendingWidgetId(-1)
        }
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
        } catch (e: Exception) {
            Log.e("WidgetHostManager", "Failed to update widget options for id $widgetId", e)
        }
    }

    fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId)
    }
}
