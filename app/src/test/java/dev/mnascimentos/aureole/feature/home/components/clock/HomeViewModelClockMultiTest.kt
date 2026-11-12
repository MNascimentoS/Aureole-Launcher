package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ClockConfigRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.addClock
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.closeEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.openEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockFontFamily
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockStyle
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.ensureGridItem
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.meetsRequirement
import dev.mnascimentos.aureole.feature.home.extensions.onUndo
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeGridItemWithUndo
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemSpec
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val WAIT_TIMEOUT_MS = 5_000L
private const val LEGACY_STYLE = "TIME_ONLY"
private const val LEGACY_FONT = "SERIF"
private const val LEGACY_TEXT_COLOR = 0xFF90CAF9.toInt()
private const val SCROLL_VIEW_ID = "scroll"

/** Many clocks on the home: each new clock has its own config, removing one erases only its config (RF-12, RF-15). */
@RunWith(RobolectricTestRunner::class)
class HomeViewModelClockMultiTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var gridRepository: GridRepository
    private lateinit var clockConfigRepository: ClockConfigRepository
    private lateinit var viewModel: HomeViewModel

    private val clockPrefs
        get() = context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)

    private val legacyClock: LauncherItemState
        get() = checkNotNull(
            GridDefaults.getDefaultItem(LauncherItemType.CLOCK, isLandscape = false, isLeftHanded = false)
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        clockPrefs.edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.resetClockSettings()
        // The look of a 0.9.20 clock: the settings had the only clock look there was.
        settingsRepository.clockStyle = LEGACY_STYLE
        settingsRepository.clockFontFamily = LEGACY_FONT
        settingsRepository.clockTextColor = LEGACY_TEXT_COLOR
        gridRepository = GridRepository(context)
        gridRepository.resetToDefault()
        // Only the clock of the default layout, so new clocks have room.
        gridRepository.saveGridItems(listOf(legacyClock), isLandscape = false)
        // A landscape never opened: no clock there that shares the portrait clock's config.
        gridRepository.saveGridItems(emptyList(), isLandscape = true)
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
        // The real portrait grid, as the home screen reports it.
        viewModel.updateUiState {
            it.copy(gridLimits = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS))
        }
        // The view model migrates the 0.9.20 clock itself, off the main thread.
        awaitConfigs { GridDefaults.CLOCK_ITEM_ID in it }
    }

    @After
    fun tearDown() {
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        runBlocking(Dispatchers.IO) { AppDatabase.getInstance(context).clearAllTables() }
        clockPrefs.edit().clear().commit()
        settingsRepository.resetClockSettings()
        gridRepository.resetToDefault()
        Dispatchers.resetMain()
    }

    private fun awaitConfigs(predicate: (Map<String, ClockConfig>) -> Boolean): Map<String, ClockConfig> =
        runBlocking(Dispatchers.IO) { withTimeout(WAIT_TIMEOUT_MS) { clockConfigRepository.configs.first(predicate) } }

    private fun clockIds(): List<String> =
        viewModel.uiState.value.gridItems.filter { it.safeType == LauncherItemType.CLOCK }.map { it.id }

    /** Adds a clock from Customize and saves the edit mode it leaves open; returns the new clock id. */
    private fun addSavedClock(): String {
        val before = clockIds().toSet()
        viewModel.addClock()
        viewModel.saveGridEditMode()
        return clockIds().single { it !in before }
    }

    private fun editClock(clockId: String, style: String, font: String) {
        viewModel.openEditClockBottomSheet(clockId)
        viewModel.updateClockStyle(style)
        viewModel.updateClockFontFamily(font)
        viewModel.closeEditClockBottomSheet()
    }

    @Test
    fun `when adding a clock twice, should create two clocks with distinct ids and a default config each`() {
        val first = addSavedClock()
        val second = addSavedClock()
        val default = clockConfigRepository.defaultConfig()

        val configs = awaitConfigs { first in it && second in it }

        assertNotEquals(first, second)
        assertEquals(3, clockIds().size)
        assertTrue(listOf(first, second).none { it == GridDefaults.CLOCK_ITEM_ID })
        assertEquals(default, configs.getValue(first))
        assertEquals(default, configs.getValue(second))
        assertEquals(default, viewModel.uiState.value.clockConfigs.getValue(first))
    }

    @Test
    fun `when the clock spot is free, should place the new clock there with a new id`() {
        viewModel.removeGridItemWithUndo(GridDefaults.CLOCK_ITEM_ID)

        val added = addSavedClock()
        val item = viewModel.uiState.value.gridItems.single { it.id == added }

        assertNotEquals(GridDefaults.CLOCK_ITEM_ID, added)
        assertEquals(legacyClock.copy(id = added), item)
        assertEquals(clockConfigRepository.defaultConfig(), awaitConfigs { added in it }.getValue(added))
    }

    @Test
    fun `when removing one clock, should erase only its config`() {
        val first = addSavedClock()
        val second = addSavedClock()
        editClock(second, style = "GREETING_AND_DATE", font = "MONOSPACE")
        val secondConfig = viewModel.uiState.value.clockConfigs.getValue(second)

        viewModel.removeGridItemWithUndo(first)
        val configs = awaitConfigs { first !in it }

        assertEquals(listOf(GridDefaults.CLOCK_ITEM_ID, second), clockIds())
        assertEquals(secondConfig, configs.getValue(second))
        assertEquals(LEGACY_STYLE, configs.getValue(GridDefaults.CLOCK_ITEM_ID).style)
        assertFalse(viewModel.uiState.value.clockConfigs.containsKey(first))
    }

    @Test
    fun `when undoing a clock removal, should restore the clock and its config`() {
        val clock = addSavedClock()
        editClock(clock, style = "DYNAMIC_GREETING", font = "MONOSPACE")
        val item = viewModel.uiState.value.gridItems.single { it.id == clock }
        val config = viewModel.uiState.value.clockConfigs.getValue(clock)

        viewModel.removeGridItemWithUndo(clock)
        awaitConfigs { clock !in it }
        viewModel.onUndo()
        val configs = awaitConfigs { it[clock] == config }

        assertEquals(item, viewModel.uiState.value.gridItems.single { it.id == clock })
        assertEquals(config, configs.getValue(clock))
        assertEquals(config, viewModel.uiState.value.clockConfigs.getValue(clock))
    }

    @Test
    fun `when the other layout still has the clock, should keep its shared config on removal`() {
        val landscapeClock = legacyClock.copy(colSpan = 10)
        viewModel.updateUiState { it.copy(landscapeGridItems = listOf(landscapeClock)) }
        val before = viewModel.uiState.value.clockConfigs.getValue(GridDefaults.CLOCK_ITEM_ID)

        viewModel.removeGridItemWithUndo(GridDefaults.CLOCK_ITEM_ID)

        assertTrue(clockIds().isEmpty())
        assertEquals(before, clockConfigRepository.configs.value[GridDefaults.CLOCK_ITEM_ID])
    }

    @Test
    fun `when removing a clock in the grid edit mode, should keep its config for a cancel`() {
        viewModel.addClock()
        val clock = clockIds().single { it != GridDefaults.CLOCK_ITEM_ID }
        awaitConfigs { clock in it }

        viewModel.removeGridItemWithUndo(clock)

        assertTrue(viewModel.uiState.value.isGridEditMode)
        assertTrue(clockConfigRepository.configs.value.containsKey(clock))
    }

    @Test
    fun `when coming from 0_9_20 and adding another clock, should keep the migrated clock as it was`() {
        val migrated = viewModel.uiState.value.clockConfigs.getValue(GridDefaults.CLOCK_ITEM_ID)

        val added = addSavedClock()
        editClock(added, style = "DATE_ON_TOP", font = "SANS_SERIF")
        val configs = awaitConfigs { it[added]?.style == "DATE_ON_TOP" }

        assertEquals(LEGACY_STYLE, migrated.style)
        assertEquals(LEGACY_FONT, migrated.fontFamily)
        assertEquals(LEGACY_TEXT_COLOR, migrated.textColor)
        assertEquals(migrated, configs.getValue(GridDefaults.CLOCK_ITEM_ID))
        assertEquals(LEGACY_STYLE, settingsRepository.clockStyle)
    }

    @Test
    fun `when adding a clock from the edit mode dialog or into a scroll view, should give it a config`() {
        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.CLOCK))
        val fromDialog = clockIds().single { it != GridDefaults.CLOCK_ITEM_ID }
        val scrollView = LauncherItemState(id = SCROLL_VIEW_ID, type = LauncherItemType.SCROLL_VIEW, row = 20)
        viewModel.updateUiState {
            val items = it.gridItems + scrollView
            it.copy(gridItems = items, portraitGridItems = items, targetParentContainerId = SCROLL_VIEW_ID)
        }
        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.CLOCK))
        val child = viewModel.uiState.value.gridItems.single { it.id == SCROLL_VIEW_ID }.safeChildren.single().id

        val configs = awaitConfigs { fromDialog in it && child in it }

        assertEquals(clockConfigRepository.defaultConfig(), configs.getValue(fromDialog))
        assertEquals(clockConfigRepository.defaultConfig(), configs.getValue(child))
    }

    @Test
    fun `when adding an item that is not a clock, should save no clock config`() {
        val before = clockConfigRepository.configs.value.keys

        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.QUICK_BUTTON))

        assertEquals(before, clockConfigRepository.configs.value.keys)
    }

    @Test
    fun `when the clock task runs, should reuse a clock on the home and count it as done`() {
        val added = addSavedClock()
        viewModel.removeGridItemWithUndo(GridDefaults.CLOCK_ITEM_ID)

        val taskClock = viewModel.ensureGridItem(LauncherItemType.CLOCK)

        assertEquals(added, taskClock)
        assertEquals(listOf(added), clockIds())
        assertTrue(viewModel.uiState.value.meetsRequirement(OnboardingTask.ADD_CLOCK))
    }

    @Test
    fun `when the clock task places the first clock, should give it a config`() {
        viewModel.removeGridItemWithUndo(GridDefaults.CLOCK_ITEM_ID)
        awaitConfigs { GridDefaults.CLOCK_ITEM_ID !in it }

        val taskClock = checkNotNull(viewModel.ensureGridItem(LauncherItemType.CLOCK))
        viewModel.saveGridEditMode()

        assertEquals(clockConfigRepository.defaultConfig(), awaitConfigs { taskClock in it }.getValue(taskClock))
        assertTrue(viewModel.uiState.value.meetsRequirement(OnboardingTask.ADD_CLOCK))
    }
}
