package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.EnterExitState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.model.AnchoredSheetState
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHeaderTitle
import dev.mnascimentos.aureole.core.designsystem.model.OverlayLayout
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.hasHeaderAction
import dev.mnascimentos.aureole.core.designsystem.model.overlayLayoutFor
import dev.mnascimentos.aureole.core.designsystem.model.isPreviewHidden
import dev.mnascimentos.aureole.core.designsystem.model.previewAlpha
import dev.mnascimentos.aureole.core.designsystem.model.sheetRestFraction
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

@Composable
internal fun OverlayContentBox(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    hazeState: HazeState?
) {
    val localHaze = LocalHazeState.current
    val effectiveHazeState = hazeState ?: localHaze

    val tapOutside = if (params.config.dismissOnTapOutside) {
        Modifier.scrimTapToDismiss(stateData.triggerDismiss)
    } else {
        Modifier
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .then(tapOutside)
    ) {
        if (!params.config.hasExternalBackdrop && !params.config.host.hasExternalBackdrop) {
            AureoleOverlayBackdrop(
                hazeState = effectiveHazeState,
                modifier = Modifier
                    .fillMaxSize()
                    .fadeWith(stateData)
            )
        }
        val density = LocalDensity.current
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val overlayLayout = overlayLayoutFor(
            width = with(density) { maxWidth.toPx() },
            height = screenHeightPx,
            hasPreview = params.previewContent != null
        )
        val restFraction = sheetRestFraction(
            layout = overlayLayout,
            isLandscape = maxWidth > maxHeight,
            host = params.config.host,
            fractions = params.config.restHeightFraction to params.config.expandedHeightFraction
        )
        val bounds = sheetBoundsFor(density, screenHeightPx, restFraction, params.config.expandedHeightFraction)
        val motion = remember { OverlaySheetMotion() }

        val state = AnchoredSheetState(bounds, motion, stateData.isExpanded)
        val maxSheetWidth = params.config.host.maxSheetWidth
        val hiddenFraction = rememberSheetHiddenFraction(stateData)
        // As the sheet expands the preview only fades (D-04): same width, scale and corners, never cut.
        val alpha: () -> Float = { previewAlpha(expandFractionOf(motion, stateData.isExpanded, bounds)) }
        val preview: @Composable () -> Unit = {
            PreviewSlot(previewContent = params.previewContent, alpha = alpha, modifier = Modifier.fadeWith(stateData))
        }
        val sheet: @Composable () -> Unit = {
            SheetSurface(stateData = stateData, params = params, bounds = bounds, motion = motion)
        }
        when (overlayLayout) {
            OverlayLayout.STACKED -> AnchoredPreviewLayout(state, hiddenFraction, alpha, preview, sheet)
            OverlayLayout.SIDE_BY_SIDE -> SideBySidePreviewLayout(state, maxSheetWidth, hiddenFraction, preview, sheet)
        }
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
 * layout pass. The preview follows the sheet while it is dragged, settles, expands or slides in and out with the
 * overlay ([hiddenFraction] of the sheet height below its place), so the room it takes shrinks
 * with the rising sheet. It keeps the size it gets above the sheet at rest: no scale, so it is never cut at the
 * sides (a scaled layer also threw off the clock's Haze glass, RF-G1). Fully faded ([previewAlpha] 0) it is not
 * placed, so it takes no touch.
 */
@Composable
private fun AnchoredPreviewLayout(
    state: AnchoredSheetState,
    hiddenFraction: () -> Float,
    previewAlpha: () -> Float,
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
            if (!isPreviewHidden(previewAlpha())) {
                previewPlaceables.forEach { it.placeRelative(0, sheetTop - previewGapPx - it.height) }
            }
            // Centered: a sheet narrower than the screen (OverlayHostSpec.maxSheetWidth) sits in the middle.
            sheetPlaceables.forEach { it.placeRelative((constraints.maxWidth - it.width) / 2, sheetTop) }
        }
    }
}

@Composable
private fun PreviewSlot(
    previewContent: (@Composable BoxScope.() -> Unit)?,
    alpha: () -> Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha() },
        contentAlignment = Alignment.BottomCenter
    ) {
        previewContent?.invoke(this)
    }
}

/**
 * Inside of the overlay sheet: gesture zone, header (title and logo on one line) and content at the top, bottom bar
 * at the bottom.
 * At rest the sheet hugs content and bottom bar; when it is taller than its content (expanded), the free space sits
 * between content and bottom bar.
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
        // Never filled: at rest the sheet is as tall as what it shows (no empty band above the buttons of a short
        // confirmation, RF-10); expanded, SpaceBetween still puts the free space above the bottom bar. The
        // weight only bounds tall content to the room left by the bottom bar.
        Column(modifier = Modifier.weight(1f, fill = false)) {
            decorations.gestureZone?.invoke()
            if (title != null) {
                val slots = decorations.headerSlots
                val accessory = slots.accessory
                val showAction = hasHeaderAction(
                    showHeaderAction = decorations.showHeaderAction,
                    hasClick = onSettingsClick != null,
                    hasCustomIcon = slots.headerIcon != null || accessory != null
                )
                OverlaySheetHeader(
                    title = OverlayHeaderTitle(title, slots.titleIcon, slots.subtitle),
                    action = when {
                        !showAction -> null
                        accessory != null -> accessory
                        else -> {
                            { OverlaySheetHeaderLogo(onClick = onSettingsClick, headerIcon = slots.headerIcon) }
                        }
                    },
                    isLeftHanded = decorations.isLeftHanded,
                    leadingAction = slots.leadingAction
                )
            }
            if (sheetContent != null) {
                val contentPadding = if (decorations.edgeToEdgeContent) 0.dp else AureoleDS.dimens.small
                Column(
                    modifier = Modifier
                        .padding(horizontal = contentPadding)
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
                    top = AureoleDS.dimens.small,
                    bottom = AureoleDS.dimens.medium
                )
            ) {
                bottomBarContent()
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
