package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.model.anchoredPreviewTop
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import kotlin.math.roundToInt

private const val ANIMATION_DURATION_MS = 300

@Composable
internal fun OverlayContentBox(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    hazeState: HazeState?
) {
    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    val hazeModifier = if (effectiveHazeState != null) {
        Modifier.hazeEffect(
            state = effectiveHazeState,
            style = HazeStyle(
                blurRadius = AureoleDS.dimens.xLarge,
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
            .scrimTapToDismiss(stateData.triggerDismiss)
    ) {
        val density = LocalDensity.current
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val bounds = SheetBounds(
            restMaxPx = screenHeightPx * params.config.restHeightFraction,
            expandedPx = screenHeightPx * params.config.expandedHeightFraction
        )
        val motion = remember { OverlaySheetMotion() }

        AnchoredPreviewLayout(
            bounds = bounds,
            motion = motion,
            isExpanded = stateData.isExpanded,
            preview = {
                PreviewSlot(
                    previewContent = params.previewContent,
                    effectiveAnimated = stateData.effectiveAnimated,
                    animScope = stateData.animScope
                )
            },
            sheet = {
                SheetSurface(
                    stateData = stateData,
                    params = params,
                    bounds = bounds,
                    motion = motion
                )
            }
        )
    }
}

/**
 * Closes the overlay on a tap outside the sheet. A plain tap detector instead of `clickable`, so the
 * scrim does not merge the whole sheet into a single accessibility node; TalkBack closes with back.
 */
private fun Modifier.scrimTapToDismiss(onDismiss: () -> Unit): Modifier {
    return pointerInput(onDismiss) { detectTapGestures(onTap = { onDismiss() }) }
}

/**
 * Places the sheet at the bottom and the preview right above the sheet's real top, measured in the same
 * layout pass. The preview follows the sheet while it is dragged, settles or expands over it, and keeps
 * the size it gets above the sheet at rest.
 */
@Composable
private fun AnchoredPreviewLayout(
    bounds: SheetBounds,
    motion: OverlaySheetMotion,
    isExpanded: Boolean,
    preview: @Composable () -> Unit,
    sheet: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val previewGapPx = with(density) { AureoleDS.dimens.medium.roundToPx() }
    val previewTopPaddingPx = with(density) { AureoleDS.dimens.large.roundToPx() }
    Layout(
        contents = listOf(preview, sheet),
        modifier = Modifier.fillMaxSize()
    ) { (previewMeasurables, sheetMeasurables), constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val sheetPlaceables = sheetMeasurables.map { it.measure(loose) }
        val sheetHeight = sheetPlaceables.maxOfOrNull { it.height } ?: 0
        if (motion.fixedHeight(isExpanded, bounds) == null) motion.restOuterHeight = sheetHeight
        val restSheetHeight = motion.restOuterHeight.takeIf { it > 0 } ?: bounds.restMaxPx.roundToInt()
        val previewMaxHeight = (constraints.maxHeight - restSheetHeight - previewGapPx - previewTopPaddingPx)
            .coerceAtLeast(0)
        val previewPlaceables = previewMeasurables.map { it.measure(loose.copy(maxHeight = previewMaxHeight)) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            val sheetTop = constraints.maxHeight - sheetHeight + motion.translation(bounds).roundToInt()
            previewPlaceables.forEach { placeable ->
                placeable.placeRelative(
                    0,
                    anchoredPreviewTop(sheetTop, previewGapPx, placeable.height, previewTopPaddingPx)
                )
            }
            sheetPlaceables.forEach { it.placeRelative(0, sheetTop) }
        }
    }
}

@Composable
private fun PreviewSlot(
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
            .then(previewModifier)
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.BottomCenter
    ) {
        previewContent?.invoke(this)
    }
}

/**
 * Inside of the overlay sheet: handle, header and content at the top, bottom bar at the bottom.
 * When the sheet is taller than its content (expanded), the free space sits between content and bottom bar.
 */
@Composable
internal fun OverlaySheetBody(
    title: String?,
    onSettingsClick: (() -> Unit)?,
    bottomBarContent: (@Composable ColumnScope.() -> Unit)?,
    sheetContent: (@Composable ColumnScope.() -> Unit)?,
    decorations: OverlaySheetDecorations = OverlaySheetDecorations()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            decorations.handle?.invoke()
            if (title != null) {
                OverlaySheetHeader(
                    title = title,
                    onSettingsClick = onSettingsClick,
                    headerIcon = decorations.headerIcon
                )
            }
            if (sheetContent != null) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = AureoleDS.dimens.small)
                        .weight(1f, fill = false)
                        .fillMaxWidth()
                ) {
                    sheetContent()
                }
            }
        }

        if (bottomBarContent != null) {
            Column(
                modifier = Modifier.padding(
                    start = AureoleDS.dimens.small,
                    end = AureoleDS.dimens.small,
                    top = AureoleDS.dimens.small
                )
            ) {
                bottomBarContent()
            }
        }
    }
}

@Composable
private fun OverlaySheetHeader(
    title: String,
    onSettingsClick: (() -> Unit)?,
    headerIcon: (@Composable (Modifier) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AureoleDS.dimens.medium),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = title,
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            textAlign = TextAlign.Center
        )

        if (onSettingsClick != null || headerIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                    .clickable(
                        onClick = { onSettingsClick?.invoke() },
                        role = Role.Button
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (headerIcon != null) {
                    headerIcon(Modifier.size(AureoleDS.dimens.iconSmall))
                } else {
                    AureoleDS.icons.Logo(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                }
            }
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
