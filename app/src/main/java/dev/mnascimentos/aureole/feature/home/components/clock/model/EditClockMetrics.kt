package dev.mnascimentos.aureole.feature.home.components.clock.model

/** Fixed values of the clock editor that are not design system tokens. */
object EditClockMetrics {
    /** Opacity of a disabled part of the editor (Format › Date with a layout without date), Material's 38%. */
    const val DISABLED_ALPHA = 0.38f

    /** Length of each dash (and gap) of the "None" dot of the card color, as a share of the dot size. */
    const val NONE_DOT_DASH_FRACTION = 0.12f

    /** Columns of the color page of the clock editor, as in the palette picker in color mode. */
    const val COLOR_PAGE_COLUMNS = 3
}

/** Test tag of the time bar in the miniature of a layout tile (it follows the clock alignment, RF-D5). */
const val CLOCK_LAYOUT_TIME_BAR_TAG = "clock_layout_time_bar"
