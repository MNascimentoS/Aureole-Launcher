package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
internal fun FavoritesEmptyHint(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AureoleDS.icons.Star(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMediumSmall)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = stringResource(R.string.home_favorites_tap_to_select),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}

@Composable
internal fun AllAppsDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f)
        )
        AureoleText(
            text = stringResource(R.string.home_favorites_all_apps_divider),
            color = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f)
        )
    }
}

@AureolePreview
@Composable
private fun FavoritesEmptyHintPreview() {
    AureoleLauncherTheme {
        FavoritesEmptyHint(onClick = {})
    }
}
