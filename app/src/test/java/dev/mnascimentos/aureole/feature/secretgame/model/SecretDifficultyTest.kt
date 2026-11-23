package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

class SecretDifficultyTest {

    @Test
    fun `level goes up every 100 points`() {
        assertEquals(0, secretLevel(0))
        assertEquals(0, secretLevel(90))
        assertEquals(1, secretLevel(100))
        assertEquals(13, secretLevel(1360))
    }

    @Test
    fun `speed starts slow, grows 8 percent per level and stops at the cap`() {
        assertEquals(SecretDifficulty.SPEED_BASE, secretFallSpeed(0), EPS)
        assertEquals(SecretDifficulty.SPEED_BASE * SecretDifficulty.SPEED_GROWTH, secretFallSpeed(1), EPS)
        assertTrue((1..40).all { secretFallSpeed(it) >= secretFallSpeed(it - 1) })
        assertEquals(SecretDifficulty.SPEED_MAX, secretFallSpeed(100), EPS)
    }

    @Test
    fun `spawn interval shrinks per level down to the floor`() {
        assertEquals(SecretDifficulty.SPAWN_BASE_MS, secretSpawnInterval(0), EPS)
        assertTrue(secretSpawnInterval(5) < secretSpawnInterval(4))
        assertEquals(SecretDifficulty.SPAWN_MIN_MS, secretSpawnInterval(100), EPS)
    }

    @Test
    fun `double waves only from level 3 and capped`() {
        assertEquals(0f, secretDoubleChance(2), EPS)
        assertEquals(SecretDifficulty.DOUBLE_BASE, secretDoubleChance(3), EPS)
        assertEquals(SecretDifficulty.DOUBLE_MAX, secretDoubleChance(100), EPS)
    }

    @Test
    fun `every wave leaves an exit at most one lane from the previous exit`() {
        val random = Random(SEED)
        var lastSafe = 1
        var doubles = 0
        repeat(WAVES) {
            val wave = nextSecretWave(lastSafe, level = HIGH_LEVEL, random = random)
            assertTrue(wave.blocked.size in 1..2)
            assertFalse(wave.safeLane in wave.blocked)
            assertTrue(abs(wave.safeLane - lastSafe) <= 1)
            assertEquals(wave.blocked.size, wave.blocked.distinct().size)
            if (wave.blocked.size == 2) doubles++
            lastSafe = wave.safeLane
        }
        assertTrue(doubles > 0)
    }

    @Test
    fun `single wave keeps the exit when the previous exit is free`() {
        val random = Random(SEED)
        repeat(WAVES) {
            val wave = nextSecretWave(lastSafe = 0, level = 0, random = random)
            assertEquals(1, wave.blocked.size)
            if (wave.blocked.single() != 0) assertEquals(0, wave.safeLane)
        }
    }

    private companion object {
        const val SEED = 11
        const val WAVES = 3000
        const val HIGH_LEVEL = 30
        const val EPS = 0.0001f
    }
}
