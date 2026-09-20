package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import dev.mnascimentos.aureole.feature.home.ext.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.ext.onFolderIntent
import dev.mnascimentos.aureole.feature.home.ext.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.ext.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.ext.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.ext.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.ext.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.ext.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.ext.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.ext.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.ext.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.ext.toggleFavorite
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        widgetHostManager = WidgetHostManager(this, viewModel, appWidgetHost, appWidgetManager)

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
                    handleBackNavigation(state)
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
                        handleBackNavigation(uiState)
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
            onResizeWidgetHeight = { height -> viewModel.setWidgetRowHeight(height) }
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

    override fun onResume() {
        super.onResume()
        viewModel.loadSettings()
        viewModel.loadApps()
    }

    companion object {
        private const val APPWIDGET_HOST_ID = 1024
    }
}
