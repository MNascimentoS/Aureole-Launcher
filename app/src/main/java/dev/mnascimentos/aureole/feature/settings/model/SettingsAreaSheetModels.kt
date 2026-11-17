package dev.mnascimentos.aureole.feature.settings.model

/**
 * What the area sheet of the settings shows.
 *
 * @property area The open area.
 * @property inner Picker shown inside the sheet in place of the list (Appearance only), or null for the list.
 * @property isVisible False while a child sheet is swapped in, or once the area is closing.
 */
data class AreaSheetSpec(
    val area: SettingsArea,
    val inner: SettingsInnerKind?,
    val isVisible: Boolean
)

/**
 * How the area sheet leaves.
 *
 * @property onClosed The sheet slid out (tap outside, drag down, back on the list).
 * @property onBackFromInner Back while a picker is inside the sheet: the list comes back, the sheet stays.
 */
class AreaSheetEvents(
    val onClosed: () -> Unit,
    val onBackFromInner: () -> Unit
)
