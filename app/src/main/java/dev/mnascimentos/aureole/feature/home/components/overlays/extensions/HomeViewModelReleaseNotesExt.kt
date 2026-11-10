package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.checkReleaseNotes(currentVersionCode: Int, versionName: String) {
    activeVersionCode = currentVersionCode
    val lastSeen = settingsRepository.lastSeenVersionCode
    updateUiState { it.copy(currentVersionName = versionName) }
    if (lastSeen >= currentVersionCode) return
    when {
        // A fresh install has nothing new to read: record the version silently.
        lastSeen == 0 -> settingsRepository.lastSeenVersionCode = currentVersionCode
        uiState.value.onboarding.stage == OnboardingStage.COMPLETED -> {
            updateUiState { it.copy(showReleaseNotesBottomSheet = true) }
            settingsRepository.lastSeenVersionCode = currentVersionCode
        }
        // Otherwise the notes wait, unseen, for the first resume after the onboarding.
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
