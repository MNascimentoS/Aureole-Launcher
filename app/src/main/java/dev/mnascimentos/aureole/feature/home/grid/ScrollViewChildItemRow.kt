package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
internal fun ScrollViewChildItemRow(
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

        ScrollViewChildControls(
            ScrollViewChildArgs(
                parentId = parentId,
                child = child,
                isVertical = isVertical,
                spanVal = spanVal,
                actions = actions,
                onDismissRequest = onDismissRequest
            )
        )
    }
}

@Composable
private fun ScrollViewChildControls(args: ScrollViewChildArgs) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ScrollViewChildDecreaseButton(args.parentId, args.child, args.isVertical, args.actions)
        Text(
            text = "${args.spanVal}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        ScrollViewChildIncreaseButton(args.parentId, args.child, args.isVertical, args.actions)

        if (args.child.type == LauncherItemType.APPS_LIST) {
            IconButton(
                onClick = {
                    args.actions.onOpenFavoritePicker(args.child.id)
                    args.onDismissRequest()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar Favoritos",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        IconButton(onClick = { args.actions.onRemoveChildFromScrollView(args.parentId, args.child.id) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Remover",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun ScrollViewChildDecreaseButton(
    parentId: String,
    child: LauncherItemState,
    isVertical: Boolean,
    actions: HomeScreenActions
) {
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
}

@Composable
private fun ScrollViewChildIncreaseButton(
    parentId: String,
    child: LauncherItemState,
    isVertical: Boolean,
    actions: HomeScreenActions
) {
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
}

data class ScrollViewChildArgs(
    val parentId: String,
    val child: LauncherItemState,
    val isVertical: Boolean,
    val spanVal: Int,
    val actions: HomeScreenActions,
    val onDismissRequest: () -> Unit
)
