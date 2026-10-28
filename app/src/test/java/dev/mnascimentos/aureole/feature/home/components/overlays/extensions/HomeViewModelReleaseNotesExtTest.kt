package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelReleaseNotesExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
        ShadowLooper.idleMainLooper()
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back: they resume
        // on Main from IO threads, so drive the test scheduler until the cancelled scope completes.
        val scope = viewModel.viewModelScope.coroutineContext.job
        scope.cancel()
        val deadline = System.currentTimeMillis() + VIEW_MODEL_SHUTDOWN_MS
        while (!scope.isCompleted && System.currentTimeMillis() < deadline) {
            testDispatcher.scheduler.advanceUntilIdle()
            Thread.sleep(SHUTDOWN_POLL_MS)
        }
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `checkReleaseNotes displays release notes when new version detected`() {
        settingsRepository.lastSeenVersionCode = 10

        viewModel.checkReleaseNotes(currentVersionCode = 20, versionName = "2.0")

        val state = viewModel.uiState.value
        assertEquals("2.0", state.currentVersionName)
        assertTrue(state.showReleaseNotesBottomSheet)
        assertEquals(20, settingsRepository.lastSeenVersionCode)
    }

    @Test
    fun `checkReleaseNotes does not display release notes when version is same or older`() {
        settingsRepository.lastSeenVersionCode = 20

        viewModel.checkReleaseNotes(currentVersionCode = 20, versionName = "2.0")

        val state = viewModel.uiState.value
        assertEquals("2.0", state.currentVersionName)
        assertFalse(state.showReleaseNotesBottomSheet)
    }

    @Test
    fun `openReleaseNotes sets showReleaseNotesBottomSheet true`() {
        viewModel.openReleaseNotes()

        assertTrue(viewModel.uiState.value.showReleaseNotesBottomSheet)
    }

    @Test
    fun `dismissReleaseNotes sets showReleaseNotesBottomSheet false and updates lastSeenVersionCode`() {
        viewModel.openReleaseNotes()
        assertTrue(viewModel.uiState.value.showReleaseNotesBottomSheet)

        viewModel.dismissReleaseNotes(versionCode = 30)

        assertFalse(viewModel.uiState.value.showReleaseNotesBottomSheet)
        assertEquals(30, settingsRepository.lastSeenVersionCode)
    }

    @Test
    fun `checkReleaseNotes on a fresh install records the version without showing notes`() {
        settingsRepository.lastSeenVersionCode = 0

        viewModel.checkReleaseNotes(currentVersionCode = 20, versionName = "2.0")

        assertFalse(viewModel.uiState.value.showReleaseNotesBottomSheet)
        assertEquals(20, settingsRepository.lastSeenVersionCode)
    }

    @Test
    fun `checkReleaseNotes during the onboarding waits until it completes`() {
        settingsRepository.lastSeenVersionCode = 10
        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST)) }

        viewModel.checkReleaseNotes(currentVersionCode = 20, versionName = "2.0")

        assertFalse(viewModel.uiState.value.showReleaseNotesBottomSheet)
        assertEquals(10, settingsRepository.lastSeenVersionCode)

        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.COMPLETED)) }
        viewModel.checkReleaseNotes(currentVersionCode = 20, versionName = "2.0")

        assertTrue(viewModel.uiState.value.showReleaseNotesBottomSheet)
        assertEquals(20, settingsRepository.lastSeenVersionCode)
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
