package dev.mnascimentos.aureole.composable.minihome

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleMiniHomeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setMiniHome(isLeftHanded: Boolean) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleMiniHome(content = sampleMiniHomeContent(), isLeftHanded = isLeftHanded)
            }
        }
    }

    @Test
    fun `should show the clock, the date and four apps`() {
        setMiniHome(isLeftHanded = false)

        composeTestRule.onAllNodesWithText("09:41").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("Tuesday, November 10").assertCountEquals(1)
        listOf("Phone", "Messages", "Camera", "Browser").forEach {
            composeTestRule.onAllNodesWithText(it).assertCountEquals(1)
        }
    }

    @Test
    fun `right-handed should keep the apps on the left, away from the thumb`() {
        setMiniHome(isLeftHanded = false)

        val phone = composeTestRule.onNodeWithText("Phone").getUnclippedBoundsInRoot()
        val clock = composeTestRule.onNodeWithText("09:41").getUnclippedBoundsInRoot()
        assertTrue(phone.left < clock.left)
    }

    @Test
    fun `left-handed should keep the apps on the right, away from the thumb`() {
        setMiniHome(isLeftHanded = true)

        val phone = composeTestRule.onNodeWithText("Phone").getUnclippedBoundsInRoot()
        val clock = composeTestRule.onNodeWithText("09:41").getUnclippedBoundsInRoot()
        assertTrue(phone.right > clock.right + 0.dp)
    }
}
