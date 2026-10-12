package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import android.view.View
import android.widget.TextClock
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MIN_GREETING_HEIGHT_DP = 100
private const val COMPACT_CONTAINER_HEIGHT_DP = 90
private const val MAX_CONTAINER_HEIGHT_DP = 140

private const val CLOCK_TEXT_SIZE_SMALL_PX = 42f
private const val CLOCK_TEXT_SIZE_NORMAL_PX = 56f
private const val CLOCK_TEXT_SIZE_LARGE_PX = 68f

private const val GREETING_SIZE_COMPACT = 12f
private const val GREETING_SIZE_NORMAL = 14f
private const val DATE_SIZE_COMPACT = 11f
private const val DATE_SIZE_NORMAL = 13f

private const val MORNING_HOUR_START = 5
private const val MORNING_HOUR_END = 11
private const val AFTERNOON_HOUR_START = 12
private const val AFTERNOON_HOUR_END = 17
private const val DEFAULT_NOON_HOUR = 12
private const val GREETING_CROSSFADE_DURATION_MS = 500

data class ClockHazeConfig(
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f,
    val isBackgroundEnabled: Boolean = true
)

data class ClockHeaderConfig(
    val style: String? = null,
    val customGreeting: String? = null,
    val alignment: String? = null,
    val fontFamily: String? = null,
    val timeFormat: String? = null,
    val dateFormat: String? = null,
    val textColor: Int? = null,
    val backgroundColor: Int? = null
)

private data class ClockHeaderContentParams(
    val maxHeight: Dp,
    val colorPrimary: Int,
    val composeTextColor: Color,
    val greeting: String,
    val clockStyle: String,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String
)

private data class ClockTimeDisplayParams(
    val colorPrimary: Int,
    val textSizePx: Float,
    val format12: String,
    val format24: String,
    val typeface: Typeface,
    val alignment: String
)

private data class ResolvedClockConfig(
    val style: String,
    val customGreeting: String,
    val alignment: String,
    val fontFamily: String,
    val timeFormat: String,
    val dateFormat: String,
    val textColor: Int,
    val backgroundColor: Int
)

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

    val defaultTextColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val colorPrimaryInt = if (resolvedConfig.textColor != 0) resolvedConfig.textColor else defaultTextColor
    val colorPrimaryCompose = if (resolvedConfig.textColor != 0) {
        Color(resolvedConfig.textColor)
    } else {
        MaterialTheme.colorScheme.primary
    }

    val greeting = if (resolvedConfig.style == "CUSTOM_GREETING" && resolvedConfig.customGreeting.isNotBlank()) {
        resolvedConfig.customGreeting
    } else {
        getGreetingForCurrentHour()
    }

    val hazeModifier = resolveClockHazeModifier(isHazeEnabled, hazeStateRef, hazeOpacity)
    val containerBgColor = resolveClockBgColor(
        isBgEnabled = isBgEnabled,
        isHazeEnabled = isHazeEnabled,
        bgColorInt = resolvedConfig.backgroundColor,
        hazeOpacity = hazeOpacity
    )
    val clickModifier = resolveClockClickModifier(onClick, onLongClick)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .then(hazeModifier)
            .background(containerBgColor)
            .then(clickModifier)
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
private fun resolveClockHazeModifier(isHazeEnabled: Boolean, hazeStateRef: HazeState?, hazeOpacity: Float): Modifier {
    if (!isHazeEnabled || hazeStateRef == null) return Modifier
    return Modifier.hazeEffect(
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
private fun resolveClockClickModifier(onClick: (() -> Unit)?, onLongClick: (() -> Unit)?): Modifier {
    if (onClick == null && onLongClick == null) return Modifier
    return Modifier.combinedClickable(
        onClick = { onClick?.invoke() },
        onLongClick = onLongClick
    )
}

private fun getGreetingForCurrentHour(): String {
    val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: DEFAULT_NOON_HOUR
    return when (hour) {
        in MORNING_HOUR_START..MORNING_HOUR_END -> "Bom dia"
        in AFTERNOON_HOUR_START..AFTERNOON_HOUR_END -> "Boa tarde"
        else -> "Boa noite"
    }
}

@Composable
private fun ClockHeaderContent(params: ClockHeaderContentParams) {
    val (horizontalAlignment, textAlign) = ClockHeaderUtils.resolveClockAlignments(params.alignment)

    val showGreeting = when (params.clockStyle) {
        "TIME_ONLY", "DATE_ON_TOP" -> false
        "GREETING_AND_DATE", "CUSTOM_GREETING" -> true
        else -> params.maxHeight >= MIN_GREETING_HEIGHT_DP.dp
    }

    val isCompact = params.maxHeight < COMPACT_CONTAINER_HEIGHT_DP.dp
    val greetingSize = if (isCompact) GREETING_SIZE_COMPACT else GREETING_SIZE_NORMAL
    val dateSize = if (isCompact) DATE_SIZE_COMPACT else DATE_SIZE_NORMAL

    val clockTextSizePx = when {
        params.maxHeight < COMPACT_CONTAINER_HEIGHT_DP.dp -> CLOCK_TEXT_SIZE_SMALL_PX
        params.maxHeight > MAX_CONTAINER_HEIGHT_DP.dp -> CLOCK_TEXT_SIZE_LARGE_PX
        else -> CLOCK_TEXT_SIZE_NORMAL_PX
    }

    val typeface = ClockHeaderUtils.resolveClockTypeface(params.fontFamily)
    val (format12, format24) = ClockHeaderUtils.resolveClockTimeFormats(params.timeFormat)
    val datePattern = ClockHeaderUtils.resolveClockDatePattern(params.dateFormat)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        if (params.clockStyle == "DATE_ON_TOP") {
            ClockDateDisplay(
                datePattern = datePattern,
                dateSizeSp = dateSize,
                textColor = params.composeTextColor,
                textAlign = textAlign
            )
        }

        if (showGreeting) {
            ClockGreetingDisplay(
                greeting = params.greeting,
                greetingSizeSp = greetingSize,
                textColor = params.composeTextColor,
                textAlign = textAlign
            )
        }

        ClockTimeDisplay(
            params = ClockTimeDisplayParams(
                colorPrimary = params.colorPrimary,
                textSizePx = clockTextSizePx,
                format12 = format12,
                format24 = format24,
                typeface = typeface,
                alignment = params.alignment
            )
        )

        if (params.clockStyle != "TIME_ONLY" && params.clockStyle != "DATE_ON_TOP") {
            ClockDateDisplay(
                datePattern = datePattern,
                dateSizeSp = dateSize,
                textColor = params.composeTextColor,
                textAlign = textAlign
            )
        }
    }
}

@Composable
private fun ClockGreetingDisplay(
    greeting: String,
    greetingSizeSp: Float,
    textColor: Color,
    textAlign: TextAlign
) {
    Crossfade(
        targetState = greeting,
        animationSpec = tween(GREETING_CROSSFADE_DURATION_MS),
        label = "greeting_fade"
    ) { currentGreeting ->
        AureoleText(
            text = currentGreeting,
            fontSize = greetingSizeSp.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = textAlign,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ClockDateDisplay(
    datePattern: String,
    dateSizeSp: Float,
    textColor: Color,
    textAlign: TextAlign
) {
    AndroidView(
        factory = { ctx ->
            TextClock(ctx).apply {
                format12Hour = datePattern
                format24Hour = datePattern
                textSize = dateSizeSp
                setTextColor(textColor.toArgb())
                textAlignment = when (textAlign) {
                    TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                    TextAlign.End, TextAlign.Right -> View.TEXT_ALIGNMENT_TEXT_END
                    else -> View.TEXT_ALIGNMENT_TEXT_START
                }
            }
        },
        update = { clock ->
            clock.format12Hour = datePattern
            clock.format24Hour = datePattern
            clock.textSize = dateSizeSp
            clock.setTextColor(textColor.toArgb())
            clock.textAlignment = when (textAlign) {
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                TextAlign.End, TextAlign.Right -> View.TEXT_ALIGNMENT_TEXT_END
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }
    )
}

@Composable
private fun ClockTimeDisplay(params: ClockTimeDisplayParams) {
    val textClockAlignment = when (params.alignment.uppercase()) {
        "CENTER" -> View.TEXT_ALIGNMENT_CENTER
        "END", "RIGHT" -> View.TEXT_ALIGNMENT_TEXT_END
        else -> View.TEXT_ALIGNMENT_TEXT_START
    }

    AndroidView(
        factory = { ctx ->
            TextClock(ctx).apply {
                format12Hour = params.format12
                format24Hour = params.format24
                textSize = params.textSizePx
                setTextColor(params.colorPrimary)
                typeface = params.typeface
                textAlignment = textClockAlignment
            }
        },
        update = { clock ->
            clock.format12Hour = params.format12
            clock.format24Hour = params.format24
            clock.textSize = params.textSizePx
            clock.setTextColor(params.colorPrimary)
            clock.typeface = params.typeface
            clock.textAlignment = textClockAlignment
        }
    )
}
