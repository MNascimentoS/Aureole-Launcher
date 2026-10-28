package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import java.util.UUID

internal class EditContainerState(
    val panel: ContainerModel,
    options: ContainerOptionsState,
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>,
    quickButtons: List<QuickButtonModel> = panel.quickButtons
) {
    var position by mutableStateOf(options.position)
    var orientation by mutableStateOf(options.orientation)
    var isBackgroundEnabled by mutableStateOf(options.isBackgroundEnabled)
    var isExpandCell by mutableStateOf(options.isExpandCell)
    var showAddFolderButton by mutableStateOf(options.showAddFolderButton)
    var showFolderLabels by mutableStateOf(options.showFolderLabels)
    var isGridFolderEnabled by mutableStateOf(options.isGridFolderEnabled)
    var hideText by mutableStateOf(options.hideText)
    var textColor by mutableStateOf(options.textColor)
    var backgroundColor by mutableStateOf(options.backgroundColor)
    var strokeEnabled by mutableStateOf(options.strokeEnabled)
    var strokeColor by mutableStateOf(options.strokeColor)
    var folderColor by mutableStateOf(options.folderColor)
    var folderStrokeEnabled by mutableStateOf(options.folderStrokeEnabled)
    var scrollUpShortcut by mutableStateOf(options.scrollUpShortcut)
    var scrollDownShortcut by mutableStateOf(options.scrollDownShortcut)
    var itemsList by mutableStateOf(itemsList)
    var folders by mutableStateOf(folders)
    var quickButtons by mutableStateOf(quickButtons)

    fun toModel(): ContainerModel {
        val updatedApps = itemsList
            .filter { it.itemType == ContainerItemType.APP }
            .mapNotNull { it.packageName }

        val updatedFolders = folders.filter { f ->
            itemsList.any { it.itemType == ContainerItemType.FOLDER && it.folderId == f.id }
        }.map { it.copy(displayAsGrid = isGridFolderEnabled) }

        val updatedQuickButtons = quickButtons.filter { qb ->
            itemsList.any { item ->
                item.itemType == ContainerItemType.QUICK_BUTTON &&
                    (item.quickButtonId == qb.id || item.id == qb.id)
            }
        }

        return panel.copy(
            position = position,
            orientation = orientation,
            isBackgroundEnabled = isBackgroundEnabled,
            isExpandCell = isExpandCell,
            showAddFolderButton = showAddFolderButton,
            showFolderLabels = showFolderLabels,
            isGridFolderEnabled = isGridFolderEnabled,
            hideText = hideText,
            textColor = textColor,
            backgroundColor = backgroundColor,
            strokeEnabled = strokeEnabled,
            strokeColor = strokeColor,
            folderColor = folderColor,
            folderStrokeEnabled = folderStrokeEnabled,
            scrollUpShortcut = scrollUpShortcut,
            scrollDownShortcut = scrollDownShortcut,
            items = itemsList.mapIndexed { idx, item -> item.copy(orderIndex = idx) },
            appPackageNames = updatedApps,
            folders = updatedFolders,
            quickButtons = updatedQuickButtons
        )
    }

    fun moveItemUp(index: Int) {
        if (index > 0) {
            val list = itemsList.toMutableList()
            val temp = list[index]
            list[index] = list[index - 1]
            list[index - 1] = temp
            itemsList = list
        }
    }

    fun moveItemDown(index: Int) {
        if (index < itemsList.size - 1) {
            val list = itemsList.toMutableList()
            val temp = list[index]
            list[index] = list[index + 1]
            list[index + 1] = temp
            itemsList = list
        }
    }

    fun removeItem(index: Int) {
        val list = itemsList.toMutableList()
        if (index in list.indices) {
            val removed = list.removeAt(index)
            itemsList = list
            if (removed.itemType == ContainerItemType.FOLDER) {
                folders = folders.filterNot { it.id == removed.folderId }
            } else if (removed.itemType == ContainerItemType.QUICK_BUTTON) {
                quickButtons = quickButtons.filterNot { it.id == removed.quickButtonId || it.id == removed.id }
            }
        }
    }

    fun addFolder(name: String) {
        val newFolder = AppFolder(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            name = name,
            displayAsGrid = isGridFolderEnabled
        )
        folders = folders + newFolder
        itemsList = itemsList + ContainerItemEntity(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            itemType = ContainerItemType.FOLDER,
            folderId = newFolder.id,
            orderIndex = itemsList.size
        )
    }

    fun addApp(pkg: String) {
        if (itemsList.none { it.itemType == ContainerItemType.APP && it.packageName == pkg }) {
            itemsList = itemsList + ContainerItemEntity(
                id = UUID.randomUUID().toString(),
                panelId = panel.id,
                itemType = ContainerItemType.APP,
                packageName = pkg,
                orderIndex = itemsList.size
            )
        }
    }

    fun addQuickButton(model: QuickButtonModel = QuickButtonModel()) {
        val qbId = if (model.id.isNotEmpty()) model.id else UUID.randomUUID().toString()
        val newQuickButton = model.copy(id = qbId)
        quickButtons = quickButtons + newQuickButton
        itemsList = itemsList + ContainerItemEntity(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            itemType = ContainerItemType.QUICK_BUTTON,
            quickButtonId = newQuickButton.id,
            orderIndex = itemsList.size
        ).also { it.quickButtonModel = newQuickButton }
    }
}

private fun buildInitialItemsList(panel: ContainerModel): List<ContainerItemEntity> {
    if (panel.items.isNotEmpty()) return panel.items
    val list = mutableListOf<ContainerItemEntity>()
    var idx = 0
    panel.folders.forEach { f ->
        list.add(
            ContainerItemEntity(
                id = UUID.randomUUID().toString(),
                panelId = panel.id,
                itemType = ContainerItemType.FOLDER,
                folderId = f.id,
                orderIndex = idx++
            )
        )
    }
    panel.appPackageNames.forEach { pkg ->
        list.add(
            ContainerItemEntity(
                id = UUID.randomUUID().toString(),
                panelId = panel.id,
                itemType = ContainerItemType.APP,
                packageName = pkg,
                orderIndex = idx++
            )
        )
    }
    panel.quickButtons.forEach { qb ->
        list.add(
            ContainerItemEntity(
                id = UUID.randomUUID().toString(),
                panelId = panel.id,
                itemType = ContainerItemType.QUICK_BUTTON,
                quickButtonId = qb.id,
                orderIndex = idx++
            ).also { it.quickButtonModel = qb }
        )
    }
    return list
}

@Composable
internal fun rememberEditContainerState(panel: ContainerModel): EditContainerState {
    return remember(panel.id) {
        val initialItemsList = buildInitialItemsList(panel)

        EditContainerState(
            panel = panel,
            options = ContainerOptionsState(
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
            ),
            itemsList = initialItemsList,
            folders = panel.folders
        )
    }
}

@AureolePreview
@Composable
private fun EditContainerStatePreview() {
    AureoleLauncherTheme {
        rememberEditContainerState(ContainerModel(id = "preview"))
    }
}
