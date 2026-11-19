package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/** What the Settings root needs to host the hidden game: what is open, where the logo is and the current frame. */
class SecretGameHostState {
    var isGameOpen by mutableStateOf(false)
    var showBestScore by mutableStateOf(false)
    var frame by mutableStateOf<SecretGameFrame?>(null)
    var logoBounds by mutableStateOf<Rect?>(null)
    var rootOrigin by mutableStateOf(Offset.Zero)
    var counter by mutableStateOf(SecretTapCounter())

    /** Alpha of the Settings content: it fades out as the game takes the screen. */
    val settingsAlpha: Float get() = frame?.settingsAlpha ?: 1f

    /** Opens the game once the Settings logo has been measured. */
    fun open() {
        isGameOpen = logoBounds != null
    }

    fun finish() {
        isGameOpen = false
        frame = null
    }

    /** Measures the Settings logo and hides it while the game draws its own copy. */
    val logoModifier: Modifier
        get() = Modifier
            .onGloballyPositioned { logoBounds = it.boundsInWindow() }
            .graphicsLayer { alpha = if (frame?.settingsLogoVisible == false) 0f else 1f }

    val rootModifier: Modifier
        get() = Modifier.onGloballyPositioned { rootOrigin = it.positionInWindow() }
}
