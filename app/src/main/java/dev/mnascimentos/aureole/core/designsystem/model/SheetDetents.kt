package dev.mnascimentos.aureole.core.designsystem.model

/** Below this share of the rest height, a released sheet closes. */
private const val DISMISS_FRACTION_OF_REST = 0.7f

/** Release speed, in dp per second, that counts as a fling. */
private const val FLING_VELOCITY_DP_PER_SECOND = 400f

/** Where a released sheet settles. */
internal enum class SheetDetentTarget { Rest, Expanded, Dismiss }

/**
 * Picks where the sheet goes when the finger lifts.
 *
 * A strong fling up expands. A strong fling down collapses to rest, or closes when the sheet is already
 * at or below rest. Otherwise a sheet under [DISMISS_FRACTION_OF_REST] of [rest] closes and any other
 * height snaps to the closest of [rest] and [expanded].
 *
 * @param height Current sheet height. Same unit as [rest] and [expanded].
 * @param rest Height of the sheet at rest.
 * @param expanded Height of the expanded sheet.
 * @param velocity Release speed in dp per second; positive means the finger moved down.
 */
internal fun resolveDetent(height: Float, rest: Float, expanded: Float, velocity: Float): SheetDetentTarget {
    return when {
        velocity <= -FLING_VELOCITY_DP_PER_SECOND -> SheetDetentTarget.Expanded
        velocity >= FLING_VELOCITY_DP_PER_SECOND -> flingDownTarget(height, rest)
        height < rest * DISMISS_FRACTION_OF_REST -> SheetDetentTarget.Dismiss
        height - rest <= expanded - height -> SheetDetentTarget.Rest
        else -> SheetDetentTarget.Expanded
    }
}

private fun flingDownTarget(height: Float, rest: Float): SheetDetentTarget {
    return if (height <= rest) SheetDetentTarget.Dismiss else SheetDetentTarget.Rest
}

/**
 * Whether moving from [previous] to [current] reached or passed one of [detents].
 *
 * Leaving a detent the sheet sits on does not count, so a drag that starts at rest only ticks when it
 * reaches the next height.
 */
internal fun crossedDetent(previous: Float, current: Float, detents: List<Float>): Boolean {
    return detents.any { detent ->
        (previous < detent && current >= detent) || (previous > detent && current <= detent)
    }
}

/**
 * Top of the preview slot so that its bottom sits [gap] above the sheet top.
 * Never above [minTop]: when the sheet expands past it, the preview stays put
 * and the sheet covers it.
 */
internal fun anchoredPreviewTop(sheetTop: Int, gap: Int, previewHeight: Int, minTop: Int = 0): Int {
    return maxOf(minTop, sheetTop - gap - previewHeight)
}
