package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.db.ContainerDatabaseHelper
import dev.mnascimentos.aureole.core.data.db.FolderDatabaseHelper
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ContainerRepository(context: Context) {
    private val dbHelper = ContainerDatabaseHelper(context)
    private val folderDbHelper = FolderDatabaseHelper(context)

    suspend fun getAllContainers(): List<ContainerModel> = withContext(Dispatchers.IO) {
        val entities = dbHelper.getAllContainers()
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }

        entities.map { entity ->
            val items = dbHelper.getItemsForPanel(entity.id)
            val panelFolders = allFolders.filter { it.panelId == entity.id }
            val appPackageNames = items.filter { it.itemType == ContainerItemType.APP && it.packageName != null }
                .mapNotNull { it.packageName }

            ContainerModel(
                id = entity.id,
                title = entity.title,
                position = entity.position,
                orientation = entity.orientation,
                isBackgroundEnabled = entity.isBackgroundEnabled,
                isExpandCell = entity.isExpandCell,
                showAddFolderButton = entity.showAddFolderButton,
                showFolderLabels = entity.showFolderLabels,
                isGridFolderEnabled = entity.isGridFolderEnabled,
                items = items,
                folders = panelFolders,
                appPackageNames = appPackageNames
            )
        }
    }

    suspend fun getContainer(panelId: String): ContainerModel? = withContext(Dispatchers.IO) {
        val entity = dbHelper.getContainer(panelId) ?: return@withContext null
        val items = dbHelper.getItemsForPanel(panelId)
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
        val panelFolders = allFolders.filter { it.panelId == panelId }
        val appPackageNames = items.filter { it.itemType == ContainerItemType.APP && it.packageName != null }
            .mapNotNull { it.packageName }

        ContainerModel(
            id = entity.id,
            title = entity.title,
            position = entity.position,
            orientation = entity.orientation,
            isBackgroundEnabled = entity.isBackgroundEnabled,
            isExpandCell = entity.isExpandCell,
            showAddFolderButton = entity.showAddFolderButton,
            showFolderLabels = entity.showFolderLabels,
            isGridFolderEnabled = entity.isGridFolderEnabled,
            items = items,
            folders = panelFolders,
            appPackageNames = appPackageNames
        )
    }

    suspend fun ensureContainerExists(panelId: String, defaultTitle: String = "Painel Lateral"): ContainerModel =
        withContext(Dispatchers.IO) {
            val existing = getContainer(panelId)
            if (existing != null) return@withContext existing

            val newEntity = ContainerEntity(
                id = panelId,
                title = defaultTitle
            )
            dbHelper.insertOrUpdateContainer(newEntity)

            getContainer(panelId) ?: ContainerModel(id = panelId, title = defaultTitle)
        }

    suspend fun saveContainer(panel: ContainerModel) = withContext(Dispatchers.IO) {
        val entity = ContainerEntity(
            id = panel.id,
            title = panel.title,
            position = panel.position,
            orientation = panel.orientation,
            isBackgroundEnabled = panel.isBackgroundEnabled,
            isExpandCell = panel.isExpandCell,
            showAddFolderButton = panel.showAddFolderButton,
            showFolderLabels = panel.showFolderLabels,
            isGridFolderEnabled = panel.isGridFolderEnabled
        )
        dbHelper.insertOrUpdateContainer(entity)

        val itemEntities = mutableListOf<ContainerItemEntity>()

        if (panel.items.isNotEmpty()) {
            panel.items.forEachIndexed { index, item ->
                itemEntities.add(item.copy(orderIndex = index))
            }
        } else {
            var orderIndex = 0
            panel.folders.forEach { folder ->
                itemEntities.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.FOLDER,
                        folderId = folder.id,
                        orderIndex = orderIndex++
                    )
                )
            }

            panel.appPackageNames.forEach { pkg ->
                itemEntities.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.APP,
                        packageName = pkg,
                        orderIndex = orderIndex++
                    )
                )
            }
        }

        panel.folders.forEach { folder ->
            val folderWithPanelId = folder.copy(panelId = panel.id, displayAsGrid = panel.isGridFolderEnabled)
            folderDbHelper.insertFolder(folderWithPanelId)
        }

        dbHelper.updatePanelItems(panel.id, itemEntities)
    }

    suspend fun deleteContainer(panelId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteContainer(panelId)
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
        allFolders.filter { it.panelId == panelId }.forEach { folder ->
            folderDbHelper.deleteFolder(folder.id)
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
        folderDbHelper.insertFolder(folderWithPanelId)

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
        folderDbHelper.deleteFolder(folderId)
        val current = getContainer(panelId) ?: return@withContext
        val updatedFolders = current.folders.filter { it.id != folderId }
        saveContainer(current.copy(folders = updatedFolders))
    }
}
