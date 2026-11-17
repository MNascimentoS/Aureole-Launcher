package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderFormDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when typing a name, should update the anchored preview label`() {
        composeTestRule.setContent {
            AureoleLauncherTheme { FolderFormDialog(onDismiss = {}, onSave = { _, _ -> }) }
        }
        // Blank name: the preview shows the placeholder.
        composeTestRule.onAllNodesWithText("New Folder").assertCountEquals(2)

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Work")

        // The preview label, the field and the "Work" category chip.
        composeTestRule.onAllNodesWithText("Work").assertCountEquals(3)
    }

    @Test
    fun `when creating with a blank name, should save the placeholder name and the plain folder icon`() {
        var name: String? = null
        var icon: String? = "unset"
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    onDismiss = {},
                    onSave = { n, i ->
                        name = n
                        icon = i
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("Create").performClick()

        assertEquals("New Folder", name)
        assertEquals("folder", icon)
    }

    @Test
    fun `when editing, should show the folder name and the delete row with no Save button`() {
        var deleted = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = AppFolder(id = "1", name = "Tools"),
                    onDismiss = {},
                    onSave = { _, _ -> },
                    onDelete = { deleted = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        composeTestRule.onNodeWithText("Delete folder").performClick()

        assertEquals(true, deleted)
    }

    @Test
    fun `when editing and changing the name, closing should save the new name once`() {
        val saved = mutableListOf<String>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = AppFolder(id = "1", name = "Tools"),
                    onDismiss = {},
                    onSave = { name, _ -> saved += name }
                )
            }
        }

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Work tools")
        composeTestRule.closeOverlayWithBack()

        assertEquals(listOf("Work tools"), saved)
    }

    @Test
    fun `when editing without a change, closing should not save`() {
        val saved = mutableListOf<String>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = AppFolder(id = "1", name = "Tools"),
                    onDismiss = {},
                    onSave = { name, _ -> saved += name }
                )
            }
        }

        composeTestRule.closeOverlayWithBack()

        assertEquals(emptyList<String>(), saved)
    }

    @Test
    fun `when creating, should not show the delete row`() {
        composeTestRule.setContent {
            AureoleLauncherTheme { FolderFormDialog(onDismiss = {}, onSave = { _, _ -> }, onDelete = {}) }
        }

        composeTestRule.onNodeWithText("Delete folder").assertDoesNotExist()
    }
}
