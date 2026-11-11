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
            val rawItems = containerDao.getItemsForPanel(entity.id)
            val items = rawItems.map { itemMapper.mapItemQuickButton(it) }
            val panelFolders = allFolders.filter { it.panelId == entity.id }
            val appPackageNames = items.filter { it.itemType == ContainerItemType.APP && it.packageName != null }
                .mapNotNull { it.packageName }
            val quickButtons = items.filter { it.itemType == ContainerItemType.QUICK_BUTTON }
                .mapNotNull { it.quickButtonModel }

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
                hideText = entity.hideText,
                textColor = entity.textColor,
                backgroundColor = entity.backgroundColor,
                strokeEnabled = entity.strokeEnabled,
                strokeColor = entity.strokeColor,
                folderColor = entity.folderColor,
                folderStrokeEnabled = entity.folderStrokeEnabled,
                scrollUpShortcut = entity.scrollUpShortcut,
                scrollDownShortcut = entity.scrollDownShortcut,
                items = items,
                folders = panelFolders,
                appPackageNames = appPackageNames,
                quickButtons = quickButtons
            )
        }
    }

    suspend fun getContainer(panelId: String): ContainerModel? = withContext(Dispatchers.IO) {
        val entity = containerDao.getContainer(panelId) ?: return@withContext null
        val rawItems = containerDao.getItemsForPanel(panelId)
        val items = rawItems.map { itemMapper.mapItemQuickButton(it) }
        val allFolders = folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
        val panelFolders = allFolders.filter { it.panelId == panelId }
        val appPackageNames = items.filter { it.itemType == ContainerItemType.APP && it.packageName != null }
            .mapNotNull { it.packageName }
        val quickButtons = items.filter { it.itemType == ContainerItemType.QUICK_BUTTON }
            .mapNotNull { it.quickButtonModel }

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
            hideText = entity.hideText,
            textColor = entity.textColor,
            backgroundColor = entity.backgroundColor,
            strokeEnabled = entity.strokeEnabled,
            strokeColor = entity.strokeColor,
            folderColor = entity.folderColor,
            folderStrokeEnabled = entity.folderStrokeEnabled,
            scrollUpShortcut = entity.scrollUpShortcut,
            scrollDownShortcut = entity.scrollDownShortcut,
            items = items,
            folders = panelFolders,
            appPackageNames = appPackageNames,
            quickButtons = quickButtons
        )
    }

    suspend fun ensureContainerExists(
        panelId: String,
        defaultTitle: String = "Painel Lateral",
        isBackgroundEnabled: Boolean = false
    ): ContainerModel =
        withContext(Dispatchers.IO) {
            val existing = getContainer(panelId)
            if (existing != null) return@withContext existing

            val newEntity = ContainerEntity(
                id = panelId,
                title = defaultTitle,
                isBackgroundEnabled = isBackgroundEnabled
            )
            containerDao.insertOrUpdateContainer(newEntity)

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
            isGridFolderEnabled = panel.isGridFolderEnabled,
            hideText = panel.hideText,
            textColor = panel.textColor,
            backgroundColor = panel.backgroundColor,
            strokeEnabled = panel.strokeEnabled,
            strokeColor = panel.strokeColor,
            folderColor = panel.folderColor,
            folderStrokeEnabled = panel.folderStrokeEnabled,
            scrollUpShortcut = panel.scrollUpShortcut,
            scrollDownShortcut = panel.scrollDownShortcut
        )
        containerDao.insertOrUpdateContainer(entity)

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
