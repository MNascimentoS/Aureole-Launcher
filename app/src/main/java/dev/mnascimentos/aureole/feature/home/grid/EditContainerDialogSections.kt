package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
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
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
internal fun EditDialogDeleteRow(
    itemId: String,
    onDeleteConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.error.copy(alpha = 0.12f))
            .clickable(
                role = Role.Button,
                onClick = {
                    onDeleteConfirm(itemId)
                    onDismissRequest()
                }
            )
            .semantics(mergeDescendants = true) {}
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
internal fun EditDialogTypeActionSection(
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
        LauncherItemType.QUICK_BUTTON -> "Personalizar Botão Rápido" to {
            actions.onOpenQuickButtonSettings(item.id)
            onDismissRequest()
        }
        else -> null to null
    }

    if (text == null || onClick == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
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

@Composable
internal fun EditDialogScrollViewSection(
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
                .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant)
                .clickable(
                    role = Role.Button,
                    onClick = { actions.onOpenAddContainerForParent(item.id) }
                )
                .semantics(mergeDescendants = true) {}
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

private fun mockHomeScreenActions() = HomeScreenActions(
    onWidgetRowHeightChanged = {},
    onAppClick = {},
    onExpandNotificationShade = {},
    onFolderIntent = {},
    onSetAddAppToFolderDialogVisible = {},
    onSetRenameFolderDialogVisible = {},
    onSearchQueryChanged = {},
    onSettingsClick = {},
    onAllAppsDrawerClose = {},
    onAllAppsDrawerOpen = {}
)

@AureolePreview
@Composable
private fun EditDialogDeleteRowPreview() {
    AureoleLauncherTheme {
        EditDialogDeleteRow(
            itemId = "preview_item",
            onDeleteConfirm = {},
            onDismissRequest = {}
        )
    }
}

@AureolePreview
@Composable
private fun EditDialogTypeActionSectionPreview() {
    AureoleLauncherTheme {
        EditDialogTypeActionSection(
            item = LauncherItemState(
                id = "clock_item",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 0,
                colSpan = 2,
                rowSpan = 2
            ),
            actions = mockHomeScreenActions(),
            onDismissRequest = {}
        )
    }
}

@AureolePreview
@Composable
private fun EditDialogScrollViewSectionPreview() {
    AureoleLauncherTheme {
        EditDialogScrollViewSection(
            item = LauncherItemState(
                id = "scroll_view_item",
                type = LauncherItemType.SCROLL_VIEW,
                col = 0,
                row = 0,
                colSpan = 4,
                rowSpan = 4
            ),
            actions = mockHomeScreenActions(),
            onDismissRequest = {}
        )
    }
}
