package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import android.view.View
import android.widget.TextClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import java.util.Calendar

private const val DATE_ALPHA = 0.85f

private const val MORNING_START_HOUR = 5
private const val MORNING_END_HOUR = 11
private const val AFTERNOON_START_HOUR = 12
private const val AFTERNOON_END_HOUR = 17

private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val HAZE_ALPHA_MULTIPLIER = 0.8f
private const val HAZE_MIN_ALPHA = 0.25f
private const val HAZE_MAX_ALPHA = 0.95f

private val MIN_GREETING_HEIGHT_DP = 70.dp
private val MIN_DATE_HEIGHT_DP = 95.dp
private val THRESHOLD_H1_DP = 65.dp
private val THRESHOLD_H2_DP = 90.dp
private val THRESHOLD_H3_DP = 130.dp
private val THRESHOLD_W1_DP = 140.dp
private val THRESHOLD_W2_DP = 200.dp
private val THRESHOLD_DATE_H_DP = 110.dp

private const val TIME_SIZE_SMALL = 28f
private const val TIME_SIZE_MEDIUM = 38f
private const val TIME_SIZE_LARGE = 48f
private const val TIME_SIZE_XLARGE = 60f
private const val DATE_SIZE_SMALL = 11f
private const val DATE_SIZE_NORMAL = 13f

@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = DEFAULT_HAZE_OPACITY,
    isBackgroundEnabled: Boolean = true,
    clockStyle: String? = null,
    clockCustomGreeting: String? = null,
    clockAlignment: String? = null,
    clockFontFamily: String? = null,
    clockTimeFormat: String? = null,
    clockDateFormat: String? = null,
    clockTextColor: Int? = null,
    clockBackgroundColor: Int? = null,
    onLongClick: (() -> Unit)? = null
) {
    val uiState = LocalHomeUiState.current
    val hazeStateRef = hazeState ?: LocalHazeState.current
    val actualIsHazeEnabled = isHazeEnabled || uiState.isHazeEnabled
    val actualHazeOpacity = if (isHazeEnabled) hazeOpacity else uiState.hazeOpacity
    val actualIsBgEnabled = isBackgroundEnabled && uiState.isClockBackgroundEnabled

    val actualClockStyle = clockStyle ?: uiState.clockStyle
    val actualCustomGreeting = clockCustomGreeting ?: uiState.clockCustomGreeting
    val actualAlignment = clockAlignment ?: uiState.clockAlignment
    val actualFontFamily = clockFontFamily ?: uiState.clockFontFamily
    val actualTimeFormat = clockTimeFormat ?: uiState.clockTimeFormat
    val actualDateFormat = clockDateFormat ?: uiState.clockDateFormat
    val actualTextColor = clockTextColor ?: uiState.clockTextColor
    val actualBgColor = clockBackgroundColor ?: uiState.clockBackgroundColor

    val defaultTextColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val defaultComposeTextColor = MaterialTheme.colorScheme.primary
    val colorPrimaryInt = if (actualTextColor != 0) actualTextColor else defaultTextColor
    val colorPrimaryCompose = if (actualTextColor != 0) Color(actualTextColor) else defaultComposeTextColor

    val greeting = when {
        actualClockStyle == "CUSTOM_GREETING" && actualCustomGreeting.isNotBlank() -> actualCustomGreeting
        else -> rememberClockGreeting()
    }

    val hasHaze = actualIsBgEnabled && actualIsHazeEnabled && (hazeStateRef != null) && (actualBgColor == 0)

    val hazeModifier = Modifier.buildClockHazeModifier(hasHaze, hazeStateRef, actualHazeOpacity)
    val backgroundColor = if (actualBgColor != 0) {
        Color(actualBgColor)
    } else {
        getClockBackgroundColor(actualIsBgEnabled, hasHaze, actualHazeOpacity)
    }

    val containerMarginModifier = if (actualIsBgEnabled) {
        Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    } else {
        Modifier.padding(start = 12.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
    }

    val longClickModifier = if (onLongClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongClick() })
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .then(containerMarginModifier)
            .clip(RoundedCornerShape(20.dp))
            .then(if (hasHaze) hazeModifier else Modifier)
            .background(backgroundColor)
            .then(longClickModifier)
            .then(if (actualIsBgEnabled) Modifier.padding(12.dp) else Modifier.padding(6.dp))
    ) {
        ClockHeaderContent(
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
    }
}

@Composable
private fun Modifier.buildClockHazeModifier(
    hasHaze: Boolean,
    hazeState: HazeState?,
    hazeOpacity: Float
): Modifier {
    val surfaceColor = AureoleDS.colors.surfaceVariant
    val surfaceTint = surfaceColor.copy(alpha = hazeOpacity)
    return if (hasHaze && hazeState != null) {
        this.then(
            Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(blurRadius = 24.dp, tint = HazeTint(surfaceTint))
            ) {
                blurEnabled = true
            }
        )
    } else {
        this
    }
}

@Composable
private fun getClockBackgroundColor(isBgEnabled: Boolean, hasHaze: Boolean, hazeOpacity: Float): Color {
    val surfaceColor = AureoleDS.colors.surfaceVariant
    return if (isBgEnabled) {
        if (hasHaze) {
            val backgroundAlpha = (hazeOpacity * HAZE_ALPHA_MULTIPLIER)
                .coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
            surfaceColor.copy(alpha = backgroundAlpha)
        } else {
            surfaceColor.copy(alpha = 0.95f)
        }
    } else {
        Color.Transparent
    }
}

@Composable
private fun rememberClockGreeting(): String {
    return remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in MORNING_START_HOUR..MORNING_END_HOUR -> "Bom dia"
            in AFTERNOON_START_HOUR..AFTERNOON_END_HOUR -> "Boa tarde"
            else -> "Boa noite"
        }
    }
}

private fun getClockTypeface(fontFamily: String): Typeface {
    return when (fontFamily.uppercase()) {
        "BOLD" -> Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        "SERIF" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
        "MONOSPACE" -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        "ROUNDED" -> Typeface.create("sans-serif-rounded", Typeface.BOLD)
        else -> Typeface.create("sans-serif-medium", Typeface.BOLD)
    }
}

@Composable
private fun ClockHeaderContent(
    maxHeight: Dp,
    maxWidth: Dp,
    colorPrimary: Int,
    composeTextColor: Color,
    greeting: String,
    clockStyle: String,
    alignment: String,
    fontFamily: String,
    timeFormat: String,
    dateFormat: String
) {
    val horizontalAlignment = when (alignment.uppercase()) {
        "CENTER" -> Alignment.CenterHorizontally
        "END", "RIGHT" -> Alignment.End
        else -> Alignment.Start
    }

    val textAlign = when (alignment.uppercase()) {
        "CENTER" -> TextAlign.Center
        "END", "RIGHT" -> TextAlign.End
        else -> TextAlign.Start
    }

    val showGreeting = when (clockStyle) {
        "TIME_ONLY", "DATE_ON_TOP" -> false
        "GREETING_AND_DATE", "CUSTOM_GREETING" -> true
        else -> maxHeight >= MIN_GREETING_HEIGHT_DP
    }

    val showDate = when (clockStyle) {
        "TIME_ONLY" -> false
        "DATE_ON_TOP", "GREETING_AND_DATE" -> true
        else -> maxHeight >= MIN_DATE_HEIGHT_DP
    }

    val timeTextSizePx = calculateTimeTextSize(maxHeight, maxWidth)
    val dateTextSizePx = if (maxHeight < THRESHOLD_DATE_H_DP) DATE_SIZE_SMALL else DATE_SIZE_NORMAL
    val typeface = remember(fontFamily) { getClockTypeface(fontFamily) }

    val (timeFormat12, timeFormat24) = when (timeFormat.uppercase()) {
        "12H" -> Pair("hh:mm", "hh:mm")
        "24H" -> Pair("HH:mm", "HH:mm")
        else -> Pair("hh:mm", "HH:mm")
    }

    val dateFormatPattern = when (dateFormat.uppercase()) {
        "SHORT" -> "EEE, d MMM"
        "MEDIUM" -> "d 'de' MMMM"
        "NUMERIC" -> "dd/MM/yyyy"
        else -> "EEEE, d 'de' MMMM"
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = horizontalAlignment
    ) {
        if (clockStyle == "DATE_ON_TOP" && showDate) {
            ClockDateDisplay(
                colorPrimary = colorPrimary,
                textSizePx = dateTextSizePx,
                dateFormat = dateFormatPattern,
                typeface = typeface,
                alignment = alignment
            )
            Spacer(modifier = Modifier.height(2.dp))
        } else if (showGreeting) {
            ClockGreeting(
                greeting = greeting,
                textColor = composeTextColor,
                textAlign = textAlign,
                alignment = horizontalAlignment
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        ClockTimeDisplay(
            colorPrimary = colorPrimary,
            textSizePx = timeTextSizePx,
            format12 = timeFormat12,
            format24 = timeFormat24,
            typeface = typeface,
            alignment = alignment
        )

        if (clockStyle != "DATE_ON_TOP" && showDate) {
            Spacer(modifier = Modifier.height(2.dp))
            ClockDateDisplay(
                colorPrimary = colorPrimary,
                textSizePx = dateTextSizePx,
                dateFormat = dateFormatPattern,
                typeface = typeface,
                alignment = alignment
            )
        }
    }
}

private fun calculateTimeTextSize(maxHeight: Dp, maxWidth: Dp): Float {
    return when {
        maxHeight < THRESHOLD_H1_DP || maxWidth < THRESHOLD_W1_DP -> TIME_SIZE_SMALL
        maxHeight < THRESHOLD_H2_DP || maxWidth < THRESHOLD_W2_DP -> TIME_SIZE_MEDIUM
        maxHeight < THRESHOLD_H3_DP -> TIME_SIZE_LARGE
        else -> TIME_SIZE_XLARGE
    }
}

@Composable
private fun ClockGreeting(
    greeting: String,
    textColor: Color,
    textAlign: TextAlign,
    alignment: Alignment.Horizontal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = when (alignment) {
            Alignment.CenterHorizontally -> Arrangement.Center
            Alignment.End -> Arrangement.End
            else -> Arrangement.Start
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = greeting,
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            textAlign = textAlign
        )
    }
}

@Composable
private fun ClockTimeDisplay(
    colorPrimary: Int,
    textSizePx: Float,
    format12: String,
    format24: String,
    typeface: Typeface,
    alignment: String
) {
    val textAlignmentView = when (alignment.uppercase()) {
        "CENTER" -> View.TEXT_ALIGNMENT_CENTER
        "END", "RIGHT" -> View.TEXT_ALIGNMENT_TEXT_END
        else -> View.TEXT_ALIGNMENT_TEXT_START
    }

    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = format12
                format24Hour = format24
                textSize = textSizePx
                setTextColor(colorPrimary)
                this.typeface = typeface
                this.textAlignment = textAlignmentView
                includeFontPadding = false
            }
        },
        update = { view ->
            view.format12Hour = format12
            view.format24Hour = format24
            view.textSize = textSizePx
            view.setTextColor(colorPrimary)
            view.typeface = typeface
            view.textAlignment = textAlignmentView
        },
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
private fun ClockDateDisplay(
    colorPrimary: Int,
    textSizePx: Float,
    dateFormat: String,
    typeface: Typeface,
    alignment: String
) {
    val textAlignmentView = when (alignment.uppercase()) {
        "CENTER" -> View.TEXT_ALIGNMENT_CENTER
        "END", "RIGHT" -> View.TEXT_ALIGNMENT_TEXT_END
        else -> View.TEXT_ALIGNMENT_TEXT_START
    }

    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = dateFormat
                format24Hour = dateFormat
                textSize = textSizePx
                setTextColor(colorPrimary)
                alpha = DATE_ALPHA
                this.typeface = typeface
                this.textAlignment = textAlignmentView
            }
        },
        update = { view ->
            view.format12Hour = dateFormat
            view.format24Hour = dateFormat
            view.textSize = textSizePx
            view.setTextColor(colorPrimary)
            view.typeface = typeface
            view.textAlignment = textAlignmentView
        }
    )
}

@AureolePreview
@Composable
fun ClockHeaderPreview() {
    AureoleLauncherTheme {
        ClockHeader()
    }
}
