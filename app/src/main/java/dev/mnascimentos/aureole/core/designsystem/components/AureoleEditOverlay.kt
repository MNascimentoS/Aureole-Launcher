package dev.mnascimentos.aureole.core.designsystem.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

private const val ANIMATION_DURATION_MS = 300
private const val DISMISS_DRAG_THRESHOLD_PX = 50f
private const val DISMISS_VELOCITY_THRESHOLD = 1000f
private const val EXPAND_VELOCITY_THRESHOLD = -500f
private const val COLLAPSE_VELOCITY_THRESHOLD = 500f

/**
 * Configuration options for [AureoleEditOverlay].
 *
 * @property title Title text displayed at the top of the overlay sheet.
 * @property hazeState Blur effect state for background haze blurring.
 * @property isVisible Controls visibility state of the overlay.
 * @property animated Whether show/hide transition animations are enabled.
 * @property maxSheetHeightFraction Maximum fraction of screen height occupied by the sheet.
 * @property onBackPress Custom back key handler callback.
 * @property onSettingsClick Settings icon click callback.
 */
data class AureoleEditOverlayConfig(
    val title: String? = null,
    val hazeState: HazeState? = null,
    val isVisible: Boolean = true,
    val animated: Boolean = true,
    val maxSheetHeightFraction: Float = 0.70f,
    val onBackPress: (() -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
)

/**
 * Content slots and configuration parameters for [AureoleEditOverlay].
 *
 * @property config Configuration options provided via [AureoleEditOverlayConfig].
 * @property previewContent Optional preview slot displayed in the main screen area above the bottom sheet.
 * @property bottomBarContent Optional action bottom bar slot anchored below the sheet.
 * @property sheetContent Main sheet content slot.
 */
class AureoleEditOverlayParams(
    val config: AureoleEditOverlayConfig = AureoleEditOverlayConfig(),
    val previewContent: (@Composable BoxScope.() -> Unit)? = null,
    val bottomBarContent: (@Composable ColumnScope.() -> Unit)? = null,
    val sheetContent: (@Composable ColumnScope.() -> Unit)? = null,
)

/**
 * Fullscreen modal overlay sheet for edit mode features (such as container/clock/widget group editing).
 *
 * Supports background blur via Haze, swipe-down dismiss gestures, preview content area, bottom bar actions, and custom sheet layout.
 *
 * @param modifier The [Modifier] applied to the root overlay layout. First optional parameter.
 * @param params Configuration and content slots provided via [AureoleEditOverlayParams].
 * @param onDismissRequest Callback executed when overlay is dismissed via drag gesture or back press. Slot callback parameter placed last.
 */
@Composable
fun AureoleEditOverlay(
    modifier: Modifier = Modifier,
    params: AureoleEditOverlayParams = AureoleEditOverlayParams(),
    onDismissRequest: () -> Unit = {},
) {
    val config = params.config
    val isInspection = LocalInspectionMode.current
    val effectiveAnimated = config.animated && !isInspection

    val overlayState = rememberOverlayDismissState(
        effectiveAnimated = effectiveAnimated,
        isVisible = config.isVisible,
        onBackPress = config.onBackPress,
        onDismissRequest = onDismissRequest
    )

    if (!isInspection) {
        BackHandler(enabled = config.isVisible && overlayState.internalVisible) {
            overlayState.triggerDismiss()
        }
    }

    AnimatedVisibility(
        visibleState = overlayState.visibleState,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
        exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)),
        modifier = modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            OverlayContentBox(
                params = params,
                hazeState = config.hazeState,
                effectiveAnimated = effectiveAnimated,
                triggerDismiss = overlayState.triggerDismiss,
                animScope = this
            )
        }
    }
}

private class OverlayDismissState(
    val visibleState: MutableTransitionState<Boolean>,
    val internalVisible: Boolean,
    val triggerDismiss: () -> Unit
)

@Composable
private fun rememberOverlayDismissState(
    effectiveAnimated: Boolean,
    isVisible: Boolean,
    onBackPress: (() -> Unit)?,
    onDismissRequest: () -> Unit
): OverlayDismissState {
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

    return remember(visibleState, internalVisible, triggerDismiss) {
        OverlayDismissState(visibleState, internalVisible, triggerDismiss)
    }
}

@Composable
private fun OverlayContentBox(
    params: AureoleEditOverlayParams,
    hazeState: HazeState?,
    effectiveAnimated: Boolean,
    triggerDismiss: () -> Unit,
    animScope: AnimatedVisibilityScope
) {
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
        val maxSheetHeight = maxHeight * params.config.maxSheetHeightFraction
        val sheetMaxExpandedHeight = maxHeight - AureoleDS.dimens.large
        val maxExpansionAllowed = (sheetMaxExpandedHeight - maxSheetHeight).coerceAtLeast(0.dp)
        val bounds = SheetBounds(maxSheetHeight, maxExpansionAllowed)

        Column(modifier = Modifier.fillMaxSize()) {
            PreviewSlot(
                previewContent = params.previewContent,
                effectiveAnimated = effectiveAnimated,
                animScope = animScope
            )

            SheetSurface(
                params = params,
                bounds = bounds,
                effectiveAnimated = effectiveAnimated,
                triggerDismiss = triggerDismiss,
                animScope = animScope
            )
        }
    }
}

@Composable
private fun ColumnScope.PreviewSlot(
    previewContent: (@Composable BoxScope.() -> Unit)?,
    effectiveAnimated: Boolean,
    animScope: AnimatedVisibilityScope
) {
    val previewModifier = if (effectiveAnimated) {
        with(animScope) {
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
        }
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
        previewContent?.invoke(this)
    }
}

private data class SheetBounds(
    val maxHeight: Dp,
    val maxExpansionAllowed: Dp
)

@Composable
private fun SheetSurface(
    params: AureoleEditOverlayParams,
    bounds: SheetBounds,
    effectiveAnimated: Boolean,
    triggerDismiss: () -> Unit,
    animScope: AnimatedVisibilityScope
) {
    var dragExpansion by remember { mutableFloatStateOf(0f) }

    val draggableState = rememberDraggableState { delta ->
        dragExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
        if (dragExpansion <= 0f && delta > DISMISS_DRAG_THRESHOLD_PX) {
            triggerDismiss()
        }
    }

    val currentMaxSheetHeight = bounds.maxHeight + dragExpansion.dp
    val sheetModifier = if (effectiveAnimated) {
        with(animScope) {
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
        }
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
                    handleDragStopped(velocity, dragExpansion, bounds.maxExpansionAllowed, triggerDismiss) {
                        dragExpansion = it
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
        color = AureoleDS.colors.surface,
        contentColor = AureoleDS.colors.onSurfaceMedium
    ) {
        OverlaySheetBody(
            title = params.config.title,
            onSettingsClick = params.config.onSettingsClick,
            bottomBarContent = params.bottomBarContent,
            sheetContent = params.sheetContent
        )
    }
}

private fun handleDragStopped(
    velocity: Float,
    dragExpansion: Float,
    maxExpansionAllowed: Dp,
    triggerDismiss: () -> Unit,
    onExpansionChanged: (Float) -> Unit
) {
    if (velocity > DISMISS_VELOCITY_THRESHOLD && dragExpansion == 0f) {
        triggerDismiss()
    } else if (velocity < EXPAND_VELOCITY_THRESHOLD) {
        onExpansionChanged(maxExpansionAllowed.value)
    } else if (velocity > COLLAPSE_VELOCITY_THRESHOLD) {
        onExpansionChanged(0f)
    } else {
        onExpansionChanged(
            if (dragExpansion > maxExpansionAllowed.value / 2) {
                maxExpansionAllowed.value
            } else {
                0f
            }
        )
    }
}

@Composable
private fun OverlaySheetBody(
    title: String?,
    onSettingsClick: (() -> Unit)?,
    bottomBarContent: (@Composable ColumnScope.() -> Unit)?,
    sheetContent: (@Composable ColumnScope.() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.large)
    ) {
        if (title != null) {
            OverlaySheetHeader(title = title, onSettingsClick = onSettingsClick)
        }

        if (sheetContent != null) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .fillMaxWidth()
            ) {
                sheetContent()
            }
        }

        if (bottomBarContent != null) {
            Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
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
            .padding(bottom = AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(AureoleDS.dimens.iconXLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .clickable(
                    onClick = { effectiveSettingsClick() },
                    role = Role.Button
                ),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun AureoleEditOverlayPreview() {
    AureoleLauncherTheme {
        AureoleEditOverlay(
            onDismissRequest = {},
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(title = "Edit Section"),
                previewContent = {
                    Text(
                        text = "Preview Slot",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                sheetContent = {
                    Text(
                        text = "Sheet Content",
                        color = AureoleDS.colors.onSurfaceHigh
                    )
                }
            )
        )
    }
}
