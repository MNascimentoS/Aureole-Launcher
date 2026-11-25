package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.components.AureoleLogoRing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.isChecklistMinimized
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.nextPendingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.titleRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistSharedModifiers
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolvedTasks
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.showsLayoutHint
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.taskProgress

/**
 * Minimized checklist: a pill at the bottom with the progress halo, the next step and "Continue"; tapping anywhere on
 * it reopens the list.
 * It hides while [isOtherOverlayActive], so it never covers an editor the list itself opened.
 */
@Composable
fun OnboardingChecklistPillComponent(
    onboarding: OnboardingUiState,
    isOtherOverlayActive: Boolean,
    glass: OnboardingGlass,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!onboarding.isChecklistMinimized || isOtherOverlayActive) return
    OnboardingChecklistPill(onboarding = onboarding, glass = glass, onExpand = onExpand, modifier = modifier)
}

/**
 * The pill itself, with no visibility rules. Inside the checklist morph, [shared] ties its outline, halo and text
 * to the open list.
 */
@Composable
internal fun OnboardingChecklistPill(
    onboarding: OnboardingUiState,
    glass: OnboardingGlass,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier,
    shared: ChecklistSharedModifiers = ChecklistSharedModifiers()
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        OnboardingGlassCard(
            glass = glass,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(AureoleDS.dimens.medium)
                .then(shared.card)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
                .clickable(role = Role.Button, onClick = onExpand)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                if (onboarding.showsLayoutHint) {
                    AureoleText(
                        text = stringResource(R.string.home_onboarding_layout_bounds_hint),
                        modifier = shared.fading.fillMaxWidth(),
                        color = AureoleDS.colors.onSurfaceMedium,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                PillContent(onboarding = onboarding, shared = shared)
            }
        }
    }
}

/** Halo, "First steps · N/8" over "Next: <step>", and "Continue" at the end (RF-B1, D-08). */
@Composable
private fun PillContent(onboarding: OnboardingUiState, shared: ChecklistSharedModifiers) {
    val resolved = onboarding.resolvedTasks.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = AureoleDS.dimens.small),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleLogoRing(
            progress = onboarding.taskProgress,
            modifier = shared.halo.size(AureoleDS.dimens.iconLarge)
        )
        Column(modifier = Modifier.weight(1f)) {
            AureoleText(
                text = pluralStringResource(
                    R.plurals.home_onboarding_checklist_pill,
                    resolved,
                    resolved,
                    OnboardingTask.entries.size
                ),
                modifier = shared.progressText,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            )
            onboarding.nextPendingTask?.let { next ->
                AureoleText(
                    text = stringResource(
                        R.string.home_onboarding_checklist_pill_next,
                        AnnotatedString.fromHtml(stringResource(next.titleRes())).text
                    ),
                    modifier = shared.fading,
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.text,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                )
            }
        }
        AureoleText(
            text = stringResource(R.string.home_onboarding_checklist_pill_continue),
            modifier = shared.fading,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.text, maxLines = 1)
        )
    }
}

@AureolePreview
@Composable
private fun OnboardingChecklistPillPreview() {
    AureoleLauncherTheme {
        OnboardingChecklistPillComponent(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                isChecklistExpanded = false,
                completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_VISUAL)
            ),
            isOtherOverlayActive = false,
            glass = OnboardingGlass(),
            onExpand = {}
        )
    }
}
