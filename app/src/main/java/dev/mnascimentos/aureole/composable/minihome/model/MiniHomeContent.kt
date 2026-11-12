package dev.mnascimentos.aureole.composable.minihome.model

import androidx.compose.ui.graphics.Color

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
 * @property favorites Favorites listed above the dock.
 * @property dockSize Number of icons in the dock.
 */
data class MiniHomeContent(
    val time: String,
    val date: String,
    val favorites: List<MiniHomeFavorite>,
    val dockSize: Int = DEFAULT_DOCK_SIZE
) {
    companion object {
        const val DEFAULT_DOCK_SIZE = 4
    }
}
