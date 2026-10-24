package dev.mnascimentos.aureole.feature.home.components.overlays

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
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.SettingsBottomSheet
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Icon
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.Placeholder
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

@Composable
fun CustomizeBottomSheet(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsBottomSheet(
        title = stringResource(R.string.customize_title),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            CustomizeAddItemOptions(actions = actions, onDismissRequest = onDismissRequest)
            CustomizeSystemOptions(actions = actions, onDismissRequest = onDismissRequest)
        }
    }
}

@Composable
private fun CustomizeAddItemOptions(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    Column {
        CustomizeClockOption(actions, onDismissRequest)
        CustomizeContainerOption(actions, onDismissRequest)
        CustomizeScrollViewOption(actions, onDismissRequest)
        CustomizeQuickButtonOption(actions, onDismissRequest)
        CustomizeWidgetGroupOption(actions, onDismissRequest)
    }
}

@Composable
private fun CustomizeClockOption(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    CustomizeOptionItem(
        title = stringResource(R.string.customize_add_clock),
        icon = {
            AureoleDS.icons.Add(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        onClick = {
            actions.onAddGridItem(LauncherItemType.CLOCK, null, null)
            onDismissRequest()
        }
    )
}

@Composable
private fun CustomizeContainerOption(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    CustomizeOptionItem(
        title = stringResource(R.string.customize_add_container),
        icon = {
            AureoleDS.icons.Star(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        onClick = {
            actions.onAddGridItem(LauncherItemType.SHORTCUTS_CONTAINER, null, null)
            onDismissRequest()
        }
    )
}

@Composable
private fun CustomizeScrollViewOption(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    CustomizeOptionItem(
        title = stringResource(R.string.customize_add_scrollview),
        icon = {
            AureoleDS.icons.VerticalContainer(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        onClick = {
            actions.onAddGridItem(LauncherItemType.SCROLL_VIEW, null, ScrollOrientation.VERTICAL)
            onDismissRequest()
        }
    )
}

@Composable
private fun CustomizeQuickButtonOption(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    CustomizeOptionItem(
        title = stringResource(R.string.customize_add_quick_button),
        icon = {
            AureoleDS.icons.Click(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        onClick = {
            actions.onAddGridItem(LauncherItemType.QUICK_BUTTON, null, null)
            onDismissRequest()
        }
    )
}

@Composable
private fun CustomizeWidgetGroupOption(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    CustomizeOptionItem(
        title = stringResource(R.string.customize_add_widget_group),
        icon = {
            AureoleDS.icons.MultipleView(
                modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                tint = AureoleDS.colors.onSurfaceMedium
            )
        },
        onClick = {
            actions.onAddGridItem(LauncherItemType.WIDGET_LIST, null, null)
            onDismissRequest()
        }
    )
}

@Composable
private fun CustomizeSystemOptions(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    Column {
        CustomizeOptionItem(
            title = stringResource(R.string.customize_adjust_layout),
            icon = {
                AureoleDS.icons.Layout(
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                    tint = AureoleDS.colors.onSurfaceMedium
                )
            },
            onClick = {
                actions.onEnterGridEditMode()
                onDismissRequest()
            }
        )
        CustomizeOptionItem(
            title = stringResource(R.string.customize_set_background),
            icon = {
                AureoleDS.icons.Placeholder(
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                    tint = AureoleDS.colors.onSurfaceMedium
                )
            },
            onClick = {
                actions.onSettingsClick()
                onDismissRequest()
            }
        )
        CustomizeOptionItem(
            title = stringResource(R.string.customize_aureole_settings),
            icon = {
                AureoleDS.icons.Icon(
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge),
                    tint = AureoleDS.colors.onSurfaceMedium
                )
            },
            onClick = {
                actions.onSettingsClick()
                onDismissRequest()
            }
        )
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
            modifier = Modifier.size(AureoleDS.dimens.iconLarge),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        AureoleText(
            text = title,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
    }
}

@AureolePreview
@Composable
private fun CustomizeBottomSheetPreview() {
    val actions = HomeScreenActions(
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
    AureoleLauncherTheme {
        CustomizeBottomSheet(
            actions = actions,
            onDismissRequest = {}
        )
    }
}
