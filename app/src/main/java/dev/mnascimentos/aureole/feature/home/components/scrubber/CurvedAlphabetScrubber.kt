package dev.mnascimentos.aureole.feature.home.components.scrubber

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Density
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.scrubber.extensions.scrubberBand
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.LetterReporter
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.LetterSelectionTracker
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberRenderParams
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterIndexAt
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState

private const val SCRUBBER_ANIMATION_DURATION = 150
private const val HINT_ALPHA = 0.35f
private const val PILL_ALPHA = 0.6f

@Composable
fun CurvedAlphabetScrubber(
    alphabet: List<Char>,
    callbacks: ScrubberCallbacks,
    modifier: Modifier = Modifier,
    options: ScrubberOptions = ScrubberOptions(),
) {
    val uiState = LocalHomeUiState.current
    if (uiState.isAlphabetScrubberDisabled) return

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (alphabet.isEmpty() || isLandscape) return

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
    val alignment = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd

    var internalTouchYPx by remember { mutableFloatStateOf(-1f) }
    var isInternalDragging by remember { mutableStateOf(false) }
    var lettersHeightPx by remember { mutableFloatStateOf(0f) }
    // The home hands the finger over through externalTouchY; it is read in the layout below, so the very first
    // layout of the scrubber already selects the letter, without waiting for another move (RF-20).
    val isExternalDragging = options.externalTouchY >= 0f
    val isDragging = isInternalDragging || isExternalDragging
    val reportLetter = rememberLetterReporter(options.isProminentSelection, callbacks)
    ExternalTouchEndEffect(isExternalDragging = isExternalDragging, callbacks = callbacks, onEnd = reportLetter.reset)

    val scrubberAlpha by animateFloatAsState(
        targetValue = targetScrubberAlpha(isDragging, options),
        animationSpec = tween(durationMillis = SCRUBBER_ANIMATION_DURATION),
        label = "scrubberAlpha"
    )

    // The scrubber's own drag reports at once from the pointer, so even a flick shorter than a frame selects.
    fun updateInternalTouch(y: Float) {
        internalTouchYPx = y
        reportLetter.report(alphabet.getOrNull(letterIndexAt(y, lettersHeightPx, alphabet.size)))
    }

    val gestureModifier = Modifier.buildGestureModifier(
        isGestureEnabled = options.isGestureEnabled,
        alphabet = alphabet,
        onDragStart = { offset ->
            isInternalDragging = true
            callbacks.onInteractionStarted()
            updateInternalTouch(offset.y)
        },
        onDrag = { positionY -> updateInternalTouch(positionY) },
        onDragEnd = {
            isInternalDragging = false
            reportLetter.reset()
            callbacks.onInteractionEnded()
        }
    )

    val dimens = AureoleDS.dimens
    val pillColor = AureoleDS.colors.surfaceVariant.copy(alpha = PILL_ALPHA)
    val baseModifier = modifier.scrubberBaseModifier(uiState.isLeftHandedMode, options, dimens, pillColor) { height ->
        lettersHeightPx = height
    }

    ScrubberMeasuredContent(
        alphabet = alphabet,
        touchYPx = if (isExternalDragging) options.externalTouchY else internalTouchYPx,
        renderBase = ScrubberRenderParams(
            touchYPx = -1f,
            isDragging = isDragging,
            selectedIndex = -1,
            scrubberAlpha = scrubberAlpha,
            alignment = alignment,
            isProminentSelection = options.isProminentSelection
        ),
        onLetterUnderFinger = reportLetter.report,
        modifier = baseModifier.then(gestureModifier)
    )
}

/** Reads the letters height from the constraints, so the first layout already resolves the letter under the finger. */
@Composable
private fun ScrubberMeasuredContent(
    alphabet: List<Char>,
    touchYPx: Float,
    renderBase: ScrubberRenderParams,
    onLetterUnderFinger: (Char?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val isDragging = renderBase.isDragging
    BoxWithConstraints(modifier = modifier) {
        val heightPx = if (constraints.hasBoundedHeight) constraints.maxHeight.toFloat() else 0f
        if (heightPx > 0f) {
            val clampedTouchY = if (isDragging) touchYPx.coerceIn(0f, heightPx) else -1f
            val selectedIndex = if (isDragging) letterIndexAt(clampedTouchY, heightPx, alphabet.size) else -1
            val selectedChar = alphabet.getOrNull(selectedIndex)
            LaunchedEffect(selectedChar) { onLetterUnderFinger(selectedChar) }
            ScrubberContent(
                alphabet = alphabet,
                containerHeightPx = heightPx,
                density = density,
                params = renderBase.copy(touchYPx = clampedTouchY, selectedIndex = selectedIndex)
            )
        }
    }
}

/**
 * Reports each new letter under the finger once, with a light tick for the drawer highlight. The tick goes through
 * the view haptics, which follow the system touch feedback setting; the app has no haptics preference of its own.
 */
@Composable
private fun rememberLetterReporter(hasTicks: Boolean, callbacks: ScrubberCallbacks): LetterReporter {
    val haptics = LocalHapticFeedback.current
    val currentCallbacks by rememberUpdatedState(callbacks)
    val currentHasTicks by rememberUpdatedState(hasTicks)
    val tracker = remember { LetterSelectionTracker() }
    return remember(tracker, haptics) {
        LetterReporter(
            report = { char ->
                if (tracker.select(char) && char != null) {
                    if (currentHasTicks) haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    currentCallbacks.onLetterSelected(char)
                }
            },
            reset = tracker::reset
        )
    }
}

/** The finger the home handed over has lifted: end the interaction as the scrubber's own drag does. */
@Composable
private fun ExternalTouchEndEffect(isExternalDragging: Boolean, callbacks: ScrubberCallbacks, onEnd: () -> Unit) {
    val currentCallbacks by rememberUpdatedState(callbacks)
    var hadExternalTouch by remember { mutableStateOf(false) }
    LaunchedEffect(isExternalDragging) {
        if (isExternalDragging) {
            hadExternalTouch = true
        } else if (hadExternalTouch) {
            hadExternalTouch = false
            onEnd()
            currentCallbacks.onInteractionEnded()
        }
    }
}

/** Full while used, faint while the onboarding hints at it, hidden otherwise. */
private fun targetScrubberAlpha(isDragging: Boolean, options: ScrubberOptions): Float = when {
    isDragging || options.isAlwaysVisible -> 1f
    options.isHintVisible -> HINT_ALPHA
    else -> 0f
}

/** Size, padding and pill of the scrubber; the theme values come from the caller, read unconditionally. */
private fun Modifier.scrubberBaseModifier(
    isLeftHandedMode: Boolean,
    options: ScrubberOptions,
    dimens: AureoleDimens,
    pillColor: Color,
    onLettersHeightChanged: (Float) -> Unit
): Modifier {
    return this
        .scrubberBand(options.heightFraction, options.bottomPadding)
        .padding(
            start = if (isLeftHandedMode) dimens.xSmall else dimens.zero,
            end = if (isLeftHandedMode) dimens.zero else dimens.xSmall
        )
        .width(dimens.minTouchTarget)
        .then(
            if (options.hasPillBackground) {
                Modifier.drawBehind {
                    val pillWidth = dimens.iconXLargeMedium.toPx()
                    drawRoundRect(
                        color = pillColor,
                        topLeft = Offset((size.width - pillWidth) / 2f, 0f),
                        size = Size(pillWidth, size.height),
                        cornerRadius = CornerRadius(pillWidth / 2f)
                    )
                }
            } else {
                Modifier
            }
        )
        .onSizeChanged { onLettersHeightChanged(it.height.toFloat()) }
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

@Composable
private fun BoxScope.ScrubberContent(
    alphabet: List<Char>,
    containerHeightPx: Float,
    density: Density,
    params: ScrubberRenderParams
) {
    val uiState = LocalHomeUiState.current

    ScrubberAlphabetItems(
        alphabet = alphabet,
        containerHeightPx = containerHeightPx,
        params = params,
        isLeftHandedMode = uiState.isLeftHandedMode
    )

    val selectedChar = alphabet.getOrNull(params.selectedIndex)?.takeIf { params.isDragging }
    if (params.isProminentSelection) {
        ScrubberProminentBadge(
            selectedChar = selectedChar,
            touchYPx = params.touchYPx,
            alignment = params.alignment,
            isLeftHandedMode = uiState.isLeftHandedMode
        )
    } else if (selectedChar != null) {
        val badgeSizePx = with(density) { AureoleDS.dimens.minTouchTarget.toPx() }
        val badgeMarginPx = with(density) { ScrubberDimensions.BadgeMargin.toPx() }
        val badgeYPx = letterCenterY(params.selectedIndex, containerHeightPx, alphabet.size) - (badgeSizePx / 2f)
        val badgeXPx = if (uiState.isLeftHandedMode) badgeMarginPx else -badgeMarginPx - badgeSizePx

        ScrubberSelectedBadge(
            selectedChar = selectedChar,
            badgeYPx = badgeYPx,
            badgeXPx = badgeXPx,
            alignment = params.alignment
        )
    }
}

@AureolePreview
@Composable
private fun CurvedAlphabetScrubberPreview() {
    AureoleLauncherTheme {
        ProvideHomeUiState(MainUiState()) {
            CurvedAlphabetScrubber(
                alphabet = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G'),
                options = ScrubberOptions(isAlwaysVisible = true),
                callbacks = ScrubberCallbacks(onLetterSelected = {})
            )
        }
    }
}
