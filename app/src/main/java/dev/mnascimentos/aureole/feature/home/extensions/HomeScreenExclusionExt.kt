package dev.mnascimentos.aureole.feature.home.extensions

import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect

fun Modifier.homeScreenExclusion(isSystemBackActive: Boolean): Modifier {
    return if (!isSystemBackActive) {
        this.systemGestureExclusion {
            val heightPx = it.size.height.toFloat()
            val widthPx = it.size.width.toFloat()
            Rect(0f, 0f, widthPx, heightPx)
        }
    } else {
        this
    }
}
