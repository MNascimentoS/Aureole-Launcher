package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.parseThemeColorRef

/**
 * The card color "Theme" of the clock picks: the variant tone of the current theme, the card the clock always had
 * (seletores-relogio D-13). A null card color is "None": no card at all (relogio-home-sheet D-09).
 */
val CLOCK_CARD_THEME_REF: ThemeColorRef = ThemeColorRef.Role(ThemeColorRole.VARIANT)

/**
 * The card color, as [ThemeColorRef] JSON, of a clock saved with the removed card switch (RF-E3): switched off is
 * no card (null); on with a saved color keeps it ([refJson], else the old ARGB [legacyArgb]); on with no color is
 * the theme card ([CLOCK_CARD_THEME_REF]). The clock keeps the look it had.
 */
fun migratedClockCardRef(isEnabled: Boolean, legacyArgb: Int, refJson: String?): String? = when {
    !isEnabled -> null
    parseThemeColorRef(refJson) != null -> refJson
    legacyArgb != 0 -> ThemeColorRef.Fixed(legacyArgb).toJson()
    else -> CLOCK_CARD_THEME_REF.toJson()
}

/**
 * [this] config with the card switch of an older version folded into the card color ([migratedClockCardRef]); a
 * config saved without the switch is returned as it is.
 */
fun ClockConfig.withMigratedCard(): ClockConfig {
    val wasEnabled = legacyBackgroundEnabled ?: return this
    return copy(
        legacyBackgroundEnabled = null,
        backgroundColor = 0,
        backgroundColorRef = migratedClockCardRef(wasEnabled, backgroundColor, backgroundColorRef)
    )
}
