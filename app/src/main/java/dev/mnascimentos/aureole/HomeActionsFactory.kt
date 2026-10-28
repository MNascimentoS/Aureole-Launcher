package dev.mnascimentos.aureole

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions.onSearchQueryChanged
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.closeEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.openEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.resetClockSettings
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toggleClockBackground
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockAlignment
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockBackgroundColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockCustomGreeting
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockDateFormat
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockFontFamily
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockStyle
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTextColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTimeFormat
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.closeEditContainerDialog
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.deleteContainerInstance
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.openEditContainerDialog
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.removeAppFromContainer
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.saveContainerModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.onFolderIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.removeAppFromFolder
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateContainerFavorites
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateFavoritePackages
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.closeContainerAppBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.closeContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.closeFolderAppBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.closeWidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.dismissReleaseNotes
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.openContainerAppBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.openContainerFolderBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.openEditFolderForFolder
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.openFolderAppBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.openWidgetStackBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.toggleWidgetStackDots
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseQuickButtonSettings
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onCloseShortcutSelection
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenQuickButtonSettings
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onOpenShortcutSelection
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.closeWidgetPopup
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.openWidgetPopup
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.reorderWidgets
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setIsAddingSingleWidget
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetPicker
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setShowWidgetResizeDialog
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setTargetWidgetStackId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.setWidgetRowHeight
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setShowCustomizeBottomSheet
import dev.mnascimentos.aureole.feature.home.extensions.toggleShowAllAppsOnHome
import dev.mnascimentos.aureole.feature.home.grid.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.dismissGridError
import dev.mnascimentos.aureole.feature.home.grid.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.openAddContainerForParent
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeChildFromScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.resetGridItems
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.setEditingGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.setShowAddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateGridItemsOrientation
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateScrollViewOrientation
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemSpec
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

class HomeActionsFactory(
    private val context: Context,
    private val viewModel: HomeViewModel,
    private val appUpdateManager: AppUpdateManager,
    private val updateActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val cachedAppUpdateInfo: AppUpdateInfo? = null,
) {
    fun createHomeActions(): HomeScreenActions {
        val baseActions = HomeScreenActions(
            onWidgetRowHeightChanged = { viewModel.setWidgetRowHeight(it) },
            onAddWidgetClick = { stackId ->
                viewModel.setTargetWidgetStackId(stackId)
                viewModel.setShowWidgetPicker(true)
            },
            onRemoveWidgetClick = { widgetId, stackId ->
                viewModel.removeWidgetId(widgetId, stackId)
            },
            onReorderWidgets = { newWidgets, stackId ->
                viewModel.reorderWidgets(newWidgets, stackId)
            },
            onAppClick = { appInfo ->
                context.packageManager.getLaunchIntentForPackage(appInfo.packageName)?.let { launchIntent ->
                    context.startActivity(launchIntent)
                }
            },
            onExpandNotificationShade = { IntentUtils.expandNotificationShade(context) },
            onFolderIntent = { viewModel.onFolderIntent(it) },
            onSetAddAppToFolderDialogVisible = { viewModel.setAddAppToFolderDialogVisible(it) },
            onSetRenameFolderDialogVisible = { viewModel.setRenameFolderDialogVisible(it) },
            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onSettingsClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                context.startActivity(intent)
            },
            onAllAppsDrawerClose = { viewModel.setAllAppsDrawerOpen(false) },
            onAllAppsDrawerOpen = { viewModel.setAllAppsDrawerOpen(true) },
            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
            onToggleFavoriteWithContainer = { pkg, containerId -> viewModel.toggleFavorite(pkg, containerId) },
            onUpdateFavoritePackages = { containerId, pkgs -> handleUpdateFavoritePackages(containerId, pkgs) },
            onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
            onAppInfoClick = { app -> IntentUtils.openAppInfo(context, app.packageName) },
            onUninstallAppClick = { app -> IntentUtils.uninstallApp(context, app.packageName) },
            onOpenFavoritePicker = { containerId -> viewModel.setShowFavoritePicker(true, containerId) },
            onDismissFavoritePicker = { viewModel.setShowFavoritePicker(false) },
            onDismissWidgetPicker = { viewModel.setShowWidgetPicker(false) },
            onDismissReleaseNotes = { viewModel.dismissReleaseNotes() },
            onOpenWidgetPopup = { id, y -> viewModel.openWidgetPopup(id, y) },
            onCloseWidgetPopup = { viewModel.closeWidgetPopup() },
            onOpenWidgetResizeDialog = { viewModel.setShowWidgetResizeDialog(true) },
            onCloseWidgetResizeDialog = { viewModel.closeWidgetPopup() },
            onResizeWidgetHeight = { viewModel.setWidgetRowHeight(it) },
            onStartInAppUpdate = createStartUpdateAction(),
            onCompleteInAppUpdate = { handleCompleteInAppUpdate() },
            onDismissUpdateDialog = { handleDismissUpdateDialog() }
        )

        val gridBase = attachGridActions(baseActions)
        return gridBase
    }

    private fun attachGridActions(base: HomeScreenActions): HomeScreenActions {
        val gridBase = base.copy(
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
                viewModel.resizeChildInScrollView(pId, cId, cSpan, rSpan)
            },
            onOpenAddContainerForParent = { pId -> viewModel.openAddContainerForParent(pId) },
            onOpenEditContainerDialog = { id -> viewModel.openEditContainerDialog(id) },
            onCloseEditContainerDialog = { viewModel.closeEditContainerDialog() },
            onSaveContainerModel = { model -> viewModel.saveContainerModel(model) },
            onDeleteContainerInstance = { id -> viewModel.deleteContainerInstance(id) }
        )
        return attachContainerAndSheetActions(gridBase)
    }

    private fun attachContainerAndSheetActions(base: HomeScreenActions): HomeScreenActions {
        return base.copy(
            onOpenContainerAppBottomSheet = { app, panelId -> viewModel.openContainerAppBottomSheet(app, panelId) },
            onCloseContainerAppBottomSheet = { viewModel.closeContainerAppBottomSheet() },
            onOpenContainerFolderBottomSheet = { folder, panelId ->
                viewModel.openContainerFolderBottomSheet(folder, panelId)
            },
            onCloseContainerFolderBottomSheet = { viewModel.closeContainerFolderBottomSheet() },
            onOpenFolderAppBottomSheet = { app, folder -> viewModel.openFolderAppBottomSheet(app, folder) },
            onCloseFolderAppBottomSheet = { viewModel.closeFolderAppBottomSheet() },
            onOpenWidgetStackBottomSheet = { widgetId, stackId ->
                viewModel.openWidgetStackBottomSheet(widgetId, stackId)
            },
            onCloseWidgetStackBottomSheet = { viewModel.closeWidgetStackBottomSheet() },
            onToggleWidgetStackDots = { stackId -> viewModel.toggleWidgetStackDots(stackId) },
            onRemoveAppFromContainer = { panelId, pkg -> viewModel.removeAppFromContainer(panelId, pkg) },
            onRemoveAppFromFolder = { folderId, pkg -> viewModel.removeAppFromFolder(folderId, pkg) },
            onOpenEditFolderForFolder = { folder -> viewModel.openEditFolderForFolder(folder) },
            onOpenCustomizeBottomSheet = { viewModel.setShowCustomizeBottomSheet(true) },
            onCloseCustomizeBottomSheet = { viewModel.setShowCustomizeBottomSheet(false) },
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
            onResetClockSettings = { viewModel.resetClockSettings() },
            onOpenQuickButtonSettings = { id -> viewModel.onOpenQuickButtonSettings(id) },
            onCloseQuickButtonSettings = { viewModel.onCloseQuickButtonSettings() },
            onOpenQuickButtonAppearance = { viewModel.onOpenQuickButtonAppearance() },
            onCloseQuickButtonAppearance = { viewModel.onCloseQuickButtonAppearance() },
            onOpenShortcutSelection = { type -> viewModel.onOpenShortcutSelection(type) },
            onCloseShortcutSelection = { viewModel.onCloseShortcutSelection() },
            onUpdateQuickButtonAppearance = { app -> viewModel.onUpdateQuickButtonAppearance(app) },
            onUpdateQuickButtonShortcuts = { shortcuts -> viewModel.onUpdateQuickButtonShortcuts(shortcuts) }
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
        viewModel.isUpdateInProgressOrDismissed = true
        viewModel.setShowUpdateAvailableDialog(visible = false)
        viewModel.setShowUpdateDownloadedDialog(visible = false)
    }

    private fun createStartUpdateAction(): () -> Unit = {
        viewModel.isUpdateInProgressOrDismissed = true
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
