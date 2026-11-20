package dev.mnascimentos.aureole.feature.secretgame.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSound

private const val MAX_STREAMS = 6
private const val FULL_VOLUME = 1f
private const val PRIORITY = 1
private const val NO_LOOP = 0
private const val NORMAL_RATE = 1f

/**
 * Efeitos sonoros do jogo secreto num [SoundPool] (volume de mídia). Quem cria deve chamar [release] ao fechar o
 * jogo. Com [isEnabled] falso (previews do layoutlib, que não tem SoundPool) não carrega nada e [play] não faz
 * nada. Cada som é o `res/raw/secret_<nome em minúsculas>.ogg`.
 */
class SecretSoundPlayer(context: Context, isEnabled: Boolean = true) {
    private val pool = if (isEnabled) buildPool() else null
    private val ids = pool?.let { loaded ->
        SecretSound.entries.associateWith { loaded.load(context, it.rawRes(), PRIORITY) }
    }.orEmpty()

    fun play(sound: SecretSound) {
        val id = ids[sound] ?: return
        pool?.play(id, FULL_VOLUME, FULL_VOLUME, PRIORITY, NO_LOOP, NORMAL_RATE)
    }

    fun release() {
        pool?.release()
    }

    private fun buildPool(): SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
}

private fun SecretSound.rawRes(): Int = when (this) {
    SecretSound.OPEN -> R.raw.secret_open
    SecretSound.LANE -> R.raw.secret_lane
    SecretSound.COUNT -> R.raw.secret_count
    SecretSound.GO -> R.raw.secret_go
    SecretSound.LEVEL -> R.raw.secret_level
    SecretSound.CRASH -> R.raw.secret_crash
    SecretSound.FIREWORK -> R.raw.secret_firework
    SecretSound.RECORD -> R.raw.secret_record
    SecretSound.LOSE -> R.raw.secret_lose
}
