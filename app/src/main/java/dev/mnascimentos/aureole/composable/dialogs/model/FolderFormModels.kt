package dev.mnascimentos.aureole.composable.dialogs.model

import androidx.compose.ui.unit.dp

object FolderButtonMetrics {
    val SmallSize = 52.dp
    val MediumSize = 60.dp
    val LabelMaxWidth = 56.dp
    val ContainerMaxWidth = 68.dp
    val GridGap = 18.dp
}

data class FolderFormConfig(
    val initialName: String,
    val initialIcon: String?,
    val isEditing: Boolean,
    val submitText: String
)
