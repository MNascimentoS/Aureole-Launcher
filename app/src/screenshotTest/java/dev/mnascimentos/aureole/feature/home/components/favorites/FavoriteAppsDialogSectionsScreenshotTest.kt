package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsSheetContent
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode

@PreviewTest
@AureolePreview
@Composable
private fun FavoriteAppsDialogSectionsScreenshotTest() {
    AureoleLauncherTheme {
        Column {
            FavoriteAppsSheetContent(
                currentMode = FavoriteAppsScreenMode.MAIN,
                config = FavoriteAppsDialogConfig(
                    allApps = emptyList(),
                    favoriteAppPackages = emptyList()
                ),
                actions = FavoriteAppsDialogActions(),
                onEditShortcutsClick = {},
                onAppearanceClick = {}
            )

            FavoriteAppsSheetContent(
                currentMode = FavoriteAppsScreenMode.APPEARANCE,
                config = FavoriteAppsDialogConfig(
                    allApps = emptyList(),
                    favoriteAppPackages = emptyList(),
                    showAllAppsOnHome = true
                ),
                actions = FavoriteAppsDialogActions(),
                onEditShortcutsClick = {},
                onAppearanceClick = {}
            )
        }
    }
}
