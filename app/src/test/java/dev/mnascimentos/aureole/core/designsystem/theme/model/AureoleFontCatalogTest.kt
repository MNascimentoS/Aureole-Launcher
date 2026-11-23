package dev.mnascimentos.aureole.core.designsystem.theme.model

import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleFontCatalogTest {

    private val keys = AureoleFontCatalog.map { it.key }

    @Test
    fun `catalog keys are unique`() {
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test
    fun `catalog has Serif Monospaced and Rounded with a translated name`() {
        listOf(FONT_KEY_SERIF, FONT_KEY_MONOSPACED, FONT_KEY_ROUNDED).forEach {
            assertTrue(it, it in keys)
            assertNotNull(it, fontNameRes(it))
        }
    }

    @Test
    fun `keys saved by older versions are still in the catalog`() {
        val legacy = listOf(
            "Istok Web", "Roboto", "Roboto Mono", "Montserrat", "Andika", "Rubik",
            "Chelsea Market", "Special Elite", "Orbitron", "Space Grotesk", "Space Mono", "Audiowide"
        )
        assertEquals(legacy, keys.take(legacy.size))
    }

    @Test
    fun `catalog has the 17 fonts accepted for this release`() {
        val added = listOf(
            "Poppins", "Outfit", "Manrope", "Lexend", "Quicksand", "Nunito", "Josefin Sans", "Bebas Neue",
            "Righteous", "Michroma", "Playfair Display", "DM Serif Display", "Lora", "Abril Fatface",
            "JetBrains Mono", "Share Tech Mono", "VT323"
        )
        added.forEach { assertTrue(it, it in keys) }
        assertEquals(12 + 3 + added.size, keys.size)
    }

    @Test
    fun `every catalog key has a family and the other way round`() {
        assertEquals(keys, AvailableFonts.keys.toList())
    }

    @Test
    fun `proper names have no string resource`() {
        assertNull(fontNameRes("Poppins"))
        assertNull(fontNameRes("unknown"))
    }
}
