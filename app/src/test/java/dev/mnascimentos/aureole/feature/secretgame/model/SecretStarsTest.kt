package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretStarsTest {
    private val layout = SecretGameLayout(aspect = 2.144f, logoX = 0.5f, logoY = 0.425f, logoWidth = 0.33f)
    private val engine = SecretGameEngine(Random(SEED))
    private val started = engine.start(layout, bestScore = 0)
    private val framesFrom = T.HIDE_MS + T.TO_CENTER_MS + T.MORPH_MS + (T.DESCEND_MS * T.FRAMES_LEAD).toLong()

    private fun at(phase: SecretGamePhase, phaseMs: Long = 0L, playMs: Long = 0L) =
        started.copy(phase = phase, phaseMs = phaseMs, playMs = playMs, clockMs = phaseMs)

    @Test
    fun `start scatters the stars above the buttons`() {
        val bottom = layout.aspect - T.BUTTON_BOTTOM_OFFSET
        assertEquals(SecretStarsTuning.COUNT, started.stars.size)
        assertTrue(started.stars.all { it.x in 0f..1f && it.y in SecretStarsTuning.TOP..bottom })
    }

    @Test
    fun `stars light up together with the button frames`() {
        assertTrue(at(SecretGamePhase.ENTER, framesFrom - 1).toFrame().stars.isEmpty())
        val half = at(SecretGamePhase.ENTER, framesFrom + SecretStarsTuning.IN_MS / 2)
        assertEquals(0.5f, half.starsAlpha(), EPS)
        assertEquals(SecretStarsTuning.COUNT, half.toFrame().stars.size)
    }

    @Test
    fun `stars stay through intimidation and countdown and twinkle`() {
        assertEquals(1f, at(SecretGamePhase.INTIMIDATE).starsAlpha(), EPS)
        assertEquals(1f, at(SecretGamePhase.COUNTDOWN).starsAlpha(), EPS)
        val a = at(SecretGamePhase.COUNTDOWN, phaseMs = 0L).toFrame().stars.map { it.alpha }
        val b = at(SecretGamePhase.COUNTDOWN, phaseMs = TWINKLE_MS).toFrame().stars.map { it.alpha }
        assertTrue(a != b)
        assertTrue(a.all { it in 0f..SecretStarsTuning.ALPHA })
    }

    @Test
    fun `on GO the stars warp down, stretch into streaks and the speed lines take over`() {
        val start = at(SecretGamePhase.PLAY, playMs = 0L).toFrame().stars
        assertTrue(start.all { it.tail > 0f })
        val mid = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.WARP_MS / 2).toFrame()
        val pairs = started.stars.zip(mid.stars)
        assertTrue(pairs.all { (star, sprite) -> sprite.y > star.y })
        assertTrue(mid.stars.zip(start).all { (now, before) -> now.tail >= before.tail })
        val nearest = started.stars.indexOf(started.stars.maxBy { it.radius })
        val farthest = started.stars.indexOf(started.stars.minBy { it.radius })
        assertTrue(mid.stars[nearest].y - started.stars[nearest].y > mid.stars[farthest].y - started.stars[farthest].y)
        assertEquals(0f, at(SecretGamePhase.PLAY, playMs = 100L).streaksAlpha(), EPS)
        assertTrue(mid.streaksAlpha > 0f)
        val done = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.WARP_MS)
        assertTrue(done.toFrame().stars.isEmpty())
        assertEquals(1f, done.streaksAlpha(), EPS)
    }

    @Test
    fun `pausing during the warp freezes it`() {
        val play = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.WARP_MS / 3).toFrame()
        val paused = at(SecretGamePhase.PAUSED, playMs = SecretStarsTuning.WARP_MS / 3).toFrame()
        assertEquals(play.stars, paused.stars)
        assertEquals(play.streaksAlpha, paused.streaksAlpha, EPS)
    }

    @Test
    fun `no stars after the run`() {
        listOf(SecretGamePhase.EXPLODE, SecretGamePhase.RESULT, SecretGamePhase.EXIT, SecretGamePhase.DONE).forEach {
            assertTrue(at(it).toFrame().stars.isEmpty())
        }
    }

    private companion object {
        const val SEED = 9
        const val TWINKLE_MS = 400L
        const val EPS = 0.001f
    }
}
