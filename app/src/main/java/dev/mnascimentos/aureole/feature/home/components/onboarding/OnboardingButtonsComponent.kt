package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Filled call to action: high-contrast pill, as in the storyboard. */
@Composable
fun OnboardingPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.onSurfaceHigh)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = text,
            color = AureoleDS.colors.background,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.text,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}

/** Quiet secondary action: text only, still a full touch target. */
@Composable
fun OnboardingTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = AureoleDS.dimens.minTouchTarget, minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = text,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.text, textAlign = TextAlign.Center)
        )
    }
}

@AureolePreview
@Composable
private fun OnboardingButtonsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier.padding(AureoleDS.dimens.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingPrimaryButton(text = stringResource(R.string.home_onboarding_welcome_start), onClick = {})
            OnboardingTextButton(text = stringResource(R.string.home_onboarding_welcome_skip), onClick = {})
        }
    }
}
