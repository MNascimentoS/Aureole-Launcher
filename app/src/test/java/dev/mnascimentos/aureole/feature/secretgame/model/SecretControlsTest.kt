package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretControlsTest {
    private val logo = SecretLogoPlacement(Offset(100f, 100f), 100f)

    private fun layoutFor(width: Float, height: Float, density: Float = 1f) =
        secretLayoutOf(secretArenaOf(width, height), logo, density)

    @Test
    fun `phone keeps the frames under the arena, where they always were`() {
        val layout = layoutFor(360f, 772f, density = 3f)
        assertFalse(layout.buttonsOnSides)
        val y = layout.aspect - T.BUTTON_BOTTOM_OFFSET - T.BUTTON_SIZE / 2f
        assertEquals(Offset(T.BUTTON_SIDE_INSET, y), secretButtonTopLeft(true, layout))
        assertEquals(Offset(1f - T.BUTTON_SIDE_INSET - T.BUTTON_SIZE, y), secretButtonTopLeft(false, layout))
        assertEquals(0f, secretButtonCornerX(true, layout), EPS)
        assertEquals(1f, secretButtonCornerX(false, layout), EPS)
    }

    @Test
    fun `open foldable puts each frame in the middle of its side`() {
        listOf(841f to 701f, 701f to 841f).forEach { (w, h) ->
            val layout = layoutFor(w, h)
            assertTrue(layout.buttonsOnSides)
            val left = secretButtonTopLeft(true, layout).x + T.BUTTON_SIZE / 2f
            val right = secretButtonTopLeft(false, layout).x + T.BUTTON_SIZE / 2f
            assertEquals(-layout.sideSpan / 2f, left, EPS)
            assertEquals(1f + layout.sideSpan / 2f, right, EPS)
            assertEquals(-layout.sideSpan, secretButtonCornerX(true, layout), EPS)
            assertEquals(1f + layout.sideSpan, secretButtonCornerX(false, layout), EPS)
        }
    }

    @Test
    fun `narrow sides keep the frames under the arena`() {
        val layout = layoutFor(300f, 260f)
        assertTrue(layout.sideSpan > 0f)
        assertFalse(layout.buttonsOnSides)
        assertEquals(T.BUTTON_SIDE_INSET, secretButtonTopLeft(true, layout).x, EPS)
    }

    @Test
    fun `side width is measured in dp`() {
        assertTrue(layoutFor(300f * 3, 260f * 3, density = 1f).buttonsOnSides)
        assertFalse(layoutFor(300f * 3, 260f * 3, density = 3f).buttonsOnSides)
    }

    @Test
    fun `keys map to moves and pause`() {
        assertEquals(SecretKeyAction.LEFT, secretKeyAction(Key.DirectionLeft))
        assertEquals(SecretKeyAction.LEFT, secretKeyAction(Key.A))
        assertEquals(SecretKeyAction.RIGHT, secretKeyAction(Key.DirectionRight))
        assertEquals(SecretKeyAction.RIGHT, secretKeyAction(Key.D))
        assertEquals(SecretKeyAction.PAUSE, secretKeyAction(Key.Spacebar))
        assertNull(secretKeyAction(Key.Enter))
    }

    @Test
    fun `keys move, pause and resume the game`() {
        val engine = SecretGameEngine(Random(SEED))
        val playing = engine.start(layoutFor(841f, 701f), bestScore = 0).copy(phase = SecretGamePhase.PLAY)
        assertEquals(0, engine.onKey(playing, SecretKeyAction.LEFT).state.lane)
        assertEquals(2, engine.onKey(playing, SecretKeyAction.RIGHT).state.lane)
        val paused = engine.onKey(playing, SecretKeyAction.PAUSE).state
        assertEquals(SecretGamePhase.PAUSED, paused.phase)
        assertEquals(SecretGamePhase.PLAY, engine.onKey(paused, SecretKeyAction.PAUSE).state.phase)
        val entering = playing.copy(phase = SecretGamePhase.ENTER)
        assertEquals(entering, engine.onKey(entering, SecretKeyAction.PAUSE).state)
    }

    private companion object {
        const val EPS = 0.0001f
        const val SEED = 3
    }
}
