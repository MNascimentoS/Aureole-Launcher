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
import dev.mnascimentos.aureole.feature.home.extensions.closeContainerAppBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.closeContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.closeEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.closeEditContainerDialog
import dev.mnascimentos.aureole.feature.home.extensions.closeFolderAppBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.closeWidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.deleteContainerInstance
import dev.mnascimentos.aureole.feature.home.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.extensions.dismissGridError
import dev.mnascimentos.aureole.feature.home.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.extensions.moveChildInScrollView
import dev.mnascimentos.aureole.feature.home.extensions.moveGridItem
import dev.mnascimentos.aureole.feature.home.extensions.onFolderIntent
import dev.mnascimentos.aureole.feature.home.extensions.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.extensions.openAddContainerForParent
import dev.mnascimentos.aureole.feature.home.extensions.openContainerAppBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.openContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.openEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.openEditContainerDialog
import dev.mnascimentos.aureole.feature.home.extensions.openEditFolderForFolder
import dev.mnascimentos.aureole.feature.home.extensions.openFolderAppBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.extensions.openWidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.removeAppFromContainer
import dev.mnascimentos.aureole.feature.home.extensions.removeAppFromFolder
import dev.mnascimentos.aureole.feature.home.extensions.removeChildFromScrollView
import dev.mnascimentos.aureole.feature.home.extensions.resetClockSettings
import dev.mnascimentos.aureole.feature.home.extensions.resetGridItems
import dev.mnascimentos.aureole.feature.home.extensions.resizeChildInScrollView
import dev.mnascimentos.aureole.feature.home.extensions.resizeGridItem
import dev.mnascimentos.aureole.feature.home.extensions.saveContainerModel
import dev.mnascimentos.aureole.feature.home.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.extensions.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setEditingGridItem
import dev.mnascimentos.aureole.feature.home.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.extensions.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.extensions.setShowAddContainerDialog
import dev.mnascimentos.aureole.feature.home.extensions.setShowCustomizeBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.extensions.toggleClockBackground
import dev.mnascimentos.aureole.feature.home.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.extensions.toggleShowAllAppsOnHome
import dev.mnascimentos.aureole.feature.home.extensions.toggleWidgetStackDots
import dev.mnascimentos.aureole.feature.home.extensions.updateClockAlignment
import dev.mnascimentos.aureole.feature.home.extensions.updateClockBackgroundColor
import dev.mnascimentos.aureole.feature.home.extensions.updateClockCustomGreeting
import dev.mnascimentos.aureole.feature.home.extensions.updateClockDateFormat
import dev.mnascimentos.aureole.feature.home.extensions.updateClockFontFamily
import dev.mnascimentos.aureole.feature.home.extensions.updateClockStyle
import dev.mnascimentos.aureole.feature.home.extensions.updateClockTextColor
import dev.mnascimentos.aureole.feature.home.extensions.updateClockTimeFormat
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
        val actions = HomeScreenActions(
            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
            onAddWidgetClick = { viewModel.setShowWidgetPicker(true) },
            onRemoveWidgetClick = { widgetId -> widgetHostManager.removeWidget(widgetId) },
            onAppClick = { appInfo -> viewModel.launchApp(appInfo.componentName) },
            onExpandNotificationShade = { IntentUtils.expandNotificationShade(activity) },
            onFolderIntent = { intent -> viewModel.onFolderIntent(intent) },
            onSetAddAppToFolderDialogVisible = { visible -> viewModel.setAddAppToFolderDialogVisible(visible) },
            onSetRenameFolderDialogVisible = { visible -> viewModel.setRenameFolderDialogVisible(visible) },
            onSearchQueryChanged = { query -> viewModel.onSearchQueryChanged(query) },
            onSettingsClick = { activity.startActivity(Intent(activity, SettingsActivity::class.java)) },
            onAllAppsDrawerClose = { viewModel.setAllAppsDrawerOpen(false) },
            onAllAppsDrawerOpen = { viewModel.setAllAppsDrawerOpen(true) },
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onUpdateFavoritePackages = { cId, pkgs -> handleUpdateFavoritePackages(cId, pkgs) },
            onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
            onAppInfoClick = { app -> IntentUtils.openAppInfo(activity, app.packageName) },
            onUninstallAppClick = { app -> IntentUtils.uninstallApp(activity, app.packageName) },
            onOpenFavoritePicker = { containerId -> viewModel.setShowFavoritePicker(true, containerId) },
            onOpenWidgetPopup = { widgetId, topY -> viewModel.openWidgetPopup(widgetId, topY) },
            onCloseWidgetPopup = { viewModel.closeWidgetPopup() },
            onOpenWidgetResizeDialog = { viewModel.setShowWidgetResizeDialog(true) },
            onCloseWidgetResizeDialog = { viewModel.closeWidgetPopup() },
            onResizeWidgetHeight = { height -> viewModel.setWidgetRowHeight(height) },
            onStartInAppUpdate = createStartUpdateAction(),
            onCompleteInAppUpdate = { handleCompleteInAppUpdate() },
            onDismissUpdateDialog = { handleDismissUpdateDialog() }
        )
        return attachGridActions(actions)
    }

    private fun attachGridActions(base: HomeScreenActions): HomeScreenActions {
        return base.copy(
            onEnterGridEditMode = { viewModel.enterGridEditMode() },
            onCancelGridEditMode = { viewModel.cancelGridEditMode() },
            onSaveGridEditMode = { viewModel.saveGridEditMode() },
            onUpdateGridOrientation = { cols, rows -> viewModel.updateGridItemsOrientation(cols, rows) },
            onMoveGridItem = { id, col, row -> viewModel.moveGridItem(id, col, row) },
            onResizeGridItem = { id, colSpan, rowSpan -> viewModel.resizeGridItem(id, colSpan, rowSpan) },
            onResetGridItems = { viewModel.resetGridItems() },
            onOpenAddContainerDialog = { viewModel.setShowAddContainerDialog(true) },
            onCloseAddContainerDialog = { viewModel.setShowAddContainerDialog(false) },
            onAddGridItem = { type, widgetId, orientation ->
                viewModel.addGridItem(GridItemSpec(type = type, widgetId = widgetId, scrollOrientation = orientation))
            },
            onOpenEditGridItemDialog = { item -> viewModel.setEditingGridItem(item) },
            onCloseEditGridItemDialog = { viewModel.setEditingGridItem(null) },
            onDeleteGridItem = { id -> viewModel.deleteGridItem(id) },
            onDismissGridError = { viewModel.dismissGridError() },
            onSetIsAddingSingleWidget = { viewModel.setIsAddingSingleWidget(it) },
            onUpdateScrollViewOrientation = { id, o -> viewModel.updateScrollViewOrientation(id, o) },
            onRemoveChildFromScrollView = { pId, cId -> viewModel.removeChildFromScrollView(pId, cId) },
            onMoveChildInScrollView = { pId, cId, moveUp -> viewModel.moveChildInScrollView(pId, cId, moveUp) },
            onResizeChildInScrollView = { pId, cId, cSpan, rSpan ->
                viewModel.resizeChildInScrollView(
                    pId,
                    cId,
                    cSpan,
                    rSpan
                )
            },
            onOpenAddContainerForParent = { pId -> viewModel.openAddContainerForParent(pId) },
            onOpenEditContainerDialog = { id -> viewModel.openEditContainerDialog(id) },
            onCloseEditContainerDialog = { viewModel.closeEditContainerDialog() },
            onSaveContainerModel = { model -> viewModel.saveContainerModel(model) },
            onDeleteContainerInstance = { id -> viewModel.deleteContainerInstance(id) },

            // Contextual Bottom Sheets
            onOpenContainerAppBottomSheet = { app, panelId -> viewModel.openContainerAppBottomSheet(app, panelId) },
            onCloseContainerAppBottomSheet = { viewModel.closeContainerAppBottomSheet() },
            onOpenContainerFolderBottomSheet = { folder, panelId ->
                viewModel.openContainerFolderBottomSheet(
                    folder,
                    panelId
                )
            },
            onCloseContainerFolderBottomSheet = { viewModel.closeContainerFolderBottomSheet() },
            onOpenFolderAppBottomSheet = { app, folder -> viewModel.openFolderAppBottomSheet(app, folder) },
            onCloseFolderAppBottomSheet = { viewModel.closeFolderAppBottomSheet() },
            onOpenWidgetStackBottomSheet = { widgetId, stackId ->
                viewModel.openWidgetStackBottomSheet(
                    widgetId,
                    stackId
                )
            },
            onCloseWidgetStackBottomSheet = { viewModel.closeWidgetStackBottomSheet() },
            onToggleWidgetStackDots = { stackId -> viewModel.toggleWidgetStackDots(stackId) },
            onRemoveAppFromContainer = { panelId, pkg -> viewModel.removeAppFromContainer(panelId, pkg) },
            onRemoveAppFromFolder = { folderId, pkg -> viewModel.removeAppFromFolder(folderId, pkg) },
            onOpenEditFolderForFolder = { folder -> viewModel.openEditFolderForFolder(folder) },
            onOpenCustomizeBottomSheet = { viewModel.setShowCustomizeBottomSheet(true) },
            onCloseCustomizeBottomSheet = { viewModel.setShowCustomizeBottomSheet(false) },

            // Clock Customization Actions
            onOpenEditClockBottomSheet = { viewModel.openEditClockBottomSheet() },
            onCloseEditClockBottomSheet = { viewModel.closeEditClockBottomSheet() },
            onUpdateClockStyle = { style -> viewModel.updateClockStyle(style) },
            onUpdateClockCustomGreeting = { greeting -> viewModel.updateClockCustomGreeting(greeting) },
            onUpdateClockAlignment = { alignment -> viewModel.updateClockAlignment(alignment) },
            onUpdateClockFontFamily = { fontFamily -> viewModel.updateClockFontFamily(fontFamily) },
            onUpdateClockTimeFormat = { format -> viewModel.updateClockTimeFormat(format) },
            onUpdateClockDateFormat = { format -> viewModel.updateClockDateFormat(format) },
            onUpdateClockTextColor = { color -> viewModel.updateClockTextColor(color) },
            onUpdateClockBackgroundColor = { color -> viewModel.updateClockBackgroundColor(color) },
            onToggleClockBackground = { viewModel.toggleClockBackground() },
            onResetClockSettings = { viewModel.resetClockSettings() }
        )
    }

    private fun handleUpdateFavoritePackages(containerId: String?, pkgs: List<String>) {
        if (containerId != null) {
            viewModel.updateContainerFavorites(containerId, pkgs)
        } else {
            viewModel.updateFavoritePackages(pkgs)
        }
    }

    private fun handleCompleteInAppUpdate() {
        viewModel.setShowUpdateDownloadedDialog(visible = false)
        appUpdateManager.completeUpdate()
    }

    private fun handleDismissUpdateDialog() {
        viewModel.setShowUpdateAvailableDialog(visible = false)
        viewModel.setShowUpdateDownloadedDialog(visible = false)
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
