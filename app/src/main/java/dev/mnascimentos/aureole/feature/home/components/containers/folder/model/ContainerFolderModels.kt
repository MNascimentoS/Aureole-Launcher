@file:Suppress("Filename", "MatchingDeclarationName")

package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.AppFolder

data class ContainerFolderButtonParams(
    val folder: AppFolder,
    val isGridFolderEnabled: Boolean,
    val containerColor: Color,
    val textColor: Color
)

data class ContainerFolderBottomSheetParams(
    val onEditShortcuts: () -> Unit,
    val onRename: () -> Unit,
    val onIcon: () -> Unit,
    val onRemoveFolder: () -> Unit,
    val onOpenContainerSettings: () -> Unit,
)
