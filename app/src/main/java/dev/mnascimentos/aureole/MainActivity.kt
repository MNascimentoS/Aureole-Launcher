package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.lifecycle.UpdateLifecycleObserver
import dev.mnascimentos.aureole.core.lifecycle.WidgetLifecycleObserver
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.MainScaffold
import dev.mnascimentos.aureole.feature.home.extensions.checkReleaseNotes
import dev.mnascimentos.aureole.feature.home.extensions.handleBackNavigation
import dev.mnascimentos.aureole.feature.home.extensions.loadGridItems
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.extensions.setPendingWidgetId
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.WidgetHostManager

class MainActivity : ComponentActivity() {

    internal val viewModel: HomeViewModel by viewModels()

    // Widget System
    internal lateinit var appWidgetManager: AppWidgetManager
    internal lateinit var appWidgetHost: AppWidgetHost
    internal lateinit var widgetHostManager: WidgetHostManager

    // In-App Update
    internal lateinit var appUpdateManager: AppUpdateManager
    internal var cachedAppUpdateInfo: AppUpdateInfo? = null

    internal val updateActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.w(TAG, "In-app update flow failed or was cancelled by user: ${result.resultCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)
        widgetHostManager = WidgetHostManager(this, viewModel, appWidgetHost, appWidgetManager)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        lifecycle.addObserver(UpdateLifecycleObserver(appUpdateManager, viewModel) { cachedAppUpdateInfo = it })
        lifecycle.addObserver(WidgetLifecycleObserver(appWidgetHost) { wasInBackground = it })
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.loadSettings()
                viewModel.loadApps()
                viewModel.loadGridItems()
                val versionCode = getAppVersionCode(this@MainActivity)
                val versionName = getAppVersionName(this@MainActivity)
                viewModel.checkReleaseNotes(versionCode, versionName)
                wasInBackground = false
            }
        })

        setupWindowAndBackHandling()
        setupContent()
    }

    private fun setupWindowAndBackHandling() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
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
            }
        )

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

                val isDarkTheme = isSystemInDarkTheme()
                val palette = getPaletteByName(uiState.selectedThemeName, isDarkTheme, uiState.manualSeedColor)
                AureoleLauncherTheme(
                    darkTheme = isDarkTheme,
                    isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
                    aureoleColors = palette.colors,
                    aureoleFontName = uiState.selectedFontName,
                    aureoleDimens = AureoleDimens(cornerRadius = uiState.cornerRadiusDp.dp)
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

    private var wasInBackground = false

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) {
            val state = viewModel.uiState.value
            if (wasInBackground) {
                viewModel.setAllAppsDrawerOpen(false)
                wasInBackground = false
            } else if (state.homeButtonOpensAllApps) {
                viewModel.setAllAppsDrawerOpen(!state.isAllAppsDrawerOpen, fromHomeButton = true)
            } else {
                viewModel.setAllAppsDrawerOpen(false)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WidgetHostManager.REQUEST_PICK_APPWIDGET ||
            requestCode == WidgetHostManager.REQUEST_BIND_APPWIDGET
        ) {
            val pendingWidgetId = viewModel.pendingWidgetId
            if (pendingWidgetId != -1) {
                if (resultCode == RESULT_OK) {
                    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
                    widgetHostManager.completeWidgetConfiguration(pendingWidgetId, appWidgetInfo)
                } else {
                    appWidgetHost.deleteAppWidgetId(pendingWidgetId)
                    viewModel.setPendingWidgetId(-1)
                    viewModel.setIsAddingSingleWidget(false)
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val APPWIDGET_HOST_ID = 1024
    }
}

private fun checkOverlayActive(uiState: MainUiState): Boolean {
    return checkFolderOrSheetActive(uiState) || checkEditOrPickerActive(uiState)
}

private fun checkFolderOrSheetActive(uiState: MainUiState): Boolean {
    val isFolderActive = uiState.activeFolder != null ||
        uiState.isCreateFolderDialogVisible ||
        uiState.isAddAppToFolderDialogVisible ||
        uiState.isRenameFolderDialogVisible
    return uiState.showReleaseNotesBottomSheet ||
        uiState.isAllAppsDrawerOpen ||
        uiState.searchQuery.isNotEmpty() ||
        isFolderActive
}

private fun checkEditOrPickerActive(uiState: MainUiState): Boolean {
    val isWidgetActive = uiState.showWidgetPicker ||
        uiState.showWidgetPopup ||
        uiState.showWidgetResizeDialog
    val isContainerActive = uiState.showAddContainerDialog ||
        uiState.editingGridItem != null ||
        uiState.isEditContainerDialogVisible
    return uiState.isGridEditMode ||
        uiState.showFavoritePickerDialog ||
        isWidgetActive ||
        isContainerActive
}

private fun MainActivity.createHomeActions(): HomeScreenActions {
    return HomeActionsFactory(
        activity = this,
        viewModel = viewModel,
        widgetHostManager = widgetHostManager,
        appUpdateManager = appUpdateManager,
        updateActivityResultLauncher = updateActivityResultLauncher,
        cachedAppUpdateInfo = cachedAppUpdateInfo
    ).createHomeActions()
}

private fun getAppVersionCode(context: Context): Int {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    } catch (_: Exception) {
        1
    }
}

private fun getAppVersionName(context: Context): String {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "0.3.13"
    } catch (_: Exception) {
        "0.3.13"
    }
}
