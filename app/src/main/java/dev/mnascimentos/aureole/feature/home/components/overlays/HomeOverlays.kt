package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.isScrubberHintVisible
import dev.mnascimentos.aureole.feature.home.components.scrubber.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.model.AllAppsOpenSource
import dev.mnascimentos.aureole.feature.home.model.AppsDrawerOverlayConfig
import dev.mnascimentos.aureole.feature.home.model.DrawerMotion
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.ScrubberOverlayConfig
import dev.mnascimentos.aureole.feature.home.model.drawerMotionFor
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BoxScope.HomeOverlaysContent(
    config: HomeOverlaysConfig,
    modifier: Modifier = Modifier
) {
    AppsDrawerOverlay(
        config = AppsDrawerOverlayConfig(
            isAllAppsDrawerOpen = config.uiState.isAllAppsDrawerOpen,
            openSource = config.uiState.allAppsOpenSource,
            isLeftHandedMode = config.uiState.isLeftHandedMode,
            listState = config.listState,
            hazeState = config.hazeState,
            onClose = config.actions.onAllAppsDrawerClose,
        ),
        modifier = modifier
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
    modifier: Modifier = Modifier,
) {
    val enterTransition = when (drawerMotionFor(config.openSource, config.isLeftHandedMode)) {
        DrawerMotion.FROM_BOTTOM -> fadeIn() + slideInVertically { it }
        DrawerMotion.FROM_RIGHT -> fadeIn() + slideInHorizontally { it }
        DrawerMotion.FROM_LEFT -> fadeIn() + slideInHorizontally { -it }
    }

    val exitTransition = when (drawerMotionFor(config.openSource, config.isLeftHandedMode)) {
        DrawerMotion.FROM_BOTTOM -> fadeOut() + slideOutVertically { it }
        DrawerMotion.FROM_RIGHT -> fadeOut() + slideOutHorizontally { it }
        DrawerMotion.FROM_LEFT -> fadeOut() + slideOutHorizontally { -it }
    }

    AnimatedVisibility(
        visible = config.isAllAppsDrawerOpen,
        enter = enterTransition,
        exit = exitTransition,
        modifier = modifier.fillMaxSize(),
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
            externalTouchY = config.externalTouchY,
            isHintVisible = config.uiState.isScrubberHintVisible,
            isProminentSelection = true
        ),
        callbacks = ScrubberCallbacks(
            onLetterSelected = { letter ->
                config.actions.onboarding.onAlphabetScrubbed()
                if (!config.uiState.isAllAppsDrawerOpen) {
                    config.actions.onAllAppsDrawerOpenFromEdge()
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
                    config.actions.onAllAppsDrawerOpenFromEdge()
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

@AureolePreview
@Composable
private fun AppsDrawerOverlayPreview() {
    AureoleLauncherTheme {
        AppsDrawerOverlay(
            config = AppsDrawerOverlayConfig(
                isAllAppsDrawerOpen = true,
                openSource = AllAppsOpenSource.EDGE,
                isLeftHandedMode = false,
                listState = rememberLazyListState(),
                hazeState = remember { HazeState() },
                onClose = {}
            )
        )
    }
}
