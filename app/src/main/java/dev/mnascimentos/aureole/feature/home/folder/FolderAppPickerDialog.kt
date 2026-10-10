package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.folder.model.FolderAppRowItemParams
import dev.mnascimentos.aureole.feature.home.folder.model.FolderSectionParams

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val sheetContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            FolderAppPickerDialogContent(
                folder = folder,
                allApps = allApps,
                onDismiss = onDismiss,
                onSave = onSave
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
            dragHandle = null,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
        ) {
            sheetContent()
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

        FolderAppPickerSearchInput(
            query = params.searchQuery,
            onQueryChange = onSearchQueryChange
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
        AureoleText(
            text = "Apps Selecionados (${filteredSelected.size})",
            style = AureoleTheme.typography.titleSmall,
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
            AureoleText(
                text = emptyText,
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
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
        AureoleText(
            text = "Outros Aplicativos (${filteredRemaining.size})",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceMedium,
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
            AureoleText(
                text = emptyText,
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
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
    val iconBitmap = remember(params.app.packageName) { params.app.getIconBitmap() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = params.app.label,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            AureoleText(
                text = params.app.label,
                style = AureoleTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }

        SelectedFolderAppRowActionButtons(
            params = params,
            onMoveUp = onMoveUp,
            onMoveDown = onMoveDown,
            onToggleSelect = onToggleSelect
        )
    }
}

@Composable
private fun FolderAppPickerSearchInput(
    query: String,
    onQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { AureoleText("Buscar aplicativo...", color = AureoleTheme.colors.onSurfaceLow) },
        leadingIcon = {
            AureoleDS.icons.Search(
                tint = AureoleTheme.colors.onSurfaceMedium
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = AureoleTheme.colors.outline,
            focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
fun FolderAppPickerDialogPreview() {
    val mockFolder = AppFolder(id = "1", name = "Social")
    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = mockFolder,
            allApps = emptyList(),
            onDismiss = {},
            onSave = {}
        )
    }
}
