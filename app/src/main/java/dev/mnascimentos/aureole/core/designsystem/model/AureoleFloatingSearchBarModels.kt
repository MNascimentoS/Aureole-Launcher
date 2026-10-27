package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState

/**
 * Configuration for the haze effect applied to the Aureole floating search bar.
 */
data class AureoleSearchBarHazeConfig(
    val state: HazeState? = null,
    val isEnabled: Boolean = false,
    val opacity: Float = 0.5f,
)

/**
 * Sizing configuration for the Aureole floating search bar.
 */
data class AureoleSearchBarSizes(
    val barHeight: Dp = 56.dp,
    val collapsedSize: Dp = 56.dp,
)

/**
 * State holder for [dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar].
 */
data class AureoleSearchBarState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val isExpanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
)

/**
 * Parameters for configuring [dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar].
 */
class AureoleFloatingSearchBarParams(
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester(),
    val hazeConfig: AureoleSearchBarHazeConfig = AureoleSearchBarHazeConfig(),
    val sizes: AureoleSearchBarSizes = AureoleSearchBarSizes()
)

/**
 * Parameters for configuring internal expanded search row layout.
 */
data class ExpandedSearchRowParams(
    val backgroundColor: Color,
    val barHeight: Dp,
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester()
)
