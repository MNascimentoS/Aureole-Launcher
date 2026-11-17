package dev.mnascimentos.aureole.core.designsystem.model

/**
 * What the layout that anchors the preview above the sheet reads.
 *
 * @property bounds Height limits of the sheet.
 * @property motion Live height of the sheet.
 * @property isExpanded Whether the sheet is expanded.
 */
internal data class AnchoredSheetState(
    val bounds: SheetBounds,
    val motion: OverlaySheetMotion,
    val isExpanded: Boolean
)

/**
 * State of the gesture zone at the top of the sheet.
 *
 * @property isExpanded Whether the sheet is expanded.
 * @property isLeftHanded Whether the settings action sits on the left.
 * @property canExpand False when the sheet at rest already shows all its content.
 */
data class GestureZoneState(
    val isExpanded: Boolean,
    val isLeftHanded: Boolean,
    val canExpand: Boolean = true
)
