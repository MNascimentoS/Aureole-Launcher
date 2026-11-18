package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType

/**
 * What "Manage items" shows inside the container sheet (RF-07): the list of items, or the editor of one of them in
 * its place, the way Customize swaps to the palette or font picker. Back from an editor returns to the list.
 */
sealed interface ManageItemsPage {
    data object ItemList : ManageItemsPage
    data class FolderEditor(val folderId: String) : ManageItemsPage
    data class QuickButtonEditor(val quickButtonId: String) : ManageItemsPage

    /** "Select a shortcut" for the click of a quick button just created, after its appearance (RF-B2). */
    data class QuickButtonShortcut(val quickButtonId: String) : ManageItemsPage
}

/** Where back goes from [this] page: the list from an editor, null (leave Manage items) from the list. */
fun ManageItemsPage.backTarget(): ManageItemsPage? =
    if (this == ManageItemsPage.ItemList) null else ManageItemsPage.ItemList

/** The editor a tap on the row of [this] item opens; null for apps, which have nothing to edit here. */
fun ContainerItemEntity.editorPage(): ManageItemsPage? = when (itemType) {
    ContainerItemType.FOLDER -> folderId?.let { ManageItemsPage.FolderEditor(it) }
    ContainerItemType.QUICK_BUTTON -> (quickButtonId ?: id).let { ManageItemsPage.QuickButtonEditor(it) }
    ContainerItemType.APP -> null
}
