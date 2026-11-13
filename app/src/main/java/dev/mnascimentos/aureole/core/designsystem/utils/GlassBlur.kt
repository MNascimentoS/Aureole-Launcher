package dev.mnascimentos.aureole.core.designsystem.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel

/** Blur radius that [GlassLevel.MEDIUM] gives the components that have no radius of their own (the preview card). */
val GlassBaseBlurRadius: Dp = 20.dp

/** Glass level chosen in "Cards and glass"; the Home provides it, the default keeps every radius as it was. */
val LocalGlassLevel = staticCompositionLocalOf { GlassLevel.MEDIUM }

/** Blur radius for a component whose radius at [GlassLevel.MEDIUM] is [base], at this level. */
fun GlassLevel.blurRadius(base: Dp): Dp = base * blurFactor

/** [base] scaled by the glass level in effect; the single point every `HazeStyle` of the launcher goes through. */
@Composable
@ReadOnlyComposable
fun glassBlurRadius(base: Dp): Dp = LocalGlassLevel.current.blurRadius(base)
