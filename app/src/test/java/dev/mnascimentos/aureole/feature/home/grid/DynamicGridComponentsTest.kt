package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.junit4.createComposeRule
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicGridComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering background overlay without drag target, should not crash`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridBackgroundOverlay(
                    cellWidthPx = 100f,
                    cellHeightPx = 100f,
                    activeDragTarget = null
                )
            }
        }
    }

    @Test
    fun `when rendering background overlay with valid drag target, should not crash`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridBackgroundOverlay(
                    cellWidthPx = 100f,
                    cellHeightPx = 100f,
                    activeDragTarget = DragTargetSlot(
                        itemId = "test",
                        col = 0,
                        row = 0,
                        colSpan = 2,
                        rowSpan = 2,
                        isValid = true
                    )
                )
            }
        }
    }

    @Test
    fun `when rendering background overlay with invalid drag target, should not crash`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridBackgroundOverlay(
                    cellWidthPx = 100f,
                    cellHeightPx = 100f,
                    activeDragTarget = DragTargetSlot(
                        itemId = "test",
                        col = 0,
                        row = 0,
                        colSpan = 2,
                        rowSpan = 2,
                        isValid = false
                    )
                )
            }
        }
    }
}
