package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState

/**
 * State holder for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @property internalVisible Whether the overlay is internally visible for exit animations.
 * @property isExpanded Whether the sheet sits at its expanded height instead of its rest height.
 */
class AureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialExpanded: Boolean = false,
) {
    var internalVisible by mutableStateOf(initialInternalVisible)
    var isExpanded by mutableStateOf(initialExpanded)
}

/**
 * Creates and remembers an [AureoleEditOverlayState].
 */
@Composable
fun rememberAureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialExpanded: Boolean = false,
): AureoleEditOverlayState {
    return remember {
        AureoleEditOverlayState(
            initialInternalVisible = initialInternalVisible,
            initialExpanded = initialExpanded,
        )
    }
}

/**
 * Configuration options for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @property title Title text displayed at the top of the overlay sheet.
 * @property hazeState Blur effect state for background haze blurring.
 * @property isVisible Controls visibility state of the overlay.
 * @property animated Whether show/hide transition animations are enabled.
 * @property restHeightFraction Maximum fraction of the screen the sheet takes at rest; below it the sheet
 * hugs its content.
 * @property expandedHeightFraction Fraction of the screen the sheet takes when expanded.
 * @property isLeftHanded Whether the user holds the phone with the left thumb. Callers pass the launcher setting.
 * @property onBackPress Custom back key handler callback.
 * @property onSettingsClick Settings icon click callback.
 * @property headerIcon Optional custom icon for sheet header end action.
 */
data class AureoleEditOverlayConfig(
    val title: String? = null,
    val hazeState: HazeState? = null,
    val isVisible: Boolean = true,
    val animated: Boolean = true,
    val restHeightFraction: Float = 0.6f,
    val expandedHeightFraction: Float = 0.9f,
    val isLeftHanded: Boolean = false,
    val onBackPress: (() -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
    val headerIcon: (@Composable (Modifier) -> Unit)? = null,
)

/**
 * Parameter container for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @param config Configuration options provided via [AureoleEditOverlayConfig].
 * @param previewContent Optional preview slot displayed in the main screen area above the bottom sheet.
 * @param bottomBarContent Optional action bottom bar slot anchored below the sheet.
 * @param sheetContent Main sheet content slot.
 */
class AureoleEditOverlayParams(
    val config: AureoleEditOverlayConfig = AureoleEditOverlayConfig(),
    val previewContent: (@Composable BoxScope.() -> Unit)? = null,
    val bottomBarContent: (@Composable ColumnScope.() -> Unit)? = null,
    val sheetContent: (@Composable ColumnScope.() -> Unit)? = null,
)

/**
 * Sheet heights in pixels, resolved from the screen height and [AureoleEditOverlayConfig].
 *
 * @property restMaxPx Largest height the sheet takes at rest.
 * @property expandedPx Height of the expanded sheet.
 */
internal data class SheetBounds(
    val restMaxPx: Float,
    val expandedPx: Float
)

/**
 * Extra slots of the sheet body: a custom header icon and the invisible gesture zone above the header.
 */
internal class OverlaySheetDecorations(
    val headerIcon: (@Composable (Modifier) -> Unit)? = null,
    val gestureZone: (@Composable () -> Unit)? = null
)

internal class OverlayDismissState(
    val visibleState: MutableTransitionState<Boolean>,
    val internalVisible: Boolean,
    val triggerDismiss: () -> Unit
)
