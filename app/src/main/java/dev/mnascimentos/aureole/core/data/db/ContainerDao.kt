package dev.mnascimentos.aureole.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity

@Dao
interface ContainerDao {

    @Query("SELECT * FROM containers ORDER BY created_at ASC")
    suspend fun getAllContainers(): List<ContainerEntity>

    @Query("SELECT * FROM containers WHERE id = :panelId LIMIT 1")
    suspend fun getContainer(panelId: String): ContainerEntity?

    @Query("SELECT * FROM container_items WHERE panel_id = :panelId ORDER BY order_index ASC")
    suspend fun getItemsForPanel(panelId: String): List<ContainerItemEntity>

    @Upsert
    suspend fun insertOrUpdateContainer(container: ContainerEntity)

    @Query("DELETE FROM container_items WHERE panel_id = :panelId")
    suspend fun deleteContainerItemsForPanel(panelId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContainerItems(items: List<ContainerItemEntity>)

    @Transaction
    suspend fun updatePanelItems(panelId: String, items: List<ContainerItemEntity>) {
        deleteContainerItemsForPanel(panelId)
        insertContainerItems(items)
    }

    @Query("DELETE FROM containers WHERE id = :panelId")
    suspend fun deleteContainer(panelId: String)
}
