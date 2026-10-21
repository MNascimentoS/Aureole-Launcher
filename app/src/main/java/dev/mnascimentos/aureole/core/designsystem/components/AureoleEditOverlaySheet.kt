package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState

private const val ANIMATION_DURATION_MS = 300
private const val DISMISS_DRAG_THRESHOLD_PX = 50f
private const val DISMISS_VELOCITY_THRESHOLD = 1000f
private const val EXPAND_VELOCITY_THRESHOLD = -500f
private const val COLLAPSE_VELOCITY_THRESHOLD = 500f

@Composable
internal fun OverlayContentBox(
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
                role = Role.Button,
                onClick = triggerDismiss
            )
    ) {
        val maxSheetHeight = maxHeight * params.config.maxSheetHeightFraction
        val sheetMaxExpandedHeight = maxHeight - AureoleDS.dimens.large
        val maxExpansionAllowed = (sheetMaxExpandedHeight - maxSheetHeight).coerceAtLeast(0.dp)
        val bounds = SheetBounds(maxSheetHeight, maxExpansionAllowed)

        // The preview height is fixed to the available unexpanded sheet space
        // We use maxSheetHeight to determine the top available space, keeping the preview height constant
        val previewHeight = maxHeight - maxSheetHeight

        Box(modifier = Modifier.fillMaxSize()) {
            PreviewSlot(
                previewHeight = previewHeight,
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
private fun BoxScope.PreviewSlot(
    previewHeight: Dp,
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
            .align(Alignment.TopCenter)
            .fillMaxWidth()
            .height(previewHeight)
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

@Suppress("LongMethod")
@Composable
private fun BoxScope.SheetSurface(
    params: AureoleEditOverlayParams,
    bounds: SheetBounds,
    effectiveAnimated: Boolean,
    triggerDismiss: () -> Unit,
    animScope: AnimatedVisibilityScope
) {
    var dragExpansion by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember(bounds.maxExpansionAllowed) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                if (delta < 0 && dragExpansion < bounds.maxExpansionAllowed.value) {
                    val prevExpansion = dragExpansion
                    dragExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
                    val consumedY = dragExpansion - prevExpansion
                    return Offset(0f, -consumedY)
                }
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (delta > 0 && dragExpansion > 0f) {
                    val prevExpansion = dragExpansion
                    dragExpansion = (dragExpansion - delta).coerceIn(0f, bounds.maxExpansionAllowed.value)
                    val consumedY = prevExpansion - dragExpansion
                    return Offset(0f, consumedY)
                }
                return Offset.Zero
            }
        }
    }

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
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .heightIn(max = currentMaxSheetHeight)
            .nestedScroll(nestedScrollConnection)
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
                role = Role.Button,
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
        return
    }
    if (velocity < EXPAND_VELOCITY_THRESHOLD) {
        onExpansionChanged(maxExpansionAllowed.value)
        return
    }
    if (velocity > COLLAPSE_VELOCITY_THRESHOLD) {
        onExpansionChanged(0f)
        return
    }
    val targetExpansion = if (dragExpansion > maxExpansionAllowed.value / 2) {
        maxExpansionAllowed.value
    } else {
        0f
    }
    onExpansionChanged(targetExpansion)
}

@Composable
internal fun OverlaySheetBody(
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
                    onClick = { onSettingsClick?.invoke() },
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
private fun AureoleEditOverlaySheetPreview() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Preview Overlay Sheet",
            onSettingsClick = {},
            bottomBarContent = null,
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}

@AureolePreview
@Composable
private fun AureoleEditOverlaySheetWithBottomBarPreview() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Overlay Sheet with Bottom Bar",
            onSettingsClick = {},
            bottomBarContent = {
                Text(
                    text = "Bottom Bar Content",
                    color = AureoleDS.colors.onSurfaceMedium
                )
            },
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}
