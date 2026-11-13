package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReleaseNotesBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun version(name: String, isCurrent: Boolean, item: String) = ReleaseNoteVersion(
        versionName = name,
        releaseDate = "",
        isCurrent = isCurrent,
        sections = listOf(ReleaseNoteSection("Section", listOf(ReleaseNoteItem(item, "Description of $item"))))
    )

    private val versions = listOf(
        version("2.0", isCurrent = true, item = "Item two"),
        version("1.0", isCurrent = false, item = "Item one")
    )

    private fun setSheet(onDismiss: () -> Unit = {}) {
        composeTestRule.setContent {
            AureoleLauncherTheme { ReleaseNotesBottomSheet(onDismissRequest = onDismiss, allVersions = versions) }
        }
    }

    @Test
    fun `opens on the current version with chips for all versions`() {
        setSheet()

        composeTestRule.onNodeWithText("Item two").assertExists()
        composeTestRule.onNodeWithText("v2.0 (Current)").assertIsDisplayed()
        composeTestRule.onNodeWithText("v1.0").assertIsDisplayed()
    }

    @Test
    fun `tapping a chip swaps the cards`() {
        setSheet()

        composeTestRule.onNodeWithText("v1.0").performClick()

        composeTestRule.onNodeWithText("Item one").assertExists()
        composeTestRule.onNodeWithText("Description of Item one").assertExists()
    }

    @Test
    fun `understood closes the sheet`() {
        var dismissed = false
        setSheet { dismissed = true }

        composeTestRule.onNodeWithText("Got it").performClick()

        assertTrue(dismissed)
    }
}
