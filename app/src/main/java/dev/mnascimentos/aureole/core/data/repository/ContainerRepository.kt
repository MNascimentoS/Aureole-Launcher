package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.ContainerDao
import dev.mnascimentos.aureole.core.data.db.FolderDao
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.newContainerColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ContainerRepository(
    private val containerDao: ContainerDao,
    private val folderDao: FolderDao
) {
    private val itemMapper = ContainerItemMapper()

    constructor(context: Context) : this(
        AppDatabase.getInstance(context).containerDao(),
        AppDatabase.getInstance(context).folderDao()
    )

    suspend fun getAllContainers(): List<ContainerModel> = withContext(Dispatchers.IO) {
        val entities = containerDao.getAllContainers()
        val allFolders = folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
        entities.map { entity ->
            val items = containerDao.getItemsForPanel(entity.id).map { itemMapper.mapItemQuickButton(it) }
            entity.toContainerModel(items, allFolders.filter { it.panelId == entity.id })
        }
    }

    suspend fun getContainer(panelId: String): ContainerModel? = withContext(Dispatchers.IO) {
        val entity = containerDao.getContainer(panelId) ?: return@withContext null
        val items = containerDao.getItemsForPanel(panelId).map { itemMapper.mapItemQuickButton(it) }
        val allFolders = folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
        entity.toContainerModel(items, allFolders.filter { it.panelId == panelId })
    }

    /**
     * The group [panelId], created with the initial values when it does not exist yet: with [isBackgroundEnabled]
     * it starts with the theme background, and its items with the theme item background (D-12).
     */
    suspend fun ensureContainerExists(
        panelId: String,
        defaultTitle: String = "Painel Lateral",
        isBackgroundEnabled: Boolean = false,
        position: String = ContainerModel().position,
        isExpandCell: Boolean = false
    ): ContainerModel =
        withContext(Dispatchers.IO) {
            val existing = getContainer(panelId)
            if (existing != null) return@withContext existing

            val colors = newContainerColors(hasBackground = isBackgroundEnabled)
            val newEntity = ContainerEntity(
                id = panelId,
                title = defaultTitle,
                position = position,
                isBackgroundEnabled = isBackgroundEnabled,
                isExpandCell = isExpandCell,
                backgroundColorRef = colors.background?.toJson(),
                itemColorRef = colors.item?.toJson()
            )
            containerDao.insertOrUpdateContainer(newEntity)

            getContainer(panelId) ?: ContainerModel(id = panelId, title = defaultTitle)
        }

    suspend fun saveContainer(panel: ContainerModel) = withContext(Dispatchers.IO) {
        containerDao.insertOrUpdateContainer(panel.toContainerEntity())

        val itemEntities = itemMapper.buildItemEntities(panel)

        panel.folders.forEach { folder ->
            val folderWithPanelId = folder.copy(panelId = panel.id, displayAsGrid = panel.isGridFolderEnabled)
            folderDao.insertFolder(folderWithPanelId)
        }

        containerDao.updatePanelItems(panel.id, itemEntities)
    }

    suspend fun deleteContainer(panelId: String) = withContext(Dispatchers.IO) {
        containerDao.deleteContainer(panelId)
        val allFolders = folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
        allFolders.filter { it.panelId == panelId }.forEach { folder ->
            folderDao.deleteFolder(folder.id)
        }
    }

    suspend fun addAppToPanel(panelId: String, packageName: String) = withContext(Dispatchers.IO) {
        val current = getContainer(panelId) ?: ensureContainerExists(panelId)
        if (!current.appPackageNames.contains(packageName)) {
            val updatedApps = current.appPackageNames + packageName
            saveContainer(current.copy(appPackageNames = updatedApps))
        }
    }

    suspend fun removeAppFromPanel(panelId: String, packageName: String) = withContext(Dispatchers.IO) {
        val current = getContainer(panelId) ?: return@withContext
        val updatedApps = current.appPackageNames - packageName
        saveContainer(current.copy(appPackageNames = updatedApps))
    }

    suspend fun addFolderToPanel(panelId: String, folder: AppFolder) = withContext(Dispatchers.IO) {
        val current = getContainer(panelId) ?: ensureContainerExists(panelId)
        val folderWithPanelId = folder.copy(panelId = panelId)
        folderDao.insertFolder(folderWithPanelId)

        val newFolderItem = ContainerItemEntity(
            id = UUID.randomUUID().toString(),
            panelId = panelId,
            itemType = ContainerItemType.FOLDER,
            folderId = folderWithPanelId.id,
            orderIndex = current.items.size
        )

        val updatedFolders = current.folders.filter { it.id != folder.id } + folderWithPanelId
        val updatedItems = if (current.items.none {
                it.itemType == ContainerItemType.FOLDER && it.folderId == folderWithPanelId.id
            }
        ) {
            current.items + newFolderItem
        } else {
            current.items
        }

        saveContainer(current.copy(folders = updatedFolders, items = updatedItems))
    }

    suspend fun deleteFolderFromPanel(panelId: String, folderId: String) = withContext(Dispatchers.IO) {
        folderDao.deleteFolder(folderId)
        val current = getContainer(panelId) ?: return@withContext
        val updatedFolders = current.folders.filter { it.id != folderId }
        saveContainer(current.copy(folders = updatedFolders))
    }
}
