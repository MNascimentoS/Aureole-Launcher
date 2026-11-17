package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.ShowLabelsParams
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
        onCheckedChange: (Boolean) -> Unit = {},
        labels: Boolean = true,
        onLabelsChange: (Boolean) -> Unit = {}
    ) = FavoriteAppsDialogConfig(
        allApps = emptyList(),
        favoriteAppPackages = favorites,
        showAllApps = ShowAllAppsParams(checked = checked, onCheckedChange = onCheckedChange),
        showLabels = ShowLabelsParams(checked = labels, onCheckedChange = onLabelsChange)
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
    fun `a home apps list shows both options as chips on one line`() {
        setSheet(homeList(favorites = listOf("a", "b"), checked = true, labels = true))

        composeTestRule.onNodeWithText("Show all apps").assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("Labels").assertIsDisplayed().assertIsSelected().assertIsEnabled()
    }

    @Test
    fun `tapping Show all apps reports the new value at once`() {
        val values = mutableListOf<Boolean>()
        setSheet(homeList(favorites = emptyList(), checked = true, onCheckedChange = { values += it }))

        composeTestRule.onNodeWithText("Show all apps").performClick()

        assertEquals(listOf(false), values)
    }

    @Test
    fun `tapping Labels reports the new value while all apps are shown`() {
        val values = mutableListOf<Boolean>()
        setSheet(homeList(favorites = emptyList(), checked = true, labels = true, onLabelsChange = { values += it }))

        composeTestRule.onNodeWithText("Labels").performClick()

        assertEquals(listOf(false), values)
    }

    @Test
    fun `Labels is disabled with Show all apps off`() {
        setSheet(homeList(favorites = listOf("a"), checked = false, labels = true))

        composeTestRule.onNodeWithText("Show all apps").assertIsNotSelected()
        composeTestRule.onNodeWithText("Labels").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun `without favorites and Show all apps on, the notice says the Home shows all apps`() {
        setSheet(homeList(favorites = emptyList(), checked = true))

        composeTestRule.onNodeWithText("No favorites: the Home shows all 0 apps.").assertIsDisplayed()
    }

    @Test
    fun `without favorites and Show all apps off, the notice says the list stays empty`() {
        setSheet(homeList(favorites = emptyList(), checked = false))

        composeTestRule.onNodeWithText("No favorites: the list stays empty on the Home.").assertIsDisplayed()
        composeTestRule.onNodeWithText("No favorites: the Home shows all 0 apps.").assertDoesNotExist()
    }

    @Test
    fun `with favorites there is no notice`() {
        setSheet(homeList(favorites = listOf("a"), checked = false))

        composeTestRule.onNodeWithText("No favorites", substring = true).assertDoesNotExist()
    }

    @Test
    fun `the Settings sheet has no chips and keeps the notice of all apps`() {
        setSheet(FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = emptyList()))

        composeTestRule.onNodeWithText("Labels").assertDoesNotExist()
        composeTestRule.onNodeWithText("No favorites: the Home shows all 0 apps.").assertIsDisplayed()
    }

    @Test
    fun `Customize home is not a row of the list anymore`() {
        setSheet(
            homeList(favorites = listOf("a"), checked = true),
            FavoriteAppsDialogActions(onCustomizeHome = {})
        )

        composeTestRule.onNodeWithText("Customize home").assertDoesNotExist()
    }

    @Test
    fun `both sections show their headers with the count`() {
        setSheet(homeList(favorites = emptyList(), checked = true))

        composeTestRule.onNodeWithText("Favorites · 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("All apps · 0").assertIsDisplayed()
    }
}
