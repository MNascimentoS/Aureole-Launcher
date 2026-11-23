package dev.mnascimentos.aureole.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class SecretBestScoreRepositoryTest {

    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        repository = SettingsRepository(RuntimeEnvironment.getApplication())
        repository.prefs.edit()
            .remove("secret_game_best_score")
            .remove("secret_game_played")
            .remove("secret_game_sound_on")
            .commit()
    }

    @Test
    fun `when nothing was played, best score is zero`() {
        assertEquals(0, repository.secretBestScore)
    }

    @Test
    fun `when score is higher, should save it`() {
        assertTrue(repository.updateSecretBestScore(120))
        assertEquals(120, repository.secretBestScore)
    }

    @Test
    fun `when score is not higher, should keep the best`() {
        repository.updateSecretBestScore(120)

        assertFalse(repository.updateSecretBestScore(120))
        assertFalse(repository.updateSecretBestScore(40))
        assertEquals(120, repository.secretBestScore)
    }

    @Test
    fun `when nobody played and there is no record, played is false`() {
        assertFalse(repository.secretGamePlayed)
    }

    @Test
    fun `when there is a saved record, played defaults to true`() {
        repository.updateSecretBestScore(50)

        assertTrue(repository.secretGamePlayed)
    }

    @Test
    fun `when played is saved, should keep it`() {
        repository.secretGamePlayed = true

        assertTrue(repository.secretGamePlayed)
    }

    @Test
    fun `when sound was never changed, it is on and can be turned off`() {
        assertTrue(repository.secretSoundOn)

        repository.secretSoundOn = false

        assertFalse(repository.secretSoundOn)
    }
}
