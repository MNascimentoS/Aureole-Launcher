package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.moveDown
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.moveUp
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.searchFolderApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.selectedTrayApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.toggleBestSearchResult
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.togglePackageSelection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPickerMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.SelectedFolderAppSectionParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 300L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode
    val sheetContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = FolderPickerMetrics.DialogContentPadding)
                .padding(bottom = FolderPickerMetrics.DialogContentPadding)
        ) {
            FolderAppPickerDialogContent(
                folder = folder,
                allApps = allApps,
                onDismiss = onDismiss,
                onSave = onSave,
                isLeftHandedMode = isLeftHandedMode
            )
        }
    }

    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleDS.dimens.radiusLarge,
                    topEnd = AureoleDS.dimens.radiusLarge
                ),
                color = AureoleDS.colors.surface,
                contentColor = AureoleDS.colors.onSurfaceMedium,
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
                topStart = AureoleDS.dimens.radiusLarge,
                topEnd = AureoleDS.dimens.radiusLarge
            ),
            containerColor = AureoleDS.colors.surface,
            contentColor = AureoleDS.colors.onSurfaceMedium,
            modifier = modifier.windowInsetsPadding(WindowInsets.statusBars)
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
    onSave: (List<String>) -> Unit,
    isLeftHandedMode: Boolean
) {
    val selectedPackages = remember {
        mutableStateListOf<String>().apply { addAll(folder.appPackageNames) }
    }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(SEARCH_DEBOUNCE_MS)
        debouncedQuery = searchQuery
    }

    val appMap = remember(allApps) {
        allApps.associateBy { it.packageName }
    }

    val selectedApps = remember(selectedPackages.toList(), appMap) {
        selectedTrayApps(selectedPackages, appMap)
    }

    val remainingApps = remember(allApps, selectedPackages.toList()) {
        val selectedSet = selectedPackages.toSet()
        allApps.filter { it.packageName !in selectedSet }
            .sortedBy { it.label.lowercase() }
    }

    val sortedApps = remember(allApps) { allApps.sortedBy { it.label.lowercase() } }
    val isSearching = searchQuery.isNotBlank() && debouncedQuery.isNotBlank()
    val searchResults = remember(debouncedQuery, sortedApps) { searchFolderApps(sortedApps, debouncedQuery) }

    FolderAppPickerDialogBody(
        params = FolderAppPickerBodyParams(
            folderName = folder.name,
            searchQuery = searchQuery,
            filteredSelected = selectedApps,
            filteredRemaining = remainingApps,
            selectedPackages = selectedPackages,
            isSearching = isSearching,
            searchResults = searchResults,
            trayApps = selectedApps,
            isLeftHandedMode = isLeftHandedMode
        ),
        onSearchQueryChange = { searchQuery = it },
        // Enter uses the best of what is typed right now, not of the debounced query.
        onSearch = { toggleBestSearchResult(selectedPackages, searchFolderApps(sortedApps, searchQuery)) },
        onDismiss = onDismiss,
        onSave = onSave
    )
}

@Composable
internal fun FolderAppPickerDialogBody(
    params: FolderAppPickerBodyParams,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit
) {
    val selectedPackages = params.selectedPackages
    val onToggleSelect = { pkg: String -> togglePackageSelection(selectedPackages, pkg) }

    Column(modifier = Modifier.fillMaxWidth()) {
        FolderAppPickerHeader(folderName = params.folderName, onDismiss = onDismiss)
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        if (params.isSearching) {
            FolderAppPickerSearchList(
                query = params.searchQuery.trim(),
                results = params.searchResults,
                selectedPackages = selectedPackages,
                onToggleSelect = onToggleSelect,
                modifier = Modifier.weight(1f)
            )
        } else {
            FolderAppPickerBrowseList(params = params, onToggleSelect = onToggleSelect, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        FolderAppPickerFooter(
            params = FolderAppPickerFooterParams(
                trayApps = params.trayApps,
                searchQuery = params.searchQuery,
                saveCount = selectedPackages.size,
                isLeftHandedMode = params.isLeftHandedMode
            ),
            actions = FolderAppPickerFooterActions(
                onQueryChange = onSearchQueryChange,
                onSearch = onSearch,
                onRemove = onToggleSelect,
                onDismiss = onDismiss,
                onSave = { onSave(selectedPackages.toList()) }
            )
        )
    }
}

/** Selected apps (with reorder arrows) followed by the remaining apps. */
@Composable
private fun FolderAppPickerBrowseList(
    params: FolderAppPickerBodyParams,
    onToggleSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPackages = params.selectedPackages
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        selectedFolderAppSection(
            params = SelectedFolderAppSectionParams(
                filteredSelected = params.filteredSelected,
                searchQuery = params.searchQuery,
                totalSelectedCount = selectedPackages.size,
                onMoveUp = { index -> selectedPackages.moveUp(index) },
                onMoveDown = { index -> selectedPackages.moveDown(index) },
                onToggleSelect = onToggleSelect
            )
        )

        remainingFolderAppSection(
            filteredRemaining = params.filteredRemaining,
            searchQuery = params.searchQuery,
            onToggleSelect = onToggleSelect
        )
    }
}

@AureolePreview
@Composable
private fun FolderAppPickerDialogPreview() {
    val sampleFolder = AppFolder(id = "f1", name = "Tools", appPackageNames = listOf("com.example.camera"))
    val sampleApps = listOf(
        AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )
    )
    AureoleLauncherTheme {
        FolderAppPickerDialog(
            folder = sampleFolder,
            allApps = sampleApps,
            onDismiss = {},
            onSave = {}
        )
    }
}
