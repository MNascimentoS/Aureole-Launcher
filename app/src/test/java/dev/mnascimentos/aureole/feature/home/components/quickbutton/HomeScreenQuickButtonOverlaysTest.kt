package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenQuickButtonOverlaysTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val defaultActions: HomeScreenActions = createDefaultHomeScreenActions()

    @Test
    fun `when all overlays are hidden, should render empty container without overlays`() {
        // Arrange
        val uiState = MainUiState(
            showQuickButtonSettingsBottomSheet = false,
            showQuickButtonAppearanceBottomSheet = false,
            showShortcutSelectionBottomSheet = false
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeScreenQuickButtonOverlays(
                    uiState = uiState,
                    actions = defaultActions,
                    hazeState = null
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button settings").assertDoesNotExist()
        composeTestRule.onNodeWithText("Quick button appearance").assertDoesNotExist()
        composeTestRule.onNodeWithText("Select a shortcut").assertDoesNotExist()
    }

    @Test
    fun `when settings sheet flag is true, should display quick button settings title`() {
        // Arrange
        val uiState = MainUiState(showQuickButtonSettingsBottomSheet = true)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeScreenQuickButtonOverlays(
                    uiState = uiState,
                    actions = defaultActions,
                    hazeState = null
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button settings").assertIsDisplayed()
    }

    @Test
    fun `when appearance sheet flag is true, should display quick button appearance title`() {
        // Arrange
        val uiState = MainUiState(showQuickButtonAppearanceBottomSheet = true)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeScreenQuickButtonOverlays(
                    uiState = uiState,
                    actions = defaultActions,
                    hazeState = null
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button appearance").assertIsDisplayed()
    }

    @Test
    fun `when shortcut selection flag is true, should display shortcut selection title`() {
        // Arrange
        val uiState = MainUiState(showShortcutSelectionBottomSheet = true)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeScreenQuickButtonOverlays(
                    uiState = uiState,
                    actions = defaultActions,
                    hazeState = null
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Select a shortcut").assertIsDisplayed()
    }
}
