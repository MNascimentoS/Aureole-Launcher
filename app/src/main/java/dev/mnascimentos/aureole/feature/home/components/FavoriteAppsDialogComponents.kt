package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.model.FavoriteAppRowParams

@Composable
fun FavoriteAppsDialogHeader(
    containerId: String?
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val titleText = if (containerId != null) {
            "Editar Favoritos do Container"
        } else {
            "Editar Favoritos da Tela Inicial"
        }
        AureoleText(
            text = titleText,
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FavoriteAppsShowAllSwitchRow(
    showAllAppsOnHome: Boolean,
    onToggleShowAllAppsOnHome: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = "Mostrar todos os apps na Home",
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            AureoleText(
                text = "Se desativado, exibe apenas os aplicativos favoritados",
                style = AureoleDS.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
        AureoleText(
            text = app.label,
            style = AureoleDS.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FavoriteAppRowActionButtons(
    params: FavoriteAppRowParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (params.canReorder) {
            val upTint = if (params.index > 0) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
            IconButton(
                onClick = onMoveUp,
                enabled = params.index > 0,
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = upTint
                )
            }

            val downTint = if (params.index < params.totalCount - 1) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
            IconButton(
                onClick = onMoveDown,
                enabled = params.index < params.totalCount - 1,
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = downTint
                )
            }
        }

        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp)
        ) {
            AureoleDS.icons.Close(
                tint = MaterialTheme.colorScheme.error
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
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .padding(end = AureoleDS.dimens.xSmall)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = app.label,
                style = AureoleDS.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(32.dp)
        ) {
            AureoleDS.icons.Add(
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
