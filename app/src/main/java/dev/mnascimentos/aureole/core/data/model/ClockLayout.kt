package dev.mnascimentos.aureole.core.data.model

/** One line the clock draws: the message (greeting or custom text), the time or the date. */
enum class ClockLine { MESSAGE, TIME, DATE }

/**
 * Which lines the clock draws, top to bottom. [key] is the value saved in `ClockConfig.style` and in the settings
 * (`clock_style`); `TIME_ONLY` and `DATE_ON_TOP` keep the keys of the old styles with the same look.
 */
enum class ClockLayout(val key: String, val lines: List<ClockLine>) {
    TIME_ONLY("TIME_ONLY", listOf(ClockLine.TIME)),
    DATE_ON_TOP("DATE_ON_TOP", listOf(ClockLine.DATE, ClockLine.TIME)),
    TIME_ON_TOP("TIME_ON_TOP", listOf(ClockLine.TIME, ClockLine.DATE)),
    DATE_TIME_MSG("DATE_TIME_MSG", listOf(ClockLine.DATE, ClockLine.TIME, ClockLine.MESSAGE)),
    MSG_TIME_DATE("MSG_TIME_DATE", listOf(ClockLine.MESSAGE, ClockLine.TIME, ClockLine.DATE)),
    MSG_ON_TOP("MSG_ON_TOP", listOf(ClockLine.MESSAGE, ClockLine.TIME)),
    MSG_ON_BOTTOM("MSG_ON_BOTTOM", listOf(ClockLine.TIME, ClockLine.MESSAGE));

    /** True when the layout has a date line. */
    val showsDate: Boolean get() = ClockLine.DATE in lines

    /** True when the layout has a message line. */
    val showsMessage: Boolean get() = ClockLine.MESSAGE in lines

    companion object {
        /** The look of a new install: the old default style, "Date on top" (D-15). */
        val DEFAULT = DATE_ON_TOP

        /** The layout saved as [key], or null when it is not a layout key (an old style or garbage). */
        fun fromKey(key: String?): ClockLayout? = entries.firstOrNull { it.key == key }
    }
}

/** What the message line of a layout shows. [key] is the value saved in `ClockConfig.message`. */
enum class ClockMessage(val key: String) {
    GREETING("GREETING"),
    CUSTOM("CUSTOM");

    companion object {
        val DEFAULT = GREETING

        /** The message saved as [key]; the daily greeting for none or an unknown one. */
        fun fromKey(key: String?): ClockMessage = entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}

/** A layout and its message, as read from a saved (possibly old) clock style. */
data class ClockLayoutChoice(val layout: ClockLayout, val message: ClockMessage)

private const val LEGACY_GREETING_AND_DATE = "GREETING_AND_DATE"
private const val LEGACY_DAILY_GREETING = "DYNAMIC_GREETING"
private const val LEGACY_CUSTOM_GREETING = "CUSTOM_GREETING"

/**
 * Reads a saved clock [style] and [message] as a layout and its message (D-13). A layout key stays as it is. The
 * old styles keep their look: "Info and date", "Daily greeting" and "Custom text" all drew the greeting, the time
 * and the date (`ClockHeaderContent` before 0.9.60), so they become [ClockLayout.MSG_TIME_DATE] with the greeting
 * or the custom text (D-19). Anything else falls back to the default layout.
 */
fun resolveClockLayout(style: String?, message: String?): ClockLayoutChoice {
    ClockLayout.fromKey(style)?.let { return ClockLayoutChoice(it, ClockMessage.fromKey(message)) }
    return when (style) {
        LEGACY_GREETING_AND_DATE, LEGACY_DAILY_GREETING ->
            ClockLayoutChoice(ClockLayout.MSG_TIME_DATE, ClockMessage.GREETING)
        LEGACY_CUSTOM_GREETING -> ClockLayoutChoice(ClockLayout.MSG_TIME_DATE, ClockMessage.CUSTOM)
        else -> ClockLayoutChoice(ClockLayout.DEFAULT, ClockMessage.fromKey(message))
    }
}

/** [this] config with its style read as a layout key and its message set ([resolveClockLayout]). */
fun ClockConfig.withMigratedLayout(): ClockConfig {
    val choice = resolveClockLayout(style, message)
    return copy(style = choice.layout.key, message = choice.message.key)
}
