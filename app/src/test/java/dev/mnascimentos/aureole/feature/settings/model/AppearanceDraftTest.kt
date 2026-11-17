package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.toLight
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** The palette and font tried inside the Appearance sheet: what leaving a picker or closing the sheet saves. */
class AppearanceDraftTest {

    private val selectedPalette = allPalettes[0].name
    private val otherPalette = allPalettes[1].name

    @Test
    fun `back from the palette saves the palette and keeps the font draft`() {
        val draft = AppearanceDraft(paletteName = otherPalette, fontName = "Roboto")
        val saved = draft.saveOnLeaving(SettingsInnerKind.PALETTE, selectedPalette, "Istok Web")
        assertEquals(otherPalette, saved.paletteName)
        assertNull(saved.fontName)
        assertEquals(AppearanceDraft(fontName = "Roboto"), saved.rest)
    }

    @Test
    fun `back from the font saves the font and keeps the palette draft`() {
        val draft = AppearanceDraft(paletteName = otherPalette, fontName = "Roboto")
        val saved = draft.saveOnLeaving(SettingsInnerKind.FONT, selectedPalette, "Istok Web")
        assertNull(saved.paletteName)
        assertEquals("Roboto", saved.fontName)
        assertEquals(AppearanceDraft(paletteName = otherPalette), saved.rest)
    }

    @Test
    fun `closing the sheet saves both and leaves nothing`() {
        val draft = AppearanceDraft(paletteName = otherPalette, fontName = "Roboto")
        val saved = draft.saveOnLeaving(null, selectedPalette, "Istok Web")
        assertEquals(otherPalette, saved.paletteName)
        assertEquals("Roboto", saved.fontName)
        assertEquals(AppearanceDraft(), saved.rest)
    }

    @Test
    fun `a choice equal to the selected one, or no tap, saves nothing`() {
        val sameAsSelected = AppearanceDraft(paletteName = selectedPalette, fontName = "Istok Web")
        val saved = sameAsSelected.saveOnLeaving(null, "$selectedPalette Light", "Istok Web")
        assertNull(saved.paletteName)
        assertNull(saved.fontName)
        val untouched = AppearanceDraft().saveOnLeaving(null, selectedPalette, "Istok Web")
        assertEquals(AppearanceSave(null, null, AppearanceDraft()), untouched)
    }

    @Test
    fun `the preview shows the drafted palette, light on a light system`() {
        val draft = AppearanceDraft(paletteName = otherPalette, fontName = "Roboto")
        assertEquals(allPalettes[1], draft.toPreviewState(isDarkTheme = true).palette)
        assertEquals(allPalettes[1].toLight(), draft.toPreviewState(isDarkTheme = false).palette)
        assertEquals("Roboto", draft.toPreviewState(isDarkTheme = true).fontName)
        assertNull(AppearanceDraft().toPreviewState(isDarkTheme = true).palette)
    }

    @Test
    fun `the draft survives being saved as text`() {
        listOf(
            AppearanceDraft(),
            AppearanceDraft(paletteName = otherPalette),
            AppearanceDraft(fontName = "Roboto"),
            AppearanceDraft(paletteName = otherPalette, fontName = "Roboto")
        ).forEach { draft -> assertEquals(draft, decodeAppearanceDraft(encodeAppearanceDraft(draft))) }
        assertEquals(AppearanceDraft(), decodeAppearanceDraft(""))
    }
}
