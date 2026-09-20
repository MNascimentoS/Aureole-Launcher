package dev.mnascimentos.aureole.ui.theme

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

val LocalHazeState = compositionLocalOf<HazeState?> { null }

fun Modifier.fadingEdges(
    scrollState: LazyListState,
    topLength: Dp = 24.dp,
    bottomLength: Dp = 24.dp
): Modifier = this.graphicsLayer {
    compositingStrategy = CompositingStrategy.Offscreen
}.drawWithContent {
    drawContent()
    val topPx = topLength.toPx()
    val bottomPx = bottomLength.toPx()
    val showTopFade = scrollState.canScrollBackward
    val showBottomFade = scrollState.canScrollForward

    if (showTopFade && topPx > 0f) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black),
                startY = 0f,
                endY = topPx
            ),
            blendMode = BlendMode.DstIn
        )
    }

    if (showBottomFade && bottomPx > 0f) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startY = size.height - bottomPx,
                endY = size.height
            ),
            blendMode = BlendMode.DstIn
        )
    }
}

object HazeUtils {
    /**
     * Checks whether the current device is capable of rendering Haze blur effects.
     * RenderEffect-based blur requires Android 12+ (API 31+) and non-low-RAM hardware.
     */
    fun isDeviceHazeSupported(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return false
        }
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        return activityManager?.isLowRamDevice != true
    }
}
