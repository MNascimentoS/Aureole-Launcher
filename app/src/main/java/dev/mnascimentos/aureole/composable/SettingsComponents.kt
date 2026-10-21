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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun AdvancedIcon(
    modifier: Modifier = Modifier,
    tint: Color = AureoleDS.colors.onSurfaceMedium
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
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
        color = AureoleDS.colors.onSurfaceHigh,
        modifier = modifier.padding(vertical = AureoleDS.dimens.medium),
        format = AureoleTextFormatParams(style = AureoleDS.typography.displayMedium, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun SettingsMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
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
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(AureoleDS.dimens.minimal))
                AureoleText(
                    text = subtitle,
                    color = AureoleDS.colors.onSurfaceLow,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
                )
            }
        }

        if (trailingContent != null) {
            trailingContent()
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
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = { onCheckedChange(!checked) },
                role = Role.Switch
            )
            .semantics(mergeDescendants = true) {}
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
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
            )
        }

        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AureoleDS.colors.surface,
                checkedTrackColor = AureoleDS.colors.onSurfaceMedium,
                uncheckedThumbColor = AureoleDS.colors.outline,
                uncheckedTrackColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingsActionItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
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
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, fontWeight = FontWeight.Normal)
        )
    }
}

@AureolePreview
@Composable
private fun SettingsComponentsPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            SettingsHugeTitle(text = "Settings")
            SettingsMenuItem(title = "General", subtitle = "App preferences", onClick = {})
            SettingsToggleItem(title = "Enable Feature", checked = true, onCheckedChange = {})
            SettingsActionItem(title = "Reset All", onClick = {})
        }
    }
}
