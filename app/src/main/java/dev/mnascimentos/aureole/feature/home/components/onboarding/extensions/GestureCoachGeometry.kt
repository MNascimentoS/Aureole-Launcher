package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions

private const val HINT_START_FRACTION = 0.2f
private const val HINT_END_FRACTION = 0.75f

/**
 * Where the coach's finger starts and ends its scrub, in px from the top. Both sit inside the band
 * the alphabet really takes: from `height × (1 − SCRUBBER_HEIGHT_FRACTION)` down to
 * `height − paddingBottom`.
 */
fun scrubberHintPath(heightPx: Float, paddingBottomPx: Float): Pair<Float, Float> {
    val top = heightPx * (1f - ScrubberDimensions.SCRUBBER_HEIGHT_FRACTION)
    val band = (heightPx - paddingBottomPx - top).coerceAtLeast(0f)
    return (top + band * HINT_START_FRACTION) to (top + band * HINT_END_FRACTION)
}
