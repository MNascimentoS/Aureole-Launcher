package dev.mnascimentos.aureole.feature.home.folder

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview

private const val PICKER_HEIGHT_FRACTION = 0.85f
private val APP_ICON_SIZE = 36.dp

@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val selectedPackages = remember {
        mutableStateListOf<String>().apply { addAll(folder.appPackageNames) }
    }
    var searchQuery by remember { mutableStateOf("") }

    val appMap = remember(allApps) {
        allApps.associateBy { it.packageName }
    }

    // Selected apps pinned at top in current order
    val selectedApps = remember(selectedPackages.toList(), appMap) {
        selectedPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    // Remaining non-selected apps sorted alphabetically
    val remainingApps = remember(allApps, selectedPackages.toList()) {
        val selectedSet = selectedPackages.toSet()
        allApps.filter { it.packageName !in selectedSet }
            .sortedBy { it.label.lowercase() }
    }

    val filteredSelected = remember(searchQuery, selectedApps) {
        if (searchQuery.isBlank()) {
            selectedApps
        } else {
            selectedApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    val filteredRemaining = remember(searchQuery, remainingApps) {
        if (searchQuery.isBlank()) {
            remainingApps
        } else {
            remainingApps.filter { it.label.contains(searchQuery, ignoreCase = true) }
        }
    }

    val handleMoveUp = { index: Int ->
        if (index > 0 && index < selectedPackages.size) {
            val item = selectedPackages.removeAt(index)
            selectedPackages.add(index - 1, item)
        }
    }

    val handleMoveDown = { index: Int ->
        if (index >= 0 && index < selectedPackages.size - 1) {
            val item = selectedPackages.removeAt(index)
            selectedPackages.add(index + 1, item)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxHeight(PICKER_HEIGHT_FRACTION)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            FolderAppPickerHeader(
                folderName = folder.name,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(12.dp))

            FolderAppPickerSearchField(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Section 1: Selected apps pinned at top with reordering (Up/Down)
                item(key = "header_selected") {
                    Text(
                        text = "Na Pasta (${selectedApps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                if (filteredSelected.isEmpty()) {
                    item(key = "empty_selected") {
                        Text(
                            text = if (searchQuery.isBlank()) "Nenhum aplicativo adicionado à pasta." else "Nenhum aplicativo encontrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                } else {
                    itemsIndexed(
                        items = filteredSelected,
                        key = { _, app -> "sel_${app.packageName}" }
                    ) { index, app ->
                        SelectedFolderAppRow(
                            app = app,
                            index = index,
                            totalCount = filteredSelected.size,
                            canReorder = searchQuery.isBlank(),
                            onMoveUp = { handleMoveUp(index) },
                            onMoveDown = { handleMoveDown(index) },
                            onToggleSelect = {
                                selectedPackages.remove(app.packageName)
                            }
                        )
                    }
                }

                item(key = "divider_sections") {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                // Section 2: Remaining Installed Apps
                item(key = "header_remaining") {
                    Text(
                        text = "Outros Aplicativos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }

                if (filteredRemaining.isEmpty()) {
                    item(key = "empty_remaining") {
                        Text(
                            text = if (searchQuery.isBlank()) "Todos os aplicativos já foram adicionados." else "Nenhum outro aplicativo encontrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                    }
                } else {
                    itemsIndexed(
                        items = filteredRemaining,
                        key = { _, app -> "rem_${app.packageName}" }
                    ) { _, app ->
                        NonSelectedFolderAppRow(
                            app = app,
                            onToggleSelect = {
                                if (!selectedPackages.contains(app.packageName)) {
                                    selectedPackages.add(app.packageName)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FolderAppPickerFooter(
                onDismiss = onDismiss,
                onSave = { onSave(selectedPackages.toList()) }
            )
        }
    }
}

@Composable
private fun FolderAppPickerHeader(
    folderName: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Adicionar a $folderName",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Fechar"
            )
        }
    }
}

@Composable
private fun FolderAppPickerSearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = { Text("Buscar aplicativos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectedFolderAppRow(
    app: AppInfo,
    index: Int,
    totalCount: Int,
    canReorder: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleSelect: () -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            .clickable(onClick = onToggleSelect)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (canReorder) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = index > 0,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Mover para cima",
                        tint = if (index > 0) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = index < totalCount - 1,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Mover para baixo",
                        tint = if (index < totalCount - 1) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(APP_ICON_SIZE)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = true,
            onCheckedChange = { onToggleSelect() }
        )
    }
}

@Composable
private fun NonSelectedFolderAppRow(
    app: AppInfo,
    onToggleSelect: () -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggleSelect)
            .padding(vertical = 6.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(APP_ICON_SIZE)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Checkbox(
            checked = false,
            onCheckedChange = { onToggleSelect() }
        )
    }
}

@Composable
private fun FolderAppPickerFooter(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onDismiss) {
            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onSave) {
            Text("Salvar", fontWeight = FontWeight.Bold)
        }
    }
}

@AureolePreview
@Composable
fun FolderAppPickerDialogPreview() {
    val mockApp = AppInfo(
        label = "Calendar",
        packageName = "com.example.calendar",
        componentName = ComponentName("com.example.calendar", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockFolder = AppFolder(id = "1", name = "Produtividade")

    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = mockFolder,
            allApps = listOf(mockApp),
            onDismiss = {},
            onSave = {}
        )
    }
}
