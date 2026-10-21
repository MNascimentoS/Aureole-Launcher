package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteSectionParams
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteAppsListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.GREEN)
    )

    @Test
    fun `when favoriteSectionItems is called with empty list and empty search, should display empty message`() {
        val params = FavoriteSectionParams(
            filteredFavorites = emptyList(),
            searchQuery = "",
            favoriteCount = 0
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = params,
                        onMoveUp = {},
                        onMoveDown = {},
                        onToggleFavorite = {},
                        onDeactivateAll = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Favoritos (0)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nenhum aplicativo favorito selecionado.").assertIsDisplayed()
    }

    @Test
    fun `when favoriteSectionItems is called with empty list and search query, should display not found message`() {
        val params = FavoriteSectionParams(
            filteredFavorites = emptyList(),
            searchQuery = "busca",
            favoriteCount = 0
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    favoriteSectionItems(
                        params = params,
                        onMoveUp = {},
                        onMoveDown = {},
                        onToggleFavorite = {},
                        onDeactivateAll = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Nenhum favorito encontrado.").assertIsDisplayed()
    }

    @Test
    fun `when remainingSectionItems is called with empty list and empty search, should display empty message`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                LazyColumn {
                    remainingSectionItems(
                        filteredRemaining = emptyList(),
                        searchQuery = "",
                        onToggleFavorite = {},
                        onActivateAll = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Outros Aplicativos (0)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Todos os aplicativos já estão nos favoritos.").assertIsDisplayed()
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
                        onToggleFavorite = {},
                        onActivateAll = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Outros Aplicativos (1)").assertIsDisplayed()
        composeTestRule.onNodeWithText("Novo App").assertIsDisplayed()
    }
}
