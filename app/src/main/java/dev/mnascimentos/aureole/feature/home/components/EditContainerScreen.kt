package dev.mnascimentos.aureole.feature.home.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.DragMenu
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import kotlinx.coroutines.delay
import java.util.UUID

enum class EditContainerScreenMode {
    MAIN, APPEARANCE, BEHAVIOR, MANAGE_ITEMS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContainerScreen(
    panel: ContainerModel,
    allApps: List<AppInfo>,
    isBlurEnabled: Boolean = true,
    hazeState: HazeState? = null,
    onDismiss: () -> Unit,
    onSave: (ContainerModel) -> Unit,
    onDeletePanel: (String) -> Unit
) {
    val state = rememberEditContainerState(panel)
    var currentMode by remember { mutableStateOf(EditContainerScreenMode.MAIN) }
    var isVisible by remember { mutableStateOf(true) }

    BackHandler(enabled = isVisible) {
        if (currentMode != EditContainerScreenMode.MAIN) {
            currentMode = EditContainerScreenMode.MAIN
        } else {
            onSave(state.toModel())
            isVisible = false
        }
    }

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

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(300)
        )
    ) {
        val hazeModifier = if (isBlurEnabled && hazeState != null) {
            Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 32.dp,
                    tint = HazeTint(Color.Black.copy(alpha = 0.55f))
                )
            ) {
                blurEnabled = true
            }
        } else {
            Modifier
        }

        val backgroundColor = if (isBlurEnabled && hazeState != null) {
            Color.Transparent
        } else {
            Color.Black
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(hazeModifier)
                .background(backgroundColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        onSave(state.toModel())
                        isVisible = false
                    }
                )
        ) {
            var bottomSheetHeight by remember { mutableStateOf(0) }
            val density = LocalDensity.current

            // Bottom Sheet Surface (fills width, consumes clicks)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { coordinates ->
                        bottomSheetHeight = coordinates.size.height
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume clicks on bottom sheet
                    ),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    when (currentMode) {
                        EditContainerScreenMode.MAIN -> {
                            MainSettingsContent(
                                state = state,
                                onAppearanceClick = { currentMode = EditContainerScreenMode.APPEARANCE },
                                onBehaviorClick = { currentMode = EditContainerScreenMode.BEHAVIOR },
                                onManageItemsClick = { currentMode = EditContainerScreenMode.MANAGE_ITEMS },
                                onSave = { onSave(state.toModel()) }
                            )
                        }
                        EditContainerScreenMode.APPEARANCE -> {
                            AppearanceSettingsContent(
                                state = state,
                                onBack = { currentMode = EditContainerScreenMode.MAIN }
                            )
                        }
                        EditContainerScreenMode.BEHAVIOR -> {
                            BehaviorSettingsContent(
                                state = state,
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

            // Preview Container positioned dynamically right above bottom sheet, leaving sides clickable
            val bottomSheetHeightDp = if (bottomSheetHeight == 0) 280.dp else with(density) { bottomSheetHeight.toDp() }
            val previewModifier = if (state.orientation.equals("Horizontal", ignoreCase = true)) {
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomSheetHeightDp + 12.dp)
                    .fillMaxWidth(0.85f)
                    .height(90.dp)
            } else {
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = bottomSheetHeightDp + 12.dp)
                    .width(80.dp)
                    .height(240.dp)
            }

            Container(
                config = config,
                onFolderClick = { _, _ -> },
                modifier = previewModifier
            )

            if (!isVisible) {
                LaunchedEffect(isVisible) {
                    delay(300)
                    onDismiss()
                }
            }
        }
    }
}

@Composable
private fun MainSettingsContent(
    state: EditContainerState,
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

    MenuItem(
        title = "Appearance",
        icon = {
            AureoleDS.icons.ColorFill(
                tint = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = onAppearanceClick
    )
    MenuItem(
        title = "Behavior",
        icon = { AureoleDS.icons.Click(tint = AureoleTheme.colors.onSurfaceMedium, modifier = Modifier.size(24.dp)) },
        onClick = onBehaviorClick
    )
    MenuItem(
        title = "Manage Items",
        icon = {
            AureoleDS.icons.DragMenu(
                tint = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = onManageItemsClick
    )

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
    onBack: () -> Unit
) {
    Text(
        text = "Appearance",
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        color = AureoleTheme.colors.onSurfaceHigh,
        textAlign = TextAlign.Center
    )

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
        Switch(
            checked = state.isBackgroundEnabled,
            onCheckedChange = { state.isBackgroundEnabled = it }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Show folder labels", color = AureoleTheme.colors.onSurfaceMedium)
        Switch(
            checked = state.showFolderLabels,
            onCheckedChange = { state.showFolderLabels = it }
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Voltar")
    }
}

@Composable
private fun BehaviorSettingsContent(
    state: EditContainerState,
    onBack: () -> Unit
) {
    Text(
        text = "Container behavior",
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
        color = AureoleTheme.colors.onSurfaceHigh,
        textAlign = TextAlign.Center
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Expand cells", color = AureoleTheme.colors.onSurfaceMedium)
        Switch(
            checked = state.isExpandCell,
            onCheckedChange = { state.isExpandCell = it }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Grid folders", color = AureoleTheme.colors.onSurfaceMedium)
        Switch(
            checked = state.isGridFolderEnabled,
            onCheckedChange = { state.isGridFolderEnabled = it }
        )
    }

    // TODO: Behavior configs (Scroll up/down)

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                val title: String
                val isFolder = item.itemType == ContainerItemType.FOLDER

                if (isFolder) {
                    val folder = state.folders.find { it.id == item.folderId }
                    title = folder?.name ?: "Pasta"
                } else {
                    val appInfo = allApps.find { it.packageName == item.packageName }
                    title = appInfo?.label ?: item.packageName ?: "App"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon placeholder
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                AureoleTheme.colors.surfaceVariant.copy(alpha = 0.8f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A", // Just a placeholder
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

                    // Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Up
                        if (index > 0) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { state.moveItemUp(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "↑",
                                    color = AureoleTheme.colors.onSurfaceHigh,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Down
                        if (index < state.itemsList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { state.moveItemDown(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "↓",
                                    color = AureoleTheme.colors.onSurfaceHigh,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Remove
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { state.removeItem(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "X",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Voltar")
    }
}

@Composable
private fun MenuItem(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = AureoleTheme.colors.onSurfaceHigh)
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
