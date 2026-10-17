package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.folder.CreateFolderDialog

@Suppress("LongMethod") // Layout managing container reordering and item deletion controls
@Composable
internal fun ManageItemsContent(
    state: EditContainerState,
    allApps: List<AppInfo>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreateFolderDialogVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (isCreateFolderDialogVisible) {
            CreateFolderDialog(
                onDismiss = { isCreateFolderDialogVisible = false },
                onSubmit = { name ->
                    state.addFolder(name)
                    isCreateFolderDialogVisible = false
                }
            )
        }

        Text(
            text = "Componentes Filhos (${state.itemsList.size})",
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth(),
            color = AureoleDS.colors.onSurfaceHigh
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EditContainerAddAppDropdown(
                    allApps = allApps,
                    onAppSelected = { state.addApp(it) },
                    label = "App..."
                )
            }
            OutlinedButton(
                onClick = { isCreateFolderDialogVisible = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AureoleDS.colors.outline.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AureoleDS.colors.onSurfaceHigh
                ),
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.height(52.dp)
            ) {
                AureoleDS.icons.Add(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Pasta", style = AureoleDS.typography.labelLarge)
            }
        }

        ManageItemsListSection(state = state, allApps = allApps)

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}

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
                    AureoleDS.colors.surfaceVariant.copy(alpha = 0.25f),
                    RoundedCornerShape(14.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum componente adicionado ainda.",
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceMedium
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
                color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AureoleDS.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (index > 0) {
                Button(
                    onClick = { actions.onMoveUp(index) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("▲", style = AureoleDS.typography.labelSmall)
                }
            }
            if (index < params.totalItems - 1) {
                Button(
                    onClick = { actions.onMoveDown(index) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("▼", style = AureoleDS.typography.labelSmall)
                }
            }
            Button(
                onClick = { actions.onRemove(index) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AureoleDS.colors.error.copy(alpha = 0.2f),
                    contentColor = AureoleDS.colors.error
                ),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("✕", style = AureoleDS.typography.labelSmall)
            }
        }
    }
}
