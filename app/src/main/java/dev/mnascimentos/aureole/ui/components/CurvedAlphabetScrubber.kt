package dev.mnascimentos.aureole.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.exp
import kotlin.math.roundToInt

private val MAX_BULGE_AMPLITUDE = 56.dp
private val CURVE_SPREAD = 80.dp
private val SCRUBBER_WIDTH = 48.dp
private val BADGE_SIZE = 48.dp
private val BADGE_MARGIN = 12.dp

private val SCRUBBER_PADDING_TOP = 56.dp
private val SCRUBBER_PADDING_BOTTOM = 56.dp
private val SCRUBBER_PADDING_END = 16.dp

@Composable
fun CurvedAlphabetScrubber(
    alphabet: List<Char>,
    onLetterSelected: (Char) -> Unit,
    onInteractionStarted: () -> Unit,
    onInteractionEnded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (alphabet.isEmpty()) return

    val density = LocalDensity.current
    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    var touchYPx by remember { mutableFloatStateOf(-1f) }
    var isDragging by remember { mutableStateOf(value = false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    val amplitudePx = with(density) { MAX_BULGE_AMPLITUDE.toPx() }
    val sigmaPx = with(density) { CURVE_SPREAD.toPx() }
    val badgeSizePx = with(density) { BADGE_SIZE.toPx() }
    val badgeMarginPx = with(density) { BADGE_MARGIN.toPx() }

    fun updateTouch(y: Float) {
        if (containerHeightPx <= 0f) return
        touchYPx = y.coerceIn(0f, containerHeightPx)
        val itemHeight = containerHeightPx / alphabet.size
        val newIndex = (touchYPx / itemHeight).toInt().coerceIn(0, alphabet.lastIndex)
        if (newIndex != selectedIndex) {
            selectedIndex = newIndex
            onLetterSelected(alphabet[newIndex])
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                top = SCRUBBER_PADDING_TOP,
                bottom = SCRUBBER_PADDING_BOTTOM,
                end = SCRUBBER_PADDING_END,
            )
            .width(SCRUBBER_WIDTH)
            .onGloballyPositioned { coordinates ->
                containerHeightPx = coordinates.size.height.toFloat()
            }
            .pointerInput(alphabet) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        onInteractionStarted()
                        updateTouch(offset.y)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        updateTouch(change.position.y)
                    },
                    onDragEnd = {
                        isDragging = false
                        onInteractionEnded()
                    },
                    onDragCancel = {
                        isDragging = false
                        onInteractionEnded()
                    },
                )
            }
    ) {
        if (containerHeightPx > 0f) {
            val itemHeightPx = containerHeightPx / alphabet.size

            alphabet.forEachIndexed { index, char ->
                val itemCenterY = (index + 0.5f) * itemHeightPx

                val offsetXPx = if (isDragging && (touchYPx >= 0f)) {
                    val dy = itemCenterY - touchYPx
                    -amplitudePx * exp(-(dy * dy) / (2f * sigmaPx * sigmaPx))
                } else {
                    0f
                }

                val isActive = isDragging && (index == selectedIndex)

                Text(
                    text = char.toString(),
                    style = if (isActive) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(
                            alpha = if (isDragging) 0.8f else 0.5f
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            translationX = offsetXPx
                            translationY = itemCenterY - (itemHeightPx / 2f)
                        }
                )
            }

            if (isDragging && selectedIndex in alphabet.indices) {
                val badgeX = -amplitudePx - badgeSizePx - badgeMarginPx
                val badgeY = (touchYPx - badgeSizePx / 2f).coerceIn(
                    0f,
                    containerHeightPx - badgeSizePx
                )

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset { IntOffset(badgeX.roundToInt(), badgeY.roundToInt()) }
                        .size(BADGE_SIZE)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = alphabet[selectedIndex].toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
