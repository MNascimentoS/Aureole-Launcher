package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import dev.mnascimentos.aureole.feature.secretgame.audio.SecretSoundPlayer

/** The [SecretSoundPlayer] of this screen: loaded once and released when the screen leaves. */
@Composable
fun rememberSecretSoundPlayer(): SecretSoundPlayer {
    val context = LocalContext.current.applicationContext
    val isPreview = LocalInspectionMode.current
    val player = remember { SecretSoundPlayer(context, isEnabled = !isPreview) }
    DisposableEffect(player) { onDispose { player.release() } }
    return player
}
