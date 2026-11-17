package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.ui.semantics.ProgressBarRangeInfo

/** Share of the circle the arc covers while the ring spins without a known progress. */
const val LOGO_RING_INDETERMINATE_SWEEP = 0.28f

/** Logo width as a share of the ring diameter. */
const val LOGO_RING_LOGO_FRACTION = 0.5f

/** Larger logo for a ring that is the header of a card (the "First steps" checklist: ~40 dp in a 64 dp ring). */
const val LOGO_RING_LOGO_FRACTION_LARGE = 0.62f

/**
 * Share of the circle the arc covers: the progress clamped to 0..1, or a fixed arc for a ring without one
 * (`null` = waiting with no known end).
 */
fun logoRingSweepFraction(progress: Float?): Float =
    progress?.coerceIn(0f, 1f) ?: LOGO_RING_INDETERMINATE_SWEEP

/** What TalkBack hears: the clamped progress, or "in progress" when there is none. */
fun logoRingRangeInfo(progress: Float?): ProgressBarRangeInfo =
    if (progress == null) {
        ProgressBarRangeInfo.Indeterminate
    } else {
        ProgressBarRangeInfo(progress.coerceIn(0f, 1f), 0f..1f)
    }

/** Whether the ring spins: only without a progress and with the system animations on. */
fun isLogoRingSpinning(progress: Float?, animationsEnabled: Boolean): Boolean =
    progress == null && animationsEnabled
