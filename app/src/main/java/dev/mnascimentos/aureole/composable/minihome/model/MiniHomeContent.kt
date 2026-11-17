package dev.mnascimentos.aureole.composable.minihome.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.appitem.model.ThemedIconStyle

/**
 * A favorite shown on the mini-home: a solid color tile and its name.
 *
 * @property name Label of the favorite.
 * @property color Solid color of the tile.
 */
data class MiniHomeFavorite(
    val name: String,
    val color: Color
)

/**
 * Sample content drawn by `AureoleMiniHome`.
 *
 * @property time Sample clock time.
 * @property date Sample date line.
 * @property favorites Favorites listed under the clock.
 */
data class MiniHomeContent(
    val time: String,
    val date: String,
    val favorites: List<MiniHomeFavorite>
)

/**
 * Fill of a favorite icon on the mini-home.
 *
 * @property tile Color of the icon tile.
 * @property glyph Color of the glyph drawn inside the tile; null for the original icon (a plain colored tile).
 */
data class MiniHomeIconFill(
    val tile: Color,
    val glyph: Color?
)

/**
 * The icon of a favorite: its own color, or, with [themedIcons], the themed look of the home ([themed]).
 */
fun miniHomeIconFill(
    favorite: MiniHomeFavorite,
    themedIcons: Boolean,
    themed: ThemedIconStyle
): MiniHomeIconFill = if (themedIcons) {
    MiniHomeIconFill(tile = themed.tile, glyph = themed.tint)
} else {
    MiniHomeIconFill(tile = favorite.color, glyph = null)
}

/**
 * How the favorites of the mini-home are drawn.
 *
 * @property isLeftHanded Apps on the right, label first.
 * @property themedIcons Icons with the themed look of the home.
 */
data class MiniHomeFavoritesLayout(
    val isLeftHanded: Boolean,
    val themedIcons: Boolean
)
