package dev.mnascimentos.aureole.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

@Composable
fun AdvancedIcon(
    modifier: Modifier = Modifier,
    tint: Color = AureoleTheme.colors.onSurfaceMedium
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxxSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.size(AureoleDS.dimens.iconMedium)
    ) {
        Box(Modifier.size(AureoleDS.dimens.xxSmall).background(tint, CircleShape))
        Box(Modifier.size(AureoleDS.dimens.xxSmall).background(tint, CircleShape))
        Box(Modifier.size(AureoleDS.dimens.xxSmall).background(tint, CircleShape))
    }
}

@Composable
fun SettingsHugeTitle(text: String, modifier: Modifier = Modifier) {
    AureoleText(
        text = text,
        style = AureoleTheme.typography.displayMedium,
        fontWeight = FontWeight.Bold,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = modifier.padding(vertical = AureoleDS.dimens.medium)
    )
}

@Composable
fun SettingsMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            Box(
                modifier = Modifier
                    .padding(end = AureoleDS.dimens.large)
                    .size(AureoleDS.dimens.iconMedium),
                contentAlignment = Alignment.Center
            ) {
                leadingContent()
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = title,
                style = AureoleTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = AureoleTheme.colors.onSurfaceMedium
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(AureoleDS.dimens.xxxSmall))
                AureoleText(
                    text = subtitle,
                    style = AureoleTheme.typography.bodySmall,
                    color = AureoleTheme.colors.onSurfaceLow
                )
            }
        }
    }
}

@Composable
fun SettingsToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            Box(
                modifier = Modifier
                    .padding(end = AureoleDS.dimens.large)
                    .size(AureoleDS.dimens.iconMedium),
                contentAlignment = Alignment.Center
            ) {
                leadingContent()
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = title,
                style = AureoleTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        }

        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AureoleTheme.colors.surface,
                checkedTrackColor = AureoleTheme.colors.onSurfaceMedium,
                uncheckedThumbColor = AureoleTheme.colors.outline,
                uncheckedTrackColor = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    leadingContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable { onClick() }
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            Box(
                modifier = Modifier
                    .padding(end = AureoleDS.dimens.large)
                    .size(AureoleDS.dimens.iconMedium),
                contentAlignment = Alignment.Center
            ) {
                leadingContent()
            }
        }
        AureoleText(
            text = title,
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
