package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec

/**
 * Slides its card in from outside the screen edge with a spring, the card spring unless [damping] and
 * [stiffness] say otherwise. [fromTop] picks the edge; previews and reduced motion show it settled.
 */
@Composable
fun OnboardingCardEntrance(
    fromTop: Boolean,
    modifier: Modifier = Modifier,
    damping: Float = OnboardingMotionSpec.CARD_DAMPING,
    stiffness: Float = OnboardingMotionSpec.CARD_STIFFNESS,
    content: @Composable BoxScope.() -> Unit
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val hidden = remember { Animatable(if (loopsEnabled) 1f else 0f) }
    LaunchedEffect(Unit) {
        hidden.animateTo(0f, spring(damping, stiffness))
    }
    Box(
        modifier = modifier.graphicsLayer {
            val travel = size.height * 2 * hidden.value
            translationY = if (fromTop) -travel else travel
        },
        content = content
    )
}

@AureolePreview
@Composable
private fun OnboardingCardEntrancePreview() {
    AureoleLauncherTheme {
        OnboardingCardEntrance(fromTop = false) {
            AureoleText(
                text = stringResource(R.string.home_onboarding_default_title),
                color = AureoleDS.colors.onSurfaceHigh
            )
        }
    }
}
