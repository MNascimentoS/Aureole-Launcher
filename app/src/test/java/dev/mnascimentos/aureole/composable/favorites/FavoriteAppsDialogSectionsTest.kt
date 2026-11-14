package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteAppsDialogSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setSheet(
        config: FavoriteAppsDialogConfig,
        actions: FavoriteAppsDialogActions = FavoriteAppsDialogActions()
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(config = config, actions = actions)
            }
        }
    }

    private fun homeList(
        favorites: List<String>,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit = {}
    ) = FavoriteAppsDialogConfig(
        allApps = emptyList(),
        favoriteAppPackages = favorites,
        showAllApps = ShowAllAppsParams(checked = checked, onCheckedChange = onCheckedChange)
    )

    @Test
    fun `when rendering FavoriteAppsBottomBar, should show the search and no Back button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsBottomBar()
            }
        }

        composeTestRule.onNodeWithText("Back").assertDoesNotExist()
    }

    @Test
    fun `the single screen has no menu row and no switch without the list's switch params`() {
        setSheet(FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a", "b", "c")))

        composeTestRule.onNodeWithText("Show all apps").assertDoesNotExist()
        composeTestRule.onNodeWithText("Customize home").assertDoesNotExist()
        composeTestRule.onNodeWithText("Edit shortcuts").assertDoesNotExist()
    }

    @Test
    fun `a home apps list shows the switch on top with the on-top subtitle while it is on`() {
        setSheet(homeList(favorites = listOf("a", "b"), checked = true))

        composeTestRule.onNodeWithText("Show all apps").assertIsDisplayed().assertIsOn()
        composeTestRule.onNodeWithText("2 favorites on top, then all apps").assertIsDisplayed()
    }

    @Test
    fun `tapping the switch reports the new value at once`() {
        val values = mutableListOf<Boolean>()
        setSheet(homeList(favorites = emptyList(), checked = true, onCheckedChange = { values += it }))

        composeTestRule.onNodeWithText("Show all apps").performClick()

        assertEquals(listOf(false), values)
    }

    @Test
    fun `with the switch off and no favorites, the subtitle says none is selected`() {
        setSheet(homeList(favorites = emptyList(), checked = false))

        composeTestRule.onNodeWithText("Show all apps").assertIsOff()
        // The switch subtitle and the empty list message use the same text.
        composeTestRule.onAllNodesWithText("No favorite app selected.", substring = true).assertCountEquals(2)
    }

    @Test
    fun `with the switch on and no favorites, the subtitle says the Home shows all apps`() {
        setSheet(homeList(favorites = emptyList(), checked = true))

        composeTestRule.onNodeWithText("No favorites: shows all apps").assertIsDisplayed()
    }

    @Test
    fun `with the switch off and favorites, the subtitle uses the plural and singular count`() {
        setSheet(homeList(favorites = listOf("a"), checked = false))

        composeTestRule.onNodeWithText("1 favorite, the rest stays hidden").assertIsDisplayed()
    }

    @Test
    fun `Customize home shows below the switch and opens the customization`() {
        var opened = 0
        setSheet(
            homeList(favorites = listOf("a"), checked = true),
            FavoriteAppsDialogActions(onCustomizeHome = { opened++ })
        )

        composeTestRule.onNodeWithText("Customize home").assertIsDisplayed().performClick()

        assertEquals(1, opened)
    }

    @Test
    fun `Customize home is left out when the sheet has no action for it`() {
        setSheet(homeList(favorites = listOf("a"), checked = true))

        composeTestRule.onNodeWithText("Customize home").assertDoesNotExist()
    }
}
