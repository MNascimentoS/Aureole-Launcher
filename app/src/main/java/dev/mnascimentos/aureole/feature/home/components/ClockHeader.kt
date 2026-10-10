package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState

private const val HAZE_ALPHA_MULTIPLIER = 0.8f
private const val HAZE_MIN_ALPHA = 0.25f
private const val HAZE_MAX_ALPHA = 0.95f

@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    config: ClockHeaderConfig = ClockHeaderConfig()
) {
    val uiState = LocalHomeUiState.current
    val hazeStateRef = config.hazeState ?: LocalHazeState.current
    val actualIsHazeEnabled = config.isHazeEnabled || uiState.isHazeEnabled
    val actualHazeOpacity = if (config.isHazeEnabled) config.hazeOpacity else uiState.hazeOpacity
    val actualIsBgEnabled = config.isBackgroundEnabled && uiState.isClockBackgroundEnabled

    val actualClockStyle = config.clockStyle ?: uiState.clockStyle
    val actualCustomGreeting = config.clockCustomGreeting ?: uiState.clockCustomGreeting
    val actualAlignment = config.clockAlignment ?: uiState.clockAlignment
    val actualFontFamily = config.clockFontFamily ?: uiState.clockFontFamily
    val actualTimeFormat = config.clockTimeFormat ?: uiState.clockTimeFormat
    val actualDateFormat = config.clockDateFormat ?: uiState.clockDateFormat
    val actualTextColor = config.clockTextColor ?: uiState.clockTextColor
    val actualBgColor = config.clockBackgroundColor ?: uiState.clockBackgroundColor

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

    val longClickModifier = if (config.onLongClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = { config.onLongClick() })
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .then(longClickModifier)
            .clip(RoundedCornerShape(AureoleDS.dimens.cornerRadius))
            .then(hazeModifier)
            .background(backgroundColor)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        ClockHeaderContent(
            ClockContentConfig(
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

internal fun Modifier.buildClockHazeModifier(
    hasHaze: Boolean,
    hazeState: HazeState?,
    hazeOpacity: Float
): Modifier {
    return if (hasHaze && hazeState != null) {
        val hazeAlpha = (hazeOpacity * HAZE_ALPHA_MULTIPLIER).coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
        this.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                tint = HazeTint(Color.Black.copy(alpha = hazeAlpha)),
                blurRadius = 24.dp
            )
        )
    } else {
        this
    }
}

internal fun getClockBackgroundColor(
    isBgEnabled: Boolean,
    hasHaze: Boolean,
    hazeOpacity: Float
): Color {
    return when {
        hasHaze -> Color.Transparent
        isBgEnabled -> Color.Black.copy(alpha = (hazeOpacity * 0.5f).coerceIn(0.1f, 0.8f))
        else -> Color.Transparent
    }
}

@Composable
private fun ClockHeaderContent(config: ClockContentConfig) {
    val textAlign = when (config.alignment) {
        "LEFT" -> TextAlign.Start
        "RIGHT" -> TextAlign.End
        else -> TextAlign.Center
    }
    val horizontalAlignment = when (config.alignment) {
        "LEFT" -> Alignment.Start
        "RIGHT" -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

    val timeSizePx = calculateTimeTextSize(config.maxHeight, config.maxWidth)
    val typeface = getClockTypeface(config.fontFamily)

    val format12 = when (config.clockStyle) {
        "ANALOG" -> ""
        else -> config.timeFormat.ifBlank { "hh:mm a" }
    }
    val format24 = when (config.clockStyle) {
        "ANALOG" -> ""
        else -> config.timeFormat.ifBlank { "HH:mm" }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.Center
    ) {
        ClockGreeting(
            greeting = config.greeting,
            composeTextColor = config.composeTextColor,
            textAlign = textAlign,
            horizontalAlignment = horizontalAlignment
        )

        if (config.greeting.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
        }

        ClockTimeDisplay(
            ClockTimeConfig(
                colorPrimary = config.colorPrimary,
                textSizePx = timeSizePx,
                format12 = format12,
                format24 = format24,
                typeface = typeface,
                alignment = config.alignment
            )
        )

        ClockDateDisplay(
            colorPrimary = config.colorPrimary,
            maxHeight = config.maxHeight,
            dateFormat = config.dateFormat,
            clockTypeface = typeface,
            alignment = config.alignment
        )
    }
}
