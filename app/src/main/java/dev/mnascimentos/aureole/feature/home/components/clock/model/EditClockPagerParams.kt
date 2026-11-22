package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Data and callbacks shown by the pages of the clock editor; [isExpanded] is whether the sheet is expanded,
 * [nowMillis] the instant the format tiles preview and [onOpenFontPicker] opens the font picker over the sheet. */
class EditClockPagerParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val isExpanded: Boolean,
    val tempCustomGreeting: String,
    val onGreetingChange: (String) -> Unit,
    val nowMillis: Long = System.currentTimeMillis(),
    val onOpenFontPicker: () -> Unit = {}
)
