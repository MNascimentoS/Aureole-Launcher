package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** The photo editor is a child sheet of the background sheet, which comes back when it closes. */
class WallpaperEditorNavTest {

    private val background = SettingsSheet.Child(SettingsChildKind.BACKGROUND, returnTo = SettingsArea.APPEARANCE)
    private val editor = SettingsSheet.Child(
        SettingsChildKind.WALLPAPER_EDITOR,
        returnTo = SettingsArea.APPEARANCE,
        parent = SettingsChildKind.BACKGROUND
    )
    private val openEditor = SettingsSheetEvent.OpenChild(SettingsChildKind.WALLPAPER_EDITOR)

    @Test
    fun `the editor opens in place of the background sheet and gives it back`() {
        assertEquals(editor, reduceSettingsSheet(background, openEditor))
        val closeEditor = SettingsSheetEvent.CloseChild(SettingsChildKind.WALLPAPER_EDITOR)
        assertEquals(background, reduceSettingsSheet(editor, closeEditor))
        assertEquals(background, reduceSettingsSheet(editor, SettingsSheetEvent.Back))
        assertEquals(
            SettingsSheet.Area(SettingsArea.APPEARANCE),
            reduceSettingsSheet(background, SettingsSheetEvent.CloseChild(SettingsChildKind.BACKGROUND))
        )
    }

    @Test
    fun `a second request while the editor is open changes nothing, a late close of another sheet neither`() {
        assertEquals(editor, reduceSettingsSheet(editor, openEditor))
        assertEquals(editor, reduceSettingsSheet(editor, SettingsSheetEvent.CloseChild(SettingsChildKind.BACKGROUND)))
    }

    @Test
    fun `opened from an area it goes back to the area`() {
        val fromArea = reduceSettingsSheet(SettingsSheet.Area(SettingsArea.APPEARANCE), openEditor)
        assertEquals(SettingsSheet.Child(SettingsChildKind.WALLPAPER_EDITOR, SettingsArea.APPEARANCE), fromArea)
        val back = reduceSettingsSheet(fromArea, SettingsSheetEvent.Back)
        assertEquals(SettingsSheet.Area(SettingsArea.APPEARANCE), back)
    }

    @Test
    fun `the editor and its parent survive a rotation`() {
        assertEquals(editor, decodeSettingsSheet(encodeSettingsSheet(editor)))
        assertEquals("child:BACKGROUND:APPEARANCE", encodeSettingsSheet(background))
        assertEquals(background, decodeSettingsSheet("child:BACKGROUND:APPEARANCE"))
    }
}
