package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isNotEnabled
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

        TASK_TITLES.filterKeys { it !in LOCKED_TASKS }.values
            .forEach { title -> composeTestRule.onNodeWithText(title).performScrollTo().performClick() }

        assertEquals(TASK_TITLES.keys.filter { it !in LOCKED_TASKS }, clickedTasks)
    }

    @Test
    fun `should split the steps into the home section and the extras`() {
        render()

        composeTestRule.onNodeWithText("YOUR HOME", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("EXTRAS · unlock after you build your home", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun `when the home steps are resolved, the extras should unlock and open their own task`() {
        render(completed = OnboardingTask.entries.filter { it !in LOCKED_TASKS }.toSet())

        TASK_TITLES.filterKeys { it in LOCKED_TASKS }.values
            .forEach { title -> composeTestRule.onNodeWithText(title).performScrollTo().performClick() }

        assertEquals(LOCKED_TASKS.toList(), clickedTasks)
        composeTestRule.onNodeWithText("EXTRAS", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithText("Home built. Only the extras left: do them or skip.").assertExists()
    }

    @Test
    fun `when home steps are pending, the extras should be locked, disabled and not react`() {
        render()

        composeTestRule.onNodeWithText("Adjust your layout").assertHasNoClickAction()
        composeTestRule.onNodeWithText("Check more settings").assertHasNoClickAction()
        composeTestRule.onNode(hasText("Adjust your layout") and isNotEnabled()).assertExists()
        composeTestRule.onNodeWithText("Choose your favorites").assertHasClickAction()
    }

    @Test
    fun `when a task is done, its item should still react so it can be redone`() {
        render(completed = setOf(OnboardingTask.ADD_CLOCK))

        composeTestRule.onNodeWithText("Add a clock").performScrollTo().performClick()

        assertEquals(listOf(OnboardingTask.ADD_CLOCK), clickedTasks)
    }

    @Test
    fun `when the home app is set, its row should no longer react`() {
        render(completed = setOf(OnboardingTask.SET_DEFAULT_HOME))

        composeTestRule.onNodeWithText("Set as home app").assertHasNoClickAction()
    }

    @Test
    fun `should count the resolved tasks out of eight`() {
        render(completed = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.ADD_WIDGET))

        composeTestRule.onNodeWithText("2 of 8 · takes about a minute").assertExists()
    }

    @Test
    fun `should show Skip on the next row only and skip it`() {
        render(completed = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL))

        composeTestRule.onAllNodesWithText("Skip").assertCountEquals(1)
        composeTestRule.onNodeWithText("Skip").performScrollTo().performClick()

        assertEquals(listOf(OnboardingTask.ADD_CLOCK), skippedTasks)
    }

    @Test
    fun `when the first step leads, should offer Skip on it too`() {
        render()

        composeTestRule.onAllNodesWithText("Skip").assertCountEquals(1)
        composeTestRule.onNodeWithText("Skip").performScrollTo().performClick()

        assertEquals(listOf(OnboardingTask.SET_DEFAULT_HOME), skippedTasks)
    }

    @Test
    fun `when a task is skipped, should count it, mark it skipped and still react to taps`() {
        render(
            completed = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL),
            skipped = setOf(OnboardingTask.ADD_CLOCK)
        )

        composeTestRule.onNodeWithText("3 of 8 · takes about a minute").assertExists()
        composeTestRule.onNodeWithText("– · skipped").assertExists()
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
    fun `when dismiss guide is tapped once, should ask again before dismissing`() {
        render()

        composeTestRule.onNodeWithText("Dismiss guide").performScrollTo().performClick()

        assertEquals(emptyList<String>(), calls)
        composeTestRule.onNodeWithText("Tap again to dismiss").performScrollTo().performClick()

        assertEquals(listOf("dismiss"), calls)
    }

    private companion object {
        val LOCKED_TASKS = setOf(OnboardingTask.ADJUST_LAYOUT, OnboardingTask.EXPLORE_SETTINGS)
        val TASK_TITLES = linkedMapOf(
            OnboardingTask.SET_DEFAULT_HOME to "Set as home app",
            OnboardingTask.CHOOSE_VISUAL to "Look: palette and font",
            OnboardingTask.ADD_CLOCK to "Add a clock",
            OnboardingTask.ADD_WIDGET to "Add some widgets",
            OnboardingTask.ADD_FAVORITE_APPS to "Choose your favorites",
            OnboardingTask.FILL_CONTAINER to "Fill the group",
            OnboardingTask.ADJUST_LAYOUT to "Adjust your layout",
            OnboardingTask.EXPLORE_SETTINGS to "Check more settings"
        )
    }
}
