package dev.mnascimentos.aureole.composable.clock.model

/**
 * Font options of the clock. [storageKey] is what the settings keep (`clockFontFamily`). [THEME] follows the font
 * chosen in the theme; the other options are fixed system families.
 */
enum class ClockFontSpec(val storageKey: String) {
    THEME("SANS_SERIF"),
    BOLD("BOLD"),
    SERIF("SERIF"),
    MONO("MONOSPACE"),
    ROUNDED("ROUNDED");

    companion object {
        /**
         * Case-insensitive; accepts the stored key or the enum name. "SANS_SERIF", "DEFAULT" and any unknown key
         * become [THEME].
         */
        fun fromKey(key: String): ClockFontSpec =
            entries.firstOrNull {
                it.storageKey.equals(key, ignoreCase = true) || it.name.equals(key, ignoreCase = true)
            } ?: THEME
    }
}
