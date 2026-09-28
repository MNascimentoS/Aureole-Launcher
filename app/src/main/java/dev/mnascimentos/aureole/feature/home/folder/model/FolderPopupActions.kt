package dev.mnascimentos.aureole.feature.home.folder.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

data class FolderPopupActions(
    val onDismiss: () -> Unit,
    val onAppClick: (AppInfo) -> Unit,
    val onAddAppsClick: () -> Unit,
    val onEditFolderClick: () -> Unit
)
