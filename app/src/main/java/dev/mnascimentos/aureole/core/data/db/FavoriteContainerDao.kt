package dev.mnascimentos.aureole.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FavoriteContainerDao {

    @Query(
        "SELECT package_name FROM container_favorite_items WHERE container_id = :containerId ORDER BY item_order ASC"
    )
    suspend fun getFavoriteItemsForContainer(containerId: String): List<String>

    @Query("SELECT * FROM favorite_containers ORDER BY created_at ASC")
    suspend fun getAllFavoriteContainers(): List<FavoriteContainerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteContainer(container: FavoriteContainerEntity)

    @Query("DELETE FROM container_favorite_items WHERE container_id = :containerId")
    suspend fun deleteFavoriteItemsForContainer(containerId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteItems(items: List<ContainerFavoriteItemEntity>)

    @Transaction
    suspend fun updateContainerItems(containerId: String, packageNames: List<String>) {
        insertFavoriteContainer(FavoriteContainerEntity(id = containerId))
        deleteFavoriteItemsForContainer(containerId)
        val items = packageNames.mapIndexed { index, pkg ->
            ContainerFavoriteItemEntity(
                containerId = containerId,
                packageName = pkg,
                itemOrder = index
            )
        }
        insertFavoriteItems(items)
    }

    @Query("DELETE FROM favorite_containers WHERE id = :containerId")
    suspend fun deleteFavoriteContainer(containerId: String)
}
