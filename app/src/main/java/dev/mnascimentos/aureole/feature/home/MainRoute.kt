package dev.mnascimentos.aureole.feature.home

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import dev.mnascimentos.aureole.HomeActionsFactory
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetHostManager
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setPendingWidgetId
import dev.mnascimentos.aureole.feature.home.extensions.handleBackNavigation
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.isOverlayActive
import timber.log.Timber

@Composable
fun MainRoute(
    viewModel: HomeViewModel,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    appUpdateManager: AppUpdateManager,
    cachedAppUpdateInfo: AppUpdateInfo?
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isOverlayActive = checkOverlayActive(uiState)

    BackHandler(enabled = isOverlayActive) {
        handleBackNavigation(uiState, viewModel)
    }

    val widgetHostManager = remember(context, viewModel, appWidgetHost, appWidgetManager) {
        var hostManager: WidgetHostManager? = null
        hostManager = WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetConfigure = { intent -> /* To be set later via launchers */ },
            launchWidgetBind = { intent -> /* To be set later via launchers */ }
        )
        hostManager
    }

    val widgetConfigureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val pendingWidgetId = viewModel.pendingWidgetId
        if (pendingWidgetId != -1) {
            if (result.resultCode == Activity.RESULT_OK) {
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
                widgetHostManager.completeWidgetConfiguration(pendingWidgetId, appWidgetInfo)
            } else {
                appWidgetHost.deleteAppWidgetId(pendingWidgetId)
                viewModel.setPendingWidgetId(-1)
                viewModel.setIsAddingSingleWidget(false)
            }
        }
    }

    val widgetBindLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val pendingWidgetId = viewModel.pendingWidgetId
        if (pendingWidgetId != -1) {
            if (result.resultCode == Activity.RESULT_OK) {
                val appWidgetInfo = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
                widgetHostManager.completeWidgetConfiguration(pendingWidgetId, appWidgetInfo)
            } else {
                appWidgetHost.deleteAppWidgetId(pendingWidgetId)
                viewModel.setPendingWidgetId(-1)
                viewModel.setIsAddingSingleWidget(false)
            }
        }
    }

    // Update the WidgetHostManager with the real launchers now that they are created
    val actualWidgetHostManager = remember(widgetHostManager, widgetConfigureLauncher, widgetBindLauncher) {
        WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetConfigure = { intent -> widgetConfigureLauncher.launch(intent) },
            launchWidgetBind = { intent -> widgetBindLauncher.launch(intent) }
        )
    }

    val updateActivityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            Timber.tag("MainRoute").w("In-app update flow failed or was cancelled by user: ${result.resultCode}")
        }
    }

    val homeActions = remember(
        context,
        viewModel,
        appUpdateManager,
        updateActivityResultLauncher,
        cachedAppUpdateInfo
    ) {
        HomeActionsFactory(
            context = context,
            viewModel = viewModel,
            appUpdateManager = appUpdateManager,
            updateActivityResultLauncher = updateActivityResultLauncher,
            cachedAppUpdateInfo = cachedAppUpdateInfo
        ).createHomeActions()
    }

    CompositionLocalProvider(
        LocalHomeUiState provides uiState,
        LocalHomeActions provides homeActions
    ) {
        MainScaffold(
            appWidgetHost = appWidgetHost,
            viewModel = viewModel,
            onWidgetSelected = { actualWidgetHostManager.handleWidgetSelected(it) }
        )
    }
}

private fun checkOverlayActive(uiState: MainUiState): Boolean {
    return uiState.isOverlayActive
}
