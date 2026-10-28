package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingChecklistExtTest {

    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val state get() = viewModel.uiState.value

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context: Context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        settingsRepository.onboarding.write(OnboardingProgress(stage = OnboardingStage.CHECKLIST))
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
        viewModel.syncOnboarding()
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
        runBlocking { scope.cancelAndJoin() }
        Dispatchers.resetMain()
        settingsRepository.onboarding.write(OnboardingProgress(stage = OnboardingStage.COMPLETED))
    }

    @Test
    fun `when an appearance task is tapped, should open its picker over the home`() {
        viewModel.onTaskClick(OnboardingTask.CHOOSE_PALETTE)
        assertEquals(AppearancePicker.PALETTE, state.onboarding.activePicker)

        viewModel.onTaskClick(OnboardingTask.CHOOSE_FONT)
        assertEquals(AppearancePicker.FONT, state.onboarding.activePicker)
    }

    @Test
    fun `when an editor task is tapped, should collapse the list to the pill`() {
        viewModel.onTaskClick(OnboardingTask.ADD_FAVORITE_APPS)

        assertFalse(state.onboarding.isChecklistExpanded)
        assertNull(state.onboarding.activePicker)
    }

    @Test
    fun `when a palette is picked, should persist and re-theme the home`() {
        viewModel.openAppearancePicker(AppearancePicker.PALETTE)

        viewModel.selectPalette("Abyss")

        val seed = resolveThemeSeedColor("Abyss")
        assertEquals("Abyss", state.selectedThemeName)
        assertEquals(seed, state.manualSeedColor)
        assertEquals("Abyss", settingsRepository.selectedThemeName)
        assertEquals(seed, settingsRepository.manualSeedColor)
    }

    @Test
    fun `when a font is picked and confirmed, should persist it and complete the task`() {
        viewModel.openAppearancePicker(AppearancePicker.FONT)

        viewModel.selectFont("Space Grotesk")
        viewModel.confirmAppearancePicker()

        assertEquals("Space Grotesk", state.selectedFontName)
        assertEquals("Space Grotesk", settingsRepository.selectedFontName)
        assertNull(state.onboarding.activePicker)
        assertTrue(OnboardingTask.CHOOSE_FONT in state.onboarding.completedTasks)
        assertTrue(OnboardingTask.CHOOSE_FONT in settingsRepository.onboarding.read()!!.completedTasks)
    }

    @Test
    fun `when the picker is dismissed, should close it without completing the task`() {
        viewModel.openAppearancePicker(AppearancePicker.PALETTE)

        viewModel.dismissAppearancePicker()

        assertNull(state.onboarding.activePicker)
        assertTrue(state.onboarding.completedTasks.isEmpty())
    }

    @Test
    fun `when the list is minimized and reopened, should persist the collapsed flag`() {
        viewModel.setChecklistExpanded(false)
        assertTrue(settingsRepository.onboarding.read()!!.isChecklistCollapsed)

        viewModel.setChecklistExpanded(true)
        assertFalse(settingsRepository.onboarding.read()!!.isChecklistCollapsed)
    }

    @Test
    fun `when the checklist is dismissed, should complete without celebrating`() {
        viewModel.dismissChecklist()

        assertEquals(OnboardingStage.COMPLETED, state.onboarding.stage)
        assertFalse(state.onboarding.isCelebrating)
        assertEquals(OnboardingStage.COMPLETED, settingsRepository.onboarding.read()?.stage)
    }

    @Test
    fun `when the last task completes, should celebrate once and stop`() {
        val allButSettings = OnboardingTask.entries.toSet() - OnboardingTask.EXPLORE_SETTINGS
        viewModel.updateOnboarding(OnboardingStage.CHECKLIST) { copy(completedTasks = allButSettings) }

        viewModel.onOnboardingSignal(OnboardingSignal.SETTINGS_OPENED)
        viewModel.onReturnedToHome()
        assertTrue(state.onboarding.isCelebrating)
        assertEquals(OnboardingStage.COMPLETED, settingsRepository.onboarding.read()?.stage)

        viewModel.finishCelebration()
        assertFalse(state.onboarding.isCelebrating)
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
