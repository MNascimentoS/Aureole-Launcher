package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import dev.mnascimentos.aureole.core.data.model.FolderDetails

sealed interface FolderViewIntent {
    data class OpenCreateFolderDialog(val panelId: String? = null) : FolderViewIntent
    /** Creates the folder; [pickApps] then opens the folder's app picker (pasta-form RF-11). */
    data class SubmitFolderName(val details: FolderDetails, val pickApps: Boolean = false) : FolderViewIntent
    data class OpenFolder(val folderId: String, val topYPx: Float = 0f) : FolderViewIntent
    object CloseFolder : FolderViewIntent
    data class LaunchApp(val packageName: String) : FolderViewIntent
    data class AddAppToFolder(val folderId: String) : FolderViewIntent
    data class SaveFolderApps(val folderId: String, val selectedPackageNames: List<String>) : FolderViewIntent
    data class RenameFolder(val folderId: String, val details: FolderDetails) : FolderViewIntent
    data class DeleteFolder(val folderId: String) : FolderViewIntent
}
