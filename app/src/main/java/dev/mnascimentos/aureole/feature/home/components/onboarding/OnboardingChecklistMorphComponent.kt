package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.checklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorphHistory
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorphTarget
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistSharedModifiers
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.nextMorphTarget
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.usesBubbleMotion

private const val CARD_KEY = "onboarding-checklist"
private const val HALO_KEY = "onboarding-halo"
private const val PROGRESS_TEXT_KEY = "onboarding-progress-text"

/**
 * The "First steps" list and its minimized pill, as one shared element: minimizing shrinks the card into the
 * pill (the rows fade first, the halo and the progress text travel, the scrim fades) and tapping the pill grows
 * it back. After a task the peek bubble sits in the pill's place; when its ring closes it opens into the list (or the
 * pill) the same way, a container transform of ~550 ms with the logo travelling to the list's halo and the rows
 * fading in late (RF-13). Showing or hiding the layer for anything else (a picker, another overlay, the end of the
 * tour) keeps the plain crossfade of the other onboarding surfaces. With animations off the swap is immediate.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun OnboardingChecklistMorphComponent(
    onboarding: OnboardingUiState,
    isOtherOverlayActive: Boolean,
    glass: OnboardingGlass,
    actions: OnboardingActions,
    modifier: Modifier = Modifier
) {
    val morph = onboarding.checklistMorph(isOtherOverlayActive)
    val history = remember { ChecklistMorphHistory() }
    val target = remember(morph) { nextMorphTarget(history.last, morph).also { history.last = it } }
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = target,
            contentKey = { it.morph },
            transitionSpec = { checklistMorphTransform() },
            label = "checklistMorph"
        ) { state ->
            val shared = checklistSharedModifiers(this, state)
            when (state.morph) {
                ChecklistMorph.EXPANDED -> OnboardingChecklistComponent(
                    onboarding = onboarding,
                    actions = actions,
                    glass = glass,
                    shared = shared
                )
                ChecklistMorph.MINIMIZED -> OnboardingChecklistPill(
                    onboarding = onboarding,
                    glass = glass,
                    onExpand = { actions.onSetChecklistExpanded(true) },
                    shared = shared
                )
                ChecklistMorph.PEEK -> OnboardingPeekBubbleComponent(glass = glass, shared = shared)
                ChecklistMorph.HIDDEN -> Unit
            }
        }
    }
}

/**
 * List, pill and bubble into one another: no crossfade of their own, the shared elements animate. List or bubble ↔
 * nothing: the surfaces' crossfade.
 */
private fun AnimatedContentTransitionScope<ChecklistMorphTarget>.checklistMorphTransform(): ContentTransform {
    val ends = setOf(initialState.morph, targetState.morph)
    val isShownOrHidden = ChecklistMorph.HIDDEN in ends &&
        (ChecklistMorph.EXPANDED in ends || ChecklistMorph.PEEK in ends)
    val transform = if (isShownOrHidden) {
        fadeIn(tween(OnboardingMotionSpec.TEXT_ENTER_MS)) togetherWith
            fadeOut(tween(OnboardingMotionSpec.WELCOME_EXIT_MS))
    } else {
        EnterTransition.None togetherWith ExitTransition.None
    }
    return transform.using(null)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.checklistSharedModifiers(
    scope: AnimatedVisibilityScope,
    target: ChecklistMorphTarget
): ChecklistSharedModifiers {
    val isBubbleMotion = target.usesBubbleMotion
    val boundsTransform = BoundsTransform { _, _ ->
        if (isBubbleMotion) {
            tween(OnboardingMotionSpec.BUBBLE_MORPH_MS, easing = FastOutSlowInEasing)
        } else {
            spring(
                dampingRatio = OnboardingMotionSpec.PILL_DAMPING,
                stiffness = OnboardingMotionSpec.CHECKLIST_STIFFNESS
            )
        }
    }
    val fadeSpring = spring<Float>(stiffness = OnboardingMotionSpec.CHECKLIST_STIFFNESS)
    val listFade = tween<Float>(OnboardingMotionSpec.CHECKLIST_LIST_FADE_MS)
    // Out of the bubble the rows wait for ~45% of the movement before they fade in.
    val listEnter = if (target.cameFromBubble) {
        fadeIn(
            tween(
                durationMillis = OnboardingMotionSpec.BUBBLE_LIST_FADE_MS,
                delayMillis = OnboardingMotionSpec.BUBBLE_LIST_FADE_DELAY_MS
            )
        )
    } else {
        fadeIn(listFade)
    }
    // Drawn in place, not in the shared overlay, so the glass blur keeps sampling the home under the real bounds.
    return ChecklistSharedModifiers(
        card = Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(CARD_KEY),
            animatedVisibilityScope = scope,
            boundsTransform = boundsTransform,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
            renderInOverlayDuringTransition = false
        ),
        halo = Modifier.sharedElement(
            sharedContentState = rememberSharedContentState(HALO_KEY),
            animatedVisibilityScope = scope,
            boundsTransform = boundsTransform,
            renderInOverlayDuringTransition = false
        ),
        progressText = Modifier.sharedBounds(
            sharedContentState = rememberSharedContentState(PROGRESS_TEXT_KEY),
            animatedVisibilityScope = scope,
            boundsTransform = boundsTransform,
            renderInOverlayDuringTransition = false
        ),
        fading = with(scope) { Modifier.animateEnterExit(enter = listEnter, exit = fadeOut(listFade)) },
        backdrop = with(scope) { Modifier.animateEnterExit(enter = fadeIn(fadeSpring), exit = fadeOut(fadeSpring)) },
        animateEntrance = !target.cameFromPill && !target.cameFromBubble
    )
}

@AureolePreview
@Composable
private fun OnboardingChecklistMorphPreview() {
    AureoleLauncherTheme {
        OnboardingChecklistMorphComponent(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                isChecklistExpanded = false,
                completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME)
            ),
            isOtherOverlayActive = false,
            glass = OnboardingGlass(),
            actions = OnboardingActions()
        )
    }
}
