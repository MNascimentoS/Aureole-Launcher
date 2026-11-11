package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.reorder.model.LocalReorderHandle
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.DragHandle
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

@Composable
fun FavoriteAppRowInfo(app: AppInfo, modifier: Modifier = Modifier) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(end = AureoleDS.dimens.xSmall)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(AureoleDS.dimens.xLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

/** The ✕ (40 dp) that takes the app out of the favorites. */
@Composable
fun FavoriteAppRemoveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.home_favorites_remove)
    Box(
        modifier = modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .semantics { contentDescription = description }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Close(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
    }
}

/**
 * The 6-dot handle (44 x 38 dp) of a favorite: hold it and drag to reorder. Inside a list with
 * `dragHandleOnly` it starts the drag of its row and lights up while the row is held.
 */
@Composable
fun FavoriteAppDragHandle(modifier: Modifier = Modifier) {
    val handle = LocalReorderHandle.current
    val description = stringResource(R.string.home_favorites_manage_drag_handle)
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
    val background = if (handle.isDragging) {
        AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.25f)
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f)
    }
    Box(
        modifier = modifier
            .size(width = HANDLE_WIDTH, height = HANDLE_HEIGHT)
            .clip(shape)
            .background(background)
            .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = 0.5f), shape)
            .then(handle.gestureModifier)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.DragHandle(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

/** An app that is not a favorite yet: tapping the row (or the + on its icon) adds it. */
@Composable
fun RemainingAppRow(
    app: AppInfo,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    val plusDesc = stringResource(R.string.favorites_dialog_plus_content_desc)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .clickable(onClickLabel = plusDesc, role = Role.Button, onClick = onToggleFavorite)
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Image(
                bitmap = iconBitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = AureoleDS.dimens.xxSmall, y = -AureoleDS.dimens.xxSmall)
                    .size(AureoleDS.dimens.medium)
                    .background(AureoleDS.colors.badgeContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Plus(
                    tint = AureoleDS.colors.onError,
                    modifier = Modifier.size(AureoleDS.dimens.iconXSmall)
                )
            }
        }
        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

private val HANDLE_WIDTH = 44.dp
private val HANDLE_HEIGHT = 38.dp
