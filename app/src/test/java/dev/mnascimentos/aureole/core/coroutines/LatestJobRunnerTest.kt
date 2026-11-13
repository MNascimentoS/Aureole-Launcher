package dev.mnascimentos.aureole.core.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LatestJobRunnerTest {

    @Test
    fun `when a second job starts while the first runs, should cancel the first and keep the last`() = runTest {
        val runner = LatestJobRunner(this)
        val applied = mutableListOf<String>()

        val first = runner.launch {
            delay(SLOW_MS)
            applied += "first"
        }
        runner.launch {
            delay(FAST_MS)
            applied += "second"
        }
        advanceUntilIdle()

        assertTrue(first.isCancelled)
        assertEquals(listOf("second"), applied)
    }

    @Test
    fun `when jobs run one after the other, should apply both`() = runTest {
        val runner = LatestJobRunner(this)
        val applied = mutableListOf<String>()

        runner.launch { applied += "first" }
        advanceUntilIdle()
        runner.launch { applied += "second" }
        advanceUntilIdle()

        assertEquals(listOf("first", "second"), applied)
    }

    private companion object {
        const val SLOW_MS = 100L
        const val FAST_MS = 10L
    }
}
