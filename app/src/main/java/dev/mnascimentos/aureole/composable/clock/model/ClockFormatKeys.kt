package dev.mnascimentos.aureole.composable.clock.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date formats of the clock. [storageKey] is what the settings keep; [skeleton] is resolved per locale into the
 * real pattern, so the weekday, day and month always come in the order and language of the device.
 */
enum class ClockDateFormat(val storageKey: String, val skeleton: String) {
    FULL("DEFAULT", "EEEEdMMMM"),
    SHORT("SHORT", "EEEdMMM"),
    MEDIUM("MEDIUM", "dMMMM"),
    NUMERIC("NUMERIC", "ddMMyyyy");

    companion object {
        /** Case-insensitive; accepts the stored key or the enum name, and falls back to [FULL]. */
        fun fromKey(key: String): ClockDateFormat =
            entries.firstOrNull {
                it.storageKey.equals(key, ignoreCase = true) || it.name.equals(key, ignoreCase = true)
            } ?: FULL
    }
}

/** Hour modes of the clock. [key] is what the settings keep. */
enum class ClockTimeFormat(val key: String) {
    SYSTEM("SYSTEM"),
    H12("12H"),
    H24("24H");

    companion object {
        /** Case-insensitive; anything unknown follows the system. */
        fun fromKey(key: String): ClockTimeFormat =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: SYSTEM
    }
}

/** Skeleton of the date format stored under [key]; an unknown key means the full date. */
fun clockDateSkeleton(key: String): String = ClockDateFormat.fromKey(key).skeleton

/** Text [pattern] produces for [nowMillis] in [locale]; used by the previews on the format tiles. */
fun formatClockSample(pattern: String, locale: Locale, nowMillis: Long): String =
    SimpleDateFormat(pattern, locale).format(Date(nowMillis))
