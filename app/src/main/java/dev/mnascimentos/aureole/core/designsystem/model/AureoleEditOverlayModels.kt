package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState

/**
 * State holder for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @property internalVisible Whether the overlay is internally visible for exit animations.
 * @property dragExpansion The vertical drag expansion offset of the sheet.
 */
class AureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialDragExpansion: Float = 0f,
) {
    var internalVisible by mutableStateOf(initialInternalVisible)
    var dragExpansion by mutableFloatStateOf(initialDragExpansion)
}

/**
 * Creates and remembers an [AureoleEditOverlayState].
 */
@Composable
fun rememberAureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialDragExpansion: Float = 0f,
): AureoleEditOverlayState {
    return remember {
        AureoleEditOverlayState(
            initialInternalVisible = initialInternalVisible,
            initialDragExpansion = initialDragExpansion,
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
 * @property maxSheetHeightFraction Maximum fraction of screen height occupied by the sheet.
 * @property onBackPress Custom back key handler callback.
 * @property onSettingsClick Settings icon click callback.
 */
data class AureoleEditOverlayConfig(
    val title: String? = null,
    val hazeState: HazeState? = null,
    val isVisible: Boolean = true,
    val animated: Boolean = true,
    val maxSheetHeightFraction: Float = 0.70f, // Magic number
    val onBackPress: (() -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
)

/**
 val maxSheetHeightFraction: Float = 0.70f,
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

internal data class SheetBounds(
    val maxHeight: Dp,
    val maxExpansionAllowed: Dp
)

internal class OverlayDismissState(
    val visibleState: MutableTransitionState<Boolean>,
    val internalVisible: Boolean,
    val triggerDismiss: () -> Unit
)
