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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack
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
    val title = when (item.type) {
        LauncherItemType.CLOCK -> "Relógio"
        LauncherItemType.APPS_LIST -> "Favoritos"
        LauncherItemType.SHORTCUTS_CONTAINER -> "Shortcuts"
        LauncherItemType.SINGLE_APP_WIDGET -> "Widget"
        LauncherItemType.WIDGET_LIST -> "Grupo de Widgets"
        LauncherItemType.SCROLL_VIEW -> "Componente Rolar"
        else -> "Container"
    }

    SettingsBottomSheet(
        title = "Configurar $title",
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = "Tamanho atual: ${item.colSpan} x ${item.rowSpan} células",
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
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

            EditDialogDeleteRow(
                itemId = item.id,
                onDeleteConfirm = onDeleteConfirm,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
private fun EditDialogDeleteRow(
    itemId: String,
    onDeleteConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.error.copy(alpha = 0.12f))
            .clickable {
                onDeleteConfirm(itemId)
                onDismissRequest()
            }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.xLarge)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                .background(AureoleDS.colors.error.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.error,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
        AureoleText(
            text = "Remover Componente",
            color = AureoleDS.colors.error,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Bold)
        )
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
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
                .clickable(onClick = onClick)
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                    .background(AureoleDS.colors.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Edit(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))
            AureoleText(
                text = text,
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun EditDialogWidgetListSection(
    item: LauncherItemState,
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    if (item.type == LauncherItemType.WIDGET_LIST || item.type == LauncherItemType.SINGLE_APP_WIDGET) {
        val widgets = uiState.getWidgetIdsForStack(item.id)
        val isIndicatorEnabled = uiState.widgetStackDots[item.id] ?: true

        AureoleText(
            text = "Widgets no Grupo (${widgets.size})",
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        )

        EditDialogWidgetListRows(widgets = widgets, stackId = item.id, actions = actions)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

        Button(
            onClick = { actions.onAddWidgetClick(item.id) },
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleDS.colors.surfaceVariant,
                contentColor = AureoleDS.colors.onSurfaceHigh
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            AureoleDS.icons.Add(modifier = Modifier.padding(end = AureoleDS.dimens.xSmall))
            AureoleText(text = "Adicionar Widget ao Grupo", color = AureoleDS.colors.onSurfaceHigh)
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .clickable { actions.onToggleWidgetStackDots(item.id) }
                .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AureoleText(
                text = "Mostrar Indicador de Posição",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
            AureoleText(
                text = if (isIndicatorEnabled) "Ativado" else "Desativado",
                color = if (isIndicatorEnabled) {
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.onSurfaceMedium
                },
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun EditDialogWidgetListRows(
    widgets: List<Int>,
    stackId: String,
    actions: HomeScreenActions
) {
    if (widgets.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.25f))
                .padding(AureoleDS.dimens.medium),
            contentAlignment = Alignment.Center
        ) {
            AureoleText(
                text = "Nenhum widget adicionado ao grupo ainda.",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    } else {
        widgets.forEachIndexed { index, widgetId ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
                    .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AureoleText(
                    text = "Widget #${index + 1} (ID: $widgetId)",
                    color = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.weight(1f),
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                )
                TextButton(onClick = { actions.onRemoveWidgetClick(widgetId, stackId) }) {
                    AureoleText(text = "Remover", color = AureoleDS.colors.error)
                }
            }
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
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
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        ScrollViewOrientationSelector(item = item, actions = actions)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.minimal))

        AureoleText(
            text = "Componentes Filhos (${item.safeChildren.size})",
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        )

        EditDialogChildrenListSection(item = item, actions = actions, onDismissRequest = onDismissRequest)

        Spacer(modifier = Modifier.height(AureoleDS.dimens.minimal))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant)
                .clickable { actions.onOpenAddContainerForParent(item.id) }
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.xLarge)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
                    .background(AureoleDS.colors.surface.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Add(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))
            AureoleText(
                text = "Adicionar Componente",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@AureolePreview
@Composable
private fun EditGridItemDialogPreview() {
    val sampleItem = LauncherItemState(
        id = "sample_item",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )
    AureoleLauncherTheme {
        EditGridItemDialog(
            item = sampleItem,
            onDismissRequest = {},
            onDeleteConfirm = {}
        )
    }
}
