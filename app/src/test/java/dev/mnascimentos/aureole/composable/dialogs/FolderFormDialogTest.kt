package dev.mnascimentos.aureole.composable.dialogs

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import dev.mnascimentos.aureole.composable.dialogs.model.FOLDER_EMOJIS
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val TALL_WINDOW = "w400dp-h900dp"

/** A choice tile: selectable and without text of its own (icon tiles). */
private val iconTile = SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected) and
    SemanticsMatcher.keyNotDefined(SemanticsProperties.Text)

/** The tile of [emoji] in the grid (the preview tile shows the marked emoji too, without being selectable). */
private fun emojiTile(emoji: String) = hasText(emoji) and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)

/** Pressed through the semantics: on the small test window a touch can land on the scrim and close the sheet. */
private fun SemanticsNodeInteraction.tap() = performSemanticsAction(SemanticsActions.OnClick)

@RunWith(RobolectricTestRunner::class)
class FolderFormDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setForm(
        folder: AppFolder? = null,
        onDelete: (() -> Unit)? = null,
        pickApps: Boolean = true,
        onSave: (FolderDetails) -> Unit = {}
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = folder,
                    onDismiss = {},
                    onSave = onSave,
                    onDelete = onDelete,
                    pickAppsOnCreate = pickApps
                )
            }
        }
    }

    @Test
    fun `a new folder shows the empty name pill and the suggestions follow what is typed`() {
        setForm()

        composeTestRule.onNodeWithText("New Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name it").assertIsDisplayed()
        composeTestRule.onNodeWithText("SUGGESTED").assertExists()

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Banco")

        composeTestRule.onNodeWithText("SUGGESTED FOR “BANCO”").assertExists()
    }

    @Test
    fun `creating with a blank name saves the placeholder and the first suggestion`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals(FolderDetails(name = "New Folder", icon = "folder"), saved)
    }

    @Test
    fun `creating a named folder saves the icon suggested for the name`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Bank")
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals(FolderDetails(name = "Bank", icon = "account_balance"), saved)
    }

    @Test
    fun `picking another suggestion saves it`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Bank")
        composeTestRule.onAllNodes(iconTile)[2].tap()
        composeTestRule.onAllNodes(iconTile)[2].assertIsSelected()
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals("credit_card", saved?.icon)
    }

    @Test
    fun `without the app picker the button is just Create`() {
        setForm(pickApps = false)

        composeTestRule.onNodeWithText("Create").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create and pick apps").assertDoesNotExist()
    }

    @Test
    fun `Letter saves a letter folder`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("Letter").tap()
        composeTestRule.onNodeWithText("The first letter of the name, in the theme font. It changes with the name.")
            .assertExists()
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals(FolderIconType.LETTER, saved?.iconType)
    }

    @Test
    fun `Emoji marks the first common emoji and saves the one picked`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("Emoji").tap()
        composeTestRule.onNode(emojiTile(FOLDER_EMOJIS.first())).assertIsSelected()
        composeTestRule.onNode(emojiTile(FOLDER_EMOJIS[4])).tap()
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals(FolderIconType.EMOJI, saved?.iconType)
        assertEquals(FOLDER_EMOJIS[4], saved?.emoji)
    }

    @Test
    fun `the plus tile takes any emoji typed on the keyboard`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("Emoji").tap()
        composeTestRule.onNodeWithContentDescription("Another emoji").tap()
        composeTestRule.onNodeWithContentDescription("Type an emoji").performTextInput("🦄")
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals("🦄", saved?.emoji)
    }

    @Config(qualifiers = TALL_WINDOW)
    @Test
    fun `All icons swaps the content for the grid and picking one goes back to the form`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("All icons").tap()
        composeTestRule.onNodeWithText("Back to the folder").assertExists()
        composeTestRule.onAllNodesWithText("All icons").fetchSemanticsNodes().let { assertEquals(1, it.size) }

        // The grid opens on the category of the marked icon (the folder, in Tools): its first tile is the first
        // icon of that category.
        val firstOfCategory = AureoleIconCatalog.byCategory(checkNotNull(AureoleIconCatalog.categoryOf("folder")))
            .first().key
        composeTestRule.onAllNodes(iconTile)[0].tap()
        composeTestRule.onNodeWithText("Back to the folder").assertDoesNotExist()
        composeTestRule.onNodeWithText("Create and pick apps").tap()

        assertEquals(firstOfCategory, saved?.icon)
        assertEquals(FolderIconType.ICON, saved?.iconType)
    }

    @Test
    fun `back from All icons returns to the form without closing`() {
        var saved: FolderDetails? = null
        setForm(onSave = { saved = it })

        composeTestRule.onNodeWithText("All icons").tap()
        composeTestRule.closeOverlayWithBack()

        composeTestRule.onNodeWithText("New Folder").assertIsDisplayed()
        assertNull(saved)
    }

    @Test
    fun `editing shows the name, no Save button and Delete folder at the end`() {
        var deleted = false
        setForm(folder = AppFolder(id = "1", name = "Tools", icon = "build"), onDelete = { deleted = true })

        composeTestRule.onNodeWithText("Edit Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tools").assertExists()
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        composeTestRule.onNodeWithText("Create and pick apps").assertDoesNotExist()
        composeTestRule.onNodeWithText("Delete folder").tap()

        assertEquals(true, deleted)
    }

    @Test
    fun `editing and changing the name saves the new details once on close`() {
        val saved = mutableListOf<FolderDetails>()
        val color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
        setForm(folder = AppFolder(id = "1", name = "Tools", icon = "build", color = color), onSave = { saved += it })

        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Work tools")
        composeTestRule.closeOverlayWithBack()

        assertEquals(listOf(FolderDetails(name = "Work tools", icon = "build", color = color)), saved)
    }

    @Test
    fun `editing without a change does not save, even for a folder without icon`() {
        val saved = mutableListOf<FolderDetails>()
        setForm(folder = AppFolder(id = "1", name = "Tools"), onSave = { saved += it })

        composeTestRule.closeOverlayWithBack()

        assertEquals(emptyList<FolderDetails>(), saved)
    }

    @Test
    fun `creating does not show the delete row`() {
        setForm(onDelete = {})

        composeTestRule.onNodeWithText("Delete folder").assertDoesNotExist()
    }
}
