package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesMenuSubtitle
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.favoritesMenuSubtitle
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.customizeHomeRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Main menu of the favorites sheet: "Choose favorites", which leads to the manage list and whose subtitle says what
 * the Home shows, and, for a home apps list, the "Show all apps" switch, which applies at once.
 */
@Composable
internal fun FavoriteAppsMainContent(
    config: FavoriteAppsDialogConfig,
    onModeChange: (FavoriteAppsScreenMode) -> Unit,
    modifier: Modifier = Modifier,
    onCustomizeHome: (() -> Unit)? = null
) {
    Column(modifier = modifier.padding(bottom = AureoleDS.dimens.large)) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_favorites_choose),
                subtitle = chooseSubtitle(config.favoriteAppPackages.size, config.showAllApps?.checked),
                onClick = { onModeChange(FavoriteAppsScreenMode.MANAGE) },
                icon = { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) },
                showChevron = true
            )
        )
        config.showAllApps?.let { showAllApps ->
            AureoleSwitchRow(
                SwitchRowParams(
                    title = stringResource(R.string.home_apps_list_show_all),
                    checked = showAllApps.checked,
                    onCheckedChange = showAllApps.onCheckedChange,
                    description = stringResource(R.string.home_apps_list_show_all_description),
                    icon = { m, c -> AureoleDS.icons.AllApps(modifier = m, tint = c) }
                )
            )
            onCustomizeHome?.let { AureoleMenuRow(customizeHomeRowParams(it)) }
        }
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsMainContentPreview() {
    AureoleLauncherTheme {
        FavoriteAppsMainContent(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = listOf("a", "b", "c", "d", "e"),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {})
            ),
            onModeChange = {},
            onCustomizeHome = {}
        )
    }
}

/** The text of [favoritesMenuSubtitle] for [favoriteCount] favorites and the list's switch. */
@Composable
@ReadOnlyComposable
private fun chooseSubtitle(favoriteCount: Int, showAllApps: Boolean?): String =
    when (favoritesMenuSubtitle(favoriteCount, showAllApps)) {
        FavoritesMenuSubtitle.ALL_SHOWN -> stringResource(R.string.home_favorites_menu_all_shown)
        FavoritesMenuSubtitle.NONE_SELECTED -> stringResource(R.string.home_favorites_none_selected)
        FavoritesMenuSubtitle.COUNT ->
            pluralStringResource(R.plurals.home_favorites_menu_count, favoriteCount, favoriteCount)
        FavoritesMenuSubtitle.COUNT_ON_TOP ->
            pluralStringResource(R.plurals.home_favorites_menu_count_on_top, favoriteCount, favoriteCount)
    }
