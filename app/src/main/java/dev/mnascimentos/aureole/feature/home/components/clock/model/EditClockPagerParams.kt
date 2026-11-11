package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** Data and callbacks shown by the pages of the clock editor; [isExpanded] is whether the sheet is expanded. */
class EditClockPagerParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val isExpanded: Boolean,
    val tempCustomGreeting: String,
    val onGreetingChange: (String) -> Unit
)
