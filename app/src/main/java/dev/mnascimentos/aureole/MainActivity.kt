package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.ui.FolderViewIntent
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.MainViewModel
import dev.mnascimentos.aureole.ui.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.ui.components.WidgetPickerBottomSheet
import dev.mnascimentos.aureole.ui.components.OpenedWidgetPopup
import dev.mnascimentos.aureole.ui.components.WidgetResizeDialog
import dev.mnascimentos.aureole.ui.screens.HomeScreen
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions
import dev.mnascimentos.aureole.ui.settings.SettingsActivity
import dev.mnascimentos.aureole.ui.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.ui.theme.LocalHazeState
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

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
                    pendingId,
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

        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID).apply { startListening() }

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
    }

    @Suppress("LongMethod")
    @Composable
    private fun MainScaffold(
        uiState: MainUiState,
        homeActions: HomeScreenActions
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { _ ->
            HomeScreen(
                uiState = uiState,
                appWidgetHost = appWidgetHost,
                actions = homeActions,
                modifier = Modifier.fillMaxSize()
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

            if (uiState.showWidgetPopup && uiState.activeWidgetId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { viewModel.closeWidgetPopup() })
                        }
                ) {
                    val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
                    val sidePadding = 76.dp
                    val screenDensity = LocalDensity.current.density
                    val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

                    val rawTopDp = if (uiState.activeWidgetTopYPx > 0f) {
                        (uiState.activeWidgetTopYPx / screenDensity).dp - topInsetDp
                    } else {
                        40.dp
                    }
                    val clampedTopDp = rawTopDp.coerceIn(8.dp, 500.dp)

                    OpenedWidgetPopup(
                        widgetId = uiState.activeWidgetId,
                        appWidgetHost = appWidgetHost,
                        onDismiss = { viewModel.closeWidgetPopup() },
                        onResizeClick = { viewModel.setShowWidgetResizeDialog(true) },
                        onRemoveClick = {
                            removeWidget(uiState.activeWidgetId!!)
                            viewModel.closeWidgetPopup()
                        },
                        hazeState = LocalHazeState.current,
                        isHazeEnabled = uiState.isHazeEnabled,
                        hazeOpacity = uiState.hazeOpacity,
                        modifier = Modifier
                            .align(popupAlign)
                            .padding(
                                start = if (uiState.isLeftHandedMode) sidePadding else 0.dp,
                                end = if (!uiState.isLeftHandedMode) sidePadding else 0.dp,
                                top = clampedTopDp
                            )
                    )
                }
            }

            if (uiState.showWidgetResizeDialog) {
                WidgetResizeDialog(
                    currentHeightDp = uiState.widgetRowHeight,
                    onHeightSelected = { newHeight ->
                        viewModel.setWidgetRowHeight(newHeight)
                        viewModel.closeWidgetPopup()
                    },
                    onDismiss = { viewModel.closeWidgetPopup() }
                )
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

    private fun handleBackNavigation(uiState: MainUiState) {
        when {
            uiState.showWidgetPopup || uiState.showWidgetResizeDialog -> viewModel.closeWidgetPopup()
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
            onOpenFavoritePicker = { viewModel.setShowFavoritePicker(true) },
            onOpenWidgetPopup = { widgetId, topY -> viewModel.openWidgetPopup(widgetId, topY) },
            onCloseWidgetPopup = { viewModel.closeWidgetPopup() },
            onOpenWidgetResizeDialog = { viewModel.setShowWidgetResizeDialog(true) },
            onCloseWidgetResizeDialog = { viewModel.closeWidgetPopup() },
            onResizeWidgetHeight = { height -> viewModel.setWidgetRowHeight(height) }
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
