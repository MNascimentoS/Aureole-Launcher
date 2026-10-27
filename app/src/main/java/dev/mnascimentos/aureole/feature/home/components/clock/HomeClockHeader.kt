package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.clock.ClockHeader
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun HomeClockHeader(
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    config: ClockHeaderConfig = ClockHeaderConfig(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val uiState = LocalHomeUiState.current
    ClockHeader(
        resolvedConfig = resolveClockConfig(config, uiState),
        modifier = modifier,
        hazeConfig = resolveClockHazeConfig(hazeConfig, uiState),
        onClick = onClick,
        onLongClick = onLongClick
    )
}

internal fun resolveClockConfig(config: ClockHeaderConfig, uiState: MainUiState): ResolvedClockConfig {
    return ResolvedClockConfig(
        style = config.style ?: uiState.clockStyle,
        customGreeting = config.customGreeting ?: uiState.clockCustomGreeting,
        alignment = config.alignment ?: uiState.clockAlignment,
        fontFamily = config.fontFamily ?: uiState.clockFontFamily,
        timeFormat = config.timeFormat ?: uiState.clockTimeFormat,
        dateFormat = config.dateFormat ?: uiState.clockDateFormat,
        textColor = config.textColor ?: uiState.clockTextColor,
        backgroundColor = config.backgroundColor ?: uiState.clockBackgroundColor
    )
}

internal fun resolveClockHazeConfig(hazeConfig: ClockHazeConfig, uiState: MainUiState): ClockHazeConfig {
    return ClockHazeConfig(
        hazeState = hazeConfig.hazeState,
        isHazeEnabled = hazeConfig.isHazeEnabled || uiState.isHazeEnabled,
        hazeOpacity = if (hazeConfig.isHazeEnabled) hazeConfig.hazeOpacity else uiState.hazeOpacity,
        isBackgroundEnabled = hazeConfig.isBackgroundEnabled && uiState.isClockBackgroundEnabled
    )
}

@AureolePreview
@Composable
private fun HomeClockHeaderPreview() {
    AureoleLauncherTheme {
        HomeClockHeader()
    }
}
