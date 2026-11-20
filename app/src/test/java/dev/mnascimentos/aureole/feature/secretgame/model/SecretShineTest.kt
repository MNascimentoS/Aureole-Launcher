package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineTuning as S

class SecretShineTest {

    @Test
    fun `when the star is outside the animation, scale is zero`() {
        assertEquals(0f, secretStarScale(0f), 0f)
        assertEquals(0f, secretStarScale(1f), 0f)
        assertEquals(0f, secretStarScale(-0.2f), 0f)
    }

    @Test
    fun `when the star reaches its peak, scale is the peak scale`() {
        assertEquals(S.STAR_PEAK_SCALE, secretStarScale(S.STAR_PEAK_AT), EPSILON)
    }

    @Test
    fun `when the star settles, scale is the settled scale`() {
        assertEquals(S.STAR_SETTLE_SCALE, secretStarScale(S.STAR_SETTLE_AT), EPSILON)
    }

    @Test
    fun `when the star is leaving, scale shrinks towards zero`() {
        assertTrue(secretStarScale(0.8f) < secretStarScale(S.STAR_SETTLE_AT))
        assertTrue(secretStarScale(0.95f) < secretStarScale(0.8f))
    }

    @Test
    fun `when the shine is cancelled, it hides and stays cancelled`() {
        val shine = SecretShineState(sweep = 0.4f, star = 0.3f)

        shine.cancel()

        assertEquals(0f, shine.sweep, 0f)
        assertEquals(0f, shine.star, 0f)
        assertTrue(shine.isCancelled)
    }

    @Test
    fun `when nobody played, should show the shine`() {
        assertTrue(shouldShowSecretShine(hasPlayed = false))
        assertFalse(shouldShowSecretShine(hasPlayed = true))
    }

    private companion object {
        const val EPSILON = 0.0001f
    }
}
