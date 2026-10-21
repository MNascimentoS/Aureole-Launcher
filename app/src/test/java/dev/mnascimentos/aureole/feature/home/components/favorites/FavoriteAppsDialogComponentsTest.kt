package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppRowParams
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteAppsDialogComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLUE)
    )

    @Test
    fun `when rendering FavoriteAppsDialogHeader, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsDialogHeader()
            }
        }

        composeTestRule.onNodeWithText("Lista de Aplicativos").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppsShowAllSwitchRow, should display text and toggle switch`() {
        var toggled = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsShowAllSwitchRow(
                    showAllAppsOnHome = true,
                    onToggleShowAllAppsOnHome = { toggled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Mostrar todos os apps na Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Se desativado, exibe apenas os aplicativos favoritados").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppRowInfo, should display app label`() {
        val app = createApp("Meu App Teste", "com.example.test")
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRowInfo(app = app)
            }
        }

        composeTestRule.onNodeWithText("Meu App Teste").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppRowActionButtons with reorder allowed, should render move buttons and handle clicks`() {
        var movedUp = false
        var movedDown = false
        var toggledFav = false

        val app = createApp("App", "com.example.app")
        val params = FavoriteAppRowParams(
            app = app,
            index = 1,
            totalCount = 3,
            canReorder = true
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRowActionButtons(
                    params = params,
                    onMoveUp = { movedUp = true },
                    onMoveDown = { movedDown = true },
                    onToggleFavorite = { toggledFav = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Arrow Up").performClick()
        assertTrue(movedUp)

        composeTestRule.onNodeWithContentDescription("Arrow Down").performClick()
        assertTrue(movedDown)

        composeTestRule.onNodeWithContentDescription("Delete").performClick()
        assertTrue(toggledFav)
    }

    @Test
    fun `when rendering FavoriteAppRowActionButtons for first item, should not render move up button`() {
        val app = createApp("App", "com.example.app")
        val params = FavoriteAppRowParams(
            app = app,
            index = 0,
            totalCount = 3,
            canReorder = true
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRowActionButtons(
                    params = params,
                    onMoveUp = {},
                    onMoveDown = {},
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Arrow Up").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Arrow Down").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppRowActionButtons for last item, should not render move down button`() {
        val app = createApp("App", "com.example.app")
        val params = FavoriteAppRowParams(
            app = app,
            index = 2,
            totalCount = 3,
            canReorder = true
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRowActionButtons(
                    params = params,
                    onMoveUp = {},
                    onMoveDown = {},
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Arrow Up").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Arrow Down").assertDoesNotExist()
    }

    @Test
    fun `when rendering RemainingAppRow, should display label and respond to toggle click`() {
        var toggled = false
        val app = createApp("Outro App", "com.example.other")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                RemainingAppRow(
                    app = app,
                    onToggleFavorite = { toggled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Outro App").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Plus").performClick()
        assertTrue(toggled)
    }
}
