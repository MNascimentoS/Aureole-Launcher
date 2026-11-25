package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val TITLE = "First steps"
private const val PILL_TEXT = "First steps · 0/8"
private const val MID_TRANSITION_MS = 80L
private const val SETTLE_MS = 3_000L
private val isRing = SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)

@RunWith(RobolectricTestRunner::class)
class OnboardingChecklistMorphComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var onboarding by mutableStateOf(
        OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = true)
    )
    private val expandedCalls = mutableListOf<Boolean>()

    private fun render(isOtherOverlayActive: Boolean = false) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingChecklistMorphComponent(
                    onboarding = onboarding,
                    isOtherOverlayActive = isOtherOverlayActive,
                    glass = OnboardingGlass(),
                    actions = OnboardingActions(onSetChecklistExpanded = { expandedCalls += it })
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    private fun switchTo(expanded: Boolean) {
        composeTestRule.mainClock.autoAdvance = false
        onboarding = onboarding.copy(isChecklistExpanded = expanded)
        Snapshot.sendApplyNotifications()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(MID_TRANSITION_MS)
    }

    private fun settle() {
        composeTestRule.mainClock.advanceTimeBy(SETTLE_MS)
        composeTestRule.mainClock.autoAdvance = true
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when minimized, the card should turn into the pill`() {
        render()
        composeTestRule.onNodeWithText(TITLE).assertExists()

        switchTo(expanded = false)
        // Mid-way both ends are composed: the card shrinking and the pill growing in its place.
        composeTestRule.onNodeWithText(PILL_TEXT).assertExists()
        settle()

        composeTestRule.onNodeWithText(TITLE).assertDoesNotExist()
        composeTestRule.onNodeWithText(PILL_TEXT).assertExists()
    }

    @Test
    fun `when the pill is tapped and the list reopens, should end with the list`() {
        onboarding = onboarding.copy(isChecklistExpanded = false)
        render()

        composeTestRule.onNodeWithText(PILL_TEXT).performClick()
        assertEquals(listOf(true), expandedCalls)

        switchTo(expanded = true)
        composeTestRule.onNodeWithText(TITLE).assertExists()
        settle()

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
        composeTestRule.onNodeWithText(TITLE).assertExists()
    }

    @Test
    fun `when the home peeks, should show the ring bubble with nothing to tap, then open the list`() {
        onboarding = onboarding.copy(isChecklistExpanded = false, isHomePeeking = true)
        render()

        composeTestRule.onNode(isRing).assertExists()
        composeTestRule.onAllNodes(hasClickAction()).assertCountEquals(0)
        composeTestRule.onNodeWithText(TITLE).assertDoesNotExist()
        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()

        composeTestRule.mainClock.autoAdvance = false
        onboarding = onboarding.copy(isHomePeeking = false, isChecklistExpanded = true)
        Snapshot.sendApplyNotifications()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(MID_TRANSITION_MS)
        composeTestRule.onNodeWithText(TITLE).assertExists()
        settle()

        composeTestRule.onNodeWithText(TITLE).assertExists()
        assertEquals(emptyList<Boolean>(), expandedCalls)
    }

    @Test
    fun `when a step is tapped, should shrink the list first and open the step after`() {
        val events = mutableListOf<String>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingChecklistMorphComponent(
                    onboarding = onboarding,
                    isOtherOverlayActive = false,
                    glass = OnboardingGlass(),
                    actions = OnboardingActions(
                        onSetChecklistExpanded = { events += "expanded=$it" },
                        onTaskClick = { events += "task=$it" }
                    )
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Set as home app").performClick()
        composeTestRule.mainClock.advanceTimeBy(SETTLE_MS)
        composeTestRule.waitUntil(timeoutMillis = SETTLE_MS) { events.size == 2 }

        assertEquals(listOf("expanded=false", "task=SET_DEFAULT_HOME"), events)
    }

    @Test
    fun `when another overlay is open, should not show the pill`() {
        onboarding = onboarding.copy(isChecklistExpanded = false)
        render(isOtherOverlayActive = true)

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
        composeTestRule.onNodeWithText(TITLE).assertDoesNotExist()
    }
}
