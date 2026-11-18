package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.grid.model.ScrollViewChildParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
internal fun EditDialogChildrenListSection(
    item: LauncherItemState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    if (item.safeChildren.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.25f))
                .padding(AureoleDS.dimens.medium),
            contentAlignment = Alignment.Center
        ) {
            AureoleText(
                text = stringResource(R.string.home_container_edit_none_added),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    } else {
        val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
        val totalChildren = item.safeChildren.size
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
            item.safeChildren.forEachIndexed { index, child ->
                ScrollViewChildItemCard(
                    params = ScrollViewChildParams(
                        parentId = item.id,
                        child = child,
                        childIndex = index,
                        totalChildren = totalChildren,
                        isVertical = isVertical
                    ),
                    actions = actions,
                    onDismissRequest = onDismissRequest
                )
            }
        }
    }
}

@Composable
internal fun ScrollViewOrientationSelector(
    item: LauncherItemState,
    actions: HomeScreenActions
) {
    val vertical = stringResource(R.string.home_container_edit_vertical)
    val horizontal = stringResource(R.string.home_container_edit_horizontal)
    AureoleSegmentedControl(
        options = listOf(vertical, horizontal),
        selectedIndex = if (item.safeScrollOrientation == ScrollOrientation.VERTICAL) 0 else 1,
        onSelect = { index ->
            val orientation = if (index == 0) ScrollOrientation.VERTICAL else ScrollOrientation.HORIZONTAL
            actions.onUpdateScrollViewOrientation(item.id, orientation)
        },
        icons = listOf(
            { tint -> AureoleDS.icons.VerticalContainer(Modifier.size(AureoleDS.dimens.iconSmall), tint) },
            { tint -> AureoleDS.icons.HorizontalContainer(Modifier.size(AureoleDS.dimens.iconSmall), tint) }
        )
    )
}

@AureolePreview
@Composable
private fun EditGridItemDialogPreview() {
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
            LocalLeftHanded provides (MainUiState()).isLeftHandedMode,
            LocalHomeActions provides HomeScreenActions(
                onWidgetRowHeightChanged = {},
                onAddWidgetClick = {},
                onRemoveWidgetClick = { _, _ -> },
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
