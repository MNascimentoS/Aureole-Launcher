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
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Icon
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Placeholder
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
        title = stringResource(R.string.home_customize_title),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            CustomizeAddOptionsComponent(actions = actions, onDismissRequest = onDismissRequest)
            CustomizeSystemOptions(actions = actions, onDismissRequest = onDismissRequest)
        }
    }
}

@Composable
private fun CustomizeSystemOptions(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    Column {
        CustomizeOptionItem(
            title = stringResource(R.string.home_customize_adjust_layout),
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
            title = stringResource(R.string.home_customize_set_background),
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
            title = stringResource(R.string.home_customize_aureole_settings),
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
internal fun CustomizeOptionItem(
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
