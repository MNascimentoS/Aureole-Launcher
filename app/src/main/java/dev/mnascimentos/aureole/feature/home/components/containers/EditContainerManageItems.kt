package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.CreateFolderDialog
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowActions
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams

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
            text = stringResource(R.string.home_container_edit_child_components, state.itemsList.size),
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.medium).fillMaxWidth(),
            color = AureoleDS.colors.onSurfaceHigh
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                EditContainerAddAppDropdown(
                    allApps = allApps,
                    onAppSelected = { state.addApp(it) },
                    label = stringResource(R.string.home_container_edit_add_app)
                )
            }
            OutlinedButton(
                onClick = { isCreateFolderDialogVisible = true },
                shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall),
                border = BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AureoleDS.colors.onSurfaceHigh
                ),
                contentPadding = PaddingValues(horizontal = AureoleDS.dimens.small),
                modifier = Modifier.height(AureoleDS.dimens.itemHeightExtraLarge)
            ) {
                AureoleDS.icons.Add(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
                Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
                Text(stringResource(R.string.home_container_edit_folder_label), style = AureoleDS.typography.labelLarge)
            }
        }

        ManageItemsListSection(state = state, allApps = allApps)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.home_container_edit_back))
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
                    RoundedCornerShape(AureoleDS.dimens.radiusMedium)
                )
                .padding(AureoleDS.dimens.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.home_container_edit_none_added),
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
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

@Composable
internal fun ManageItemRow(
    params: ManageItemRowParams,
    actions: ManageItemRowActions
) {
    val index = params.index
    val item = params.item
    val defaultFolderLabel = stringResource(R.string.home_container_edit_folder_label)
    val defaultAppLabel = stringResource(R.string.home_container_edit_app_label)
    val title = if (item.itemType == ContainerItemType.FOLDER) {
        params.folders.find { it.id == item.folderId }?.name ?: defaultFolderLabel
    } else {
        params.allApps.find { it.packageName == item.packageName }?.label ?: item.packageName ?: defaultAppLabel
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
            )
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.small),
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

        ManageItemRowActionButtons(index = index, totalItems = params.totalItems, actions = actions)
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
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        if (index > 0) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.large)
                    .background(
                        color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                    )
                    .clickable { actions.onMoveUp(index) },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
        if (index < totalItems - 1) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.large)
                    .background(
                        color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                    )
                    .clickable { actions.onMoveDown(index) },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.large)
                .background(
                    color = AureoleDS.colors.error,
                    shape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                )
                .clickable { actions.onRemove(index) },
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.onError,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    }
}

@AureolePreview
@Composable
private fun ManageItemsContentPreview() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            val state = rememberEditContainerState(ContainerModel(id = "preview"))
            ManageItemsContent(
                state = state,
                allApps = emptyList(),
                onBack = {}
            )
        }
    }
}
