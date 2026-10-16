package dev.mnascimentos.aureole.core.designsystem.components

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
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions

private const val ANIMATION_DURATION_MS = 300
private const val DISMISS_DRAG_THRESHOLD_PX = 50f
private const val DISMISS_VELOCITY_THRESHOLD = 1000f
private const val EXPAND_VELOCITY_THRESHOLD = -500f
private const val COLLAPSE_VELOCITY_THRESHOLD = 500f

/**
 * Base Design System component for editor screens (Clock, Container, Widgets, etc.).
 *
 * Provides a unified overlay layout featuring backdrop, sheet surface, animations, and preview slot.
 */
@Suppress(
    "LongParameterList",
    "LongMethod",
    "CyclomaticComplexMethod"
) // Reusable edit overlay component managing transition states, drag gestures and layout slots
@Composable
fun AureoleEditOverlay(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier.Companion,
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
    val isInspection = LocalInspectionMode.current
    val effectiveAnimated = animated && !isInspection

    var internalVisible by remember { mutableStateOf(true) }

    val visibleState = remember(effectiveAnimated) {
        MutableTransitionState(if (effectiveAnimated) false else isVisible)
    }.apply {
        targetState = if (effectiveAnimated) isVisible && internalVisible else isVisible
    }

    val triggerDismiss = remember(effectiveAnimated, onBackPress, onDismissRequest) {
        {
            if (effectiveAnimated) {
                internalVisible = false
            } else {
                onBackPress?.invoke() ?: onDismissRequest()
            }
        }
    }

    LaunchedEffect(visibleState.currentState, visibleState.isIdle, internalVisible) {
        val isHidden = !visibleState.currentState && !internalVisible
        if (effectiveAnimated && visibleState.isIdle && isHidden) {
            onBackPress?.invoke() ?: onDismissRequest()
        }
    }

    if (!isInspection) {
        BackHandler(enabled = isVisible && internalVisible) {
            triggerDismiss()
        }
    }

    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    val hazeModifier = if (effectiveHazeState != null) {
        Modifier.hazeEffect(
            state = effectiveHazeState,
            style = HazeStyle(
                blurRadius = 32.dp,
                tint = HazeTint(Color.Black.copy(alpha = 0.55f))
            )
        ) { blurEnabled = true }
    } else {
        Modifier.Companion
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
                val maxExpansionAllowed =
                    (sheetMaxExpandedHeight - maxSheetHeight).coerceAtLeast(0.dp)

                var dragExpansion by remember { mutableFloatStateOf(0f) }

                val draggableState = rememberDraggableState { delta ->
                    dragExpansion = (dragExpansion - delta).coerceIn(0f, maxExpansionAllowed.value)

                    if (dragExpansion <= 0f && delta > DISMISS_DRAG_THRESHOLD_PX) {
                        triggerDismiss()
                    }
                }

                val currentMaxSheetHeight = maxSheetHeight + dragExpansion.dp

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val previewModifier = if (effectiveAnimated) {
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
                        Modifier.Companion
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

                    val sheetModifier = if (effectiveAnimated) {
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
                        Modifier.Companion
                    }

                    Surface(
                        modifier = sheetModifier
                            .fillMaxWidth()
                            .heightIn(max = currentMaxSheetHeight)
                            .draggable(
                                state = draggableState,
                                orientation = Orientation.Vertical,
                                onDragStopped = { velocity ->
                                    if (velocity > DISMISS_VELOCITY_THRESHOLD && dragExpansion == 0f) {
                                        triggerDismiss()
                                    } else if (velocity < EXPAND_VELOCITY_THRESHOLD) {
                                        dragExpansion = maxExpansionAllowed.value
                                    } else if (velocity > COLLAPSE_VELOCITY_THRESHOLD) {
                                        dragExpansion = 0f
                                    } else {
                                        dragExpansion =
                                            if (dragExpansion > maxExpansionAllowed.value / 2) {
                                                maxExpansionAllowed.value
                                            } else {
                                                0f
                                            }
                                    }
                                }
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {}
                            ),
                        shape = RoundedCornerShape(
                            topStart = AureoleDS.dimens.radiusLarge,
                            topEnd = AureoleDS.dimens.radiusLarge
                        ),
                        color = AureoleTheme.colors.surface,
                        contentColor = AureoleTheme.colors.onSurfaceMedium
                    ) {
                        OverlaySheetBody(
                            title = title,
                            onSettingsClick = onSettingsClick,
                            bottomBarContent = bottomBarContent,
                            sheetContent = sheetContent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OverlaySheetBody(
    title: String?,
    onSettingsClick: (() -> Unit)?,
    bottomBarContent: (@Composable ColumnScope.() -> Unit)?,
    sheetContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        if (title != null) {
            OverlaySheetHeader(title = title, onSettingsClick = onSettingsClick)
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

@Composable
private fun OverlaySheetHeader(
    title: String,
    onSettingsClick: (() -> Unit)?
) {
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
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(AureoleDS.dimens.radiusMedium))
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

@AureolePreview
@Composable
fun AureoleEditOverlayPreview() {
    AureoleLauncherTheme {
        AureoleEditOverlay(
            onDismissRequest = {},
            title = "Edit Section",
            previewContent = {
                Text(
                    text = "Preview Slot",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            Text(
                text = "Sheet Content",
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }
}
