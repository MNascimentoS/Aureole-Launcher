package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.moveTrayApp
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.searchFolderApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.selectedTrayApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.toggleBestSearchResult
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.togglePackageSelection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerCallbacks
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.sortedForPickerGrid
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 300L

/**
 * Picker of the apps of a folder on the shared confirm overlay (sheet at rest height, centered title with the logo,
 * Cancel / "Save N apps" in equal widths on the thumb side). The grid with drag-to-mark and the A-Z strip scroll in
 * the sheet; the tray of chosen apps and the search field stay glued to the action row (and the keyboard).
 */
@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    FolderAppPickerDialogContent(
        folder = folder,
        allApps = allApps,
        callbacks = FolderAppPickerCallbacks(
            onDismiss = onDismiss,
            onSave = onSave,
            onSettingsClick = LocalHomeActions.current.onSettingsClick
        ),
        isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode,
        modifier = modifier
    )
}

@Composable
private fun FolderAppPickerDialogContent(
    folder: AppFolder,
    allApps: List<AppInfo>,
    callbacks: FolderAppPickerCallbacks,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier
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

    // Every app stays in the grid, marked or not, so nothing jumps away when it is tapped.
    val gridApps = remember(allApps) { sortedForPickerGrid(allApps) { it.label } }

    val sortedApps = remember(allApps) { allApps.sortedBy { it.label.lowercase() } }
    val isSearching = searchQuery.isNotBlank() && debouncedQuery.isNotBlank()
    val searchResults = remember(debouncedQuery, sortedApps) { searchFolderApps(sortedApps, debouncedQuery) }

    FolderAppPickerDialogBody(
        params = FolderAppPickerBodyParams(
            folderName = folder.name,
            searchQuery = searchQuery,
            gridApps = gridApps,
            selectedPackages = selectedPackages,
            isSearching = isSearching,
            searchResults = searchResults,
            trayApps = selectedApps,
            isLeftHandedMode = isLeftHandedMode
        ),
        onSearchQueryChange = { searchQuery = it },
        // Enter uses the best of what is typed right now, not of the debounced query.
        onSearch = { toggleBestSearchResult(selectedPackages, searchFolderApps(sortedApps, searchQuery)) },
        callbacks = callbacks,
        modifier = modifier
    )
}

@Composable
internal fun FolderAppPickerDialogBody(
    params: FolderAppPickerBodyParams,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    callbacks: FolderAppPickerCallbacks,
    modifier: Modifier = Modifier
) {
    val selectedPackages = params.selectedPackages
    val onToggleSelect = { pkg: String -> togglePackageSelection(selectedPackages, pkg) }

    AureoleConfirmOverlay(
        params = ConfirmOverlayParams(
            title = stringResource(R.string.home_folder_apps_in_folder, params.folderName),
            primaryLabel = pluralStringResource(
                R.plurals.home_folder_picker_save_count,
                selectedPackages.size,
                selectedPackages.size
            ),
            onPrimary = { callbacks.onSave(selectedPackages.toList()) },
            onDismissRequest = callbacks.onDismiss,
            isLeftHanded = params.isLeftHandedMode,
            onSettingsClick = callbacks.onSettingsClick
        ),
        modifier = modifier,
        bottomExtra = {
            FolderAppPickerFooter(
                params = FolderAppPickerFooterParams(trayApps = params.trayApps, searchQuery = params.searchQuery),
                actions = FolderAppPickerFooterActions(
                    onQueryChange = onSearchQueryChange,
                    onSearch = onSearch,
                    onRemove = onToggleSelect,
                    onMove = { from, to ->
                        moveTrayApp(selectedPackages, params.trayApps.map { it.packageName }, from, to)
                    }
                )
            )
        }
    ) {
        if (params.isSearching) {
            FolderAppPickerSearchList(
                query = params.searchQuery.trim(),
                results = params.searchResults,
                selectedPackages = selectedPackages,
                onToggleSelect = onToggleSelect,
                modifier = Modifier.weight(1f)
            )
        } else {
            FolderAppPickerBrowseGrid(params = params, onToggleSelect = onToggleSelect, modifier = Modifier.weight(1f))
        }
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
