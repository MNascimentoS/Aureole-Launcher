package dev.mnascimentos.aureole.feature.secretgame.audio

import dev.mnascimentos.aureole.feature.secretgame.model.SecretSound
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SecretSoundPlayerTest {

    @Test
    fun `when playing every sound and releasing, should not fail`() {
        val player = SecretSoundPlayer(RuntimeEnvironment.getApplication())

        SecretSound.entries.forEach(player::play)
        player.release()
    }

    @Test
    fun `when disabled, should ignore play and release`() {
        val player = SecretSoundPlayer(RuntimeEnvironment.getApplication(), isEnabled = false)

        SecretSound.entries.forEach(player::play)
        player.release()
    }
}
