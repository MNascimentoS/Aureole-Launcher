package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.MenuIcon
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
internal fun ScrollViewChildItemRow(
    parentId: String,
    child: LauncherItemState,
    isVertical: Boolean,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    ScrollViewChildItemCard(
        parentId = parentId,
        child = child,
        childIndex = 0,
        totalChildren = 1,
        isVertical = isVertical,
        actions = actions,
        onDismissRequest = onDismissRequest
    )
}

@Composable
internal fun ScrollViewChildItemCard(
    parentId: String,
    child: LauncherItemState,
    childIndex: Int,
    totalChildren: Int,
    isVertical: Boolean,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spanVal = if (isVertical) child.rowSpan else child.colSpan
    val title = getContainerTitle(child.type)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AureoleTheme.colors.surface.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                ChildTypeIcon(child.type)
            }

            Spacer(modifier = Modifier.width(12.dp))

            AureoleText(
                text = title,
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { handleEditChildClick(child, actions, onDismissRequest) },
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.Edit(
                    tint = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = { actions.onRemoveChildFromScrollView(parentId, child.id) },
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.Delete(
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { actions.onMoveChildInScrollView(parentId, child.id, true) },
                    enabled = (childIndex > 0),
                    modifier = Modifier.size(28.dp)
                ) {
                    AureoleDS.icons.ArrowUp(
                        tint = if (childIndex > 0) {
                            AureoleTheme.colors.onSurfaceHigh
                        } else {
                            AureoleTheme.colors.onSurfaceMedium.copy(
                                alpha = 0.4f
                            )
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { actions.onMoveChildInScrollView(parentId, child.id, false) },
                    enabled = (childIndex < totalChildren - 1),
                    modifier = Modifier.size(28.dp)
                ) {
                    AureoleDS.icons.ArrowDown(
                        tint = if (childIndex < totalChildren - 1) {
                            AureoleTheme.colors.onSurfaceHigh
                        } else {
                            AureoleTheme.colors.onSurfaceMedium.copy(
                                alpha = 0.4f
                            )
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AureoleTheme.colors.surface.copy(alpha = 0.5f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                IconButton(
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
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    AureoleText(
                        text = "-",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleTheme.colors.onSurfaceHigh
                    )
                }

                AureoleText(
                    text = "$spanVal cél.",
                    style = AureoleTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = {
                        if (isVertical) {
                            actions.onResizeChildInScrollView(parentId, child.id, child.colSpan, child.rowSpan + 1)
                        } else {
                            actions.onResizeChildInScrollView(parentId, child.id, child.colSpan + 1, child.rowSpan)
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    AureoleText(
                        text = "+",
                        style = AureoleTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AureoleTheme.colors.onSurfaceHigh
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildTypeIcon(type: LauncherItemType?) {
    when (type) {
        LauncherItemType.CLOCK -> AureoleDS.icons.Add(Modifier.size(20.dp))
        LauncherItemType.APPS_LIST -> AureoleDS.icons.HorizontalContainer(Modifier.size(20.dp))
        LauncherItemType.SHORTCUTS_CONTAINER -> AureoleDS.icons.MenuIcon(Modifier.size(20.dp))
        LauncherItemType.WIDGET_LIST, LauncherItemType.SINGLE_APP_WIDGET -> AureoleDS.icons.Add(Modifier.size(20.dp))
        else -> AureoleDS.icons.VerticalContainer(Modifier.size(20.dp))
    }
}

private fun handleEditChildClick(
    child: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    when (child.safeType) {
        LauncherItemType.APPS_LIST -> {
            actions.onOpenFavoritePicker(child.id)
            onDismissRequest()
        }
        LauncherItemType.CLOCK -> {
            actions.onOpenEditClockBottomSheet()
            onDismissRequest()
        }
        LauncherItemType.SHORTCUTS_CONTAINER -> {
            actions.onOpenEditContainerDialog(child.id)
            onDismissRequest()
        }
        LauncherItemType.WIDGET_LIST, LauncherItemType.SINGLE_APP_WIDGET -> {
            actions.onAddWidgetClick()
            onDismissRequest()
        }
        else -> {
            actions.onOpenEditGridItemDialog(child)
            onDismissRequest()
        }
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
