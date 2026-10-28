package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleHaloProgressTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun render(progress: Float, description: String? = null) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleHaloProgress(progress = progress, contentDescription = description)
            }
        }
    }

    @Test
    fun `when rendered, should expose its progress and label`() {
        render(progress = 0.5f, description = "First steps")

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0.5f, 0f..1f))).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("First steps")).assertIsDisplayed()
    }

    @Test
    fun `when progress overflows, should clamp it to full`() {
        render(progress = 2f)

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(1f, 0f..1f))).assertExists()
    }

    @Test
    fun `when progress is negative, should clamp it to empty`() {
        render(progress = -1f)

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(0f, 0f..1f))).assertExists()
    }
}
