package dev.mnascimentos.aureole.data.model

import java.util.UUID

data class FolderEntity(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: String? = null,
    val iconFallback: String? = null,
    val icon: String? = null,
    val displayAsGrid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
