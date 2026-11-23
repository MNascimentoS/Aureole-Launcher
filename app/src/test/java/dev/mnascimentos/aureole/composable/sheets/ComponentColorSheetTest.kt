package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.paletteNameOf
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.frostbitePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ComponentColorSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val picked = mutableListOf<ThemeColorRef?>()
    private var dismissCount = 0

    private fun render(selected: ThemeColorRef?, role: ComponentColorRole = ComponentColorRole.TEXT) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickPaletteBottomSheet(
                    color = ComponentColorOptions(
                        role = role,
                        title = "Text color",
                        subtitle = "Only for this clock",
                        selected = selected,
                        themeColor = Color.White,
                        onSelect = { picked += it }
                    ),
                    onDismissRequest = { dismissCount++ }
                )
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `shows the component header and marks Theme when nothing is saved`() {
        render(selected = null)

        composeTestRule.onNodeWithText("Only for this clock").assertIsDisplayed()
        composeTestRule.onNodeWithText("Theme").assertIsSelected()
        composeTestRule.onNodeWithText("Current").assertDoesNotExist()
    }

    @Test
    fun `an old fixed color in no palette shows as Current, marked`() {
        render(selected = ThemeColorRef.Fixed(OLD_ARGB))

        composeTestRule.onNodeWithText("Current").assertIsSelected()
    }

    @Test
    fun `tapping a palette saves its role tone at once and closes the sheet`() {
        render(selected = null, role = ComponentColorRole.CARD)

        composeTestRule.onNodeWithText(frostbitePalette.name).performClick()
        composeTestRule.mainClock.advanceTimeBy(CLOSE_MS)
        composeTestRule.waitForIdle()

        // The tone of the variant shown (dark or light, as the system), which marks Frostbite again.
        assertEquals(1, picked.size)
        assertEquals(frostbitePalette.name, paletteNameOf(picked.single(), ComponentColorRole.CARD))
        assertEquals(1, dismissCount)
    }

    @Test
    fun `tapping Theme saves null`() {
        render(selected = ThemeColorRef.Fixed(OLD_ARGB))

        composeTestRule.onNodeWithText("Theme").performClick()
        composeTestRule.waitForIdle()

        assertEquals(listOf<ThemeColorRef?>(null), picked)
    }

    private companion object {
        const val CLOSE_MS = 1_000L
        const val OLD_ARGB = 0xFF123457.toInt()
    }
}
