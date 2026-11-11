package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val CHEVRON_ROTATION = 90f
private const val DESTRUCTIVE_TILE_ALPHA = 0.18f
private const val NOTE_ALPHA = 0.35f

/**
 * A 56 dp menu row: optional icon column, title and an optional chevron. Mirrored for left-handed users so the
 * icon sits at the thumb side. Destructive rows use the error color.
 */
@Composable
fun AureoleMenuRow(
    params: MenuRowParams,
    modifier: Modifier = Modifier
) {
    val titleColor = if (params.isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceMedium
    LeftHandMirror {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = AureoleDS.dimens.menuRowHeight)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .clickable(role = Role.Button, onClick = params.onClick)
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = AureoleDS.dimens.xxSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            params.icon?.let { icon -> MenuIconColumn(icon = icon, isDestructive = params.isDestructive) }
            MenuRowTexts(params = params, titleColor = titleColor, modifier = Modifier.weight(1f))
            if (params.showChevron) MenuRowChevron()
        }
    }
}

@Composable
private fun MenuRowTexts(params: MenuRowParams, titleColor: Color, modifier: Modifier = Modifier) {
    val subtitle = params.subtitle
    Column(modifier = modifier) {
        AureoleText(
            text = params.title,
            color = if (subtitle != null && !params.isDestructive) AureoleDS.colors.onSurfaceHigh else titleColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyLarge.copy(textDirection = TextDirection.Content),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        )
        if (subtitle != null) {
            AureoleText(
                text = subtitle,
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodySmall.copy(textDirection = TextDirection.Content),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
        }
    }
}

/**
 * Fixed 36 dp column that centers a 22 dp icon, so every title in a list starts at the same x no matter the icon.
 * A destructive row keeps a tinted tile behind the icon.
 */
@Composable
internal fun MenuIconColumn(
    icon: @Composable (Modifier, Color) -> Unit,
    modifier: Modifier = Modifier,
    isDestructive: Boolean = false
) {
    val iconTint = if (isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceMedium
    val tileModifier = if (isDestructive) {
        Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(AureoleDS.colors.error.copy(alpha = DESTRUCTIVE_TILE_ALPHA))
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(AureoleDS.dimens.iconXLarge)
            .then(tileModifier),
        contentAlignment = Alignment.Center
    ) {
        icon(Modifier.size(AureoleDS.dimens.iconLargePlus), iconTint)
    }
}

@Composable
private fun MenuRowChevron() {
    // The row is mirrored for left-handed users, so the chevron turns the other way.
    val rotation = if (LocalLayoutDirection.current == LayoutDirection.Rtl) CHEVRON_ROTATION else -CHEVRON_ROTATION
    AureoleDS.icons.ArrowDown(
        tint = AureoleDS.colors.onSurfaceLow,
        modifier = Modifier
            .size(AureoleDS.dimens.iconSmall)
            .rotate(rotation)
    )
}

/** Small caps section label above a group of rows ("ACTIONS", "FOLDER"). */
@Composable
fun AureoleMenuSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    LeftHandMirror {
        AureoleText(
            text = text.uppercase(),
            color = AureoleDS.colors.onSurfaceLow,
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = AureoleDS.dimens.xxSmall,
                    end = AureoleDS.dimens.xxSmall,
                    top = AureoleDS.dimens.small,
                    bottom = AureoleDS.dimens.xSmall
                ),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall.copy(textDirection = TextDirection.Content),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

/** Read-only centered note, such as the current size of a grid item. */
@Composable
fun AureoleMenuNote(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = NOTE_ALPHA))
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = text,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyMedium.copy(textDirection = TextDirection.Content),
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleMenuRowsPreview() {
    AureoleLauncherTheme {
        Column(modifier = Modifier.padding(AureoleDS.dimens.medium)) {
            AureoleMenuRow(
                MenuRowParams(
                    title = "Remove",
                    onClick = {},
                    icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                    isDestructive = true
                )
            )
            AureoleMenuNote(text = "Current size: 2 x 2 cells")
            AureoleMenuSectionLabel(text = "Actions")
            AureoleMenuRow(
                MenuRowParams(
                    title = "Settings",
                    onClick = {},
                    icon = { m, c -> AureoleDS.icons.Settings(modifier = m, tint = c) },
                    showChevron = true
                )
            )
        }
    }
}
