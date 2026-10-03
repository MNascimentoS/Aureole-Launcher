package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
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
    isBackgroundEnabled: Boolean,
    isExpandCell: Boolean,
    showAddFolderButton: Boolean,
    showFolderLabels: Boolean,
    isGridFolderEnabled: Boolean,
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>
) {
    var position by mutableStateOf(position)
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
                    isBackgroundEnabled = state.isBackgroundEnabled,
                    isExpandCell = state.isExpandCell,
                    showAddFolderButton = state.showAddFolderButton,
                    showFolderLabels = state.showFolderLabels,
                    isGridFolderEnabled = state.isGridFolderEnabled
                ),
                callbacks = EditContainerFormCallbacks(
                    onPositionChange = { state.position = it },
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
        EditContainerAlignmentSelector(position = formState.position, onPositionSelected = callbacks.onPositionChange)
    }
    item {
        SettingSwitchRow(
            label = "Fundo do Container",
            checked = formState.isBackgroundEnabled,
            onCheckedChange = callbacks.onBgChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Expandir Célula",
            checked = formState.isExpandCell,
            onCheckedChange = callbacks.onExpandChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Botão de Criar Pasta",
            checked = formState.showAddFolderButton,
            onCheckedChange = callbacks.onAddFolderBtnChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Exibir Rótulos das Pastas",
            checked = formState.showFolderLabels,
            onCheckedChange = callbacks.onFolderLabelsChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Modo de Pasta Expansiva (Grid)",
            checked = formState.isGridFolderEnabled,
            onCheckedChange = callbacks.onGridFolderChange
        )
    }
    editContainerItemsSection(
        itemsList = itemsList,
        folders = folders,
        allApps = allApps,
        onMoveUp = onMoveUp,
        onMoveDown = onMoveDown,
        onRemoveItem = onRemoveItem,
        onAddApp = onAddApp,
        onAddFolder = onAddFolder
    )
}

private fun LazyListScope.editContainerItemsSection(
    itemsList: List<ContainerItemEntity>,
    folders: List<AppFolder>,
    allApps: List<AppInfo>,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onAddApp: (String) -> Unit,
    onAddFolder: () -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        AureoleText(
            text = "Conteúdo do Container (${itemsList.size})",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }

    itemsIndexed(itemsList, key = { _, item -> item.id }) { index, item ->
        val title: String
        val iconVector: ImageVector
        val isFolder = item.itemType == ContainerItemType.FOLDER

        if (isFolder) {
            val folder = folders.find { it.id == item.folderId }
            title = folder?.name ?: "Pasta"
            iconVector = FolderIconRegistry.getIcon(folder?.icon) ?: Icons.Default.Menu
        } else {
            val appInfo = allApps.find { it.packageName == item.packageName }
            title = appInfo?.label ?: item.packageName ?: "App"
            iconVector = Icons.Default.Menu
        }

        EditContainerItemRow(
            title = title,
            iconVector = iconVector,
            isFolder = isFolder,
            canMoveUp = index > 0,
            canMoveDown = index < itemsList.size - 1,
            onMoveUp = { onMoveUp(index) },
            onMoveDown = { onMoveDown(index) },
            onRemove = { onRemoveItem(index) }
        )
    }

    item {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EditContainerAddAppDropdown(allApps = allApps, onAppSelected = onAddApp)
            }
            Button(
                onClick = onAddFolder,
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                AureoleText("Pasta")
            }
        }
    }
}

@Composable
private fun EditContainerHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = "Editar Container",
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fechar",
                tint = AureoleTheme.colors.onSurfaceMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContainerAlignmentSelector(
    position: String,
    onPositionSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val positionOptions = listOf("Space Between", "Top", "Center", "Bottom", "Space Evenly")

    AureoleText(
        text = "Alinhamento Vertical",
        style = AureoleTheme.typography.labelMedium,
        color = AureoleTheme.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
    ) {
        OutlinedTextField(
            value = position,
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
            positionOptions.forEach { option ->
                DropdownMenuItem(
                    text = { AureoleText(option, color = AureoleTheme.colors.onSurfaceHigh) },
                    onClick = {
                        onPositionSelected(option)
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
    iconVector: ImageVector,
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
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Mover para cima",
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
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Mover para baixo",
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

        Icon(
            imageVector = iconVector,
            contentDescription = null,
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
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover",
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
            placeholder = { AureoleText("Buscar app para adicionar...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = AureoleTheme.colors.outline,
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
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Excluir Container"
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
