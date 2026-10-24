@file:Suppress("MagicNumber")

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
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.filterAppsBySearch
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.togglePackageSelection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPickerMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.SelectedFolderAppSectionParams
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderAppPickerDialog(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onSave: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
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
                onSave = onSave
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
    onSave: (List<String>) -> Unit
) {
    val selectedPackages = remember {
        mutableStateListOf<String>().apply { addAll(folder.appPackageNames) }
    }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(300)
        debouncedQuery = searchQuery
    }

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

    val filteredSelected = remember(debouncedQuery, selectedApps) {
        filterAppsBySearch(selectedApps, debouncedQuery)
    }

    val filteredRemaining = remember(debouncedQuery, remainingApps) {
        filterAppsBySearch(remainingApps, debouncedQuery)
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
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        FolderAppPickerSearchInput(
            query = params.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
        ) {
            selectedFolderAppSection(
                params = SelectedFolderAppSectionParams(
                    filteredSelected = params.filteredSelected,
                    searchQuery = params.searchQuery,
                    totalSelectedCount = selectedPackages.size,
                    onMoveUp = handleMoveUp,
                    onMoveDown = handleMoveDown,
                    onToggleSelect = { pkg -> togglePackageSelection(selectedPackages, pkg) }
                )
            )

            remainingFolderAppSection(
                filteredRemaining = params.filteredRemaining,
                searchQuery = params.searchQuery,
                onToggleSelect = { pkg -> togglePackageSelection(selectedPackages, pkg) }
            )
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        FolderAppPickerFooter(onDismiss = onDismiss, onSave = { onSave(selectedPackages.toList()) })
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
