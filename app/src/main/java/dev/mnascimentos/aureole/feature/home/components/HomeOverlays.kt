package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.model.AppsDrawerOverlayConfig
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.ScrubberOverlayConfig
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val EDGE_GESTURE_START_WIDTH_DP = 32
private const val EDGE_INTENT_THRESHOLD_DP = 28
private const val VERTICAL_INTENT_THRESHOLD_DP = 36

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoxScope.HomeOverlaysContent(
    config: HomeOverlaysConfig
) {
    AppsDrawerOverlay(
        config = AppsDrawerOverlayConfig(
            isAllAppsDrawerOpen = config.uiState.isAllAppsDrawerOpen,
            isOpenedFromBottom = config.uiState.isAllAppsOpenedFromBottom,
            isLeftHandedMode = config.uiState.isLeftHandedMode,
            listState = config.listState,
            hazeState = config.hazeState,
            onClose = config.actions.onAllAppsDrawerClose,
        ),
    )

    if (!config.uiState.isAlphabetScrubberDisabled && !WindowInsets.isImeVisible) {
        val scrubberAlign = if (config.uiState.isLeftHandedMode) {
            Alignment.BottomStart
        } else {
            Alignment.BottomEnd
        }

        ScrubberOverlay(
            config = ScrubberOverlayConfig(
                uiState = config.uiState,
                externalTouchY = config.externalTouchY,
                listState = config.listState,
                coroutineScope = config.coroutineScope,
                actions = config.actions,
                onExternalTouchYReset = config.onExternalTouchYReset
            ),
            modifier = Modifier.align(scrubberAlign)
        )
    }
}

@Composable
fun AppsDrawerOverlay(
    config: AppsDrawerOverlayConfig,
) {
    val enterTransition = if (config.isOpenedFromBottom) {
        fadeIn() + slideInVertically { it }
    } else {
        fadeIn() + slideInHorizontally { if (config.isLeftHandedMode) -it / 2 else it / 2 }
    }

    val exitTransition = if (config.isOpenedFromBottom) {
        fadeOut() + slideOutVertically { it }
    } else {
        fadeOut() + slideOutHorizontally { if (config.isLeftHandedMode) -it / 2 else it / 2 }
    }

    AnimatedVisibility(
        visible = config.isAllAppsDrawerOpen,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { config.onClose() })
                },
        ) {
            val drawerAlign =
                if (config.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd

            AppsListDrawer(
                listState = config.listState,
                hazeState = config.hazeState,
                modifier = Modifier.align(drawerAlign),
            )
        }
    }
}

@Composable
fun ScrubberOverlay(
    config: ScrubberOverlayConfig,
    modifier: Modifier = Modifier
) {
    var lastScrolledIndex by remember { mutableIntStateOf(-1) }

    CurvedAlphabetScrubber(
        alphabet = config.uiState.alphabet,
        options = ScrubberOptions(
            isAlwaysVisible = config.uiState.isAllAppsDrawerOpen,
            isGestureEnabled = config.uiState.isAllAppsDrawerOpen,
            externalTouchY = config.externalTouchY
        ),
        callbacks = ScrubberCallbacks(
            onLetterSelected = { letter ->
                if (!config.uiState.isAllAppsDrawerOpen) {
                    config.actions.onAllAppsDrawerOpen()
                }
                config.uiState.letterIndexMap[letter]?.let { targetIndex ->
                    if (targetIndex != lastScrolledIndex) {
                        lastScrolledIndex = targetIndex
                        config.coroutineScope.launch {
                            config.listState.scrollToItem(targetIndex)
                        }
                    }
                }
            },
            onInteractionStarted = {
                if (!config.uiState.isAllAppsDrawerOpen) {
                    config.actions.onAllAppsDrawerOpen()
                }
            },
            onInteractionEnded = {
                lastScrolledIndex = -1
                config.onExternalTouchYReset()
            }
        ),
        modifier = modifier
    )
}

fun Modifier.homeDragGestures(
    params: HomeDragParams
): Modifier = pointerInput(
    params.isLeftHandedMode,
    params.isAlphabetScrubberDisabled,
    params.screenHeightPx,
    params.screenWidthPx
) {
    var dragStartedOnEdge = false
    var hasTriggeredGesture = false
    var totalDragOffset = Offset.Zero

    detectDragGestures(
        onDragStart = { offset ->
            totalDragOffset = Offset.Zero
            hasTriggeredGesture = false
            dragStartedOnEdge = handleEdgeDragStart(offset, params)
        },
        onDragEnd = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
            hasTriggeredGesture = false
        },
        onDragCancel = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
            hasTriggeredGesture = false
        },
        onDrag = { change, dragAmount ->
            totalDragOffset += dragAmount
            val totalDx = totalDragOffset.x
            val totalDy = totalDragOffset.y

            if (dragStartedOnEdge) {
                hasTriggeredGesture = handleEdgeDrag(
                    change = change,
                    totalDx = totalDx,
                    totalDy = totalDy,
                    hasTriggered = hasTriggeredGesture,
                    params = params
                )
            } else {
                hasTriggeredGesture = handleVerticalGesture(
                    change = change,
                    totalDx = totalDx,
                    totalDy = totalDy,
                    hasTriggered = hasTriggeredGesture,
                    params = params
                )
            }
        }
    )
}

private fun handleEdgeDragStart(
    offset: Offset,
    params: HomeDragParams
): Boolean {
    if (params.isAlphabetScrubberDisabled) return false
    val edgeThreshold = with(params.density) { EDGE_GESTURE_START_WIDTH_DP.dp.toPx() }
    return if (params.isLeftHandedMode) {
        offset.x < edgeThreshold
    } else {
        offset.x > params.screenWidthPx - edgeThreshold
    }
}

private fun handleEdgeDrag(
    change: PointerInputChange,
    totalDx: Float,
    totalDy: Float,
    hasTriggered: Boolean,
    params: HomeDragParams
): Boolean {
    var triggered = hasTriggered
    if (!triggered) {
        val inwardDrag = if (params.isLeftHandedMode) totalDx else -totalDx
        val edgeIntentThresholdPx = with(params.density) { EDGE_INTENT_THRESHOLD_DP.dp.toPx() }

        if (inwardDrag > edgeIntentThresholdPx || abs(totalDy) > edgeIntentThresholdPx) {
            triggered = true
            if (!params.isAllAppsDrawerOpen) {
                params.onAllAppsDrawerOpen()
            }
        }
    }

    if (triggered) {
        change.consume()
        val scrubberTopYPx = params.screenHeightPx * (1f / 3f)
        params.onExternalTouchYChange(change.position.y - scrubberTopYPx)
    }
    return triggered
}

private fun handleVerticalGesture(
    change: PointerInputChange,
    totalDx: Float,
    totalDy: Float,
    hasTriggered: Boolean,
    params: HomeDragParams
): Boolean {
    if (params.isAllAppsDrawerOpen || change.isConsumed) return hasTriggered

    if (!hasTriggered) {
        val verticalIntentThresholdPx = with(params.density) { VERTICAL_INTENT_THRESHOLD_DP.dp.toPx() }
        val absX = abs(totalDx)
        val absY = abs(totalDy)

        if (absY > verticalIntentThresholdPx && absY > 1.5f * absX) {
            change.consume()
            if (totalDy > 0) {
                params.onExpandNotificationShade()
            } else {
                params.onAllAppsDrawerOpen()
            }
            return true
        }
        return false
    }

    change.consume()
    return true
}
