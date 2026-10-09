package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val CONTAINER_HAZE_ALPHA_MULTIPLIER = 0.7f
private const val CONTAINER_MIN_ALPHA = 0.2f
private const val CONTAINER_MAX_ALPHA = 0.95f
private const val CONTAINER_DEFAULT_ALPHA = 0.95f

private fun Modifier.hazeModifier(
    hasHaze: Boolean,
    hazeState: HazeState?,
    hazeOpacity: Float,
    surfaceColor: Color
): Modifier {
    return if (hasHaze && hazeState != null) {
        this.then(
            Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 20.dp,
                    tint = HazeTint(surfaceColor.copy(alpha = hazeOpacity))
                )
            ) {
                blurEnabled = true
            }
        )
    } else {
        this
    }
}

private fun getBackgroundColor(
    isBgEnabled: Boolean,
    hasHaze: Boolean,
    hazeOpacity: Float,
    surfaceColor: Color
): Color {
    return if (isBgEnabled) {
        if (hasHaze) {
            val backgroundAlpha = (hazeOpacity * CONTAINER_HAZE_ALPHA_MULTIPLIER)
                .coerceIn(CONTAINER_MIN_ALPHA, CONTAINER_MAX_ALPHA)
            surfaceColor.copy(alpha = backgroundAlpha)
        } else {
            surfaceColor.copy(alpha = CONTAINER_DEFAULT_ALPHA)
        }
    } else {
        Color.Transparent
    }
}

private fun getVerticalArrangement(position: String): Arrangement.Vertical {
    return when (position) {
        "Top" -> Arrangement.Top
        "Center" -> Arrangement.Center
        "Bottom" -> Arrangement.Bottom
        "Space Evenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.SpaceBetween
    }
}

private fun getHorizontalArrangement(position: String): Arrangement.Horizontal {
    return when (position) {
        "Top", "Left" -> Arrangement.Start
        "Center" -> Arrangement.Center
        "Bottom", "Right" -> Arrangement.End
        "Space Evenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.SpaceBetween
    }
}

@Composable
fun Container(
    config: ContainerConfig,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = LocalHomeUiState.current
    val surfaceColor = AureoleDS.colors.surface
    val hasHaze = config.isBackgroundEnabled && uiState.isHazeEnabled && (config.hazeState != null)

    val hazeModifier = Modifier.hazeModifier(hasHaze, config.hazeState, uiState.hazeOpacity, surfaceColor)
    val backgroundColor = getBackgroundColor(config.isBackgroundEnabled, hasHaze, uiState.hazeOpacity, surfaceColor)
    val shouldExpand = config.isExpandCell

    val boxModifier = modifier.then(if (shouldExpand) Modifier.fillMaxSize() else Modifier)

    Box(
        modifier = boxModifier
    ) {
        if (config.orientation.equals("Horizontal", ignoreCase = true)) {
            val horizontalArrangement = getHorizontalArrangement(config.position)
            ContainerRow(
                config = config,
                backgroundColor = backgroundColor,
                horizontalArrangement = horizontalArrangement,
                onFolderClick = onFolderClick,
                modifier = if (config.isBackgroundEnabled) hazeModifier else Modifier,
                shouldExpand = shouldExpand
            )
        } else {
            val verticalArrangement = getVerticalArrangement(config.position)
            ContainerColumn(
                config = config,
                backgroundColor = backgroundColor,
                verticalArrangement = verticalArrangement,
                onFolderClick = onFolderClick,
                modifier = if (config.isBackgroundEnabled) hazeModifier else Modifier,
                shouldExpand = shouldExpand
            )
        }
    }
}

private sealed class ContainerRenderItem {
    data class Folder(val folder: AppFolder) : ContainerRenderItem()
    data class App(val appInfo: AppInfo) : ContainerRenderItem()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContainerColumn(
    config: ContainerConfig,
    backgroundColor: Color,
    verticalArrangement: Arrangement.Vertical,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
    shouldExpand: Boolean = false
) {
    val actions = LocalHomeActions.current
    val uiState = LocalHomeUiState.current
    val scrollState = rememberScrollState()

    val renderItems = remember(config.items, config.folders, config.appPackageNames, uiState.apps) {
        if (config.items.isNotEmpty()) {
            config.items.mapNotNull { item ->
                when (item.itemType) {
                    ContainerItemType.FOLDER -> {
                        config.folders.find { it.id == item.folderId }?.let { ContainerRenderItem.Folder(it) }
                    }
                    ContainerItemType.APP -> {
                        uiState.apps.find { it.packageName == item.packageName }?.let { ContainerRenderItem.App(it) }
                    }
                }
            }
        } else {
            val folderItems = config.folders.map { ContainerRenderItem.Folder(it) }
            val appItems = config.appPackageNames.mapNotNull { pkg ->
                uiState.apps.find { it.packageName == pkg }?.let { ContainerRenderItem.App(it) }
            }
            folderItems + appItems
        }
    }

    val paddingHorizontal = if (config.isBackgroundEnabled) 10.dp else 2.dp
    val paddingVertical = if (config.isBackgroundEnabled) 8.dp else 4.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement,
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .then(if (shouldExpand) Modifier.fillMaxSize() else Modifier)
            .then(modifier)
            .background(backgroundColor)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onLongClick = { actions.onOpenEditContainerDialog(config.panelId) }
            )
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
                when (renderItem) {
                    is ContainerRenderItem.Folder -> {
                        ContainerFolderItem(
                            folder = renderItem.folder,
                            isOpened = renderItem.folder.id == config.openedFolderId,
                            showFolderLabels = config.showFolderLabels,
                            isGridFolderEnabled = config.isGridFolderEnabled,
                            isBackgroundEnabled = config.isBackgroundEnabled,
                            onFolderClick = { f, y ->
                                val updatedFolder = f.copy(
                                    panelId = config.panelId,
                                    displayAsGrid = config.isGridFolderEnabled || f.displayAsGrid
                                )
                                onFolderClick(updatedFolder, y)
                            },
                            onLongClick = {
                                actions.onOpenContainerFolderBottomSheet(
                                    renderItem.folder,
                                    config.panelId
                                )
                            }
                        )
                    }
                    is ContainerRenderItem.App -> {
                        ContainerAppItem(
                            app = renderItem.appInfo,
                            showLabels = config.showFolderLabels,
                            isBackgroundEnabled = config.isBackgroundEnabled,
                            onAppClick = { appInfo -> actions.onAppClick(appInfo) },
                            onLongClick = { actions.onOpenContainerAppBottomSheet(renderItem.appInfo, config.panelId) }
                        )
                    }
                }
            }
            if (config.showAddFolderButton) {
                Spacer(modifier = Modifier.height(4.dp))
                ContainerAddFolderButton(
                    onClick = { actions.onFolderIntent(FolderViewIntent.OpenCreateFolderDialog(config.panelId)) },
                    isBackgroundEnabled = config.isBackgroundEnabled
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ContainerRow(
    config: ContainerConfig,
    backgroundColor: Color,
    horizontalArrangement: Arrangement.Horizontal,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
    shouldExpand: Boolean = false
) {
    val actions = LocalHomeActions.current
    val uiState = LocalHomeUiState.current
    val scrollState = rememberScrollState()

    val renderItems = remember(config.items, config.folders, config.appPackageNames, uiState.apps) {
        if (config.items.isNotEmpty()) {
            config.items.mapNotNull { item ->
                when (item.itemType) {
                    ContainerItemType.FOLDER -> {
                        config.folders.find { it.id == item.folderId }?.let { ContainerRenderItem.Folder(it) }
                    }
                    ContainerItemType.APP -> {
                        uiState.apps.find { it.packageName == item.packageName }?.let { ContainerRenderItem.App(it) }
                    }
                }
            }
        } else {
            val folderItems = config.folders.map { ContainerRenderItem.Folder(it) }
            val appItems = config.appPackageNames.mapNotNull { pkg ->
                uiState.apps.find { it.packageName == pkg }?.let { ContainerRenderItem.App(it) }
            }
            folderItems + appItems
        }
    }

    val paddingHorizontal = if (config.isBackgroundEnabled) 8.dp else 4.dp
    val paddingVertical = if (config.isBackgroundEnabled) 10.dp else 2.dp

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .then(if (shouldExpand) Modifier.fillMaxSize() else Modifier)
            .then(modifier)
            .background(backgroundColor)
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onLongClick = { actions.onOpenEditContainerDialog(config.panelId) }
            )
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
                when (renderItem) {
                    is ContainerRenderItem.Folder -> {
                        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                            ContainerFolderItem(
                                folder = renderItem.folder,
                                isOpened = renderItem.folder.id == config.openedFolderId,
                                showFolderLabels = config.showFolderLabels,
                                isGridFolderEnabled = config.isGridFolderEnabled,
                                isBackgroundEnabled = config.isBackgroundEnabled,
                                onFolderClick = { f, y ->
                                    val updatedFolder = f.copy(
                                        panelId = config.panelId,
                                        displayAsGrid = config.isGridFolderEnabled || f.displayAsGrid
                                    )
                                    onFolderClick(updatedFolder, y)
                                },
                                onLongClick = {
                                    actions.onOpenContainerFolderBottomSheet(
                                        renderItem.folder,
                                        config.panelId
                                    )
                                }
                            )
                        }
                    }
                    is ContainerRenderItem.App -> {
                        Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                            ContainerAppItem(
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
                        }
                    }
                }
            }
            if (config.showAddFolderButton) {
                Spacer(modifier = Modifier.width(4.dp))
                ContainerAddFolderButton(
                    onClick = { actions.onFolderIntent(FolderViewIntent.OpenCreateFolderDialog(config.panelId)) },
                    isBackgroundEnabled = config.isBackgroundEnabled
                )
            }
        }
    }
}

@AureolePreview
@Composable
fun ContainerPreview() {
    val mockFolder = AppFolder(id = "1", name = "Social")
    val config = ContainerConfig(
        folders = listOf(mockFolder),
        openedFolderId = null,
        showFolderLabels = true
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            Container(
                config = config,
                onFolderClick = { _, _ -> }
            )
        }
    }
}
