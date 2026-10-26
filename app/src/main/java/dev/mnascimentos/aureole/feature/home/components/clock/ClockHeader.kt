package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.clock.ClockHeader
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderConfig

@NonRestartableComposable
@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    config: ClockHeaderConfig = ClockHeaderConfig(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    ClockHeader(
        modifier = modifier,
        hazeConfig = hazeConfig,
        config = config,
        onClick = onClick,
        onLongClick = onLongClick
    )
}
