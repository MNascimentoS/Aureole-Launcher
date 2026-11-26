package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.toSize
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
    val density = LocalDensity.current
    // The editor preview draws this clock at the size it has here (RF-B5).
    Box(
        modifier = modifier.onSizeChanged { size ->
            actions.onClockMeasured(clockId, with(density) { size.toSize().toDpSize() })
        }
    ) {
        HomeClockHeader(
            hazeConfig = ClockHazeConfig(
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity,
                isBackgroundEnabled = clockConfig.isBackgroundEnabled
            ),
            config = clockConfig.toHeaderConfig(),
            onLongClick = { actions.onOpenEditClockBottomSheet(clockId) },
            isCentered = true
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
