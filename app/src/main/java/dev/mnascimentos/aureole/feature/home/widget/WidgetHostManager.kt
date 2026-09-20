package dev.mnascimentos.aureole.feature.home.widget

import android.app.Activity.RESULT_OK
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.addWidgetId
import dev.mnascimentos.aureole.feature.home.removeWidgetId
import dev.mnascimentos.aureole.feature.home.setPendingWidgetId

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
                if (widgetId != -1) viewModel.addWidgetId(widgetId)
            } else {
                if (pendingId != -1) appWidgetHost.deleteAppWidgetId(pendingId)
            }
            viewModel.setPendingWidgetId(-1)
        }

    fun handleWidgetSelected(provider: AppWidgetProviderInfo) {
        val id = appWidgetHost.allocateAppWidgetId()
        viewModel.setPendingWidgetId(id)
        val allowed = appWidgetManager.bindAppWidgetIdIfAllowed(id, provider.provider)
        if (allowed) {
            configureWidget(id, provider)
        } else {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
            }
            bindWidgetLauncher.launch(intent)
        }
    }

    private fun configureWidget(widgetId: Int, provider: AppWidgetProviderInfo?) {
        if (provider?.configure != null) {
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            configureWidgetLauncher.launch(intent)
        } else {
            viewModel.addWidgetId(widgetId)
            viewModel.setPendingWidgetId(-1)
        }
    }

    fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId)
    }
}
