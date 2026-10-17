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
 * @param params Additional parameters for configuring the menu item.
 */
@Suppress("LongMethod") // AureolePopupMenuItem provides action menu item with custom icon, badge, and trailing slots
@Composable
fun AureolePopupMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    params: AureolePopupMenuItemParams = AureolePopupMenuItemParams(),
) {
    val badge = params.badge
    val onTrailingClick = params.onTrailingClick
    val isDestructive = params.isDestructive
    val icon = params.icon
    val customIcon = params.customIcon
    val trailingIcon = params.trailingIcon
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
                color = textColor,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
        ) {
            if (!badge.isNullOrBlank()) {
                AureoleText(
                    text = badge,
                    color = AureoleDS.colors.onSurfaceLow,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
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

/**
 * Parameters for [AureolePopupMenuItem].
 *
 * @param badge The badge text to display.
 * @param onTrailingClick Executes when the trailing icon is clicked.
 * @param isDestructive Whether the action is destructive, which applies error coloring.
 * @param icon The icon slot of the menu item.
 * @param customIcon Custom content slot for the icon.
 * @param trailingIcon The trailing icon slot of the menu item.
 */
class AureolePopupMenuItemParams(
    val badge: String? = null,
    val onTrailingClick: (() -> Unit)? = null,
    val isDestructive: Boolean = false,
    val icon: @Composable ((Modifier) -> Unit)? = null,
    val customIcon: (@Composable () -> Unit)? = null,
    val trailingIcon: @Composable ((Modifier) -> Unit)? = null
)

private val PREVIEW_POPUP_WIDTH = 260.dp

@AureolePreview
@Composable
private fun AureolePopupPreview() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(PREVIEW_POPUP_WIDTH),
        ) {
            AureolePopupHeader(
                title = "Folder name",
                params = AureolePopupHeaderParams(
                    sessionTitle = "Folder actions",
                    onSettingsClick = {},
                    onCloseClick = {},
                )
            )

            AureolePopupMenuSection(title = "Titulo de sessão")

            AureolePopupMenuItem(
                title = "Add folder",
                onClick = {},
                params = AureolePopupMenuItemParams(
                    icon = { modifier -> AureoleDS.icons.Settings(modifier, AureoleDS.colors.onSurfaceHigh) },
                    badge = "12",
                )
            )

            AureolePopupMenuItem(
                title = "Create stack",
                onClick = {},
                params = AureolePopupMenuItemParams(
                    badge = "36",
                )
            )

            AureolePopupMenuItem(
                title = "Remove",
                onClick = {},
                params = AureolePopupMenuItemParams(
                    isDestructive = true,
                )
            )
        }
    }
}
