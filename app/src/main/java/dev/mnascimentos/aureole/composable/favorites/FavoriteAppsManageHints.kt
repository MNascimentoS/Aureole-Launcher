package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesEmptyNotice
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * One-line notice of the list without favorites (RF-03): with [FavoritesEmptyNotice.HOME_SHOWS_ALL] the Home shows
 * all [appCount] apps, with [FavoritesEmptyNotice.HOME_STAYS_EMPTY] the list stays empty there.
 */
@Composable
internal fun FavoriteAppsEmptyNotice(
    notice: FavoritesEmptyNotice,
    appCount: Int,
    modifier: Modifier = Modifier
) {
    val text = when (notice) {
        FavoritesEmptyNotice.HOME_SHOWS_ALL ->
            pluralStringResource(R.plurals.home_favorites_empty_notice_all, appCount, appCount)
        FavoritesEmptyNotice.HOME_STAYS_EMPTY -> stringResource(R.string.home_favorites_empty_notice_empty)
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Info(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
        AureoleText(
            text = text,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            FavoriteAppsEmptyNotice(notice = FavoritesEmptyNotice.HOME_SHOWS_ALL, appCount = 34)
            FavoriteAppsEmptyNotice(notice = FavoritesEmptyNotice.HOME_STAYS_EMPTY, appCount = 34)
        }
    }
}
