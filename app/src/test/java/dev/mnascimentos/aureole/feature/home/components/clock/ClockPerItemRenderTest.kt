package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.content.GridClockContent
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val CLOCK_A = "clock_a"
private const val CLOCK_B = "clock_b"
private const val CLOCK_HEIGHT_DP = 200

/** Each clock of the grid is drawn, and edited, with the config of its own id (RF-12, RF-13). */
@RunWith(RobolectricTestRunner::class)
class ClockPerItemRenderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val greetingOf = { text: String -> ClockConfig(style = "CUSTOM_GREETING", customGreeting = text) }

    /** The default look (the clock fields of the state) greets with its own text. */
    private val homeState = MainUiState(
        isLoading = false,
        clockStyle = "CUSTOM_GREETING",
        clockCustomGreeting = "Default clock",
        clockConfigs = mapOf(CLOCK_A to greetingOf("Clock A"), CLOCK_B to greetingOf("Clock B"))
    )

    private fun clockItem(id: String) = LauncherItemState(id = id, type = LauncherItemType.CLOCK)

    @Composable
    private fun GridClock(id: String, uiState: MainUiState = homeState) {
        GridClockContent(
            item = clockItem(id),
            containerConfig = ContainerConfig(),
            uiState = uiState,
            modifier = Modifier.height(CLOCK_HEIGHT_DP.dp)
        )
    }

    @Test
    fun `when two clocks have their own config, should draw each one with its own`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(homeState) {
                    Column {
                        GridClock(CLOCK_A)
                        GridClock(CLOCK_B)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("Clock A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clock B").assertIsDisplayed()
        composeTestRule.onNodeWithText("Default clock").assertDoesNotExist()
    }

    @Test
    fun `when a clock has no config, should draw it with the default look`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(homeState) { GridClock("clock_without_config") }
            }
        }

        composeTestRule.onNodeWithText("Default clock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clock A").assertDoesNotExist()
    }

    @Test
    fun `when a grid clock is long pressed, should open the editor for its id`() {
        var openedFor: String? = null
        val actions = createDefaultHomeScreenActions().copy(onOpenEditClockBottomSheet = { openedFor = it })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(homeState) {
                    CompositionLocalProvider(LocalHomeActions provides actions) { GridClock(CLOCK_B) }
                }
            }
        }
        composeTestRule.onNode(hasClickAction()).performTouchInput { longClick() }

        assertEquals(CLOCK_B, openedFor)
    }

    @Test
    fun `when the editor opens for clock B, should show the config of clock B`() {
        val editing = homeState.copy(
            showEditClockBottomSheet = true,
            editingClockId = CLOCK_B,
            clockStyle = "TIME_ONLY"
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(editing) {
                    EditClockBottomSheet(
                        uiState = editing,
                        actions = createDefaultHomeScreenActions(),
                        onDismissRequest = {}
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(EDITOR_ANIMATION_MS)
        composeTestRule.waitForIdle()

        // Clock B greets with its own text, although the default look has no greeting at all.
        composeTestRule.onNodeWithText("Greeting Text").performScrollTo().assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).assert(hasText("Clock B"))
    }

    private companion object {
        const val EDITOR_ANIMATION_MS = 500L
    }
}
