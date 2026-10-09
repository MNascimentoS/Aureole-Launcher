package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.HorizontalContainer
import dev.mnascimentos.aureole.core.designsystem.icons.Icon
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.Placeholder
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet

@Composable
fun CustomizeBottomSheet(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
) {
    SettingsBottomSheet(
        title = "Customize",
        onDismissRequest = onDismissRequest
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            CustomizeOptionItem(
                title = "Add quick panel",
                icon = {
                    AureoleDS.icons.Star(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onAddGridItem(LauncherItemType.SHORTCUTS_CONTAINER, null, null)
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Add vertical container",
                icon = {
                    AureoleDS.icons.VerticalContainer(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onAddGridItem(LauncherItemType.SCROLL_VIEW, null, ScrollOrientation.VERTICAL)
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Add horizontal container",
                icon = {
                    AureoleDS.icons.HorizontalContainer(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onAddGridItem(LauncherItemType.SCROLL_VIEW, null, ScrollOrientation.HORIZONTAL)
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Add custom widget steak",
                icon = {
                    AureoleDS.icons.MultipleView(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onAddGridItem(LauncherItemType.WIDGET_LIST, null, null)
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Adjust layout",
                icon = {
                    AureoleDS.icons.Layout(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onEnterGridEditMode()
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Set Background",
                icon = {
                    AureoleDS.icons.Placeholder(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onSettingsClick()
                    onDismissRequest()
                }
            )
            CustomizeOptionItem(
                title = "Aureole settings",
                icon = {
                    AureoleDS.icons.Icon(
                        modifier = Modifier.size(24.dp),
                        tint = AureoleTheme.colors.onSurfaceMedium
                    )
                },
                onClick = {
                    actions.onSettingsClick()
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun CustomizeOptionItem(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = AureoleDS.dimens.medium, horizontal = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            style = AureoleTheme.typography.bodyLarge,
            color = AureoleTheme.colors.onSurfaceMedium
        )
    }
}

@AureolePreview
@Composable
fun CustomizeBottomSheetPreview() {
    val actions = HomeScreenActions(
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
    AureoleLauncherTheme {
        CustomizeBottomSheet(
            actions = actions,
            onDismissRequest = {}
        )
    }
}
