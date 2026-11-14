package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val CLOCK_A = "clock_a"
private const val CLOCK_B = "clock_b"
private const val SAVES_IN_A_ROW = 20

@RunWith(RobolectricTestRunner::class)
class ClockConfigRepositoryTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var gridRepository: GridRepository
    private lateinit var repository: ClockConfigRepository

    private val clockPrefs
        get() = context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        clockPrefs.edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.resetClockSettings()
        gridRepository = GridRepository(context)
        // A home without clocks, so only what each test saves is there.
        gridRepository.saveGridItems(emptyList(), isLandscape = false)
        gridRepository.saveGridItems(emptyList(), isLandscape = true)
        repository = ClockConfigRepository(context, gridRepository, settingsRepository)
    }

    @Test
    fun `when saving two clocks, should read each one back on its own`() = runBlocking {
        val configA = ClockConfig(style = "MINIMAL", fontFamily = "SERIF")
        val configB = ClockConfig(alignment = "END", isBackgroundEnabled = true)

        repository.save(CLOCK_A, configA)
        repository.save(CLOCK_B, configB)

        assertEquals(configA, repository.get(CLOCK_A))
        assertEquals(configB, repository.get(CLOCK_B))
    }

    @Test
    fun `when saving one clock again, should not change the other`() = runBlocking {
        val configB = ClockConfig(style = "GREETING")
        repository.save(CLOCK_A, ClockConfig())
        repository.save(CLOCK_B, configB)

        repository.save(CLOCK_A, ClockConfig(textColor = 1))

        assertEquals(ClockConfig(textColor = 1), repository.get(CLOCK_A))
        assertEquals(configB, repository.get(CLOCK_B))
    }

    @Test
    fun `when saving one clock many times at once, should publish and keep the last config`() = runBlocking {
        repository.ensureMigrated()
        val saves = (1..SAVES_IN_A_ROW).map { color ->
            launch(Dispatchers.IO) { repository.save(CLOCK_A, ClockConfig(textColor = color)) }
        }
        saves.joinAll()
        val last = repository.configs.value.getValue(CLOCK_A)

        val restarted = ClockConfigRepository(context, gridRepository, settingsRepository)

        assertEquals(mapOf(CLOCK_A to last), restarted.getAll())
    }

    @Test
    fun `when saving, should publish the config before it is written`() = runBlocking {
        repository.ensureMigrated()

        val save = launch(Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            repository.save(CLOCK_A, ClockConfig(style = "MINIMAL"))
        }

        assertEquals("MINIMAL", repository.configs.value.getValue(CLOCK_A).style)
        save.join()
    }

    @Test
    fun `when saved, should keep the config across a restart`() = runBlocking {
        val config = ClockConfig(customGreeting = "Bom dia", backgroundColor = 2)
        repository.save(CLOCK_A, config)

        val restarted = ClockConfigRepository(context, gridRepository, settingsRepository)

        assertEquals(mapOf(CLOCK_A to config), restarted.getAll())
    }

    @Test
    fun `when removing a clock, should delete only its config`() = runBlocking {
        repository.save(CLOCK_A, ClockConfig(style = "MINIMAL"))
        repository.save(CLOCK_B, ClockConfig(style = "GREETING"))

        repository.remove(CLOCK_A)

        assertNull(repository.get(CLOCK_A))
        assertEquals(ClockConfig(style = "GREETING"), repository.get(CLOCK_B))
        val restarted = ClockConfigRepository(context, gridRepository, settingsRepository)
        assertEquals(setOf(CLOCK_B), restarted.getAll().keys)
    }

    @Test
    fun `when a clock has no config, should give the settings look as its default`() = runBlocking {
        settingsRepository.clockStyle = "MINIMAL"
        settingsRepository.isClockBackgroundEnabled = true

        val config = repository.getOrDefault(CLOCK_A)

        assertEquals(ClockConfig(style = "MINIMAL", isBackgroundEnabled = true), config)
        assertEquals(config, repository.defaultConfig())
    }

    @Test
    fun `when saving and removing, should publish the configs`() = runBlocking {
        repository.save(CLOCK_A, ClockConfig(style = "MINIMAL"))
        repository.save(CLOCK_B, ClockConfig())
        repository.remove(CLOCK_B)

        assertEquals(mapOf(CLOCK_A to ClockConfig(style = "MINIMAL")), repository.configs.value)
    }

    @Test
    fun `when a saved config cannot be read, should skip it and keep the others`() = runBlocking {
        clockPrefs.edit()
            .putString("clock_config_$CLOCK_A", "not json")
            .putString("clock_config_$CLOCK_B", "{\"fontFamily\":\"SERIF\"}")
            .putInt("unrelated_key", 1)
            .commit()

        assertEquals(mapOf(CLOCK_B to ClockConfig(fontFamily = "SERIF")), repository.getAll())
    }
}
