package dev.mnascimentos.aureole.composable.fireworks.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksTuning as F
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class FireworksTest {
    private val random = Random(SEED)

    private fun play(show: FireworksState, ms: Long): Pair<FireworksState, Int> {
        var fw = show
        var bursts = 0
        repeat((ms / STEP).toInt()) {
            val step = fw.step(STEP, ASPECT, random)
            fw = step.fireworks
            bursts += step.bursts
        }
        return fw to bursts
    }

    @Test
    fun `first rocket waits, then climbs from below the screen`() {
        val (waiting, _) = play(fireworksShow(), (F.FIRST_MS - STEP * 2).toLong())
        assertTrue(waiting.rockets.isEmpty())
        val (launched, _) = play(waiting, STEP * 3)
        val rocket = launched.rockets.single()
        assertTrue(rocket.vy < 0f)
        assertTrue(rocket.x in F.X_MIN..F.X_MIN + F.X_SPAN)
        assertTrue(rocket.targetY in F.TARGET_MIN_Y..F.TARGET_MIN_Y + ASPECT * F.TARGET_SPAN_OF_ASPECT)
    }

    @Test
    fun `every rocket bursts into a ring plus a white core`() {
        val (_, bursts) = play(FireworksState(left = 1), (F.CLIMB_S * 1000).toLong() + STEP * 3)
        assertEquals(1, bursts)
    }

    @Test
    fun `a show bursts six times and then goes idle`() {
        val (fw, bursts) = play(fireworksShow(), SHOW_MS)
        assertEquals(F.ROCKETS, bursts)
        assertTrue(fw.isIdle)
    }

    @Test
    fun `sparks slow down, fall and fade`() {
        val spark = FireworkSpark(0.5f, 0.5f, 0.3f, 0f, life = 1f, radius = 0.005f, tone = FireworkTone.PRIMARY)
        val moved = listOf(spark).advanceSparks(0.1f).single()
        assertTrue(moved.vx < spark.vx && moved.vy > 0f && moved.life < 1f)
        assertTrue(listOf(spark).advanceSparks(1f).isEmpty())
    }

    private companion object {
        const val SEED = 5
        const val STEP = 16L
        const val ASPECT = 2.144f
        const val SHOW_MS = 6000L
    }
}
