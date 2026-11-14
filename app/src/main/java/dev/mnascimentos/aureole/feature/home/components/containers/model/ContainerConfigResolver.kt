package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * The config of the container [item], read from the model saved under the item's own id. Before the model loads the
 * container is an empty one with the initial values, never the global settings, so two containers (also inside a
 * scroll group) can never share items or config.
 */
internal fun MainUiState.containerConfigFor(item: LauncherItemState, hazeState: HazeState?): ContainerConfig {
    val model = containers[item.id] ?: ContainerModel(id = item.id)
    return ContainerConfig(
        panelId = model.id,
        title = model.title,
        items = model.items,
        folders = model.folders,
        appPackageNames = model.appPackageNames,
        quickButtons = model.quickButtons,
        openedFolderId = openedFolderId,
        position = model.position,
        orientation = model.orientation,
        showFolderLabels = model.showFolderLabels,
        showAddFolderButton = model.showAddFolderButton,
        isBackgroundEnabled = model.isBackgroundEnabled,
        isExpandCell = model.isExpandCell,
        isGridFolderEnabled = model.isGridFolderEnabled,
        hideText = model.hideText,
        textColor = model.textColor,
        backgroundColor = model.backgroundColor,
        strokeEnabled = model.strokeEnabled,
        strokeColor = model.strokeColor,
        folderColor = model.folderColor,
        folderStrokeEnabled = model.folderStrokeEnabled,
        scrollUpShortcut = model.scrollUpShortcut,
        scrollDownShortcut = model.scrollDownShortcut,
        hazeState = hazeState
    )
}
