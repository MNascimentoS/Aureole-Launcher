package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.SidePanelModel
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry

private const val DIALOG_WIDTH_FRACTION = 0.92f

data class EditSidePanelFormState(
    val title: String,
    val position: String,
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean
)

data class EditSidePanelFormCallbacks(
    val onTitleChange: (String) -> Unit,
    val onPositionChange: (String) -> Unit,
    val onBgChange: (Boolean) -> Unit,
    val onExpandChange: (Boolean) -> Unit,
    val onAddFolderBtnChange: (Boolean) -> Unit,
    val onFolderLabelsChange: (Boolean) -> Unit,
    val onGridFolderChange: (Boolean) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSidePanelDialog(
    panel: SidePanelModel,
    onDismiss: () -> Unit,
    onSave: (SidePanelModel) -> Unit,
    onDeletePanel: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(DIALOG_WIDTH_FRACTION)
                .heightIn(max = 600.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            EditSidePanelDialogContent(
                panel = panel,
                onDismiss = onDismiss,
                onSave = onSave,
                onDeletePanel = onDeletePanel
            )
        }
    }
}

@Composable
private fun EditSidePanelDialogContent(
    panel: SidePanelModel,
    onDismiss: () -> Unit,
    onSave: (SidePanelModel) -> Unit,
    onDeletePanel: (String) -> Unit
) {
    var title by remember { mutableStateOf(panel.title) }
    var position by remember { mutableStateOf(panel.position) }
    var isBackgroundEnabled by remember { mutableStateOf(panel.isBackgroundEnabled) }
    var isExpandCell by remember { mutableStateOf(panel.isExpandCell) }
    var showAddFolderButton by remember { mutableStateOf(panel.showAddFolderButton) }
    var showFolderLabels by remember { mutableStateOf(panel.showFolderLabels) }
    var isGridFolderEnabled by remember { mutableStateOf(panel.isGridFolderEnabled) }

    Column(modifier = Modifier.fillMaxWidth()) {
        EditSidePanelHeader(onDismiss = onDismiss)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            editSidePanelFormItems(
                state = EditSidePanelFormState(
                    title = title,
                    position = position,
                    isBackgroundEnabled = isBackgroundEnabled,
                    isExpandCell = isExpandCell,
                    showAddFolderButton = showAddFolderButton,
                    showFolderLabels = showFolderLabels,
                    isGridFolderEnabled = isGridFolderEnabled
                ),
                callbacks = EditSidePanelFormCallbacks(
                    onTitleChange = { title = it },
                    onPositionChange = { position = it },
                    onBgChange = { isBackgroundEnabled = it },
                    onExpandChange = { isExpandCell = it },
                    onAddFolderBtnChange = { showAddFolderButton = it },
                    onFolderLabelsChange = { showFolderLabels = it },
                    onGridFolderChange = { isGridFolderEnabled = it }
                ),
                folders = panel.folders
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        EditSidePanelFooter(
            onDelete = { onDeletePanel(panel.id) },
            onSave = {
                val updated = panel.copy(
                    title = title,
                    position = position,
                    isBackgroundEnabled = isBackgroundEnabled,
                    isExpandCell = isExpandCell,
                    showAddFolderButton = showAddFolderButton,
                    showFolderLabels = showFolderLabels,
                    isGridFolderEnabled = isGridFolderEnabled
                )
                onSave(updated)
            }
        )
    }
}

private fun LazyListScope.editSidePanelFormItems(
    state: EditSidePanelFormState,
    callbacks: EditSidePanelFormCallbacks,
    folders: List<AppFolder>
) {
    item {
        OutlinedTextField(
            value = state.title,
            onValueChange = callbacks.onTitleChange,
            label = { Text("Nome do Painel") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
    item {
        EditSidePanelAlignmentSelector(position = state.position, onPositionSelected = callbacks.onPositionChange)
    }
    item {
        SettingSwitchRow(
            label = "Fundo do Painel",
            checked = state.isBackgroundEnabled,
            onCheckedChange = callbacks.onBgChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Expandir Célula",
            checked = state.isExpandCell,
            onCheckedChange = callbacks.onExpandChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Botão de Criar Pasta",
            checked = state.showAddFolderButton,
            onCheckedChange = callbacks.onAddFolderBtnChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Exibir Rótulos das Pastas",
            checked = state.showFolderLabels,
            onCheckedChange = callbacks.onFolderLabelsChange
        )
    }
    item {
        SettingSwitchRow(
            label = "Modo de Pasta Expansiva (Grid)",
            checked = state.isGridFolderEnabled,
            onCheckedChange = callbacks.onGridFolderChange
        )
    }
    if (folders.isNotEmpty()) {
        editSidePanelFoldersSection(folders)
    }
}

private fun LazyListScope.editSidePanelFoldersSection(folders: List<AppFolder>) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Conteúdo do Painel",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
    item {
        Text(
            text = "Pastas (${folders.size})",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    items(folders, key = { it.id }) { folder ->
        EditSidePanelFolderRow(folder = folder)
    }
}

@Composable
private fun EditSidePanelHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Editar Painel Lateral",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fechar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSidePanelAlignmentSelector(
    position: String,
    onPositionSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val positionOptions = listOf("Space Between", "Top", "Center", "Bottom", "Space Evenly")

    Text(
        text = "Alinhamento Vertical",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            positionOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
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
private fun EditSidePanelFolderRow(folder: AppFolder) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = FolderIconRegistry.getIcon(folder.icon) ?: Icons.Default.Menu,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = folder.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun EditSidePanelFooter(
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
                contentDescription = "Excluir Painel"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Excluir")
        }

        Button(onClick = onSave) {
            Text("Salvar")
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
