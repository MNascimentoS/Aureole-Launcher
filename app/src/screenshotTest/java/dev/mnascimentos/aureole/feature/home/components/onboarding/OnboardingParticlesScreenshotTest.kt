package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.drawParticles

private const val HALFWAY = 0.5f

/** The "All set" celebration burst caught halfway: dots on a spiral flying out and fading. */
@PreviewTest
@AureolePreview
@Composable
private fun OnboardingCelebrationBurstScreenshotTest() {
    AureoleLauncherTheme {
        val color = AureoleDS.colors.onSurfaceHigh
        Canvas(modifier = Modifier.fillMaxSize().background(AureoleDS.colors.background)) {
            drawParticles(HALFWAY, color)
        }
    }
}
