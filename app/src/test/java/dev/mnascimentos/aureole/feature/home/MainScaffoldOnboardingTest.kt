package dev.mnascimentos.aureole.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainScaffoldOnboardingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun render(stage: OnboardingStage, actions: HomeScreenActions = createDefaultHomeScreenActions()) {
        val uiState = MainUiState(isLoading = false, onboarding = OnboardingUiState(stage = stage))
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    MainScaffold(appWidgetHost = null, onWidgetSelected = {})
                }
            }
        }
    }

    @Test
    fun `when the tour starts, should welcome the user above the home`() {
        render(OnboardingStage.WELCOME)

        composeTestRule.onNodeWithText("Welcome to Aureole").assertExists()
    }

    @Test
    fun `when the tour is over, should show no onboarding`() {
        render(OnboardingStage.COMPLETED)

        composeTestRule.onNodeWithText("Welcome to Aureole").assertDoesNotExist()
        composeTestRule.onNodeWithText("Swipe up").assertDoesNotExist()
    }

    @Test
    fun `when the gesture coach is shown, a swipe up should still open the apps`() {
        var drawerOpened = false
        val actions = createDefaultHomeScreenActions().copy(onAllAppsDrawerOpen = { drawerOpened = true })
        render(OnboardingStage.GESTURES, actions)
        composeTestRule.onNodeWithText("Swipe up").assertExists()

        composeTestRule.onRoot().performTouchInput { swipeUp() }

        assertTrue(drawerOpened)
    }
}
