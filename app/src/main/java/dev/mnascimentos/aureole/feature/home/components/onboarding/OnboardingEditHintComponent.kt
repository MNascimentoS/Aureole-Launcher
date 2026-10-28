package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec

/**
 * Shown at the top while a checklist task keeps the grid in edit mode: how to adjust the new item,
 * and the save that completes the task.
 */
@Composable
fun OnboardingEditHintComponent(
    isVisible: Boolean,
    glass: OnboardingGlass,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        OnboardingGlassCard(
            glass = glass,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .statusBarsPadding()
                .padding(AureoleDS.dimens.medium)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AureoleText(
                    text = stringResource(R.string.home_onboarding_edit_hint),
                    color = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.weight(1f),
                    format = AureoleTextFormatParams(style = AureoleDS.typography.text)
                )
                OnboardingPrimaryButton(
                    text = stringResource(R.string.home_onboarding_edit_save),
                    onClick = onSave,
                    modifier = Modifier.width(IntrinsicSize.Max)
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun OnboardingEditHintPreview() {
    AureoleLauncherTheme {
        OnboardingEditHintComponent(isVisible = true, glass = OnboardingGlass(), onSave = {})
    }
}
