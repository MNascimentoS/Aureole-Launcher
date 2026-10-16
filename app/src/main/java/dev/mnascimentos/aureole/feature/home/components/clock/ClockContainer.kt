package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

// Note: Reduce parameter count
@Suppress("LongParameterList")
@Composable
fun ClockContainer(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    uiState: MainUiState = LocalHomeUiState.current,
    actions: HomeScreenActions = LocalHomeActions.current,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = { actions.onOpenEditClockBottomSheet() }
) {
    Box(modifier = modifier) {
        ClockHeader(
            hazeConfig = ClockHazeConfig(
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity,
                isBackgroundEnabled = uiState.isClockBackgroundEnabled
            ),
            onClick = onClick,
            onLongClick = onLongClick
        )
    }
}

@AureolePreview
@Composable
internal fun ClockContainerPreview() {
    AureoleLauncherTheme {
        ClockContainer()
    }
}
