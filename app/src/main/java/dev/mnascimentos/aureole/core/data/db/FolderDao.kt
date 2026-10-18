package dev.mnascimentos.aureole.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderEntity
import dev.mnascimentos.aureole.core.data.model.FolderItemEntity
import dev.mnascimentos.aureole.core.data.model.FolderWithItems

@Dao
interface FolderDao {

    @Transaction
    @Query("SELECT * FROM folders ORDER BY created_at ASC")
    suspend fun getAllFoldersWithItems(): List<FolderWithItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderEntity(folder: FolderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolderItems(items: List<FolderItemEntity>)

    @Query("DELETE FROM folder_items WHERE folder_id = :folderId")
    suspend fun deleteFolderItems(folderId: String)

    @Transaction
    suspend fun insertFolder(appFolder: AppFolder) {
        val folderEntity = FolderEntity(
            id = appFolder.id,
            panelId = appFolder.panelId,
            name = appFolder.name,
            color = null,
            iconFallback = appFolder.iconPackage,
            icon = appFolder.icon,
            displayAsGrid = appFolder.displayAsGrid
        )
        insertFolderEntity(folderEntity)
        deleteFolderItems(appFolder.id)
        val items = appFolder.appPackageNames.mapIndexed { index, pkg ->
            FolderItemEntity(
                folderId = appFolder.id,
                packageName = pkg,
                itemOrder = index
            )
        }
        insertFolderItems(items)
    }

    @Query("UPDATE folders SET name = :name, icon = COALESCE(:icon, icon) WHERE id = :folderId")
    suspend fun updateFolderDetails(folderId: String, name: String, icon: String?)

    @Transaction
    suspend fun updateFolderItems(folderId: String, packageNames: List<String>) {
        deleteFolderItems(folderId)
        val items = packageNames.mapIndexed { index, pkg ->
            FolderItemEntity(
                folderId = folderId,
                packageName = pkg,
                itemOrder = index
            )
        }
        insertFolderItems(items)
    }

    @Query("DELETE FROM folders WHERE id = :folderId")
    suspend fun deleteFolder(folderId: String)
}
