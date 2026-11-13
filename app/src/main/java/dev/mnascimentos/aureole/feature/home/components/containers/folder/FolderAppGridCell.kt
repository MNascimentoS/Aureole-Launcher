package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Marked apps shrink a little while a selection drag runs, so the sweep shows where it went. */
private const val MARKED_WHILE_SELECTING_SCALE = 0.94f
private const val CELL_SCALE_ANIMATION_MS = 100

/**
 * One app of the picker grid: icon on top, name below and a check in the corner when marked. A tap
 * toggles it; screen readers see a checkbox with its state, so the drag is never the only way to mark.
 */
@Composable
internal fun FolderAppGridCell(
    app: AppInfo,
    isMarked: Boolean,
    isSelecting: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = AureoleDS.dimens
    val scale by animateFloatAsState(
        targetValue = if (isSelecting && isMarked) MARKED_WHILE_SELECTING_SCALE else 1f,
        animationSpec = tween(durationMillis = CELL_SCALE_ANIMATION_MS),
        label = "folderGridCellScale"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(dimens.radiusSmall))
            .toggleable(value = isMarked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(vertical = dimens.xxSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.xxSmall)
    ) {
        Box {
            FolderAppGridIcon(app = app, isMarked = isMarked)
            if (isMarked) {
                FolderAppGridCheck(modifier = Modifier.align(Alignment.TopEnd))
            }
        }
        AureoleText(
            text = app.label,
            color = if (isMarked) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(horizontal = dimens.minimal),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

@Composable
private fun FolderAppGridIcon(app: AppInfo, isMarked: Boolean) {
    val dimens = AureoleDS.dimens
    val outline = if (isMarked) {
        Modifier.border(dimens.minimal, AureoleDS.colors.onSurfaceHigh, RoundedCornerShape(dimens.radiusMedium))
    } else {
        Modifier
    }
    AureoleAppIcon(
        app = app,
        modifier = outline.padding(dimens.xxSmall),
        size = dimens.iconHuge,
        params = AureoleAppIconParams(legacyShape = RoundedCornerShape(dimens.radiusSmall))
    )
}

@Composable
private fun FolderAppGridCheck(modifier: Modifier = Modifier) {
    val dimens = AureoleDS.dimens
    Box(
        modifier = modifier
            .size(dimens.iconMediumSmall)
            .background(AureoleDS.colors.onSurfaceHigh, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Check(
            tint = AureoleDS.colors.surface,
            modifier = Modifier.size(dimens.iconXSmall)
        )
    }
}

@AureolePreview
@Composable
private fun FolderAppGridCellPreview() {
    val app = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )
    AureoleLauncherTheme {
        Row(modifier = Modifier.background(AureoleDS.colors.surface)) {
            FolderAppGridCell(
                app = app,
                isMarked = false,
                isSelecting = false,
                onToggle = {},
                modifier = Modifier.weight(1f)
            )
            FolderAppGridCell(
                app = app,
                isMarked = true,
                isSelecting = true,
                onToggle = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}
