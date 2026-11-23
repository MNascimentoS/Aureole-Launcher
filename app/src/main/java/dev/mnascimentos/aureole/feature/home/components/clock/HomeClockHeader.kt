package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.clock.ClockHeader
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.composable.clock.model.resolveClockColor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun HomeClockHeader(
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    config: ClockHeaderConfig = ClockHeaderConfig(),
    onLongClick: (() -> Unit)? = null
) {
    val uiState = LocalHomeUiState.current
    ClockHeader(
        resolvedConfig = resolveClockConfig(config, uiState, AureoleDS.colors),
        modifier = modifier,
        hazeConfig = resolveClockHazeConfig(hazeConfig, uiState, config),
        onLongClick = onLongClick
    )
}

internal fun resolveClockConfig(
    config: ClockHeaderConfig,
    uiState: MainUiState,
    colors: AureoleColors? = null
): ResolvedClockConfig {
    return ResolvedClockConfig(
        style = config.style ?: uiState.clockStyle,
        customGreeting = config.customGreeting ?: uiState.clockCustomGreeting,
        message = config.message ?: uiState.clockMessage,
        alignment = config.alignment ?: uiState.clockAlignment,
        fontFamily = config.fontFamily ?: uiState.clockFontFamily,
        isFontBold = config.isFontBold ?: uiState.clockFontBold,
        timeFormat = config.timeFormat ?: uiState.clockTimeFormat,
        dateFormat = config.dateFormat ?: uiState.clockDateFormat,
        textColor = resolveClockColor(
            legacyArgb = config.textColor ?: uiState.clockTextColor,
            refJson = if (config.textColor != null) config.textColorRef else uiState.clockTextColorRef,
            colors = colors
        ),
        backgroundColor = resolveClockColor(
            legacyArgb = config.backgroundColor ?: uiState.clockBackgroundColor,
            refJson = if (config.backgroundColor != null) {
                config.backgroundColorRef
            } else {
                uiState.clockBackgroundColorRef
            },
            colors = colors
        )
    )
}

/** The background switch of [config] wins over the one of the state, as the other fields of [resolveClockConfig]. */
internal fun resolveClockHazeConfig(
    hazeConfig: ClockHazeConfig,
    uiState: MainUiState,
    config: ClockHeaderConfig = ClockHeaderConfig()
): ClockHazeConfig {
    return ClockHazeConfig(
        hazeState = hazeConfig.hazeState,
        isHazeEnabled = hazeConfig.isHazeEnabled || uiState.isHazeEnabled,
        hazeOpacity = if (hazeConfig.isHazeEnabled) hazeConfig.hazeOpacity else uiState.hazeOpacity,
        isBackgroundEnabled = hazeConfig.isBackgroundEnabled &&
            (config.isBackgroundEnabled ?: uiState.isClockBackgroundEnabled)
    )
}

@AureolePreview
@Composable
private fun HomeClockHeaderPreview() {
    AureoleLauncherTheme {
        HomeClockHeader()
    }
}
