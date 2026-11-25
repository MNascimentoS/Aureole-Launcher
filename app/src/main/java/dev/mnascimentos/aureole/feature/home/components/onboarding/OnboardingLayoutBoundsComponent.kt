package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * While the onboarding adds the home components, a line says the sizes can be adjusted later, in the "Adjust your
 * layout" step (D-03). The dashed frame around the whole screen is gone: only the new item is outlined now
 * (onboarding-redesenho RF-D2). It only fades, never slides (RF-D6), and draws no pointer input, so the home
 * underneath keeps every touch.
 */
@Composable
fun OnboardingLayoutBoundsComponent(isVisible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut(), modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(AureoleDS.dimens.small)
        ) {
            AureoleText(
                text = stringResource(R.string.home_onboarding_layout_bounds_hint),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.sizeLarge)
                    .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, textAlign = TextAlign.Center)
            )
        }
    }
}

@AureolePreview
@Composable
private fun OnboardingLayoutBoundsPreview() {
    AureoleLauncherTheme {
        OnboardingLayoutBoundsComponent(isVisible = true)
    }
}
