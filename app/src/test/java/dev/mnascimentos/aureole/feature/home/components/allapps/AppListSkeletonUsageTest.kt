package dev.mnascimentos.aureole.feature.home.components.allapps

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppsDialog
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.isAppsLoading
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** FUE-24 B: every component that lists apps shows the same skeleton until the apps arrive. */
@RunWith(RobolectricTestRunner::class)
class AppListSkeletonUsageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app = AppInfo(
        label = "Camera App",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = Color.BLUE.toDrawable()
    )
    private val loading = MainUiState(isLoading = true, apps = emptyList())
    private val loaded = MainUiState(isLoading = false, apps = listOf(app))

    private fun withHome(state: MainUiState, content: @Composable () -> Unit) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides state,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) { content() }
            }
        }
    }

    @Test
    fun `when the first sync has no apps yet, should report the apps as loading`() {
        assertTrue(loading.isAppsLoading)
        assertFalse(loaded.isAppsLoading)
        assertFalse(MainUiState(isLoading = false).isAppsLoading)
    }

    @Test
    fun `when the all apps list is loading, should show the skeleton`() {
        withHome(loading) { AllAppsList(config = listConfig()) }

        composeTestRule.onNodeWithText(LOADING).assertExists()
    }

    @Test
    fun `when the apps arrive, the all apps list should drop the skeleton`() {
        withHome(loaded) { AllAppsList(config = listConfig()) }

        composeTestRule.onNodeWithText(LOADING).assertDoesNotExist()
        composeTestRule.onNodeWithText("Camera App").assertExists()
    }

    @Test
    fun `when the drawer is loading, should show the skeleton`() {
        withHome(loading) { AppsListDrawer(listState = LazyListState()) }

        composeTestRule.onNodeWithText(LOADING).assertExists()
    }

    @Test
    fun `when the favorites picker is loading, should show the skeleton`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsDialog(
                    config = FavoriteAppsDialogConfig(
                        allApps = emptyList(),
                        favoriteAppPackages = emptyList(),
                        isLoading = true
                    ),
                    actions = FavoriteAppsDialogActions()
                )
            }
        }

        composeTestRule.onNodeWithText(LOADING).assertExists()
    }

    private fun listConfig() = FavoritesListConfig(
        currentHeightDp = 200.dp,
        currentHeightPx = 400f,
        state = LazyListState()
    )

    private companion object {
        const val LOADING = "Loading your apps…"
    }
}
