package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.DragHandle
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Fixed tip of the manage list, shown with two or more favorites: how to reorder them. */
@Composable
internal fun FavoriteAppsDragHint(modifier: Modifier = Modifier) {
    FavoriteAppsNotice(
        text = stringResource(R.string.home_favorites_manage_drag_hint),
        modifier = modifier
    ) {
        AureoleDS.icons.DragHandle(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

/** Banner of the manage list without favorites: the Home shows all [appCount] apps. */
@Composable
internal fun FavoriteAppsEmptyBanner(appCount: Int, modifier: Modifier = Modifier) {
    FavoriteAppsNotice(
        text = stringResource(R.string.home_favorites_manage_empty_banner, appCount),
        modifier = modifier
    ) {
        AureoleDS.icons.AllApps(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

@Composable
private fun FavoriteAppsNotice(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
            .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = 0.35f), shape)
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
        AureoleText(
            text = text,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsManageHintsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            FavoriteAppsDragHint()
            FavoriteAppsEmptyBanner(appCount = 34)
        }
    }
}
