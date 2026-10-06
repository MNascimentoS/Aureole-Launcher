package dev.mnascimentos.aureole.feature.home.components.model

data class EditContainerFormState(
    val position: String,
    val orientation: String = "Vertical",
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean
)

data class EditContainerFormCallbacks(
    val onPositionChange: (String) -> Unit,
    val onOrientationChange: (String) -> Unit = {},
    val onBgChange: (Boolean) -> Unit,
    val onExpandChange: (Boolean) -> Unit,
    val onAddFolderBtnChange: (Boolean) -> Unit,
    val onFolderLabelsChange: (Boolean) -> Unit,
    val onGridFolderChange: (Boolean) -> Unit
)
