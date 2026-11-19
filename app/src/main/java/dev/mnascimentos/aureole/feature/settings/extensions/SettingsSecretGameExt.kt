package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.feature.settings.SettingsViewModel

/** Saves the hidden game's score when it is a new best and publishes the best score. */
fun SettingsViewModel.onSecretGameOver(score: Int) {
    settingsRepository.updateSecretBestScore(score)
    updateUiState { it.copy(secretBestScore = settingsRepository.secretBestScore) }
}
