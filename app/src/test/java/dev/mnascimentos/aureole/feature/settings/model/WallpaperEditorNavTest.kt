package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** The photo editor opens full screen from the background picker inside Appearance, which comes back when it closes. */
class WallpaperEditorNavTest {

    private val background = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND)
    private val editor = SettingsSheet.Child(
        SettingsChildKind.WALLPAPER_EDITOR,
        returnTo = SettingsArea.APPEARANCE,
        returnInner = SettingsInnerKind.BACKGROUND
    )
    private val openEditor = SettingsSheetEvent.OpenChild(SettingsChildKind.WALLPAPER_EDITOR)

    @Test
    fun `the editor opens over the background picker and gives it back`() {
        assertEquals(editor, reduceSettingsSheet(background, openEditor))
        val closeEditor = SettingsSheetEvent.CloseChild(SettingsChildKind.WALLPAPER_EDITOR)
        assertEquals(background, reduceSettingsSheet(editor, closeEditor))
        assertEquals(background, reduceSettingsSheet(editor, SettingsSheetEvent.Back))
        val list = reduceSettingsSheet(background, SettingsSheetEvent.Back)
        assertEquals(SettingsSheet.Area(SettingsArea.APPEARANCE), list)
    }

    @Test
    fun `a second request while the editor is open changes nothing, a late close of another sheet neither`() {
        assertEquals(editor, reduceSettingsSheet(editor, openEditor))
        assertEquals(editor, reduceSettingsSheet(editor, SettingsSheetEvent.CloseChild(SettingsChildKind.CARDS_GLASS)))
    }

    @Test
    fun `opened from the area list it goes back to the list`() {
        val fromArea = reduceSettingsSheet(SettingsSheet.Area(SettingsArea.APPEARANCE), openEditor)
        assertEquals(SettingsSheet.Child(SettingsChildKind.WALLPAPER_EDITOR, SettingsArea.APPEARANCE), fromArea)
        val back = reduceSettingsSheet(fromArea, SettingsSheetEvent.Back)
        assertEquals(SettingsSheet.Area(SettingsArea.APPEARANCE), back)
    }

    @Test
    fun `another child opened from a picker gives back the list, not the picker`() {
        val palette = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE)
        val glass = reduceSettingsSheet(palette, SettingsSheetEvent.OpenChild(SettingsChildKind.CARDS_GLASS))
        assertEquals(SettingsSheet.Child(SettingsChildKind.CARDS_GLASS, SettingsArea.APPEARANCE), glass)
    }

    @Test
    fun `the editor and the picker it returns to survive a rotation`() {
        assertEquals(editor, decodeSettingsSheet(encodeSettingsSheet(editor)))
        assertEquals("child:WALLPAPER_EDITOR:APPEARANCE:BACKGROUND", encodeSettingsSheet(editor))
        assertEquals(background, decodeSettingsSheet(encodeSettingsSheet(background)))
    }
}
