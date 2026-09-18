package dev.mnascimentos.aureole.ui.components

import android.graphics.Typeface
import android.widget.TextClock
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val CLOCK_TEXT_SIZE = 56f
private const val DATE_TEXT_SIZE = 14f
private const val DATE_ALPHA = 0.8f

@Composable
fun ClockHeader(modifier: Modifier = Modifier) {
    val colorPrimary = MaterialTheme.colorScheme.onBackground.toArgb()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Relógio grande
        AndroidView(
            factory = { context ->
                TextClock(context).apply {
                    format12Hour = "hh:mm"
                    format24Hour = "HH:mm"
                    textSize = CLOCK_TEXT_SIZE
                    setTextColor(colorPrimary)
                    typeface = Typeface.DEFAULT_BOLD
                    includeFontPadding = false
                }
            },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Data pequena
        AndroidView(
            factory = { context ->
                TextClock(context).apply {
                    format12Hour = "EEE, MMM d"
                    format24Hour = "EEE, MMM d"
                    textSize = DATE_TEXT_SIZE
                    setTextColor(colorPrimary)
                    alpha = DATE_ALPHA
                    typeface = Typeface.DEFAULT_BOLD
                }
            }
        )
    }
}
