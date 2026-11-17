package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.readableOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Own color of [folder] under the current theme, or null when it has none (the container color applies). */
@Composable
fun folderOwnColor(folder: AppFolder): Color? = folder.color?.resolve(AureoleDS.colors)

/**
 * What a folder shows, by its [AppFolder.iconType]: the catalog icon (the first letter when it has none), the
 * first letter of the name in the theme font, or the emoji (the letter when it has none). The glyph is [size] tall
 * and drawn in [tint], or in a readable color over the folder's own color.
 */
@Composable
fun FolderGlyph(
    folder: AppFolder,
    size: Dp,
    tint: Color,
    letterStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    val color = folderOwnColor(folder)?.let { readableOn(it, AureoleDS.colors) } ?: tint
    val emojiSize = with(LocalDensity.current) { size.toSp() }
    val emoji = folder.emoji?.takeIf { folder.iconType == FolderIconType.EMOJI && it.isNotBlank() }
    when {
        emoji != null -> AureoleText(
            text = emoji,
            color = color,
            modifier = modifier,
            format = AureoleTextFormatParams(style = letterStyle.copy(fontSize = emojiSize))
        )
        folder.iconType == FolderIconType.ICON && folder.icon != null -> FolderIconRegistry.RenderIcon(
            name = folder.icon,
            modifier = modifier.size(size),
            tint = color
        )
        else -> AureoleText(
            text = folder.name.take(1).uppercase(),
            color = color,
            modifier = modifier,
            format = AureoleTextFormatParams(style = letterStyle, fontWeight = FontWeight.Bold)
        )
    }
}

/**
 * The folder as a small rounded tile of [size]: its own color (or [fallbackColor]) behind its glyph. Used where a
 * folder is listed, such as the rows of "Edit container".
 */
@Composable
fun FolderBadge(
    folder: AppFolder,
    size: Dp,
    modifier: Modifier = Modifier,
    fallbackColor: Color = AureoleDS.colors.surfaceVariant
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(folderOwnColor(folder) ?: fallbackColor),
        contentAlignment = Alignment.Center
    ) {
        FolderGlyph(
            folder = folder,
            size = size * GLYPH_FRACTION,
            tint = AureoleDS.colors.onSurfaceHigh,
            letterStyle = AureoleDS.typography.labelLarge
        )
    }
}

private const val GLYPH_FRACTION = 0.6f

@AureolePreview
@Composable
private fun FolderBadgePreview() {
    AureoleLauncherTheme {
        FolderBadge(
            folder = AppFolder(
                name = "Bank",
                icon = "account_balance",
                color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
            ),
            size = AureoleDS.dimens.xLarge
        )
    }
}
