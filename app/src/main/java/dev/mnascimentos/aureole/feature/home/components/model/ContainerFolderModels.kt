@file:Suppress("Filename", "MatchingDeclarationName")

package dev.mnascimentos.aureole.feature.home.components.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.AppFolder

data class ContainerFolderButtonParams(
    val folder: AppFolder,
    val isGridFolderEnabled: Boolean,
    val containerColor: Color,
    val textColor: Color
)
