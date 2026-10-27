package dev.mnascimentos.aureole.feature.home.components.clock

import android.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClockHeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering clock header with default config, should render successfully`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader()
            }
        }

        // Assert - ClockHeader root is displayed
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun `when rendering clock header with custom greeting, should display custom greeting`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(
                    config = ClockHeaderConfig(
                        style = "CUSTOM_GREETING",
                        customGreeting = "Olá Mundo",
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Olá Mundo").assertIsDisplayed()
    }

    @Test
    fun `when clock header is clicked, should invoke onClick callback`() {
        // Arrange
        var clicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(
                    onClick = { clicked = true }
                )
            }
        }

        // Act
        composeTestRule.onNode(hasClickAction()).performClick()

        // Assert
        assertTrue(clicked)
    }

    @Test
    fun `when clock header is long clicked, should invoke onLongClick callback`() {
        // Arrange
        var longClicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader {
                    longClicked = true
                }
            }
        }

        // Act
        composeTestRule.onNode(hasClickAction()).performTouchInput {
            longClick()
        }

        // Assert
        assertTrue(longClicked)
    }

    @Test
    fun `when clock container is rendered with haze config, should display without error`() {
        // Arrange
        val uiState = MainUiState(isClockBackgroundEnabled = true, isHazeEnabled = false)

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    ClockContainer()
                }
            }
        }

        // Assert
        composeTestRule.onRoot().assertIsDisplayed()
    }

    @Test
    fun `when clock container is long clicked, should trigger onOpenEditClockBottomSheet action`() {
        // Arrange
        var editClockTriggered = false
        val actions = HomeScreenActions(
            onWidgetRowHeightChanged = {},
            onAppClick = {},
            onExpandNotificationShade = {},
            onFolderIntent = {},
            onSetAddAppToFolderDialogVisible = {},
            onSetRenameFolderDialogVisible = {},
            onSearchQueryChanged = {},
            onSettingsClick = {},
            onAllAppsDrawerClose = {},
            onAllAppsDrawerOpen = {},
            onOpenEditClockBottomSheet = { editClockTriggered = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides actions
                ) {
                    ClockContainer()
                }
            }
        }

        // Act
        composeTestRule.onNode(hasClickAction()).performTouchInput {
            longClick()
        }

        // Assert
        assertTrue(editClockTriggered)
    }

    @Test
    fun `when background and haze enabled in ClockHeader, should render correctly`() {
        // Arrange
        val hazeConfig = ClockHazeConfig(
            hazeState = null,
            isHazeEnabled = true,
            hazeOpacity = 0.8f,
            isBackgroundEnabled = true
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(
                    hazeConfig = hazeConfig,
                    config = ClockHeaderConfig(
                        backgroundColor = Color.BLUE,
                        textColor = Color.WHITE
                    )
                )
            }
        }

        // Assert
        composeTestRule.onRoot().assertIsDisplayed()
    }
}
