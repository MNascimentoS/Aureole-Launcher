package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import android.widget.TextClock
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.theme.LocalHazeState
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
    isBackgroundEnabled: Boolean = true
) {
    val uiState = LocalHomeUiState.current
    val hazeStateRef = hazeState ?: LocalHazeState.current
    val actualIsHazeEnabled = isHazeEnabled || uiState.isHazeEnabled
    val actualHazeOpacity = if (isHazeEnabled) hazeOpacity else uiState.hazeOpacity
    val actualIsBgEnabled = isBackgroundEnabled && uiState.isClockBackgroundEnabled

    val colorPrimary = MaterialTheme.colorScheme.onSurface.toArgb()
    val greeting = rememberClockGreeting()

    val hasHaze = actualIsBgEnabled && actualIsHazeEnabled && (hazeStateRef != null)

    val hazeModifier = Modifier.buildClockHazeModifier(hasHaze, hazeStateRef, actualHazeOpacity)
    val backgroundColor = getClockBackgroundColor(actualIsBgEnabled, hasHaze, actualHazeOpacity)

    val containerMarginModifier = if (actualIsBgEnabled) {
        Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    } else {
        Modifier.padding(start = 12.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .then(containerMarginModifier)
            .clip(RoundedCornerShape(20.dp))
            .then(if (hasHaze) hazeModifier else Modifier)
            .background(backgroundColor)
            .then(if (actualIsBgEnabled) Modifier.padding(12.dp) else Modifier.padding(6.dp))
    ) {
        ClockHeaderContent(
            maxHeight = maxHeight,
            maxWidth = maxWidth,
            colorPrimary = colorPrimary,
            greeting = greeting
        )
    }
}

@Composable
private fun Modifier.buildClockHazeModifier(
    hasHaze: Boolean,
    hazeState: HazeState?,
    hazeOpacity: Float
): Modifier {
    val surfaceTint = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = hazeOpacity)
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
    return if (isBgEnabled) {
        if (hasHaze) {
            val backgroundAlpha = (hazeOpacity * HAZE_ALPHA_MULTIPLIER)
                .coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = backgroundAlpha)
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f)
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

@Composable
private fun ClockHeaderContent(
    maxHeight: Dp,
    maxWidth: Dp,
    colorPrimary: Int,
    greeting: String
) {
    val showGreeting = maxHeight >= MIN_GREETING_HEIGHT_DP
    val showDate = maxHeight >= MIN_DATE_HEIGHT_DP

    val timeTextSizePx = calculateTimeTextSize(maxHeight, maxWidth)
    val dateTextSizePx = if (maxHeight < THRESHOLD_DATE_H_DP) DATE_SIZE_SMALL else DATE_SIZE_NORMAL

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        if (showGreeting) {
            ClockGreeting(greeting = greeting)
            Spacer(modifier = Modifier.height(2.dp))
        }

        ClockTimeDisplay(colorPrimary = colorPrimary, textSizePx = timeTextSizePx)

        if (showDate) {
            Spacer(modifier = Modifier.height(2.dp))
            ClockDateDisplay(colorPrimary = colorPrimary, textSizePx = dateTextSizePx)
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
private fun ClockGreeting(greeting: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ClockTimeDisplay(colorPrimary: Int, textSizePx: Float) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = "hh:mm"
                format24Hour = "HH:mm"
                textSize = textSizePx
                setTextColor(colorPrimary)
                typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
                includeFontPadding = false
            }
        },
        update = { view ->
            if (view is TextClock) {
                view.textSize = textSizePx
                view.setTextColor(colorPrimary)
            }
        },
        modifier = Modifier.padding(vertical = 1.dp)
    )
}

@Composable
private fun ClockDateDisplay(colorPrimary: Int, textSizePx: Float) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = "EEEE, d 'de' MMMM"
                format24Hour = "EEEE, d 'de' MMMM"
                textSize = textSizePx
                setTextColor(colorPrimary)
                alpha = DATE_ALPHA
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
        },
        update = { view ->
            if (view is TextClock) {
                view.textSize = textSizePx
                view.setTextColor(colorPrimary)
            }
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
