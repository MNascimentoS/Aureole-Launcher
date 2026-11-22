package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Data and callbacks shown by the pages of the clock editor: [greeting] is the custom message being typed,
 * [isExpanded] whether the sheet is expanded, [nowMillis] the instant the format tiles preview and [pickers] open
 * the font and the color pickers over the sheet.
 */
class EditClockPagerParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val isExpanded: Boolean,
    val greeting: EditClockGreeting,
    val nowMillis: Long = System.currentTimeMillis(),
    val pickers: EditClockPickerOpeners = EditClockPickerOpeners()
)

/** Opens the pickers that cover the editor: [onFont] the font picker, [onColor] the color picker of a role. */
class EditClockPickerOpeners(
    val onFont: () -> Unit = {},
    val onColor: (ComponentColorRole) -> Unit = {}
)

/** What the pickers over the editor show: the clock being edited, its [preview] and the format instant. */
class EditClockPickersParams(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val nowMillis: Long,
    val preview: @Composable BoxScope.() -> Unit
)

/** The custom message being typed in the editor ([text]) and what to do when it changes. */
class EditClockGreeting(val text: String, val onChange: (String) -> Unit)
