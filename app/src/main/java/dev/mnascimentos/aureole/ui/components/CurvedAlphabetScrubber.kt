package dev.mnascimentos.aureole.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
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

private val SCRUBBER_PADDING_TOP = 64.dp
private val SCRUBBER_PADDING_BOTTOM = 110.dp
private val SCRUBBER_PADDING_HORIZONTAL = 16.dp
private const val GAUSSIAN_HALF_FACTOR = 0.5f

data class ScrubberCallbacks(
    val onLetterSelected: (Char) -> Unit,
    val onInteractionStarted: () -> Unit,
    val onInteractionEnded: () -> Unit
)

@Suppress("LongMethod", "CyclomaticComplexMethod", "LongParameterList")
@Composable
fun CurvedAlphabetScrubber(
    alphabet: List<Char>,
    isAlwaysVisible: Boolean,
    isGestureEnabled: Boolean,
    externalTouchY: Float = -1f,
    callbacks: ScrubberCallbacks,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier,
) {
    if (alphabet.isEmpty()) return

    val density = LocalDensity.current
    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    var touchYPx by remember { mutableFloatStateOf(-1f) }
    var isDragging by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    val amplitudePx = with(density) { MAX_BULGE_AMPLITUDE.toPx() }
    val sigmaPx = with(density) { CURVE_SPREAD.toPx() }
    val badgeSizePx = with(density) { BADGE_SIZE.toPx() }
    val badgeMarginPx = with(density) { BADGE_MARGIN.toPx() }

    val scrubberAlpha by animateFloatAsState(
        targetValue = if (isDragging || isAlwaysVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "scrubberAlpha"
    )

    fun updateTouch(y: Float) {
        if (containerHeightPx <= 0f) return
        touchYPx = y.coerceIn(0f, containerHeightPx)
        val itemHeight = containerHeightPx / alphabet.size
        val newIndex = (touchYPx / itemHeight).toInt().coerceIn(0, alphabet.lastIndex)
        if (newIndex != selectedIndex) {
            selectedIndex = newIndex
            callbacks.onLetterSelected(alphabet[newIndex])
        }
    }

    LaunchedEffect(externalTouchY, containerHeightPx) {
        if (externalTouchY >= 0f && containerHeightPx > 0f) {
            isDragging = true
            updateTouch(externalTouchY)
        } else if (externalTouchY < 0f && isDragging) {
            isDragging = false
        }
    }

    val baseModifier = modifier
        .fillMaxHeight()
        .padding(
            top = SCRUBBER_PADDING_TOP,
            bottom = SCRUBBER_PADDING_BOTTOM,
            start = if (isLeftHandedMode) SCRUBBER_PADDING_HORIZONTAL else 0.dp,
            end = if (isLeftHandedMode) 0.dp else SCRUBBER_PADDING_HORIZONTAL
        )
        .width(SCRUBBER_WIDTH)
        .onGloballyPositioned { coordinates ->
            containerHeightPx = coordinates.size.height.toFloat()
        }

    val gestureModifier = if (isGestureEnabled) {
        Modifier.pointerInput(alphabet) {
            detectDragGestures(
                onDragStart = { offset ->
                    isDragging = true
                    callbacks.onInteractionStarted()
                    updateTouch(offset.y)
                },
                onDrag = { change, _ ->
                    change.consume()
                    updateTouch(change.position.y)
                },
                onDragEnd = {
                    isDragging = false
                    callbacks.onInteractionEnded()
                },
                onDragCancel = {
                    isDragging = false
                    callbacks.onInteractionEnded()
                },
            )
        }
    } else Modifier

    Box(modifier = baseModifier.then(gestureModifier)) {
        if (containerHeightPx > 0f) {
            val itemHeightPx = containerHeightPx / alphabet.size
            val alignment = if (isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd

            alphabet.forEachIndexed { index, char ->
                val itemCenterY = (index + GAUSSIAN_HALF_FACTOR) * itemHeightPx

                val offsetXPx = if (isDragging && touchYPx >= 0f) {
                    val dy = itemCenterY - touchYPx
                    val magnitude = amplitudePx * exp(-(dy * dy) / (2f * sigmaPx * sigmaPx))
                    if (isLeftHandedMode) magnitude else -magnitude
                } else {
                    0f
                }

                val isActive = isDragging && index == selectedIndex

                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                    color = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    },
                    modifier = Modifier
                        .align(alignment)
                        .graphicsLayer {
                            translationX = offsetXPx
                            translationY = itemCenterY - (size.height / 2f)
                            alpha = scrubberAlpha
                        }
                )
            }

            if (isDragging && selectedIndex in alphabet.indices) {
                val selectedChar = alphabet[selectedIndex]
                val badgeYPx = (selectedIndex + GAUSSIAN_HALF_FACTOR) * itemHeightPx - (badgeSizePx / 2f)
                val badgeXPx = if (isLeftHandedMode) badgeMarginPx else -badgeMarginPx - badgeSizePx

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(BADGE_SIZE)
                        .align(alignment)
                        .offset { IntOffset(badgeXPx.roundToInt(), badgeYPx.roundToInt()) }
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = selectedChar.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Convenience overload for backwards compatibility
@Suppress("LongParameterList")
@Composable
fun CurvedAlphabetScrubber(
    alphabet: List<Char>,
    isAlwaysVisible: Boolean,
    isGestureEnabled: Boolean,
    externalTouchY: Float = -1f,
    onLetterSelected: (Char) -> Unit,
    onInteractionStarted: () -> Unit,
    onInteractionEnded: () -> Unit,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier,
) {
    CurvedAlphabetScrubber(
        alphabet = alphabet,
        isAlwaysVisible = isAlwaysVisible,
        isGestureEnabled = isGestureEnabled,
        externalTouchY = externalTouchY,
        callbacks = ScrubberCallbacks(
            onLetterSelected = onLetterSelected,
            onInteractionStarted = onInteractionStarted,
            onInteractionEnded = onInteractionEnded
        ),
        isLeftHandedMode = isLeftHandedMode,
        modifier = modifier
    )
}
