package dev.mnascimentos.aureole.feature.home.components.clock

import android.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClockHeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun hasLongClickAction() = SemanticsMatcher.keyIsDefined(SemanticsActions.OnLongClick)

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
    fun `when clock header has a long press, should offer only it, with no button role or tap action`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(onLongClick = {})
            }
        }

        // Assert
        val node = composeTestRule.onNode(hasLongClickAction()).fetchSemanticsNode()
        assertEquals("Customize clock", node.config[SemanticsActions.OnLongClick].label)
        assertFalse(node.config.contains(SemanticsProperties.Role))
        composeTestRule.onAllNodes(hasClickAction()).assertCountEquals(0)
    }

    @Test
    fun `when clock header has no long press, should have no gesture at all`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader()
            }
        }

        // Assert
        composeTestRule.onAllNodes(hasLongClickAction()).assertCountEquals(0)
        composeTestRule.onAllNodes(hasClickAction()).assertCountEquals(0)
    }

    @Test
    fun `when clock header long press action runs from accessibility, should invoke onLongClick callback`() {
        // Arrange
        var longClicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(onLongClick = { longClicked = true })
            }
        }

        // Act
        composeTestRule.onNode(hasLongClickAction()).performSemanticsAction(SemanticsActions.OnLongClick)

        // Assert
        assertTrue(longClicked)
    }

    @Test
    fun `when clock header is long clicked, should invoke onLongClick callback`() {
        // Arrange
        var longClicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                HomeClockHeader(onLongClick = {
                    longClicked = true
                })
            }
        }

        // Act
        composeTestRule.onNode(hasLongClickAction()).performTouchInput {
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
                ProvideHomeUiState(uiState) {
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
                    LocalLeftHanded provides (MainUiState()).isLeftHandedMode,
                    LocalHomeActions provides actions
                ) {
                    ClockContainer()
                }
            }
        }

        // Act
        composeTestRule.onNode(hasLongClickAction()).performTouchInput {
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
