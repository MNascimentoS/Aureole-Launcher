package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.feature.settings.SettingsViewModel

/** Saves the hidden game's score when it is a new best and publishes the best score. */
fun SettingsViewModel.onSecretGameOver(score: Int) {
    settingsRepository.updateSecretBestScore(score)
    updateUiState { it.copy(secretBestScore = settingsRepository.secretBestScore) }
}

/** The hidden game was opened (5th tap): remembers it, so the "Muito obrigado!" shine stops. */
fun SettingsViewModel.onSecretGameOpened() {
    settingsRepository.secretGamePlayed = true
    updateUiState { it.copy(secretGamePlayed = true) }
}

/** Flips the game's sound on/off and saves it. */
fun SettingsViewModel.onSecretSoundToggled() {
    settingsRepository.secretSoundOn = !settingsRepository.secretSoundOn
    updateUiState { it.copy(secretSoundOn = settingsRepository.secretSoundOn) }
}
