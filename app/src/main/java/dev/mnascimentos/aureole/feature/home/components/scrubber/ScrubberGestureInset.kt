package dev.mnascimentos.aureole.feature.home.components.scrubber

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberEdge
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberExclusionRect
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberGestureInsetPx
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

/** Width in px of the system gesture strip on the scrubber edge, read from the window insets; zero with buttons. */
@Composable
fun scrubberGestureInsetPx(): Int {
    val density = LocalDensity.current
    val insets = WindowInsets.systemGestures
    return scrubberGestureInsetPx(
        isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode,
        leftInsetPx = insets.getLeft(density, LayoutDirection.Ltr),
        rightInsetPx = insets.getRight(density, LayoutDirection.Ltr)
    )
}

/** Edge and inset of the scrubber: moved inside the gesture strip only when the options ask for it. */
@Composable
fun rememberScrubberEdge(options: ScrubberOptions): ScrubberEdge {
    val density = LocalDensity.current
    val insetPx = if (options.isInsetByGestures) scrubberGestureInsetPx() else 0
    return ScrubberEdge(
        isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode,
        gestureInset = with(density) { insetPx.toDp() }
    )
}

/** Keeps the system back gesture off the touch area while the drawer is open; nothing otherwise. */
fun Modifier.scrubberGestureExclusion(isActive: Boolean): Modifier {
    if (!isActive) return this
    return systemGestureExclusion { scrubberExclusionRect(it.size.width.toFloat(), it.size.height.toFloat()) }
}
