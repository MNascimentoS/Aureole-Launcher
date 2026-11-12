package dev.mnascimentos.aureole.feature.home.components.clock

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ClockConfigRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.core.data.repository.globalClockConfig
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.closeEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.editingClockConfig
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.forClockEditor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.openEditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.resetClockSettings
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.toggleClockBackground
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockAlignment
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockBackgroundColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockCustomGreeting
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockDateFormat
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockFontFamily
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockStyle
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTextColor
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.updateClockTimeFormat
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val CLOCK_A = "clock_a"
private const val CLOCK_B = "clock_b"
private const val CLOCK_NEW = "clock_new"
private const val TEXT_COLOR = 0xFF90CAF9.toInt()
private const val BACKGROUND_COLOR = 0xFF1A237E.toInt()

/** The clock editor changes only the clock it was opened for (RF-13). */
@RunWith(RobolectricTestRunner::class)
class HomeViewModelClockEditByIdTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var clockConfigRepository: ClockConfigRepository
    private lateinit var viewModel: HomeViewModel

    private val configA = ClockConfig(style = "TIME_ONLY", fontFamily = "SERIF", textColor = 1)
    private val configB = ClockConfig(style = "CUSTOM_GREETING", customGreeting = "Clock B", alignment = "END")

    private val clockPrefs
        get() = context.getSharedPreferences(ClockConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        clockPrefs.edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.resetClockSettings()
        val gridRepository = GridRepository(context)
        gridRepository.resetToDefault()
        clockConfigRepository = ClockConfigRepository(context, gridRepository, settingsRepository)
        // Saved before the home starts, off the main thread, as two clocks of a home already in use.
        runBlocking(Dispatchers.IO) {
            clockConfigRepository.save(CLOCK_A, configA)
            clockConfigRepository.save(CLOCK_B, configB)
        }
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
        clockPrefs.edit().clear().commit()
        settingsRepository.resetClockSettings()
        Dispatchers.resetMain()
    }

    @Test
    fun `when the home starts, should have the saved config of each clock`() {
        val configs = viewModel.uiState.value.clockConfigs

        assertEquals(configA, configs[CLOCK_A])
        assertEquals(configB, configs[CLOCK_B])
    }

    @Test
    fun `when opening the editor from clock A, should show the config of clock A`() {
        viewModel.openEditClockBottomSheet(CLOCK_A)

        val state = viewModel.uiState.value
        assertTrue(state.showEditClockBottomSheet)
        assertEquals(CLOCK_A, state.editingClockId)
        assertEquals(configA, state.editingClockConfig)
        val editor = state.forClockEditor()
        assertEquals(configA.style, editor.clockStyle)
        assertEquals(configA.fontFamily, editor.clockFontFamily)
        assertEquals(configA.textColor, editor.clockTextColor)
    }

    @Test
    fun `when closing the editor, should forget the clock it was editing`() {
        viewModel.openEditClockBottomSheet(CLOCK_B)

        viewModel.closeEditClockBottomSheet()

        assertFalse(viewModel.uiState.value.showEditClockBottomSheet)
        assertNull(viewModel.uiState.value.editingClockId)
    }

    @Test
    fun `when editing every field of clock A, should not change clock B nor the settings`() {
        val defaultBefore = settingsRepository.globalClockConfig()
        viewModel.openEditClockBottomSheet(CLOCK_A)

        editEveryField()

        val expectedA = ClockConfig(
            style = "CUSTOM_GREETING",
            customGreeting = "Clock A",
            alignment = "START",
            fontFamily = "MONOSPACE",
            timeFormat = "24H",
            dateFormat = "SHORT",
            textColor = TEXT_COLOR,
            backgroundColor = BACKGROUND_COLOR,
            isBackgroundEnabled = true
        )
        val configs = viewModel.uiState.value.clockConfigs
        assertEquals(expectedA, configs[CLOCK_A])
        assertEquals(configB, configs[CLOCK_B])
        assertEquals(expectedA, clockConfigRepository.configs.value[CLOCK_A])
        assertEquals(configB, clockConfigRepository.configs.value[CLOCK_B])
        assertEquals(defaultBefore, settingsRepository.globalClockConfig())
    }

    @Test
    fun `when editing clock B after clock A, should keep each change on its own clock`() {
        viewModel.openEditClockBottomSheet(CLOCK_A)
        viewModel.updateClockFontFamily("MONOSPACE")
        viewModel.closeEditClockBottomSheet()

        viewModel.openEditClockBottomSheet(CLOCK_B)
        viewModel.updateClockFontFamily("SERIF_ITALIC")

        val configs = viewModel.uiState.value.clockConfigs
        assertEquals(configA.copy(fontFamily = "MONOSPACE"), configs[CLOCK_A])
        assertEquals(configB.copy(fontFamily = "SERIF_ITALIC"), configs[CLOCK_B])
    }

    @Test
    fun `when editing a clock with no config, should start from the default look and save it for that clock`() {
        // The settings were reset, so the default look is the factory one in the settings and in the state alike.
        val default = settingsRepository.globalClockConfig()

        viewModel.openEditClockBottomSheet(CLOCK_NEW)
        assertEquals(default, viewModel.uiState.value.editingClockConfig)
        viewModel.updateClockAlignment("END")

        assertEquals(default.copy(alignment = "END"), viewModel.uiState.value.clockConfigs[CLOCK_NEW])
        assertEquals(default, settingsRepository.globalClockConfig())
        assertEquals(configA, viewModel.uiState.value.clockConfigs[CLOCK_A])
    }

    @Test
    fun `when resetting clock A, should reset only clock A`() {
        val defaultBefore = settingsRepository.globalClockConfig()
        viewModel.openEditClockBottomSheet(CLOCK_A)

        viewModel.resetClockSettings()

        val configs = viewModel.uiState.value.clockConfigs
        assertEquals(ClockConfig(), configs[CLOCK_A])
        assertEquals(configB, configs[CLOCK_B])
        assertEquals(defaultBefore, settingsRepository.globalClockConfig())
    }

    @Test
    fun `when editing with no clock id, should change the default look and no clock config`() {
        viewModel.openEditClockBottomSheet(null)

        viewModel.updateClockFontFamily("MONOSPACE")

        assertEquals("MONOSPACE", settingsRepository.clockFontFamily)
        assertEquals("MONOSPACE", viewModel.uiState.value.clockFontFamily)
        assertEquals(configA, viewModel.uiState.value.clockConfigs[CLOCK_A])
        assertEquals(configB, viewModel.uiState.value.clockConfigs[CLOCK_B])
    }

    private fun editEveryField() {
        viewModel.updateClockStyle("CUSTOM_GREETING")
        viewModel.updateClockCustomGreeting("Clock A")
        viewModel.updateClockAlignment("START")
        viewModel.updateClockFontFamily("MONOSPACE")
        viewModel.updateClockTimeFormat("24H")
        viewModel.updateClockDateFormat("SHORT")
        viewModel.updateClockTextColor(TEXT_COLOR)
        viewModel.updateClockBackgroundColor(BACKGROUND_COLOR)
        viewModel.toggleClockBackground()
    }
}
