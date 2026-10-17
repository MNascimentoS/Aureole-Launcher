package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Standard Popup Menu Action Row Item with icon, title, optional badge, or trailing action button.
 *
 * @param title The title of the menu item.
 * @param onClick Executes when the item is clicked.
 * @param modifier The modifier to be applied to the layout.
 * @param badge The badge text to display.
 * @param onTrailingClick Executes when the trailing icon is clicked.
 * @param isDestructive Whether the action is destructive, which applies error coloring.
 * @param icon The icon slot of the menu item.
 * @param customIcon Custom content slot for the icon.
 * @param trailingIcon The trailing icon slot of the menu item.
 */
@Suppress(
    "LongParameterList",
    "LongMethod"
) // AureolePopupMenuItem provides action menu item with custom icon, badge, and trailing slots
@Composable
fun AureolePopupMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    isDestructive: Boolean = false,
    icon: @Composable ((Modifier) -> Unit)? = null,
    customIcon: (@Composable () -> Unit)? = null,
    trailingIcon: @Composable ((Modifier) -> Unit)? = null,
) {
    val textColor = if (isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceHigh

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .clickable(onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                customIcon != null -> customIcon()
                icon != null -> icon(Modifier.size(AureoleDS.dimens.iconMedium))
            }

            if (icon != null || customIcon != null) {
                Spacer(modifier = Modifier.width(AureoleDS.dimens.iconMedium))
            }

            AureoleText(
                text = title,
                style = AureoleDS.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
        ) {
            if (!badge.isNullOrBlank()) {
                AureoleText(
                    text = badge,
                    style = AureoleDS.typography.labelMedium,
                    color = AureoleDS.colors.onSurfaceLow,
                )
            }

            if (trailingIcon != null) {
                IconButton(
                    onClick = { (onTrailingClick ?: onClick).invoke() },
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                ) {
                    trailingIcon(Modifier.size(AureoleDS.dimens.iconSmall))
                }
            }
        }
    }
}

@AureolePreview
@Composable
private fun AureolePopupPreview() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(260.dp),
        ) {
            AureolePopupHeader(
                title = "Folder name",
                sessionTitle = "Folder actions",
                onSettingsClick = {},
                onCloseClick = {},
            )

            AureolePopupMenuSection(title = "Titulo de sessão")

            AureolePopupMenuItem(
                title = "Add folder",
                icon = { modifier -> AureoleDS.icons.Settings(modifier, AureoleDS.colors.onSurfaceHigh) },
                badge = "12",
                onClick = {},
            )

            AureolePopupMenuItem(
                title = "Create stack",
                badge = "36",
                onClick = {},
            )

            AureolePopupMenuItem(
                title = "Remove",
                isDestructive = true,
                onClick = {},
            )
        }
    }
}
