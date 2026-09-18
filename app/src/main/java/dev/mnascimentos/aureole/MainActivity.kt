package dev.mnascimentos.aureole

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
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

    private var topWidgetIds by mutableStateOf<List<Int>>(emptyList())
    private var widgetRowHeight by mutableStateOf(160.dp)
    private var showWidgetPicker by mutableStateOf(false)
    private var pendingWidgetId by mutableIntStateOf(-1)

    private val bindWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val provider = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
                configureWidget(pendingWidgetId, provider)
            } else {
                if (pendingWidgetId != -1) appWidgetHost.deleteAppWidgetId(pendingWidgetId)
                pendingWidgetId = -1
            }
        }

    private val configureWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val widgetId = result.data?.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    pendingWidgetId
                ) ?: pendingWidgetId
                if (widgetId != -1) addWidgetId(widgetId)
            } else {
                if (pendingWidgetId != -1) appWidgetHost.deleteAppWidgetId(pendingWidgetId)
            }
            pendingWidgetId = -1
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa o Host de Widgets
        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        appWidgetHost.startListening()

        // Recupera os widgets salvos anteriormente
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedIds = prefs.getString(KEY_TOP_WIDGET_IDS, "") ?: ""
        topWidgetIds = savedIds.split(",").mapNotNull { it.toIntOrNull() }

        val savedHeight = prefs.getFloat(KEY_WIDGET_ROW_HEIGHT, DEFAULT_WIDGET_ROW_HEIGHT)
        widgetRowHeight = savedHeight.dp

        setContent {
            AureoleLauncherTheme {
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        uiState = uiState,
                        topWidgetIds = topWidgetIds,
                        appWidgetHost = appWidgetHost,
                        widgetRowHeight = widgetRowHeight,
                        actions = HomeScreenActions(
                            onWidgetRowHeightChanged = { saveWidgetRowHeight(it) },
                            onAddWidgetClick = { showWidgetPicker = true },
                            onRemoveWidgetClick = { widgetId -> removeWidget(widgetId) },
                            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) }
                        ),
                        modifier = Modifier.padding(innerPadding)
                    )

                    if (showWidgetPicker) {
                        WidgetPickerBottomSheet(
                            appWidgetManager = appWidgetManager,
                            onWidgetSelected = { provider ->
                                showWidgetPicker = false
                                handleWidgetSelected(provider)
                            },
                            onDismiss = { showWidgetPicker = false }
                        )
                    }
                }
            }
        }
    }

    private fun handleWidgetSelected(provider: AppWidgetProviderInfo) {
        val id = appWidgetHost.allocateAppWidgetId()
        pendingWidgetId = id
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
            addWidgetId(widgetId)
            pendingWidgetId = -1
        }
    }

    private fun addWidgetId(widgetId: Int) {
        if (!topWidgetIds.contains(widgetId)) {
            val newList = topWidgetIds + widgetId
            saveWidgetIds(newList)
        }
    }

    private fun removeWidget(widgetId: Int) {
        appWidgetHost.deleteAppWidgetId(widgetId)
        val newList = topWidgetIds - widgetId
        saveWidgetIds(newList)
    }

    private fun saveWidgetIds(ids: List<Int>) {
        topWidgetIds = ids
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit {
            putString(KEY_TOP_WIDGET_IDS, ids.joinToString(","))
        }
    }

    private fun saveWidgetRowHeight(height: Dp) {
        widgetRowHeight = height
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit {
            putFloat(KEY_WIDGET_ROW_HEIGHT, height.value)
        }
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
        private const val PREFS_NAME = "aureole_prefs"
        private const val KEY_TOP_WIDGET_IDS = "top_widget_ids_list"
        private const val KEY_WIDGET_ROW_HEIGHT = "widget_row_height"
        private const val DEFAULT_WIDGET_ROW_HEIGHT = 160f
    }
}
