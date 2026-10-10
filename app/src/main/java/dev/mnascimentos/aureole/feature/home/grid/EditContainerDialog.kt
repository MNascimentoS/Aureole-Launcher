package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet

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

    SettingsBottomSheet(
        title = "Configurar $title",
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = "Tamanho atual: ${item.colSpan} x ${item.rowSpan} células",
                    style = AureoleTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = AureoleTheme.colors.onSurfaceMedium
                )
            }

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
    val (text, onClick) = when (item.type) {
        LauncherItemType.CLOCK -> "Personalizar Relógio" to {
            actions.onOpenEditClockBottomSheet()
            onDismissRequest()
        }
        LauncherItemType.APPS_LIST -> "Editar Favoritos e Configurações" to {
            actions.onOpenFavoritePicker(item.id)
            onDismissRequest()
        }
        LauncherItemType.SHORTCUTS_CONTAINER -> "Editar Configurações do Painel Lateral" to {
            actions.onOpenEditContainerDialog(item.id)
            onDismissRequest()
        }
        else -> null to null
    }

    if (text != null && onClick != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.45f))
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AureoleTheme.colors.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Edit(
                    tint = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            AureoleText(
                text = text,
                style = AureoleTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

internal fun getContainerTitle(type: LauncherItemType?): String {
    return when (type) {
        LauncherItemType.CLOCK -> "Relógio"
        LauncherItemType.APPS_LIST -> "Lista de Aplicativos"
        LauncherItemType.SHORTCUTS_CONTAINER -> "Container"
        LauncherItemType.SINGLE_APP_WIDGET,
        LauncherItemType.WIDGET_LIST -> "Grupo de Widgets"
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
    if (item.type == LauncherItemType.WIDGET_LIST || item.type == LauncherItemType.SINGLE_APP_WIDGET) {
        val widgets = uiState.topWidgetIds
        val isIndicatorEnabled = uiState.widgetStackDots[item.id] ?: true

        AureoleText(
            text = "Widgets no Grupo (${widgets.size})",
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        if (widgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.25f))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = "Nenhum widget adicionado ao grupo ainda.",
                    style = AureoleTheme.typography.bodyMedium,
                    color = AureoleTheme.colors.onSurfaceMedium
                )
            }
        } else {
            widgets.forEachIndexed { index, widgetId ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.35f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AureoleText(
                        text = "Widget #${index + 1} (ID: $widgetId)",
                        style = AureoleTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = AureoleTheme.colors.onSurfaceHigh,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { actions.onRemoveWidgetClick(widgetId) }) {
                        AureoleText(text = "Remover", color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = { actions.onAddWidgetClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleTheme.colors.surfaceVariant,
                contentColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            AureoleDS.icons.Add(modifier = Modifier.padding(end = AureoleDS.dimens.xSmall))
            AureoleText(text = "Adicionar Widget ao Grupo", color = AureoleTheme.colors.onSurfaceHigh)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { actions.onToggleWidgetStackDots(item.id) }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AureoleText(
                text = "Mostrar Indicador de Posição",
                style = AureoleTheme.typography.bodyMedium,
                color = AureoleTheme.colors.onSurfaceHigh
            )
            AureoleText(
                text = if (isIndicatorEnabled) "Ativado" else "Desativado",
                style = AureoleTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isIndicatorEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    AureoleTheme.colors.onSurfaceMedium
                }
            )
        }
    }
}

@Composable
private fun EditDialogScrollViewSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ScrollViewOrientationSelector(item = item, actions = actions)

        Spacer(modifier = Modifier.height(2.dp))

        AureoleText(
            text = "Componentes Filhos (${item.safeChildren.size})",
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleTheme.colors.onSurfaceHigh
        )

        if (item.safeChildren.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.25f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = "Nenhum componente adicionado ainda.",
                    style = AureoleTheme.typography.bodyMedium,
                    color = AureoleTheme.colors.onSurfaceMedium
                )
            }
        } else {
            val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
            val totalChildren = item.safeChildren.size
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item.safeChildren.forEachIndexed { index, child ->
                    ScrollViewChildItemCard(
                        parentId = item.id,
                        child = child,
                        childIndex = index,
                        totalChildren = totalChildren,
                        isVertical = isVertical,
                        actions = actions,
                        onDismissRequest = onDismissRequest
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AureoleTheme.colors.surfaceVariant)
                .clickable { actions.onOpenAddContainerForParent(item.id) }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AureoleTheme.colors.surface.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Add(
                    tint = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            AureoleText(
                text = "Adicionar Componente",
                style = AureoleTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh
            )
        }
    }
}

@Composable
private fun ScrollViewOrientationSelector(
    item: LauncherItemState,
    actions: HomeScreenActions
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AureoleText(
            text = "Orientação do Scroll",
            style = AureoleTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OrientationOptionCard(
                title = "Vertical",
                isSelected = item.safeScrollOrientation == ScrollOrientation.VERTICAL,
                onClick = { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.VERTICAL) },
                modifier = Modifier.weight(1f)
            )

            OrientationOptionCard(
                title = "Horizontal",
                isSelected = item.safeScrollOrientation == ScrollOrientation.HORIZONTAL,
                onClick = { actions.onUpdateScrollViewOrientation(item.id, ScrollOrientation.HORIZONTAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun OrientationOptionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
    } else {
        AureoleTheme.colors.surfaceVariant.copy(alpha = 0.35f)
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        AureoleTheme.colors.onSurfaceMedium
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(2.dp))
        AureoleText(
            text = title,
            style = AureoleTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun EditDialogDeleteButtonRow(onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.12f))
            .clickable(onClick = onDelete)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        AureoleText(
            text = "Remover Container",
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
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
