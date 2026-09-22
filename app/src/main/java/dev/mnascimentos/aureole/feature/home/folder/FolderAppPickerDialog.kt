package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo

private const val DIALOG_WIDTH_FRACTION = 0.92f

data class FolderAppRowItemParams(
    val app: AppInfo,
    val index: Int,
    val totalCount: Int,
    val canReorder: Boolean
)

data class FolderSectionParams(
    val filteredSelected: List<AppInfo>,
    val searchQuery: String,
    val totalSelectedCount: Int
)

data class FolderAppPickerBodyParams(
    val folderName: String,
    val searchQuery: String,
    val filteredSelected: List<AppInfo>,
    val filteredRemaining: List<AppInfo>,
    val selectedPackages: MutableList<String>
)

@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
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
                .heightIn(max = 620.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            FolderAppPickerDialogContent(
                folder = folder,
                allApps = allApps,
                onDismiss = onDismiss,
                onSave = onSave
            )
        }
    }
}

@Composable
private fun FolderAppPickerDialogContent(
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

    val selectedApps = remember(selectedPackages.toList(), appMap) {
        selectedPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    val remainingApps = remember(allApps, selectedPackages.toList()) {
        val selectedSet = selectedPackages.toSet()
        allApps.filter { it.packageName !in selectedSet }
            .sortedBy { it.label.lowercase() }
    }

    val filteredSelected = remember(searchQuery, selectedApps) {
        filterAppsBySearch(selectedApps, searchQuery)
    }

    val filteredRemaining = remember(searchQuery, remainingApps) {
        filterAppsBySearch(remainingApps, searchQuery)
    }

    FolderAppPickerDialogBody(
        params = FolderAppPickerBodyParams(
            folderName = folder.name,
            searchQuery = searchQuery,
            filteredSelected = filteredSelected,
            filteredRemaining = filteredRemaining,
            selectedPackages = selectedPackages
        ),
        onSearchQueryChange = { searchQuery = it },
        onDismiss = onDismiss,
        onSave = onSave
    )
}

private fun filterAppsBySearch(apps: List<AppInfo>, query: String): List<AppInfo> {
    return if (query.isBlank()) {
        apps
    } else {
        apps.filter { it.label.contains(query, ignoreCase = true) }
    }
}

@Composable
private fun FolderAppPickerDialogBody(
    params: FolderAppPickerBodyParams,
    onSearchQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val selectedPackages = params.selectedPackages
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

    Column(modifier = Modifier.fillMaxWidth()) {
        FolderAppPickerHeader(folderName = params.folderName, onDismiss = onDismiss)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = params.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Buscar aplicativo...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            selectedFolderAppSection(
                params = FolderSectionParams(
                    filteredSelected = params.filteredSelected,
                    searchQuery = params.searchQuery,
                    totalSelectedCount = selectedPackages.size
                ),
                onMoveUp = handleMoveUp,
                onMoveDown = handleMoveDown,
                onToggleSelect = { pkg -> togglePackageSelection(selectedPackages, pkg) }
            )

            remainingFolderAppSection(
                filteredRemaining = params.filteredRemaining,
                searchQuery = params.searchQuery,
                onToggleSelect = { pkg -> togglePackageSelection(selectedPackages, pkg) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        FolderAppPickerFooter(onDismiss = onDismiss, onSave = { onSave(selectedPackages.toList()) })
    }
}

private fun togglePackageSelection(selectedPackages: MutableList<String>, pkg: String) {
    if (selectedPackages.contains(pkg)) {
        selectedPackages.remove(pkg)
    } else {
        selectedPackages.add(pkg)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.selectedFolderAppSection(
    params: FolderSectionParams,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onToggleSelect: (String) -> Unit
) {
    val filteredSelected = params.filteredSelected
    val searchQuery = params.searchQuery
    val totalSelectedCount = params.totalSelectedCount

    item {
        Text(
            text = "Apps Selecionados (${filteredSelected.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    if (filteredSelected.isEmpty()) {
        item {
            val emptyText = if (searchQuery.isBlank()) {
                "Nenhum aplicativo adicionado à pasta."
            } else {
                "Nenhum aplicativo encontrado."
            }
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        itemsIndexed(
            items = filteredSelected,
            key = { _, app -> "sel_${app.packageName}" }
        ) { index, app ->
            SelectedFolderAppRow(
                params = FolderAppRowItemParams(
                    app = app,
                    index = index,
                    totalCount = totalSelectedCount,
                    canReorder = searchQuery.isBlank()
                ),
                onMoveUp = { onMoveUp(index) },
                onMoveDown = { onMoveDown(index) },
                onToggleSelect = { onToggleSelect(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.remainingFolderAppSection(
    filteredRemaining: List<AppInfo>,
    searchQuery: String,
    onToggleSelect: (String) -> Unit
) {
    item {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Outros Aplicativos (${filteredRemaining.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    if (filteredRemaining.isEmpty()) {
        item {
            val emptyText = if (searchQuery.isBlank()) {
                "Todos os aplicativos já foram adicionados."
            } else {
                "Nenhum outro aplicativo encontrado."
            }
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )
        }
    } else {
        itemsIndexed(
            items = filteredRemaining,
            key = { _, app -> "rem_${app.packageName}" }
        ) { _, app ->
            RemainingFolderAppRow(
                app = app,
                onToggleSelect = { onToggleSelect(app.packageName) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun SelectedFolderAppRow(
    params: FolderAppRowItemParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = params.app.label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        SelectedFolderAppRowActionButtons(
            params = params,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onToggleSelect = onToggleSelect
        )
    }
}
