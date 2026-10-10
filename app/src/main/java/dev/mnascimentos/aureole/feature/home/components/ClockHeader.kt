package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import android.view.View
import android.widget.TextClock
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
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
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MIN_GREETING_HEIGHT_DP = 140.dp
private val COMPACT_CONTAINER_HEIGHT_DP = 100.dp
private val MAX_CONTAINER_HEIGHT_DP = 220.dp
private const val CLOCK_TEXT_SIZE_SMALL_PX = 32f
private const val CLOCK_TEXT_SIZE_NORMAL_PX = 42f
private const val CLOCK_TEXT_SIZE_LARGE_PX = 56f
private const val DEFAULT_HAZE_OPACITY = 0.5f

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
    val hazeOpacity: Float = DEFAULT_HAZE_OPACITY,
    val isBackgroundEnabled: Boolean = true,
)

data class ClockHeaderConfig(
    val style: String? = null,
    val customGreeting: String? = null,
    val alignment: String? = null,
    val fontFamily: String? = null,
    val timeFormat: String? = null,
    val dateFormat: String? = null,
    val textColor: Int? = null,
    val backgroundColor: Int? = null,
)

private data class ClockHeaderContentParams(
    val maxHeight: Dp,
    val maxWidth: Dp,
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

@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeConfig: ClockHazeConfig = ClockHazeConfig(),
    config: ClockHeaderConfig = ClockHeaderConfig(),
    onLongClick: (() -> Unit)? = null
) {
    val uiState = LocalHomeUiState.current
    val hazeStateRef = hazeConfig.hazeState ?: LocalHazeState.current
    val actualIsHazeEnabled = hazeConfig.isHazeEnabled || uiState.isHazeEnabled
    val actualHazeOpacity = if (hazeConfig.isHazeEnabled) hazeConfig.hazeOpacity else uiState.hazeOpacity
    val actualIsBgEnabled = hazeConfig.isBackgroundEnabled && uiState.isClockBackgroundEnabled

    val actualClockStyle = config.style ?: uiState.clockStyle
    val actualCustomGreeting = config.customGreeting ?: uiState.clockCustomGreeting
    val actualAlignment = config.alignment ?: uiState.clockAlignment
    val actualFontFamily = config.fontFamily ?: uiState.clockFontFamily
    val actualTimeFormat = config.timeFormat ?: uiState.clockTimeFormat
    val actualDateFormat = config.dateFormat ?: uiState.clockDateFormat
    val actualTextColor = config.textColor ?: uiState.clockTextColor
    val actualBgColor = config.backgroundColor ?: uiState.clockBackgroundColor

    val defaultTextColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val defaultComposeTextColor = MaterialTheme.colorScheme.primary
    val colorPrimaryInt = if (actualTextColor != 0) actualTextColor else defaultTextColor
    val colorPrimaryCompose = if (actualTextColor != 0) Color(actualTextColor) else defaultComposeTextColor

    val greeting = when {
        actualClockStyle == "CUSTOM_GREETING" && actualCustomGreeting.isNotBlank() -> actualCustomGreeting
        else -> getGreetingForCurrentHour()
    }

    val hazeModifier = if (actualIsHazeEnabled && hazeStateRef != null) {
        Modifier.hazeEffect(
            state = hazeStateRef,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = actualHazeOpacity))
            )
        ) {
            blurEnabled = actualIsHazeEnabled
        }
    } else {
        Modifier
    }

    val baseBgColor = if (actualBgColor != 0) {
        Color(actualBgColor)
    } else {
        AureoleDS.colors.surface
    }

    val containerBgColor = when {
        !actualIsBgEnabled -> Color.Transparent
        actualIsHazeEnabled -> baseBgColor.copy(alpha = actualHazeOpacity)
        else -> baseBgColor
    }

    val clickModifier = if (onLongClick != null) {
        Modifier.clickable { onLongClick() }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .then(hazeModifier)
            .background(containerBgColor)
            .then(clickModifier)
            .padding(
                horizontal = AureoleDS.dimens.medium,
                vertical = AureoleDS.dimens.medium
            )
    ) {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                maxHeight = maxHeight,
                maxWidth = maxWidth,
                colorPrimary = colorPrimaryInt,
                composeTextColor = colorPrimaryCompose,
                greeting = greeting,
                clockStyle = actualClockStyle,
                alignment = actualAlignment,
                fontFamily = actualFontFamily,
                timeFormat = actualTimeFormat,
                dateFormat = actualDateFormat
            )
        )
    }
}

private fun getGreetingForCurrentHour(): String {
    val hour = SimpleDateFormat("H", Locale.getDefault()).format(Date()).toIntOrNull() ?: DEFAULT_NOON_HOUR
    return when (hour) {
        in MORNING_HOUR_START..MORNING_HOUR_END -> "Bom dia"
        in AFTERNOON_HOUR_START..AFTERNOON_HOUR_END -> "Boa tarde"
        else -> "Boa noite"
    }
}

private fun resolveClockTimeFormats(timeFormat: String): Pair<String, String> {
    return when (timeFormat.uppercase()) {
        "12H" -> Pair("hh:mm", "hh:mm")
        "24H" -> Pair("HH:mm", "HH:mm")
        else -> Pair("hh:mm", "HH:mm")
    }
}

private fun resolveClockDatePattern(dateFormat: String): String {
    return when (dateFormat.uppercase()) {
        "SHORT" -> "EEE, d MMM"
        "NUMERIC" -> "dd/MM/yyyy"
        "ISO" -> "yyyy-MM-dd"
        else -> "EEEE, d 'de' MMMM"
    }
}

private fun resolveClockAlignments(alignment: String): Pair<Alignment.Horizontal, TextAlign> {
    return when (alignment.uppercase()) {
        "CENTER" -> Pair(Alignment.CenterHorizontally, TextAlign.Center)
        "END", "RIGHT" -> Pair(Alignment.End, TextAlign.End)
        else -> Pair(Alignment.Start, TextAlign.Start)
    }
}

private fun resolveClockTypeface(fontFamily: String): Typeface {
    return when (fontFamily.uppercase()) {
        "BOLD" -> Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        "SERIF" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
        "MONOSPACE" -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        "ROUNDED" -> Typeface.create("sans-serif-rounded", Typeface.BOLD)
        else -> Typeface.create("sans-serif-medium", Typeface.BOLD)
    }
}

@Composable
private fun ClockHeaderContent(params: ClockHeaderContentParams) {
    val (horizontalAlignment, textAlign) = resolveClockAlignments(params.alignment)

    val showGreeting = when (params.clockStyle) {
        "TIME_ONLY", "DATE_ON_TOP" -> false
        "GREETING_AND_DATE", "CUSTOM_GREETING" -> true
        else -> params.maxHeight >= MIN_GREETING_HEIGHT_DP
    }

    val isCompact = params.maxHeight < COMPACT_CONTAINER_HEIGHT_DP
    val greetingSize = if (isCompact) GREETING_SIZE_COMPACT else GREETING_SIZE_NORMAL
    val dateSize = if (isCompact) DATE_SIZE_COMPACT else DATE_SIZE_NORMAL

    val clockTextSizePx = when {
        params.maxHeight < COMPACT_CONTAINER_HEIGHT_DP -> CLOCK_TEXT_SIZE_SMALL_PX
        params.maxHeight > MAX_CONTAINER_HEIGHT_DP -> CLOCK_TEXT_SIZE_LARGE_PX
        else -> CLOCK_TEXT_SIZE_NORMAL_PX
    }

    val typeface = resolveClockTypeface(params.fontFamily)
    val (format12, format24) = resolveClockTimeFormats(params.timeFormat)
    val datePattern = resolveClockDatePattern(params.dateFormat)

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
            Crossfade(
                targetState = params.greeting,
                animationSpec = tween(GREETING_CROSSFADE_DURATION_MS),
                label = "greeting_fade"
            ) { currentGreeting ->
                AureoleText(
                    text = currentGreeting,
                    fontSize = greetingSize.sp,
                    fontWeight = FontWeight.Medium,
                    color = params.composeTextColor,
                    textAlign = textAlign,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
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
private fun ClockDateDisplay(
    datePattern: String,
    dateSizeSp: Float,
    textColor: Color,
    textAlign: TextAlign
) {
    val dateText = remember(datePattern) {
        try {
            SimpleDateFormat(datePattern, Locale.getDefault()).format(Date())
        } catch (_: Exception) {
            SimpleDateFormat("EEEE, d 'de' MMMM", Locale.getDefault()).format(Date())
        }
    }

    AureoleText(
        text = dateText,
        fontSize = dateSizeSp.sp,
        fontWeight = FontWeight.SemiBold,
        color = textColor,
        textAlign = textAlign
    )
}

@Composable
private fun ClockTimeDisplay(params: ClockTimeDisplayParams) {
    val textAlignmentView = when (params.alignment.uppercase()) {
        "CENTER" -> View.TEXT_ALIGNMENT_CENTER
        "END", "RIGHT" -> View.TEXT_ALIGNMENT_TEXT_END
        else -> View.TEXT_ALIGNMENT_TEXT_START
    }

    if (LocalInspectionMode.current) {
        val sampleTime = remember(params.format24) {
            try {
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            } catch (_: Exception) {
                "12:00"
            }
        }
        AureoleText(
            text = sampleTime,
            fontSize = (params.textSizePx / 2.5f).sp,
            fontWeight = FontWeight.Bold,
            color = Color(params.colorPrimary)
        )
    } else {
        AndroidView(
            factory = { context ->
                TextClock(context).apply {
                    format12Hour = params.format12
                    format24Hour = params.format24
                    textSize = params.textSizePx
                    setTextColor(params.colorPrimary)
                    this.typeface = params.typeface
                    this.textAlignment = textAlignmentView
                }
            },
            update = { clockView ->
                clockView.format12Hour = params.format12
                clockView.format24Hour = params.format24
                clockView.textSize = params.textSizePx
                clockView.setTextColor(params.colorPrimary)
                clockView.typeface = params.typeface
                clockView.textAlignment = textAlignmentView
            }
        )
    }
}

@AureolePreview
@Composable
fun ClockHeaderPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ClockHeader()
        }
    }
}
