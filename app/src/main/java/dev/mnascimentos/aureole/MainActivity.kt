package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.LocalHazeState
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.MainScaffold
import dev.mnascimentos.aureole.feature.home.extensions.checkAppUpdate
import dev.mnascimentos.aureole.feature.home.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.handleBackNavigation
import dev.mnascimentos.aureole.feature.home.extensions.onFolderIntent
import dev.mnascimentos.aureole.feature.home.extensions.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.extensions.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.WidgetHostManager
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    // Widget System
    private lateinit var appWidgetManager: AppWidgetManager
    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var widgetHostManager: WidgetHostManager

    // In-App Update
    private lateinit var appUpdateManager: AppUpdateManager
    private var cachedAppUpdateInfo: AppUpdateInfo? = null

    private val updateActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.w(TAG, "In-app update flow failed or was cancelled by user: ${result.resultCode}")
            }
        }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            viewModel.setShowUpdateDownloadedDialog(visible = true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        widgetHostManager = WidgetHostManager(this, viewModel, appWidgetHost, appWidgetManager)

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.registerListener(installStateUpdatedListener)

        setupWindowAndBackHandling()
        setupContent()
    }

    private fun setupWindowAndBackHandling() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val state = viewModel.uiState.value
                if (checkOverlayActive(state)) {
                    handleBackNavigation(state, viewModel)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.decorView.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                val w = view.width
                val h = view.height
                val isOverlayActive = checkOverlayActive(viewModel.uiState.value)
                if (w > 0 && h > 0) {
                    view.systemGestureExclusionRects = if (isOverlayActive) {
                        emptyList()
                    } else {
                        listOf(Rect(0, 0, w, h))
                    }
                }
            }
        }
    }

    private fun setupContent() {
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isOverlayActive = checkOverlayActive(uiState)
            val hazeState = rememberHazeState()

            CompositionLocalProvider(LocalHazeState provides hazeState) {
                LaunchedEffect(isOverlayActive) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val view = window.decorView
                        val w = view.width
                        val h = view.height
                        if (w > 0 && h > 0) {
                            view.systemGestureExclusionRects = if (isOverlayActive) {
                                emptyList()
                            } else {
                                listOf(Rect(0, 0, w, h))
                            }
                        }
                    }
                }

                AureoleLauncherTheme(
                    isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
                    seedColor = Color(uiState.manualSeedColor),
                ) {
                    BackHandler(enabled = isOverlayActive) {
                        handleBackNavigation(uiState, viewModel)
                    }

                    val homeActions = createHomeActions()

                    CompositionLocalProvider(
                        LocalHomeUiState provides uiState,
                        LocalHomeActions provides homeActions
                    ) {
                        MainScaffold(
                            appWidgetHost = appWidgetHost,
                            appWidgetManager = appWidgetManager,
                            viewModel = viewModel,
                            onWidgetSelected = { widgetHostManager.handleWidgetSelected(it) },
                            onRemoveWidget = { widgetHostManager.removeWidget(it) }
                        )
                    }
                }
            }
        }
    }

    private fun checkOverlayActive(uiState: MainUiState): Boolean {
        return (uiState.isAllAppsDrawerOpen ||
                uiState.activeFolder != null ||
                uiState.isCreateFolderDialogVisible ||
                uiState.isAddAppToFolderDialogVisible ||
                uiState.isRenameFolderDialogVisible ||
                uiState.searchQuery.isNotEmpty() ||
                uiState.showWidgetPicker ||
                uiState.showFavoritePickerDialog ||
                uiState.showWidgetPopup ||
                uiState.showWidgetResizeDialog)
    }

    private fun createHomeActions(): HomeScreenActions {
        return HomeScreenActions(
            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
            onAddWidgetClick = { viewModel.setShowWidgetPicker(true) },
            onRemoveWidgetClick = { widgetId -> widgetHostManager.removeWidget(widgetId) },
            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) },
            onExpandNotificationShade = { IntentUtils.expandNotificationShade(this) },
            onFolderIntent = { intent -> viewModel.onFolderIntent(intent) },
            onSetAddAppToFolderDialogVisible = { visible -> viewModel.setAddAppToFolderDialogVisible(visible) },
            onSetRenameFolderDialogVisible = { visible -> viewModel.setRenameFolderDialogVisible(visible) },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onSettingsClick = { startActivity(Intent(this, SettingsActivity::class.java)) },
            onAllAppsDrawerClose = { viewModel.setAllAppsDrawerOpen(false) },
            onAllAppsDrawerOpen = { viewModel.setAllAppsDrawerOpen(true) },
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onAppInfoClick = { app -> IntentUtils.openAppInfo(this, app.packageName) },
            onOpenFavoritePicker = { viewModel.setShowFavoritePicker(true) },
            onOpenWidgetPopup = { widgetId, topY -> viewModel.openWidgetPopup(widgetId, topY) },
            onCloseWidgetPopup = { viewModel.closeWidgetPopup() },
            onOpenWidgetResizeDialog = { viewModel.setShowWidgetResizeDialog(true) },
            onCloseWidgetResizeDialog = { viewModel.closeWidgetPopup() },
            onResizeWidgetHeight = { height -> viewModel.setWidgetRowHeight(height) },
            onStartInAppUpdate = {
                viewModel.setShowUpdateAvailableDialog(visible = false)
                cachedAppUpdateInfo?.let { appUpdateInfo ->
                    val updateType = if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        AppUpdateType.FLEXIBLE
                    } else {
                        AppUpdateType.IMMEDIATE
                    }
                    val options = AppUpdateOptions.newBuilder(updateType).build()
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateActivityResultLauncher,
                        options
                    )
                }
            },
            onCompleteInAppUpdate = {
                viewModel.setShowUpdateDownloadedDialog(visible = false)
                appUpdateManager.completeUpdate()
            },
            onDismissUpdateDialog = {
                viewModel.setShowUpdateAvailableDialog(visible = false)
                viewModel.setShowUpdateDownloadedDialog(visible = false)
            }
        )
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

    override fun onDestroy() {
        super.onDestroy()
        if (::appUpdateManager.isInitialized) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings()
        viewModel.loadApps()
        checkAppUpdate(appUpdateManager, viewModel) { cachedAppUpdateInfo = it }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val APPWIDGET_HOST_ID = 1024
    }
}
