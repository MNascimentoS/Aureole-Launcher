package dev.mnascimentos.aureole.core.designsystem.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.R

// Hidden game (jogo-secreto-fix)
@Composable
fun AureoleIcons.SoundOn(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_sound_on, modifier, tint) }

@Composable
fun AureoleIcons.SoundOff(
    modifier: Modifier = Modifier,
    tint: Color? = null
) { AureoleIcon(R.drawable.ic_aureole_sound_off, modifier, tint) }
