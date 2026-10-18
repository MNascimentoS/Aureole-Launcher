package dev.mnascimentos.aureole.core.data.model

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class AppFolder(
    val id: String = UUID.randomUUID().toString(),
    val panelId: String? = null,
    val name: String,
    val iconPackage: String? = null,
    val icon: String? = null,
    val appPackageNames: List<String> = emptyList(),
    val displayAsGrid: Boolean = false,
)
