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
    private val bgChild = SettingsSheet.Child(SettingsChildKind.BACKGROUND, SettingsArea.APPEARANCE)

    @Test
    fun `opening an area from the root shows that area`() {
        SettingsArea.entries.forEach { area ->
            assertEquals(SettingsSheet.Area(area), reduceSettingsSheet(null, SettingsSheetEvent.OpenArea(area)))
        }
    }

    @Test
    fun `opening a child swaps it in place of the area and remembers the area`() {
        val next = reduceSettingsSheet(appearance, SettingsSheetEvent.OpenChild(SettingsChildKind.BACKGROUND))
        assertEquals(bgChild, next)
    }

    @Test
    fun `opening a child from the root has no area to go back to`() {
        val next = reduceSettingsSheet(null, SettingsSheetEvent.OpenChild(SettingsChildKind.RELEASE_NOTES))
        assertEquals(SettingsSheet.Child(SettingsChildKind.RELEASE_NOTES, returnTo = null), next)
    }

    @Test
    fun `opening a child over a child keeps the area of the first`() {
        val next = reduceSettingsSheet(bgChild, SettingsSheetEvent.OpenChild(SettingsChildKind.CARDS_GLASS))
        assertEquals(SettingsSheet.Child(SettingsChildKind.CARDS_GLASS, SettingsArea.APPEARANCE), next)
    }

    @Test
    fun `closing the child brings its area back`() {
        val next = reduceSettingsSheet(bgChild, SettingsSheetEvent.CloseChild(SettingsChildKind.BACKGROUND))
        assertEquals(appearance, next)
    }

    @Test
    fun `closing a child opened from the root goes back to the root`() {
        val notes = SettingsSheet.Child(SettingsChildKind.RELEASE_NOTES, returnTo = null)
        assertNull(reduceSettingsSheet(notes, SettingsSheetEvent.CloseChild(SettingsChildKind.RELEASE_NOTES)))
    }

    @Test
    fun `a late close of another child changes nothing`() {
        val next = reduceSettingsSheet(bgChild, SettingsSheetEvent.CloseChild(SettingsChildKind.CARDS_GLASS))
        assertEquals(bgChild, next)
        val lateClose = SettingsSheetEvent.CloseChild(SettingsChildKind.CARDS_GLASS)
        assertEquals(appearance, reduceSettingsSheet(appearance, lateClose))
        assertNull(reduceSettingsSheet(null, SettingsSheetEvent.CloseChild(SettingsChildKind.CARDS_GLASS)))
    }

    @Test
    fun `closing the area goes back to the root`() {
        assertNull(reduceSettingsSheet(advanced, SettingsSheetEvent.CloseArea(SettingsArea.ADVANCED)))
    }

    @Test
    fun `a late close of another area, or while a child is up, changes nothing`() {
        assertEquals(advanced, reduceSettingsSheet(advanced, SettingsSheetEvent.CloseArea(SettingsArea.BEHAVIOR)))
        assertEquals(
            bgChild,
            reduceSettingsSheet(bgChild, SettingsSheetEvent.CloseArea(SettingsArea.APPEARANCE))
        )
        assertNull(reduceSettingsSheet(null, SettingsSheetEvent.CloseArea(SettingsArea.APPEARANCE)))
    }

    @Test
    fun `back walks child, then area, then root`() {
        val area = reduceSettingsSheet(bgChild, SettingsSheetEvent.Back)
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
        assertEquals(SettingsArea.APPEARANCE, bgChild.area)
        assertNull(SettingsSheet.Child(SettingsChildKind.CONTRIBUTORS, returnTo = null).area)
        val none: SettingsSheet? = null
        assertNull(none.area)
    }

    @Test
    fun `only Appearance has the pickers inside and the mini-home preview`() {
        assertTrue(settingsAreaHasInner(SettingsArea.APPEARANCE))
        assertFalse(settingsAreaHasInner(SettingsArea.BEHAVIOR))
        assertFalse(settingsAreaHasInner(SettingsArea.ADVANCED))
    }

    @Test
    fun `palette and font open inside the Appearance sheet, not as another sheet`() {
        SettingsInnerKind.entries.forEach { kind ->
            val next = reduceSettingsSheet(appearance, SettingsSheetEvent.OpenInner(kind))
            assertEquals(SettingsSheet.Area(SettingsArea.APPEARANCE, inner = kind), next)
            assertEquals(SettingsArea.APPEARANCE, next.area)
        }
    }

    @Test
    fun `a picker opens only in the Appearance list`() {
        val behavior = SettingsSheet.Area(SettingsArea.BEHAVIOR)
        assertEquals(behavior, reduceSettingsSheet(behavior, SettingsSheetEvent.OpenInner(SettingsInnerKind.PALETTE)))
        assertEquals(
            bgChild,
            reduceSettingsSheet(bgChild, SettingsSheetEvent.OpenInner(SettingsInnerKind.FONT))
        )
        assertNull(reduceSettingsSheet(null, SettingsSheetEvent.OpenInner(SettingsInnerKind.FONT)))
    }

    @Test
    fun `back from a picker brings the Appearance list back without closing the sheet`() {
        val palette = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE)
        val list = reduceSettingsSheet(palette, SettingsSheetEvent.Back)
        assertEquals(appearance, list)
        assertNull(reduceSettingsSheet(list, SettingsSheetEvent.Back))
    }

    @Test
    fun `closing the sheet with a picker inside goes back to the root`() {
        val font = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.FONT)
        assertNull(reduceSettingsSheet(font, SettingsSheetEvent.CloseArea(SettingsArea.APPEARANCE)))
    }

    @Test
    fun `a picker can switch to the other one and a child leaves the picker behind`() {
        val palette = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE)
        assertEquals(
            SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.FONT),
            reduceSettingsSheet(palette, SettingsSheetEvent.OpenInner(SettingsInnerKind.FONT))
        )
        val child = reduceSettingsSheet(palette, SettingsSheetEvent.OpenChild(SettingsChildKind.BACKGROUND))
        assertEquals(bgChild, child)
        val closed = SettingsSheetEvent.CloseChild(SettingsChildKind.BACKGROUND)
        assertEquals(appearance, reduceSettingsSheet(child, closed))
    }

    @Test
    fun `the sheet title follows the picker inside it`() {
        assertEquals(
            R.string.settings_appearance_color_palette,
            settingsSheetTitle(SettingsArea.APPEARANCE, SettingsInnerKind.PALETTE)
        )
        assertEquals(
            R.string.home_customize_adjust_font,
            settingsSheetTitle(SettingsArea.APPEARANCE, SettingsInnerKind.FONT)
        )
        assertEquals(R.string.settings_appearance_title, settingsSheetTitle(SettingsArea.APPEARANCE, null))
        assertEquals(R.string.settings_advanced_title, settingsSheetTitle(SettingsArea.ADVANCED, null))
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
            SettingsInnerKind.entries.forEach { add(SettingsSheet.Area(SettingsArea.APPEARANCE, inner = it)) }
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
            SettingsSheet.Child(SettingsChildKind.CARDS_GLASS, returnTo = null),
            decodeSettingsSheet("child:CARDS_GLASS:NOPE")
        )
        assertEquals(appearance, decodeSettingsSheet("area:APPEARANCE:NOPE"))
        // Only Appearance has pickers: a saved one on another area reads as its list.
        assertEquals(advanced, decodeSettingsSheet("area:ADVANCED:PALETTE"))
    }
}
