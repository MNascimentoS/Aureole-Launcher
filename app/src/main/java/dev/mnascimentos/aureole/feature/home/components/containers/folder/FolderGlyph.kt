package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.readableOn
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/** Own color of [folder] under the current theme, or null when it has none (the container color applies). */
@Composable
internal fun folderOwnColor(folder: AppFolder): Color? = folder.color?.resolve(AureoleDS.colors)

/**
 * What a folder shows on the home, by its [AppFolder.iconType]: the catalog icon (the first letter when it has
 * none), the first letter of the name in the theme font, or the emoji (the letter when it has none). The glyph is
 * [size] tall and drawn in [tint], or in a readable color over the folder's own color.
 */
@Composable
internal fun FolderGlyph(
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
