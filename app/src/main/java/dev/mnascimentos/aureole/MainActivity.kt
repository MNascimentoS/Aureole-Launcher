package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.ui.FolderViewIntent
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.MainViewModel
import dev.mnascimentos.aureole.ui.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.ui.components.WidgetPickerBottomSheet
import dev.mnascimentos.aureole.ui.screens.HomeScreen
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions
import dev.mnascimentos.aureole.ui.settings.SettingsActivity
import dev.mnascimentos.aureole.ui.theme.AureoleLauncherTheme

@Suppress("TooManyFunctions")
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // Widget System
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

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID).apply { startListening() }

        setContent {
            AureoleLauncherTheme {
                val uiState by viewModel.uiState.collectAsState()
                val isOverlayActive = checkOverlayActive(uiState)

                BackHandler(enabled = isOverlayActive) {
                    handleBackNavigation(uiState)
                }

                val homeActions = createHomeActions()

                MainScaffold(
                    uiState = uiState,
                    homeActions = homeActions
                )
            }
        }
    }

    @Composable
    private fun MainScaffold(
        uiState: MainUiState,
        homeActions: HomeScreenActions
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            HomeScreen(
                uiState = uiState,
                appWidgetHost = appWidgetHost,
                actions = homeActions,
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

            if (uiState.showFavoritePickerDialog) {
                FavoriteAppsDialog(
                    allApps = uiState.apps,
                    favoriteAppPackages = uiState.favoriteAppPackages,
                    onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
                    onDismiss = { viewModel.setShowFavoritePicker(false) }
                )
            }
        }
    }

    private fun checkOverlayActive(uiState: MainUiState): Boolean {
        return uiState.isAllAppsDrawerOpen ||
                uiState.activeFolder != null ||
                uiState.isCreateFolderDialogVisible ||
                uiState.isAddAppToFolderDialogVisible ||
                uiState.isRenameFolderDialogVisible ||
                uiState.searchQuery.isNotEmpty() ||
                uiState.showWidgetPicker ||
                uiState.showFavoritePickerDialog
    }

    private fun handleBackNavigation(uiState: MainUiState) {
        when {
            uiState.isCreateFolderDialogVisible ||
            uiState.isAddAppToFolderDialogVisible ||
            uiState.isRenameFolderDialogVisible ||
            uiState.activeFolder != null -> viewModel.onFolderIntent(FolderViewIntent.CloseFolder)
            uiState.isAllAppsDrawerOpen -> viewModel.setAllAppsDrawerOpen(false)
            uiState.searchQuery.isNotEmpty() -> viewModel.onSearchQueryChanged("")
            uiState.showWidgetPicker -> viewModel.setShowWidgetPicker(false)
            uiState.showFavoritePickerDialog -> viewModel.setShowFavoritePicker(false)
        }
    }

    private fun createHomeActions(): HomeScreenActions {
        return HomeScreenActions(
            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
            onAddWidgetClick = { viewModel.setShowWidgetPicker(true) },
            onRemoveWidgetClick = { widgetId -> removeWidget(widgetId) },
            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) },
            onExpandNotificationShade = { expandNotificationShade() },
            onFolderIntent = { intent -> viewModel.onFolderIntent(intent) },
            onSetAddAppToFolderDialogVisible = { visible -> viewModel.setAddAppToFolderDialogVisible(visible) },
            onSetRenameFolderDialogVisible = { visible -> viewModel.setRenameFolderDialogVisible(visible) },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onSettingsClick = { startActivity(Intent(this, SettingsActivity::class.java)) },
            onAllAppsDrawerClose = { viewModel.setAllAppsDrawerOpen(false) },
            onAllAppsDrawerOpen = { viewModel.setAllAppsDrawerOpen(true) },
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onAppInfoClick = { app -> openAppInfo(app.packageName) },
            onOpenFavoritePicker = { viewModel.setShowFavoritePicker(true) }
        )
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

    @Deprecated("Deprecated in Java")
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val state = viewModel.uiState.value
        if (checkOverlayActive(state)) {
            handleBackNavigation(state)
        }
    }

    private fun openAppInfo(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (_: Exception) {}
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun expandNotificationShade() {
        try {
            val statusBarService = getSystemService("statusbar")
            val statusBarManager = Class.forName("android.app.StatusBarManager")
            val expandMethod = statusBarManager.getMethod("expandNotificationsPanel")
            expandMethod.invoke(statusBarService)
        } catch (_: Exception) {
            // Cannot expansion without framework internal permissions
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) {
            val state = viewModel.uiState.value
            if (state.homeButtonOpensAllApps) {
                viewModel.setAllAppsDrawerOpen(!state.isAllAppsDrawerOpen)
            } else {
                viewModel.setAllAppsDrawerOpen(false)
            }
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
        viewModel.loadSettings()
        viewModel.loadApps()
    }

    companion object {
        private const val APPWIDGET_HOST_ID = 1024
    }
}
