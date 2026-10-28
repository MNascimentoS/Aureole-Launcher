package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingEditHintComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var saveCount = 0

    private fun render(isVisible: Boolean) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingEditHintComponent(isVisible = isVisible, glass = OnboardingGlass(), onSave = { saveCount++ })
            }
        }
    }

    @Test
    fun `when visible, should explain the edit mode and save on tap`() {
        render(isVisible = true)

        composeTestRule.onNodeWithText(HINT).assertExists()
        composeTestRule.onNodeWithText("Save").performClick()

        assertEquals(1, saveCount)
    }

    @Test
    fun `when hidden, should draw nothing`() {
        render(isVisible = false)

        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
    }

    private companion object {
        const val HINT = "Drag to move, pull the edge to resize."
    }
}
