package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/**
 * What the Visual sheet saves when it closes (onboarding-redesenho RF-C1): the palette and the font to apply, each
 * null when it stays as it is.
 */
data class VisualChoice(
    val paletteName: String? = null,
    val fontName: String? = null
)

/** Label of the tab of the Visual sheet that shows [this] picker. */
@get:StringRes
val AppearancePicker.tabLabelRes: Int
    get() = when (this) {
        AppearancePicker.PALETTE -> R.string.home_onboarding_visual_tab_palette
        AppearancePicker.FONT -> R.string.home_onboarding_visual_tab_font
    }

/**
 * What closing the Visual sheet applies: what the user tapped, and, the first time only ([isFirstVisit]), the
 * default of a tab left untouched (Frostbite / Istok Web, as the separate pickers did). Reopening a done step never
 * resets the side the user did not touch.
 */
fun resolveVisualClose(
    tapped: VisualChoice,
    isFirstVisit: Boolean,
    defaults: VisualChoice
): VisualChoice = VisualChoice(
    paletteName = tapped.paletteName ?: defaults.paletteName.takeIf { isFirstVisit },
    fontName = tapped.fontName ?: defaults.fontName.takeIf { isFirstVisit }
)
