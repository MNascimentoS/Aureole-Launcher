package dev.mnascimentos.aureole.composable.clock

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.CLOCK_TIME_PATTERN_12H
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.composable.clock.model.ResolvedClockConfig
import dev.mnascimentos.aureole.composable.clock.model.resolveClockSizing
import dev.mnascimentos.aureole.core.data.model.ClockMessage
import dev.mnascimentos.aureole.core.data.model.resolveClockLayout
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.designsystem.utils.glassBlurRadius

/**
 * The clock card. Its sizes come from the room it gets (`resolveClockSizing`): the time takes the biggest size that
 * fits the width and the height, and every other size is a share of it (relogio-home-sheet D-08), so the card is
 * the same at any size on the grid and in the editor preview. [onLongClick] opens the editor; a tap does nothing
 * and shows no ripple (RF-A1). With [isCentered], a card taller than its lines (a tall cell on the grid) centers them.
 */
@Composable
fun ClockHeader(
    resolvedConfig: ResolvedClockConfig,
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    onLongClick: (() -> Unit)? = null,
    isCentered: Boolean = false
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
    val sidePadding = AureoleDS.dimens.large
    val hourGreeting = stringResource(greetingRes(hourOfDay()))
    val fontScale = LocalDensity.current.fontScale
    val isTwelveHour = ClockHeaderUtils.resolveClockTimePattern(
        resolvedConfig.timeFormat,
        DateFormat.is24HourFormat(LocalContext.current)
    ) == CLOCK_TIME_PATTERN_12H

    // A style saved by an older version reads as its layout and message, so the look never changes.
    val layoutChoice = resolveClockLayout(resolvedConfig.style, resolvedConfig.message)
    val greeting = clockGreeting(layoutChoice.message, resolvedConfig.customGreeting, hourGreeting)

    val containerBgColor = resolveClockBgColor(
        isBgEnabled = isBgEnabled,
        isHazeEnabled = isHazeEnabled,
        bgColorInt = resolvedConfig.backgroundColor,
        hazeOpacity = hazeOpacity,
        surfaceColor = surfaceColor
    )

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sizing = resolveClockSizing(
            lines = layoutChoice.layout.lines,
            maxTextWidthDp = (maxWidth - sidePadding * 2).value,
            maxHeightDp = maxHeight.value,
            fontScale = fontScale,
            isTwelveHour = isTwelveHour
        )
        val edge = sizing.metrics.edgeDp.dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isCentered && constraints.hasBoundedHeight) Modifier.fillMaxHeight() else Modifier)
                .clip(RoundedCornerShape(sizing.metrics.radiusDp.dp))
                .clockHazeEffect(isHazeEnabled, hazeStateRef, hazeOpacity, hazeBlurRadius, hazeTintColor)
                .background(containerBgColor)
                .clockLongPressOf(onLongClick)
                // Inner room: 24 dp on the sides; above and below the trimmed lines, the edge share of the time.
                .padding(start = sidePadding, end = sidePadding, top = edge, bottom = edge)
        ) {
            ClockHeaderContent(
                params = ClockHeaderContentParams(
                    sizing = sizing,
                    colorPrimary = colorPrimaryInt,
                    composeTextColor = colorPrimaryCompose,
                    greeting = greeting,
                    layout = layoutChoice.layout,
                    alignment = resolvedConfig.alignment,
                    fontFamily = resolvedConfig.fontFamily,
                    timeFormat = resolvedConfig.timeFormat,
                    dateFormat = resolvedConfig.dateFormat,
                    isFontBold = resolvedConfig.isFontBold
                )
            )
        }
    }
}

/** The custom message when the layout asks for it and it is not blank; the greeting of the hour otherwise. */
private fun clockGreeting(message: ClockMessage, customGreeting: String, hourGreeting: String): String =
    if (message == ClockMessage.CUSTOM && customGreeting.isNotBlank()) customGreeting else hourGreeting

/**
 * The long press of the clock: a `LongPress` haptic, then [onLongClick]; no modifier at all without it. The gesture
 * reads the latest callback, so a recomposition never restarts a press in progress.
 */
@Composable
private fun Modifier.clockLongPressOf(onLongClick: (() -> Unit)?): Modifier {
    val label = stringResource(R.string.home_clock_customize_title)
    val haptics = LocalHapticFeedback.current
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    return clockLongPress(isEnabled = onLongClick != null, label = label) {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        currentOnLongClick?.invoke()
    }
}

/**
 * Long press only, with no press indication (RF-A1, RF-A2): holding runs [onHold]. No button role; TalkBack reads
 * the merged clock and offers "double tap and hold to [label]" (RF-A3). Not [isEnabled]: no gesture at all.
 */
private fun Modifier.clockLongPress(isEnabled: Boolean, label: String, onHold: () -> Unit): Modifier {
    if (!isEnabled) return this
    return this
        .pointerInput(Unit) { detectTapGestures(onLongPress = { onHold() }) }
        .semantics(mergeDescendants = true) {
            onLongClick(label = label) {
                onHold()
                true
            }
        }
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
