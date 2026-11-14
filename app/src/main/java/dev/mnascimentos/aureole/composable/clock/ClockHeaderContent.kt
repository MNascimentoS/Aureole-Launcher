package dev.mnascimentos.aureole.composable.clock

import android.graphics.Typeface
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.composable.clock.model.ClockFontSpec
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderContentParams
import dev.mnascimentos.aureole.composable.clock.model.ClockTimeDisplayParams
import dev.mnascimentos.aureole.composable.clock.model.DATE_TRIM_TOP
import dev.mnascimentos.aureole.composable.clock.model.GREETING_TRIM_BOTTOM
import dev.mnascimentos.aureole.composable.clock.model.GREETING_TRIM_TOP
import dev.mnascimentos.aureole.composable.clock.model.TIME_TRIM_BOTTOM
import dev.mnascimentos.aureole.composable.clock.model.TIME_TRIM_TOP
import dev.mnascimentos.aureole.composable.clock.model.resolveClockSizing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val COMPACT_CONTAINER_HEIGHT_DP = 90

private const val GREETING_SIZE_COMPACT = 12f
private const val GREETING_SIZE_NORMAL = 14f
private const val DATE_SIZE_COMPACT = 11f
private const val DATE_SIZE_NORMAL = 13f

private const val GREETING_CROSSFADE_DURATION_MS = 500

@Composable
internal fun ClockHeaderContent(params: ClockHeaderContentParams) {
    val (horizontalAlignment, textAlign) = ClockHeaderUtils.resolveClockAlignments(params.alignment)

    val sizing = resolveClockSizing(
        maxHeightDp = params.maxHeight.value,
        fontScale = LocalDensity.current.fontScale,
        greetingAllowed = params.clockStyle != "TIME_ONLY" && params.clockStyle != "DATE_ON_TOP",
        hasDate = params.clockStyle != "TIME_ONLY"
    )

    val isCompact = params.maxHeight < COMPACT_CONTAINER_HEIGHT_DP.dp
    val greetingSize = if (isCompact) GREETING_SIZE_COMPACT else GREETING_SIZE_NORMAL
    val dateSize = if (isCompact) DATE_SIZE_COMPACT else DATE_SIZE_NORMAL

    val fontSpec = ClockFontSpec.fromKey(params.fontFamily)
    val typeface = fontSpec.timeTypeface()
    val dateTypeface = fontSpec.dateTypeface()
    val (format12, format24) = ClockHeaderUtils.resolveClockTimeFormats(params.timeFormat)
    val datePattern = ClockHeaderUtils.resolveClockDatePattern(params.dateFormat, LocalConfiguration.current.locales[0])

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        if (params.clockStyle == "DATE_ON_TOP") {
            ClockDateDisplay(
                datePattern = datePattern,
                dateSizeSp = dateSize,
                dateTypeface = dateTypeface,
                textColor = params.composeTextColor,
                textAlign = textAlign
            )
        }

        if (sizing.showGreeting) {
            ClockGreetingDisplay(
                greeting = params.greeting,
                greetingSizeSp = greetingSize,
                fontFamily = fontSpec.fontFamily(),
                textColor = params.composeTextColor,
                textAlign = textAlign
            )
        }

        ClockTimeDisplay(
            params = ClockTimeDisplayParams(
                colorPrimary = params.colorPrimary,
                textSizePx = sizing.textSizePx,
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
                dateTypeface = dateTypeface,
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
    fontFamily: FontFamily,
    textColor: Color,
    textAlign: TextAlign
) {
    Crossfade(
        modifier = Modifier.trimVerticalBands(GREETING_TRIM_TOP, GREETING_TRIM_BOTTOM),
        targetState = greeting,
        animationSpec = tween(GREETING_CROSSFADE_DURATION_MS),
        label = "greeting_fade"
    ) { currentGreeting ->
        AureoleText(
            text = currentGreeting,
            color = textColor,
            format = AureoleTextFormatParams(
                fontSize = greetingSizeSp.sp,
                fontFamily = fontFamily,
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
    dateTypeface: Typeface,
    textColor: Color,
    textAlign: TextAlign
) {
    AndroidView(
        modifier = Modifier.trimVerticalBands(DATE_TRIM_TOP, 0f),
        factory = { ctx ->
            TextClock(ctx).apply {
                format12Hour = datePattern
                format24Hour = datePattern
                textSize = dateSizeSp
                typeface = dateTypeface
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
            clock.typeface = dateTypeface
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
        modifier = Modifier.trimVerticalBands(TIME_TRIM_TOP, TIME_TRIM_BOTTOM),
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
