package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.overlay.AureoleOverlayWindow
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.composable.reorder.ReorderableColumn
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.reorder.rememberReorderAutoScroll
import dev.mnascimentos.aureole.composable.reorder.reorderAutoScrollViewport
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams

/** Tallest the list of items gets before it scrolls: the sheet itself does not scroll. */
private val MANAGE_LIST_MAX_HEIGHT = 300.dp

@Composable
internal fun ManageItemsContent(
    state: EditContainerState,
    allApps: List<AppInfo>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreateFolderDialogVisible by remember { mutableStateOf(false) }

    if (isCreateFolderDialogVisible) {
        AureoleOverlayWindow(onDismissRequest = { isCreateFolderDialogVisible = false }) {
            CreateFolderDialog(
                onDismiss = { isCreateFolderDialogVisible = false },
                onSubmit = { name ->
                    state.addFolder(name)
                    isCreateFolderDialogVisible = false
                }
            )
        }
    }

    Column(modifier = modifier) {
        ManageItemsSectionLabel(text = stringResource(R.string.home_container_edit_section_add))
        ManageItemsAddSection(
            state = state,
            allApps = allApps,
            onOpenCreateFolderDialog = { isCreateFolderDialogVisible = true }
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))

        ManageItemsSectionLabel(
            text = stringResource(R.string.home_container_edit_child_components),
            count = state.itemsList.size
        )

        ManageItemsListSection(state = state, allApps = allApps)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
        EditContainerBackButton(onClick = onBack)
    }
}

/** Small caps section label; [count], when given, sits at the end of the row as a separate text. */
@Composable
private fun ManageItemsSectionLabel(text: String, modifier: Modifier = Modifier, count: Int? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = AureoleDS.dimens.xSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = AureoleDS.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceMedium
        )
        if (count != null) {
            Text(
                text = count.toString(),
                style = AureoleDS.typography.labelMedium,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
internal fun ManageItemsAddSection(
    state: EditContainerState,
    allApps: List<AppInfo>,
    onOpenCreateFolderDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = AureoleDS.colors.surfaceVariant.copy(alpha = 0.25f),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
            )
            .border(
                width = AureoleDS.dimens.borderMinimal,
                color = AureoleDS.colors.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
            )
            .padding(AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        EditContainerAddAppDropdown(
            allApps = allApps,
            onAppSelected = { state.addApp(it) },
            label = stringResource(R.string.home_container_edit_add_app),
            modifier = Modifier.fillMaxWidth()
        )

        ManageItemsAddButtons(
            onOpenCreateFolderDialog = onOpenCreateFolderDialog,
            onAddQuickButton = { state.addQuickButton() }
        )
    }
}

@Composable
private fun ManageItemsAddButtons(
    onOpenCreateFolderDialog: () -> Unit,
    onAddQuickButton: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        OutlinedButton(
            onClick = onOpenCreateFolderDialog,
            shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall),
            border = BorderStroke(
                AureoleDS.dimens.borderMinimal,
                AureoleDS.colors.outline.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AureoleDS.colors.onSurfaceHigh
            ),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.small),
            modifier = Modifier
                .weight(1f)
                .height(AureoleDS.dimens.itemHeightExtraLarge)
        ) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            Text(
                text = stringResource(R.string.home_container_edit_folder_label),
                style = AureoleDS.typography.labelLarge
            )
        }

        OutlinedButton(
            onClick = onAddQuickButton,
            shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall),
            border = BorderStroke(
                AureoleDS.dimens.borderMinimal,
                AureoleDS.colors.outline.copy(alpha = 0.5f)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AureoleDS.colors.onSurfaceHigh
            ),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.small),
            modifier = Modifier
                .weight(1f)
                .height(AureoleDS.dimens.itemHeightExtraLarge)
        ) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            Text(
                text = stringResource(R.string.home_grid_item_quick_button),
                style = AureoleDS.typography.labelLarge
            )
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
                .aureoleDashedBorder(color = AureoleDS.colors.outline.copy(alpha = 0.5f))
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
        LeftHandMirror { ReorderableManageItems(state = state, allApps = allApps) }
    }
}

/**
 * The items in their order: hold one and drag it up or down to reorder, ✕ removes it. The sheet does not scroll,
 * so the list keeps its own capped scroll; a held item near its top or bottom edge scrolls it.
 */
@Composable
private fun ReorderableManageItems(state: EditContainerState, allApps: List<AppInfo>) {
    val scrollState = rememberScrollState()
    val autoScroll = rememberReorderAutoScroll(scrollState)
    Column(
        modifier = Modifier
            .heightIn(max = MANAGE_LIST_MAX_HEIGHT)
            .fillMaxWidth()
            .reorderAutoScrollViewport(autoScroll)
            .verticalScroll(scrollState)
    ) {
        ReorderableColumn(
            params = ReorderableParams(
                items = state.itemsList,
                key = { it.id },
                actions = ReorderableActions(onMove = state::moveItem, onRemove = state::removeItem),
                spacing = AureoleDS.dimens.xSmall
            ),
            autoScroll = autoScroll
        ) { item ->
            ManageItemRow(params = ManageItemRowParams(item = item, folders = state.folders, allApps = allApps))
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
