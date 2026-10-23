package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
internal fun SettingsRow(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    value: String? = null,
    textColor: Color = AureoleDS.colors.onSurfaceHigh,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconMedium))
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            color = textColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            ),
            modifier = Modifier.weight(1f)
        )
        if (value != null) {
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
            AureoleText(
                text = value,
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
        }
    }
}

@AureolePreview
@Composable
private fun SettingsRowPreview() {
    AureoleLauncherTheme {
        SettingsRow(
            title = "Test row",
            icon = { m -> AureoleDS.icons.Edit(m) },
            value = "Value",
            onClick = {}
        )
    }
}
