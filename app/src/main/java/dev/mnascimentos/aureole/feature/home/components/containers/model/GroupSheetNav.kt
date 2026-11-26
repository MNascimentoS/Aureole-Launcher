package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormDraft
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.composable.dialogs.model.initialFolderDraft
import dev.mnascimentos.aureole.core.data.model.AppFolder

/**
 * Where the group sheet is (RF-B1, RF-B10): the [tab] and, in its place, the [page] open; and while a folder is
 * edited in place, the form's draft (the typed name stays as typed; the group gets the resolved one).
 */
class GroupSheetNav(initialTab: GroupTab = GroupTab.ITEMS) {
    var tab: GroupTab by mutableStateOf(initialTab)
        private set
    var page: GroupSheetPage by mutableStateOf(GroupSheetPage.Tabs)
        private set
    var folderDraft: FolderFormDraft by mutableStateOf(FolderFormDraft(name = ""))
        private set
    var folderMode: FolderFormMode by mutableStateOf(FolderFormMode.FORM)

    /** What "New folder" has so far: the name typed and the apps picked, in order (RF-C4). */
    var newFolder: NewFolderDraft by mutableStateOf(NewFolderDraft())

    /** True while a page covers the tabs. */
    val isOnPage: Boolean
        get() = page != GroupSheetPage.Tabs

    fun selectTab(target: GroupTab) {
        tab = target
        page = GroupSheetPage.Tabs
    }

    fun open(target: GroupSheetPage) {
        if (target == GroupSheetPage.NewFolder) newFolder = NewFolderDraft()
        page = target
    }

    /** Opens the folder form of [folder] in place of the tabs. */
    fun openFolder(folder: AppFolder) {
        folderDraft = initialFolderDraft(folder)
        folderMode = FolderFormMode.FORM
        page = GroupSheetPage.FolderEditor(folder.id)
    }

    fun editFolderDraft(draft: FolderFormDraft) {
        folderDraft = draft
    }

    /**
     * Back from a page: the folder's icon grid goes back to its form, any other page to the tab it came from; true
     * when it went back, false on the tabs (the caller closes the sheet).
     */
    fun back(): Boolean {
        val current = page
        return when {
            current is GroupSheetPage.FolderEditor && folderMode == FolderFormMode.ALL_ICONS -> {
                folderMode = FolderFormMode.FORM
                true
            }
            current != GroupSheetPage.Tabs -> {
                page = GroupSheetPage.Tabs
                true
            }
            else -> false
        }
    }
}

/** The folder being made in "New folder": its [name] (blank takes the default) and the [apps] picked. */
data class NewFolderDraft(val name: String = "", val apps: List<String> = emptyList())
