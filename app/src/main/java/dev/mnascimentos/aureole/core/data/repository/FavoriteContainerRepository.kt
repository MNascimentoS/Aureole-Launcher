package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.db.FavoriteContainerDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteContainerRepository(
    context: Context,
    private val settingsRepository: SettingsRepository = SettingsRepository(context)
) {
    private val dbHelper = FavoriteContainerDatabaseHelper(context)

    suspend fun getFavoritesForContainer(containerId: String): List<String> = withContext(Dispatchers.IO) {
        val items = dbHelper.getFavoriteItemsForContainer(containerId)
        if (items.isEmpty()) {
            val legacyFavorites = settingsRepository.favoriteAppPackages
            if (legacyFavorites.isNotEmpty()) {
                dbHelper.updateContainerItems(containerId, legacyFavorites)
                return@withContext legacyFavorites
            }
        }
        items
    }

    suspend fun getAllContainerFavorites(): Map<String, List<String>> = withContext(Dispatchers.IO) {
        var map = dbHelper.getAllContainersWithItems()
        if (map.isEmpty()) {
            val legacyFavorites = settingsRepository.favoriteAppPackages
            if (legacyFavorites.isNotEmpty()) {
                val defaultId = DEFAULT_CONTAINER_ID
                dbHelper.updateContainerItems(defaultId, legacyFavorites)
                map = mapOf(defaultId to legacyFavorites)
            }
        }
        map
    }

    suspend fun updateContainerFavorites(containerId: String, packageNames: List<String>) = withContext(
        Dispatchers.IO
    ) {
        dbHelper.updateContainerItems(containerId, packageNames)
    }

    suspend fun deleteContainer(containerId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteContainer(containerId)
    }

    companion object {
        const val DEFAULT_CONTAINER_ID = "default_favorites"
    }
}
