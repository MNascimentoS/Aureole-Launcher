package dev.mnascimentos.aureole.composable.clock.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockFontSpecTest {

    @Test
    fun `each storage key maps to its option`() {
        // Arrange
        val expected = mapOf(
            "BOLD" to ClockFontSpec.BOLD,
            "SERIF" to ClockFontSpec.SERIF,
            "MONOSPACE" to ClockFontSpec.MONO,
            "MONO" to ClockFontSpec.MONO,
            "ROUNDED" to ClockFontSpec.ROUNDED
        )

        // Act & Assert
        expected.forEach { (key, spec) -> assertEquals(key, spec, ClockFontSpec.fromKey(key)) }
    }

    @Test
    fun `sans serif, default and unknown keys follow the theme`() {
        // Arrange & Act & Assert
        listOf("SANS_SERIF", "DEFAULT", "THEME", "", "whatever").forEach {
            assertEquals(it, ClockFontSpec.THEME, ClockFontSpec.fromKey(it))
        }
    }

    @Test
    fun `key is case insensitive`() {
        // Arrange & Act & Assert
        assertEquals(ClockFontSpec.SERIF, ClockFontSpec.fromKey("serif"))
        assertEquals(ClockFontSpec.MONO, ClockFontSpec.fromKey("Monospace"))
    }

    @Test
    fun `storage key round trips`() {
        // Arrange & Act & Assert
        ClockFontSpec.entries.forEach { assertEquals(it, ClockFontSpec.fromKey(it.storageKey)) }
    }
}
