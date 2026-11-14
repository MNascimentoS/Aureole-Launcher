package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val favoriteApp = app(FAV_LABEL, "com.example.fav")
    private val otherApp = app(OTHER_LABEL, "com.example.other")

    private fun app(label: String, packageName: String) = AppInfo(
        label = label,
        packageName = packageName,
        componentName = ComponentName(packageName, "MainActivity"),
        icon = Color.GREEN.toDrawable()
    )

    private fun render(
        uiState: MainUiState,
        options: FavoritesListOptions,
        actions: HomeScreenActions = createDefaultHomeScreenActions()
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalLeftHanded provides uiState.isLeftHandedMode,
                    LocalHomeActions provides actions
                ) {
                    AppsList(
                        config = FavoritesListConfig(
                            currentHeightDp = 200.dp,
                            currentHeightPx = 400f,
                            state = LazyListState()
                        ),
                        options = options
                    )
                }
            }
        }
    }

    private fun listState(favorites: List<String>) = MainUiState(
        isLoading = false,
        apps = listOf(favoriteApp, otherApp),
        containerFavorites = mapOf(LIST_ID to favorites)
    )

    private fun listOptions(showAllApps: Boolean, isInsideScrollView: Boolean = false) = FavoritesListOptions(
        containerId = LIST_ID,
        showHeadersAndWidgets = false,
        isInsideScrollView = isInsideScrollView,
        showAllApps = showAllApps
    )

    @Test
    fun `when rendering the list without apps, should display empty hint in scrollable mode`() {
        val uiState = MainUiState(favoriteAppPackages = emptyList(), apps = emptyList())

        render(uiState, FavoritesListOptions(isInsideScrollView = false))

        composeTestRule.onNodeWithText("Tap to select Favorites").assertExists()
    }

    @Test
    fun `when it shows all apps with favorites, should list the favorites on top under both labels`() {
        render(listState(listOf(favoriteApp.packageName)), listOptions(showAllApps = true))

        assertFavoritesThenAllApps()
    }

    @Test
    fun `when it shows all apps with favorites inside a scroll view, should list them the same way`() {
        render(listState(listOf(favoriteApp.packageName)), listOptions(showAllApps = true, isInsideScrollView = true))

        assertFavoritesThenAllApps()
    }

    private fun assertFavoritesThenAllApps() {
        val favoritesLabel = composeTestRule.onNodeWithText("FAVORITES").getUnclippedBoundsInRoot()
        val favorite = composeTestRule.onNodeWithText(FAV_LABEL).getUnclippedBoundsInRoot()
        val allLabel = composeTestRule.onNodeWithText("ALL APPS").getUnclippedBoundsInRoot()
        val other = composeTestRule.onNodeWithText(OTHER_LABEL).getUnclippedBoundsInRoot()
        assertTrue(favoritesLabel.top < favorite.top && favorite.top < allLabel.top && allLabel.top < other.top)
        assertEquals(1, composeTestRule.onAllNodesWithText(FAV_LABEL).fetchSemanticsNodes().size)
    }

    @Test
    fun `when it shows all apps without favorites, should show only the all apps label`() {
        render(listState(emptyList()), listOptions(showAllApps = true))

        composeTestRule.onNodeWithText("FAVORITES").assertDoesNotExist()
        composeTestRule.onNodeWithText("ALL APPS").assertExists()
        composeTestRule.onNodeWithText(FAV_LABEL).assertExists()
        composeTestRule.onNodeWithText(OTHER_LABEL).assertExists()
    }

    @Test
    fun `when it shows only favorites, should list them without labels`() {
        render(listState(listOf(favoriteApp.packageName)), listOptions(showAllApps = false))

        composeTestRule.onNodeWithText(FAV_LABEL).assertExists()
        composeTestRule.onNodeWithText(OTHER_LABEL).assertDoesNotExist()
        composeTestRule.onNodeWithText("FAVORITES").assertDoesNotExist()
        composeTestRule.onNodeWithText("ALL APPS").assertDoesNotExist()
    }

    @Test
    fun `when it shows only favorites and has none, should show the hint inside a scroll view too`() {
        render(listState(emptyList()), listOptions(showAllApps = false, isInsideScrollView = true))

        composeTestRule.onNodeWithText("Tap to select Favorites").assertExists()
        composeTestRule.onNodeWithText(FAV_LABEL).assertDoesNotExist()
    }

    @Test
    fun `when favoriting an app of the all apps part, should toggle it in this list`() {
        val toggled = mutableListOf<Pair<String, String?>>()
        val actions = createDefaultHomeScreenActions().copy(
            onToggleFavoriteWithContainer = { pkg, listId -> toggled += pkg to listId }
        )
        render(listState(emptyList()), listOptions(showAllApps = true), actions)

        composeTestRule.onNodeWithText(OTHER_LABEL).performTouchInput { longClick() }
        composeTestRule.onNodeWithText("Add to favorites").performClick()

        assertEquals(listOf(otherApp.packageName to LIST_ID), toggled)
    }

    @Test
    fun `when left-handed mode is on in scrollable mode, should place the icon after the name`() {
        renderLeftHanded(isInsideScrollView = false)

        assertIconAfterLabel()
    }

    @Test
    fun `when left-handed mode is on inside a scroll view, should place the icon after the name`() {
        renderLeftHanded(isInsideScrollView = true)

        assertIconAfterLabel()
    }

    private fun renderLeftHanded(isInsideScrollView: Boolean) {
        val uiState = MainUiState(
            apps = listOf(favoriteApp),
            favoriteAppPackages = listOf(favoriteApp.packageName),
            isLeftHandedMode = true
        )
        render(uiState, FavoritesListOptions(isInsideScrollView = isInsideScrollView))
    }

    private fun assertIconAfterLabel() {
        val labelBounds = composeTestRule.onNodeWithText(FAV_LABEL, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()
        val iconBounds = composeTestRule.onNodeWithContentDescription(FAV_LABEL, useUnmergedTree = true)
            .getUnclippedBoundsInRoot()

        assertTrue(labelBounds.right > labelBounds.left)
        assertTrue(iconBounds.left >= labelBounds.right)
    }

    private companion object {
        const val FAV_LABEL = "Fav App"
        const val OTHER_LABEL = "Other App"
        const val LIST_ID = "list_1"
    }
}
