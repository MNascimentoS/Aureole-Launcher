package dev.mnascimentos.aureole.feature.home.components

import android.content.res.Configuration
import android.graphics.Typeface
import android.widget.TextClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import java.util.Calendar

private const val CLOCK_TEXT_SIZE = 64f
private const val DATE_TEXT_SIZE = 14f
private const val DATE_ALPHA = 0.85f

private const val MORNING_START_HOUR = 5
private const val MORNING_END_HOUR = 11
private const val AFTERNOON_START_HOUR = 12
private const val AFTERNOON_END_HOUR = 17

private const val DEFAULT_HAZE_OPACITY = 0.5f
private const val HAZE_ALPHA_MULTIPLIER = 0.8f
private const val HAZE_MIN_ALPHA = 0.25f
private const val HAZE_MAX_ALPHA = 0.95f
private const val NON_HAZE_SURFACE_ALPHA = 0.35f

@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = DEFAULT_HAZE_OPACITY
) {
    val colorPrimary = MaterialTheme.colorScheme.onSurface.toArgb()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in MORNING_START_HOUR..MORNING_END_HOUR -> "Bom dia"
            in AFTERNOON_START_HOUR..AFTERNOON_END_HOUR -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    val hazeModifier = if (isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = isHazeEnabled
        }
    } else {
        Modifier
    }

    val backgroundAlpha = if (isHazeEnabled) {
        (hazeOpacity * HAZE_ALPHA_MULTIPLIER).coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
    } else {
        NON_HAZE_SURFACE_ALPHA
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = backgroundAlpha))
            .padding(20.dp)
    ) {
        ClockGreeting(greeting = greeting)
        Spacer(modifier = Modifier.height(4.dp))
        ClockTimeDisplay(colorPrimary = colorPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        ClockDateDisplay(colorPrimary = colorPrimary)
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
private fun ClockTimeDisplay(colorPrimary: Int) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = "hh:mm"
                format24Hour = "HH:mm"
                textSize = CLOCK_TEXT_SIZE
                setTextColor(colorPrimary)
                typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
                includeFontPadding = false
            }
        },
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun ClockDateDisplay(colorPrimary: Int) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = "EEEE, d 'de' MMMM"
                format24Hour = "EEEE, d 'de' MMMM"
                textSize = DATE_TEXT_SIZE
                setTextColor(colorPrimary)
                alpha = DATE_ALPHA
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
            }
        }
    )
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ClockHeaderPreview() {
    AureoleLauncherTheme {
        ClockHeader()
    }
}

