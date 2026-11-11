package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.reorder.model.ReorderAutoScroll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Band at each end of the viewport where a held item scrolls the list. */
private const val AUTO_SCROLL_EDGE_DP = 48

/** Scroll speed with the finger at the very edge; it ramps up linearly from the inner border of the band. */
private const val AUTO_SCROLL_MAX_SPEED_DP_PER_SECOND = 800

private const val NANOS_PER_SECOND = 1_000_000_000f

/** Auto scroll of a reorderable list that lives inside a container scrolled by [scrollable]. */
@Composable
fun rememberReorderAutoScroll(scrollable: ScrollableState): ReorderAutoScroll =
    remember(scrollable) { ReorderAutoScroll(scrollable) }

/**
 * Marks the viewport of [autoScroll]: apply it to the scrolling container before its scroll modifier
 * (`Modifier.reorderAutoScrollViewport(a).verticalScroll(state)`), or to a lazy list's own modifier.
 */
fun Modifier.reorderAutoScrollViewport(autoScroll: ReorderAutoScroll): Modifier =
    onPlaced { coordinates -> autoScroll.viewport = coordinates }

/** True when the last tracked finger sits in an edge band and the list can still scroll that way. */
internal fun ReorderAutoScroll.wantsScroll(density: Density): Boolean = frameDelta(
    edge = with(density) { AUTO_SCROLL_EDGE_DP.dp.toPx() },
    maxSpeed = with(density) { AUTO_SCROLL_MAX_SPEED_DP_PER_SECOND.dp.toPx() },
    frameSeconds = 1f
) != 0f

/**
 * Scrolls one step per frame while the finger stays in an edge band, reporting each scroll [onScrolled] (px
 * really scrolled, positive towards the end) so the held item can stay under the finger. Returns once the finger
 * leaves the band or the list reaches that end; cancel it when the finger lifts.
 */
internal suspend fun ReorderAutoScroll.scrollWhileNearEdge(density: Density, onScrolled: (Float) -> Unit) {
    val edge = with(density) { AUTO_SCROLL_EDGE_DP.dp.toPx() }
    val maxSpeed = with(density) { AUTO_SCROLL_MAX_SPEED_DP_PER_SECOND.dp.toPx() }
    var last = withFrameNanos { it }
    while (currentCoroutineContext().isActive && wantsScroll(density)) {
        val now = withFrameNanos { it }
        val delta = frameDelta(edge, maxSpeed, (now - last) / NANOS_PER_SECOND)
        last = now
        val consumed = if (delta == 0f) 0f else scrollable.scrollBy(delta)
        if (consumed != 0f) onScrolled(consumed)
    }
}

/** Starts [scrollWhileNearEdge] in [scope] when the finger just entered an edge band and no loop runs yet. */
internal fun ReorderAutoScroll.scrollIfNearEdge(scope: CoroutineScope, density: Density, onScrolled: (Float) -> Unit) {
    if (job?.isActive == true || !wantsScroll(density)) return
    job = scope.launch { scrollWhileNearEdge(density, onScrolled) }
}

/** Stops scrolling and forgets the finger: the drag ended or was cancelled. */
internal fun ReorderAutoScroll.release() {
    job?.cancel()
    job = null
    stop()
}
