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

    val hideText: Boolean = false,
    val textColor: Long? = null,
    val backgroundColor: Long? = null,
    val strokeEnabled: Boolean = false,
    val strokeColor: Long? = null,
    val folderColor: Long? = null,
    val folderStrokeEnabled: Boolean = false,
    val scrollUpShortcut: String? = null,
    val scrollDownShortcut: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)
