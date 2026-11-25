package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.withDetails
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import dev.mnascimentos.aureole.feature.home.components.containers.model.RemovedGroupItem
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.normalized
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
    var backgroundColorRef by mutableStateOf(options.backgroundColorRef)
    var itemColorRef by mutableStateOf(options.itemColorRef)
    var itemSpacing by mutableStateOf(options.itemSpacing)
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
        }.map { it.copy(appearance = it.appearance.normalized()) }

        return panel.copy(
            position = position,
            orientation = orientation,
            // The old switch follows the color, for what still reads it (sizes of the items, RF-E3).
            isBackgroundEnabled = backgroundColorRef != null,
            backgroundColorRef = backgroundColorRef,
            itemColorRef = itemColorRef,
            itemSpacing = itemSpacing,
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

    /** Moves the item at [from] to [to] (one swap of the reorderable list); out of range changes nothing. */
    fun moveItem(from: Int, to: Int) {
        itemsList = move(itemsList, from, to)
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

    fun addFolder(name: String) = addFolder(FolderDetails(name = name))

    /** Adds a new folder with the name and look chosen in the folder form, holding [apps] (RF-C4). */
    fun addFolder(details: FolderDetails, apps: List<String> = emptyList()) {
        val newFolder = AppFolder(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            name = details.name,
            appPackageNames = apps,
            displayAsGrid = isGridFolderEnabled
        ).withDetails(details)
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

    /** Whether the app [pkg] is an item of the group (the mark of "Add apps", RF-C3). */
    fun hasApp(pkg: String): Boolean = itemsList.any { it.itemType == ContainerItemType.APP && it.packageName == pkg }

    /** "Add apps": a tap puts the app [pkg] at the end of the group, or takes it out when it is there (RF-C3). */
    fun toggleApp(pkg: String) {
        val index = itemsList.indexOfFirst { it.itemType == ContainerItemType.APP && it.packageName == pkg }
        if (index >= 0) removeItem(index) else addApp(pkg)
    }

    /**
     * Removes the item at [index] (×, RF-C6) and returns what puts it back ([restore]): the item, where it was, and
     * the folder or quick button it took along; null out of range. [name] is what the snackbar calls it.
     */
    fun removeItemAt(index: Int, id: Long, name: String): RemovedGroupItem? {
        val item = itemsList.getOrNull(index) ?: return null
        val removed = RemovedGroupItem(
            id = id,
            index = index,
            item = item,
            name = name,
            folder = folders.find { item.itemType == ContainerItemType.FOLDER && it.id == item.folderId },
            quickButton = quickButtons.find {
                item.itemType == ContainerItemType.QUICK_BUTTON && (it.id == item.quickButtonId || it.id == item.id)
            }
        )
        removeItem(index)
        return removed
    }

    /** Puts a removed item back where it was (Undo, RF-C6), with its folder or quick button. */
    fun restore(removed: RemovedGroupItem) {
        if (itemsList.any { it.id == removed.item.id }) return
        val list = itemsList.toMutableList()
        list.add(removed.index.coerceIn(0, list.size), removed.item)
        itemsList = list
        removed.folder?.let { folder -> if (folders.none { it.id == folder.id }) folders = folders + folder }
        removed.quickButton?.let { qb -> if (quickButtons.none { it.id == qb.id }) quickButtons = quickButtons + qb }
    }

    /** "Quick button": adds a button whose tap runs [action] (RF-C5). */
    fun addQuickButton(action: ShortcutAction): String =
        addQuickButton(QuickButtonModel(shortcuts = QuickButtonShortcuts(click = action)))

    /** Adds a quick button and returns its id, so the sheet can open its editor right away (RF-07). */
    fun addQuickButton(model: QuickButtonModel = QuickButtonModel()): String {
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
        return qbId
    }

    /** Writes the name and look chosen in the in-place folder editor to the folder [folderId]. */
    fun updateFolder(folderId: String, details: FolderDetails) {
        folders = folders.map { if (it.id == folderId) it.withDetails(details) else it }
    }

    /**
     * Writes the change [change] made in the in-place quick button editor (the look or the shortcuts) to the button
     * [quickButtonId].
     */
    fun updateQuickButton(quickButtonId: String, change: (QuickButtonModel) -> QuickButtonModel) {
        val updated = quickButtons.find { it.id == quickButtonId }?.let(change) ?: return
        quickButtons = quickButtons.map { if (it.id == quickButtonId) updated else it }
        // The entity keeps its model outside equals(), so a new list would compare equal and not be stored:
        // the item's model is updated where it is (the save reads it before the quick button list).
        itemsList.filter { item ->
            item.itemType == ContainerItemType.QUICK_BUTTON &&
                (item.quickButtonId == quickButtonId || item.id == quickButtonId)
        }.forEach { it.quickButtonModel = updated }
    }
}

/** The container as the live preview draws it, from the draft in [EditContainerState]. */
internal fun EditContainerState.toConfig(): ContainerConfig = ContainerConfig(
    panelId = panel.id,
    items = itemsList,
    folders = folders.map { it.copy(displayAsGrid = isGridFolderEnabled) },
    appPackageNames = itemsList.mapNotNull { it.packageName },
    quickButtons = quickButtons,
    position = position,
    orientation = orientation,
    isBackgroundEnabled = backgroundColorRef != null,
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
    backgroundColorRef = backgroundColorRef,
    itemColorRef = itemColorRef,
    itemSpacing = itemSpacing
)

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
                scrollDownShortcut = panel.scrollDownShortcut,
                backgroundColorRef = panel.backgroundColorRef,
                itemColorRef = panel.itemColorRef,
                itemSpacing = panel.itemSpacing
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
