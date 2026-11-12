package dev.mnascimentos.aureole.composable.clock

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.designsystem.utils.glassBlurRadius

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockHeader(
    resolvedConfig: ResolvedClockConfig,
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val hazeStateRef = hazeConfig.hazeState ?: LocalHazeState.current
    val isHazeEnabled = hazeConfig.isHazeEnabled
    val hazeOpacity = hazeConfig.hazeOpacity
    val isBgEnabled = hazeConfig.isBackgroundEnabled

    val defaultTextColor = AureoleDS.colors.onSurfaceHigh
    val colorPrimaryInt = if (resolvedConfig.textColor != 0) resolvedConfig.textColor else defaultTextColor.toArgb()
    val colorPrimaryCompose = if (resolvedConfig.textColor != 0) Color(resolvedConfig.textColor) else defaultTextColor

    // Read every theme value and string unconditionally: a conditional read here misaligns the slot table
    // when the glass, background or style changes (ClassCastException in rememberComposableLambda).
    val surfaceColor = AureoleDS.colors.surface
    val hazeTintColor = AureoleDS.colors.surfaceVariant
    val hazeBlurRadius = glassBlurRadius(AureoleDS.dimens.large)
    val hourGreeting = stringResource(greetingRes(hourOfDay()))

    val greeting = if (resolvedConfig.style == "CUSTOM_GREETING" && resolvedConfig.customGreeting.isNotBlank()) {
        resolvedConfig.customGreeting
    } else {
        hourGreeting
    }

    val containerBgColor = resolveClockBgColor(
        isBgEnabled = isBgEnabled,
        isHazeEnabled = isHazeEnabled,
        bgColorInt = resolvedConfig.backgroundColor,
        hazeOpacity = hazeOpacity,
        surfaceColor = surfaceColor
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .clockHazeEffect(isHazeEnabled, hazeStateRef, hazeOpacity, hazeBlurRadius, hazeTintColor)
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

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.clockClickable(onClick: (() -> Unit)?, onLongClick: (() -> Unit)?): Modifier {
    if (onClick == null && onLongClick == null) return this
    return this.combinedClickable(
        onClick = { onClick?.invoke() },
        onLongClick = onLongClick,
        role = Role.Button
    ).semantics(mergeDescendants = true) {}
}

@AureolePreview
@Composable
private fun ClockHeaderPreview() {
    AureoleLauncherTheme {
        ClockHeader(
            resolvedConfig = ResolvedClockConfig(
                style = SettingsRepository.DEFAULT_CLOCK_STYLE,
                customGreeting = "",
                alignment = SettingsRepository.DEFAULT_CLOCK_ALIGNMENT,
                fontFamily = SettingsRepository.DEFAULT_CLOCK_FONT_FAMILY,
                timeFormat = SettingsRepository.DEFAULT_CLOCK_TIME_FORMAT,
                dateFormat = SettingsRepository.DEFAULT_CLOCK_DATE_FORMAT,
                textColor = 0,
                backgroundColor = 0
            )
        )
    }
}
