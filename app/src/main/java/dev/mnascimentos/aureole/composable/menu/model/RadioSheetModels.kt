package dev.mnascimentos.aureole.composable.menu.model

/**
 * One choice of an [dev.mnascimentos.aureole.composable.menu.AureoleRadioSheet].
 *
 * @property value Stable value handed to the caller when the choice is picked.
 * @property label Text shown on the row.
 */
data class RadioOption(
    val value: String,
    val label: String
)

/**
 * Content of an [dev.mnascimentos.aureole.composable.menu.AureoleRadioSheet].
 *
 * @property title Sheet title.
 * @property options Choices, in the order they are listed.
 * @property selectedValue Value of the option that is marked.
 */
data class RadioSheetParams(
    val title: String,
    val options: List<RadioOption>,
    val selectedValue: String
)
