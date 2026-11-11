package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
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
    fun `when rendering FavoriteAppsBottomBar in MAIN mode, should display Concluido`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsBottomBar(
                    currentMode = FavoriteAppsScreenMode.MAIN,
                    onBack = {},
                    onClose = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Done").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppsBottomBar in MANAGE mode, should display Voltar`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsBottomBar(
                    currentMode = FavoriteAppsScreenMode.MANAGE,
                    onBack = {},
                    onClose = {}
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
    fun `when rendering the MAIN menu, should show the switch and the three rows with the favorites count`() {
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a", "b", "c")),
            FavoriteAppsDialogActions(),
            mutableListOf()
        )

        composeTestRule.onNodeWithText("Edit shortcuts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Manage apps and order").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 favorites, the rest stay hidden").assertIsDisplayed()
    }

    @Test
    fun `when tapping Manage apps and order, should switch to the MANAGE mode`() {
        val modes = mutableListOf<FavoriteAppsScreenMode>()
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = listOf("a")),
            FavoriteAppsDialogActions(),
            modes
        )

        composeTestRule.onNodeWithText("Manage apps and order").performClick()

        assertEquals(listOf(FavoriteAppsScreenMode.MANAGE), modes)
    }

    @Test
    fun `when tapping Edit shortcuts, should open the EDIT_SHORTCUTS sub-screen`() {
        val modes = mutableListOf<FavoriteAppsScreenMode>()
        setMainContent(
            FavoriteAppsDialogConfig(allApps = emptyList(), favoriteAppPackages = emptyList()),
            FavoriteAppsDialogActions(),
            modes
        )

        composeTestRule.onNodeWithText("Edit shortcuts").performClick()

        assertEquals(listOf(FavoriteAppsScreenMode.EDIT_SHORTCUTS), modes)
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
        composeTestRule.onNodeWithText("Show all apps on Home").assertDoesNotExist()
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
    fun `the EDIT_SHORTCUTS sub-screen shows the shortcut list and not the menu`() {
        setSubScreen(FavoriteAppsScreenMode.EDIT_SHORTCUTS)

        composeTestRule.onNodeWithText("Manage apps and order").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show all apps on Home").assertDoesNotExist()
    }

    @Test
    fun `the sub-screens go back to the menu`() {
        assertEquals(FavoriteAppsScreenMode.MAIN, FavoriteAppsScreenMode.EDIT_SHORTCUTS.backTarget())
    }

    @Test
    fun `backTarget closes from the main menu and returns to it from the manage list`() {
        assertNull(FavoriteAppsScreenMode.MAIN.backTarget())
        assertEquals(FavoriteAppsScreenMode.MAIN, FavoriteAppsScreenMode.MANAGE.backTarget())
    }
}
