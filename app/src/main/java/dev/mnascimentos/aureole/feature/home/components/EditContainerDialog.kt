package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.model.EditContainerFormCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.EditContainerFormState
import dev.mnascimentos.aureole.feature.home.folder.CreateFolderDialog
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContainerBottomSheet(
    panel: ContainerModel,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (ContainerModel) -> Unit,
    onDeletePanel: (String) -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AureoleDS.dimens.large)
                .padding(bottom = 32.dp)
        ) {
            EditContainerBottomSheetContent(
                panel = panel,
                allApps = allApps,
                onDismiss = onDismiss,
                onSave = onSave,
                onDeletePanel = onDeletePanel
            )
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius
                ),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium,
            dragHandle = null
        ) {
            sheetContent()
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
            position = panel.position,
            orientation = panel.orientation,
            isBackgroundEnabled = panel.isBackgroundEnabled,
            isExpandCell = panel.isExpandCell,
            showAddFolderButton = panel.showAddFolderButton,
            showFolderLabels = panel.showFolderLabels,
            isGridFolderEnabled = panel.isGridFolderEnabled,
            itemsList = initialItemsList,
            folders = panel.folders
        )
    }
}

private class EditContainerState(
    val panel: ContainerModel,
    position: String,
    orientation: String,
    isBackgroundEnabled: Boolean,
    isExpandCell: Boolean,
    showAddFolderButton: Boolean,
    showFolderLabels: Boolean,
    isGridFolderEnabled: Boolean,
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>
) {
    var position by mutableStateOf(position)
    var orientation by mutableStateOf(orientation)
    var isBackgroundEnabled by mutableStateOf(isBackgroundEnabled)
    var isExpandCell by mutableStateOf(isExpandCell)
    var showAddFolderButton by mutableStateOf(showAddFolderButton)
    var showFolderLabels by mutableStateOf(showFolderLabels)
    var isGridFolderEnabled by mutableStateOf(isGridFolderEnabled)
    var itemsList by mutableStateOf(itemsList)
    var folders by mutableStateOf(folders)

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
        list.removeAt(index)
        itemsList = list
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
private fun EditContainerBottomSheetContent(
    panel: ContainerModel,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (ContainerModel) -> Unit,
    onDeletePanel: (String) -> Unit
) {
    val state = rememberEditContainerState(panel)
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

    Column(modifier = Modifier.fillMaxWidth()) {
        EditContainerHeader(onDismiss = onDismiss)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        LazyColumn(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            editContainerFormItems(
                formState = EditContainerFormState(
                    position = state.position,
                    orientation = state.orientation,
                    isBackgroundEnabled = state.isBackgroundEnabled,
                    isExpandCell = state.isExpandCell,
                    showAddFolderButton = state.showAddFolderButton,
                    showFolderLabels = state.showFolderLabels,
                    isGridFolderEnabled = state.isGridFolderEnabled
                ),
                callbacks = EditContainerFormCallbacks(
                    onPositionChange = { state.position = it },
                    onOrientationChange = { state.orientation = it },
                    onBgChange = { state.isBackgroundEnabled = it },
                    onExpandChange = { state.isExpandCell = it },
                    onAddFolderBtnChange = { state.showAddFolderButton = it },
                    onFolderLabelsChange = { state.showFolderLabels = it },
                    onGridFolderChange = { state.isGridFolderEnabled = it }
                ),
                itemsList = state.itemsList,
                folders = state.folders,
                allApps = allApps,
                onAddApp = { state.addApp(it) },
                onAddFolder = { isCreateFolderDialogVisible = true },
                onMoveUp = { state.moveItemUp(it) },
                onMoveDown = { state.moveItemDown(it) },
                onRemoveItem = { state.removeItem(it) }
            )
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        EditContainerFooter(
            onDelete = { onDeletePanel(panel.id) },
            onSave = {
                val updatedApps = state.itemsList.filter { it.itemType == ContainerItemType.APP }.mapNotNull { it.packageName }
                val updated = panel.copy(
                    position = state.position,
                    orientation = state.orientation,
                    isBackgroundEnabled = state.isBackgroundEnabled,
                    isExpandCell = state.isExpandCell,
                    showAddFolderButton = state.showAddFolderButton,
                    showFolderLabels = state.showFolderLabels,
                    isGridFolderEnabled = state.isGridFolderEnabled,
                    items = state.itemsList.mapIndexed { idx, item -> item.copy(orderIndex = idx) },
                    appPackageNames = updatedApps,
                    folders = state.folders
                )
                onSave(updated)
            }
        )
    }
}

private fun LazyListScope.editContainerFormItems(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks,
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>,
    allApps: List<AppInfo>,
    onAddApp: (String) -> Unit,
    onAddFolder: () -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    item {
        AureoleText(
            text = "Adicionar ao Container",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EditContainerAddAppDropdown(allApps = allApps, onAppSelected = onAddApp)
            }
            OutlinedButton(
                onClick = onAddFolder,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AureoleTheme.colors.outline.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                AureoleDS.icons.Add(
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                AureoleText("Pasta", style = AureoleTheme.typography.labelLarge)
            }
        }
    }

    item {
        AureoleText(
            text = "Itens e Ordem (${itemsList.size})",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
    }

    if (itemsList.isEmpty()) {
        item {
            AureoleText(
                text = "Nenhum item adicionado ainda.",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    } else {
        itemsIndexed(itemsList, key = { _, item -> item.id }) { index, item ->
            val title: String
            val iconName: String?
            val isFolder = item.itemType == ContainerItemType.FOLDER

            if (isFolder) {
                val folder = folders.find { it.id == item.folderId }
                title = folder?.name ?: "Pasta"
                iconName = folder?.icon ?: "menu"
            } else {
                val appInfo = allApps.find { it.packageName == item.packageName }
                title = appInfo?.label ?: item.packageName ?: "App"
                iconName = "menu"
            }

            EditContainerItemRow(
                title = title,
                iconName = iconName,
                isFolder = isFolder,
                canMoveUp = index > 0,
                canMoveDown = index < itemsList.size - 1,
                onMoveUp = { onMoveUp(index) },
                onMoveDown = { onMoveDown(index) },
                onRemove = { onRemoveItem(index) }
            )
        }
    }

    item {
        Spacer(modifier = Modifier.height(8.dp))
        EditContainerOptionsExpandableSection(
            formState = formState,
            callbacks = callbacks
        )
    }
}

@Composable
private fun EditContainerOptionsExpandableSection(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AureoleText(
                text = "Opções de Aparência e Comportamento",
                style = AureoleTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(32.dp)
            ) {
                if (isExpanded) {
                    AureoleDS.icons.ArrowUp(
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                } else {
                    AureoleDS.icons.ArrowDown(
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
                modifier = Modifier.padding(top = AureoleDS.dimens.small)
            ) {
                EditContainerOrientationSelector(
                    orientation = formState.orientation,
                    onOrientationSelected = callbacks.onOrientationChange
                )

                EditContainerAlignmentSelector(
                    position = formState.position,
                    onPositionSelected = callbacks.onPositionChange
                )

                SettingSwitchRow(
                    label = "Fundo do Container",
                    checked = formState.isBackgroundEnabled,
                    onCheckedChange = callbacks.onBgChange
                )

                SettingSwitchRow(
                    label = "Expandir Célula",
                    checked = formState.isExpandCell,
                    onCheckedChange = callbacks.onExpandChange
                )

                SettingSwitchRow(
                    label = "Botão de Criar Pasta",
                    checked = formState.showAddFolderButton,
                    onCheckedChange = callbacks.onAddFolderBtnChange
                )

                SettingSwitchRow(
                    label = "Exibir Rótulos das Pastas",
                    checked = formState.showFolderLabels,
                    onCheckedChange = callbacks.onFolderLabelsChange
                )

                SettingSwitchRow(
                    label = "Modo de Pasta Expansiva (Grid)",
                    checked = formState.isGridFolderEnabled,
                    onCheckedChange = callbacks.onGridFolderChange
                )
            }
        }
    }
}

@Composable
private fun EditContainerHeader(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = "Editar Container",
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EditContainerOrientationSelector(
    orientation: String,
    onOrientationSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xSmall)
    ) {
        AureoleText(
            text = "Orientação do Container",
            style = AureoleTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            val isVertical = orientation.equals("Vertical", ignoreCase = true)
            val isHorizontal = orientation.equals("Horizontal", ignoreCase = true)

            if (isVertical) {
                Button(
                    onClick = { onOrientationSelected("Vertical") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    AureoleText(text = "Vertical")
                }
            } else {
                OutlinedButton(
                    onClick = { onOrientationSelected("Vertical") },
                    modifier = Modifier.weight(1f)
                ) {
                    AureoleText(text = "Vertical", color = AureoleTheme.colors.onSurfaceHigh)
                }
            }

            if (isHorizontal) {
                Button(
                    onClick = { onOrientationSelected("Horizontal") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    AureoleText(text = "Horizontal")
                }
            } else {
                OutlinedButton(
                    onClick = { onOrientationSelected("Horizontal") },
                    modifier = Modifier.weight(1f)
                ) {
                    AureoleText(text = "Horizontal", color = AureoleTheme.colors.onSurfaceHigh)
                }
            }
        }
    }
}

private val positionOptionsMap = listOf(
    "Space Between" to "Espaçamento Entre (Separados)",
    "Space Evenly" to "Espaçamento Igual (Igualitário)",
    "Top" to "Início (Topo / Esquerda)",
    "Center" to "Centro",
    "Bottom" to "Fim (Base / Direita)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContainerAlignmentSelector(
    position: String,
    onPositionSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val displayValue = positionOptionsMap.find { it.first == position }?.second ?: position

    AureoleText(
        text = "Alinhamento dos Itens",
        style = AureoleTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = AureoleTheme.colors.outline,
                focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            positionOptionsMap.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { AureoleText(label, color = AureoleTheme.colors.onSurfaceHigh) },
                    onClick = {
                        onPositionSelected(key)
                        isDropdownExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EditContainerItemRow(
    title: String,
    iconName: String?,
    isFolder: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            IconButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                modifier = Modifier.size(28.dp)
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = if (canMoveUp) {
                        AureoleTheme.colors.onSurfaceHigh
                    } else {
                        AureoleTheme.colors.onSurfaceMedium.copy(
                            alpha = 0.3f
                        )
                    }
                )
            }
            IconButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                modifier = Modifier.size(28.dp)
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = if (canMoveDown) {
                        AureoleTheme.colors.onSurfaceHigh
                    } else {
                        AureoleTheme.colors.onSurfaceMedium.copy(
                            alpha = 0.3f
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        FolderIconRegistry.RenderIcon(
            name = iconName,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

        AureoleText(
            text = title,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f)
        )

        AureoleText(
            text = if (isFolder) "Pasta" else "App",
            style = AureoleTheme.typography.labelSmall,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 4.dp)
        )

        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContainerAddAppDropdown(
    allApps: List<AppInfo>,
    onAppSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, allApps) {
        if (searchQuery.isBlank()) {
            allApps.take(10)
        } else {
            allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }.take(10)
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isExpanded = true
            },
            placeholder = {
                AureoleText(
                    text = "Buscar app...",
                    style = AureoleTheme.typography.bodyMedium,
                    color = AureoleTheme.colors.onSurfaceLow,
                    maxLines = 1
                )
            },
            leadingIcon = {
                AureoleDS.icons.Search(
                    tint = AureoleTheme.colors.onSurfaceMedium,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .height(48.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = AureoleTheme.colors.outline.copy(alpha = 0.5f),
                focusedContainerColor = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            filteredApps.forEach { appInfo ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val iconBitmap = remember(appInfo.packageName) { appInfo.getIconBitmap() }
                            Image(
                                bitmap = iconBitmap,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AureoleText(appInfo.label, color = AureoleTheme.colors.onSurfaceHigh)
                        }
                    },
                    onClick = {
                        onAppSelected(appInfo.packageName)
                        searchQuery = ""
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun EditContainerFooter(
    onDelete: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            AureoleText("Excluir")
        }

        Button(onClick = onSave) {
            AureoleText("Salvar")
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = label,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@AureolePreview
@Composable
fun EditContainerBottomSheetPreview() {
    val samplePanel = ContainerModel(id = "1")
    AureoleLauncherTheme {
        EditContainerBottomSheet(
            panel = samplePanel,
            allApps = emptyList(),
            onDismiss = {},
            onSave = {},
            onDeletePanel = {}
        )
    }
}
