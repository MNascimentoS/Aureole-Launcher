package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
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
    fun `when rendering FavoriteAppsBottomBar in APPEARANCE mode, should display Voltar`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsBottomBar(
                    currentMode = FavoriteAppsScreenMode.APPEARANCE,
                    onBack = {},
                    onClose = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppsSheetContent in MAIN mode, should display options`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(
                    currentMode = FavoriteAppsScreenMode.MAIN,
                    config = FavoriteAppsDialogConfig(
                        allApps = emptyList(),
                        favoriteAppPackages = emptyList()
                    ),
                    actions = FavoriteAppsDialogActions(),
                    onEditShortcutsClick = {},
                    onAppearanceClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Edit shortcuts").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppsSheetContent in EDIT_SHORTCUTS mode, should render body`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(
                    currentMode = FavoriteAppsScreenMode.EDIT_SHORTCUTS,
                    config = FavoriteAppsDialogConfig(
                        allApps = emptyList(),
                        favoriteAppPackages = emptyList()
                    ),
                    actions = FavoriteAppsDialogActions(),
                    onEditShortcutsClick = {},
                    onAppearanceClick = {}
                )
            }
        }
    }
}
