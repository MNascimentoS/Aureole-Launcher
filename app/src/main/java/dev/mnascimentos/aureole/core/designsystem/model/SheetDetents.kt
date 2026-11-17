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
 * @param canExpand False when the content already fits at rest: the sheet then never settles expanded.
 */
internal fun resolveDetent(
    height: Float,
    rest: Float,
    expanded: Float,
    velocity: Float,
    canExpand: Boolean = true
): SheetDetentTarget {
    val target = when {
        isFlingUp(velocity) -> SheetDetentTarget.Expanded
        velocity >= FLING_VELOCITY_DP_PER_SECOND -> flingDownTarget(height, rest)
        height < rest * DISMISS_FRACTION_OF_REST -> SheetDetentTarget.Dismiss
        height - rest <= expanded - height -> SheetDetentTarget.Rest
        else -> SheetDetentTarget.Expanded
    }
    return if (!canExpand && target == SheetDetentTarget.Expanded) SheetDetentTarget.Rest else target
}

/** Whether a release at [velocity] dp per second (positive = down) is a fling up. */
internal fun isFlingUp(velocity: Float): Boolean = velocity <= -FLING_VELOCITY_DP_PER_SECOND

/**
 * Height the sheet expands to, or null when it cannot expand.
 *
 * When everything the sheet holds already shows at [rest] (with [slack] of tolerance) there is nothing to
 * expand to. With a little more content the sheet expands only to [contentFull], never past [expanded].
 * An unknown [contentFull] (lazy content, which has no intrinsic height) keeps the full [expanded] height.
 *
 * @param rest Height of the sheet at rest.
 * @param expanded Largest expanded height (90% of the screen).
 * @param contentFull Height the sheet would take to show all of its content, or null when unknown.
 * @param slack How much taller than [rest] the content may be and still count as fitting.
 */
internal fun expandTarget(rest: Float, expanded: Float, contentFull: Float?, slack: Float = 0f): Float? {
    if (contentFull == null) return expanded
    if (contentFull <= rest + slack) return null
    return minOf(expanded, contentFull).takeIf { it > rest }
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
