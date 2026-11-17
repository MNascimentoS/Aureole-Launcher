package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.glassBlurRadius

private const val BACKDROP_TINT_ALPHA = 0.55f

/**
 * Dimmed backdrop behind an overlay sheet: the screen blurred through [hazeState], or plain black without blur.
 * Draws only; closing on a tap outside is up to the overlay.
 */
@Composable
fun AureoleOverlayBackdrop(hazeState: HazeState?, modifier: Modifier = Modifier) {
    val surface = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = glassBlurRadius(AureoleDS.dimens.xLarge),
                tint = HazeTint(Color.Black.copy(alpha = BACKDROP_TINT_ALPHA))
            )
        ) { blurEnabled = true }
    } else {
        Modifier.background(Color.Black)
    }
    Box(modifier = modifier.then(surface))
}

@AureolePreview
@Composable
private fun AureoleOverlayBackdropPreview() {
    AureoleLauncherTheme {
        AureoleOverlayBackdrop(hazeState = null, modifier = Modifier.fillMaxSize())
    }
}
