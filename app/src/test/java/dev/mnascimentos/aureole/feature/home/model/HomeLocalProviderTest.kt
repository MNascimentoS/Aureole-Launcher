package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.test.junit4.createComposeRule
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeLocalProviderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when the state is left handed, should feed the design system hand mode`() {
        // Arrange
        var leftHanded = false
        var state: MainUiState? = null

        // Act
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState(isLeftHandedMode = true)) {
                leftHanded = LocalLeftHanded.current
                state = LocalHomeUiState.current
            }
        }
        composeTestRule.waitForIdle()

        // Assert
        assertTrue(leftHanded)
        assertEquals(true, state?.isLeftHandedMode)
    }

    @Test
    fun `when the state is right handed, should keep the design system hand mode off`() {
        // Arrange
        var leftHanded = true

        // Act
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                leftHanded = LocalLeftHanded.current
            }
        }
        composeTestRule.waitForIdle()

        // Assert
        assertFalse(leftHanded)
    }
}
