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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Standard Popup Menu Action Row Item with leading icon, action title, optional badge, or trailing action button.
 *
 * @param title The primary title text for the menu action item.
 * @param modifier The [Modifier] applied to the item row layout. First optional parameter.
 * @param params Additional configuration parameters provided via [AureolePopupMenuItemParams].
 * @param onClick Action callback executed when the item row is clicked. Slot parameter placed last.
 */
@Suppress("LongMethod") // AureolePopupMenuItem provides action menu item with custom icon, badge, and trailing slots
@Composable
fun AureolePopupMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    params: AureolePopupMenuItemParams = AureolePopupMenuItemParams(),
    onClick: () -> Unit = {},
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
            .height(48.dp)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
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
 * Configuration parameters for [AureolePopupMenuItem].
 *
 * @param badge Optional text label displayed on the right side of the item.
 * @param onTrailingClick Optional callback for clicking the trailing icon separately from item click.
 * @param isDestructive When true, applies error tint styling to indicate destructive actions.
 * @param icon Slot for rendering a standard icon provided with a [Modifier].
 * @param customIcon Custom `@Composable` slot for rendering custom leading icon content.
 * @param trailingIcon Slot for rendering a trailing icon provided with a [Modifier].
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
