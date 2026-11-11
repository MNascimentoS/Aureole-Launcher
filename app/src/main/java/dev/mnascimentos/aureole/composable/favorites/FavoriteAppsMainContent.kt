package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Main menu of the favorites sheet: the "show all apps" switch and the rows that lead to the shortcut editor, the
 * appearance and the manage list.
 */
@Composable
internal fun FavoriteAppsMainContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    onModeChange: (FavoriteAppsScreenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AureoleSwitchRow(
            title = stringResource(R.string.home_favorites_show_all_apps),
            description = stringResource(R.string.home_favorites_show_all_apps_desc),
            checked = config.showAllAppsOnHome,
            onCheckedChange = { actions.onToggleShowAllAppsOnHome() }
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_favorites_edit_shortcuts),
                onClick = { onModeChange(FavoriteAppsScreenMode.EDIT_SHORTCUTS) },
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_container_edit_appearance),
                subtitle = stringResource(R.string.home_favorites_appearance_desc),
                onClick = { onModeChange(FavoriteAppsScreenMode.APPEARANCE) },
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_favorites_manage_apps_order),
                subtitle = pluralStringResource(
                    R.plurals.home_favorites_menu_count,
                    config.favoriteAppPackages.size,
                    config.favoriteAppPackages.size
                ),
                onClick = { onModeChange(FavoriteAppsScreenMode.MANAGE) },
                showChevron = true
            )
        )
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsMainContentPreview() {
    AureoleLauncherTheme {
        FavoriteAppsMainContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = listOf("a", "b", "c", "d", "e")
            ),
            actions = FavoriteAppsDialogActions(),
            onModeChange = {}
        )
    }
}

/** Appearance sub-screen of the favorites sheet: how the Home shows the apps (the "show all apps" switch). */
@Composable
internal fun FavoriteAppsAppearanceContent(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AureoleSwitchRow(
            title = stringResource(R.string.home_favorites_show_all_apps),
            description = stringResource(R.string.home_favorites_show_all_apps_desc),
            checked = config.showAllAppsOnHome,
            onCheckedChange = { actions.onToggleShowAllAppsOnHome() }
        )
    }
}
