package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType

/**
 * The chip of the item a step just added ("New · Clock", onboarding-redesenho RF-D1), by the type of the item; null
 * for a type no step adds, which gets no highlight.
 */
@StringRes
fun newItemChipRes(type: LauncherItemType?): Int? = when (type) {
    LauncherItemType.CLOCK -> R.string.home_onboarding_new_clock
    LauncherItemType.WIDGET_LIST, LauncherItemType.SINGLE_APP_WIDGET -> R.string.home_onboarding_new_widget
    LauncherItemType.APPS_LIST -> R.string.home_onboarding_new_favorites
    LauncherItemType.SHORTCUTS_CONTAINER -> R.string.home_onboarding_new_group
    else -> null
}

/** How much of the highlight's outline shows ([visibleFraction]) and how far its dashes moved ([phasePx]). */
data class NewItemDashes(val visibleFraction: Float, val phasePx: Float)

/**
 * Where the dashes of the highlight are: while it draws itself ([drawProgress] < 1) the visible share of the
 * outline grows from the start; after that the dashes march, shifted by [marchPhase] (0 to 1, one dash cycle).
 * [dashPx] is the length of a dash and of the gap after it.
 */
fun newItemDashes(drawProgress: Float, marchPhase: Float, dashPx: Float): NewItemDashes = NewItemDashes(
    visibleFraction = drawProgress.coerceIn(0f, 1f),
    phasePx = if (drawProgress >= 1f) -marchPhase.coerceIn(0f, 1f) * dashPx * 2 else 0f
)
