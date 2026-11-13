package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoritesMenuSubtitle
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.favoritesMenuSubtitle
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.customizeHomeRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.AllApps
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Top of the "Choose favorites" screen for a home apps list: the "Show all apps" switch, which applies at once and
 * whose subtitle says what the Home shows, and the "Customize home" row. Without [FavoriteAppsDialogConfig.showAllApps]
 * (Settings, drawer favorites) there is no header.
 */
@Composable
internal fun FavoriteAppsListHeader(
    config: FavoriteAppsDialogConfig,
    modifier: Modifier = Modifier,
    onCustomizeHome: (() -> Unit)? = null
) {
    val showAllApps = config.showAllApps ?: return
    Column(modifier = modifier) {
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.home_apps_list_show_all),
                checked = showAllApps.checked,
                onCheckedChange = showAllApps.onCheckedChange,
                description = showAllSubtitle(config.favoriteAppPackages.size, showAllApps.checked),
                icon = { m, c -> AureoleDS.icons.AllApps(modifier = m, tint = c) }
            )
        )
        onCustomizeHome?.let { AureoleMenuRow(customizeHomeRowParams(it)) }
    }
}

/** The text of [favoritesMenuSubtitle] for [favoriteCount] favorites and the list's switch. */
@Composable
@ReadOnlyComposable
private fun showAllSubtitle(favoriteCount: Int, showAllApps: Boolean): String =
    when (favoritesMenuSubtitle(favoriteCount, showAllApps)) {
        FavoritesMenuSubtitle.ALL_SHOWN -> stringResource(R.string.home_favorites_menu_all_shown)
        FavoritesMenuSubtitle.NONE_SELECTED -> stringResource(R.string.home_favorites_none_selected)
        FavoritesMenuSubtitle.COUNT ->
            pluralStringResource(R.plurals.home_favorites_menu_count, favoriteCount, favoriteCount)
        FavoritesMenuSubtitle.COUNT_ON_TOP ->
            pluralStringResource(R.plurals.home_favorites_menu_count_on_top, favoriteCount, favoriteCount)
    }

@AureolePreview
@Composable
private fun FavoriteAppsListHeaderPreview() {
    AureoleLauncherTheme {
        FavoriteAppsListHeader(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = listOf("a", "b", "c"),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {})
            ),
            onCustomizeHome = {}
        )
    }
}
