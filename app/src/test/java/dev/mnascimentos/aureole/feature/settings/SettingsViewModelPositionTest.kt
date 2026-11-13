package dev.mnascimentos.aureole.feature.settings

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.extensions.onUndo
import dev.mnascimentos.aureole.feature.settings.extensions.selectPosition
import dev.mnascimentos.aureole.feature.settings.model.PositionKind
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
class SettingsViewModelPositionTest {

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
    fun `selecting a position applies it and offers undo`() {
        viewModel.selectPosition(PositionKind.SETTINGS_BUTTON, "Left")

        assertEquals("Left", viewModel.uiState.value.settingsButtonPosition)
        assertEquals("Left", repository.settingsButtonPosition)
        assertEquals(
            TextResource.fromResId(R.string.settings_position_changed),
            viewModel.uiState.value.undoSnackbar?.message
        )
    }

    @Test
    fun `undo brings the previous position back for each setting`() {
        PositionKind.entries.forEach { kind ->
            val previous = when (kind) {
                PositionKind.SETTINGS_BUTTON -> viewModel.uiState.value.settingsButtonPosition
                PositionKind.SEARCH_ICON -> viewModel.uiState.value.searchIconPosition
                PositionKind.CONTAINER -> viewModel.uiState.value.containerPosition
            }
            viewModel.selectPosition(kind, "Top")
            viewModel.onUndo()
            val restored = when (kind) {
                PositionKind.SETTINGS_BUTTON -> viewModel.uiState.value.settingsButtonPosition
                PositionKind.SEARCH_ICON -> viewModel.uiState.value.searchIconPosition
                PositionKind.CONTAINER -> viewModel.uiState.value.containerPosition
            }
            assertEquals(previous, restored)
            assertNull(viewModel.uiState.value.undoSnackbar)
        }
    }

    @Test
    fun `selecting the current position changes nothing and offers no undo`() {
        val current = viewModel.uiState.value.containerPosition

        viewModel.selectPosition(PositionKind.CONTAINER, current)

        assertNull(viewModel.uiState.value.undoSnackbar)
        assertEquals(current, viewModel.uiState.value.containerPosition)
    }
}
