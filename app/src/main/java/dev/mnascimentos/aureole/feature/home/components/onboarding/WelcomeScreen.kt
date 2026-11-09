package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.playWelcomeIgnition
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.WelcomeMotion
import kotlinx.coroutines.launch

private const val BACKDROP_ALPHA = 0.88f
private const val BACKDROP_GLOW_ALPHA = 0.35f

/**
 * "Halo ignition": the first screen of a fresh install. It covers the first app sync, so the
 * home never shows a bare spinner. Starting plays the exit burst before moving on.
 */
@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    onSkipIntro: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val motion = remember { WelcomeMotion(isSettled = !loopsEnabled) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(loopsEnabled) {
        if (loopsEnabled) playWelcomeIgnition(motion)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background.copy(alpha = BACKDROP_ALPHA))
            .background(
                Brush.radialGradient(
                    listOf(
                        AureoleDS.colors.surfaceVariant.copy(alpha = BACKDROP_GLOW_ALPHA),
                        AureoleDS.colors.surfaceVariant.copy(alpha = 0f)
                    )
                )
            )
            // Blocks touches from reaching the home below.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        WelcomeStarsComponent(twinkle = { motion.twinkle.value }, modifier = Modifier.matchParentSize())
        WelcomeContent(
            motion = motion,
            onStart = {
                if (!loopsEnabled) {
                    onStart()
                } else {
                    scope.launch {
                        motion.exit.animateTo(
                            1f,
                            tween(OnboardingMotionSpec.WELCOME_EXIT_MS, easing = FastOutLinearInEasing)
                        )
                        onStart()
                    }
                }
            },
            onSkipIntro = onSkipIntro
        )
    }
}

@Composable
private fun WelcomeContent(
    motion: WelcomeMotion,
    onStart: () -> Unit,
    onSkipIntro: () -> Unit
) {
    val offsetPx = with(LocalDensity.current) { OnboardingMotionSpec.TEXT_ENTER_OFFSET.toPx() }
    val entering: (Int) -> Modifier = { index ->
        Modifier.graphicsLayer {
            val progress = motion.texts[index].value
            alpha = progress * (1f - motion.exit.value)
            translationY = (1f - progress) * offsetPx
        }
    }
    Column(
        modifier = Modifier
            .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
            .padding(horizontal = AureoleDS.dimens.xLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeHaloComponent(
            motion = motion,
            modifier = Modifier.graphicsLayer {
                scaleX = motion.haloBurst
                scaleY = motion.haloBurst
                alpha = 1f - motion.exit.value
            }
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xLarge))
        AureoleText(
            text = stringResource(R.string.home_onboarding_welcome_title),
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = entering(0).semantics { heading() },
            format = AureoleTextFormatParams(style = AureoleDS.typography.displaySmall, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        AureoleText(
            text = stringResource(R.string.home_onboarding_welcome_subtitle),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = entering(1),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxLarge))
        Column(modifier = entering(2), horizontalAlignment = Alignment.CenterHorizontally) {
            OnboardingPrimaryButton(text = stringResource(R.string.home_onboarding_welcome_start), onClick = onStart)
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
            OnboardingTextButton(text = stringResource(R.string.home_onboarding_welcome_skip), onClick = onSkipIntro)
        }
    }
}

@AureolePreview
@Composable
private fun WelcomeScreenPreview() {
    AureoleLauncherTheme {
        WelcomeScreen(onStart = {}, onSkipIntro = {})
    }
}
