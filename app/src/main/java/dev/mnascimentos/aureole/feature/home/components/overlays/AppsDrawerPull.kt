package dev.mnascimentos.aureole.feature.home.components.overlays

import android.animation.ValueAnimator
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import dev.mnascimentos.aureole.feature.home.model.DRAWER_TRAVEL_FRACTION
import dev.mnascimentos.aureole.feature.home.model.DrawerDragState
import dev.mnascimentos.aureole.feature.home.model.DrawerSettle
import dev.mnascimentos.aureole.feature.home.model.drawerCloseSettleOf
import dev.mnascimentos.aureole.feature.home.model.homeDimAlpha
import dev.mnascimentos.aureole.feature.home.model.homeScale
import kotlinx.coroutines.launch

/** The home shrinks and dims while the drawer is pulled up (RF-04, D-06); nothing without a [drawerDrag]. */
fun Modifier.drawerPullEffect(drawerDrag: DrawerDragState?): Modifier {
    if (drawerDrag == null) return this
    return this
        .graphicsLayer {
            val scale = homeScale(drawerDrag.effect.value)
            scaleX = scale
            scaleY = scale
        }
        .drawWithContent {
            drawContent()
            drawRect(Color.Black.copy(alpha = homeDimAlpha(drawerDrag.effect.value)))
        }
}

/**
 * A drag down on the open drawer, with its list at the top, takes the drawer down with the finger (RF-05): on release
 * it closes ([onClose]) or comes back, by the same limit and speed as the swipe up, mirrored. With the system
 * animations removed it jumps to its end (RF-08).
 */
@Composable
fun Modifier.drawerCloseDrag(listState: LazyListState, onClose: () -> Unit): Modifier {
    var offsetPx by remember { mutableFloatStateOf(0f) }
    var heightPx by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current.density
    val isAnimated = remember { ValueAnimator.areAnimatorsEnabled() }
    val connection = remember(listState, onClose) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Finger going back up with the drawer pulled down: it gives the room back first.
                if (source != NestedScrollSource.UserInput || offsetPx <= 0f || available.y >= 0f) return Offset.Zero
                val back = maxOf(available.y, -offsetPx)
                offsetPx += back
                return Offset(0f, back)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput || available.y <= 0f || listState.canScrollBackward) {
                    return Offset.Zero
                }
                offsetPx += available.y
                return Offset(0f, available.y)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (offsetPx <= 0f) return Velocity.Zero
                val closed = (offsetPx / (heightPx * DRAWER_TRAVEL_FRACTION)).coerceIn(0f, 1f)
                if (drawerCloseSettleOf(closed, available.y / density) == DrawerSettle.CLOSE) {
                    onClose()
                } else if (isAnimated) {
                    scope.launch {
                        animate(offsetPx, 0f, animationSpec = spring(Spring.DampingRatioNoBouncy)) { value, _ ->
                            offsetPx = value
                        }
                    }
                } else {
                    offsetPx = 0f
                }
                return Velocity(0f, available.y)
            }
        }
    }
    return this
        .onSizeChanged { heightPx = it.height.toFloat() }
        .nestedScroll(connection)
        .graphicsLayer { translationY = offsetPx }
}
