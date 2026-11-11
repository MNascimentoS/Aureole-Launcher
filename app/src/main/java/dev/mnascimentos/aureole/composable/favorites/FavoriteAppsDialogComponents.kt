package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
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

/** Red button that takes the app out of the favorites. */
@Composable
fun FavoriteAppRemoveButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.home_favorites_remove)
    Box(
        modifier = modifier
            .size(AureoleDS.dimens.large)
            .background(color = AureoleDS.colors.error, shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .semantics { contentDescription = description }
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Delete(
            tint = AureoleDS.colors.onError,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
    }
}

@Composable
fun RemainingAppRow(
    app: AppInfo,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
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

        val plusDesc = stringResource(R.string.favorites_dialog_plus_content_desc)
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.large)
                .background(
                    color = AureoleDS.colors.surfaceVariant,
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                )
                .semantics { contentDescription = plusDesc }
                .clickable(role = Role.Button) { onToggleFavorite() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Plus(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}
