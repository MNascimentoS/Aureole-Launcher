package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val CARD_FILL_ALPHA = 0.45f

/**
 * A card of the Settings root: icon, title and subtitle on a rounded card. No chevron and no value tags: the whole
 * card is the button that opens the area, announced as "open panel" (it opens a sheet).
 */
@Composable
internal fun SettingsAreaCard(
    icon: @Composable (Modifier, Color) -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = CARD_FILL_ALPHA))
            .clickable(
                onClickLabel = stringResource(R.string.settings_root_open_panel),
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconLarge), AureoleDS.colors.onSurfaceHigh)
        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = title,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            )
            AureoleText(
                text = subtitle,
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}

@AureolePreview
@Composable
private fun SettingsAreaCardPreview() {
    AureoleLauncherTheme {
        SettingsAreaCard(
            icon = { m, c -> AureoleDS.icons.Edit(modifier = m, tint = c) },
            title = "Appearance",
            subtitle = "Colors, background, font, and glass",
            onClick = {},
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
