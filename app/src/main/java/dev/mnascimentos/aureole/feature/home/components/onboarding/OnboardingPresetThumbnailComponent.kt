package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.thumbnailItems
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPresetMetrics
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

private const val CLOCK_BAR_WIDTH = 0.5f
private const val CLOCK_BAR_HEIGHT = 0.3f
private const val APP_ROW_WIDTH = 0.6f
private const val APP_ROWS = 5
private const val HALF = 0.5f

/** The portrait home of a preset, drawn small: one rounded block per item, with a hint of what it shows. */
@Composable
fun OnboardingPresetThumbnail(
    items: List<LauncherItemState>,
    modifier: Modifier = Modifier
) {
    val block = AureoleDS.colors.onSurfaceHigh.copy(alpha = OnboardingPresetMetrics.THUMBNAIL_ITEM_ALPHA)
    val detail = AureoleDS.colors.onSurfaceHigh.copy(alpha = OnboardingPresetMetrics.THUMBNAIL_DETAIL_ALPHA)
    val outline = AureoleDS.colors.onSurfaceLow
    val radius = AureoleDS.dimens.radiusMinimal
    val gap = OnboardingPresetMetrics.THUMBNAIL_GAP
    Canvas(
        modifier = modifier
            .width(OnboardingPresetMetrics.THUMBNAIL_WIDTH)
            .aspectRatio(GridEngineUtils.PORTRAIT_MAX_COLS.toFloat() / GridEngineUtils.PORTRAIT_MAX_ROWS)
    ) {
        val corner = CornerRadius(radius.toPx())
        drawRoundRect(color = outline, cornerRadius = corner, style = Stroke())
        val cell = Size(size.width / GridEngineUtils.PORTRAIT_MAX_COLS, size.height / GridEngineUtils.PORTRAIT_MAX_ROWS)
        val gapPx = gap.toPx()
        items.forEach { item ->
            val topLeft = Offset(item.col * cell.width + gapPx, item.row * cell.height + gapPx)
            val itemSize = Size(item.colSpan * cell.width - gapPx * 2, item.rowSpan * cell.height - gapPx * 2)
            drawRoundRect(color = block, topLeft = topLeft, size = itemSize, cornerRadius = corner)
            drawItemDetail(item.safeType, topLeft, itemSize, detail)
        }
    }
}

private fun DrawScope.drawItemDetail(type: LauncherItemType, topLeft: Offset, itemSize: Size, color: Color) {
    when (type) {
        LauncherItemType.CLOCK -> {
            val bar = Size(itemSize.width * CLOCK_BAR_WIDTH, itemSize.height * CLOCK_BAR_HEIGHT)
            drawRect(
                color = color,
                topLeft = Offset(
                    topLeft.x + (itemSize.width - bar.width) * HALF,
                    topLeft.y + (itemSize.height - bar.height) * HALF
                ),
                size = bar
            )
        }
        LauncherItemType.APPS_LIST -> {
            val rowHeight = itemSize.height / (APP_ROWS * 2)
            repeat(APP_ROWS) { index ->
                drawRect(
                    color = color,
                    topLeft = Offset(topLeft.x + rowHeight, topLeft.y + rowHeight * (index * 2 + HALF)),
                    size = Size(itemSize.width * APP_ROW_WIDTH, rowHeight)
                )
            }
        }
        else -> Unit
    }
}

@AureolePreview
@Composable
private fun OnboardingPresetThumbnailPreview() {
    AureoleLauncherTheme {
        OnboardingPresetThumbnail(items = LayoutPreset.PRODUCTIVE.thumbnailItems(plan = null, isLeftHanded = false))
    }
}
