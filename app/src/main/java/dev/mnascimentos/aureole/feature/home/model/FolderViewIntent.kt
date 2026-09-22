package dev.mnascimentos.aureole.feature.home.model

sealed interface FolderViewIntent {
    data class OpenCreateFolderDialog(val panelId: String? = null) : FolderViewIntent
    data class SubmitFolderName(val name: String) : FolderViewIntent
    data class OpenFolder(val folderId: String, val topYPx: Float = 0f) : FolderViewIntent
    object CloseFolder : FolderViewIntent
    data class LaunchApp(val packageName: String) : FolderViewIntent
    data class AddAppToFolder(val folderId: String) : FolderViewIntent
    data class SaveFolderApps(val folderId: String, val selectedPackageNames: List<String>) : FolderViewIntent
    data class RenameFolder(val folderId: String, val newName: String, val icon: String? = null) : FolderViewIntent
    data class DeleteFolder(val folderId: String) : FolderViewIntent
}
