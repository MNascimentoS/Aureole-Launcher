package dev.mnascimentos.aureole.feature.home.components.clock.extensions

import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHeaderConfig
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.feature.home.components.clock.resolveClockConfig
import dev.mnascimentos.aureole.feature.home.components.clock.resolveClockHazeConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val CLOCK_A = "clock_a"

class MainUiStateClockExtTest {

    private val configA = ClockConfig(
        style = "MINIMAL",
        customGreeting = "Oi",
        alignment = "END",
        fontFamily = "SERIF",
        timeFormat = "24H",
        dateFormat = "SHORT",
        textColor = 1,
        backgroundColor = 2,
        isBackgroundEnabled = true
    )
    private val state = MainUiState(clockFontFamily = "MONOSPACE", clockConfigs = mapOf(CLOCK_A to configA))

    @Test
    fun `when the clock has a config, should use it`() {
        assertEquals(configA, state.clockConfigOf(CLOCK_A))
    }

    @Test
    fun `when the clock has no config or no id, should use the clock fields of the state`() {
        val default = ClockConfig(fontFamily = "MONOSPACE")

        assertEquals(default, state.defaultClockConfig())
        assertEquals(default, state.clockConfigOf("clock_b"))
        assertEquals(default, state.clockConfigOf(null))
    }

    @Test
    fun `when editing a clock, should give the editor the clock fields of that clock`() {
        val editor = state.copy(editingClockId = CLOCK_A).forClockEditor()

        assertEquals(configA, editor.editingClockConfig)
        assertEquals(configA, editor.defaultClockConfig())
        assertEquals(state.clockConfigs, editor.clockConfigs)
    }

    @Test
    fun `when a config becomes header overrides, should win over every clock field of the state`() {
        val header = configA.toHeaderConfig()

        val resolved = resolveClockConfig(header, MainUiState())

        assertEquals(configA.style, resolved.style)
        assertEquals(configA.customGreeting, resolved.customGreeting)
        assertEquals(configA.alignment, resolved.alignment)
        assertEquals(configA.fontFamily, resolved.fontFamily)
        assertEquals(configA.timeFormat, resolved.timeFormat)
        assertEquals(configA.dateFormat, resolved.dateFormat)
        assertEquals(configA.textColor, resolved.textColor)
        assertEquals(configA.backgroundColor, resolved.backgroundColor)
    }

    @Test
    fun `when the header has its own background switch, should not use the one of the state`() {
        val haze = ClockHazeConfig(isBackgroundEnabled = true)
        val noBackgroundHome = MainUiState(isClockBackgroundEnabled = false)
        val backgroundHome = MainUiState(isClockBackgroundEnabled = true)

        assertTrue(resolveClockHazeConfig(haze, noBackgroundHome, configA.toHeaderConfig()).isBackgroundEnabled)
        assertFalse(resolveClockHazeConfig(haze, backgroundHome, ClockConfig().toHeaderConfig()).isBackgroundEnabled)
        assertTrue(resolveClockHazeConfig(haze, backgroundHome, ClockHeaderConfig()).isBackgroundEnabled)
    }
}
