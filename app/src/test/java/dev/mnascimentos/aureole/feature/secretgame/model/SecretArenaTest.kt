package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.ui.geometry.Offset
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretArenaTest {

    @Test
    fun `phone windows keep the whole window as arena`() {
        listOf(360f to 772f, 412f to 915f, 360f to 640f).forEach { (w, h) ->
            assertEquals(SecretArena(0f, 0f, w, h), secretArenaOf(w, h))
        }
    }

    @Test
    fun `open foldable in landscape gets a centered column with the reference aspect`() {
        val arena = secretArenaOf(841f, 701f)
        assertEquals(T.REFERENCE_ASPECT, arena.aspect, EPS)
        assertEquals(701f, arena.height, EPS)
        assertEquals(701f / T.REFERENCE_ASPECT, arena.width, EPS)
        assertEquals(841f, arena.left * 2 + arena.width, EPS)
    }

    @Test
    fun `open foldable in portrait keeps the same arena aspect`() {
        val portrait = secretArenaOf(701f, 841f)
        assertEquals(secretArenaOf(841f, 701f).aspect, portrait.aspect, EPS)
        assertTrue(portrait.left > 0f)
    }

    @Test
    fun `layout is measured in arena widths, logo included`() {
        val arena = secretArenaOf(841f, 701f)
        val layout = secretLayoutOf(arena, SecretLogoPlacement(Offset(100f, 70f), arena.width / 3))
        assertEquals(arena.aspect, layout.aspect, EPS)
        assertEquals(arena.left / arena.width, layout.sideSpan, EPS)
        assertTrue("logo left of the column", layout.logoX < 0f)
        assertEquals(70f / arena.width, layout.logoY, EPS)
        assertEquals(1f / 3, layout.logoWidth, EPS)
    }

    @Test
    fun `phone layout has no sides`() {
        val layout = secretLayoutOf(secretArenaOf(360f, 772f), SecretLogoPlacement(Offset(180f, 150f), 120f))
        assertEquals(0f, layout.sideSpan, EPS)
        assertEquals(0.5f, layout.logoX, EPS)
    }

    @Test
    fun `same aspect only swaps the layout`() {
        val state = playing()
        val wider = state.layout.copy(sideSpan = 0.8f)
        val moved = state.rescaledTo(wider)
        assertEquals(wider, moved.layout)
        assertSame(state.faces, moved.faces)
        assertSame(state.stars, moved.stars)
    }

    @Test
    fun `new aspect keeps every height at the same fraction of the arena`() {
        val state = playing()
        val target = state.layout.copy(aspect = 1.94f)
        val ratio = 1.94f / state.layout.aspect
        val moved = state.rescaledTo(target)
        assertEquals(state.faces.first().y * ratio, moved.faces.first().y, EPS)
        assertEquals(state.stars.first().y * ratio, moved.stars.first().y, EPS)
        assertEquals(state.streaks.first().y * ratio, moved.streaks.first().y, EPS)
        assertEquals(state.particles.first().y * ratio, moved.particles.first().y, EPS)
        assertEquals(state.fireworks.rockets.first().targetY * ratio, moved.fireworks.rockets.first().targetY, EPS)
        assertEquals(state.faces.first().lane, moved.faces.first().lane)
    }

    private fun playing(): SecretGameState {
        val layout = SecretGameLayout(aspect = T.REFERENCE_ASPECT, logoX = 0.5f, logoY = 0.4f, logoWidth = 0.3f)
        val started = SecretGameEngine(Random(SEED)).start(layout, bestScore = 0)
        return started.copy(
            phase = SecretGamePhase.PLAY,
            faces = listOf(SecretFace(id = 0, lane = 2, y = 0.8f, kind = SecretFaceKind.DREAMER, wobble = 0f)),
            particles = listOf(SecretParticle(0.5f, 1.2f, 0f, 0.3f, 1f, 0.01f, SecretParticleKind.FACE)),
            fireworks = FireworksState(
                rockets = listOf(FireworkRocket(0.5f, 1.8f, targetY = 0.6f, vy = -1f, tone = FireworkTone.WHITE))
            ),
        )
    }

    private companion object {
        const val EPS = 0.0001f
        const val SEED = 7
    }
}
