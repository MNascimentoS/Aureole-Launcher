package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.compose.ui.Modifier

/**
 * What the "First steps" layer shows: the open list, the minimized pill, the peek bubble (the logo with its ring
 * while the home shows alone after a task, RF-13), or nothing.
 */
enum class ChecklistMorph { EXPANDED, MINIMIZED, PEEK, HIDDEN }

/**
 * The checklist layer's target, with where it came from: a list that reopens from the pill grows out of it
 * instead of rising from below the screen, and so does one that opens out of the peek bubble.
 */
data class ChecklistMorphTarget(
    val morph: ChecklistMorph,
    val cameFromPill: Boolean = false,
    val cameFromBubble: Boolean = false
)

/** The next target for [morph]; the same [previous] while the morph does not change. */
fun nextMorphTarget(previous: ChecklistMorphTarget?, morph: ChecklistMorph): ChecklistMorphTarget {
    if (previous?.morph == morph) return previous
    return ChecklistMorphTarget(
        morph = morph,
        cameFromPill = previous?.morph == ChecklistMorph.MINIMIZED,
        cameFromBubble = previous?.morph == ChecklistMorph.PEEK
    )
}

/** The bubble's container transform (bubble to list or pill) and its way back use the bubble's motion. */
val ChecklistMorphTarget.usesBubbleMotion: Boolean
    get() = cameFromBubble || morph == ChecklistMorph.PEEK

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
 * @property fading Title, rows and footer: they fade out before the card shrinks, and in after ~45% of the movement
 * when the list opens out of the peek bubble.
 * @property backdrop The dimmed screen behind the list, which fades with it.
 * @property animateEntrance False when the list grows out of the pill or the bubble instead of rising from below.
 */
class ChecklistSharedModifiers(
    val card: Modifier = Modifier,
    val halo: Modifier = Modifier,
    val progressText: Modifier = Modifier,
    val fading: Modifier = Modifier,
    val backdrop: Modifier = Modifier,
    val animateEntrance: Boolean = true
)
