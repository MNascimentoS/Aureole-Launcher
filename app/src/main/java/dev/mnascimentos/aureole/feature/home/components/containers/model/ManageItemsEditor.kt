package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.composable.dialogs.model.initialFolderDraft
import dev.mnascimentos.aureole.core.data.model.AppFolder

/**
 * Navigation of "Manage items" inside the container sheet (RF-07): which page is shown and, while a folder is
 * edited in place, the form's draft (the typed name stays as typed; the container gets the resolved one).
 */
class ManageItemsEditor {
    var page: ManageItemsPage by mutableStateOf(ManageItemsPage.ItemList)
        private set
    var folderDraft: FolderFormDraft by mutableStateOf(FolderFormDraft(name = ""))
        private set
    var folderMode: FolderFormMode by mutableStateOf(FolderFormMode.FORM)

    val isEditing: Boolean
        get() = page != ManageItemsPage.ItemList

    /** Opens the folder form of [folder] in place of the list. */
    fun openFolder(folder: AppFolder) {
        folderDraft = initialFolderDraft(folder)
        folderMode = FolderFormMode.FORM
        page = ManageItemsPage.FolderEditor(folder.id)
    }

    /** Opens the editor of the quick button [quickButtonId] in place of the list. */
    fun openQuickButton(quickButtonId: String) {
        page = ManageItemsPage.QuickButtonEditor(quickButtonId)
    }

    fun editFolderDraft(draft: FolderFormDraft) {
        folderDraft = draft
    }

    /** Back from an editor: shows the list again and returns true; false on the list (the caller handles it). */
    fun back(): Boolean {
        val target = page.backTarget() ?: return false
        page = target
        return true
    }
}
