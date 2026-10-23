package dev.mnascimentos.aureole.feature.home.components.clock.model

/**
 * Configuration for [dev.mnascimentos.aureole.feature.home.components.clock.EditClockDropdown].
 *
 * @param label Optional text label displayed above the dropdown field.
 * @param selectedKey Key of the currently selected option.
 * @param options List of key-value pairs representing dropdown choices.
 * @param initialExpanded Whether the dropdown menu is initially expanded.
 */
data class EditClockDropdownConfig(
    val label: String? = null,
    val selectedKey: String,
    val options: List<Pair<String, String>>,
    val initialExpanded: Boolean = false
)
