package dev.mnascimentos.aureole.feature.home.components.favorites.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/**
 * The result of tapping the favorite toggle of an app in one favorites list.
 *
 * @property favorites The list after the tap: the app added at the end, or removed from it.
 * @property wasFavorite Whether the app was already in the list (so the tap removed it).
 */
data class FavoriteToggle(val favorites: List<String>, val wasFavorite: Boolean) {
    /** The undo snackbar message: "removed" when the tap took the app out, "added" otherwise. */
    @get:StringRes
    val undoMessageRes: Int
        get() = if (wasFavorite) R.string.home_undo_favorite_removed else R.string.home_undo_favorite_added
}

/** Adds [packageName] to the end of [previous], or removes it when it is already there. */
fun toggleFavoriteIn(previous: List<String>, packageName: String): FavoriteToggle {
    val wasFavorite = packageName in previous
    val favorites = if (wasFavorite) previous - packageName else previous + packageName
    return FavoriteToggle(favorites = favorites, wasFavorite = wasFavorite)
}
