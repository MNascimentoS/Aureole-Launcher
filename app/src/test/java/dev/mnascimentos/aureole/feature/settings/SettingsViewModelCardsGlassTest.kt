package dev.mnascimentos.aureole.feature.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.extensions.finishCardsGlass
import dev.mnascimentos.aureole.feature.settings.extensions.onUndo
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SettingsViewModelCardsGlassTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        viewModel = SettingsViewModel(
            settingsRepository = repository,
            appRepository = AppRepository(context),
            folderRepository = FolderRepository(context),
            gridRepository = GridRepository(context)
        )
    }

    @After
    fun tearDown() {
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `closing after a change offers an undo that restores the opening values`() {
        val before = viewModel.uiState.value.toCardsGlassDraft()
        val changed = CardsGlassDraft(radiusDp = 8, glass = GlassLevel.MAX, opacity = 0.9f)
        viewModel.setSettingValue(SettingValue.CardsGlass(changed))

        viewModel.finishCardsGlass(before)

        assertEquals(
            TextResource.fromResId(R.string.settings_cards_glass_changed),
            viewModel.uiState.value.undoSnackbar?.message
        )
        viewModel.onUndo()
        assertEquals(before, viewModel.uiState.value.toCardsGlassDraft())
    }

    @Test
    fun `closing without a change offers no undo`() {
        val before = viewModel.uiState.value.toCardsGlassDraft()

        viewModel.finishCardsGlass(before)

        assertNull(viewModel.uiState.value.undoSnackbar)
    }
}
