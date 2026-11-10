package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(instrumentedPackages = ["androidx.loader.content"])
class FavoriteAppsDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.RED)
    )

    @Test
    fun `when rendering FavoriteAppsDialog with empty favorites, should display empty message`() {
        val config = FavoriteAppsDialogConfig(
            allApps = emptyList(),
            favoriteAppPackages = emptyList()
        )
        val actions = FavoriteAppsDialogActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsDialog(config = config, actions = actions)
            }
        }

        composeTestRule.onAllNodesWithText("No favorite app selected.").onFirst().assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppsDialog with favorites, should display favorite items`() {
        val app1 = createApp("App Favorito 1", "com.example.fav1")
        val config = FavoriteAppsDialogConfig(
            allApps = listOf(app1),
            favoriteAppPackages = listOf("com.example.fav1")
        )
        val actions = FavoriteAppsDialogActions()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsDialog(config = config, actions = actions)
            }
        }

        composeTestRule.onAllNodesWithText("App Favorito 1").onFirst().assertIsDisplayed()
    }
}
