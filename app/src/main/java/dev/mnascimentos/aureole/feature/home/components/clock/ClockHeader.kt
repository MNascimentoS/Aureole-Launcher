package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MORNING_HOUR_START = 5
private const val MORNING_HOUR_END = 11
private const val AFTERNOON_HOUR_START = 12
private const val AFTERNOON_HOUR_END = 17
private const val DEFAULT_NOON_HOUR = 12

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    config: ClockHeaderConfig = ClockHeaderConfig(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val uiState = LocalHomeUiState.current
    val resolvedConfig = resolveClockConfig(config, uiState)
    val hazeStateRef = hazeConfig.hazeState ?: LocalHazeState.current
    val isHazeEnabled = hazeConfig.isHazeEnabled || uiState.isHazeEnabled
    val hazeOpacity = if (hazeConfig.isHazeEnabled) hazeConfig.hazeOpacity else uiState.hazeOpacity
    val isBgEnabled = hazeConfig.isBackgroundEnabled && uiState.isClockBackgroundEnabled

    val defaultTextColor = AureoleDS.colors.onSurfaceHigh.toArgb()
    val colorPrimaryInt = if (resolvedConfig.textColor != 0) resolvedConfig.textColor else defaultTextColor
    val colorPrimaryCompose = if (resolvedConfig.textColor != 0) {
        Color(resolvedConfig.textColor)
    } else {
        AureoleDS.colors.onSurfaceHigh
    }

    val greeting = if (resolvedConfig.style == "CUSTOM_GREETING" && resolvedConfig.customGreeting.isNotBlank()) {
        resolvedConfig.customGreeting
    } else {
        getGreetingForCurrentHour()
    }

    val containerBgColor = resolveClockBgColor(
        isBgEnabled = isBgEnabled,
        isHazeEnabled = isHazeEnabled,
        bgColorInt = resolvedConfig.backgroundColor,
        hazeOpacity = hazeOpacity
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .clockHazeEffect(isHazeEnabled, hazeStateRef, hazeOpacity)
            .background(containerBgColor)
            .clockClickable(onClick, onLongClick)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
    ) {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                maxHeight = maxHeight,
                colorPrimary = colorPrimaryInt,
                composeTextColor = colorPrimaryCompose,
                greeting = greeting,
                clockStyle = resolvedConfig.style,
                alignment = resolvedConfig.alignment,
                fontFamily = resolvedConfig.fontFamily,
                timeFormat = resolvedConfig.timeFormat,
                dateFormat = resolvedConfig.dateFormat
            )
        )
    }
}

private fun resolveClockConfig(config: ClockHeaderConfig, uiState: MainUiState): ResolvedClockConfig {
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

@Composable
private fun Modifier.clockHazeEffect(isHazeEnabled: Boolean, hazeStateRef: HazeState?, hazeOpacity: Float): Modifier {
    if (!isHazeEnabled || hazeStateRef == null) return this
    return this.hazeEffect(
        state = hazeStateRef,
        style = HazeStyle(
            blurRadius = 24.dp,
            tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = hazeOpacity))
        )
    ) {
        blurEnabled = isHazeEnabled
    }
}

@Composable
private fun resolveClockBgColor(
    isBgEnabled: Boolean,
    isHazeEnabled: Boolean,
    bgColorInt: Int,
    hazeOpacity: Float
): Color {
    if (!isBgEnabled) return Color.Transparent
    val baseBgColor = if (bgColorInt != 0) Color(bgColorInt) else AureoleDS.colors.surface
    return if (isHazeEnabled) baseBgColor.copy(alpha = hazeOpacity) else baseBgColor
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.clockClickable(onClick: (() -> Unit)?, onLongClick: (() -> Unit)?): Modifier {
    if (onClick == null && onLongClick == null) return this
    return this.combinedClickable(
        onClick = { onClick?.invoke() },
        onLongClick = onLongClick,
        role = Role.Button
    ).semantics(mergeDescendants = true) {}
}

private fun getGreetingForCurrentHour(): String {
    val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: DEFAULT_NOON_HOUR
    return when (hour) {
        in MORNING_HOUR_START..MORNING_HOUR_END -> "Bom dia"
        in AFTERNOON_HOUR_START..AFTERNOON_HOUR_END -> "Boa tarde"
        else -> "Boa noite"
    }
}

@AureolePreview
@Composable
private fun ClockHeaderPreview() {
    AureoleLauncherTheme {
        ClockHeader()
    }
}
