package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import android.view.Gravity
import android.widget.TextClock
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import java.util.Calendar

private const val DATE_ALPHA = 0.85f

private const val MORNING_START_HOUR = 5
private const val MORNING_END_HOUR = 11
private const val AFTERNOON_START_HOUR = 12
private const val AFTERNOON_END_HOUR = 17

private const val TIME_SIZE_SMALL = 28f
private const val TIME_SIZE_MEDIUM = 38f
private const val TIME_SIZE_LARGE = 48f
private const val TIME_SIZE_XLARGE = 60f
private const val DATE_SIZE_SMALL = 11f
private const val DATE_SIZE_NORMAL = 13f

data class ClockHeaderConfig(
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f,
    val isBackgroundEnabled: Boolean = true,
    val clockStyle: String? = null,
    val clockCustomGreeting: String? = null,
    val clockAlignment: String? = null,
    val clockFontFamily: String? = null,
    val clockTimeFormat: String? = null,
    val clockDateFormat: String? = null,
    val clockTextColor: Int? = null,
    val clockBackgroundColor: Int? = null,
    val onLongClick: (() -> Unit)? = null
)

data class ClockContentConfig(
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

data class ClockTimeConfig(
    val colorPrimary: Int,
    val textSizePx: Float,
    val format12: String,
    val format24: String,
    val typeface: Typeface,
    val alignment: String
)

@Composable
internal fun rememberClockGreeting(): String {
    return remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in MORNING_START_HOUR..MORNING_END_HOUR -> "Bom dia"
            in AFTERNOON_START_HOUR..AFTERNOON_END_HOUR -> "Boa tarde"
            else -> "Boa noite"
        }
    }
}

internal fun getClockTypeface(fontFamily: String): Typeface {
    return when (fontFamily) {
        "SERIF" -> Typeface.SERIF
        "MONOSPACE" -> Typeface.MONOSPACE
        "SANS_SERIF" -> Typeface.SANS_SERIF
        else -> Typeface.DEFAULT
    }
}

internal fun calculateTimeTextSize(maxHeight: Dp, maxWidth: Dp): Float {
    return when {
        maxHeight < 65.dp || maxWidth < 140.dp -> TIME_SIZE_SMALL
        maxHeight < 90.dp || maxWidth < 200.dp -> TIME_SIZE_MEDIUM
        maxHeight < 130.dp -> TIME_SIZE_LARGE
        else -> TIME_SIZE_XLARGE
    }
}

@Composable
internal fun ClockGreeting(
    greeting: String,
    composeTextColor: Color,
    textAlign: TextAlign,
    horizontalAlignment: Alignment.Horizontal
) {
    if (greeting.isNotBlank()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = horizontalAlignment
        ) {
            AureoleText(
                text = greeting,
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = composeTextColor,
                textAlign = textAlign
            )
        }
    }
}

@Composable
internal fun ClockTimeDisplay(config: ClockTimeConfig) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                setTextColor(config.colorPrimary)
                textSize = config.textSizePx
                typeface = config.typeface
                format12Hour = config.format12
                format24Hour = config.format24
                gravity = when (config.alignment) {
                    "LEFT" -> Gravity.START
                    "RIGHT" -> Gravity.END
                    else -> Gravity.CENTER
                }
            }
        },
        update = { view ->
            view.setTextColor(config.colorPrimary)
            view.textSize = config.textSizePx
            view.typeface = config.typeface
            view.format12Hour = config.format12
            view.format24Hour = config.format24
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
internal fun ClockDateDisplay(
    colorPrimary: Int,
    maxHeight: Dp,
    dateFormat: String,
    clockTypeface: Typeface,
    alignment: String
) {
    if (maxHeight >= 110.dp) {
        val dateSize = if (maxHeight < 130.dp) DATE_SIZE_SMALL else DATE_SIZE_NORMAL
        AndroidView(
            factory = { context ->
                TextClock(context).apply {
                    setTextColor(colorPrimary)
                    textSize = dateSize
                    this.typeface = clockTypeface
                    format12Hour = dateFormat.ifBlank { "EEE, d 'de' MMMM" }
                    format24Hour = dateFormat.ifBlank { "EEE, d 'de' MMMM" }
                    alpha = DATE_ALPHA
                    gravity = when (alignment) {
                        "LEFT" -> Gravity.START
                        "RIGHT" -> Gravity.END
                        else -> Gravity.CENTER
                    }
                }
            },
            update = { view ->
                view.setTextColor(colorPrimary)
                view.textSize = dateSize
                view.typeface = clockTypeface
                view.format12Hour = dateFormat.ifBlank { "EEE, d 'de' MMMM" }
                view.format24Hour = dateFormat.ifBlank { "EEE, d 'de' MMMM" }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        )
    }
}

@AureolePreview
@Composable
fun ClockSubComponentsPreview() {
    AureoleLauncherTheme {
        ClockHeader(modifier = Modifier.padding(16.dp))
    }
}
