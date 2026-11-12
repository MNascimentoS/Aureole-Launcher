package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.clockConfigOf
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toHeaderConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * The clock [clockId] of the grid, drawn with its own config (or the default one when it has none); a long press
 * opens the clock editor for it.
 */
@Composable
fun ClockContainer(
    modifier: Modifier = Modifier,
    clockId: String? = null,
    hazeState: HazeState? = null,
    uiState: MainUiState = LocalHomeUiState.current,
    actions: HomeScreenActions = LocalHomeActions.current
) {
    val clockConfig = uiState.clockConfigOf(clockId)
    Box(modifier = modifier) {
        HomeClockHeader(
            hazeConfig = ClockHazeConfig(
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity,
                isBackgroundEnabled = clockConfig.isBackgroundEnabled
            ),
            config = clockConfig.toHeaderConfig(),
            onLongClick = { actions.onOpenEditClockBottomSheet(clockId) }
        )
    }
}

@AureolePreview
@Composable
private fun ClockContainerPreview() {
    AureoleLauncherTheme {
        ClockContainer()
    }
}
