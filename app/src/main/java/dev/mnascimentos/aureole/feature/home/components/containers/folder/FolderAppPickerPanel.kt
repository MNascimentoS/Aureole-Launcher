package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.moveTrayApp
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.searchFolderApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.selectedTrayApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.toggleBestSearchResult
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.togglePackageSelection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.sortedForPickerGrid
import kotlinx.coroutines.delay

private const val PANEL_SEARCH_DEBOUNCE_MS = 300L

/**
 * The folder picker as a panel of a sheet that is not its own overlay (the "New folder" page of the group): the grid
 * of every app with drag-to-mark and the A-Z strip (or the search results) filling the room, then the tray of the
 * chosen apps, in order, and the search field. [selectedPackages] is the choice, in order; it changes as the user
 * taps, sweeps, reorders or removes.
 */
@Composable
internal fun ColumnScope.FolderAppPickerPanel(
    allApps: List<AppInfo>,
    selectedPackages: SnapshotStateList<String>,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    LaunchedEffect(searchQuery) {
        delay(PANEL_SEARCH_DEBOUNCE_MS)
        debouncedQuery = searchQuery
    }
    val appMap = remember(allApps) { allApps.associateBy { it.packageName } }
    val gridApps = remember(allApps) { sortedForPickerGrid(allApps) { it.label } }
    val sortedApps = remember(allApps) { allApps.sortedBy { it.label.lowercase() } }
    val searchResults = remember(debouncedQuery, sortedApps) { searchFolderApps(sortedApps, debouncedQuery) }
    val trayApps = selectedTrayApps(selectedPackages, appMap)
    val params = FolderAppPickerBodyParams(
        folderName = "",
        searchQuery = searchQuery,
        gridApps = gridApps,
        selectedPackages = selectedPackages,
        isSearching = searchQuery.isNotBlank() && debouncedQuery.isNotBlank(),
        searchResults = searchResults,
        trayApps = trayApps,
        isLeftHandedMode = isLeftHandedMode
    )
    val onToggleSelect = { pkg: String -> togglePackageSelection(selectedPackages, pkg) }
    if (params.isSearching) {
        FolderAppPickerSearchList(
            query = searchQuery.trim(),
            results = searchResults,
            selectedPackages = selectedPackages,
            onToggleSelect = onToggleSelect,
            modifier = modifier.weight(1f)
        )
    } else {
        FolderAppPickerBrowseGrid(params = params, onToggleSelect = onToggleSelect, modifier = modifier.weight(1f))
    }
    FolderAppPickerFooter(
        params = FolderAppPickerFooterParams(trayApps = trayApps, searchQuery = searchQuery),
        actions = FolderAppPickerFooterActions(
            onQueryChange = { searchQuery = it },
            onSearch = { toggleBestSearchResult(selectedPackages, searchFolderApps(sortedApps, searchQuery)) },
            onRemove = onToggleSelect,
            onMove = { from, to -> moveTrayApp(selectedPackages, trayApps.map { it.packageName }, from, to) }
        )
    )
}
