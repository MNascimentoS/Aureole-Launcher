package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Velocity
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetController
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetEvents
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetStateData
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlin.math.roundToInt

private const val PREVIEW_REST_MAX_PX = 900f
private const val PREVIEW_EXPANDED_PX = 1400f

/**
 * Bottom sheet of [AureoleEditOverlay].
 *
 * At rest it hugs its content up to [SheetBounds.restMaxPx]; expanded it takes [SheetBounds.expandedPx].
 * Dragging the sheet, or scrolling its content past the edge, resizes it; on release it snaps with
 * [OverlaySheetController]. The content stays at the top and the bottom bar at the bottom of the sheet.
 */
@Composable
internal fun SheetSurface(
    stateData: OverlaySheetStateData,
    params: AureoleEditOverlayParams,
    bounds: SheetBounds,
    motion: OverlaySheetMotion,
    modifier: Modifier = Modifier
) {
    val controller = rememberSheetController(stateData, bounds, motion)
    val nestedScrollConnection = remember(controller) { sheetNestedScrollConnection(controller) }
    val draggableState = rememberDraggableState { delta -> controller.onDrag(delta) }
    val isExpanded = stateData.isExpanded
    val config = params.config

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
            .sheetHeight(motion, isExpanded, bounds)
            .onSizeChanged { size ->
                if (motion.fixedHeight(isExpanded, bounds) == null) motion.restHeight = size.height.toFloat()
            }
            .nestedScroll(nestedScrollConnection)
            .draggable(
                state = draggableState,
                orientation = Orientation.Vertical,
                onDragStarted = { controller.onDragStarted() },
                onDragStopped = { velocity -> controller.onRelease(velocity) }
            )
            // Swallows taps so they do not reach the scrim, without merging the sheet into one a11y node.
            .pointerInput(Unit) { detectTapGestures() },
        shape = RoundedCornerShape(
            topStart = AureoleDS.dimens.radiusLarge,
            topEnd = AureoleDS.dimens.radiusLarge
        ),
        color = AureoleDS.colors.surface,
        contentColor = AureoleDS.colors.onSurfaceMedium
    ) {
        OverlaySheetBody(
            title = config.title,
            onSettingsClick = config.onSettingsClick,
            bottomBarContent = params.bottomBarContent,
            sheetContent = params.sheetContent,
            decorations = OverlaySheetDecorations(
                headerIcon = config.headerIcon,
                handle = {
                    OverlaySheetHandle(
                        isExpanded = isExpanded,
                        isLeftHanded = config.isLeftHanded,
                        onToggleExpanded = controller::toggle,
                        onSettingsClick = config.onSettingsClick
                    )
                }
            )
        )
    }
}

@Composable
private fun rememberSheetController(
    stateData: OverlaySheetStateData,
    bounds: SheetBounds,
    motion: OverlaySheetMotion
): OverlaySheetController {
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val density = LocalDensity.current.density
    return remember(stateData, bounds, motion, scope, haptics, density) {
        OverlaySheetController(
            motion = motion,
            bounds = bounds,
            isExpanded = stateData.isExpanded,
            events = OverlaySheetEvents(
                onExpandedChanged = stateData.onExpandedChanged,
                onDismiss = stateData.triggerDismiss,
                onTick = { haptics.performHapticFeedback(HapticFeedbackType.SegmentTick) }
            ),
            scope = scope,
            density = density
        )
    }
}

/**
 * Sizes the sheet in the layout phase, so a drag only relayouts it: a fixed height while it is held,
 * settling or expanded, and up to the rest height (hugging the content) otherwise.
 */
private fun Modifier.sheetHeight(motion: OverlaySheetMotion, isExpanded: Boolean, bounds: SheetBounds): Modifier {
    return layout { measurable, constraints ->
        val fixed = motion.fixedHeight(isExpanded, bounds)?.roundToInt()?.coerceAtMost(constraints.maxHeight)
        val childConstraints = if (fixed == null) {
            constraints.copy(maxHeight = bounds.restMaxPx.roundToInt().coerceAtMost(constraints.maxHeight))
        } else {
            constraints.copy(minHeight = fixed, maxHeight = fixed)
        }
        val placeable = measurable.measure(childConstraints)
        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

/**
 * Lets the content scroll resize the sheet: scrolling up grows it to the expanded height before the
 * content scrolls, and pulling down at the top of the content shrinks it, down to closing.
 */
private fun sheetNestedScrollConnection(controller: OverlaySheetController): NestedScrollConnection {
    return object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val canGrow = available.y < 0f && controller.currentHeight() < controller.expandedHeight()
            if (source != NestedScrollSource.UserInput || !canGrow) return Offset.Zero
            return Offset(0f, controller.scrollBy(available.y))
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            val canShrink = available.y > 0f && controller.currentHeight() > 0f
            if (source != NestedScrollSource.UserInput || !canShrink) return Offset.Zero
            return Offset(0f, controller.scrollBy(available.y))
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (!controller.isHeld) return Velocity.Zero
            controller.onRelease(available.y)
            return available
        }
    }
}

@Composable
private fun SheetSurfacePreviewContent(isExpanded: Boolean) {
    AureoleLauncherTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = true, modifier = Modifier.align(Alignment.BottomCenter)) {
                SheetSurface(
                    stateData = OverlaySheetStateData(
                        isExpanded = isExpanded,
                        onExpandedChanged = {},
                        triggerDismiss = {},
                        effectiveAnimated = false,
                        animScope = this
                    ),
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            title = "Preview Title",
                            onSettingsClick = {}
                        ),
                        previewContent = { AureoleText("Preview Content") },
                        bottomBarContent = { AureoleText("Bottom Bar") },
                        sheetContent = { AureoleText("Sheet Content") }
                    ),
                    bounds = SheetBounds(restMaxPx = PREVIEW_REST_MAX_PX, expandedPx = PREVIEW_EXPANDED_PX),
                    motion = remember { OverlaySheetMotion() }
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun SheetSurfaceDefaultPreview() {
    SheetSurfacePreviewContent(isExpanded = false)
}

@AureolePreview
@Composable
private fun SheetSurfaceExpandedPreview() {
    SheetSurfacePreviewContent(isExpanded = true)
}
