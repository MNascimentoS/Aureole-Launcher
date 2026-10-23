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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.model.AureolePopupMenuItemParams
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
@Composable
fun AureolePopupMenuItem(
    title: String,
    modifier: Modifier = Modifier,
    params: AureolePopupMenuItemParams = AureolePopupMenuItemParams(),
    onClick: () -> Unit = {},
) {
    val textColor = if (params.isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceHigh

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
        AureolePopupMenuItemLeading(
            title = title,
            textColor = textColor,
            icon = params.icon,
            customIcon = params.customIcon,
            modifier = Modifier.weight(1f)
        )

        AureolePopupMenuItemTrailing(
            badge = params.badge,
            trailingIcon = params.trailingIcon,
            onTrailingClick = params.onTrailingClick ?: onClick
        )
    }
}

@Composable
private fun AureolePopupMenuItemLeading(
    title: String,
    textColor: Color,
    icon: (@Composable (Modifier) -> Unit)?,
    customIcon: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
}

@Composable
private fun AureolePopupMenuItemTrailing(
    badge: String?,
    trailingIcon: (@Composable (Modifier) -> Unit)?,
    onTrailingClick: () -> Unit,
) {
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
                onClick = onTrailingClick,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
            ) {
                trailingIcon(Modifier.size(AureoleDS.dimens.iconSmall))
            }
        }
    }
}

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
