package dev.mnascimentos.aureole.feature.home

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.Context
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
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
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

    BackHandler(enabled = uiState.isOverlayActive) {
        handleBackNavigation(uiState, viewModel)
    }

    val widgetHostManager = rememberWidgetHostManager(
        viewModel = viewModel,
        appWidgetHost = appWidgetHost,
        appWidgetManager = appWidgetManager
    )

    val homeActions = rememberHomeActions(
        context = context,
        viewModel = viewModel,
        appUpdateManager = appUpdateManager,
        cachedAppUpdateInfo = cachedAppUpdateInfo
    )

    CompositionLocalProvider(
        LocalHomeUiState provides uiState,
        LocalHomeActions provides homeActions
    ) {
        MainScaffold(
            appWidgetHost = appWidgetHost,
            onWidgetSelected = { widgetHostManager.handleWidgetSelected(it) }
        )
    }
}

@Composable
private fun rememberWidgetHostManager(
    viewModel: HomeViewModel,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager
): WidgetHostManager {
    val initialManager = remember(viewModel, appWidgetHost, appWidgetManager) {
        WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager
        )
    }

    val configureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleWidgetResult(result.resultCode, viewModel, appWidgetHost, appWidgetManager, initialManager)
    }

    val bindLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleWidgetResult(result.resultCode, viewModel, appWidgetHost, appWidgetManager, initialManager)
    }

    return remember(initialManager, configureLauncher, bindLauncher) {
        WidgetHostManager(
            viewModel = viewModel,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            launchWidgetConfigure = { intent -> configureLauncher.launch(intent) },
            launchWidgetBind = { intent -> bindLauncher.launch(intent) }
        )
    }
}

private fun handleWidgetResult(
    resultCode: Int,
    viewModel: HomeViewModel,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    manager: WidgetHostManager
) {
    val pendingWidgetId = viewModel.pendingWidgetId
    if (pendingWidgetId != -1) {
        if (resultCode == Activity.RESULT_OK) {
            val appWidgetInfo = appWidgetManager.getAppWidgetInfo(pendingWidgetId)
            manager.completeWidgetConfiguration(pendingWidgetId, appWidgetInfo)
        } else {
            appWidgetHost.deleteAppWidgetId(pendingWidgetId)
            viewModel.setPendingWidgetId(-1)
            viewModel.setIsAddingSingleWidget(false)
        }
    }
}

@Composable
private fun rememberHomeActions(
    context: Context,
    viewModel: HomeViewModel,
    appUpdateManager: AppUpdateManager,
    cachedAppUpdateInfo: AppUpdateInfo?
): HomeScreenActions {
    val updateLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            Timber.tag("MainRoute").w("In-app update flow failed or was cancelled by user: ${result.resultCode}")
        }
    }

    return remember(context, viewModel, appUpdateManager, updateLauncher, cachedAppUpdateInfo) {
        HomeActionsFactory(
            context = context,
            viewModel = viewModel,
            appUpdateManager = appUpdateManager,
            updateActivityResultLauncher = updateLauncher,
            cachedAppUpdateInfo = cachedAppUpdateInfo
        ).createHomeActions()
    }
}
