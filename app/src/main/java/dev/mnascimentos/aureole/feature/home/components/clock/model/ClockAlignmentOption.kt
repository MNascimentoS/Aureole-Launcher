package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** An alignment of the clock inside its card, in the order of the segmented control. */
enum class ClockAlignmentOption(val key: String, @StringRes val labelRes: Int) {
    START("START", R.string.home_clock_align_left),
    CENTER("CENTER", R.string.home_clock_align_center),
    END("END", R.string.home_clock_align_right);

    companion object {
        /** Segment to highlight for the stored alignment; -1 when it matches none. */
        fun indexOfKey(key: String): Int = entries.indexOfFirst { it.key == key }
    }
}
