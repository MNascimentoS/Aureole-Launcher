package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun WelcomeScreenScreenshotTest() {
    AureoleLauncherTheme {
        WelcomeScreen(onStart = {}, onSkipIntro = {})
    }
}
