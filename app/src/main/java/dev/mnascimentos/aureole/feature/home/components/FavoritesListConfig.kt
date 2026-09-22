package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState

data class FavoritesListConfig(
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val state: LazyListState,
    val hazeState: HazeState? = null
)
