package dev.mnascimentos.aureole.core.designsystem.model

/** Share of the free width the sheet takes next to the preview (the prototype splits 1 : 1.15). */
private const val SIDE_SHEET_SHARE = 1.15f / 2.15f

/** How an edit overlay lays out its preview and its sheet. */
enum class OverlayLayout {
    /** Portrait (or no preview): the sheet at the bottom and the preview right above it. */
    STACKED,

    /** Landscape with a preview: the preview on the left, centered, and the sheet on the right. */
    SIDE_BY_SIDE
}

/** The layout for a screen of [width] x [height] (any unit): side by side only in landscape with a preview. */
fun overlayLayoutFor(width: Float, height: Float, hasPreview: Boolean): OverlayLayout =
    if (hasPreview && width > height) OverlayLayout.SIDE_BY_SIDE else OverlayLayout.STACKED

/**
 * Columns of the side-by-side layout, in pixels from the left edge.
 *
 * @property previewStart Left edge of the preview column.
 * @property previewWidth Width of the preview column.
 * @property sheetStart Left edge of the sheet.
 * @property sheetWidth Width of the sheet.
 */
data class SideBySideColumns(
    val previewStart: Int,
    val previewWidth: Int,
    val sheetStart: Int,
    val sheetWidth: Int
)

/**
 * Splits [width] between the preview and the sheet: [gap] around and between them, inside the safe area left by
 * [insetStart] and [insetEnd]. The sheet takes a little more than half of what is free, never more than
 * [maxSheetWidth]; the preview takes the rest.
 */
fun sideBySideColumns(
    width: Int,
    insets: Pair<Int, Int>,
    gap: Int,
    maxSheetWidth: Int
): SideBySideColumns {
    val (insetStart, insetEnd) = insets
    val free = (width - insetStart - insetEnd - gap * 3).coerceAtLeast(0)
    val sheetWidth = minOf((free * SIDE_SHEET_SHARE).toInt(), maxSheetWidth.coerceAtLeast(0))
    val previewStart = insetStart + gap
    val previewWidth = free - sheetWidth
    return SideBySideColumns(
        previewStart = previewStart,
        previewWidth = previewWidth,
        sheetStart = previewStart + previewWidth + gap,
        sheetWidth = sheetWidth
    )
}
