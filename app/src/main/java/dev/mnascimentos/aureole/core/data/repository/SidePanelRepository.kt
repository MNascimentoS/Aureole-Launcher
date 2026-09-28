package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.db.FolderDatabaseHelper
import dev.mnascimentos.aureole.core.data.db.SidePanelDatabaseHelper
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.SidePanelEntity
import dev.mnascimentos.aureole.core.data.model.SidePanelItemEntity
import dev.mnascimentos.aureole.core.data.model.SidePanelItemType
import dev.mnascimentos.aureole.core.data.model.SidePanelModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SidePanelRepository(context: Context) {
    private val dbHelper = SidePanelDatabaseHelper(context)
    private val folderDbHelper = FolderDatabaseHelper(context)

    suspend fun getAllSidePanels(): List<SidePanelModel> = withContext(Dispatchers.IO) {
        val entities = dbHelper.getAllSidePanels()
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }

        entities.map { entity ->
            val items = dbHelper.getItemsForPanel(entity.id)
            val panelFolders = allFolders.filter { it.panelId == entity.id }
            val appPackageNames = items.filter { it.itemType == SidePanelItemType.APP && it.packageName != null }
                .mapNotNull { it.packageName }

            SidePanelModel(
                id = entity.id,
                title = entity.title,
                position = entity.position,
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

    suspend fun getSidePanel(panelId: String): SidePanelModel? = withContext(Dispatchers.IO) {
        val entity = dbHelper.getSidePanel(panelId) ?: return@withContext null
        val items = dbHelper.getItemsForPanel(panelId)
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
        val panelFolders = allFolders.filter { it.panelId == panelId }
        val appPackageNames = items.filter { it.itemType == SidePanelItemType.APP && it.packageName != null }
            .mapNotNull { it.packageName }

        SidePanelModel(
            id = entity.id,
            title = entity.title,
            position = entity.position,
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

    suspend fun ensureSidePanelExists(panelId: String, defaultTitle: String = "Painel Lateral"): SidePanelModel =
        withContext(Dispatchers.IO) {
            val existing = getSidePanel(panelId)
            if (existing != null) return@withContext existing

            val newEntity = SidePanelEntity(
                id = panelId,
                title = defaultTitle
            )
            dbHelper.insertOrUpdateSidePanel(newEntity)

            getSidePanel(panelId) ?: SidePanelModel(id = panelId, title = defaultTitle)
        }

    suspend fun saveSidePanel(panel: SidePanelModel) = withContext(Dispatchers.IO) {
        val entity = SidePanelEntity(
            id = panel.id,
            title = panel.title,
            position = panel.position,
            isBackgroundEnabled = panel.isBackgroundEnabled,
            isExpandCell = panel.isExpandCell,
            showAddFolderButton = panel.showAddFolderButton,
            showFolderLabels = panel.showFolderLabels,
            isGridFolderEnabled = panel.isGridFolderEnabled
        )
        dbHelper.insertOrUpdateSidePanel(entity)

        val itemEntities = mutableListOf<SidePanelItemEntity>()
        var orderIndex = 0

        panel.appPackageNames.forEach { pkg ->
            itemEntities.add(
                SidePanelItemEntity(
                    id = UUID.randomUUID().toString(),
                    panelId = panel.id,
                    itemType = SidePanelItemType.APP,
                    packageName = pkg,
                    orderIndex = orderIndex++
                )
            )
        }

        panel.folders.forEach { folder ->
            val folderWithPanelId = folder.copy(panelId = panel.id, displayAsGrid = panel.isGridFolderEnabled)
            folderDbHelper.insertFolder(folderWithPanelId)

            itemEntities.add(
                SidePanelItemEntity(
                    id = UUID.randomUUID().toString(),
                    panelId = panel.id,
                    itemType = SidePanelItemType.FOLDER,
                    folderId = folder.id,
                    orderIndex = orderIndex++
                )
            )
        }

        dbHelper.updatePanelItems(panel.id, itemEntities)
    }

    suspend fun deleteSidePanel(panelId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteSidePanel(panelId)
        val allFolders = folderDbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
        allFolders.filter { it.panelId == panelId }.forEach { folder ->
            folderDbHelper.deleteFolder(folder.id)
        }
    }

    suspend fun addAppToPanel(panelId: String, packageName: String) = withContext(Dispatchers.IO) {
        val current = getSidePanel(panelId) ?: ensureSidePanelExists(panelId)
        if (!current.appPackageNames.contains(packageName)) {
            val updatedApps = current.appPackageNames + packageName
            saveSidePanel(current.copy(appPackageNames = updatedApps))
        }
    }

    suspend fun removeAppFromPanel(panelId: String, packageName: String) = withContext(Dispatchers.IO) {
        val current = getSidePanel(panelId) ?: return@withContext
        val updatedApps = current.appPackageNames - packageName
        saveSidePanel(current.copy(appPackageNames = updatedApps))
    }

    suspend fun addFolderToPanel(panelId: String, folder: AppFolder) = withContext(Dispatchers.IO) {
        val current = getSidePanel(panelId) ?: ensureSidePanelExists(panelId)
        val folderWithPanelId = folder.copy(panelId = panelId)
        folderDbHelper.insertFolder(folderWithPanelId)

        val updatedFolders = current.folders.filter { it.id != folder.id } + folderWithPanelId
        saveSidePanel(current.copy(folders = updatedFolders))
    }

    suspend fun deleteFolderFromPanel(panelId: String, folderId: String) = withContext(Dispatchers.IO) {
        folderDbHelper.deleteFolder(folderId)
        val current = getSidePanel(panelId) ?: return@withContext
        val updatedFolders = current.folders.filter { it.id != folderId }
        saveSidePanel(current.copy(folders = updatedFolders))
    }
}
