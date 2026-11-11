package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Main menu of the favorites sheet: the rows that lead to the shortcut editor and to the manage list, whose
 * subtitle says what the Home shows.
 */
@Composable
internal fun FavoriteAppsMainContent(
    config: FavoriteAppsDialogConfig,
    onModeChange: (FavoriteAppsScreenMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_favorites_edit_shortcuts),
                onClick = { onModeChange(FavoriteAppsScreenMode.EDIT_SHORTCUTS) },
                icon = { m, c -> AureoleDS.icons.Click(modifier = m, tint = c) },
                showChevron = true
            )
        )
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_favorites_manage_apps_order),
                subtitle = manageSubtitle(config.favoriteAppPackages.size),
                onClick = { onModeChange(FavoriteAppsScreenMode.MANAGE) },
                icon = { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) },
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
            onModeChange = {}
        )
    }
}

/** "N favorites, the rest stays hidden", or that the Home shows every app when there are no favorites. */
@Composable
@ReadOnlyComposable
private fun manageSubtitle(favoriteCount: Int): String =
    if (favoriteCount == 0) {
        stringResource(R.string.home_favorites_menu_all_shown)
    } else {
        pluralStringResource(R.plurals.home_favorites_menu_count, favoriteCount, favoriteCount)
    }
