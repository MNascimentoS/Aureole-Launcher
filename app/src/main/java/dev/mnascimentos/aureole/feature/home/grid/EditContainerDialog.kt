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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
fun EditContainerDialog(
    item: LauncherItemState,
    onDismissRequest: () -> Unit,
    onDeleteConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val title = getContainerTitle(item.type)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Configurar $title") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tamanho atual: ${item.colSpan} x ${item.rowSpan} células",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
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
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Fechar")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun EditDialogTypeActionSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    if (item.type == LauncherItemType.APPS_LIST) {
        Button(
            onClick = {
                actions.onOpenFavoritePicker(item.id)
                onDismissRequest()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar Favoritos",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Editar Favoritos e Configurações")
        }
    }

    if (item.type == LauncherItemType.SHORTCUTS_SIDE_PANEL) {
        Button(
            onClick = {
                actions.onOpenEditSidePanelDialog(item.id)
                onDismissRequest()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar Painel Lateral",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "Editar Configurações do Painel Lateral")
        }
    }
}

private fun getContainerTitle(type: LauncherItemType?): String {
    return when (type) {
        LauncherItemType.CLOCK -> "Relógio"
        LauncherItemType.APPS_LIST -> "Lista de Aplicativos"
        LauncherItemType.SHORTCUTS_SIDE_PANEL -> "Barra de Atalhos"
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
        Text(
            text = "Widgets na lista (${uiState.topWidgetIds.size}):",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        uiState.topWidgetIds.forEach { widgetId ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Widget ID: $widgetId", style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { actions.onRemoveWidgetClick(widgetId) }) {
                    Text(text = "Remover Widget", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun EditDialogScrollViewSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    ScrollViewOrientationSelector(item = item, actions = actions)

    Text(
        text = "Componentes Filhos (${item.safeChildren.size}):",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    if (item.safeChildren.isEmpty()) {
        Text(
            text = "Nenhum componente adicionado ainda.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar Componente",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "Adicionar Componente")
    }

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun ScrollViewOrientationSelector(
    item: LauncherItemState,
    actions: HomeScreenActions
) {
    Text(
        text = "Orientação do Scroll:",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.VERTICAL) }
                .padding(end = 16.dp)
        ) {
            RadioButton(
                selected = item.safeScrollOrientation == ScrollOrientation.VERTICAL,
                onClick = { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.VERTICAL) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Vertical", style = MaterialTheme.typography.bodyMedium)
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
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Horizontal", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ScrollViewChildItemRow(
    parentId: String,
    child: LauncherItemState,
    isVertical: Boolean,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    val spanVal = if (isVertical) child.rowSpan else child.colSpan
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = getContainerTitle(child.type),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                onClick = {
                    if (isVertical) {
                        actions.onResizeChildInScrollView(
                            parentId,
                            child.id,
                            child.colSpan,
                            (child.rowSpan - 1).coerceAtLeast(1)
                        )
                    } else {
                        actions.onResizeChildInScrollView(
                            parentId,
                            child.id,
                            (child.colSpan - 1).coerceAtLeast(1),
                            child.rowSpan
                        )
                    }
                }
            ) {
                Text("-", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = "$spanVal",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            TextButton(
                onClick = {
                    if (isVertical) {
                        actions.onResizeChildInScrollView(parentId, child.id, child.colSpan, child.rowSpan + 1)
                    } else {
                        actions.onResizeChildInScrollView(parentId, child.id, child.colSpan + 1, child.rowSpan)
                    }
                }
            ) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }

            if (child.type == LauncherItemType.APPS_LIST) {
                IconButton(
                    onClick = {
                        actions.onOpenFavoritePicker(child.id)
                        onDismissRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Favoritos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = { actions.onRemoveChildFromScrollView(parentId, child.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EditDialogDeleteButtonRow(onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDelete)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remover Container",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Remover Container",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}
