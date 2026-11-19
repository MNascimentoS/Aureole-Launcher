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

    /**
     * True from the creation of a quick button until its click shortcut is chosen: leaving its appearance
     * then goes to "Select a shortcut" instead of the list. Editing an existing button never sets it.
     */
    var isCreatingQuickButton: Boolean by mutableStateOf(false)
        private set

    /** Opens the editor of the quick button [quickButtonId] in place of the list; [isNew] for a button just created. */
    fun openQuickButton(quickButtonId: String, isNew: Boolean = false) {
        isCreatingQuickButton = isNew
        page = ManageItemsPage.QuickButtonEditor(quickButtonId)
    }

    /** The click shortcut of the new button was chosen: creation is over and the list is shown again. */
    fun finishQuickButtonCreation() {
        isCreatingQuickButton = false
        page = ManageItemsPage.ItemList
    }

    fun editFolderDraft(draft: FolderFormDraft) {
        folderDraft = draft
    }

    /** Back from an editor: shows the list again and returns true; false on the list (the caller handles it). */
    fun back(): Boolean {
        val current = page
        val target = when {
            // Creating: leaving the appearance goes on to the shortcut (D-01); back from there returns to the look.
            current is ManageItemsPage.QuickButtonEditor && isCreatingQuickButton ->
                ManageItemsPage.QuickButtonShortcut(current.quickButtonId)
            current is ManageItemsPage.QuickButtonShortcut -> ManageItemsPage.QuickButtonEditor(current.quickButtonId)
            else -> current.backTarget()
        } ?: return false
        page = target
        return true
    }
}
