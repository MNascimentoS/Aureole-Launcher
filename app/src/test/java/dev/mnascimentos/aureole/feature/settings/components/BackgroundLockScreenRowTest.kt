package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.LockScreenOption
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The "Also use on lock screen" switch of the background sheet and the questions around it (RF-13, 15, 16, 17). */
@RunWith(RobolectricTestRunner::class)
class BackgroundLockScreenRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var option by mutableStateOf(LockScreenOption())
    private var turnedOn = 0
    private val turnedOff = mutableListOf<Boolean>()

    private fun showSheet(initial: LockScreenOption) {
        option = initial.copy(
            onTurnOn = { turnedOn++ },
            onTurnOff = { turnedOff += it }
        )
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SetBackgroundBottomSheet(
                    SetBackgroundParams(
                        currentColor = RecommendedColors[1],
                        isCustomImageSet = false,
                        onSolidColorSelected = {},
                        onCustomImageClick = {},
                        onDismissRequest = {},
                        lockScreen = option
                    )
                )
            }
        }
    }

    @Test
    fun `switch starts off and says what it does`() {
        showSheet(LockScreenOption())

        composeTestRule.onNodeWithText("Also use on lock screen").assertIsDisplayed().assertIsOff()
    }

    @Test
    fun `first time turning on asks the warning and only Use anyway turns it on`() {
        showSheet(LockScreenOption(isNoticeSeen = false))

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()
        composeTestRule.onNodeWithText("Use anyway").assertIsDisplayed()
        assertEquals(0, turnedOn)

        composeTestRule.onNodeWithText("Use anyway").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, turnedOn)
    }

    @Test
    fun `Not now leaves the switch off`() {
        showSheet(LockScreenOption(isNoticeSeen = false))

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()
        composeTestRule.onNodeWithText("Not now").performClick()
        composeTestRule.waitForIdle()

        assertEquals(0, turnedOn)
        assertTrue(turnedOff.isEmpty())
    }

    @Test
    fun `after the warning was seen, turning on is immediate`() {
        showSheet(LockScreenOption(isNoticeSeen = true))

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()

        assertEquals(1, turnedOn)
    }

    @Test
    fun `turning off asks and Keep as is leaves the lock screen alone`() {
        showSheet(LockScreenOption(checked = true, isNoticeSeen = true))
        composeTestRule.onNodeWithText("Also use on lock screen").assertIsOn()

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()
        composeTestRule.onNodeWithText("Keep as is").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf(false), turnedOff)
    }

    @Test
    fun `turning off and choosing Back to system wallpaper gives the lock screen back`() {
        showSheet(LockScreenOption(checked = true, isNoticeSeen = true))

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()
        composeTestRule.onNodeWithText("Back to system wallpaper").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf(true), turnedOff)
    }

    @Test
    fun `when the device does not allow it, the switch is off, disabled and explained`() {
        showSheet(LockScreenOption(checked = true, isAvailable = false))

        composeTestRule.onNodeWithText("Also use on lock screen").assertIsOff().assertIsNotEnabled()
        composeTestRule.onNodeWithText("Your device doesn't allow changing the wallpaper.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Also use on lock screen").performClick()

        assertEquals(0, turnedOn)
        assertFalse(turnedOff.isNotEmpty())
    }
}
