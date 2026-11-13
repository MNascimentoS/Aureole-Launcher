package dev.mnascimentos.aureole.composable.mirror.model

import androidx.compose.ui.unit.LayoutDirection

/**
 * Layout direction of a list row: flipped for the left-handed mode so the row reads from the right,
 * with the icon next to the thumb and the text aligned to the right (`row-reverse` in the prototype).
 * Right-handed rows keep [current].
 */
fun mirroredLayoutDirection(isLeftHanded: Boolean, current: LayoutDirection): LayoutDirection = when {
    !isLeftHanded -> current
    current == LayoutDirection.Ltr -> LayoutDirection.Rtl
    else -> LayoutDirection.Ltr
}
