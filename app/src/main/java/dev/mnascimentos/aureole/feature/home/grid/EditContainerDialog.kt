package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun EditGridItemDialog(
    item: LauncherItemState,
    onDismissRequest: () -> Unit,
    onDeleteConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val title = getContainerTitle(item.type)

    AureoleDialog(
        onDismissRequest = onDismissRequest,
        title = "Configurar $title",
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            AureoleText(
                text = "Tamanho atual: ${item.colSpan} x ${item.rowSpan} células",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(bottom = AureoleDS.dimens.small)
            )

            EditDialogTypeActionSection(
                item = item,
                actions = actions,
                onDismissRequest = onDismissRequest
            )

            EditDialogWidgetListSection(item = item, uiState = uiState, actions = actions)

            if (item.type == LauncherItemType.SCROLL_VIEW) {
                EditDialogScrollViewSection(item = item, actions = actions, onDismissRequest = onDismissRequest)
            }

            EditDialogDeleteButtonRow(
                onDelete = {
                    onDeleteConfirm(item.id)
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun EditDialogTypeActionSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    if (item.type == LauncherItemType.CLOCK) {
        Button(
            onClick = {
                actions.onOpenEditClockBottomSheet()
                onDismissRequest()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleTheme.colors.surfaceVariant,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small)
        ) {
            AureoleDS.icons.Edit(
                modifier = Modifier.padding(end = AureoleDS.dimens.xSmall)
            )
            AureoleText(
                text = "Personalizar Relógio",
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }

    if (item.type == LauncherItemType.APPS_LIST) {
        Button(
            onClick = {
                actions.onOpenFavoritePicker(item.id)
                onDismissRequest()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleTheme.colors.surfaceVariant,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small)
        ) {
            AureoleDS.icons.Edit(
                modifier = Modifier.padding(end = AureoleDS.dimens.xSmall)
            )
            AureoleText(
                text = "Editar Favoritos e Configurações",
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }

    if (item.type == LauncherItemType.SHORTCUTS_CONTAINER) {
        Button(
            onClick = {
                actions.onOpenEditContainerDialog(item.id)
                onDismissRequest()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleTheme.colors.surfaceVariant,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.small)
        ) {
            AureoleDS.icons.Edit(
                modifier = Modifier.padding(end = AureoleDS.dimens.xSmall)
            )
            AureoleText(
                text = "Editar Configurações do Painel Lateral",
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }
}

internal fun getContainerTitle(type: LauncherItemType?): String {
    return when (type) {
        LauncherItemType.CLOCK -> "Relógio"
        LauncherItemType.APPS_LIST -> "Lista de Aplicativos"
        LauncherItemType.SHORTCUTS_CONTAINER -> "Barra de Atalhos"
        LauncherItemType.SINGLE_APP_WIDGET -> "Widget Individual"
        LauncherItemType.WIDGET_LIST -> "Lista de Widgets"
        LauncherItemType.SCROLL_VIEW -> "Scroll View"
        null -> "Container"
    }
}

@Composable
private fun EditDialogWidgetListSection(
    item: LauncherItemState,
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    if (item.type == LauncherItemType.WIDGET_LIST && uiState.topWidgetIds.isNotEmpty()) {
        AureoleText(
            text = "Widgets na lista (${uiState.topWidgetIds.size}):",
            style = AureoleTheme.typography.labelMedium,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall)
        )
        uiState.topWidgetIds.forEach { widgetId ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AureoleDS.dimens.xxSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AureoleText(
                    text = "Widget ID: $widgetId",
                    style = AureoleTheme.typography.bodySmall,
                    color = AureoleTheme.colors.onSurfaceHigh
                )
                TextButton(onClick = { actions.onRemoveWidgetClick(widgetId) }) {
                    AureoleText(text = "Remover Widget", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
    }
}

@Composable
private fun EditDialogScrollViewSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    ScrollViewOrientationSelector(item = item, actions = actions)

    AureoleText(
        text = "Componentes Filhos (${item.safeChildren.size}):",
        style = AureoleTheme.typography.labelLarge,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall)
    )

    if (item.safeChildren.isEmpty()) {
        AureoleText(
            text = "Nenhum componente adicionado ainda.",
            style = AureoleTheme.typography.bodySmall,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
        )
    } else {
        val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
        item.safeChildren.forEach { child ->
            ScrollViewChildItemRow(
                parentId = item.id,
                child = child,
                isVertical = isVertical,
                actions = actions,
                onDismissRequest = onDismissRequest
            )
        }
    }

    Button(
        onClick = { actions.onOpenAddContainerForParent(item.id) },
        colors = ButtonDefaults.buttonColors(
            containerColor = AureoleTheme.colors.surfaceVariant,
            contentColor = AureoleTheme.colors.onSurfaceHigh
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xSmall)
    ) {
        AureoleDS.icons.Add(
            modifier = Modifier.padding(end = AureoleDS.dimens.xSmall)
        )
        AureoleText(text = "Adicionar Componente", color = AureoleTheme.colors.onSurfaceHigh)
    }

    Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
}

@Composable
private fun ScrollViewOrientationSelector(
    item: LauncherItemState,
    actions: HomeScreenActions
) {
    AureoleText(
        text = "Orientação do Scroll:",
        style = AureoleTheme.typography.labelLarge,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.VERTICAL) }
                .padding(end = AureoleDS.dimens.medium)
        ) {
            RadioButton(
                selected = item.safeScrollOrientation == ScrollOrientation.VERTICAL,
                onClick = { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.VERTICAL) }
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            AureoleText(
                text = "Vertical",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.HORIZONTAL)
            }
        ) {
            RadioButton(
                selected = item.safeScrollOrientation == ScrollOrientation.HORIZONTAL,
                onClick = { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.HORIZONTAL) }
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            AureoleText(
                text = "Horizontal",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }
}

@Composable
private fun EditDialogDeleteButtonRow(onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDelete)
            .padding(vertical = AureoleDS.dimens.small, horizontal = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Delete(
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = "Remover Container",
            style = AureoleTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@AureolePreview
@Composable
fun EditGridItemDialogPreview() {
    val sampleItem = LauncherItemState(
        id = "item_1",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides MainUiState(),
            LocalHomeActions provides HomeScreenActions(
                onWidgetRowHeightChanged = {},
                onAddWidgetClick = {},
                onRemoveWidgetClick = {},
                onAppClick = {},
                onExpandNotificationShade = {},
                onFolderIntent = {},
                onSetAddAppToFolderDialogVisible = {},
                onSetRenameFolderDialogVisible = {},
                onSearchQueryChanged = {},
                onSettingsClick = {},
                onAllAppsDrawerClose = {},
                onAllAppsDrawerOpen = {},
                onEnterGridEditMode = {},
                onCancelGridEditMode = {},
                onSaveGridEditMode = {}
            )
        ) {
            EditGridItemDialog(
                item = sampleItem,
                onDismissRequest = {},
                onDeleteConfirm = {}
            )
        }
    }
}
