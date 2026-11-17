package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupBounds
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.folderPopupPlacement
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.gridFolderPopupPlacement
import kotlin.math.roundToInt

/**
 * Places the opened folder over the whole home (RF-15, RF-16): measured with at most the room between the status bar
 * and the margin above the navigation bar, then opened below [anchorTopPx] when it fits there, or moved up until it
 * does. A grid folder is centered in that room. The list folder sits at the end side, or the start side
 * when [isLeftHanded]; its own padding keeps it off the edge.
 */
@Composable
internal fun OpenedFolderPlacementLayout(
    anchorTopPx: Float,
    isGridFolder: Boolean,
    isLeftHanded: Boolean,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val statusInsetPx = WindowInsets.statusBars.getTop(density).toFloat()
    val navInsetPx = WindowInsets.navigationBars.getBottom(density).toFloat()
    val marginPx = with(density) { AureoleDS.dimens.medium.toPx() }
    val listCapPx = with(density) { FolderPopupMetrics.HeightMax.toPx() }
    Layout(content = content, modifier = Modifier.fillMaxSize()) { measurables, constraints ->
        val screenHeightPx = constraints.maxHeight.toFloat()
        val bounds = FolderPopupBounds(
            screenHeightPx = screenHeightPx,
            statusInsetPx = statusInsetPx,
            navInsetPx = navInsetPx,
            marginPx = marginPx,
            // The grid caps its own app area; here it only has to fit the screen.
            heightCapPx = if (isGridFolder) screenHeightPx else listCapPx
        )
        val childConstraints = constraints.copy(minWidth = 0, minHeight = 0, maxHeight = bounds.maxHeightPx.toInt())
        val placeables = measurables.map { it.measure(childConstraints) }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                val placement = if (isGridFolder) {
                    gridFolderPopupPlacement(placeable.height.toFloat(), bounds)
                } else {
                    folderPopupPlacement(anchorTopPx, placeable.height.toFloat(), bounds)
                }
                val x = when {
                    isGridFolder -> (constraints.maxWidth - placeable.width) / 2
                    isLeftHanded -> 0
                    else -> constraints.maxWidth - placeable.width
                }
                placeable.placeRelative(x, placement.topPx.roundToInt())
            }
        }
    }
}
