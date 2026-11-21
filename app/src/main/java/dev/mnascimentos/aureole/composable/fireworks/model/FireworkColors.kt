package dev.mnascimentos.aureole.composable.fireworks.model

import androidx.compose.ui.graphics.Color

/** Cores dos fogos por tom; quem desenha passa cores do `AureoleDS`, sem hex. */
data class FireworkColors(val primary: Color, val secondary: Color, val white: Color) {
    fun of(tone: FireworkTone): Color = when (tone) {
        FireworkTone.PRIMARY -> primary
        FireworkTone.SECONDARY -> secondary
        FireworkTone.WHITE -> white
    }
}
