package dev.mnascimentos.aureole.feature.home.model

import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults

/**
 * Ids of the favorites list that existed before lists became independent. Only it falls back to the global
 * favorites (what a user coming from 0.9.20 already has); every other list starts empty.
 */
private val LEGACY_FAVORITES_IDS = setOf(
    GridDefaults.APPS_LIST_ITEM_ID,
    FavoriteContainerRepository.DEFAULT_CONTAINER_ID
)

/** True when [itemId] is the legacy favorites list, or no list at all (the global favorites). */
fun isLegacyFavoritesId(itemId: String?): Boolean = itemId == null || itemId in LEGACY_FAVORITES_IDS

/**
 * The packages favorited in the list [itemId]: its own saved favorites, else the global ones for the legacy list
 * only. A list created later has no saved favorites yet, so it is empty instead of inheriting another list's apps.
 */
fun MainUiState.favoritesOf(itemId: String?): List<String> =
    itemId?.let { containerFavorites[it] } ?: if (isLegacyFavoritesId(itemId)) favoriteAppPackages else emptyList()
