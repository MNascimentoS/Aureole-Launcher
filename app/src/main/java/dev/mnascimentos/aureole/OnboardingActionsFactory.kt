package dev.mnascimentos.aureole

import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.applyPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.closePresets
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.closeVisualSheet
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.dismissChecklist
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.finishCelebration
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onOnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onTaskClick
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.openPresets
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.postponeDefaultHome
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.selectPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.setChecklistExpanded
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.showMeGesture
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.skipGestures
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.skipIntro
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.skipTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.startGridTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.startLayoutTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.startOnboarding
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.extensions.onUndo
import dev.mnascimentos.aureole.feature.home.extensions.onUndoExpired
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsIntentUtils
import dev.mnascimentos.aureole.util.DefaultHomeRoleUtils

/**
 * Maps onboarding callbacks to the view model. The clock, container, favorites and widget tasks place their item
 * on the home and open its own screen, with no grid edit mode; the layout step opens the grid edit mode.
 */
class OnboardingActionsFactory(
    private val context: Context,
    private val viewModel: HomeViewModel,
    private val homeActions: HomeScreenActions,
    private val launchDefaultHomeRequest: (Intent) -> Unit,
) {
    fun createOnboardingActions(): OnboardingActions = OnboardingActions(
        onStart = { viewModel.startOnboarding() },
        onSkipIntro = { viewModel.skipIntro() },
        onUsePreset = { viewModel.openPresets() },
        onClosePresets = { viewModel.closePresets() },
        onSelectPreset = { preset -> viewModel.selectPreset(preset) },
        onApplyPreset = { preset -> viewModel.applyPreset(preset) },
        onUndo = { viewModel.onUndo() },
        onUndoExpired = { id -> viewModel.onUndoExpired(id) },
        onRequestDefaultHome = { requestDefaultHome() },
        onOpenSystemHomeSettings = { openSystemHomeSettings() },
        onPostponeDefaultHome = { viewModel.postponeDefaultHome() },
        onSkipGestures = { viewModel.skipGestures() },
        onShowMeGesture = { viewModel.showMeGesture() },
        onAlphabetScrubbed = { viewModel.onOnboardingSignal(OnboardingSignal.ALPHABET_SCRUBBED) },
        onTaskClick = { task -> handleTaskClick(task) },
        onSkipTask = { task -> viewModel.skipTask(task) },
        onCloseVisual = { choice -> viewModel.closeVisualSheet(choice) },
        onSetChecklistExpanded = { expanded -> viewModel.setChecklistExpanded(expanded) },
        onDismissChecklist = { viewModel.dismissChecklist() },
        onFinishCelebration = { viewModel.finishCelebration() },
        onSaveGridEdit = { homeActions.onSaveGridEditMode() },
        onOpenSettings = { homeActions.onSettingsClick() }
    )

    private fun handleTaskClick(task: OnboardingTask) {
        viewModel.onTaskClick(task)
        when (task) {
            OnboardingTask.SET_DEFAULT_HOME -> requestDefaultHome()
            OnboardingTask.CHOOSE_VISUAL -> Unit
            OnboardingTask.ADD_CLOCK -> viewModel.startGridTask(task)?.let(homeActions.onOpenEditClockBottomSheet)
            OnboardingTask.FILL_CONTAINER -> viewModel.startGridTask(task)?.let(homeActions.onOpenEditContainerDialog)
            OnboardingTask.ADD_FAVORITE_APPS -> viewModel.startGridTask(task)?.let(homeActions.onChooseFavorites)
            OnboardingTask.ADD_WIDGET -> openWidgetGroupEditor(task)
            OnboardingTask.ADJUST_LAYOUT -> viewModel.startLayoutTask()
            OnboardingTask.EXPLORE_SETTINGS -> homeActions.onSettingsClick()
        }
    }

    /** The widget task opens the group editor (several widgets at once); closing it judges the task. */
    private fun openWidgetGroupEditor(task: OnboardingTask) {
        val itemId = viewModel.startGridTask(task) ?: return
        viewModel.uiState.value.gridItems.firstOrNull { it.id == itemId }?.let(homeActions.onOpenEditGridItemDialog)
    }

    private fun requestDefaultHome() {
        val intent = DefaultHomeRoleUtils.createRequestRoleIntent(context)
        if (intent != null) launchDefaultHomeRequest(intent) else openSystemHomeSettings()
    }

    /** No result comes back from the system screen: the next resume re-checks the home app. */
    private fun openSystemHomeSettings() {
        SettingsIntentUtils.openDefaultLauncherSettings(context)
        viewModel.onOnboardingSignal(OnboardingSignal.DEFAULT_HOME_RESULT)
    }
}
