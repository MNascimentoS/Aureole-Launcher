package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
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
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private val viewModels = mutableListOf<HomeViewModel>()
    private val testDispatcher = StandardTestDispatcher()

    private val onboarding: OnboardingUiState
        get() = viewModels.last().uiState.value.onboarding

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
        settingsRepository.lastSeenVersionCode = 0
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back: they resume
        // on Main from IO threads, so drive the test scheduler until the cancelled scope completes.
        viewModels.forEach { viewModel ->
            val scope = viewModel.viewModelScope.coroutineContext.job
            scope.cancel()
            val deadline = System.currentTimeMillis() + VIEW_MODEL_SHUTDOWN_MS
            while (!scope.isCompleted && System.currentTimeMillis() < deadline) {
                testDispatcher.scheduler.advanceUntilIdle()
                Thread.sleep(SHUTDOWN_POLL_MS)
            }
            runBlocking { scope.cancelAndJoin() }
        }
        Dispatchers.resetMain()
    }

    private fun newViewModel(): HomeViewModel {
        val viewModel = HomeViewModel(
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
        viewModels += viewModel
        return viewModel
    }

    private fun viewModelAt(progress: OnboardingProgress): HomeViewModel {
        settingsRepository.onboarding.write(progress)
        return newViewModel().apply { syncOnboarding() }
    }

    // region sync

    @Test
    fun `when it is a fresh install, should start at welcome and persist it`() {
        newViewModel().syncOnboarding()

        assertEquals(OnboardingStage.WELCOME, onboarding.stage)
        assertEquals(OnboardingStage.WELCOME, settingsRepository.onboarding.read()?.stage)
    }

    @Test
    fun `when an existing user updates, should skip the onboarding`() {
        settingsRepository.lastSeenVersionCode = 826

        newViewModel().syncOnboarding()

        assertEquals(OnboardingStage.COMPLETED, onboarding.stage)
        assertEquals(OnboardingStage.COMPLETED, settingsRepository.onboarding.read()?.stage)
    }

    @Test
    fun `when progress was saved, should survive a new view model`() {
        val progress = OnboardingProgress(
            stage = OnboardingStage.CHECKLIST,
            completedSteps = GestureCoachStep.entries.toSet(),
            completedTasks = setOf(OnboardingTask.CHOOSE_FONT),
            isChecklistCollapsed = true
        )

        viewModelAt(progress)

        assertEquals(OnboardingStage.CHECKLIST, onboarding.stage)
        assertEquals(setOf(OnboardingTask.CHOOSE_FONT), onboarding.completedTasks)
        assertFalse(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when resumed again, should keep transient state`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.CHECKLIST))
        viewModel.openAppearancePicker(AppearancePicker.PALETTE)

        viewModel.syncOnboarding()

        assertEquals(AppearancePicker.PALETTE, onboarding.activePicker)
    }

    @Test
    fun `when settings restarted the tour, should pick it up on resume`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.COMPLETED))

        settingsRepository.onboarding.restart()
        viewModel.syncOnboarding()

        assertEquals(OnboardingStage.WELCOME, onboarding.stage)
    }

    @Test
    @Config(sdk = [28])
    fun `when aureole is already the home app, should skip the default home step`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.DEFAULT_HOME))

        assertEquals(OnboardingStage.GESTURES, onboarding.stage)
        assertTrue(onboarding.isDefaultLauncher)
    }

    @Test
    @Config(sdk = [28])
    fun `when coming back from the role request as default, should stay to show the success`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.WELCOME))
        viewModel.updateOnboarding(OnboardingStage.WELCOME) { copy(stage = OnboardingStage.DEFAULT_HOME) }

        viewModel.refreshDefaultLauncherStatus()

        assertEquals(OnboardingStage.DEFAULT_HOME, onboarding.stage)
        assertTrue(onboarding.defaultHomeAttempted)
        assertTrue(onboarding.isDefaultLauncher)
    }

    @Test
    fun `when coming back from the role request without it, should remember the attempt`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.DEFAULT_HOME))

        viewModel.refreshDefaultLauncherStatus()

        assertEquals(OnboardingStage.DEFAULT_HOME, onboarding.stage)
        assertTrue(onboarding.defaultHomeAttempted)
        assertFalse(onboarding.isDefaultLauncher)
    }

    // endregion

    // region flow

    @Test
    fun `when starting, should go to the default home step`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.WELCOME)).startOnboarding()

        assertEquals(OnboardingStage.DEFAULT_HOME, onboarding.stage)
    }

    @Test
    @Config(sdk = [28])
    fun `when starting as the home app, should go straight to gestures`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.WELCOME)).startOnboarding()

        assertEquals(OnboardingStage.GESTURES, onboarding.stage)
    }

    @Test
    fun `when skipping the intro, should open the checklist and persist it`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.WELCOME)).skipIntro()

        assertEquals(OnboardingStage.CHECKLIST, onboarding.stage)
        assertTrue(onboarding.isChecklistExpanded)
        assertEquals(OnboardingStage.CHECKLIST, settingsRepository.onboarding.read()?.stage)
    }

    @Test
    fun `when a transition runs in the wrong stage, should ignore it`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.GESTURES))

        viewModel.skipIntro()
        viewModel.postponeDefaultHome()
        viewModel.dismissChecklist()

        assertEquals(OnboardingStage.GESTURES, onboarding.stage)
    }

    @Test
    fun `when postponing the home app, should teach gestures`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.DEFAULT_HOME)).postponeDefaultHome()

        assertEquals(OnboardingStage.GESTURES, onboarding.stage)
    }

    @Test
    fun `when skipping gestures, should complete every step and open the checklist`() {
        viewModelAt(OnboardingProgress(stage = OnboardingStage.GESTURES)).skipGestures()

        assertEquals(OnboardingStage.CHECKLIST, onboarding.stage)
        assertEquals(GestureCoachStep.entries.toSet(), onboarding.completedSteps)
    }

    @Test
    fun `when show me runs for each step, should perform the gesture and complete it`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.GESTURES))

        viewModel.showMeGesture()
        assertTrue(viewModel.uiState.value.isAllAppsDrawerOpen)
        assertEquals(GestureCoachStep.OPEN_APPS, onboarding.succeededStep)

        viewModel.updateUiState { it.copy(isAllAppsDrawerOpen = false) }
        viewModel.showMeGesture()
        assertEquals(GestureCoachStep.ALPHABET_SCRUB, onboarding.succeededStep)

        viewModel.updateUiState { it.copy(isAllAppsDrawerOpen = false) }
        viewModel.showMeGesture()
        assertTrue(viewModel.uiState.value.showCustomizeBottomSheet)

        viewModel.updateUiState { it.copy(showCustomizeBottomSheet = false) }
        assertEquals(OnboardingStage.CHECKLIST, onboarding.stage)
    }

    @Test
    fun `when show me runs outside gestures, should do nothing`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.CHECKLIST))

        viewModel.showMeGesture()

        assertFalse(viewModel.uiState.value.isAllAppsDrawerOpen)
    }

    // endregion

    // region back

    @Test
    fun `when back is pressed in each stage, should step back or let the home handle it`() {
        val welcome = viewModelAt(OnboardingProgress(stage = OnboardingStage.WELCOME))
        assertTrue(welcome.onOnboardingBack())
        assertEquals(OnboardingStage.WELCOME, onboarding.stage)

        val defaultHome = viewModelAt(OnboardingProgress(stage = OnboardingStage.DEFAULT_HOME))
        assertTrue(defaultHome.onOnboardingBack())
        assertEquals(OnboardingStage.WELCOME, onboarding.stage)

        assertFalse(viewModelAt(OnboardingProgress(stage = OnboardingStage.GESTURES)).onOnboardingBack())
        assertFalse(viewModelAt(OnboardingProgress(stage = OnboardingStage.COMPLETED)).onOnboardingBack())
    }

    @Test
    fun `when back is pressed on the checklist, should close the picker, then minimize`() {
        val viewModel = viewModelAt(OnboardingProgress(stage = OnboardingStage.CHECKLIST))
        viewModel.openAppearancePicker(AppearancePicker.FONT)

        assertTrue(viewModel.onOnboardingBack())
        assertNull(onboarding.activePicker)
        assertTrue(onboarding.isChecklistExpanded)

        assertTrue(viewModel.onOnboardingBack())
        assertFalse(onboarding.isChecklistExpanded)

        assertFalse(viewModel.onOnboardingBack())
    }

    // endregion

    companion object {
        private const val PREFS_NAME = "aureole_settings_prefs"
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
