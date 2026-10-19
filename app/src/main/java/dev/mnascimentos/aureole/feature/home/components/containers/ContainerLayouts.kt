package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerAddFolderButton
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerAppItem
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderItem
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerAppItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

internal data class ContainerColumnConfig(
    val backgroundColor: Color,
    val verticalArrangement: Arrangement.Vertical,
    val onFolderClick: (AppFolder, Float) -> Unit,
    val shouldExpand: Boolean = false
)

internal data class ContainerRowConfig(
    val backgroundColor: Color,
    val horizontalArrangement: Arrangement.Horizontal,
    val onFolderClick: (AppFolder, Float) -> Unit,
    val shouldExpand: Boolean = false
)

internal sealed class ContainerRenderItem {
    data class Folder(val folder: AppFolder) : ContainerRenderItem()
    data class App(val appInfo: AppInfo) : ContainerRenderItem()
}

internal fun resolveContainerRenderItems(
    config: ContainerConfig,
    allApps: List<AppInfo>
): List<ContainerRenderItem> {
    if (config.items.isNotEmpty()) {
        return config.items.mapNotNull { resolveConfigItem(it, config, allApps) }
    }
    val folderItems = config.folders.map { ContainerRenderItem.Folder(it) }
    val appItems = config.appPackageNames.mapNotNull { pkg ->
        allApps.find { it.packageName == pkg }?.let { ContainerRenderItem.App(it) }
    }
    return folderItems + appItems
}

private fun resolveConfigItem(
    item: ContainerItemEntity,
    config: ContainerConfig,
    allApps: List<AppInfo>
): ContainerRenderItem? = when (item.itemType) {
    ContainerItemType.FOLDER -> config.folders.find { it.id == item.folderId }?.let { ContainerRenderItem.Folder(it) }
    ContainerItemType.APP -> allApps.find { it.packageName == item.packageName }?.let { ContainerRenderItem.App(it) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ContainerColumn(
    config: ContainerConfig,
    columnConfig: ContainerColumnConfig,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current
    val uiState = LocalHomeUiState.current
    val scrollState = rememberScrollState()

    val renderItems = resolveContainerRenderItems(config, uiState.apps)

    val paddingHorizontal = if (config.isBackgroundEnabled) 10.dp else 2.dp
    val paddingVertical = if (config.isBackgroundEnabled) 8.dp else 4.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnConfig.verticalArrangement,
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(if (columnConfig.shouldExpand) Modifier.fillMaxSize() else Modifier)
            .then(modifier)
            .background(columnConfig.backgroundColor)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onLongClick = { actions.onOpenEditContainerDialog(config.panelId) }
            )
            .semantics(mergeDescendants = true) {}
            .fadingEdges(scrollState, edgeLength = 16.dp)
            .verticalScroll(scrollState)
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    ) {
        if (renderItems.isEmpty()) {
            if (config.showAddFolderButton) {
                ContainerAddFolderButton(
                    onClick = { actions.onOpenEditContainerDialog(config.panelId) },
                    isBackgroundEnabled = config.isBackgroundEnabled
                )
            }
        } else {
            renderItems.forEach { renderItem ->
                ColumnRenderItem(renderItem, config, columnConfig)
            }
        }
    }
}

@Composable
private fun ColumnRenderItem(
    renderItem: ContainerRenderItem,
    config: ContainerConfig,
    columnConfig: ContainerColumnConfig
) {
    val actions = LocalHomeActions.current
    when (renderItem) {
        is ContainerRenderItem.Folder -> {
            ContainerFolderItem(
                params = ContainerFolderItemParams(
                    folder = renderItem.folder,
                    isOpened = renderItem.folder.id == config.openedFolderId,
                    config = ContainerFolderConfig(
                        showFolderLabels = config.showFolderLabels,
                        isGridFolderEnabled = config.isGridFolderEnabled,
                        isBackgroundEnabled = config.isBackgroundEnabled
                    ),
                    onFolderClick = { f, y ->
                        val updatedFolder = f.copy(
                            panelId = config.panelId,
                            displayAsGrid = config.isGridFolderEnabled || f.displayAsGrid
                        )
                        columnConfig.onFolderClick(updatedFolder, y)
                    },
                    onLongClick = {
                        actions.onOpenContainerFolderBottomSheet(
                            renderItem.folder,
                            config.panelId
                        )
                    }
                )
            )
        }
        is ContainerRenderItem.App -> {
            ContainerAppItem(
                params = ContainerAppItemParams(
                    app = renderItem.appInfo,
                    showLabels = config.showFolderLabels,
                    isBackgroundEnabled = config.isBackgroundEnabled,
                    onAppClick = { appInfo -> actions.onAppClick(appInfo) },
                    onLongClick = { actions.onOpenContainerAppBottomSheet(renderItem.appInfo, config.panelId) }
                )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ContainerRow(
    config: ContainerConfig,
    rowConfig: ContainerRowConfig,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current
    val uiState = LocalHomeUiState.current
    val scrollState = rememberScrollState()

    val renderItems = resolveContainerRenderItems(config, uiState.apps)

    val paddingHorizontal = if (config.isBackgroundEnabled) 8.dp else 4.dp
    val paddingVertical = if (config.isBackgroundEnabled) 10.dp else 2.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = rowConfig.horizontalArrangement,
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(if (rowConfig.shouldExpand) Modifier.fillMaxSize() else Modifier)
            .then(modifier)
            .background(rowConfig.backgroundColor)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onLongClick = { actions.onOpenEditContainerDialog(config.panelId) }
            )
            .semantics(mergeDescendants = true) {}
            .fadingEdges(scrollState, edgeLength = 16.dp)
            .horizontalScroll(scrollState)
            .padding(horizontal = paddingHorizontal, vertical = paddingVertical)
    ) {
        if (renderItems.isEmpty()) {
            if (config.showAddFolderButton) {
                ContainerAddFolderButton(
                    onClick = { actions.onOpenEditContainerDialog(config.panelId) },
                    isBackgroundEnabled = config.isBackgroundEnabled
                )
            }
        } else {
            renderItems.forEach { renderItem ->
                RowRenderItem(renderItem, config, rowConfig)
            }
        }
    }
}

@Composable
private fun RowRenderItem(
    renderItem: ContainerRenderItem,
    config: ContainerConfig,
    rowConfig: ContainerRowConfig
) {
    val actions = LocalHomeActions.current
    when (renderItem) {
        is ContainerRenderItem.Folder -> {
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                ContainerFolderItem(
                    params = ContainerFolderItemParams(
                        folder = renderItem.folder,
                        isOpened = renderItem.folder.id == config.openedFolderId,
                        config = ContainerFolderConfig(
                            showFolderLabels = config.showFolderLabels,
                            isGridFolderEnabled = config.isGridFolderEnabled,
                            isBackgroundEnabled = config.isBackgroundEnabled
                        ),
                        onFolderClick = { f, y ->
                            val updatedFolder = f.copy(
                                panelId = config.panelId,
                                displayAsGrid = config.isGridFolderEnabled || f.displayAsGrid
                            )
                            rowConfig.onFolderClick(updatedFolder, y)
                        },
                        onLongClick = {
                            actions.onOpenContainerFolderBottomSheet(
                                renderItem.folder,
                                config.panelId
                            )
                        }
                    )
                )
            }
        }
        is ContainerRenderItem.App -> {
            Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                ContainerAppItem(
                    params = ContainerAppItemParams(
                        app = renderItem.appInfo,
                        showLabels = config.showFolderLabels,
                        isBackgroundEnabled = config.isBackgroundEnabled,
                        onAppClick = { appInfo -> actions.onAppClick(appInfo) },
                        onLongClick = {
                            actions.onOpenContainerAppBottomSheet(
                                renderItem.appInfo,
                                config.panelId
                            )
                        }
                    )
                )
            }
        }
    }
}
