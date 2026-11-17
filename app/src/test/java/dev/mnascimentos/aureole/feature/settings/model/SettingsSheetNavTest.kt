package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsSheetNavTest {

    private val appearance = SettingsSheet.Area(SettingsArea.APPEARANCE)
    private val advanced = SettingsSheet.Area(SettingsArea.ADVANCED)
    private val paletteFromAppearance = SettingsSheet.Child(SettingsChildKind.PALETTE, SettingsArea.APPEARANCE)

    @Test
    fun `opening an area from the root shows that area`() {
        SettingsArea.entries.forEach { area ->
            assertEquals(SettingsSheet.Area(area), reduceSettingsSheet(null, SettingsSheetEvent.OpenArea(area)))
        }
    }

    @Test
    fun `opening a child swaps it in place of the area and remembers the area`() {
        val next = reduceSettingsSheet(appearance, SettingsSheetEvent.OpenChild(SettingsChildKind.PALETTE))
        assertEquals(paletteFromAppearance, next)
    }

    @Test
    fun `opening a child from the root has no area to go back to`() {
        val next = reduceSettingsSheet(null, SettingsSheetEvent.OpenChild(SettingsChildKind.RELEASE_NOTES))
        assertEquals(SettingsSheet.Child(SettingsChildKind.RELEASE_NOTES, returnTo = null), next)
    }

    @Test
    fun `opening a child over a child keeps the area of the first`() {
        val next = reduceSettingsSheet(paletteFromAppearance, SettingsSheetEvent.OpenChild(SettingsChildKind.FONT))
        assertEquals(SettingsSheet.Child(SettingsChildKind.FONT, SettingsArea.APPEARANCE), next)
    }

    @Test
    fun `closing the child brings its area back`() {
        val next = reduceSettingsSheet(paletteFromAppearance, SettingsSheetEvent.CloseChild(SettingsChildKind.PALETTE))
        assertEquals(appearance, next)
    }

    @Test
    fun `closing a child opened from the root goes back to the root`() {
        val notes = SettingsSheet.Child(SettingsChildKind.RELEASE_NOTES, returnTo = null)
        assertNull(reduceSettingsSheet(notes, SettingsSheetEvent.CloseChild(SettingsChildKind.RELEASE_NOTES)))
    }

    @Test
    fun `a late close of another child changes nothing`() {
        val next = reduceSettingsSheet(paletteFromAppearance, SettingsSheetEvent.CloseChild(SettingsChildKind.FONT))
        assertEquals(paletteFromAppearance, next)
        assertEquals(appearance, reduceSettingsSheet(appearance, SettingsSheetEvent.CloseChild(SettingsChildKind.FONT)))
        assertNull(reduceSettingsSheet(null, SettingsSheetEvent.CloseChild(SettingsChildKind.FONT)))
    }

    @Test
    fun `closing the area goes back to the root`() {
        assertNull(reduceSettingsSheet(advanced, SettingsSheetEvent.CloseArea(SettingsArea.ADVANCED)))
    }

    @Test
    fun `a late close of another area, or while a child is up, changes nothing`() {
        assertEquals(advanced, reduceSettingsSheet(advanced, SettingsSheetEvent.CloseArea(SettingsArea.BEHAVIOR)))
        assertEquals(
            paletteFromAppearance,
            reduceSettingsSheet(paletteFromAppearance, SettingsSheetEvent.CloseArea(SettingsArea.APPEARANCE))
        )
        assertNull(reduceSettingsSheet(null, SettingsSheetEvent.CloseArea(SettingsArea.APPEARANCE)))
    }

    @Test
    fun `back walks child, then area, then root`() {
        val area = reduceSettingsSheet(paletteFromAppearance, SettingsSheetEvent.Back)
        assertEquals(appearance, area)
        val root = reduceSettingsSheet(area, SettingsSheetEvent.Back)
        assertNull(root)
        assertNull(reduceSettingsSheet(root, SettingsSheetEvent.Back))
    }

    @Test
    fun `back on a child opened from the root goes to the root`() {
        val notes = SettingsSheet.Child(SettingsChildKind.RELEASE_NOTES, returnTo = null)
        assertNull(reduceSettingsSheet(notes, SettingsSheetEvent.Back))
    }

    @Test
    fun `the area under a sheet is the open area or the one the child returns to`() {
        assertEquals(SettingsArea.APPEARANCE, appearance.area)
        assertEquals(SettingsArea.APPEARANCE, paletteFromAppearance.area)
        assertNull(SettingsSheet.Child(SettingsChildKind.CONTRIBUTORS, returnTo = null).area)
        val none: SettingsSheet? = null
        assertNull(none.area)
    }

    @Test
    fun `only Appearance has the mini-home preview`() {
        assertTrue(settingsAreaHasPreview(SettingsArea.APPEARANCE))
        assertFalse(settingsAreaHasPreview(SettingsArea.BEHAVIOR))
        assertFalse(settingsAreaHasPreview(SettingsArea.ADVANCED))
    }

    @Test
    fun `each area has its own title`() {
        assertEquals(R.string.settings_appearance_title, settingsAreaTitle(SettingsArea.APPEARANCE))
        assertEquals(R.string.settings_behavior_title, settingsAreaTitle(SettingsArea.BEHAVIOR))
        assertEquals(R.string.settings_advanced_title, settingsAreaTitle(SettingsArea.ADVANCED))
    }

    @Test
    fun `every sheet survives being saved as text`() {
        val sheets = buildList<SettingsSheet?> {
            add(null)
            SettingsArea.entries.forEach { add(SettingsSheet.Area(it)) }
            SettingsChildKind.entries.forEach { kind ->
                add(SettingsSheet.Child(kind, returnTo = null))
                SettingsArea.entries.forEach { add(SettingsSheet.Child(kind, it)) }
            }
        }
        sheets.forEach { sheet -> assertEquals(sheet, decodeSettingsSheet(encodeSettingsSheet(sheet))) }
    }

    @Test
    fun `unknown saved text reads as the root`() {
        listOf(null, "", "area", "area:NOPE", "child", "child:NOPE:APPEARANCE", "other:APPEARANCE").forEach {
            assertNull(decodeSettingsSheet(it))
        }
        assertEquals(
            SettingsSheet.Child(SettingsChildKind.FONT, returnTo = null),
            decodeSettingsSheet("child:FONT:NOPE")
        )
    }
}
