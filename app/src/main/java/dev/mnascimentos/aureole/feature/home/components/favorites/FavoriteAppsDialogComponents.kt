package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppRowParams

@Composable
fun FavoriteAppsDialogHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(bottom = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = "Lista de Aplicativos",
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
fun FavoriteAppsShowAllSwitchRow(
    showAllAppsOnHome: Boolean,
    onToggleShowAllAppsOnHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = "Mostrar todos os apps na Home",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
            AureoleText(
                text = "Se desativado, exibe apenas os aplicativos favoritados",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }
        Switch(
            checked = showAllAppsOnHome,
            onCheckedChange = { onToggleShowAllAppsOnHome() }
        )
    }
}

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

@Composable
fun FavoriteAppRowActionButtons(
    params: FavoriteAppRowParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        if (params.canReorder && params.index > 0) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.large)
                    .background(
                        color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                    )
                    .clickable { onMoveUp() },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
        if (params.canReorder && params.index < params.totalCount - 1) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.large)
                    .background(
                        color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                    )
                    .clickable { onMoveDown() },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.large)
                .background(
                    color = AureoleDS.colors.error,
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                )
                .clickable { onToggleFavorite() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.onError,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
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

        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.large)
                .background(
                    color = AureoleDS.colors.surfaceVariant,
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                )
                .clickable { onToggleFavorite() },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Plus(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsDialogComponentsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            FavoriteAppsDialogHeader()
            FavoriteAppsShowAllSwitchRow(
                showAllAppsOnHome = true,
                onToggleShowAllAppsOnHome = {}
            )
        }
    }
}
