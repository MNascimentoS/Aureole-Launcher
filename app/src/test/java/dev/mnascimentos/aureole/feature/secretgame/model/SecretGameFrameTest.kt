package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretGameFrameTest {
    private val layout = SecretGameLayout(aspect = 2.144f, logoX = 0.5f, logoY = 0.425f, logoWidth = 0.33f)
    private val base = SecretGameState(layout = layout)

    private fun at(phase: SecretGamePhase, ms: Long, state: SecretGameState = base) =
        state.copy(phase = phase, phaseMs = ms, clockMs = ms).toFrame()

    @Test
    fun `enter starts on the settings logo and hides settings`() {
        val f = at(SecretGamePhase.ENTER, 0L)
        assertEquals(1f, f.settingsAlpha, EPS)
        assertFalse(f.settingsLogoVisible)
        val logo = assertNotNullAndGet(f.logo)
        assertEquals(layout.logoY, logo.y, EPS)
        assertEquals(layout.logoWidth, logo.width, EPS)
        assertNull(f.ship)
        assertEquals(0f, at(SecretGamePhase.ENTER, T.HIDE_MS).settingsAlpha, EPS)
    }

    @Test
    fun `logo turns into the ship with a glow, then the ship lands and frames draw`() {
        val mid = at(SecretGamePhase.ENTER, T.HIDE_MS + T.TO_CENTER_MS + T.MORPH_MS / 2)
        assertNotNull(mid.ship)
        assertNotNull(mid.glow)
        val end = at(SecretGamePhase.ENTER, T.ENTER_TOTAL_MS)
        assertNull(end.logo)
        val ship = assertNotNullAndGet(end.ship)
        assertEquals(T.SHIP_WIDTH, ship.width, EPS)
        assertEquals(base.shipY, ship.y, T.BOB_AMPLITUDE + EPS)
        assertEquals(1f, assertNotNullAndGet(end.buttons).draw, EPS)
        assertTrue(end.flame > 0f)
    }

    @Test
    fun `intimidation fades faces in and keeps settings hidden`() {
        val s = base.copy(faces = listOf(SecretFace(0, 0, 0.1f, SecretFaceKind.CREATOR, 0f)))
        val f = at(SecretGamePhase.INTIMIDATE, T.INTIMIDATE_FADE_MS / 2, s)
        assertEquals(0f, f.settingsAlpha, EPS)
        assertEquals(0.5f, f.faces.single().sprite.alpha, EPS)
        assertNull(f.countdown)
        assertNull(f.hudScore)
    }

    @Test
    fun `countdown shows 3, 2, 1 and GO`() {
        val labels = (0 until T.COUNT_STEPS).map {
            assertNotNullAndGet(at(SecretGamePhase.COUNTDOWN, it * T.COUNT_STEP_MS + 1).countdown).label
        }
        assertEquals(SecretCountdownLabel.entries.toList(), labels)
        val fading = assertNotNullAndGet(at(SecretGamePhase.COUNTDOWN, T.COUNT_STEP_MS - 1).countdown)
        assertTrue(fading.alpha < 0.1f)
    }

    @Test
    fun `playing shows streaks and score, paused adds the overlay`() {
        val s = base.copy(score = 70, streaks = listOf(SecretStreak(0.1f, 0.2f, 0.1f)))
        val play = at(SecretGamePhase.PLAY, 10L, s)
        assertEquals(70, play.hudScore)
        assertEquals(1, play.streaks.size)
        assertFalse(play.isPaused)
        assertTrue(at(SecretGamePhase.PAUSED, 10L, s).isPaused)
    }

    @Test
    fun `faces only trail while falling and the trail grows with the level`() {
        val face = SecretFace(0, 0, 0.5f, SecretFaceKind.CREATOR, 0f)
        val s = base.copy(faces = listOf(face))
        assertEquals(0f, at(SecretGamePhase.COUNTDOWN, 10L, s).faces.single().trail, EPS)
        assertEquals(0f, at(SecretGamePhase.INTIMIDATE, 10L, s).faces.single().trail, EPS)
        val slow = at(SecretGamePhase.PLAY, 10L, s).faces.single().trail
        val fast = at(SecretGamePhase.PLAY, 10L, s.copy(score = 2000)).faces.single().trail
        assertTrue(slow > 0f && fast > slow)
        assertEquals(slow, at(SecretGamePhase.PAUSED, 10L, s).faces.single().trail, EPS)
    }

    @Test
    fun `hud flashes after a level up and shows the next level`() {
        val f = at(SecretGamePhase.PLAY, 10L, base.copy(score = 120, levelFlashMs = T.LEVEL_FLASH_MS / 2))
        assertEquals(0.5f, f.hudFlash, EPS)
        assertEquals(2, f.levelLabel)
    }

    @Test
    fun `explosion shakes, flashes, removes the ship and collapses the frames`() {
        val s = base.copy(crashedFaceId = 3, faces = listOf(SecretFace(3, 1, 1f, SecretFaceKind.DREAMER, 0f)))
        assertNotNull(at(SecretGamePhase.EXPLODE, 10L, s).ship)
        val flash = at(SecretGamePhase.EXPLODE, T.FLASH_FROM_MS + T.FLASH_MS / 2, s)
        assertNotNull(flash.glow)
        assertNull(flash.ship)
        val end = at(SecretGamePhase.EXPLODE, T.EXPLODE_TOTAL_MS, s)
        assertEquals(1f, assertNotNullAndGet(end.buttons).collapse, EPS)
        assertEquals(0f, end.faces.single().sprite.alpha, EPS)
    }

    @Test
    fun `result fades in, holds and fades out`() {
        val s = base.copy(score = 150, isNewRecord = true)
        assertEquals(0f, assertNotNullAndGet(at(SecretGamePhase.RESULT, 0L, s).result).alpha, EPS)
        val hold = assertNotNullAndGet(at(SecretGamePhase.RESULT, T.RESULT_IN_MS + 1, s).result)
        assertEquals(1f, hold.alpha, EPS)
        assertEquals(150, hold.score)
        assertTrue(hold.isNewRecord)
        assertEquals(0f, assertNotNullAndGet(at(SecretGamePhase.RESULT, T.RESULT_TOTAL_MS, s).result).alpha, EPS)
    }

    @Test
    fun `exit brings settings and its logo back`() {
        val half = at(SecretGamePhase.EXIT, T.EXIT_MS / 2)
        assertTrue(half.settingsLogoVisible)
        assertEquals(0.5f, half.settingsAlpha, EPS)
        assertEquals(1f, at(SecretGamePhase.DONE, 0L).settingsAlpha, EPS)
    }

    private fun <V : Any> assertNotNullAndGet(value: V?): V {
        assertNotNull(value)
        return value!!
    }

    private companion object {
        const val EPS = 0.001f
    }
}
