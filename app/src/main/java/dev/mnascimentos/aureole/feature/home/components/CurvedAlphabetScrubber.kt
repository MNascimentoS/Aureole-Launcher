package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberRenderParams
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlin.math.exp
import kotlin.math.roundToInt

private val MAX_BULGE_AMPLITUDE = 56.dp
private val CURVE_SPREAD = 80.dp
private val SCRUBBER_WIDTH = 48.dp
private val BADGE_SIZE = 48.dp
private val BADGE_MARGIN = 28.dp

private val SCRUBBER_PADDING_TOP = 64.dp
private val SCRUBBER_PADDING_BOTTOM = 110.dp
private val SCRUBBER_PADDING_HORIZONTAL = 16.dp
private const val GAUSSIAN_HALF_FACTOR = 0.5f

private const val SCRUBBER_ANIMATION_DURATION = 150
private const val INACTIVE_LETTER_ALPHA = 0.6f
private val BADGE_ELEVATION = 6.dp

@Composable
fun CurvedAlphabetScrubber(
    alphabet: List<Char>,
    options: ScrubberOptions = ScrubberOptions(),
    callbacks: ScrubberCallbacks,
    modifier: Modifier = Modifier,
) {
    if (alphabet.isEmpty()) return

    ScrubberContainer(
        alphabet = alphabet,
        options = options,
        callbacks = callbacks,
        modifier = modifier
    )
}

@Composable
private fun ScrubberContainer(
    alphabet: List<Char>,
    options: ScrubberOptions,
    callbacks: ScrubberCallbacks,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val density = LocalDensity.current
    val alignment = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd

    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    var touchYPx by remember { mutableFloatStateOf(-1f) }
    var isDragging by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    val scrubberAlpha by animateFloatAsState(
        targetValue = if (isDragging || options.isAlwaysVisible) 1f else 0f,
        animationSpec = tween(durationMillis = SCRUBBER_ANIMATION_DURATION),
        label = "scrubberAlpha"
    )

    fun updateTouch(y: Float) {
        if (containerHeightPx <= 0f) return
        touchYPx = y.coerceIn(0f, containerHeightPx)
        val newIndex = calculateSelectedIndex(touchYPx, containerHeightPx, alphabet.size)
        if (newIndex != selectedIndex && newIndex in alphabet.indices) {
            selectedIndex = newIndex
            callbacks.onLetterSelected(alphabet[newIndex])
        }
    }

    LaunchedEffect(options.externalTouchY, containerHeightPx) {
        if (options.externalTouchY >= 0f && containerHeightPx > 0f) {
            isDragging = true
            updateTouch(options.externalTouchY)
        } else if (options.externalTouchY < 0f && isDragging) {
            isDragging = false
        }
    }

    val gestureModifier = Modifier.buildGestureModifier(
        isGestureEnabled = options.isGestureEnabled,
        alphabet = alphabet,
        onDragStart = { offset ->
            isDragging = true
            callbacks.onInteractionStarted()
            updateTouch(offset.y)
        },
        onDrag = { positionY -> updateTouch(positionY) },
        onDragEnd = {
            isDragging = false
            callbacks.onInteractionEnded()
        }
    )

    val baseModifier = modifier.scrubberBaseModifier(uiState.isLeftHandedMode) { height ->
        containerHeightPx = height
    }

    Box(modifier = baseModifier.then(gestureModifier)) {
        if (containerHeightPx > 0f) {
            ScrubberContent(
                alphabet = alphabet,
                containerHeightPx = containerHeightPx,
                density = density,
                params = ScrubberRenderParams(
                    touchYPx = touchYPx,
                    isDragging = isDragging,
                    selectedIndex = selectedIndex,
                    scrubberAlpha = scrubberAlpha,
                    alignment = alignment
                )
            )
        }
    }
}

private fun Modifier.scrubberBaseModifier(
    isLeftHandedMode: Boolean,
    onHeightChanged: (Float) -> Unit
): Modifier = this
    .fillMaxHeight()
    .padding(
        top = SCRUBBER_PADDING_TOP,
        bottom = SCRUBBER_PADDING_BOTTOM,
        start = if (isLeftHandedMode) SCRUBBER_PADDING_HORIZONTAL else 0.dp,
        end = if (isLeftHandedMode) 0.dp else SCRUBBER_PADDING_HORIZONTAL
    )
    .width(SCRUBBER_WIDTH)
    .onGloballyPositioned { coordinates ->
        onHeightChanged(coordinates.size.height.toFloat())
    }

private fun Modifier.buildGestureModifier(
    isGestureEnabled: Boolean,
    alphabet: List<Char>,
    onDragStart: (Offset) -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit
): Modifier {
    if (!isGestureEnabled) return this
    return this.pointerInput(alphabet) {
        detectDragGestures(
            onDragStart = onDragStart,
            onDrag = { change, _ ->
                change.consume()
                onDrag(change.position.y)
            },
            onDragEnd = onDragEnd,
            onDragCancel = onDragEnd,
        )
    }
}

private fun calculateSelectedIndex(touchY: Float, containerHeightPx: Float, itemCount: Int): Int {
    if (containerHeightPx <= 0f || itemCount <= 0) return -1
    val itemHeight = containerHeightPx / itemCount
    return (touchY / itemHeight).toInt().coerceIn(0, itemCount - 1)
}

@Composable
private fun BoxScope.ScrubberContent(
    alphabet: List<Char>,
    containerHeightPx: Float,
    density: Density,
    params: ScrubberRenderParams
) {
    val uiState = LocalHomeUiState.current
    val itemHeightPx = containerHeightPx / alphabet.size

    ScrubberAlphabetItems(
        alphabet = alphabet,
        containerHeightPx = containerHeightPx,
        params = params,
        isLeftHandedMode = uiState.isLeftHandedMode
    )

    if (params.isDragging && params.selectedIndex in alphabet.indices) {
        val badgeSizePx = with(density) { BADGE_SIZE.toPx() }
        val badgeMarginPx = with(density) { BADGE_MARGIN.toPx() }
        val selectedChar = alphabet[params.selectedIndex]
        val badgeYPx = (params.selectedIndex + GAUSSIAN_HALF_FACTOR) * itemHeightPx - (badgeSizePx / 2f)
        val badgeXPx = if (uiState.isLeftHandedMode) badgeMarginPx else -badgeMarginPx - badgeSizePx

        ScrubberSelectedBadge(
            selectedChar = selectedChar,
            badgeYPx = badgeYPx,
            badgeXPx = badgeXPx,
            alignment = params.alignment
        )
    }
}

@Composable
private fun BoxScope.ScrubberAlphabetItems(
    alphabet: List<Char>,
    containerHeightPx: Float,
    params: ScrubberRenderParams,
    isLeftHandedMode: Boolean
) {
    val density = LocalDensity.current
    val amplitudePx = with(density) { MAX_BULGE_AMPLITUDE.toPx() }
    val sigmaPx = with(density) { CURVE_SPREAD.toPx() }
    val itemHeightPx = containerHeightPx / alphabet.size

    alphabet.forEachIndexed { index, char ->
        val itemCenterY = (index + GAUSSIAN_HALF_FACTOR) * itemHeightPx

        val offsetXPx = if (params.isDragging && params.touchYPx >= 0f) {
            val dy = itemCenterY - params.touchYPx
            val magnitude = amplitudePx * exp(-(dy * dy) / (2f * sigmaPx * sigmaPx))
            if (isLeftHandedMode) magnitude else -magnitude
        } else {
            0f
        }

        val isActive = params.isDragging && index == params.selectedIndex

        Text(
            text = char.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground.copy(alpha = INACTIVE_LETTER_ALPHA)
            },
            modifier = Modifier
                .align(params.alignment)
                .graphicsLayer {
                    translationX = offsetXPx
                    translationY = itemCenterY - (size.height / 2f)
                    alpha = params.scrubberAlpha
                }
        )
    }
}

@Composable
private fun BoxScope.ScrubberSelectedBadge(
    selectedChar: Char,
    badgeYPx: Float,
    badgeXPx: Float,
    alignment: Alignment
) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shadowElevation = BADGE_ELEVATION,
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

@AureolePreview
@Composable
fun CurvedAlphabetScrubberPreview() {
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            CurvedAlphabetScrubber(
                alphabet = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
                options = ScrubberOptions(isAlwaysVisible = true),
                callbacks = ScrubberCallbacks(onLetterSelected = {})
            )
        }
    }
}

