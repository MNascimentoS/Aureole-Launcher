package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import dev.mnascimentos.aureole.feature.secretgame.model.SecretArena
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout

/**
 * The game on the whole window: the [SecretGameCanvas] sits on the [arena] (the whole window on a phone, a
 * centered column on wider windows) and, unclipped, also paints the background on the sides. Stateless.
 */
@Composable
fun SecretGameStage(
    frame: SecretGameFrame,
    colors: SecretGameColors,
    layout: SecretGameLayout,
    arena: SecretArena,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(modifier = modifier.fillMaxSize()) {
        with(density) {
            SecretGameCanvas(
                frame = frame,
                colors = colors,
                layout = layout,
                modifier = Modifier
                    .offset(arena.left.toDp(), arena.top.toDp())
                    .size(arena.width.toDp(), arena.height.toDp())
            )
        }
    }
}
