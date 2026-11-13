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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Star
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
    AureoleMenuRow(editDialogDeleteParams(itemId, onDeleteConfirm, onDismissRequest))
}

/** The destructive "Remove component" row; the menu base moves it to the end of the list. */
@Composable
internal fun editDialogDeleteParams(
    itemId: String,
    onDeleteConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) = MenuRowParams(
    title = stringResource(R.string.home_grid_remove_component),
    onClick = {
        onDeleteConfirm(itemId)
        onDismissRequest()
    },
    icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
    isDestructive = true
)

@Composable
internal fun EditDialogTypeActionSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    val (text, onClick) = when (item.type) {
        LauncherItemType.CLOCK -> stringResource(R.string.home_grid_customize_clock) to {
            actions.onOpenEditClockBottomSheet()
            onDismissRequest()
        }
        LauncherItemType.APPS_LIST -> stringResource(R.string.home_grid_edit_favorites) to {
            actions.onOpenFavoritePicker(item.id)
            onDismissRequest()
        }
        LauncherItemType.SHORTCUTS_CONTAINER -> stringResource(R.string.home_grid_edit_sidebar) to {
            actions.onOpenEditContainerDialog(item.id)
            onDismissRequest()
        }
        LauncherItemType.QUICK_BUTTON -> stringResource(R.string.home_grid_customize_quick_button) to {
            actions.onOpenQuickButtonSettings(item.id)
            onDismissRequest()
        }
        else -> null to null
    }

    if (text == null || onClick == null) return

    AureoleMenuRow(
        MenuRowParams(
            title = text,
            onClick = onClick,
            icon = { m, c -> TypeActionIcon(type = item.type, modifier = m, tint = c) },
            showChevron = true
        )
    )
}

/** Icon of the type specific row: Layout for the side panel, Click, Star and Calendar. */
@Composable
private fun TypeActionIcon(type: LauncherItemType?, modifier: Modifier, tint: Color) {
    when (type) {
        LauncherItemType.CLOCK -> AureoleDS.icons.Calendar(modifier = modifier, tint = tint)
        LauncherItemType.APPS_LIST -> AureoleDS.icons.Star(modifier = modifier, tint = tint)
        LauncherItemType.QUICK_BUTTON -> AureoleDS.icons.Click(modifier = modifier, tint = tint)
        else -> AureoleDS.icons.Layout(modifier = modifier, tint = tint)
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
            text = stringResource(R.string.home_container_edit_child_components, item.safeChildren.size),
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
                text = stringResource(R.string.home_grid_add_component),
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
