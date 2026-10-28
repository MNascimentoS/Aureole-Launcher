package dev.mnascimentos.aureole

import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.confirmAppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.dismissAppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.dismissChecklist
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.finishCelebration
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onOnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onTaskClick
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.postponeDefaultHome
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.selectFont
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.selectPalette
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.setChecklistExpanded
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.showMeGesture
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.skipGestures
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.skipIntro
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.startGridTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.startOnboarding
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.settings.extensions.SettingsIntentUtils
import dev.mnascimentos.aureole.util.DefaultHomeRoleUtils

/** Maps onboarding callbacks to the view model; grid tasks place their item, then open its editor. */
class OnboardingActionsFactory(
    private val context: Context,
    private val viewModel: HomeViewModel,
    private val homeActions: HomeScreenActions,
    private val launchDefaultHomeRequest: (Intent) -> Unit,
) {
    fun createOnboardingActions(): OnboardingActions = OnboardingActions(
        onStart = { viewModel.startOnboarding() },
        onSkipIntro = { viewModel.skipIntro() },
        onRequestDefaultHome = { requestDefaultHome() },
        onOpenSystemHomeSettings = { openSystemHomeSettings() },
        onPostponeDefaultHome = { viewModel.postponeDefaultHome() },
        onSkipGestures = { viewModel.skipGestures() },
        onShowMeGesture = { viewModel.showMeGesture() },
        onAlphabetScrubbed = { viewModel.onOnboardingSignal(OnboardingSignal.ALPHABET_SCRUBBED) },
        onTaskClick = { task -> handleTaskClick(task) },
        onSelectPalette = { name -> viewModel.selectPalette(name) },
        onSelectFont = { name -> viewModel.selectFont(name) },
        onConfirmAppearancePicker = { viewModel.confirmAppearancePicker() },
        onDismissAppearancePicker = { viewModel.dismissAppearancePicker() },
        onSetChecklistExpanded = { expanded -> viewModel.setChecklistExpanded(expanded) },
        onDismissChecklist = { viewModel.dismissChecklist() },
        onFinishCelebration = { viewModel.finishCelebration() },
        onSaveGridEdit = { homeActions.onSaveGridEditMode() }
    )

    private fun handleTaskClick(task: OnboardingTask) {
        viewModel.onTaskClick(task)
        when (task) {
            OnboardingTask.SET_DEFAULT_HOME -> requestDefaultHome()
            OnboardingTask.CHOOSE_PALETTE, OnboardingTask.CHOOSE_FONT -> Unit
            OnboardingTask.ADD_CLOCK -> viewModel.startGridTask(task)?.let { homeActions.onOpenEditClockBottomSheet() }
            OnboardingTask.ADD_FAVORITE_APPS -> viewModel.startGridTask(task)?.let(homeActions.onOpenFavoritePicker)
            OnboardingTask.FILL_CONTAINER -> viewModel.startGridTask(task)?.let(homeActions.onOpenEditContainerDialog)
            OnboardingTask.ADD_WIDGET -> viewModel.startGridTask(task)?.let(homeActions.onAddWidgetClick)
            OnboardingTask.EXPLORE_SETTINGS -> homeActions.onSettingsClick()
        }
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
