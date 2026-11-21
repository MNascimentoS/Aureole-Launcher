package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

/** Dificuldade por nível: 1 nível a cada [POINTS_PER_LEVEL] pontos. Velocidade em alturas de tela por segundo. */
object SecretDifficulty {
    const val POINTS_PER_LEVEL = 100
    const val SPEED_BASE = 0.26f
    const val SPEED_GROWTH = 1.08f
    const val SPEED_MAX = 2.5f
    const val SPAWN_BASE_MS = 1250f
    const val SPAWN_DECAY = 0.94f
    const val SPAWN_MIN_MS = 300f
    const val DOUBLE_FROM_LEVEL = 3
    const val DOUBLE_BASE = 0.12f
    const val DOUBLE_GROWTH = 0.03f
    const val DOUBLE_MAX = 0.4f
}

/** Faixas bloqueadas da leva e a saída que ela deixa. */
data class SecretWave(val blocked: List<Int>, val safeLane: Int)

fun secretLevel(score: Int): Int = score / SecretDifficulty.POINTS_PER_LEVEL

fun secretFallSpeed(level: Int): Float =
    minOf(SecretDifficulty.SPEED_BASE * SecretDifficulty.SPEED_GROWTH.pow(level), SecretDifficulty.SPEED_MAX)

fun secretSpawnInterval(level: Int): Float =
    maxOf(SecretDifficulty.SPAWN_BASE_MS * SecretDifficulty.SPAWN_DECAY.pow(level), SecretDifficulty.SPAWN_MIN_MS)

fun secretDoubleChance(level: Int): Float {
    val extra = level - SecretDifficulty.DOUBLE_FROM_LEVEL
    if (extra < 0) return 0f
    return minOf(SecretDifficulty.DOUBLE_BASE + SecretDifficulty.DOUBLE_GROWTH * extra, SecretDifficulty.DOUBLE_MAX)
}

/**
 * Leva justa: a saída fica a no máximo 1 faixa da saída da leva anterior ([lastSafe]), onde o jogador precisa
 * estar para sobreviver a ela. Com 2 bloqueados sobra exatamente essa saída; com 1, a saída é a livre mais perto.
 */
fun nextSecretWave(lastSafe: Int, level: Int, random: Random): SecretWave {
    val lanes = (0 until SecretGameTuning.LANE_COUNT).toList()
    if (random.nextFloat() < secretDoubleChance(level)) {
        val safe = lanes.filter { abs(it - lastSafe) <= 1 }.random(random)
        return SecretWave(lanes.filter { it != safe }, safe)
    }
    val blocked = lanes.random(random)
    val free = lanes.filter { it != blocked }
    val nearest = free.minOf { abs(it - lastSafe) }
    return SecretWave(listOf(blocked), free.filter { abs(it - lastSafe) == nearest }.random(random))
}
