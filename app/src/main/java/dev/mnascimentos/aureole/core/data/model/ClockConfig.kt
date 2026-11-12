package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep

/**
 * The look of one clock on the home grid, saved per grid item id. Every field has a default (the factory clock), so
 * Gson keeps the default for a field a saved JSON does not have. The glass (blur and opacity) is not here: it is the
 * launcher's, shared by every container.
 */
@Keep
data class ClockConfig(
    val style: String = DEFAULT_STYLE,
    val customGreeting: String = "",
    val alignment: String = DEFAULT_ALIGNMENT,
    val fontFamily: String = DEFAULT_FONT_FAMILY,
    val timeFormat: String = DEFAULT_TIME_FORMAT,
    val dateFormat: String = DEFAULT_DATE_FORMAT,
    val textColor: Int = 0,
    val backgroundColor: Int = 0,
    val isBackgroundEnabled: Boolean = false
) {
    companion object {
        const val DEFAULT_STYLE = "DATE_ON_TOP"
        const val DEFAULT_ALIGNMENT = "CENTER"
        const val DEFAULT_FONT_FAMILY = "SANS_SERIF"
        const val DEFAULT_TIME_FORMAT = "SYSTEM"
        const val DEFAULT_DATE_FORMAT = "DEFAULT"
    }
}
