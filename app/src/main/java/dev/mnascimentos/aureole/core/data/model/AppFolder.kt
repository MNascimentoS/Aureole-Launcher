package dev.mnascimentos.aureole.core.data.model

import java.util.UUID

data class AppFolder(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconPackage: String? = null,
    val icon: String? = null,
    val appPackageNames: List<String> = emptyList(),
    val displayAsGrid: Boolean = false,
)
