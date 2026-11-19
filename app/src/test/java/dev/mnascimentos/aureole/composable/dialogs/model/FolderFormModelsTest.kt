package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderFormModelsTest {

    private val variant = ThemeColorRef.Role(ThemeColorRole.VARIANT)

    @Test
    fun `a blank name becomes the placeholder and a typed one is trimmed`() {
        assertEquals("New Folder", resolveFolderName("", "New Folder"))
        assertEquals("New Folder", resolveFolderName("   ", "New Folder"))
        assertEquals("Work", resolveFolderName("  Work ", "New Folder"))
    }

    @Test
    fun `a new folder opens empty, as an icon that follows the suggestions`() {
        val draft = initialFolderDraft(null)

        assertEquals(FolderFormDraft(name = ""), draft)
        assertEquals(folderIconSuggestions("").first(), draft.effectiveIcon())
    }

    @Test
    fun `editing opens with the details of the folder`() {
        val folder = AppFolder(
            name = "Bank",
            icon = "lock",
            iconType = FolderIconType.EMOJI,
            emoji = "x",
            color = variant
        )

        val expected = FolderFormDraft(
            name = "Bank",
            iconType = FolderIconType.EMOJI,
            icon = "lock",
            emoji = "x",
            color = variant
        )
        assertEquals(expected, initialFolderDraft(folder))
    }

    @Test
    fun `an icon folder without icon opens on Letter, as the home draws it`() {
        assertEquals(FolderIconType.LETTER, initialFolderDraft(AppFolder(name = "Tools")).iconType)
    }

    @Test
    fun `saving a new icon folder pins the suggested icon`() {
        val details = FolderFormDraft(name = "Banco").toDetails("New Folder")

        assertEquals(FolderDetails(name = "Banco", icon = "account_balance"), details)
    }

    @Test
    fun `saving uses the placeholder for a blank name`() {
        assertEquals("New Folder", FolderFormDraft(name = " ").toDetails("New Folder").name)
    }

    @Test
    fun `saving a letter folder keeps the icon chosen before`() {
        val details = FolderFormDraft(name = "Work", iconType = FolderIconType.LETTER, icon = "star", color = variant)
            .toDetails("New Folder")

        assertEquals(FolderDetails("Work", "star", FolderIconType.LETTER, null, variant), details)
    }

    @Test
    fun `saving a letter folder with no chosen icon writes no icon`() {
        assertNull(FolderFormDraft(name = "Work", iconType = FolderIconType.LETTER).toDetails("x").icon)
    }

    @Test
    fun `saving an emoji folder pins the first common emoji when none was chosen`() {
        val details = FolderFormDraft(name = "Fun", iconType = FolderIconType.EMOJI).toDetails("x")

        assertEquals(FOLDER_EMOJIS.first(), details.emoji)
        assertEquals(FolderIconType.EMOJI, details.iconType)
    }

    @Test
    fun `an emoji chosen earlier stays when the folder shows its icon`() {
        val details = FolderFormDraft(name = "Fun", icon = "star", emoji = "y").toDetails("x")

        assertEquals("y", details.emoji)
        assertEquals("star", details.icon)
    }

    @Test
    fun `the letter is the first one of the label, in upper case`() {
        assertEquals("W", folderLetter(" work"))
        assertEquals("A", folderLetter(""))
    }

    @Test
    fun `tapping the marked color clears it, any other one is chosen`() {
        val fixed = ThemeColorRef.Fixed(0xFF112233.toInt())

        assertNull(toggleFolderColor(variant, variant))
        assertEquals(fixed, toggleFolderColor(variant, fixed))
        assertEquals(variant, toggleFolderColor(null, variant))
    }

    @Test
    fun `a typed emoji goes in front of the common ones, keeping the grid size`() {
        assertEquals(FOLDER_EMOJIS, folderEmojiChoices(null))
        assertEquals(FOLDER_EMOJIS, folderEmojiChoices(FOLDER_EMOJIS[3]))

        val choices = folderEmojiChoices("z")
        assertEquals("z", choices.first())
        assertEquals(FOLDER_EMOJIS.size, choices.size)
    }

    @Test
    fun `the last grapheme keeps whole emojis`() {
        val thumbsUpWithTone = "👍🏽"

        assertEquals(thumbsUpWithTone, lastGrapheme("ab$thumbsUpWithTone"))
        assertEquals("🏦", lastGrapheme(" 🏦 "))
        assertEquals("b", lastGrapheme("ab"))
        assertNull(lastGrapheme("   "))
        assertNull(lastGrapheme(""))
    }

    @Test
    fun `an icon tile is marked by the selected key, old keys included`() {
        assertTrue(isFolderIconMarked("shopping_cart", "cart"))
        assertTrue(isFolderIconMarked("work", "work"))
        assertFalse(isFolderIconMarked("work", "star"))
        assertFalse(isFolderIconMarked("work", null))
        assertTrue(isFolderIconMarked("delete", "delete"))
    }

    @Test
    fun `the icon kind maps to the segmented control and back`() {
        FolderIconType.entries.forEach { assertEquals(it, folderIconTypeAt(folderIconTypeIndex(it))) }
        assertEquals(FolderIconType.ICON, folderIconTypeAt(-1))
        assertEquals(FolderIconType.ICON, folderIconTypeAt(FolderIconType.entries.size))
    }

    @Test
    fun `the common emojis fill whole rows with the plus tile`() {
        assertEquals(0, (FOLDER_EMOJIS.size + 1) % FOLDER_EMOJI_COLUMNS)
    }
}
