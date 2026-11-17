package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.model.AnchoredSheetState
import dev.mnascimentos.aureole.core.designsystem.model.LocalOverlaySheetMaxWidth
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetMotion
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds
import dev.mnascimentos.aureole.core.designsystem.model.sideBySideColumns
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlin.math.roundToInt

/**
 * Landscape layout of an overlay with a preview: the preview in the left column, centered on both axes, and the
 * sheet in the right column, held at the bottom like in portrait (drag, slide and close work the same) and as tall
 * as the screen allows, so nothing is cut. The sheet is at most `LocalOverlaySheetMaxWidth` wide, or
 * `settingsContentMaxWidth` when the screen sets no width.
 */
@Composable
internal fun SideBySidePreviewLayout(
    state: AnchoredSheetState,
    hiddenFraction: () -> Float,
    preview: @Composable () -> Unit,
    sheet: @Composable () -> Unit
) {
    val (bounds, motion) = state
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val safe = WindowInsets.safeDrawing
    val maxSheetWidth = LocalOverlaySheetMaxWidth.current.takeIf { it != Dp.Unspecified }
        ?: AureoleDS.dimens.settingsContentMaxWidth
    val gapPx = with(density) { AureoleDS.dimens.medium.roundToPx() }
    val maxSheetWidthPx = with(density) { maxSheetWidth.roundToPx() }
    Layout(
        contents = listOf(preview, sheet),
        modifier = Modifier.fillMaxSize()
    ) { (previewMeasurables, sheetMeasurables), constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val columns = sideBySideColumns(
            width = width,
            insets = safe.getLeft(density, layoutDirection) to safe.getRight(density, layoutDirection),
            gap = gapPx,
            maxSheetWidth = maxSheetWidthPx
        )
        val sheetConstraints = constraints.copy(minWidth = 0, minHeight = 0, maxWidth = columns.sheetWidth)
        val sheetPlaceables = sheetMeasurables.map { it.measure(sheetConstraints) }
        val sheetHeight = sheetPlaceables.maxOfOrNull { it.height } ?: 0
        if (motion.fixedHeight(state.isExpanded, bounds) == null) motion.restOuterHeight = sheetHeight
        val top = safe.getTop(density)
        val previewConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0,
            maxWidth = columns.previewWidth,
            maxHeight = (height - top - gapPx * 2).coerceAtLeast(0)
        )
        val previewPlaceables = previewMeasurables.map { it.measure(previewConstraints) }

        layout(width, height) {
            val slide = (hiddenFraction() * sheetHeight).roundToInt()
            val sheetTop = height - sheetHeight + motion.translation(bounds).roundToInt() + slide
            previewPlaceables.forEach { placeable ->
                placeable.place(
                    columns.previewStart + (columns.previewWidth - placeable.width) / 2,
                    top + (height - top - placeable.height) / 2
                )
            }
            sheetPlaceables.forEach { it.place(columns.sheetStart + (columns.sheetWidth - it.width) / 2, sheetTop) }
        }
    }
}

private const val PREVIEW_SHEET_PX = 600f

@AureolePreview
@Composable
private fun SideBySidePreviewLayoutPreview() {
    AureoleLauncherTheme {
        SideBySidePreviewLayout(
            state = AnchoredSheetState(
                bounds = SheetBounds(restMaxPx = PREVIEW_SHEET_PX, expandedPx = PREVIEW_SHEET_PX),
                motion = remember { OverlaySheetMotion() },
                isExpanded = false
            ),
            hiddenFraction = { 0f },
            preview = {
                Box(
                    modifier = Modifier
                        .size(AureoleDS.dimens.sizeExtraLarge)
                        .background(AureoleDS.colors.surfaceVariant)
                )
            },
            sheet = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AureoleDS.dimens.sizeExtraLarge)
                        .background(AureoleDS.colors.surface)
                )
            }
        )
    }
}
