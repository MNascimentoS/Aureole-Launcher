package dev.mnascimentos.aureole.data.model

data class FolderItemEntity(
    val id: Long = 0,
    val folderId: String,
    val packageName: String,
    val itemOrder: Int = 0
)
