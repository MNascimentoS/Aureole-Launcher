package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Calendar
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Home
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.RoundCheck
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.titleRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingTaskStatus

private const val BREATH_MIN_ALPHA = 0.25f

/**
 * One "First steps" row. Pending rows are buttons; done rows show a check and no longer react. The next row of a
 * skippable task also has "Skip" at its end; a skipped row is struck through and taps back into a pending one.
 */
@Composable
fun OnboardingChecklistItemComponent(
    task: OnboardingTask,
    status: OnboardingTaskStatus,
    onClick: (OnboardingTask) -> Unit,
    modifier: Modifier = Modifier,
    onSkip: (OnboardingTask) -> Unit = {}
) {
    val isDone = status == OnboardingTaskStatus.DONE
    val isSkipped = status == OnboardingTaskStatus.SKIPPED
    val isNext = status == OnboardingTaskStatus.NEXT
    val title = AnnotatedString.fromHtml(stringResource(task.titleRes()))
    val description = stringResource(
        when {
            isDone -> R.string.home_onboarding_task_done_a11y
            isSkipped -> R.string.home_onboarding_task_skipped_a11y
            else -> R.string.home_onboarding_task_pending_a11y
        },
        title.text
    )
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    val showSkip = isNext && task.isSkippable
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(shape)
            .then(if (isNext) Modifier.breathingOutline(shape) else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .then(if (isDone) Modifier else Modifier.clickable(role = Role.Button) { onClick(task) })
                .semantics(mergeDescendants = true) { contentDescription = description }
                .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RowIcon(task = task, isDone = isDone, isSkipped = isSkipped)
            AureoleText(
                text = title,
                color = if (isDone || isSkipped) AureoleDS.colors.onSurfaceMedium else AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyLarge,
                    textDecoration = if (isSkipped) TextDecoration.LineThrough else null
                )
            )
        }
        if (showSkip) {
            OnboardingTextButton(
                text = stringResource(R.string.home_onboarding_task_skip),
                onClick = { onSkip(task) }
            )
        }
    }
}

@Composable
private fun RowIcon(task: OnboardingTask, isDone: Boolean, isSkipped: Boolean) {
    val iconModifier = Modifier.size(AureoleDS.dimens.iconMedium)
    Crossfade(targetState = isDone, label = "taskIcon") { done ->
        when {
            done -> AureoleDS.icons.RoundCheck(tint = AureoleDS.colors.badgeText, modifier = iconModifier)
            isSkipped -> AureoleDS.icons.Close(modifier = iconModifier, tint = AureoleDS.colors.onSurfaceMedium)
            else -> OnboardingTaskIcon(task = task)
        }
    }
}

@Composable
private fun OnboardingTaskIcon(task: OnboardingTask) {
    val tint = AureoleDS.colors.onSurfaceHigh
    val iconModifier = Modifier.size(AureoleDS.dimens.iconMedium)
    when (task) {
        OnboardingTask.SET_DEFAULT_HOME -> AureoleDS.icons.Home(iconModifier, tint)
        OnboardingTask.CHOOSE_PALETTE -> AureoleDS.icons.ColorFill(iconModifier, tint)
        OnboardingTask.CHOOSE_FONT -> AureoleDS.icons.Text(iconModifier, tint)
        OnboardingTask.ADD_CLOCK -> AureoleDS.icons.Calendar(iconModifier, tint)
        OnboardingTask.ADD_FAVORITE_APPS -> AureoleDS.icons.Favorite(iconModifier, tint)
        OnboardingTask.FILL_CONTAINER -> AureoleDS.icons.VerticalContainer(iconModifier, tint)
        OnboardingTask.ADD_WIDGET -> AureoleDS.icons.MultipleView(iconModifier, tint)
        OnboardingTask.ADJUST_LAYOUT -> AureoleDS.icons.Edit(iconModifier, tint)
        OnboardingTask.EXPLORE_SETTINGS -> AureoleDS.icons.Settings(iconModifier, tint)
    }
}

@Composable
private fun Modifier.breathingOutline(shape: RoundedCornerShape): Modifier {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val breath by rememberInfiniteTransition(label = "nextTask").animateFloat(
        initialValue = if (loopsEnabled) BREATH_MIN_ALPHA else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(OnboardingMotionSpec.NEXT_ITEM_BREATH_MS), RepeatMode.Reverse),
        label = "nextTaskAlpha"
    )
    return border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceHigh.copy(alpha = breath), shape)
}

@AureolePreview
@Composable
private fun OnboardingChecklistItemPreview() {
    AureoleLauncherTheme {
        OnboardingChecklistItemComponent(
            task = OnboardingTask.CHOOSE_PALETTE,
            status = OnboardingTaskStatus.NEXT,
            onClick = {}
        )
    }
}
