package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.feature.home.HomeViewModel

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

fun HomeViewModel.openEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = true) }
}

fun HomeViewModel.closeEditClockBottomSheet() {
    updateUiState { it.copy(showEditClockBottomSheet = false) }
}
