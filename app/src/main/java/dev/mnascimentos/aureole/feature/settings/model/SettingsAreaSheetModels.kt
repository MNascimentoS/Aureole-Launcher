package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec

/**
 * What the area sheet of the settings shows.
 *
 * @property area The open area.
 * @property inner Picker shown inside the sheet in place of the list (Appearance only), or null for the list.
 * @property isVisible False while a child sheet is swapped in, or once the area is closing.
 * @property overlayHost What the host decides for the sheet (width in landscape, one shared backdrop).
 */
data class AreaSheetSpec(
    val area: SettingsArea,
    val inner: SettingsInnerKind?,
    val isVisible: Boolean,
    val overlayHost: OverlayHostSpec = OverlayHostSpec()
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

/**
 * What the settings host needs to draw its sheets.
 *
 * @property uiState The settings state.
 * @property actions What the rows of the sheets do.
 * @property onNavigateToLicenses Opens the licenses screen, which is not a sheet.
 * @property initialSheet Sheet open on the first frame (previews and screenshot tests).
 * @property initialScroll Scroll of that area on the first frame, clamped to its end.
 */
data class SettingsSheetHostParams(
    val uiState: SettingsUiState,
    val actions: SettingsScreenActions,
    val onNavigateToLicenses: () -> Unit,
    val initialSheet: SettingsSheet? = null,
    val initialScroll: Int = 0
)
