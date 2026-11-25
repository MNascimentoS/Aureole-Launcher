package dev.mnascimentos.aureole.composable.tips.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * What a tip card shows (grupo-apps-pastas RF-F2): its [step] of [stepCount], [title], [text] and, when there is one,
 * the [actionLabel] of its button ([onAction]). [onSkip] ends every tip ("Skip tips").
 */
data class TipCardContent(
    val step: Int,
    val stepCount: Int,
    val title: String,
    val text: String,
    val onSkip: () -> Unit,
    val actionLabel: String? = null,
    val onAction: () -> Unit = {}
)

/** Fixed values of the tip card and its pulse that are not design system tokens. */
object TipCardMetrics {
    /** Corner radius of the card (the prototype's 18 dp; no token has it). */
    val CornerRadius: Dp = 18.dp

    /** Width of the current step mark; the others are [StepWidth]. */
    val CurrentStepWidth: Dp = 18.dp
    val StepWidth: Dp = 8.dp
    val StepHeight: Dp = 4.dp

    /** One beat of the ring around the target (RF-F3). */
    const val PULSE_MS = 1600

    /** Ring opacity at the start of each beat; it fades to 0. */
    const val PULSE_START_ALPHA = 0.7f

    /** How far the ring grows past the target, as a share of its smaller side. */
    const val PULSE_GROWTH = 0.12f
}
