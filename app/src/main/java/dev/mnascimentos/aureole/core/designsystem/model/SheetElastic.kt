package dev.mnascimentos.aureole.core.designsystem.model

/** How fast the resistance builds up: lower is stiffer. */
private const val RUBBER_BAND_FACTOR = 0.55f

/** Keeps the inverse finite: the stretch never quite reaches its limit. */
private const val MAX_STRETCH_SHARE = 0.999f

/**
 * Elastic stretch for a finger [overshootPx] past the edge: it grows with the finger, slower and slower,
 * and never reaches [limitPx]. Zero for no overshoot or no limit.
 */
internal fun rubberBand(overshootPx: Float, limitPx: Float): Float {
    if (overshootPx <= 0f || limitPx <= 0f) return 0f
    return limitPx * (1f - 1f / (overshootPx / limitPx * RUBBER_BAND_FACTOR + 1f))
}

/** The finger overshoot that [rubberBand] turns into [stretchPx]; lets a new drag start from a stretched sheet. */
internal fun rubberBandInverse(stretchPx: Float, limitPx: Float): Float {
    if (stretchPx <= 0f || limitPx <= 0f) return 0f
    val share = (stretchPx / limitPx).coerceAtMost(MAX_STRETCH_SHARE)
    return limitPx / RUBBER_BAND_FACTOR * (1f / (1f - share) - 1f)
}

/** Sheet height for a finger at [rawHeight]: free up to [rest], elastic above it. */
internal fun stretchedHeight(rawHeight: Float, rest: Float, limitPx: Float): Float {
    return if (rawHeight <= rest) rawHeight else rest + rubberBand(rawHeight - rest, limitPx)
}

/** Finger height that [stretchedHeight] turns into [height]. */
internal fun unstretchedHeight(height: Float, rest: Float, limitPx: Float): Float {
    return if (height <= rest) height else rest + rubberBandInverse(height - rest, limitPx)
}
