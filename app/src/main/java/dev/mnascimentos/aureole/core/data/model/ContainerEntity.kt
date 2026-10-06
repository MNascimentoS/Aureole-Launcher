package dev.mnascimentos.aureole.core.data.model

import java.util.UUID

data class ContainerEntity(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Painel Lateral",
    val position: String = "Space Between",
    val orientation: String = "Vertical",
    val isBackgroundEnabled: Boolean = true,
    val isExpandCell: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val showFolderLabels: Boolean = false,
    val isGridFolderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
