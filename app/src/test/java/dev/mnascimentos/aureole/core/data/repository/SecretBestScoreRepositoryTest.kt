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
        repository.prefs.edit().remove("secret_game_best_score").commit()
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
}
