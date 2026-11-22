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
    fun `stars leave before the speed lines come in`() {
        val leaving = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.OUT_MS / 2)
        assertEquals(0.5f, leaving.starsAlpha(), EPS)
        assertEquals(0f, leaving.streaksAlpha(), EPS)
        val gone = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.OUT_MS)
        assertTrue(gone.toFrame().stars.isEmpty())
        val lines = at(SecretGamePhase.PLAY, playMs = SecretStarsTuning.OUT_MS + SecretStarsTuning.STREAKS_IN_MS)
        assertEquals(1f, lines.toFrame().streaksAlpha, EPS)
        assertEquals(1f, at(SecretGamePhase.PAUSED).streaksAlpha(), EPS)
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
