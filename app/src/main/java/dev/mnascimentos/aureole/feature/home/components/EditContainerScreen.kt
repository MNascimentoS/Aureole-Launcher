package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.bottomsheet.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.DragMenu
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.icons.Undo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.components.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.home.widget.WidgetPositionIndicatorBar
import kotlinx.coroutines.delay
import java.util.UUID

enum class EditContainerScreenMode {
    MAIN, APPEARANCE, BEHAVIOR, MANAGE_ITEMS
}

private const val ANIMATION_DURATION_MS = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContainerScreen(
    panel: ContainerModel,
    allApps: List<AppInfo>,
    hazeState: HazeState? = null,
    onDismiss: () -> Unit,
    onSave: (ContainerModel) -> Unit
) {
    val state = rememberEditContainerState(panel)
    var currentMode by remember { mutableStateOf(EditContainerScreenMode.MAIN) }
    var isVisible by remember { mutableStateOf(true) }

    val config = ContainerConfig(
        panelId = panel.id,
        items = state.itemsList,
        folders = state.folders.map { it.copy(displayAsGrid = state.isGridFolderEnabled) },
        appPackageNames = state.itemsList.mapNotNull { it.packageName },
        position = state.position,
        orientation = state.orientation,
        isBackgroundEnabled = state.isBackgroundEnabled,
        isExpandCell = state.isExpandCell,
        showAddFolderButton = state.showAddFolderButton,
        showFolderLabels = state.showFolderLabels,
        isGridFolderEnabled = state.isGridFolderEnabled,
        hideText = state.hideText,
        textColor = state.textColor,
        backgroundColor = state.backgroundColor,
        strokeEnabled = state.strokeEnabled,
        strokeColor = state.strokeColor,
        folderColor = state.folderColor,
        folderStrokeEnabled = state.folderStrokeEnabled,
        scrollUpShortcut = state.scrollUpShortcut,
        scrollDownShortcut = state.scrollDownShortcut
    )

    val currentUiState = LocalHomeUiState.current
    val effectiveUiState = if (currentUiState.apps.isEmpty()) currentUiState.copy(apps = allApps) else currentUiState

    CompositionLocalProvider(LocalHomeUiState provides effectiveUiState) {
        if (!isVisible) {
            LaunchedEffect(isVisible) {
                delay(ANIMATION_DURATION_MS.toLong())
                onDismiss()
            }
        }

        val isHorizontal = state.orientation.equals("Horizontal", ignoreCase = true)
        val previewModifier = if (isHorizontal) {
            Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .heightIn(min = 60.dp, max = 120.dp)
        } else {
            Modifier
                .wrapContentHeight(Alignment.Bottom)
                .widthIn(min = 60.dp, max = 120.dp)
        }

        val titleText = when (currentMode) {
            EditContainerScreenMode.MAIN -> "Container settings"
            EditContainerScreenMode.APPEARANCE -> "Aparência"
            EditContainerScreenMode.BEHAVIOR -> "Comportamento"
            EditContainerScreenMode.MANAGE_ITEMS -> "Gerenciar Itens"
        }

        val homeActions = LocalHomeActions.current

        AureoleEditOverlay(
            onDismissRequest = {
                onSave(state.toModel())
                isVisible = false
            },
            title = titleText,
            onSettingsClick = { homeActions.onSettingsClick() },
            hazeState = hazeState,
            isVisible = isVisible,
            animated = true,
            onBackPress = {
                if (currentMode != EditContainerScreenMode.MAIN) {
                    currentMode = EditContainerScreenMode.MAIN
                } else {
                    onSave(state.toModel())
                    isVisible = false
                }
            },
            previewContent = {
                Container(config = config, onFolderClick = { _, _ -> }, modifier = previewModifier)
            }
        ) {
            when (currentMode) {
                EditContainerScreenMode.MAIN -> {
                    MainSettingsContent(
                        onAppearanceClick = { currentMode = EditContainerScreenMode.APPEARANCE },
                        onBehaviorClick = { currentMode = EditContainerScreenMode.BEHAVIOR },
                        onManageItemsClick = { currentMode = EditContainerScreenMode.MANAGE_ITEMS },
                        onSave = {
                            onSave(state.toModel())
                            isVisible = false
                        }
                    )
                }
                EditContainerScreenMode.APPEARANCE, EditContainerScreenMode.BEHAVIOR -> {
                    AppearanceSettingsContent(
                        state = state,
                        isBehaviorMode = currentMode == EditContainerScreenMode.BEHAVIOR,
                        onBack = { currentMode = EditContainerScreenMode.MAIN }
                    )
                }
                EditContainerScreenMode.MANAGE_ITEMS -> {
                    ManageItemsContent(
                        state = state,
                        allApps = allApps,
                        onBack = { currentMode = EditContainerScreenMode.MAIN }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainSettingsContent(
    onAppearanceClick: () -> Unit,
    onBehaviorClick: () -> Unit,
    onManageItemsClick: () -> Unit,
    onSave: () -> Unit
) {
    Text(
        text = "Container settings",
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        color = AureoleTheme.colors.onSurfaceHigh,
        textAlign = TextAlign.Center
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAppearanceClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.ColorFill(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Appearance", color = AureoleTheme.colors.onSurfaceHigh)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBehaviorClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Click(tint = AureoleTheme.colors.onSurfaceMedium, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Behavior", color = AureoleTheme.colors.onSurfaceHigh)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onManageItemsClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.DragMenu(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "Manage Items", color = AureoleTheme.colors.onSurfaceHigh)
    }

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Salvar")
    }
}

@Composable
private fun AppearanceSettingsContent(
    state: EditContainerState,
    isBehaviorMode: Boolean,
    onBack: () -> Unit
) {
    Text(
        text = if (isBehaviorMode) "Container behavior" else "Appearance",
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        color = AureoleTheme.colors.onSurfaceHigh,
        textAlign = TextAlign.Center
    )

    if (isBehaviorMode) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Expand cells", color = AureoleTheme.colors.onSurfaceMedium)
            Switch(checked = state.isExpandCell, onCheckedChange = { state.isExpandCell = it })
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Grid folders", color = AureoleTheme.colors.onSurfaceMedium)
            Switch(checked = state.isGridFolderEnabled, onCheckedChange = { state.isGridFolderEnabled = it })
        }
    } else {
        EditContainerOrientationSelector(
            orientation = state.orientation,
            onOrientationSelected = { state.orientation = it }
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Background enabled", color = AureoleTheme.colors.onSurfaceMedium)
            Switch(checked = state.isBackgroundEnabled, onCheckedChange = { state.isBackgroundEnabled = it })
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show folder labels", color = AureoleTheme.colors.onSurfaceMedium)
            Switch(checked = state.showFolderLabels, onCheckedChange = { state.showFolderLabels = it })
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
        Text("Voltar")
    }
}

@Composable
private fun ManageItemsContent(
    state: EditContainerState,
    allApps: List<AppInfo>,
    onBack: () -> Unit
) {
    var isCreateFolderDialogVisible by remember { mutableStateOf(false) }

    if (isCreateFolderDialogVisible) {
        CreateFolderDialog(
            onDismiss = { isCreateFolderDialogVisible = false },
            onSubmit = { name ->
                state.addFolder(name)
                isCreateFolderDialogVisible = false
            }
        )
    }

    Text(
        text = "Componentes Filhos (${state.itemsList.size})",
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        color = AureoleTheme.colors.onSurfaceHigh
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AureoleDS.dimens.small),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            EditContainerAddAppDropdown(
                allApps = allApps,
                onAppSelected = { state.addApp(it) },
                label = "App..."
            )
        }
        OutlinedButton(
            onClick = { isCreateFolderDialogVisible = true },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AureoleTheme.colors.outline.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            contentPadding = PaddingValues(horizontal = 12.dp),
            modifier = Modifier.height(52.dp)
        ) {
            AureoleDS.icons.Add(
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Pasta", style = AureoleTheme.typography.labelLarge)
        }
    }

    ManageItemsListSection(state = state, allApps = allApps)

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Voltar")
    }
}

@Composable
private fun ManageItemsListSection(
    state: EditContainerState,
    allApps: List<AppInfo>
) {
    if (state.itemsList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AureoleTheme.colors.surfaceVariant.copy(alpha = 0.25f),
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum componente adicionado ainda.",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(state.itemsList, key = { _, item -> item.id }) { index, item ->
                ManageItemRow(
                    params = ManageItemRowParams(
                        index = index,
                        item = item,
                        totalItems = state.itemsList.size,
                        folders = state.folders,
                        allApps = allApps
                    ),
                    actions = ManageItemRowActions(
                        onMoveUp = { state.moveItemUp(it) },
                        onMoveDown = { state.moveItemDown(it) },
                        onRemove = { state.removeItem(it) }
                    )
                )
            }
        }
    }
}

private data class ManageItemRowParams(
    val index: Int,
    val item: ContainerItemEntity,
    val totalItems: Int,
    val folders: List<AppFolder>,
    val allApps: List<AppInfo>
)

private data class ManageItemRowActions(
    val onMoveUp: (Int) -> Unit,
    val onMoveDown: (Int) -> Unit,
    val onRemove: (Int) -> Unit
)

@Composable
private fun ManageItemRow(
    params: ManageItemRowParams,
    actions: ManageItemRowActions
) {
    val index = params.index
    val item = params.item
    val title = if (item.itemType == ContainerItemType.FOLDER) {
        params.folders.find { it.id == item.folderId }?.name ?: "Pasta"
    } else {
        params.allApps.find { it.packageName == item.packageName }?.label ?: item.packageName ?: "App"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                style = AureoleTheme.typography.labelMedium,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f)
        )

        ManageItemRowActionButtons(
            index = index,
            totalItems = params.totalItems,
            actions = actions
        )
    }
}

@Composable
private fun ManageItemRowActionButtons(
    index: Int,
    totalItems: Int,
    actions: ManageItemRowActions
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (index > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { actions.onMoveUp(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↑",
                    color = AureoleTheme.colors.onSurfaceHigh,
                    fontSize = 12.sp
                )
            }
        }
        if (index < totalItems - 1) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { actions.onMoveDown(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↓",
                    color = AureoleTheme.colors.onSurfaceHigh,
                    fontSize = 12.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { actions.onRemove(index) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontSize = 12.sp
            )
        }
    }
}

private class EditContainerState(
    val panel: ContainerModel,
    options: ContainerOptionsState,
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>
) {
    var position by mutableStateOf(options.position)
    var orientation by mutableStateOf(options.orientation)
    var isBackgroundEnabled by mutableStateOf(options.isBackgroundEnabled)
    var isExpandCell by mutableStateOf(options.isExpandCell)
    var showAddFolderButton by mutableStateOf(options.showAddFolderButton)
    var showFolderLabels by mutableStateOf(options.showFolderLabels)
    var isGridFolderEnabled by mutableStateOf(options.isGridFolderEnabled)
    var hideText by mutableStateOf(options.hideText)
    var textColor by mutableStateOf(options.textColor)
    var backgroundColor by mutableStateOf(options.backgroundColor)
    var strokeEnabled by mutableStateOf(options.strokeEnabled)
    var strokeColor by mutableStateOf(options.strokeColor)
    var folderColor by mutableStateOf(options.folderColor)
    var folderStrokeEnabled by mutableStateOf(options.folderStrokeEnabled)
    var scrollUpShortcut by mutableStateOf(options.scrollUpShortcut)
    var scrollDownShortcut by mutableStateOf(options.scrollDownShortcut)
    var resetOnWakeUp by mutableStateOf(false)
    var resetOnUsage by mutableStateOf(false)
    var itemsList by mutableStateOf(itemsList)
    var folders by mutableStateOf(folders)

    fun toModel(): ContainerModel {
        val updatedApps = itemsList
            .filter { it.itemType == ContainerItemType.APP }
            .mapNotNull { it.packageName }

        val updatedFolders = folders.filter { f ->
            itemsList.any { it.itemType == ContainerItemType.FOLDER && it.folderId == f.id }
        }.map { it.copy(displayAsGrid = isGridFolderEnabled) }

        return panel.copy(
            position = position,
            orientation = orientation,
            isBackgroundEnabled = isBackgroundEnabled,
            isExpandCell = isExpandCell,
            showAddFolderButton = showAddFolderButton,
            showFolderLabels = showFolderLabels,
            isGridFolderEnabled = isGridFolderEnabled,
            hideText = hideText,
            textColor = textColor,
            backgroundColor = backgroundColor,
            strokeEnabled = strokeEnabled,
            strokeColor = strokeColor,
            folderColor = folderColor,
            folderStrokeEnabled = folderStrokeEnabled,
            scrollUpShortcut = scrollUpShortcut,
            scrollDownShortcut = scrollDownShortcut,
            items = itemsList.mapIndexed { idx, item -> item.copy(orderIndex = idx) },
            appPackageNames = updatedApps,
            folders = updatedFolders
        )
    }

    fun moveItemUp(index: Int) {
        if (index > 0) {
            val list = itemsList.toMutableList()
            val temp = list[index]
            list[index] = list[index - 1]
            list[index - 1] = temp
            itemsList = list
        }
    }

    fun moveItemDown(index: Int) {
        if (index < itemsList.size - 1) {
            val list = itemsList.toMutableList()
            val temp = list[index]
            list[index] = list[index + 1]
            list[index + 1] = temp
            itemsList = list
        }
    }

    fun removeItem(index: Int) {
        val list = itemsList.toMutableList()
        if (index in list.indices) {
            val removed = list.removeAt(index)
            itemsList = list
            if (removed.itemType == ContainerItemType.FOLDER) {
                folders = folders.filterNot { it.id == removed.folderId }
            }
        }
    }

    fun addFolder(name: String) {
        val newFolder = AppFolder(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            name = name,
            displayAsGrid = isGridFolderEnabled
        )
        folders = folders + newFolder
        itemsList = itemsList + ContainerItemEntity(
            id = UUID.randomUUID().toString(),
            panelId = panel.id,
            itemType = ContainerItemType.FOLDER,
            folderId = newFolder.id,
            orderIndex = itemsList.size
        )
    }

    fun addApp(pkg: String) {
        if (itemsList.none { it.itemType == ContainerItemType.APP && it.packageName == pkg }) {
            itemsList = itemsList + ContainerItemEntity(
                id = UUID.randomUUID().toString(),
                panelId = panel.id,
                itemType = ContainerItemType.APP,
                packageName = pkg,
                orderIndex = itemsList.size
            )
        }
    }
}

@Composable
private fun rememberEditContainerState(panel: ContainerModel): EditContainerState {
    return remember(panel.id) {
        val initialItemsList = if (panel.items.isNotEmpty()) {
            panel.items
        } else {
            val list = mutableListOf<ContainerItemEntity>()
            var idx = 0
            panel.folders.forEach { f ->
                list.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.FOLDER,
                        folderId = f.id,
                        orderIndex = idx++
                    )
                )
            }
            panel.appPackageNames.forEach { pkg ->
                list.add(
                    ContainerItemEntity(
                        id = UUID.randomUUID().toString(),
                        panelId = panel.id,
                        itemType = ContainerItemType.APP,
                        packageName = pkg,
                        orderIndex = idx++
                    )
                )
            }
            list
        }

        EditContainerState(
            panel = panel,
            options = ContainerOptionsState(
                position = panel.position,
                orientation = panel.orientation,
                isBackgroundEnabled = panel.isBackgroundEnabled,
                isExpandCell = panel.isExpandCell,
                showAddFolderButton = panel.showAddFolderButton,
                showFolderLabels = panel.showFolderLabels,
                isGridFolderEnabled = panel.isGridFolderEnabled,
                hideText = panel.hideText,
                textColor = panel.textColor,
                backgroundColor = panel.backgroundColor,
                strokeEnabled = panel.strokeEnabled,
                strokeColor = panel.strokeColor,
                folderColor = panel.folderColor,
                folderStrokeEnabled = panel.folderStrokeEnabled,
                scrollUpShortcut = panel.scrollUpShortcut,
                scrollDownShortcut = panel.scrollDownShortcut
            ),
            itemsList = initialItemsList,
            folders = panel.folders
        )
    }
}

data class ContainerOptionsState(
    val position: String,
    val orientation: String,
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean,
    val hideText: Boolean,
    val textColor: Long?,
    val backgroundColor: Long?,
    val strokeEnabled: Boolean,
    val strokeColor: Long?,
    val folderColor: Long?,
    val folderStrokeEnabled: Boolean,
    val scrollUpShortcut: String?,
    val scrollDownShortcut: String?
)

@AureolePreview
@Composable
fun EditContainerScreenPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "App 1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        ),
        AppInfo(
            label = "App 2",
            packageName = "com.example.app2",
            componentName = ComponentName("com.example.app2", "MainActivity"),
            icon = ColorDrawable(0xFF2E7D32.toInt())
        ),
        AppInfo(
            label = "App 3",
            packageName = "com.example.app3",
            componentName = ComponentName("com.example.app3", "MainActivity"),
            icon = ColorDrawable(0xFFF57C00.toInt())
        )
    )
    val sampleFolders = listOf(
        AppFolder(id = "folder_1", name = "Social", icon = "folder", appPackageNames = listOf("com.example.app1"))
    )
    val panel = ContainerModel(
        id = "panel_1",
        title = "Favoritos",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleFolders,
        appPackageNames = sampleApps.map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_1",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleFolders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_1",
                itemType = ContainerItemType.APP,
                packageName = sampleApps.first().packageName,
                orderIndex = 1
            )
        )
    )
    AureoleLauncherTheme {
        EditContainerScreen(
            panel = panel,
            allApps = sampleApps,
            onDismiss = {},
            onSave = {}
        )
    }
}

@AureolePreview
@Composable
fun EditContainerScreenHorizontalPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "App 1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        ),
        AppInfo(
            label = "App 2",
            packageName = "com.example.app2",
            componentName = ComponentName("com.example.app2", "MainActivity"),
            icon = ColorDrawable(0xFF2E7D32.toInt())
        ),
        AppInfo(
            label = "App 3",
            packageName = "com.example.app3",
            componentName = ComponentName("com.example.app3", "MainActivity"),
            icon = ColorDrawable(0xFFF57C00.toInt())
        )
    )
    val sampleFolders = listOf(
        AppFolder(id = "folder_1", name = "Social", icon = "folder", appPackageNames = listOf("com.example.app1"))
    )
    val panel = ContainerModel(
        id = "panel_2",
        title = "Favoritos Horizontal",
        orientation = "Horizontal",
        isBackgroundEnabled = true,
        isGridFolderEnabled = true,
        folders = sampleFolders,
        appPackageNames = sampleApps.map { it.packageName },
        items = listOf(
            ContainerItemEntity(
                id = "item_f1",
                panelId = "panel_2",
                itemType = ContainerItemType.FOLDER,
                folderId = sampleFolders.first().id,
                orderIndex = 0
            ),
            ContainerItemEntity(
                id = "item_a1",
                panelId = "panel_2",
                itemType = ContainerItemType.APP,
                packageName = sampleApps.first().packageName,
                orderIndex = 1
            ),
            ContainerItemEntity(
                id = "item_a2",
                panelId = "panel_2",
                itemType = ContainerItemType.APP,
                packageName = sampleApps[1].packageName,
                orderIndex = 2
            )
        )
    )
    AureoleLauncherTheme {
        EditContainerScreen(
            panel = panel,
            allApps = sampleApps,
            onDismiss = {},
            onSave = {}
        )
    }
}
