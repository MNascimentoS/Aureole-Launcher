package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoritesListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering FavoritesList without apps, should display empty hint in scrollable mode`() {
        val uiState = MainUiState(favoriteAppPackages = emptyList(), apps = emptyList())
        val config = FavoritesListConfig(
            currentHeightDp = 200.dp,
            currentHeightPx = 400f,
            state = LazyListState()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    FavoritesList(
                        config = config,
                        options = FavoritesListOptions(isInsideScrollView = false)
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Tap to select Favorites").assertExists()
    }

    @Test
    fun `when left-handed mode is on in scrollable mode, should place the icon after the name`() {
        renderFavorites(isInsideScrollView = false)

        assertIconAfterLabel()
    }

    @Test
    fun `when left-handed mode is on inside a scroll view, should place the icon after the name`() {
        renderFavorites(isInsideScrollView = true)

        assertIconAfterLabel()
    }

    private fun renderFavorites(isInsideScrollView: Boolean) {
        val app = AppInfo(
            label = APP_LABEL,
            packageName = "com.example.fav",
            componentName = ComponentName("com.example.fav", "MainActivity"),
            icon = Color.GREEN.toDrawable()
        )
        val uiState = MainUiState(
            apps = listOf(app),
            favoriteAppPackages = listOf(app.packageName),
            isLeftHandedMode = true
        )
        val config = FavoritesListConfig(
            currentHeightDp = 200.dp,
            currentHeightPx = 400f,
            state = LazyListState()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    FavoritesList(
                        config = config,
                        options = FavoritesListOptions(isInsideScrollView = isInsideScrollView)
                    )
                }
            }
        }
    }

    private fun assertIconAfterLabel() {
        val labelBounds = composeTestRule.onNodeWithText(APP_LABEL, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val iconBounds = composeTestRule.onNodeWithContentDescription(APP_LABEL, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        assertTrue(labelBounds.right > labelBounds.left)
        assertTrue(iconBounds.left >= labelBounds.right)
    }

    private companion object {
        const val APP_LABEL = "Fav App"
    }
}
