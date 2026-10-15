package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppRowItemParams

@Suppress("LongParameterList") // Folder app selection section requires state filters and list action callbacks
@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.selectedFolderAppSection(
    filteredSelected: List<AppInfo>,
    searchQuery: String,
    totalSelectedCount: Int,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onToggleSelect: (String) -> Unit
) {
    item {
        AureoleText(
            text = "Selecionados ($totalSelectedCount)",
            style = AureoleTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    if (filteredSelected.isEmpty()) {
        item {
            val emptyText = if (searchQuery.isBlank()) {
                "Nenhum aplicativo na pasta."
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
internal fun LazyListScope.remainingFolderAppSection(
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
internal fun SelectedFolderAppRow(
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
internal fun FolderAppPickerSearchInput(
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
