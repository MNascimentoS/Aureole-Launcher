package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
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
