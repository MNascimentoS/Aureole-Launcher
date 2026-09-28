package dev.mnascimentos.aureole.feature.home.components.model

data class EditSidePanelFormState(
    val title: String,
    val position: String,
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean
)

data class EditSidePanelFormCallbacks(
    val onTitleChange: (String) -> Unit,
    val onPositionChange: (String) -> Unit,
    val onBgChange: (Boolean) -> Unit,
    val onExpandChange: (Boolean) -> Unit,
    val onAddFolderBtnChange: (Boolean) -> Unit,
    val onFolderLabelsChange: (Boolean) -> Unit,
    val onGridFolderChange: (Boolean) -> Unit
)
