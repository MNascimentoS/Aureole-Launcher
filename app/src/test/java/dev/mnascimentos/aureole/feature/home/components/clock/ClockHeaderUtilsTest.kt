package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Calendar
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class ClockHeaderUtilsTest {

    @Test
    fun `when time format is 12H, should return 12H format pair`() {
        // Arrange
        val input = "12H"

        // Act
        val result = ClockHeaderUtils.resolveClockTimeFormats(input)

        // Assert
        assertEquals("h:mm a", result.first)
        assertEquals("h:mm a", result.second)
    }

    @Test
    fun `when time format is 24H, should return 24H format pair`() {
        // Arrange
        val input = "24H"

        // Act
        val result = ClockHeaderUtils.resolveClockTimeFormats(input)

        // Assert
        assertEquals("HH:mm", result.first)
        assertEquals("HH:mm", result.second)
    }

    @Test
    fun `when time format is SYSTEM or unknown, should return default format pair`() {
        // Arrange & Act
        val resultSystem = ClockHeaderUtils.resolveClockTimeFormats("SYSTEM")
        val resultUnknown = ClockHeaderUtils.resolveClockTimeFormats("RANDOM")

        // Assert
        assertEquals("h:mm a", resultSystem.first)
        assertEquals("HH:mm", resultSystem.second)
        assertEquals("h:mm a", resultUnknown.first)
        assertEquals("HH:mm", resultUnknown.second)
    }

    @Test
    fun `when 12H, should format without leading zero and with AM PM`() {
        // Arrange
        val pattern = ClockHeaderUtils.resolveClockTimePattern("12H", isSystem24Hour = true)
        val afternoon = Calendar.getInstance().apply { set(2026, 0, 5, 15, 15, 0) }.timeInMillis

        // Act
        val text = formatClockSample(pattern, Locale.US, afternoon)

        // Assert
        assertEquals("3:15 PM", text)
    }

    @Test
    fun `when SYSTEM, should follow the system 24 hour setting`() {
        assertEquals("HH:mm", ClockHeaderUtils.resolveClockTimePattern("SYSTEM", isSystem24Hour = true))
        assertEquals("h:mm a", ClockHeaderUtils.resolveClockTimePattern("SYSTEM", isSystem24Hour = false))
        assertEquals("HH:mm", ClockHeaderUtils.resolveClockTimePattern("24H", isSystem24Hour = false))
    }

    @Test
    fun `when date format is ISO, should return iso date pattern`() {
        // Arrange & Act
        val pattern = ClockHeaderUtils.resolveClockDatePattern("ISO")

        // Assert
        assertEquals("yyyy-MM-dd", pattern)
    }

    @Test
    fun `when date format is DEFAULT or unknown, should return default date pattern`() {
        // Arrange & Act
        val patternDefault = ClockHeaderUtils.resolveClockDatePattern("DEFAULT", Locale.US)
        val patternUnknown = ClockHeaderUtils.resolveClockDatePattern("UNKNOWN", Locale.US)

        // Assert
        assertEquals(patternDefault, patternUnknown)
    }

    @Test
    fun `date patterns follow the english locale for the four formats`() {
        // Arrange
        val locale = Locale.US

        // Act
        val patterns = listOf("DEFAULT", "SHORT", "MEDIUM", "NUMERIC")
            .map { ClockHeaderUtils.resolveClockDatePattern(it, locale) }

        // Assert
        assertEquals(listOf("EEEE, MMMM d", "EEE, MMM d", "MMMM d", "MM/dd/yyyy"), patterns)
    }

    @Test
    fun `date patterns follow the portuguese locale for the four formats`() {
        // Arrange
        val locale = Locale.forLanguageTag("pt-BR")

        // Act
        val patterns = listOf("DEFAULT", "SHORT", "MEDIUM", "NUMERIC")
            .map { ClockHeaderUtils.resolveClockDatePattern(it, locale) }

        // Assert
        assertEquals(listOf("EEEE, d 'de' MMMM", "EEE, d 'de' MMM", "d 'de' MMMM", "dd/MM/yyyy"), patterns)
    }

    @Test
    fun `medium date no longer falls back to the full date`() {
        // Arrange & Act
        val medium = ClockHeaderUtils.resolveClockDatePattern("MEDIUM", Locale.US)
        val full = ClockHeaderUtils.resolveClockDatePattern("DEFAULT", Locale.US)

        // Assert
        assertNotEquals(full, medium)
    }

    @Test
    fun `when alignment is CENTER, should return center alignments`() {
        // Arrange & Act
        val (horizontal, text) = ClockHeaderUtils.resolveClockAlignments("CENTER")

        // Assert
        assertEquals(Alignment.CenterHorizontally, horizontal)
        assertEquals(TextAlign.Center, text)
    }

    @Test
    fun `when alignment is END or RIGHT, should return end alignments`() {
        // Arrange & Act
        val (horizontalEnd, textEnd) = ClockHeaderUtils.resolveClockAlignments("END")
        val (horizontalRight, textRight) = ClockHeaderUtils.resolveClockAlignments("RIGHT")

        // Assert
        assertEquals(Alignment.End, horizontalEnd)
        assertEquals(TextAlign.End, textEnd)
        assertEquals(Alignment.End, horizontalRight)
        assertEquals(TextAlign.End, textRight)
    }

    @Test
    fun `when alignment is START or unknown, should return start alignments`() {
        // Arrange & Act
        val (horizontalStart, textStart) = ClockHeaderUtils.resolveClockAlignments("START")
        val (horizontalUnknown, textUnknown) = ClockHeaderUtils.resolveClockAlignments("OTHER")

        // Assert
        assertEquals(Alignment.Start, horizontalStart)
        assertEquals(TextAlign.Start, textStart)
        assertEquals(Alignment.Start, horizontalUnknown)
        assertEquals(TextAlign.Start, textUnknown)
    }
}
