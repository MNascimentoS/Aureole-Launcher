package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClockHeaderUtilsTest {

    @Test
    fun `when time format is 12H, should return 12H format pair`() {
        // Arrange
        val input = "12H"

        // Act
        val result = ClockHeaderUtils.resolveClockTimeFormats(input)

        // Assert
        assertEquals("hh:mm", result.first)
        assertEquals("hh:mm", result.second)
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
        assertEquals("hh:mm", resultSystem.first)
        assertEquals("HH:mm", resultSystem.second)
        assertEquals("hh:mm", resultUnknown.first)
        assertEquals("HH:mm", resultUnknown.second)
    }

    @Test
    fun `when date format is SHORT, should return short date pattern`() {
        // Arrange & Act
        val pattern = ClockHeaderUtils.resolveClockDatePattern("SHORT")

        // Assert
        assertEquals("EEE, d MMM", pattern)
    }

    @Test
    fun `when date format is NUMERIC, should return numeric date pattern`() {
        // Arrange & Act
        val pattern = ClockHeaderUtils.resolveClockDatePattern("NUMERIC")

        // Assert
        assertEquals("dd/MM/yyyy", pattern)
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
        val patternDefault = ClockHeaderUtils.resolveClockDatePattern("DEFAULT")
        val patternUnknown = ClockHeaderUtils.resolveClockDatePattern("UNKNOWN")

        // Assert
        assertEquals("EEEE, d 'de' MMMM", patternDefault)
        assertEquals("EEEE, d 'de' MMMM", patternUnknown)
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

    @Test
    fun `when font family is BOLD, SERIF, MONOSPACE, or ROUNDED, should return corresponding typeface`() {
        // Arrange & Act
        val boldTypeface = ClockHeaderUtils.resolveClockTypeface("BOLD")
        val serifTypeface = ClockHeaderUtils.resolveClockTypeface("SERIF")
        val monoTypeface = ClockHeaderUtils.resolveClockTypeface("MONOSPACE")
        val roundedTypeface = ClockHeaderUtils.resolveClockTypeface("ROUNDED")
        val defaultTypeface = ClockHeaderUtils.resolveClockTypeface("SANS_SERIF")

        // Assert
        assertNotNull(boldTypeface)
        assertNotNull(serifTypeface)
        assertNotNull(monoTypeface)
        assertNotNull(roundedTypeface)
        assertNotNull(defaultTypeface)
    }
}
