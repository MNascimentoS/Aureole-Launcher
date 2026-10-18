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
import dev.mnascimentos.aureole.feature.home.LocalHomeActions

private const val ANIMATION_DURATION_MS = 300
private const val DISMISS_DRAG_THRESHOLD_PX = 50f
private const val DISMISS_VELOCITY_THRESHOLD = 1000f
private const val EXPAND_VELOCITY_THRESHOLD = -500f
private const val COLLAPSE_VELOCITY_THRESHOLD = 500f

/**
 * Configuration options for [AureoleEditOverlay].
 *
 * @param title The optional title of the overlay.
 * @param hazeState The haze state for the background blur effect.
 * @param isVisible Whether the overlay is currently visible.
 * @param animated Whether the overlay should animate its entrance and exit.
 * @param maxSheetHeightFraction The maximum height fraction of the screen the sheet can occupy.
 * @param onBackPress Executes when the user presses the back button.
 * @param onSettingsClick Executes when the user clicks the settings icon.
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
 * Parameters for [AureoleEditOverlay].
 *
 * @param config The overlay configuration options.
 * @param previewContent The content to be displayed in the preview slot.
 * @param bottomBarContent The optional content to be displayed at the bottom of the sheet.
 * @param sheetContent The main content of the sheet.
 */
class AureoleEditOverlayParams(
    val config: AureoleEditOverlayConfig = AureoleEditOverlayConfig(),
    val previewContent: (@Composable BoxScope.() -> Unit)? = null,
    val bottomBarContent: (@Composable ColumnScope.() -> Unit)? = null,
    val sheetContent: (@Composable ColumnScope.() -> Unit)? = null,
)

/**
 * Base Design System component for editor screens (Clock, Container, Widgets, etc.).
 *
 * Provides a unified overlay layout featuring backdrop, sheet surface, animations, and preview slot.
 *
 * @param params The parameters for configuring the overlay.
 * @param modifier The modifier to be applied to the layout.
 * @param onDismissRequest Executes when the user requests to dismiss the overlay.
 */
@Composable
fun AureoleEditOverlay(
    params: AureoleEditOverlayParams,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
) {
    val hazeState = params.config.hazeState
    val isVisible = params.config.isVisible
    val animated = params.config.animated
    val onBackPress = params.config.onBackPress

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

    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
        exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)),
        modifier = modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            OverlayContentBox(
                params = params,
                hazeState = hazeState,
                effectiveAnimated = effectiveAnimated,
                triggerDismiss = triggerDismiss,
                animScope = this
            )
        }
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
                    handleDragStopped(
                        velocity,
                        dragExpansion,
                        bounds.maxExpansionAllowed,
                        triggerDismiss
                    ) { newExpansion ->
                        dragExpansion = newExpansion
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
                .size(AureoleDS.dimens.xLarge)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .clickable { effectiveSettingsClick() },
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
