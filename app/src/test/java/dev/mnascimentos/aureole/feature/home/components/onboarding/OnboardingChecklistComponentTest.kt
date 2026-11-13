package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingChecklistComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val clickedTasks = mutableListOf<OnboardingTask>()
    private val calls = mutableListOf<String>()
    private val skippedTasks = mutableListOf<OnboardingTask>()
    private val actions = OnboardingActions(
        onTaskClick = { clickedTasks += it },
        onSkipTask = { skippedTasks += it },
        onSetChecklistExpanded = { calls += "expanded=$it" },
        onDismissChecklist = { calls += "dismiss" }
    )

    private fun render(completed: Set<OnboardingTask> = emptySet(), skipped: Set<OnboardingTask> = emptySet()) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingChecklistComponent(
                    onboarding = OnboardingUiState(
                        stage = OnboardingStage.CHECKLIST,
                        completedTasks = completed,
                        skippedTasks = skipped
                    ),
                    actions = actions,
                    glass = OnboardingGlass()
                )
            }
        }
    }

    @Test
    fun `when each pending item is tapped, should open its own task`() {
        render()

        TASK_TITLES.values.forEach { title -> composeTestRule.onNodeWithText(title).performScrollTo().performClick() }

        assertEquals(TASK_TITLES.keys.toList(), clickedTasks)
    }

    @Test
    fun `when a task is done, its item should no longer react`() {
        render(completed = setOf(OnboardingTask.CHOOSE_FONT))

        composeTestRule.onNodeWithText("Pick a font").assertHasNoClickAction()
        composeTestRule.onNodeWithText("Pick a color palette").assertHasClickAction()
    }

    @Test
    fun `should count the done tasks out of nine`() {
        render(completed = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.ADD_WIDGET))

        composeTestRule.onNodeWithText("2 of 9 steps").assertExists()
    }

    @Test
    fun `when the next task is skippable, should show Skip on that row only and skip it`() {
        render(
            completed = setOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.CHOOSE_PALETTE,
                OnboardingTask.CHOOSE_FONT
            )
        )

        composeTestRule.onAllNodesWithText("Skip").assertCountEquals(1)
        composeTestRule.onNodeWithText("Skip").performScrollTo().performClick()

        assertEquals(listOf(OnboardingTask.ADD_CLOCK), skippedTasks)
    }

    @Test
    fun `when the next task is not skippable, should show no Skip button`() {
        render()

        composeTestRule.onAllNodesWithText("Skip").assertCountEquals(0)
    }

    @Test
    fun `when a task is skipped, should count it, let the next one lead and still react to taps`() {
        render(
            completed = setOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.CHOOSE_PALETTE,
                OnboardingTask.CHOOSE_FONT
            ),
            skipped = setOf(OnboardingTask.ADD_CLOCK)
        )

        composeTestRule.onNodeWithText("4 of 9 steps").assertExists()
        composeTestRule.onNodeWithText("Add a clock").assertHasClickAction()
        composeTestRule.onNodeWithText("Add a clock").performScrollTo().performClick()

        assertEquals(listOf(OnboardingTask.ADD_CLOCK), clickedTasks)
    }

    @Test
    fun `when minimize is tapped, should collapse to the pill`() {
        render()

        composeTestRule.onNodeWithText("Minimize").performScrollTo().performClick()

        assertEquals(listOf("expanded=false"), calls)
    }

    @Test
    fun `when dont show again is tapped, should dismiss the checklist`() {
        render()

        composeTestRule.onNodeWithText("Don't show again").performScrollTo().performClick()

        assertEquals(listOf("dismiss"), calls)
    }

    private companion object {
        val TASK_TITLES = linkedMapOf(
            OnboardingTask.SET_DEFAULT_HOME to "Set as home app",
            OnboardingTask.CHOOSE_PALETTE to "Pick a color palette",
            OnboardingTask.CHOOSE_FONT to "Pick a font",
            OnboardingTask.ADD_FAVORITE_APPS to "Choose your favorites",
            OnboardingTask.FILL_CONTAINER to "Fill the container",
            OnboardingTask.ADD_WIDGET to "Add some widgets",
            OnboardingTask.ADJUST_LAYOUT to "Adjust your layout",
            OnboardingTask.EXPLORE_SETTINGS to "Check more settings"
        )
    }
}
