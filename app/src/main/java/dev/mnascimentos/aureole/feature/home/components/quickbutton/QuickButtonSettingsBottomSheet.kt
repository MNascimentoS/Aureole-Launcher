package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Quick button menu: "Remove" on top, then the click shortcut and the appearance, each with a chevron. The rows
 * open the shortcut picker and the appearance editor; nothing here is a draft, so there is no Save / Cancel.
 */
@Composable
fun QuickButtonSettingsBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    val clickSubtitle = clickShortcutLabel(activeModel = activeModel, uiState = uiState)

    AureoleMenuSheet(
        title = stringResource(R.string.home_quickbutton_settings_title),
        onDismiss = onDismissRequest,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> AureoleDS.icons.Click(modifier = m, tint = AureoleDS.colors.onSurfaceMedium) },
            onSettingsClick = { actions.onSettingsClick() }
        )
    ) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_quickbutton_remove),
                onClick = { uiState.activeQuickButtonId?.let { actions.onDeleteGridItem(it) } },
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true
            )
        )
        AureoleMenuSectionLabel(text = stringResource(R.string.home_quickbutton_menu_section))
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_quickbutton_click_shortcut),
                subtitle = clickSubtitle,
                onClick = { actions.onOpenShortcutSelection("click") },
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_quickbutton_appearance),
                onClick = { actions.onOpenQuickButtonAppearance() },
                showChevron = true
            )
        )
    }
}

/** What the click shortcut does, in words: "Open App (Browser)" for an app, the shortcut's name otherwise. */
@Composable
private fun clickShortcutLabel(activeModel: QuickButtonModel, uiState: MainUiState): String {
    val click = activeModel.shortcuts.click
    if (click.type != ShortcutType.OPEN_APP) return click.displayName
    val appLabel = uiState.apps.find { it.packageName == click.target }?.label
    return if (appLabel != null) {
        stringResource(R.string.home_quickbutton_open_app_name, appLabel)
    } else {
        stringResource(R.string.home_quickbutton_open_app)
    }
}

@AureolePreview
@Composable
private fun QuickButtonSettingsBottomSheetPreview() {
    val dummyActions = HomeScreenActions(
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
    AureoleLauncherTheme {
        QuickButtonSettingsBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
