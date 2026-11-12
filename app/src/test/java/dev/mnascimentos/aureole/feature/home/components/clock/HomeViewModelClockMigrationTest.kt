package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ClockConfigRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val MIGRATION_TIMEOUT_MS = 10_000L

@RunWith(RobolectricTestRunner::class)
class HomeViewModelClockMigrationTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var clockConfigRepository: ClockConfigRepository
    private lateinit var viewModel: HomeViewModel

    private val clockPrefs
        get() = context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        clockPrefs.edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.resetClockSettings()
        settingsRepository.clockFontFamily = "SERIF"
        val gridRepository = GridRepository(context)
        gridRepository.resetToDefault()
        clockConfigRepository = ClockConfigRepository(context, gridRepository, settingsRepository)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = gridRepository,
                clockConfigRepository = clockConfigRepository
            )
        )
    }

    @After
    fun tearDown() {
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        runBlocking(Dispatchers.IO) { AppDatabase.getInstance(context).clearAllTables() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when the home starts, should give the clock on the grid the settings look`() {
        // The view model starts the migration itself: nothing here reads the repository before it is done.
        val configs = runBlocking(Dispatchers.IO) {
            withTimeout(MIGRATION_TIMEOUT_MS) { clockConfigRepository.configs.first { it.isNotEmpty() } }
        }

        assertTrue(clockPrefs.getBoolean("migrated_from_globals", false))
        assertEquals(setOf(GridDefaults.CLOCK_ITEM_ID), configs.keys)
        assertEquals("SERIF", configs.getValue(GridDefaults.CLOCK_ITEM_ID).fontFamily)
    }
}
