package dev.mnascimentos.aureole.composable.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
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
 * and shows no ripple (RF-A1).
 */
@Composable
fun ClockHeader(
    resolvedConfig: ResolvedClockConfig,
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
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
    val sidePadding = AureoleDS.dimens.large
    val hourGreeting = stringResource(greetingRes(hourOfDay()))
    val longClickLabel = stringResource(R.string.home_clock_customize_title)
    val haptics = LocalHapticFeedback.current
    // The gesture reads the latest callback, so a recomposition never restarts a press in progress.
    val currentOnLongClick by rememberUpdatedState(onLongClick)
    val fontScale = LocalDensity.current.fontScale

    // A style saved by an older version reads as its layout and message, so the look never changes.
    val layoutChoice = resolveClockLayout(resolvedConfig.style, resolvedConfig.message)
    val greeting = if (layoutChoice.message == ClockMessage.CUSTOM && resolvedConfig.customGreeting.isNotBlank()) {
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

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val sizing = resolveClockSizing(
            lines = layoutChoice.layout.lines,
            maxTextWidthDp = (maxWidth - sidePadding * 2).value,
            maxHeightDp = maxHeight.value,
            fontScale = fontScale
        )
        val edge = sizing.metrics.edgeDp.dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(sizing.metrics.radiusDp.dp))
                .clockHazeEffect(isHazeEnabled, hazeStateRef, hazeOpacity, hazeBlurRadius, hazeTintColor)
                .background(containerBgColor)
                .clockLongPress(isEnabled = onLongClick != null, label = longClickLabel) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    currentOnLongClick?.invoke()
                }
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
