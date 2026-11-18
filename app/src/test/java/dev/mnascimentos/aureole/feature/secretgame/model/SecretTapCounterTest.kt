package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretTapCounterTest {

    private fun tap(times: Int, gapMs: Long, from: SecretTapCounter = SecretTapCounter()): List<SecretTapResult> {
        var counter = from
        return List(times) { i ->
            counter.register(START + i * gapMs).also { counter = it.counter }
        }
    }

    @Test
    fun `five quick taps open the game and reset the counter`() {
        val results = tap(T.TAPS_TO_OPEN, gapMs = 300L)
        assertEquals(listOf(false, false, false, false, true), results.map { it.triggered })
        assertEquals(0, results.last().counter.count)
    }

    @Test
    fun `a slow tap restarts the count`() {
        val results = tap(T.TAPS_TO_OPEN, gapMs = T.TAP_WINDOW_MS + 1)
        assertTrue(results.none { it.triggered })
        assertEquals(1, results.last().counter.count)
    }

    @Test
    fun `the window edge still counts`() {
        val results = tap(T.TAPS_TO_OPEN, gapMs = T.TAP_WINDOW_MS)
        assertTrue(results.last().triggered)
    }

    @Test
    fun `four taps do not open`() {
        assertFalse(tap(T.TAPS_TO_OPEN - 1, gapMs = 100L).last().triggered)
    }

    private companion object {
        const val START = 10_000L
    }
}
