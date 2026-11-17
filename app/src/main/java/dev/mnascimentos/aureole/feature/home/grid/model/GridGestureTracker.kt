package dev.mnascimentos.aureole.feature.home.grid.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Counts the grid cells being dragged or resized right now, so layers above the grid (the onboarding save pill)
 * can step aside during the gesture without the grid knowing about them.
 */
class GridGestureTracker {
    private var activeCount by mutableIntStateOf(0)

    /** True while at least one cell is being dragged or resized. */
    val isGesturing: Boolean get() = activeCount > 0

    fun begin() {
        activeCount++
    }

    fun end() {
        activeCount = (activeCount - 1).coerceAtLeast(0)
    }
}

/** Provided by the home scaffold; null where nobody listens (previews, tests). */
val LocalGridGestureTracker = staticCompositionLocalOf<GridGestureTracker?> { null }
