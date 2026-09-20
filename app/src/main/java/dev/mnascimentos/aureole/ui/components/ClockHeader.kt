@file:Suppress("MagicNumber", "LongParameterList")

package dev.mnascimentos.aureole.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import java.util.Calendar

private const val CLOCK_TEXT_SIZE = 64f
private const val DATE_TEXT_SIZE = 14f
private const val DATE_ALPHA = 0.85f

@Suppress("LongMethod")
@Composable
fun ClockHeader(
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
    isHazeEnabled: Boolean = false,
    hazeOpacity: Float = 0.5f
) {
    val colorPrimary = MaterialTheme.colorScheme.onSurface.toArgb()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Bom dia"
            in 12..17 -> "Boa tarde"
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .then(hazeModifier)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                    alpha = if (isHazeEnabled) (hazeOpacity * 0.8f).coerceIn(0.25f, 0.95f) else 0.35f
                )
            )
            .padding(20.dp)
    ) {
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

        Spacer(modifier = Modifier.height(4.dp))

        // Relógio grande
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

        Spacer(modifier = Modifier.height(4.dp))

        // Data formatada
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
}
