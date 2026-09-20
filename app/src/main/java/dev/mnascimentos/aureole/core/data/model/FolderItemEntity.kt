package dev.mnascimentos.aureole.core.data.model

data class FolderItemEntity(
    val id: Long = 0,
    val folderId: String,
    val packageName: String,
    val itemOrder: Int = 0
)
