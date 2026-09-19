package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.ui.MainViewModel
import dev.mnascimentos.aureole.ui.components.WidgetPickerBottomSheet
import dev.mnascimentos.aureole.ui.screens.HomeScreen
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions
import dev.mnascimentos.aureole.ui.theme.AureoleLauncherTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Sistema de Widgets
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost

    private val bindWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val pendingId = viewModel.pendingWidgetId
            if (result.resultCode == RESULT_OK) {
                val widgetId = result.data?.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    pendingId
                ) ?: pendingId
                if (widgetId != -1) viewModel.addWidgetId(widgetId)
            } else {
                if (pendingId != -1) appWidgetHost.deleteAppWidgetId(pendingId)
            }
            viewModel.setPendingWidgetId(-1)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o Host de Widgets
        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        appWidgetHost.startListening()

        setContent {
            AureoleLauncherTheme {
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        uiState = uiState,
                        topWidgetIds = uiState.topWidgetIds,
                        appWidgetHost = appWidgetHost,
                        widgetRowHeight = uiState.widgetRowHeight,
                        actions = HomeScreenActions(
                            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
                            onAddWidgetClick = { viewModel.setShowWidgetPicker(true) },
                            onRemoveWidgetClick = { widgetId -> removeWidget(widgetId) },
                            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) }
                        ),
                        modifier = Modifier.padding(innerPadding)
                    )

                    if (uiState.showWidgetPicker) {
                        WidgetPickerBottomSheet(
                            appWidgetManager = appWidgetManager,
                            onWidgetSelected = { provider ->
                                viewModel.setShowWidgetPicker(false)
                                handleWidgetSelected(provider)
                            },
                            onDismiss = { viewModel.setShowWidgetPicker(false) }
                        )
                    }
                }
            }
        }
    }

    private fun handleWidgetSelected(provider: AppWidgetProviderInfo) {
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

    private fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        viewModel.removeWidgetId(widgetId)
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega a lista de apps ao voltar para a home
        viewModel.loadApps()
    }

    companion object {
        private const val APPWIDGET_HOST_ID = 1024
    }
}
