package dev.mnascimentos.aureole.core.designsystem.components.bottomsheet

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions

private const val ANIMATION_DURATION_MS = 300

/**
 * Base Design System component for editor screens (Clock, Container, Widgets, etc.).
 *
 * Provides a unified overlay layout featuring:
 * - Backdrop with option for Haze blur or semi-transparent background and dismiss on tap.
 * - System back handler integration.
 * - Fade entrance/exit animations for backdrop and slide entrance/exit animations for sheet.
 * - Flexible layout where [previewContent] occupies the space above the bottom sheet
 *   and [sheetContent] is rendered inside a draggable bottom sheet Surface.
 *
 * @param onDismissRequest Action triggered when backdrop or system back button is pressed.
 * @param modifier Custom modifier applied to the overlay container.
 * @param title Optional title displayed at the top of the bottom sheet.
 * @param hazeState Active [HazeState] for background blur, or null if haze is disabled.
 * @param isVisible Visibility flag, used for visibility and animations.
 * @param animated Whether to animate entrance and exit of the overlay using fade and slide.
 * @param onBackPress Custom back button handler. If null, [onDismissRequest] is invoked.
 * @param onSettingsClick Optional click handler for the Aureole icon in the sheet header.
 * @param maxSheetHeightFraction Maximum height fraction of the screen that the bottom sheet can occupy in collapsed mode.
 * @param previewContent Composable slot for the live preview component floating above the bottom sheet.
 * @param bottomBarContent Optional composable slot for action buttons (e.g. Save, Reset) at the bottom of the sheet.
 * @param sheetContent Composable slot for options/controls inside the bottom sheet.
 */
@Composable
fun AureoleEditOverlay(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    hazeState: HazeState? = null,
    isVisible: Boolean = true,
    animated: Boolean = true,
    onBackPress: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    maxSheetHeightFraction: Float = 0.70f,
    previewContent: @Composable BoxScope.() -> Unit,
    bottomBarContent: (@Composable ColumnScope.() -> Unit)? = null,
    sheetContent: @Composable ColumnScope.() -> Unit
) {
    var internalVisible by remember { mutableStateOf(true) }

    val visibleState = remember {
        MutableTransitionState(if (animated) false else isVisible)
    }.apply {
        targetState = if (animated) isVisible && internalVisible else isVisible
    }

    val triggerDismiss = remember(animated, onBackPress, onDismissRequest) {
        {
            if (animated) {
                internalVisible = false
            } else {
                if (onBackPress != null) {
                    onBackPress()
                } else {
                    onDismissRequest()
                }
            }
        }
    }

    LaunchedEffect(visibleState.currentState, visibleState.isIdle, internalVisible) {
        if (animated && visibleState.isIdle && !visibleState.currentState && !internalVisible) {
            if (onBackPress != null) {
                onBackPress()
            } else {
                onDismissRequest()
            }
        }
    }

    val isInspection = LocalInspectionMode.current
    if (!isInspection) {
        BackHandler(enabled = isVisible && internalVisible) {
            triggerDismiss()
        }
    }

    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    val overlayContent = @Composable {
        val hazeModifier = if (effectiveHazeState != null) {
            Modifier.hazeEffect(
                state = effectiveHazeState,
                style = HazeStyle(
                    blurRadius = 32.dp,
                    tint = HazeTint(Color.Black.copy(alpha = 0.55f))
                )
            ) { blurEnabled = true }
        } else {
            Modifier
        }
        val backgroundColor = if (effectiveHazeState != null) Color.Transparent else Color.Black

        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
            exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)),
            modifier = modifier.fillMaxSize()
        ) {
            CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(hazeModifier)
                        .background(backgroundColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = triggerDismiss
                        )
                ) {
                    val maxSheetHeight = maxHeight * maxSheetHeightFraction
                    val sheetMaxExpandedHeight = maxHeight - AureoleDS.dimens.large
                    val maxExpansionAllowed = (sheetMaxExpandedHeight - maxSheetHeight).coerceAtLeast(0.dp)

                    var dragExpansion by remember { mutableFloatStateOf(0f) }

                    val draggableState = rememberDraggableState { delta ->
                        dragExpansion = (dragExpansion - delta).coerceIn(0f, maxExpansionAllowed.value)

                        if (dragExpansion <= 0f && delta > 50f) {
                            triggerDismiss()
                        }
                    }

                    val currentMaxSheetHeight = maxSheetHeight + dragExpansion.dp

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Live preview area occupying remaining space above the bottom sheet
                        val previewModifier = if (animated) {
                            Modifier.animateEnterExit(
                                enter = fadeIn(tween(ANIMATION_DURATION_MS)) +
                                    scaleIn(
                                        initialScale = 0.75f,
                                        animationSpec = tween(ANIMATION_DURATION_MS)
                                    ),
                                exit = fadeOut(tween(ANIMATION_DURATION_MS)) +
                                    scaleOut(
                                        targetScale = 0.75f,
                                        animationSpec = tween(ANIMATION_DURATION_MS)
                                    )
                            )
                        } else {
                            Modifier
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .then(previewModifier)
                                .padding(
                                    start = AureoleDS.dimens.medium,
                                    end = AureoleDS.dimens.medium,
                                    top = AureoleDS.dimens.large,
                                    bottom = AureoleDS.dimens.medium
                                ),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            previewContent()
                        }

                        // Bottom Sheet Surface with slide animation
                        val sheetModifier = if (animated) {
                            Modifier.animateEnterExit(
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(ANIMATION_DURATION_MS)
                                ),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = tween(ANIMATION_DURATION_MS)
                                )
                            )
                        } else {
                            Modifier
                        }

                        Surface(
                            modifier = sheetModifier
                                .fillMaxWidth()
                                .heightIn(max = currentMaxSheetHeight)
                                .draggable(
                                    state = draggableState,
                                    orientation = Orientation.Vertical,
                                    onDragStopped = { velocity ->
                                        if (velocity > 1000f && dragExpansion == 0f) {
                                            triggerDismiss()
                                        } else if (velocity < -500f) {
                                            dragExpansion = maxExpansionAllowed.value
                                        } else if (velocity > 500f) {
                                            dragExpansion = 0f
                                        } else {
                                            if (dragExpansion > maxExpansionAllowed.value / 2) {
                                                dragExpansion = maxExpansionAllowed.value
                                            } else {
                                                dragExpansion = 0f
                                            }
                                        }
                                    }
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {}
                                ),
                            shape = RoundedCornerShape(topStart = AureoleDS.dimens.radiusLarge, topEnd = AureoleDS.dimens.radiusLarge),
                            color = AureoleTheme.colors.surface,
                            contentColor = AureoleTheme.colors.onSurfaceMedium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 20.dp)
                            ) {
                                if (title != null) {
                                    val homeActions = LocalHomeActions.current
                                    val effectiveSettingsClick = onSettingsClick ?: { homeActions.onSettingsClick() }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = title,
                                            style = AureoleTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AureoleTheme.colors.onSurfaceHigh,
                                            textAlign = TextAlign.Center
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .size(AureoleDS.dimens.xLarge)
                                                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                                                .clickable { effectiveSettingsClick() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AureoleDS.icons.Logo(
                                                tint = AureoleTheme.colors.onSurfaceHigh,
                                                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        .fillMaxWidth()
                                ) {
                                    sheetContent()
                                }

                                if (bottomBarContent != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    bottomBarContent()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    overlayContent()
}
