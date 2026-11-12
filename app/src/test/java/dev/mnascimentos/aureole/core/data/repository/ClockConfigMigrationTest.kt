package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val LANDSCAPE_ONLY_CLOCK = "clock_landscape_only"
private const val NESTED_CLOCK = "clock_in_scroll_view"
private const val TEXT_COLOR = 0xFF112233.toInt()
private const val BACKGROUND_COLOR = 0x80445566.toInt()

@RunWith(RobolectricTestRunner::class)
class ClockConfigMigrationTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var gridRepository: GridRepository

    private val customized = ClockConfig(
        style = "GREETING",
        customGreeting = "Olá",
        alignment = "START",
        fontFamily = "SERIF",
        timeFormat = "H24",
        dateFormat = "SHORT",
        textColor = TEXT_COLOR,
        backgroundColor = BACKGROUND_COLOR,
        isBackgroundEnabled = true
    )

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        gridRepository = GridRepository(context)
        gridRepository.resetToDefault()
        settingsRepository.resetClockSettings()
    }

    private fun newRepository() = ClockConfigRepository(context, gridRepository, settingsRepository)

    private fun saveGlobals(config: ClockConfig) {
        settingsRepository.clockStyle = config.style
        settingsRepository.clockCustomGreeting = config.customGreeting
        settingsRepository.clockAlignment = config.alignment
        settingsRepository.clockFontFamily = config.fontFamily
        settingsRepository.clockTimeFormat = config.timeFormat
        settingsRepository.clockDateFormat = config.dateFormat
        settingsRepository.clockTextColor = config.textColor
        settingsRepository.clockBackgroundColor = config.backgroundColor
        settingsRepository.isClockBackgroundEnabled = config.isBackgroundEnabled
    }

    private fun clock(id: String, col: Int = 0) =
        LauncherItemState(id = id, type = LauncherItemType.CLOCK, col = col, colSpan = 2, rowSpan = 2)

    @Test
    fun `when settings hold a clock look, should read every field of it`() {
        saveGlobals(customized)

        assertEquals(customized, settingsRepository.globalClockConfig())
    }

    @Test
    fun `when the settings were never changed, should give the factory clock`() {
        assertEquals(ClockConfig(), settingsRepository.globalClockConfig())
    }

    @Test
    fun `when updating with clocks in portrait and landscape, should copy the settings look to each of them`() =
        runBlocking {
            saveGlobals(customized)
            val scrollView = LauncherItemState(
                id = "scroll",
                type = LauncherItemType.SCROLL_VIEW,
                children = listOf(clock(NESTED_CLOCK))
            )
            gridRepository.saveGridItems(listOf(clock(GridDefaults.CLOCK_ITEM_ID), scrollView), isLandscape = false)
            gridRepository.saveGridItems(
                listOf(clock(GridDefaults.CLOCK_ITEM_ID), clock(LANDSCAPE_ONLY_CLOCK, col = 2)),
                isLandscape = true
            )

            val configs = newRepository().getAll()

            assertEquals(
                mapOf(
                    GridDefaults.CLOCK_ITEM_ID to customized,
                    NESTED_CLOCK to customized,
                    LANDSCAPE_ONLY_CLOCK to customized
                ),
                configs
            )
        }

    @Test
    fun `when the layout was never saved, should give the default clock the settings look`() = runBlocking {
        saveGlobals(customized)

        assertEquals(customized, newRepository().get(GridDefaults.CLOCK_ITEM_ID))
    }

    @Test
    fun `when migrating twice, should keep the first result`() = runBlocking {
        saveGlobals(customized)
        val first = newRepository().also { it.ensureMigrated() }.getAll()

        val second = newRepository().also {
            it.ensureMigrated()
            it.ensureMigrated()
        }.getAll()

        assertEquals(first, second)
    }

    @Test
    fun `when the settings look changes after the migration, should keep the migrated clock config`() = runBlocking {
        saveGlobals(customized)
        newRepository().ensureMigrated()

        saveGlobals(ClockConfig(style = "MINIMAL", fontFamily = "MONOSPACE"))
        gridRepository.saveGridItems(listOf(clock(GridDefaults.CLOCK_ITEM_ID), clock("clock_added_later")))
        val restarted = newRepository()

        assertEquals(customized, restarted.get(GridDefaults.CLOCK_ITEM_ID))
        assertNull(restarted.get("clock_added_later"))
        assertEquals("MINIMAL", restarted.getOrDefault("clock_added_later").style)
    }

    @Test
    fun `when the home has no clock, should migrate nothing and not fail`() = runBlocking {
        saveGlobals(customized)
        gridRepository.saveGridItems(emptyList(), isLandscape = false)
        gridRepository.saveGridItems(emptyList(), isLandscape = true)

        val repository = newRepository()

        assertTrue(repository.getAll().isEmpty())
        assertEquals(customized, repository.getOrDefault(GridDefaults.CLOCK_ITEM_ID))
    }

    @Test
    fun `when a clock already has a config, should not overwrite it with the settings look`() = runBlocking {
        val prefs = context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("clock_config_${GridDefaults.CLOCK_ITEM_ID}", "{\"style\":\"MINIMAL\"}").commit()
        saveGlobals(customized)

        assertEquals(ClockConfig(style = "MINIMAL"), newRepository().get(GridDefaults.CLOCK_ITEM_ID))
    }

    @Test
    fun `when the migration fails, should try it again on the next start`() = runBlocking {
        saveGlobals(customized)
        val settingsPrefs = context.getSharedPreferences("aureole_settings_prefs", Context.MODE_PRIVATE)
        // A value of the wrong type makes the handedness read throw while the migration reads the layout.
        settingsPrefs.edit().putString("left_handed_mode", "broken").commit()

        assertTrue(newRepository().getAll().isEmpty())

        settingsPrefs.edit().remove("left_handed_mode").commit()
        assertEquals(customized, newRepository().get(GridDefaults.CLOCK_ITEM_ID))
    }
}
