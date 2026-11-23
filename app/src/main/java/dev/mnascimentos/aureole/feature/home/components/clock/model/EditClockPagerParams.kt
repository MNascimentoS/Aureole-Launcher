package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Data and callbacks shown by the pages of the clock editor: [greeting] is the custom message being typed,
 * [isExpanded] whether the sheet is expanded, [nowMillis] the instant the format tiles preview and [pickers] open
 * the font picker over the sheet and the color page inside it.
 */
class EditClockPagerParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val isExpanded: Boolean,
    val greeting: EditClockGreeting,
    val nowMillis: Long = System.currentTimeMillis(),
    val pickers: EditClockPickerOpeners = EditClockPickerOpeners()
)

/** Opens the pickers of the editor: [onFont] the font picker over the sheet, [onColor] the color page of a role. */
class EditClockPickerOpeners(
    val onFont: () -> Unit = {},
    val onColor: (ComponentColorRole) -> Unit = {}
)

/** The custom message being typed in the editor ([text]) and what to do when it changes. */
class EditClockGreeting(val text: String, val onChange: (String) -> Unit)
