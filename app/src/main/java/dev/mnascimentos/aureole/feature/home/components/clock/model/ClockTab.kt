package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

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

        /**
         * Pager height while the tabs slide: the height of the settled page, blended with the one of the
         * neighbour it is moving to as [offsetFraction] (signed, as in `PagerState.currentPageOffsetFraction`)
         * grows. Returns 0 while the settled page has not been measured, which means "wrap the content".
         */
        fun pagerHeight(heights: List<Int>, currentPage: Int, offsetFraction: Float): Int {
            val current = heights.getOrElse(currentPage) { 0 }
            val neighbourPage = currentPage + sign(offsetFraction).toInt()
            val neighbour = heights.getOrElse(neighbourPage) { 0 }
            if (current <= 0 || neighbour <= 0) return maxOf(current, 0)
            val fraction = abs(offsetFraction).coerceIn(0f, 1f)
            return (current + (neighbour - current) * fraction).roundToInt()
        }
    }
}
