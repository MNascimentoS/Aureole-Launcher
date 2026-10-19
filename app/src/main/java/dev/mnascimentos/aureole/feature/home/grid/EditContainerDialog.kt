package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
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
