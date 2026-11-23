package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.clockColorRefOf
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** The clock's saved color for [role] (null follows the theme), old ARGB values read as fixed colors. */
fun clockColorRefFor(uiState: MainUiState, role: ComponentColorRole): ThemeColorRef? = when (role) {
    ComponentColorRole.TEXT -> clockColorRefOf(uiState.clockTextColor, uiState.clockTextColorRef)
    ComponentColorRole.CARD -> clockColorRefOf(uiState.clockBackgroundColor, uiState.clockBackgroundColorRef)
}

/** Title of the Colors row and of the color picker for [role] ("Text color", "Card color"). */
@StringRes
fun clockColorTitleRes(role: ComponentColorRole): Int = when (role) {
    ComponentColorRole.TEXT -> R.string.home_clock_color_text_title
    ComponentColorRole.CARD -> R.string.home_clock_color_card_title
}
