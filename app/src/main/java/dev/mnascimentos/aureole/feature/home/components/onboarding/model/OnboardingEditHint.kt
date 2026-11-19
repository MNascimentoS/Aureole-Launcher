package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** Which screen edge the edit hint and its save pill sit on. */
enum class EditHintEdge { BOTTOM, TOP }

/** Where the hint sits across its edge: centered in portrait, on the thumb side (start or end) in landscape. */
enum class EditHintSide { CENTER, START, END }

/**
 * What the edit hint needs to know about the grid under it to pick its edge: only the item being edited counts
 * (the rest of the home always fills the bottom, so counting it sent the save to the top every time).
 */
data class EditHintGrid(
    val items: List<EditHintItem> = emptyList(),
    val maxRows: Int = 0,
    val maxCols: Int = 0
)

/** The cells an item covers: all the hint needs to avoid it. [colSpan] 0 means every column. */
data class EditHintItem(val row: Int, val rowSpan: Int, val col: Int = 0, val colSpan: Int = 0)

/** The strip the hint takes on its edge: [rows] tall, over the columns [cols] (all columns when null). */
data class EditHintBand(val rows: Int, val cols: IntRange? = null)

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
