package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.compose.ui.Modifier

/**
 * What the "First steps" layer shows: the open list, the minimized pill (also while the home shows alone after a
 * step, in place of the old peek bubble, onboarding-redesenho RF-D2), or nothing.
 */
enum class ChecklistMorph { EXPANDED, MINIMIZED, HIDDEN }

/**
 * The checklist layer's target, with where it came from: a list that reopens from the pill grows out of it
 * instead of rising from below the screen.
 */
data class ChecklistMorphTarget(
    val morph: ChecklistMorph,
    val cameFromPill: Boolean = false
)

/** The next target for [morph]; the same [previous] while the morph does not change. */
fun nextMorphTarget(previous: ChecklistMorphTarget?, morph: ChecklistMorph): ChecklistMorphTarget {
    if (previous?.morph == morph) return previous
    return ChecklistMorphTarget(
        morph = morph,
        cameFromPill = previous?.morph == ChecklistMorph.MINIMIZED
    )
}

/** Remembers the last target across recompositions, so the origin is read once per change. */
class ChecklistMorphHistory {
    var last: ChecklistMorphTarget? = null
}

/**
 * Modifiers that tie the list and the pill together while one turns into the other. Empty outside the morph.
 *
 * @property card Outline of the glass card, shared with the pill's outline.
 * @property halo The progress halo, which travels between the header and the pill.
 * @property progressText "0 of 9 steps", which turns into the pill text.
 * @property fading Title, rows and footer: they fade out before the card shrinks.
 * @property backdrop The dimmed screen behind the list, which fades with it.
 * @property animateEntrance False when the list grows out of the pill instead of rising from below.
 */
class ChecklistSharedModifiers(
    val card: Modifier = Modifier,
    val halo: Modifier = Modifier,
    val progressText: Modifier = Modifier,
    val fading: Modifier = Modifier,
    val backdrop: Modifier = Modifier,
    val animateEntrance: Boolean = true
)
