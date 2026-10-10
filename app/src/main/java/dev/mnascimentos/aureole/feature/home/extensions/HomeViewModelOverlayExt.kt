package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.ContainerAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.ContainerFolderBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.FolderAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.WidgetStackBottomSheetState

fun HomeViewModel.checkReleaseNotes(currentVersionCode: Int, versionName: String) {
    activeVersionCode = currentVersionCode
    val lastSeen = settingsRepository.lastSeenVersionCode
    updateUiState { it.copy(currentVersionName = versionName) }
    if (lastSeen < currentVersionCode) {
        updateUiState { it.copy(showReleaseNotesBottomSheet = true) }
        settingsRepository.lastSeenVersionCode = currentVersionCode
    }
}

fun HomeViewModel.dismissReleaseNotes(versionCode: Int = activeVersionCode) {
    if (versionCode > 0) {
        settingsRepository.lastSeenVersionCode = versionCode
    }
    updateUiState { it.copy(showReleaseNotesBottomSheet = false) }
}

fun HomeViewModel.openReleaseNotes() {
    updateUiState { it.copy(showReleaseNotesBottomSheet = true) }
}

fun HomeViewModel.openContainerAppBottomSheet(app: AppInfo, panelId: String) {
    updateUiState { it.copy(activeContainerAppBottomSheet = ContainerAppBottomSheetState(app, panelId)) }
}

fun HomeViewModel.closeContainerAppBottomSheet() {
    updateUiState { it.copy(activeContainerAppBottomSheet = null) }
}

fun HomeViewModel.openContainerFolderBottomSheet(folder: AppFolder, panelId: String) {
    updateUiState { it.copy(activeContainerFolderBottomSheet = ContainerFolderBottomSheetState(folder, panelId)) }
}

fun HomeViewModel.closeContainerFolderBottomSheet() {
    updateUiState { it.copy(activeContainerFolderBottomSheet = null) }
}

fun HomeViewModel.openFolderAppBottomSheet(app: AppInfo, folder: AppFolder) {
    updateUiState { it.copy(activeFolderAppBottomSheet = FolderAppBottomSheetState(app, folder)) }
}

fun HomeViewModel.closeFolderAppBottomSheet() {
    updateUiState { it.copy(activeFolderAppBottomSheet = null) }
}

fun HomeViewModel.openWidgetStackBottomSheet(widgetId: Int?, stackId: String) {
    updateUiState { it.copy(activeWidgetStackBottomSheet = WidgetStackBottomSheetState(widgetId, stackId)) }
}

fun HomeViewModel.closeWidgetStackBottomSheet() {
    updateUiState { it.copy(activeWidgetStackBottomSheet = null) }
}

fun HomeViewModel.toggleWidgetStackDots(stackId: String) {
    val currentMap = uiState.value.widgetStackDots
    val currentVal = currentMap[stackId] ?: widgetRepository.getWidgetDotsState(stackId)
    val newVal = !currentVal
    val updatedMap = currentMap + (stackId to newVal)
    updateUiState { it.copy(widgetStackDots = updatedMap) }
    widgetRepository.setWidgetDotsState(stackId, newVal)
}

fun HomeViewModel.openEditFolderForFolder(folder: AppFolder) {
    updateUiState { it.copy(activeFolder = folder, isRenameFolderDialogVisible = true) }
}

fun HomeViewModel.openEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = true) }
}

fun HomeViewModel.closeEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = false) }
}
