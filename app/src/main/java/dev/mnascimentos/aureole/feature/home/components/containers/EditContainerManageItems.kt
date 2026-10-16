package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

@Composable
internal fun ManageItemsListSection(
    state: EditContainerState,
    allApps: List<AppInfo>
) {
    if (state.itemsList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    AureoleTheme.colors.surfaceVariant.copy(alpha = 0.25f),
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum componente adicionado ainda.",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = state.itemsList,
                key = { _, item -> item.id },
                contentType = { _, _ -> "manage_item_row" }
            ) { index, item ->
                ManageItemRow(
                    params = ManageItemRowParams(
                        index = index,
                        item = item,
                        totalItems = state.itemsList.size,
                        folders = state.folders,
                        allApps = allApps
                    ),
                    actions = ManageItemRowActions(
                        onMoveUp = { state.moveItemUp(it) },
                        onMoveDown = { state.moveItemDown(it) },
                        onRemove = { state.removeItem(it) }
                    )
                )
            }
        }
    }
}

private data class ManageItemRowParams(
    val index: Int,
    val item: ContainerItemEntity,
    val totalItems: Int,
    val folders: List<AppFolder>,
    val allApps: List<AppInfo>
)

private data class ManageItemRowActions(
    val onMoveUp: (Int) -> Unit,
    val onMoveDown: (Int) -> Unit,
    val onRemove: (Int) -> Unit
)

@Composable
private fun ManageItemRow(
    params: ManageItemRowParams,
    actions: ManageItemRowActions
) {
    val index = params.index
    val item = params.item
    val title = if (item.itemType == ContainerItemType.FOLDER) {
        params.folders.find { it.id == item.folderId }?.name ?: "Pasta"
    } else {
        params.allApps.find { it.packageName == item.packageName }?.label ?: item.packageName ?: "App"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                style = AureoleTheme.typography.labelMedium,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f)
        )

        ManageItemRowActionButtons(
            index = index,
            totalItems = params.totalItems,
            actions = actions
        )
    }
}

@Composable
private fun ManageItemRowActionButtons(
    index: Int,
    totalItems: Int,
    actions: ManageItemRowActions
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (index > 0) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { actions.onMoveUp(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↑",
                    color = AureoleTheme.colors.onSurfaceHigh,
                    fontSize = 12.sp
                )
            }
        }
        if (index < totalItems - 1) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { actions.onMoveDown(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "↓",
                    color = AureoleTheme.colors.onSurfaceHigh,
                    fontSize = 12.sp
                )
            }
        }
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = AureoleDS.colors.error.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                )
                .clickable { actions.onRemove(index) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = AureoleDS.colors.error,
                fontSize = 12.sp
            )
        }
    }
}
