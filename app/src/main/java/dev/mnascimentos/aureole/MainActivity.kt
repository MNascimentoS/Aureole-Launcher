package dev.mnascimentos.aureole

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.designsystem.palette.getPaletteByName
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.lifecycle.MainLifecycleObserver
import dev.mnascimentos.aureole.core.lifecycle.UpdateLifecycleObserver
import dev.mnascimentos.aureole.core.lifecycle.WidgetLifecycleObserver
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.MainRoute
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.isBlocking
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.isOverlayActive
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    internal val viewModel: HomeViewModel by viewModel()

    internal lateinit var appWidgetManager: AppWidgetManager
    internal lateinit var appWidgetHost: AppWidgetHost

    internal lateinit var appUpdateManager: AppUpdateManager
    internal var cachedAppUpdateInfo: AppUpdateInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APPWIDGET_HOST_ID)

        appUpdateManager = AppUpdateManagerFactory.create(this)

        lifecycle.addObserver(UpdateLifecycleObserver(appUpdateManager, viewModel) { cachedAppUpdateInfo = it })
        lifecycle.addObserver(WidgetLifecycleObserver(appWidgetHost) { wasInBackground = it })
        lifecycle.addObserver(MainLifecycleObserver(this, viewModel) { wasInBackground = false })

        setupWindowAndBackHandling()
        setupContent()
    }

    private fun setupWindowAndBackHandling() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.decorView.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
                val w = view.width
                val h = view.height
                val isOverlayActive = viewModel.uiState.value.isOverlayActive
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

    private fun updateGestureExclusion(isOverlayActive: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
        val view = window.decorView
        val w = view.width
        val h = view.height
        if (w <= 0 || h <= 0) return
        view.systemGestureExclusionRects = if (isOverlayActive) emptyList() else listOf(Rect(0, 0, w, h))
    }

    private fun setupContent() {
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isOverlayActive = uiState.isOverlayActive

            LaunchedEffect(isOverlayActive) {
                updateGestureExclusion(isOverlayActive)
            }

            MainActivityRootContent(
                uiState = uiState
            )
        }
    }

    @Composable
    private fun MainActivityRootContent(
        uiState: MainUiState
    ) {
        val hazeState = rememberHazeState()
        val isDarkTheme = isSystemInDarkTheme()
        val palette = getPaletteByName(uiState.selectedThemeName, isDarkTheme, uiState.manualSeedColor)

        CompositionLocalProvider(LocalHazeState provides hazeState) {
            AureoleLauncherTheme(
                config = AureoleThemeConfig(
                    darkTheme = isDarkTheme,
                    isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
                    aureoleColors = palette.colors,
                    aureoleFontName = uiState.selectedFontName,
                    aureoleDimens = AureoleDimens(radiusLarge = uiState.cornerRadiusDp.dp)
                )
            ) {
                MainRoute(
                    viewModel = viewModel,
                    appWidgetHost = appWidgetHost,
                    appWidgetManager = appWidgetManager,
                    appUpdateManager = appUpdateManager,
                    cachedAppUpdateInfo = cachedAppUpdateInfo
                )
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
            } else if (state.onboarding.isBlocking) {
                return
            } else if (state.homeButtonOpensAllApps) {
                viewModel.setAllAppsDrawerOpen(!state.isAllAppsDrawerOpen, fromHomeButton = true)
            } else {
                viewModel.setAllAppsDrawerOpen(false)
            }
        }
    }

    companion object {
        private const val APPWIDGET_HOST_ID = 1024
    }
}
