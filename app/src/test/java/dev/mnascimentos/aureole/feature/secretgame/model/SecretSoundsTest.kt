package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretSoundsTest {
    private val layout = SecretGameLayout(aspect = 2.144f, logoX = 0.5f, logoY = 0.425f, logoWidth = 0.33f)
    private val engine = SecretGameEngine(Random(SEED))

    @Test
    fun `every event maps to its sound`() {
        assertEquals(SecretSound.LANE, SecretGameEvent.LaneTick.sound())
        assertEquals(SecretSound.CRASH, SecretGameEvent.Crash.sound())
        assertEquals(SecretSound.FIREWORK, SecretGameEvent.FireworkBurst.sound())
        assertEquals(SecretSound.LEVEL, SecretGameEvent.LevelUp(2).sound())
        assertEquals(SecretSound.COUNT, SecretGameEvent.CountdownTick(SecretCountdownLabel.TWO).sound())
        assertEquals(SecretSound.GO, SecretGameEvent.CountdownTick(SecretCountdownLabel.GO).sound())
        assertEquals(SecretSound.RECORD, SecretGameEvent.ResultShown(isNewRecord = true).sound())
        assertEquals(SecretSound.LOSE, SecretGameEvent.ResultShown(isNewRecord = false).sound())
        assertNull(SecretGameEvent.GameOver(10).sound())
    }

    @Test
    fun `mute plays nothing`() {
        val events = listOf(SecretGameEvent.LaneTick, SecretGameEvent.Crash, SecretGameEvent.GameOver(0))
        assertEquals(listOf(SecretSound.LANE, SecretSound.CRASH), secretSoundsFor(events, isSoundOn = true))
        assertTrue(secretSoundsFor(events, isSoundOn = false).isEmpty())
    }

    @Test
    fun `a full run emits 3, 2, 1, GO once each and the result once`() {
        var step = SecretGameStep(engine.start(layout, bestScore = 0))
        val events = mutableListOf<SecretGameEvent>()
        while (step.state.phase != SecretGamePhase.PLAY) {
            step = engine.step(step.state, STEP)
            events += step.events
        }
        assertEquals(
            SecretCountdownLabel.entries.map { SecretGameEvent.CountdownTick(it) },
            events.filterIsInstance<SecretGameEvent.CountdownTick>(),
        )
        val s = step.state.copy(score = 40, spawnCooldownMs = BIG)
        step = engine.step(s.copy(faces = listOf(SecretFace(9, s.lane, s.shipY, SecretFaceKind.CREATOR, 0f))), STEP)
        val after = mutableListOf<SecretGameEvent>()
        while (step.state.phase != SecretGamePhase.DONE) {
            step = engine.step(step.state, STEP)
            after += step.events
        }
        val results = after.filterIsInstance<SecretGameEvent.ResultShown>()
        assertEquals(listOf(SecretGameEvent.ResultShown(isNewRecord = true)), results)
    }

    @Test
    fun `countdown label only exists during the countdown`() {
        val base = SecretGameState(layout = layout)
        assertNull(base.countdownLabel())
        val countdown = base.copy(phase = SecretGamePhase.COUNTDOWN, phaseMs = T.COUNT_STEP_MS * 3 + 1)
        assertEquals(SecretCountdownLabel.GO, countdown.countdownLabel())
    }

    @Test
    fun `shine only for who never played`() {
        assertTrue(shouldShowSecretShine(hasPlayed = false))
        assertFalse(shouldShowSecretShine(hasPlayed = true))
    }

    private companion object {
        const val SEED = 3
        const val STEP = 16L
        const val BIG = 1_000_000f
    }
}
