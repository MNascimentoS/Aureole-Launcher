package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

/**
 * The room an opened folder has on the screen, in pixels of the home (RF-16).
 *
 * @property screenHeightPx Height of the home, status and navigation bars included (edge to edge).
 * @property statusInsetPx Height of the status bar at the top.
 * @property navInsetPx Height of the navigation bar at the bottom: 3 buttons (~48 dp) or the gesture pill.
 * @property marginPx Margin kept above the navigation bar.
 * @property heightCapPx Tallest the popup gets even with room to spare ([FolderPopupMetrics.HeightMax]).
 */
data class FolderPopupBounds(
    val screenHeightPx: Float,
    val statusInsetPx: Float,
    val navInsetPx: Float,
    val marginPx: Float,
    val heightCapPx: Float
) {
    /** Highest top: right below the status bar. */
    val topLimitPx: Float get() = statusInsetPx

    /** Lowest bottom: the margin above the navigation bar. */
    val bottomLimitPx: Float get() = (screenHeightPx - navInsetPx - marginPx).coerceAtLeast(topLimitPx)

    /** Height between the two limits. */
    val usableHeightPx: Float get() = bottomLimitPx - topLimitPx

    /** Tallest the popup may be: the cap, or the usable height when that is shorter (the list then scrolls). */
    val maxHeightPx: Float get() = heightCapPx.coerceAtMost(usableHeightPx).coerceAtLeast(0f)
}

/**
 * Where an opened folder goes.
 *
 * @property topPx Top of the popup in the home.
 * @property heightPx Height of the popup once capped by [FolderPopupBounds.maxHeightPx].
 */
data class FolderPopupPlacement(val topPx: Float, val heightPx: Float)

/**
 * Placement of the folder opened as a list (RF-16): the popup takes its content height, capped by
 * [FolderPopupBounds.maxHeightPx]. When it fits below [anchorTopPx] it opens there, lined up with the folder;
 * otherwise it moves up until its bottom touches the margin above the navigation bar, and never above the status bar.
 */
fun folderPopupPlacement(
    anchorTopPx: Float,
    popupHeightPx: Float,
    bounds: FolderPopupBounds
): FolderPopupPlacement {
    val height = popupHeightPx.coerceIn(0f, bounds.maxHeightPx)
    val top = anchorTopPx.coerceAtLeast(bounds.topLimitPx)
    val fittedTop = if (top + height > bounds.bottomLimitPx) bounds.bottomLimitPx - height else top
    return FolderPopupPlacement(topPx = fittedTop.coerceAtLeast(bounds.topLimitPx), heightPx = height)
}

/** Placement of the folder opened as a grid: centered in the usable height, with the same height limit (RF-16). */
fun gridFolderPopupPlacement(popupHeightPx: Float, bounds: FolderPopupBounds): FolderPopupPlacement {
    val height = popupHeightPx.coerceIn(0f, bounds.maxHeightPx)
    return FolderPopupPlacement(topPx = bounds.topLimitPx + (bounds.usableHeightPx - height) / 2, heightPx = height)
}
