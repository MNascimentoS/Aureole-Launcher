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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerAddFolderButton
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerAppItem
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderItem
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerAppItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerColumnConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRenderItem
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRowConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

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
            val updatedFolder = renderItem.folder.copy(
                panelId = config.panelId,
                displayAsGrid = config.isGridFolderEnabled || renderItem.folder.displayAsGrid
            )
            ContainerFolderItem(
                params = ContainerFolderItemParams(
                    folder = renderItem.folder,
                    isOpened = renderItem.folder.id == config.openedFolderId,
                    config = ContainerFolderConfig(
                        showFolderLabels = config.showFolderLabels,
                        isGridFolderEnabled = config.isGridFolderEnabled,
                        isBackgroundEnabled = config.isBackgroundEnabled
                    ),
                    onFolderClick = { _, y -> columnConfig.onFolderClick(updatedFolder, y) },
                    onLongClick = { actions.onOpenContainerFolderBottomSheet(renderItem.folder, config.panelId) }
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
            val updatedFolder = renderItem.folder.copy(
                panelId = config.panelId,
                displayAsGrid = config.isGridFolderEnabled || renderItem.folder.displayAsGrid
            )
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
                        onFolderClick = { _, y -> rowConfig.onFolderClick(updatedFolder, y) },
                        onLongClick = { actions.onOpenContainerFolderBottomSheet(renderItem.folder, config.panelId) }
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
                        onLongClick = { actions.onOpenContainerAppBottomSheet(renderItem.appInfo, config.panelId) }
                    )
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun ContainerColumnPreview() {
    AureoleLauncherTheme {
        ContainerColumn(
            config = ContainerConfig(
                panelId = "preview_column",
                isBackgroundEnabled = true,
                showAddFolderButton = true
            ),
            columnConfig = ContainerColumnConfig(
                backgroundColor = AureoleDS.colors.surface,
                verticalArrangement = Arrangement.Center,
                onFolderClick = { _, _ -> }
            )
        )
    }
}

@AureolePreview
@Composable
private fun ContainerRowPreview() {
    AureoleLauncherTheme {
        ContainerRow(
            config = ContainerConfig(
                panelId = "preview_row",
                isBackgroundEnabled = true,
                showAddFolderButton = true
            ),
            rowConfig = ContainerRowConfig(
                backgroundColor = AureoleDS.colors.surface,
                horizontalArrangement = Arrangement.Center,
                onFolderClick = { _, _ -> }
            )
        )
    }
}
