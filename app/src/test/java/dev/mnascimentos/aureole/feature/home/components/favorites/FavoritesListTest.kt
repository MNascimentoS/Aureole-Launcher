package dev.mnascimentos.aureole.feature.home.components.favorites

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
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
}
