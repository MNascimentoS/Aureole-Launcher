package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import dev.mnascimentos.aureole.composable.favorites.favoriteSectionItems
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteSectionParams
import dev.mnascimentos.aureole.composable.favorites.remainingSectionItems
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class FavoriteAppsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val noActions = FavoriteSectionActions(
        onMove = { _, _ -> },
        onMoveEnd = {},
        onToggleFavorite = {},
        onRemoveAll = {}
    )

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.GREEN)
    )

    @Test
    fun `with no favorites and empty search, only the header shows (the notice above says it)`() {
        val params = FavoriteSectionParams(
            filteredFavorites = emptyList(),
            searchQuery = ""
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(params = params, actions = noActions)
                }
            }
        }

        composeTestRule.onNodeWithText("Favorites · 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("No favorite app selected.").assertDoesNotExist()
    }

    @Test
    fun `with saved favorites none of which is installed, the empty message shows`() {
        val params = FavoriteSectionParams(
            filteredFavorites = emptyList(),
            searchQuery = "",
            favoritesCount = 2
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(params = params, actions = noActions)
                }
            }
        }

        composeTestRule.onNodeWithText("No favorite app selected.").assertIsDisplayed()
    }

    @Test
    fun `when favoriteSectionItems is called with empty list and search query, should display not found message`() {
        val params = FavoriteSectionParams(
            filteredFavorites = emptyList(),
            searchQuery = "busca"
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(params = params, actions = noActions)
                }
            }
        }

        composeTestRule.onNodeWithText("No favorite found.").assertIsDisplayed()
    }

    @Test
    fun `when remainingSectionItems is called with empty list and empty search, should display empty message`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    remainingSectionItems(
                        filteredRemaining = emptyList(),
                        searchQuery = "",
                        onToggleFavorite = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("All apps · 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("All apps are already in favorites.").assertIsDisplayed()
    }

    @Test
    fun `when remainingSectionItems is called with items, should display items`() {
        val app = createApp("Novo App", "com.example.novo")
        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    remainingSectionItems(
                        filteredRemaining = listOf(app),
                        searchQuery = "",
                        onToggleFavorite = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("All apps · 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Novo App").assertIsDisplayed()
    }

    @Test
    fun `when favorites are listed without search, should reorder through the move action and remove with the badge`() {
        val moves = mutableListOf<Pair<Int, Int>>()
        var moveEnds = 0
        val toggled = mutableListOf<String>()
        val apps = listOf(createApp("Uno", "com.example.uno"), createApp("Dos", "com.example.dos"))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = FavoriteSectionParams(filteredFavorites = apps, searchQuery = ""),
                        actions = FavoriteSectionActions(
                            onMove = { from, to -> moves += from to to },
                            onMoveEnd = { moveEnds++ },
                            onToggleFavorite = { toggled += it },
                            onRemoveAll = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))[0]
            .performCustomAccessibilityActionWithLabel("Move down")
        composeTestRule.onAllNodesWithContentDescription("Remove from favorites")[1].performClick()

        assertEquals(listOf(0 to 1), moves)
        assertEquals(1, moveEnds)
        assertEquals(listOf("com.example.dos"), toggled)
    }

    @Test
    fun `when favorites are searched, should show plain rows with the remove button`() {
        val toggled = mutableListOf<String>()
        val app = createApp("Uno", "com.example.uno")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = FavoriteSectionParams(filteredFavorites = listOf(app), searchQuery = "un"),
                        actions = noActions.copy(onToggleFavorite = { toggled += it })
                    )
                }
            }
        }

        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions)).assertCountEquals(0)
        composeTestRule.onNodeWithContentDescription("Remove from favorites").performClick()
        assertEquals(listOf("com.example.uno"), toggled)
    }

    private fun setSection(favorites: List<AppInfo>, searchQuery: String = "", favoritesCount: Int = favorites.size) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = FavoriteSectionParams(
                            filteredFavorites = favorites,
                            searchQuery = searchQuery,
                            favoritesCount = favoritesCount
                        ),
                        actions = noActions
                    )
                }
            }
        }
    }

    @Test
    fun `with two favorites, should show the reorder hint in the header, the numbers and one handle per row`() {
        setSection(listOf(createApp("Uno", "com.example.uno"), createApp("Dos", "com.example.dos")))

        composeTestRule.onNodeWithText("hold ⋮⋮ to reorder").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()
        composeTestRule.onAllNodesWithContentDescription("Reorder").assertCountEquals(2)
        composeTestRule.onNodeWithText("Remove all").assertIsDisplayed()
    }

    @Test
    fun `with one favorite, should not show the reorder hint`() {
        setSection(listOf(createApp("Uno", "com.example.uno")))

        composeTestRule.onNodeWithText("hold ⋮⋮ to reorder").assertDoesNotExist()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()
    }

    @Test
    fun `while searching, should not show the reorder hint nor the handles`() {
        setSection(listOf(createApp("Uno", "com.example.uno")), searchQuery = "un", favoritesCount = 3)

        composeTestRule.onNodeWithText("hold ⋮⋮ to reorder").assertDoesNotExist()
        composeTestRule.onAllNodesWithContentDescription("Reorder").assertCountEquals(0)
    }

    @Test
    fun `without favorites, should hide Remove all and the reorder hint`() {
        setSection(emptyList())

        composeTestRule.onNodeWithText("Favorites · 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("hold ⋮⋮ to reorder").assertDoesNotExist()
        composeTestRule.onNodeWithText("Remove all").assertDoesNotExist()
    }

    @Test
    fun `when Remove all is tapped, should report it`() {
        var removed = 0
        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = FavoriteSectionParams(
                            filteredFavorites = listOf(createApp("Uno", "com.example.uno")),
                            searchQuery = ""
                        ),
                        actions = noActions.copy(onRemoveAll = { removed++ })
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Remove all").performClick()

        assertEquals(1, removed)
    }
}
