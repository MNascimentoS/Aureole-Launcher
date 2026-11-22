package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Data and callbacks shown by the pages of the clock editor; [isExpanded] is whether the sheet is expanded,
 * [nowMillis] the instant the format tiles preview, [onOpenFontPicker] opens the font picker over the sheet and
 * [onOpenColorPicker] the palette picker in color mode for the text or the card. */
class EditClockPagerParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val isExpanded: Boolean,
    val tempCustomGreeting: String,
    val onGreetingChange: (String) -> Unit,
    val nowMillis: Long = System.currentTimeMillis(),
    val onOpenFontPicker: () -> Unit = {},
    val onOpenColorPicker: (ComponentColorRole) -> Unit = {}
)
