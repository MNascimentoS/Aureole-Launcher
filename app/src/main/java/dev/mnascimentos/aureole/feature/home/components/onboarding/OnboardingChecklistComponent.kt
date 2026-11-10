package dev.mnascimentos.aureole.feature.home.components.onboarding

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHaloProgress
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.nextPendingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.taskProgress
import kotlinx.coroutines.delay

private const val SCRIM_ALPHA = 0.45f

/**
 * "First steps" list over a scrim. It rises from below the screen with the Figma overshoot and
 * settles in the upper third; tapping the scrim minimizes it to the pill.
 */
@Composable
fun OnboardingChecklistComponent(
    onboarding: OnboardingUiState,
    actions: OnboardingActions,
    glass: OnboardingGlass,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background.copy(alpha = SCRIM_ALPHA))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                actions.onSetChecklistExpanded(false)
            },
        contentAlignment = Alignment.TopCenter
    ) {
        OnboardingCardEntrance(
            fromTop = false,
            damping = OnboardingMotionSpec.CHECKLIST_DAMPING,
            stiffness = OnboardingMotionSpec.CHECKLIST_STIFFNESS,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .statusBarsPadding()
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxLarge)
        ) {
            OnboardingGlassCard(
                glass = glass,
                modifier = Modifier
                    .fillMaxWidth()
                    // Taps on the card itself must not reach the scrim and minimize the list.
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
            ) {
                ChecklistCardContent(onboarding = onboarding, actions = actions)
            }
        }
    }
}

@Composable
private fun ChecklistCardContent(onboarding: OnboardingUiState, actions: OnboardingActions) {
    val next = onboarding.nextPendingTask
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        ChecklistHeader(onboarding = onboarding)
        OnboardingTask.entries.forEachIndexed { index, task ->
            OnboardingChecklistItemComponent(
                task = task,
                isDone = task in onboarding.completedTasks,
                isNext = task == next,
                onClick = actions.onTaskClick,
                modifier = Modifier.checklistItemEntrance(staggerIndex = index)
            )
        }
        ChecklistFooter(actions = actions)
    }
}

@Composable
private fun ChecklistHeader(onboarding: OnboardingUiState) {
    val total = OnboardingTask.entries.size
    val progressText =
        stringResource(R.string.home_onboarding_checklist_progress, onboarding.completedTasks.size, total)
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
    ) {
        AureoleHaloProgress(
            progress = onboarding.taskProgress,
            size = AureoleDS.dimens.iconXLarge,
            contentDescription = progressText
        )
        Column {
            AureoleText(
                text = stringResource(R.string.home_onboarding_checklist_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
            )
            AureoleText(
                text = progressText,
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.text)
            )
        }
    }
}

@Composable
private fun ChecklistFooter(actions: OnboardingActions) {
    val context = LocalContext.current
    val dismissedMessage = stringResource(R.string.home_onboarding_checklist_dismissed)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        OnboardingTextButton(
            text = stringResource(R.string.home_onboarding_checklist_dismiss),
            onClick = {
                actions.onDismissChecklist()
                Toast.makeText(context, dismissedMessage, Toast.LENGTH_LONG).show()
            }
        )
        OnboardingTextButton(
            text = stringResource(R.string.home_onboarding_checklist_minimize),
            onClick = { actions.onSetChecklistExpanded(false) }
        )
    }
}

/** Fades the row in and lifts it into place, [staggerIndex] rows after the list starts. */
@Composable
private fun Modifier.checklistItemEntrance(staggerIndex: Int): Modifier {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val progress = remember { Animatable(if (loopsEnabled) 0f else 1f) }
    LaunchedEffect(Unit) {
        delay(staggerIndex.toLong() * OnboardingMotionSpec.CHECKLIST_STAGGER_MS)
        progress.animateTo(1f, spring(OnboardingMotionSpec.CHECKLIST_DAMPING, OnboardingMotionSpec.CHECKLIST_STIFFNESS))
    }
    val offset = OnboardingMotionSpec.CHECKLIST_ITEM_OFFSET
    return graphicsLayer {
        alpha = progress.value.coerceIn(0f, 1f)
        translationY = offset.toPx() * (1f - progress.value)
    }
}

@AureolePreview
@Composable
private fun OnboardingChecklistPreview() {
    AureoleLauncherTheme {
        OnboardingChecklistComponent(
            onboarding = OnboardingUiState(
                completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_PALETTE)
            ),
            actions = OnboardingActions(),
            glass = OnboardingGlass()
        )
    }
}
