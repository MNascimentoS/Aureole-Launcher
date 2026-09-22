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
import androidx.compose.ui.graphics.Color
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

private const val DRAG_THRESHOLD_PX = 15
private const val EDGE_EXCLUSION_WIDTH_DP = 60
private const val TOP_PADDING_DP = 64

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
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
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
): Modifier = pointerInput(params.isLeftHandedMode, params.screenHeightPx, params.screenWidthPx) {
    var dragStartedOnEdge = false
    detectDragGestures(
        onDragStart = { offset ->
            val edgeThreshold = with(params.density) { EDGE_EXCLUSION_WIDTH_DP.dp.toPx() }
            dragStartedOnEdge = if (params.isLeftHandedMode) {
                offset.x < edgeThreshold
            } else {
                offset.x > params.screenWidthPx - edgeThreshold
            }

            if (dragStartedOnEdge) {
                val topPaddingPx = with(params.density) { TOP_PADDING_DP.dp.toPx() }
                params.onExternalTouchYChange(offset.y - topPaddingPx)
                if (!params.isAllAppsDrawerOpen) {
                    params.onAllAppsDrawerOpen()
                }
            }
        },
        onDragEnd = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
        },
        onDragCancel = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
        },
        onDrag = { change, dragAmount ->
            if (dragStartedOnEdge) {
                change.consume()
                val topPaddingPx = with(params.density) { TOP_PADDING_DP.dp.toPx() }
                params.onExternalTouchYChange(change.position.y - topPaddingPx)
                return@detectDragGestures
            }

            if (params.isAllAppsDrawerOpen || change.isConsumed) return@detectDragGestures

            val absX = abs(dragAmount.x)
            val absY = abs(dragAmount.y)
            val isVertical = absY > DRAG_THRESHOLD_PX && absY > absX

            if (isVertical) {
                change.consume()
                if (dragAmount.y > 0) {
                    params.onExpandNotificationShade()
                } else {
                    params.onAllAppsDrawerOpen()
                }
            }
        }
    )
}
