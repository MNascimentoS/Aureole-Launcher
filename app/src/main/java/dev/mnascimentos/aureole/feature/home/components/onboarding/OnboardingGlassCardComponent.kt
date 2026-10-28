package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass

private const val SOLID_SURFACE_ALPHA = 0.94f

/** Frosted card over the live home; a near-opaque surface when blur is unavailable. */
@Composable
fun OnboardingGlassCard(
    glass: OnboardingGlass,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val hazeState = glass.hazeState
    val surface = if (glass.isHazeEnabled && hazeState != null) {
        Modifier
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 24.dp,
                    tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = glass.hazeOpacity))
                )
            )
            .background(AureoleDS.colors.surface.copy(alpha = glass.hazeOpacity / 2))
    } else {
        Modifier.background(AureoleDS.colors.surface.copy(alpha = SOLID_SURFACE_ALPHA))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(surface)
            .padding(AureoleDS.dimens.medium),
        content = content
    )
}

@AureolePreview
@Composable
private fun OnboardingGlassCardPreview() {
    AureoleLauncherTheme {
        OnboardingGlassCard(glass = OnboardingGlass()) {
            AureoleText(
                text = stringResource(R.string.home_onboarding_welcome_title),
                color = AureoleDS.colors.onSurfaceHigh
            )
        }
    }
}
