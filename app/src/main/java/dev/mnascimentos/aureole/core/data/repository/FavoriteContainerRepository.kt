package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.FavoriteContainerDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteContainerRepository(
    private val favoriteContainerDao: FavoriteContainerDao,
    private val settingsRepository: SettingsRepository,
) {
    constructor(context: Context, settingsRepository: SettingsRepository) : this(
        AppDatabase.getInstance(context).favoriteContainerDao(),
        settingsRepository
    )

    suspend fun getFavoritesForContainer(containerId: String): List<String> = withContext(Dispatchers.IO) {
        val items = favoriteContainerDao.getFavoriteItemsForContainer(containerId)
        if (items.isEmpty()) {
            val legacyFavorites = settingsRepository.favoriteAppPackages
            if (legacyFavorites.isNotEmpty()) {
                favoriteContainerDao.updateContainerItems(containerId, legacyFavorites)
                return@withContext legacyFavorites
            }
        }
        items
    }

    suspend fun getAllContainerFavorites(): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val containers = favoriteContainerDao.getAllFavoriteContainers()
        val result = mutableMapOf<String, List<String>>()
        for (container in containers) {
            val items = favoriteContainerDao.getFavoriteItemsForContainer(container.id)
            result[container.id] = items
        }

        if (result.isEmpty()) {
            val legacyFavorites = settingsRepository.favoriteAppPackages
            if (legacyFavorites.isNotEmpty()) {
                val defaultId = DEFAULT_CONTAINER_ID
                favoriteContainerDao.updateContainerItems(defaultId, legacyFavorites)
                return@withContext mapOf(defaultId to legacyFavorites)
            }
        }
        result
    }

    suspend fun updateContainerFavorites(containerId: String, packageNames: List<String>) = withContext(
        Dispatchers.IO
    ) {
        favoriteContainerDao.updateContainerItems(containerId, packageNames)
    }

    suspend fun deleteContainer(containerId: String) = withContext(Dispatchers.IO) {
        favoriteContainerDao.deleteFavoriteContainer(containerId)
    }

    companion object {
        const val DEFAULT_CONTAINER_ID = "default_favorites"
    }
}
