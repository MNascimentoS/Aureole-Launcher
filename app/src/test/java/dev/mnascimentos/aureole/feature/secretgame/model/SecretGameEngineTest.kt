package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksTuning
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

class SecretGameEngineTest {
    private val layout = SecretGameLayout(aspect = 2.144f, logoX = 0.5f, logoY = 0.425f, logoWidth = 0.33f)
    private val engine = SecretGameEngine(Random(SEED))

    private fun run(state: SecretGameState, ms: Long): SecretGameState {
        var s = state
        var left = ms
        while (left > 0) {
            val dt = minOf(STEP, left)
            s = engine.step(s, dt).state
            left -= dt
        }
        return s
    }

    private fun playing(best: Int = 0): SecretGameState =
        run(engine.start(layout, best), T.ENTER_TOTAL_MS + T.INTIMIDATE_MS + T.COUNTDOWN_MS + STEP * 2)

    @Test
    fun `start begins in ENTER on the middle lane with the saved best`() {
        val s = engine.start(layout, bestScore = 120)
        assertEquals(SecretGamePhase.ENTER, s.phase)
        assertEquals(1, s.lane)
        assertEquals(120, s.bestScore)
        assertEquals(T.STREAK_COUNT, s.streaks.size)
        assertTrue(s.faces.isEmpty())
    }

    @Test
    fun `timeline goes ENTER, INTIMIDATE with two faces, COUNTDOWN, PLAY`() {
        val start = engine.start(layout, 0)
        val intimidate = run(start, T.ENTER_TOTAL_MS + STEP)
        assertEquals(SecretGamePhase.INTIMIDATE, intimidate.phase)
        assertEquals(listOf(0, T.LANE_COUNT - 1), intimidate.faces.map { it.lane })
        assertEquals(setOf(SecretFaceKind.CREATOR, SecretFaceKind.DREAMER), intimidate.faces.map { it.kind }.toSet())
        val countdown = run(intimidate, T.INTIMIDATE_MS + STEP)
        assertEquals(SecretGamePhase.COUNTDOWN, countdown.phase)
        assertEquals(T.INTIMIDATE_TO_Y, countdown.faces.first().y, EPS)
        assertEquals(SecretGamePhase.PLAY, run(countdown, T.COUNTDOWN_MS + STEP).phase)
    }

    @Test
    fun `intimidation faces become the first enemies`() {
        val s = playing()
        assertTrue(s.faces.any { it.id == 0 } && s.faces.any { it.id == 1 })
    }

    @Test
    fun `move changes lane, tilts and ticks only while playing`() {
        val s = playing()
        val left = engine.move(s, -1)
        assertEquals(0, left.state.lane)
        assertEquals(0L, left.state.leftPressMs)
        assertTrue(engine.step(left.state, STEP).state.tilt < 0f)
        assertEquals(listOf(SecretGameEvent.LaneTick), left.events)
        val edge = engine.move(left.state.copy(leftPressMs = BIG.toLong()), -1)
        assertEquals(0, edge.state.lane)
        assertEquals(0L, edge.state.leftPressMs)
        assertTrue(edge.events.isEmpty())
        val entering = engine.move(engine.start(layout, 0), 1)
        assertEquals(1, entering.state.lane)
        assertTrue(entering.events.isEmpty())
    }

    @Test
    fun `the lean is small and settles smoothly`() {
        var s = engine.move(playing(), 1).state
        var peak = 0f
        repeat(LEAN_STEPS) {
            s = engine.step(s.copy(spawnCooldownMs = BIG, faces = emptyList()), STEP).state
            peak = maxOf(peak, s.tilt)
        }
        assertTrue(peak in MIN_PEAK_LEAN..MAX_PEAK_LEAN)
        assertTrue(s.tilt < 0.01f)
    }

    @Test
    fun `ship glides to the lane centre`() {
        val s = run(engine.move(playing(), 1).state, LONG_MS)
        assertEquals(laneX(2), s.shipX, EPS)
    }

    @Test
    fun `each face that passes the bottom gives 10 points`() {
        val far = playing().copy(faces = listOf(face(id = 99, lane = 0, y = layout.aspect + T.PASS_MARGIN - 0.001f)))
        val s = engine.step(far.copy(spawnCooldownMs = BIG), STEP).state
        assertEquals(T.POINTS_PER_FACE, s.score)
        assertTrue(s.faces.none { it.id == 99 })
    }

    @Test
    fun `hitting a face ends the run with crash and game over events`() {
        val s = playing(best = 50).copy(score = 80, spawnCooldownMs = BIG)
        val crash = engine.step(s.copy(faces = listOf(face(7, s.lane, s.shipY))), STEP)
        assertEquals(SecretGamePhase.EXPLODE, crash.state.phase)
        assertEquals(7, crash.state.crashedFaceId)
        assertTrue(crash.state.isNewRecord)
        assertEquals(80, crash.state.bestScore)
        assertEquals(listOf(SecretGameEvent.Crash, SecretGameEvent.GameOver(80)), crash.events)
    }

    @Test
    fun `a lower score is not a record and keeps the best`() {
        val s = playing(best = 500).copy(score = 30, spawnCooldownMs = BIG)
        val crash = engine.step(s.copy(faces = listOf(face(7, s.lane, s.shipY))), STEP).state
        assertFalse(crash.isNewRecord)
        assertEquals(500, crash.bestScore)
    }

    @Test
    fun `a face in another lane does not hit`() {
        val s = playing().copy(spawnCooldownMs = BIG)
        val other = (s.lane + 1) % T.LANE_COUNT
        val next = engine.step(s.copy(faces = listOf(face(7, other, s.shipY))), STEP).state
        assertEquals(SecretGamePhase.PLAY, next.phase)
    }

    @Test
    fun `explosion bursts once, then result, exit and done`() {
        val s = playing().copy(spawnCooldownMs = BIG)
        val exploding = engine.step(s.copy(faces = listOf(face(7, s.lane, s.shipY))), STEP).state
        val boomed = run(exploding, T.BOOM_AT_MS + STEP)
        assertTrue(boomed.hasExploded)
        assertTrue(boomed.particles.size >= T.SHIP_PARTICLES)
        val result = run(boomed, T.EXPLODE_TOTAL_MS)
        assertEquals(SecretGamePhase.RESULT, result.phase)
        assertTrue(result.faces.isEmpty())
        val exit = run(result, T.RESULT_TOTAL_MS + STEP)
        assertEquals(SecretGamePhase.EXIT, exit.phase)
        val done = run(exit, T.EXIT_MS + STEP)
        assertEquals(SecretGamePhase.DONE, done.phase)
        assertFalse(done.isRunning)
        assertEquals(done, engine.step(done, STEP).state)
    }

    @Test
    fun `back pauses, tap resumes, back while paused exits saving the score`() {
        val s = playing().copy(score = 40)
        val paused = engine.onBack(s).state
        assertEquals(SecretGamePhase.PAUSED, paused.phase)
        assertEquals(paused, engine.step(paused, STEP).state)
        assertEquals(SecretGamePhase.PLAY, engine.onTap(paused).phase)
        val exit = engine.onBack(paused)
        assertEquals(SecretGamePhase.EXIT, exit.state.phase)
        assertEquals(listOf(SecretGameEvent.GameOver(40)), exit.events)
        assertTrue(exit.state.isNewRecord)
    }

    @Test
    fun `back on the result screen exits right away`() {
        val result = playing().copy(score = 40).to(SecretGamePhase.RESULT)
        assertEquals(SecretGamePhase.EXIT, engine.onBack(result).state.phase)
    }

    @Test
    fun `back during animations is consumed without effect`() {
        val entering = engine.start(layout, 0)
        assertEquals(entering, engine.onBack(entering).state)
        assertEquals(entering, engine.onTap(entering))
    }

    @Test
    fun `lifecycle pause only pauses a running game`() {
        assertEquals(SecretGamePhase.PAUSED, engine.onLifecyclePause(playing()).phase)
        val entering = engine.start(layout, 0)
        assertEquals(entering, engine.onLifecyclePause(entering))
    }

    @Test
    fun `waves never fill three lanes and keep an exit next to the previous one`() {
        var s = playing().copy(score = HIGH_SCORE, faces = emptyList())
        repeat(WAVES) {
            val before = s.nextFaceId
            val lastSafe = s.lastSafeLane
            s = engine.step(s.copy(spawnCooldownMs = 0f, faces = emptyList()), 1L).state
            val lanes = s.faces.filter { it.id >= before }.map { it.lane }
            assertTrue(lanes.size in 1..2)
            assertFalse(s.lastSafeLane in lanes)
            assertTrue(kotlin.math.abs(s.lastSafeLane - lastSafe) <= 1)
        }
    }

    @Test
    fun `crossing 100 points levels up once and flashes the hud`() {
        val s = playing().copy(score = 90, spawnCooldownMs = BIG)
        val passing = s.copy(faces = listOf(face(99, 0, layout.aspect + T.PASS_MARGIN - 0.001f)))
        val step = engine.step(passing, STEP)
        assertEquals(listOf(SecretGameEvent.LevelUp(1)), step.events)
        assertEquals(T.LEVEL_FLASH_MS, step.state.levelFlashMs)
        val later = run(step.state, T.LEVEL_FLASH_MS)
        assertEquals(0L, later.levelFlashMs)
    }

    @Test
    fun `ship reaches the lane in about 90 ms`() {
        assertTrue(1f - secretShipFollow(0.1f) < 0.01f)
        assertTrue(secretTiltChase(0.5f) > 0.99f)
    }

    @Test
    fun `particles fall and fade out`() {
        val p = SecretParticle(0.5f, 1f, 0f, 0f, life = 1f, radius = 0.01f, kind = SecretParticleKind.SHIP)
        val moved = listOf(p).advance(0.1f).single()
        assertTrue(moved.y > p.y && moved.life < 1f)
        assertTrue(listOf(p).advance(2f).isEmpty())
    }

    @Test
    fun `a new record launches six fireworks during the result`() {
        val s = playing(best = 10).copy(score = 50, spawnCooldownMs = BIG)
        var step = engine.step(s.copy(faces = listOf(face(7, s.lane, s.shipY))), STEP)
        assertEquals(FireworksTuning.ROCKETS, step.state.fireworks.left)
        var bursts = 0
        while (step.state.phase != SecretGamePhase.DONE) {
            step = engine.step(step.state, STEP)
            bursts += step.events.count { it == SecretGameEvent.FireworkBurst }
        }
        assertEquals(FireworksTuning.ROCKETS, bursts)
        assertTrue(step.state.fireworks.isIdle)
    }

    @Test
    fun `no fireworks without a record`() {
        val s = playing(best = 500).copy(score = 50, spawnCooldownMs = BIG)
        var step = engine.step(s.copy(faces = listOf(face(7, s.lane, s.shipY))), STEP)
        var bursts = 0
        while (step.state.phase != SecretGamePhase.DONE) {
            step = engine.step(step.state, STEP)
            bursts += step.events.count { it == SecretGameEvent.FireworkBurst }
        }
        assertEquals(0, bursts)
    }

    @Test
    fun `explode without the crashed face still bursts the ship`() {
        val s = playing().copy(crashedFaceId = null).explode(Random(SEED))
        assertEquals(T.SHIP_PARTICLES, s.particles.size)
        assertNull(s.crashedFaceId)
    }

    private fun face(id: Int, lane: Int, y: Float) = SecretFace(id, lane, y, SecretFaceKind.CREATOR, wobble = 0f)

    private companion object {
        const val SEED = 7
        const val STEP = 16L
        const val LONG_MS = 3000L
        const val BIG = 1_000_000f
        const val HIGH_SCORE = 3000
        const val LEAN_STEPS = 25
        const val MIN_PEAK_LEAN = 0.05f
        const val MAX_PEAK_LEAN = 0.12f
        const val WAVES = 200
        const val EPS = 0.001f
    }
}
