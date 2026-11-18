package dev.mnascimentos.aureole.core.designsystem.model

/** Smallest the preview above a sheet shrinks before it starts to fade out. */
internal const val PREVIEW_MIN_SCALE = 0.5f

/** Scale at which the fade starts: from here down to [PREVIEW_MIN_SCALE] the preview fades, so it is gone before it is cut. */
internal const val PREVIEW_FADE_START_SCALE = 0.75f

/** What the preview above the sheet does as the sheet grows (RF-09). */
enum class PreviewBehavior {
    /** It fits with its gap: full size, fully visible. */
    STAYS,

    /** It would touch the sheet: it scales down, anchored at its bottom, so the gap is kept. */
    SHRINKS,

    /** Not even at its smallest does it fit above the sheet: it is fully faded out. */
    HIDES
}

/** How the preview is drawn for the room it has: [scale] around its bottom center and [alpha]. */
data class PreviewFit(val behavior: PreviewBehavior, val scale: Float, val alpha: Float)

/**
 * How the preview above the sheet fits the [room] between the top padding and the top of the sheet, keeping at
 * least [gap] (the breathing room, `AureoleDS.dimens.medium`) between its bottom, label included, and the sheet.
 * While the content fits below it the preview stays; when the sheet takes more, the preview shrinks and, over the
 * last part of the shrink (from [PREVIEW_FADE_START_SCALE] down to [minScale]), fades out. At [minScale] it is
 * fully gone, so a preview that would reach above the top padding (cut by the status bar) is never visible.
 * Continuous: no jump in scale or alpha at any boundary.
 */
fun previewFit(previewHeight: Float, room: Float, gap: Float, minScale: Float = PREVIEW_MIN_SCALE): PreviewFit {
    if (previewHeight <= 0f || room >= previewHeight + gap) return PreviewFit(PreviewBehavior.STAYS, 1f, 1f)
    val scale = (room - gap) / previewHeight
    if (scale < minScale) return PreviewFit(PreviewBehavior.HIDES, minScale, 0f)
    val fadeStart = PREVIEW_FADE_START_SCALE.coerceAtLeast(minScale + FADE_MIN_RANGE)
    val alpha = ((scale - minScale) / (fadeStart - minScale)).coerceIn(0f, 1f)
    return PreviewFit(PreviewBehavior.SHRINKS, scale, alpha)
}

private const val FADE_MIN_RANGE = 0.01f
