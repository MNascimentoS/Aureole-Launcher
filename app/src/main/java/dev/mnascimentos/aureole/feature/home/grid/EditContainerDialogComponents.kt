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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
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
                .clip(RoundedCornerShape(14.dp))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.25f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AureoleText(
                text = "Nenhum componente adicionado ainda.",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    } else {
        val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
        val totalChildren = item.safeChildren.size
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
    Column(modifier = Modifier.fillMaxWidth()) {
        AureoleText(
            text = "Orientação do Scroll",
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.padding(bottom = 10.dp),
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
        AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f)
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f)
    }
    val contentColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(
                role = Role.RadioButton,
                onClick = onClick
            )
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
            color = contentColor,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                softWrap = false
            )
        )
    }
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
