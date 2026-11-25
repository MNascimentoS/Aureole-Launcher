package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/** Progress of the drawer, 0 closed .. 1 open, from which the swipe up opens it (D-04). */
const val DRAWER_OPEN_PROGRESS = 0.3f

/** Upward speed, in dp/s, that opens the drawer whatever the progress (D-04). */
const val DRAWER_OPEN_FLING_DP_PER_S = 1000f

/** The drawer reaches fully open after the finger travelled this share of the screen height. */
const val DRAWER_TRAVEL_FRACTION = 0.6f

/** The home dims up to this much while the drawer comes up (D-06). */
const val HOME_DIM_MAX = 0.4f

/** The home shrinks to this scale while the drawer comes up (D-06). */
const val HOME_SCALE_MIN = 0.94f

private const val SETTLE_MS = 180

/** How a drawer drag ends: it goes all the way, or comes back. */
enum class DrawerSettle { OPEN, CLOSE }

/** Progress (0..1) of a swipe up that moved [upPx] on a screen [screenHeightPx] tall. */
fun drawerProgressOf(upPx: Float, screenHeightPx: Float): Float =
    if (screenHeightPx <= 0f) 0f else (upPx / (screenHeightPx * DRAWER_TRAVEL_FRACTION)).coerceIn(0f, 1f)

/** Where a swipe up that let go at [progress], going up at [upDpPerSec] (negative: going down), ends (RF-04). */
fun drawerSettleOf(progress: Float, upDpPerSec: Float): DrawerSettle = when {
    upDpPerSec >= DRAWER_OPEN_FLING_DP_PER_S -> DrawerSettle.OPEN
    upDpPerSec <= -DRAWER_OPEN_FLING_DP_PER_S -> DrawerSettle.CLOSE
    progress >= DRAWER_OPEN_PROGRESS -> DrawerSettle.OPEN
    else -> DrawerSettle.CLOSE
}

/**
 * Where a drag down on the open drawer ends (RF-05), the mirror of [drawerSettleOf]: [closedProgress] is how far it
 * went toward closed (0..1), [downDpPerSec] how fast it goes down.
 */
fun drawerCloseSettleOf(closedProgress: Float, downDpPerSec: Float): DrawerSettle =
    if (drawerSettleOf(closedProgress, downDpPerSec) == DrawerSettle.OPEN) DrawerSettle.CLOSE else DrawerSettle.OPEN

/** Dark layer over the home at [progress] of the drawer. */
fun homeDimAlpha(progress: Float): Float = HOME_DIM_MAX * progress.coerceIn(0f, 1f)

/** Scale of the home at [progress] of the drawer. */
fun homeScale(progress: Float): Float = 1f - (1f - HOME_SCALE_MIN) * progress.coerceIn(0f, 1f)

/**
 * The drawer being pulled up by a finger (RF-04). [progress] is the drawer's place; [effect] is what shows on the
 * home (dark layer and scale), which fades on its own after the drawer takes over. With [isAnimated] off (system
 * animations removed, RF-08) a drag ends at its final state with no animation.
 */
@Stable
class DrawerDragState(private val isAnimated: Boolean = true, initialProgress: Float = 0f) {
    val progress = Animatable(initialProgress)
    val effect = Animatable(initialProgress)

    /** The finger is down, or the drawer is settling: the pulled drawer is drawn. */
    var isActive by mutableStateOf(initialProgress > 0f)
        private set

    /** The drawer that opens next was already on screen: it appears in place, with no slide of its own. */
    var isEnterSkipped by mutableStateOf(false)
        private set

    /** The drawer closed: the next open slides in as usual again. */
    fun onDrawerClosed() {
        isEnterSkipped = false
    }

    suspend fun drag(to: Float) {
        isActive = true
        progress.snapTo(to)
        effect.snapTo(to)
    }

    /** The drawer is open for real now: this one steps aside and the effect on the home fades out. */
    suspend fun handOver(open: () -> Unit) {
        isEnterSkipped = true
        open()
        progress.snapTo(0f)
        isActive = false
        if (isAnimated) effect.animateTo(0f, tween(SETTLE_MS)) else effect.snapTo(0f)
    }

    suspend fun settle(to: DrawerSettle) {
        val target = if (to == DrawerSettle.OPEN) 1f else 0f
        if (isAnimated) {
            val motion = spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
            coroutineScope {
                launch { effect.animateTo(target, motion) }
                progress.animateTo(target, motion)
            }
        } else {
            progress.snapTo(target)
            effect.snapTo(target)
        }
        if (to == DrawerSettle.CLOSE) {
            effect.snapTo(0f)
            isActive = false
        }
    }
}

/** What the vertical detector of the home reports while a swipe up pulls the drawer (RF-04). */
data class DrawerDragCallbacks(
    /** The drawer's progress (0..1) for the finger as it moves. */
    val onProgress: (Float) -> Unit,
    /** The finger let go at [progress] going up at [upDpPerSec] (negative: going down). */
    val onEnd: (progress: Float, upDpPerSec: Float) -> Unit
)

/** The state of one vertical gesture: it fired already, and it is the swipe up that pulls the drawer. */
class VerticalGestureState {
    var lock = GestureLock.UNDECIDED
    var hasTriggered = false
    var isDrawerDrag = false
    var progress = 0f

    /** A swipe down is under way: the shade opens on release if the finger is still past the limit ([shadeDownPx]). */
    var isShadeSwipe = false
    var shadeDownPx = 0f

    fun reset() {
        lock = GestureLock.UNDECIDED
        hasTriggered = false
        isDrawerDrag = false
        progress = 0f
        isShadeSwipe = false
        shadeDownPx = 0f
    }
}

/**
 * The callbacks the home hands to the vertical detector: the drawer follows the finger and, on release, goes
 * all the way (after [onConfirm], the light tick, and [onOpen], which opens it for real) or comes back (RF-04).
 */
fun DrawerDragState.toCallbacks(scope: CoroutineScope, onOpen: () -> Unit, onConfirm: () -> Unit) =
    DrawerDragCallbacks(
        onProgress = { progress -> scope.launch { drag(progress) } },
        onEnd = { progress, upDpPerSec ->
            scope.launch {
                when (drawerSettleOf(progress, upDpPerSec)) {
                    DrawerSettle.OPEN -> {
                        onConfirm()
                        settle(DrawerSettle.OPEN)
                        handOver(onOpen)
                    }
                    DrawerSettle.CLOSE -> settle(DrawerSettle.CLOSE)
                }
            }
        }
    )
