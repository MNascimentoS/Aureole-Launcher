package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.favorites.model.ShowAllAppsParams
import dev.mnascimentos.aureole.composable.favorites.model.backTarget
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteAppsDialogSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering FavoriteAppsBottomBar, should display Back`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsBottomBar(
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    private fun setMainContent(
        config: FavoriteAppsDialogConfig,
        actions: FavoriteAppsDialogActions,
        modes: MutableList<FavoriteAppsScreenMode>
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(
                    currentMode = FavoriteAppsScreenMode.MAIN,
                    config = config,
                    actions = actions,
                    onModeChange = { modes += it }
                )
            }
        }
    }

    @Test
    fun `when rendering the MAIN menu without the switch, should show Choose favorites with the favorites count`() {
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a", "b", "c")),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("Choose favorites").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 favorites, the rest stay hidden").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edit shortcuts").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show all apps").assertDoesNotExist()
    }

    @Test
    fun `when tapping Choose favorites, should switch to the MANAGE mode`() {
        val modes = mutableListOf<FavoriteAppsScreenMode>()
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a")),
            FavoriteAppsDialogActions(),
            modes
        )

        composeTestRule.onNodeWithText("Choose favorites").performClick()

        assertEquals(listOf(FavoriteAppsScreenMode.MANAGE), modes)
    }

    @Test
    fun `a home apps list shows the switch below Choose favorites and the on-top subtitle while it is on`() {
        setMainContent(
            FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = listOf("a", "b"),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = {})
            ),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("Show all apps").assertIsDisplayed().assertIsOn()
        composeTestRule.onNodeWithText("Favorites stay at the top").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 favorites on top, then all apps").assertIsDisplayed()
    }

    @Test
    fun `tapping the switch reports the new value at once`() {
        val values = mutableListOf<Boolean>()
        setMainContent(
            FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList(),
                showAllApps = ShowAllAppsParams(checked = true, onCheckedChange = { values += it })
            ),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("Show all apps").performClick()

        assertEquals(listOf(false), values)
    }

    @Test
    fun `with the switch off and no favorites, the subtitle says none is selected`() {
        setMainContent(
            FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList(),
                showAllApps = ShowAllAppsParams(checked = false, onCheckedChange = {})
            ),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("Show all apps").assertIsOff()
        composeTestRule.onNodeWithText("No favorite app selected.").assertIsDisplayed()
    }

    private fun setSubScreen(mode: FavoriteAppsScreenMode) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(
                    currentMode = mode,
                    config = FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = emptyList()),
                    actions = FavoriteAppsDialogActions(),
                    onModeChange = {}
                )
            }
        }
    }

    @Test
    fun `the main menu with no favorites says the Home shows all apps and has no switch nor Appearance row`() {
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = emptyList()),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("No favorites: shows all apps").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show all apps").assertDoesNotExist()
        composeTestRule.onNodeWithText("Appearance").assertDoesNotExist()
    }

    @Test
    fun `the main menu with one favorite uses the singular subtitle`() {
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a")),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("1 favorite, the rest stays hidden").assertIsDisplayed()
    }

    @Test
    fun `the MANAGE sub-screen shows the list and not the menu`() {
        setSubScreen(FavoriteAppsScreenMode.MANAGE)

        composeTestRule.onNodeWithText("Choose favorites").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show all apps").assertDoesNotExist()
    }

    @Test
    fun `backTarget closes from the main menu and returns to it from the manage list`() {
        assertNull(FavoriteAppsScreenMode.MAIN.backTarget())
        assertEquals(FavoriteAppsScreenMode.MAIN, FavoriteAppsScreenMode.MANAGE.backTarget())
    }

    @Test
    fun `opened straight on the manage list, back closes the sheet instead of showing the menu`() {
        assertNull(FavoriteAppsScreenMode.MANAGE.backTarget(entryMode = FavoriteAppsScreenMode.MANAGE))
        assertNull(FavoriteAppsScreenMode.MAIN.backTarget(entryMode = FavoriteAppsScreenMode.MANAGE))
    }
}
