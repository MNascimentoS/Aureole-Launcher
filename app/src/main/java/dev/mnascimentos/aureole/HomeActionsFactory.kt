package dev.mnascimentos.aureole

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.GridItemSpec
import dev.mnascimentos.aureole.feature.home.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.extensions.closeEditSidePanelDialog
import dev.mnascimentos.aureole.feature.home.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.extensions.deleteSidePanelInstance
import dev.mnascimentos.aureole.feature.home.extensions.dismissGridError
import dev.mnascimentos.aureole.feature.home.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.extensions.moveGridItem
import dev.mnascimentos.aureole.feature.home.extensions.onFolderIntent
import dev.mnascimentos.aureole.feature.home.extensions.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.extensions.openAddContainerForParent
import dev.mnascimentos.aureole.feature.home.extensions.openEditSidePanelDialog
import dev.mnascimentos.aureole.feature.home.extensions.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.removeChildFromScrollView
import dev.mnascimentos.aureole.feature.home.extensions.resetGridItems
import dev.mnascimentos.aureole.feature.home.extensions.resizeChildInScrollView
import dev.mnascimentos.aureole.feature.home.extensions.resizeGridItem
import dev.mnascimentos.aureole.feature.home.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.extensions.saveSidePanelModel
import dev.mnascimentos.aureole.feature.home.extensions.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setEditingGridItem
import dev.mnascimentos.aureole.feature.home.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.extensions.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setShowAddContainerDialog
import dev.mnascimentos.aureole.feature.home.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.extensions.toggleShowAllAppsOnHome
import dev.mnascimentos.aureole.feature.home.extensions.updateContainerFavorites
import dev.mnascimentos.aureole.feature.home.extensions.updateFavoritePackages
import dev.mnascimentos.aureole.feature.home.extensions.updateGridItemsOrientation
import dev.mnascimentos.aureole.feature.home.extensions.updateScrollViewOrientation
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.widget.WidgetHostManager
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

class HomeActionsFactory(
    private val activity: MainActivity,
    private val viewModel: HomeViewModel,
    private val widgetHostManager: WidgetHostManager,
    private val appUpdateManager: AppUpdateManager,
    private val updateActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val cachedAppUpdateInfo: AppUpdateInfo?
) {
    fun createHomeActions(): HomeScreenActions {
        return HomeScreenActions(
            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
            onAddWidgetClick = { viewModel.setShowWidgetPicker(true) },
            onRemoveWidgetClick = { widgetId -> widgetHostManager.removeWidget(widgetId) },
            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) },
            onExpandNotificationShade = { IntentUtils.expandNotificationShade(activity) },
            onFolderIntent = { intent -> viewModel.onFolderIntent(intent) },
            onSetAddAppToFolderDialogVisible = { visible ->
                viewModel.setAddAppToFolderDialogVisible(visible)
            },
            onSetRenameFolderDialogVisible = { visible ->
                viewModel.setRenameFolderDialogVisible(visible)
            },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onSettingsClick = {
                activity.startActivity(Intent(activity, SettingsActivity::class.java))
            },
            onAllAppsDrawerClose = { viewModel.setAllAppsDrawerOpen(false) },
            onAllAppsDrawerOpen = { viewModel.setAllAppsDrawerOpen(true) },
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onUpdateFavoritePackages = { containerId, pkgs ->
                if (containerId != null) {
                    viewModel.updateContainerFavorites(containerId, pkgs)
                } else {
                    viewModel.updateFavoritePackages(pkgs)
                }
            },
            onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
            onAppInfoClick = { app -> IntentUtils.openAppInfo(activity, app.packageName) },
            onOpenFavoritePicker = { containerId -> viewModel.setShowFavoritePicker(true, containerId) },
            onOpenWidgetPopup = { widgetId, topY -> viewModel.openWidgetPopup(widgetId, topY) },
            onCloseWidgetPopup = { viewModel.closeWidgetPopup() },
            onOpenWidgetResizeDialog = { viewModel.setShowWidgetResizeDialog(true) },
            onCloseWidgetResizeDialog = { viewModel.closeWidgetPopup() },
            onResizeWidgetHeight = { height -> viewModel.setWidgetRowHeight(height) },
            onStartInAppUpdate = createStartUpdateAction(),
            onCompleteInAppUpdate = {
                viewModel.setShowUpdateDownloadedDialog(visible = false)
                appUpdateManager.completeUpdate()
            },
            onDismissUpdateDialog = {
                viewModel.setShowUpdateAvailableDialog(visible = false)
                viewModel.setShowUpdateDownloadedDialog(visible = false)
            },
            onEnterGridEditMode = { viewModel.enterGridEditMode() },
            onCancelGridEditMode = { viewModel.cancelGridEditMode() },
            onSaveGridEditMode = { viewModel.saveGridEditMode() },
            onUpdateGridOrientation = { cols, rows ->
                viewModel.updateGridItemsOrientation(cols, rows)
            },
            onMoveGridItem = { id, col, row -> viewModel.moveGridItem(id, col, row) },
            onResizeGridItem = { id, colSpan, rowSpan ->
                viewModel.resizeGridItem(id, colSpan, rowSpan)
            },
            onResetGridItems = { viewModel.resetGridItems() },
            onOpenAddContainerDialog = { viewModel.setShowAddContainerDialog(true) },
            onCloseAddContainerDialog = { viewModel.setShowAddContainerDialog(false) },
            onAddGridItem = { type, widgetId ->
                viewModel.addGridItem(GridItemSpec(type = type, widgetId = widgetId))
            },
            onOpenEditContainerDialog = { item -> viewModel.setEditingGridItem(item) },
            onCloseEditContainerDialog = { viewModel.setEditingGridItem(null) },
            onDeleteGridItem = { id -> viewModel.deleteGridItem(id) },
            onDismissGridError = { viewModel.dismissGridError() },
            onSetIsAddingSingleWidget = { viewModel.setIsAddingSingleWidget(it) },
            onUpdateScrollViewOrientation = { id, orientation ->
                viewModel.updateScrollViewOrientation(id, orientation)
            },
            onRemoveChildFromScrollView = { parentId, childId ->
                viewModel.removeChildFromScrollView(parentId, childId)
            },
            onResizeChildInScrollView = { parentId, childId, colSpan, rowSpan ->
                viewModel.resizeChildInScrollView(parentId, childId, colSpan, rowSpan)
            },
            onOpenAddContainerForParent = { parentId ->
                viewModel.openAddContainerForParent(parentId)
            },
            onOpenEditSidePanelDialog = { id -> viewModel.openEditSidePanelDialog(id) },
            onCloseEditSidePanelDialog = { viewModel.closeEditSidePanelDialog() },
            onSaveSidePanelModel = { model -> viewModel.saveSidePanelModel(model) },
            onDeleteSidePanelInstance = { id -> viewModel.deleteSidePanelInstance(id) }
        )
    }

    private fun createStartUpdateAction(): () -> Unit = {
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
    }
}
