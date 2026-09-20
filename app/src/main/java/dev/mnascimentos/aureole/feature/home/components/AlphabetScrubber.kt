package dev.mnascimentos.aureole.feature.home.components

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview

@Composable
fun AlphabetScrubber(
    alphabet: List<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (alphabet.isEmpty() || isLandscape) return

    var totalHeightPx by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(32.dp)
            .padding(vertical = 16.dp)
            .onGloballyPositioned { coordinates ->
                totalHeightPx = coordinates.size.height.toFloat()
            }
            .pointerInput(alphabet) {
                detectVerticalDragGestures { change, _ ->
                    if (totalHeightPx > 0f) {
                        val itemHeight = totalHeightPx / alphabet.size
                        val index = (change.position.y / itemHeight)
                            .toInt()
                            .coerceIn(0, alphabet.lastIndex)
                        onLetterSelected(alphabet[index])
                    }
                }
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { char ->
            Text(
                text = char.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}

@AureolePreview
@Composable
fun AlphabetScrubberPreview() {
    AureoleLauncherTheme {
        AlphabetScrubber(
            alphabet = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
            onLetterSelected = {}
        )
    }
}

