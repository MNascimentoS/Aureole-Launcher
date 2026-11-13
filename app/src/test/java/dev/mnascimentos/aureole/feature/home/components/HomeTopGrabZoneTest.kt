package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeTopGrabZoneTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when long pressing the top zone, should open customize`() {
        var opened = 0
        composeTestRule.setContent {
            AureoleLauncherTheme { HomeTopGrabZone(enabled = true, onLongPress = { opened++ }) }
        }

        composeTestRule.onNodeWithTag(HOME_TOP_GRAB_ZONE_TAG).performTouchInput { longClick() }

        assertEquals(1, opened)
    }

    @Test
    fun `when in edit mode, should not draw the top zone`() {
        composeTestRule.setContent {
            AureoleLauncherTheme { HomeTopGrabZone(enabled = false, onLongPress = {}) }
        }

        composeTestRule.onNodeWithTag(HOME_TOP_GRAB_ZONE_TAG).assertDoesNotExist()
    }
}
