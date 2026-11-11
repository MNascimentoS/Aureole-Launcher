package dev.mnascimentos.aureole.core.designsystem.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

private const val FACTOR_LOW = 0.4f
private const val FACTOR_MEDIUM = 1f
private const val FACTOR_MAX = 1.6f

/**
 * How strong the glass (blur) behind the cards is. [NONE] switches the blur off; the others keep it on and scale
 * every blur radius of the launcher by [blurFactor]. [MEDIUM] keeps each component's own radius.
 */
enum class GlassLevel(val blurFactor: Float, @StringRes val labelRes: Int) {
    NONE(0f, R.string.settings_cards_glass_level_none),
    LOW(FACTOR_LOW, R.string.settings_cards_glass_level_low),
    MEDIUM(FACTOR_MEDIUM, R.string.settings_cards_glass_level_medium),
    MAX(FACTOR_MAX, R.string.settings_cards_glass_level_max)
}

/** Glass level for a stored [name]; unknown names fall back to [GlassLevel.MEDIUM]. */
fun glassLevelOf(name: String): GlassLevel = GlassLevel.entries.firstOrNull { it.name == name } ?: GlassLevel.MEDIUM
