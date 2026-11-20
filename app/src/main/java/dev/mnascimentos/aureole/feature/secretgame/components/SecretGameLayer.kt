package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameCallbacks
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameHostState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameSound
import dev.mnascimentos.aureole.feature.secretgame.model.SecretLogoPlacement

/** The popup of the best score and, while a game is open, the game on top of everything. */
@Composable
fun SecretGameLayer(
    host: SecretGameHostState,
    bestScore: Int,
    sound: SecretGameSound,
    onGameOver: (Int) -> Unit
) {
    if (host.showBestScore) {
        SecretBestScorePopup(
            bestScore = bestScore,
            triggerText = stringResource(R.string.settings_root_thank_you),
            onDismissRequest = { host.showBestScore = false }
        )
    }
    val bounds = host.logoBounds
    if (host.isGameOpen && bounds != null) {
        SecretGameOverlay(
            logo = SecretLogoPlacement(bounds.center - host.rootOrigin, bounds.width),
            bestScore = bestScore,
            sound = sound,
            callbacks = SecretGameCallbacks(
                onFrame = { host.frame = it },
                onGameOver = onGameOver,
                onFinished = host::finish
            )
        )
    }
}
