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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppRowItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.SelectedFolderAppSectionParams

@OptIn(ExperimentalFoundationApi::class)
internal fun LazyListScope.selectedFolderAppSection(
    params: SelectedFolderAppSectionParams
) {
    val filteredSelected = params.filteredSelected
    val searchQuery = params.searchQuery
    val totalSelectedCount = params.totalSelectedCount
    val onMoveUp = params.onMoveUp
    val onMoveDown = params.onMoveDown
    val onToggleSelect = params.onToggleSelect

    item {
        AureoleText(
            text = "Selecionados ($totalSelectedCount)",
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
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
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
            )
        }
    } else {
        itemsIndexed(
            items = filteredSelected,
            key = { _, app -> "sel_${app.packageName}" },
            contentType = { _, _ -> "selected_app_row" }
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
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        AureoleText(
            text = "Outros Aplicativos (${filteredRemaining.size})",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
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
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.small),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, textAlign = TextAlign.Center)
            )
        }
    } else {
        itemsIndexed(
            items = filteredRemaining,
            key = { _, app -> "rem_${app.packageName}" },
            contentType = { _, _ -> "rem_app_row" }
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
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.appRowPaddingVertical),
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
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = params.app.label,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
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
        placeholder = { AureoleText("Buscar aplicativo...", color = AureoleDS.colors.onSurfaceLow) },
        leadingIcon = {
            AureoleDS.icons.Search(
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedBorderColor = AureoleDS.colors.outline,
            focusedTextColor = AureoleDS.colors.onSurfaceHigh,
            unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@AureolePreview
@Composable
private fun FolderAppPickerDialogPreview() {
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
