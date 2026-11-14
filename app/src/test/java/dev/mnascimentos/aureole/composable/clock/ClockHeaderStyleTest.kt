package dev.mnascimentos.aureole.composable.clock

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.ClockFontSpec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import java.util.Calendar

class ClockHeaderStyleTest {

    private val surface = Color(0xFF101010)
    private val custom = 0xFF336699.toInt()

    @Test
    fun `greetingRes gives morning from 5 to 11`() {
        listOf(5, 8, 11).forEach { assertEquals(R.string.home_clock_greeting_morning, greetingRes(it)) }
    }

    @Test
    fun `greetingRes gives afternoon from 12 to 17`() {
        listOf(12, 15, 17).forEach { assertEquals(R.string.home_clock_greeting_afternoon, greetingRes(it)) }
    }

    @Test
    fun `greetingRes gives evening outside the day ranges`() {
        listOf(0, 4, 18, 23).forEach { assertEquals(R.string.home_clock_greeting_evening, greetingRes(it)) }
    }

    @Test
    fun `hourOfDay reads the hour of the given time`() {
        val time = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
        }.timeInMillis
        assertEquals(14, hourOfDay(time))
    }

    @Test
    fun `background off is transparent whatever the other flags`() {
        assertEquals(Color.Transparent, resolveClockBgColor(false, true, custom, 0.4f, surface))
        assertEquals(Color.Transparent, resolveClockBgColor(false, false, 0, 0.4f, surface))
    }

    @Test
    fun `background on without glass is the surface or the custom color, opaque`() {
        assertEquals(surface, resolveClockBgColor(true, false, 0, 0.4f, surface))
        assertEquals(Color(custom), resolveClockBgColor(true, false, custom, 0.4f, surface))
    }

    @Test
    fun `background on with glass takes the glass opacity`() {
        assertEquals(surface.copy(alpha = 0.4f), resolveClockBgColor(true, true, 0, 0.4f, surface))
        assertEquals(Color(custom).copy(alpha = 0.4f), resolveClockBgColor(true, true, custom, 0.4f, surface))
    }

    @Test
    fun `clockHazeEffect returns the same modifier when the glass is off or has no state`() {
        val base = Modifier
        assertSame(base, base.clockHazeEffect(false, HazeState(), 0.5f, 16.dp, surface))
        assertSame(base, base.clockHazeEffect(true, null, 0.5f, 16.dp, surface))
    }

    @Test
    fun `clockHazeEffect adds the glass when it is on and there is a state`() {
        val base = Modifier
        assertNotSame(base, base.clockHazeEffect(true, HazeState(), 0.5f, 16.dp, surface))
    }

    @Test
    fun `fontFamilyFor uses the theme family only for THEME`() {
        val theme = FontFamily.Cursive
        assertEquals(theme, ClockFontSpec.THEME.fontFamilyFor(theme))
        assertEquals(FontFamily.SansSerif, ClockFontSpec.BOLD.fontFamilyFor(theme))
        assertEquals(FontFamily.Serif, ClockFontSpec.SERIF.fontFamilyFor(theme))
        assertEquals(FontFamily.Monospace, ClockFontSpec.MONO.fontFamilyFor(theme))
    }
}
