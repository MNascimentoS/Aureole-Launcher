package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.GestureCoachConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

@PreviewTest
@AureolePreview
@Composable
private fun WelcomeScreenScreenshotTest() {
    AureoleLauncherTheme {
        WelcomeScreen(onStart = {}, onSkipIntro = {})
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun DefaultHomeStepScreenshotTest() {
    AureoleLauncherTheme {
        DefaultHomeStepComponent(onboarding = OnboardingUiState(), actions = OnboardingActions(), glass = OnboardingGlass())
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun DefaultHomeStepAfterAttemptScreenshotTest() {
    AureoleLauncherTheme {
        DefaultHomeStepComponent(
            onboarding = OnboardingUiState(defaultHomeAttempted = true),
            actions = OnboardingActions(),
            glass = OnboardingGlass()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun GestureCoachAppsScreenshotTest() {
    AureoleLauncherTheme {
        GestureCoachComponent(
            onboarding = OnboardingUiState(),
            actions = OnboardingActions(),
            glass = OnboardingGlass(),
            config = GestureCoachConfig()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun GestureCoachScrubScreenshotTest() {
    AureoleLauncherTheme {
        GestureCoachComponent(
            onboarding = OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS)),
            actions = OnboardingActions(),
            glass = OnboardingGlass(),
            config = GestureCoachConfig()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun GestureCoachCustomizeScreenshotTest() {
    AureoleLauncherTheme {
        GestureCoachComponent(
            onboarding = OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)
            ),
            actions = OnboardingActions(),
            glass = OnboardingGlass(),
            config = GestureCoachConfig()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun GestureCoachSuccessScreenshotTest() {
    AureoleLauncherTheme {
        GestureCoachComponent(
            onboarding = OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS),
                succeededStep = GestureCoachStep.OPEN_APPS
            ),
            actions = OnboardingActions(),
            glass = OnboardingGlass(),
            config = GestureCoachConfig()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun OnboardingChecklistOneDoneScreenshotTest() {
    AureoleLauncherTheme {
        OnboardingChecklistComponent(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME)
            ),
            actions = OnboardingActions(),
            glass = OnboardingGlass()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun OnboardingChecklistFourDoneScreenshotTest() {
    AureoleLauncherTheme {
        OnboardingChecklistComponent(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                completedTasks = setOf(
                    OnboardingTask.SET_DEFAULT_HOME,
                    OnboardingTask.CHOOSE_PALETTE,
                    OnboardingTask.CHOOSE_FONT,
                    OnboardingTask.ADD_FAVORITE_APPS
                )
            ),
            actions = OnboardingActions(),
            glass = OnboardingGlass()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun OnboardingChecklistPillScreenshotTest() {
    AureoleLauncherTheme {
        OnboardingChecklistPillComponent(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                isChecklistExpanded = false,
                completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_PALETTE)
            ),
            isOtherOverlayActive = false,
            glass = OnboardingGlass(),
            onExpand = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun OnboardingPalettePickerScreenshotTest() {
    AureoleLauncherTheme {
        OnboardingAppearancePickerComponent(
            picker = AppearancePicker.PALETTE,
            selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
            actions = OnboardingActions()
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun OnboardingCelebrationScreenshotTest() {
    AureoleLauncherTheme {
        OnboardingCelebrationComponent(onFinished = {})
    }
}
