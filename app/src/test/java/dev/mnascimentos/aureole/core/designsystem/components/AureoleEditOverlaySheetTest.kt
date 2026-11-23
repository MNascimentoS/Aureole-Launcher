package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHeaderSlots
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class AureoleEditOverlaySheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering overlay sheet body with title, should display header title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OverlaySheetBody(
                    title = "Overlay Sheet Title",
                    onSettingsClick = {},
                    bottomBarContent = null,
                    sheetContent = { Text("Sheet Body Content") }
                )
            }
        }

        composeTestRule.onNodeWithText("Overlay Sheet Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sheet Body Content").assertIsDisplayed()
    }

    @Test
    fun `when rendering overlay sheet body with bottom bar, should display sheet content and bottom bar`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OverlaySheetBody(
                    title = "Sheet with Bottom Bar",
                    onSettingsClick = {},
                    bottomBarContent = { Text("Bottom Bar Item") },
                    sheetContent = { Text("Main Body Item") }
                )
            }
        }

        composeTestRule.onNodeWithText("Sheet with Bottom Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Main Body Item").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bottom Bar Item").assertIsDisplayed()
    }

    private fun renderHeader(
        onSettingsClick: (() -> Unit)?,
        decorations: OverlaySheetDecorations = OverlaySheetDecorations(),
        title: String = "Customize"
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box(modifier = Modifier.width(SHEET_WIDTH)) {
                    OverlaySheetBody(
                        title = title,
                        onSettingsClick = onSettingsClick,
                        bottomBarContent = null,
                        sheetContent = null,
                        decorations = decorations
                    )
                }
            }
        }
    }

    @Test
    fun `when there is a settings click, the logo should sit on the title line and open the settings`() {
        var opened = 0
        renderHeader(onSettingsClick = { opened++ })

        val logo = composeTestRule.onNodeWithContentDescription("Aureole settings").getUnclippedBoundsInRoot()
        val title = composeTestRule.onNodeWithText("Customize").getUnclippedBoundsInRoot()
        // Centered on the text line (RF-05): same vertical center as the title, within half a dp.
        val logoCenter = (logo.top + logo.bottom) / 2
        val titleCenter = (title.top + title.bottom) / 2
        assertTrue(abs(logoCenter.value - titleCenter.value) <= 0.5f)
        // The title keeps the center of the sheet, with the empty slot across from the logo.
        assertTrue(abs(((title.left + title.right) / 2).value - SHEET_WIDTH.value / 2) <= 0.5f)
        assertTrue(logo.left > title.right)
        assertEquals(48f, (logo.right - logo.left).value, 0.5f)

        composeTestRule.onNodeWithContentDescription("Aureole settings").performClick()
        assertEquals(1, opened)
    }

    @Test
    fun `when left-handed, the logo should move to the start of the title line`() {
        renderHeader(onSettingsClick = {}, decorations = OverlaySheetDecorations(isLeftHanded = true))

        val logo = composeTestRule.onNodeWithContentDescription("Aureole settings").getUnclippedBoundsInRoot()
        val title = composeTestRule.onNodeWithText("Customize").getUnclippedBoundsInRoot()
        assertTrue(logo.right < title.left)
    }

    @Test
    fun `when there is no settings click, the logo should be left out`() {
        renderHeader(onSettingsClick = null)

        composeTestRule.onNodeWithText("Customize").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Aureole settings").assertDoesNotExist()
    }

    @Test
    fun `when the header action is turned off, the logo should be left out even with a click`() {
        renderHeader(onSettingsClick = {}, decorations = OverlaySheetDecorations(showHeaderAction = false))

        composeTestRule.onNodeWithContentDescription("Aureole settings").assertDoesNotExist()
    }

    @Test
    fun `when there is a subtitle and no title icon, title and subtitle should share the same center`() {
        renderHeader(
            onSettingsClick = {},
            decorations = OverlaySheetDecorations(headerSlots = OverlayHeaderSlots(subtitle = "Only for this clock")),
            title = "Text color"
        )

        val title = composeTestRule.onNodeWithText("Text color").getUnclippedBoundsInRoot()
        val subtitle = composeTestRule.onNodeWithText("Only for this clock").getUnclippedBoundsInRoot()
        // RF-C3: the shorter title centers on the subtitle instead of leaning to its start.
        assertEquals(((title.left + title.right) / 2).value, ((subtitle.left + subtitle.right) / 2).value, 0.5f)
        assertTrue(abs(((title.left + title.right) / 2).value - SHEET_WIDTH.value / 2) <= 0.5f)
        assertTrue(subtitle.top >= title.bottom)
    }

    private companion object {
        // Narrower than the 320 dp Robolectric screen, so the box really gets this width.
        val SHEET_WIDTH = 300.dp
    }
}
