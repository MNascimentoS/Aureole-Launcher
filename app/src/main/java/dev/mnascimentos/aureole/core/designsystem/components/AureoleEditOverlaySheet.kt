package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.AnchoredSheetState
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalOverlayExternalBackdrop
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.model.anchoredPreviewTop
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.designsystem.utils.expandFractionOf
import dev.mnascimentos.aureole.core.designsystem.utils.scrimTapToDismiss
import kotlin.math.roundToInt

private const val ANIMATION_DURATION_MS = 300

/** The sheet slides out faster than it comes in. */
private const val SHEET_EXIT_MS = 250

/** Lets the entry spring settle to the pixel instead of stopping 1% short. */
private const val SHEET_SLIDE_THRESHOLD = 0.001f

/** Most a sheet whose content already fits stretches above rest. */
private val SHEET_ELASTIC_LIMIT = 64.dp

/** Height of the short bounce of a sheet that cannot expand. */
private val SHEET_BOUNCE = 24.dp

/** Content up to this much taller than the rest height still counts as fitting. */
private val SHEET_FIT_SLACK = 8.dp

@Composable
internal fun OverlayContentBox(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    hazeState: HazeState?
) {
    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .scrimTapToDismiss(stateData.triggerDismiss)
    ) {
        if (!params.config.hasExternalBackdrop && !LocalOverlayExternalBackdrop.current) {
            AureoleOverlayBackdrop(
                hazeState = effectiveHazeState,
                modifier = Modifier
                    .fillMaxSize()
                    .fadeWith(stateData)
            )
        }
        val density = LocalDensity.current
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val bounds = with(density) {
            SheetBounds(
                restMaxPx = screenHeightPx * params.config.restHeightFraction,
                expandedPx = screenHeightPx * params.config.expandedHeightFraction,
                elasticLimitPx = SHEET_ELASTIC_LIMIT.toPx(),
                bouncePx = SHEET_BOUNCE.toPx(),
                fitSlackPx = SHEET_FIT_SLACK.toPx()
            )
        }
        val motion = remember { OverlaySheetMotion() }

        AnchoredPreviewLayout(
            state = AnchoredSheetState(bounds, motion, stateData.isExpanded),
            hiddenFraction = rememberSheetHiddenFraction(stateData),
            preview = {
                PreviewSlot(
                    previewContent = params.previewContent,
                    modifier = Modifier.fadeWith(stateData),
                    expandFraction = { expandFractionOf(motion, stateData.isExpanded, bounds) }
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

/** Fades with the overlay as it opens and closes; nothing when animations are off. */
private fun Modifier.fadeWith(stateData: OverlaySheetStateData): Modifier {
    if (!stateData.effectiveAnimated) return this
    return then(
        with(stateData.animScope) {
            Modifier.animateEnterExit(
                enter = fadeIn(tween(ANIMATION_DURATION_MS)),
                exit = fadeOut(tween(ANIMATION_DURATION_MS))
            )
        }
    )
}

/**
 * How much of the sheet is still below the screen while the overlay opens (1 → 0) or closes (0 → 1). Read in the
 * layout, so the slide only moves the sheet and the preview. Always 0 when animations are off.
 */
@Composable
private fun rememberSheetHiddenFraction(stateData: OverlaySheetStateData): () -> Float {
    if (!stateData.effectiveAnimated) return { 0f }
    val shown = stateData.animScope.transition.animateFloat(
        transitionSpec = {
            if (targetState == EnterExitState.Visible) {
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                    visibilityThreshold = SHEET_SLIDE_THRESHOLD
                )
            } else {
                tween(durationMillis = SHEET_EXIT_MS, easing = FastOutLinearInEasing)
            }
        },
        label = "sheetSlide"
    ) { state -> if (state == EnterExitState.Visible) 1f else 0f }
    return remember(shown) { { 1f - shown.value } }
}

/**
 * Places the sheet at the bottom and the preview right above the sheet's real top, measured in the same
 * layout pass. The preview follows the sheet while it is dragged, settles, expands over it or slides in and
 * out with the overlay ([hiddenFraction] of the sheet height below its place), and keeps the size it gets above
 * the sheet at rest.
 */
@Composable
private fun AnchoredPreviewLayout(
    state: AnchoredSheetState,
    hiddenFraction: () -> Float,
    preview: @Composable () -> Unit,
    sheet: @Composable () -> Unit
) {
    val (bounds, motion, isExpanded) = state
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
        // An unbounded parent (e.g. inside a scroll) has no height to subtract from: keep it unbounded, since
        // Infinity minus anything is not a height Constraints can represent.
        val previewMaxHeight = if (constraints.hasBoundedHeight) {
            (constraints.maxHeight - restSheetHeight - previewGapPx - previewTopPaddingPx).coerceAtLeast(0)
        } else {
            Constraints.Infinity
        }
        val previewPlaceables = previewMeasurables.map { it.measure(loose.copy(maxHeight = previewMaxHeight)) }
        val layoutHeight = if (constraints.hasBoundedHeight) {
            constraints.maxHeight
        } else {
            val previewHeight = previewPlaceables.maxOfOrNull { it.height } ?: 0
            previewTopPaddingPx + previewHeight + previewGapPx + sheetHeight
        }

        layout(constraints.maxWidth, layoutHeight) {
            val slide = (hiddenFraction() * sheetHeight).roundToInt()
            val sheetTop = layoutHeight - sheetHeight + motion.translation(bounds).roundToInt() + slide
            previewPlaceables.forEach { placeable ->
                placeable.placeRelative(
                    0,
                    anchoredPreviewTop(sheetTop, previewGapPx, placeable.height, previewTopPaddingPx)
                )
            }
            // Centered: a sheet narrower than the screen (LocalOverlaySheetMaxWidth) sits in the middle.
            sheetPlaceables.forEach { it.placeRelative((constraints.maxWidth - it.width) / 2, sheetTop) }
        }
    }
}

@Composable
private fun PreviewSlot(
    previewContent: (@Composable BoxScope.() -> Unit)?,
    expandFraction: () -> Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = 1f - expandFraction() },
        contentAlignment = Alignment.BottomCenter
    ) {
        previewContent?.invoke(this)
    }
}

/**
 * Inside of the overlay sheet: gesture zone, header and content at the top, bottom bar at the bottom.
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
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xxSmall),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val hasBottomBar = bottomBarContent != null
            Column(modifier = Modifier.weight(1f, fill = hasBottomBar)) {
                decorations.gestureZone?.invoke()
                if (title != null) {
                    OverlaySheetHeader(
                        title = title,
                        titleIcon = decorations.titleIcon
                    )
                }
                if (sheetContent != null) {
                    val contentPadding = if (decorations.edgeToEdgeContent) 0.dp else AureoleDS.dimens.small
                    Column(
                        modifier = Modifier
                            .padding(horizontal = contentPadding)
                            .weight(1f, fill = hasBottomBar)
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
                        top = AureoleDS.dimens.small,
                        bottom = AureoleDS.dimens.medium
                    )
                ) {
                    bottomBarContent()
                }
            }
        }

        if (decorations.showHeaderAction) {
            OverlaySheetSettingsSlot(
                onSettingsClick = onSettingsClick,
                headerIcon = decorations.headerIcon,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/** Top-end slot of the sheet: header icon (or logo) that opens settings when [onSettingsClick] is set. */
@Composable
private fun OverlaySheetSettingsSlot(
    onSettingsClick: (() -> Unit)?,
    headerIcon: (@Composable (Modifier) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val settingsLabel = stringResource(R.string.overlay_sheet_action_settings)
    Box(
        modifier = modifier
            .padding(top = AureoleDS.dimens.minimal, end = AureoleDS.dimens.minimal)
            .size(AureoleDS.dimens.iconXLarge)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .then(
                if (onSettingsClick != null) {
                    Modifier
                        .clickable(onClick = onSettingsClick, role = Role.Button)
                        .semantics { contentDescription = settingsLabel }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (headerIcon != null) {
            headerIcon.invoke(Modifier.size(AureoleDS.dimens.iconSmall))
        } else {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@Composable
private fun OverlaySheetHeader(
    title: String,
    titleIcon: (@Composable (Modifier) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = AureoleDS.dimens.small,
                top = AureoleDS.dimens.small,
                end = AureoleDS.dimens.small,
                bottom = AureoleDS.dimens.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (titleIcon != null) {
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.iconXLarge)
                        .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                        .background(AureoleDS.colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    titleIcon(Modifier.size(AureoleDS.dimens.iconMedium))
                }
                Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
            }
            Text(
                text = title,
                style = AureoleDS.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh,
                textAlign = TextAlign.Center
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
