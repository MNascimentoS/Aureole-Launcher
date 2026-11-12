package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** Tabs of the clock editor, in the order the pager shows them. */
enum class ClockTab(@StringRes val labelRes: Int) {
    STYLE(R.string.home_clock_tab_style),
    FORMAT(R.string.home_clock_tab_format),
    COLORS(R.string.home_clock_tab_colors);

    /** Page index of this tab in the pager. */
    val index: Int get() = ordinal

    companion object {
        val pageCount: Int = entries.size

        /** Tab for a pager page; out-of-range pages clamp to the first or last tab. */
        fun fromPage(page: Int): ClockTab = entries[page.coerceIn(0, entries.lastIndex)]

        /**
         * Tab the bar highlights: the page being scrolled to while a swipe or animation runs,
         * the settled page otherwise.
         */
        fun resolve(currentPage: Int, targetPage: Int, isScrolling: Boolean): ClockTab =
            fromPage(if (isScrolling) targetPage else currentPage)

        /** Largest page height seen so far, so the sheet keeps one height on every tab. */
        fun stableHeight(current: Int, measured: Int): Int = maxOf(current, measured)
    }
}
