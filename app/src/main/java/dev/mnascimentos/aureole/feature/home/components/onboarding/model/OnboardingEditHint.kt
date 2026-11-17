package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** Which screen edge the edit hint and its save pill sit on. */
enum class EditHintEdge { BOTTOM, TOP }

/** What the edit hint needs to know about the grid under it to pick its edge. */
data class EditHintGrid(
    val items: List<EditHintItem> = emptyList(),
    val maxRows: Int = 0
)

/** The rows an item covers: all the hint needs to avoid it. */
data class EditHintItem(val row: Int, val rowSpan: Int)

/**
 * Everything the edit hint reads: whether it shows, the glass, the grid under it and the thumb side.
 * [startsFolded] opens it already as the bare save pill (the state it reaches after 3 s or the first gesture).
 */
data class EditHintConfig(
    val isVisible: Boolean,
    val glass: OnboardingGlass = OnboardingGlass(),
    val grid: EditHintGrid = EditHintGrid(),
    val isLandscape: Boolean = false,
    val isLeftHanded: Boolean = false,
    val startsFolded: Boolean = false
)

/** Labels and state of the save pill. */
data class EditHintSaveParams(val text: String, val hint: String, val isGesturing: Boolean)
