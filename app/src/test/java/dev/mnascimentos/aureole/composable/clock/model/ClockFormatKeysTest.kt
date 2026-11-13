package dev.mnascimentos.aureole.composable.clock.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class ClockFormatKeysTest {

    @Test
    fun `each date key maps to its skeleton`() {
        // Arrange
        val expected = mapOf(
            "DEFAULT" to "EEEEdMMMM",
            "FULL" to "EEEEdMMMM",
            "SHORT" to "EEEdMMM",
            "MEDIUM" to "dMMMM",
            "NUMERIC" to "ddMMyyyy"
        )

        // Act & Assert
        expected.forEach { (key, skeleton) -> assertEquals(key, skeleton, clockDateSkeleton(key)) }
    }

    @Test
    fun `date key is case insensitive and unknown falls back to full`() {
        // Arrange & Act & Assert
        assertEquals("EEEdMMM", clockDateSkeleton("short"))
        assertEquals("dMMMM", clockDateSkeleton("Medium"))
        assertEquals("EEEEdMMMM", clockDateSkeleton("UNKNOWN"))
        assertEquals("EEEEdMMMM", clockDateSkeleton(""))
    }

    @Test
    fun `date storage key round trips`() {
        // Arrange & Act & Assert
        ClockDateFormat.entries.forEach { assertEquals(it, ClockDateFormat.fromKey(it.storageKey)) }
    }

    @Test
    fun `time key maps to 12H, 24H or system`() {
        // Arrange & Act & Assert
        assertEquals(ClockTimeFormat.H12, ClockTimeFormat.fromKey("12H"))
        assertEquals(ClockTimeFormat.H24, ClockTimeFormat.fromKey("24h"))
        assertEquals(ClockTimeFormat.SYSTEM, ClockTimeFormat.fromKey("SYSTEM"))
        assertEquals(ClockTimeFormat.SYSTEM, ClockTimeFormat.fromKey("whatever"))
    }

    @Test
    fun `sample is the pattern applied to the given instant`() {
        // Arrange
        val default = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

        try {
            // Act
            val result = formatClockSample("HH:mm", Locale.US, 0L)

            // Assert
            assertEquals("00:00", result)
        } finally {
            TimeZone.setDefault(default)
        }
    }
}
