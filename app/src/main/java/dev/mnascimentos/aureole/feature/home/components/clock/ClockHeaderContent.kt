package dev.mnascimentos.aureole.feature.home.components.clock

import android.view.View
import android.widget.TextClock
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTimeDisplayParams

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

private const val GREETING_CROSSFADE_DURATION_MS = 500

@Composable
internal fun ClockHeaderContent(params: ClockHeaderContentParams) {
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
            color = textColor,
            format = AureoleTextFormatParams(
                fontSize = greetingSizeSp.sp,
                fontWeight = FontWeight.Medium,
                textAlign = textAlign,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

@AureolePreview
@Composable
private fun ClockHeaderContentPreview() {
    AureoleLauncherTheme {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                maxHeight = 120.dp,
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "Boa tarde",
                clockStyle = "STANDARD",
                alignment = "CENTER",
                fontFamily = "DEFAULT",
                timeFormat = "12H",
                dateFormat = "DEFAULT"
            )
        )
    }
}

@AureolePreview
@Composable
private fun ClockHeaderContentDateOnTopPreview() {
    AureoleLauncherTheme {
        ClockHeaderContent(
            params = ClockHeaderContentParams(
                maxHeight = 120.dp,
                colorPrimary = android.graphics.Color.WHITE,
                composeTextColor = Color.White,
                greeting = "Bom dia",
                clockStyle = "DATE_ON_TOP",
                alignment = "START",
                fontFamily = "DEFAULT",
                timeFormat = "24H",
                dateFormat = "DEFAULT"
            )
        )
    }
}
