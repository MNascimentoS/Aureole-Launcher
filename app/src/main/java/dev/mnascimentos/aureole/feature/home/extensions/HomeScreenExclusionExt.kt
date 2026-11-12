package dev.mnascimentos.aureole.feature.home.extensions

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import dev.mnascimentos.aureole.feature.home.model.GestureExclusion
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.gestureExclusion
import dev.mnascimentos.aureole.feature.home.model.gestureExclusionRects

/** Applies the exclusion decided by [dev.mnascimentos.aureole.feature.home.model.gestureExclusion]. */
private fun Modifier.homeScreenExclusion(exclusion: GestureExclusion, edgeInsetPx: Int): Modifier {
    if (exclusion == GestureExclusion.NONE) return this
    return systemGestureExclusion {
        val rect = gestureExclusionRects(exclusion, it.size.width, it.size.height, edgeInsetPx).firstOrNull()
        if (rect == null) {
            Rect.Zero
        } else {
            Rect(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
        }
    }
}

/** The exclusion modifier of the home for [uiState], using the system gesture strip width of its edge. */
@Composable
fun rememberHomeScreenExclusion(uiState: MainUiState): Modifier {
    val exclusion = uiState.gestureExclusion()
    return Modifier.homeScreenExclusion(exclusion, rememberExclusionInsetPx(exclusion))
}

/** Width of the system gesture strip on the edge that [exclusion] covers; zero with button navigation. */
@Composable
private fun rememberExclusionInsetPx(exclusion: GestureExclusion): Int {
    val gestures = WindowInsets.systemGestures
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    return if (exclusion == GestureExclusion.LEFT_EDGE) {
        gestures.getLeft(density, layoutDirection)
    } else {
        gestures.getRight(density, layoutDirection)
    }
}
