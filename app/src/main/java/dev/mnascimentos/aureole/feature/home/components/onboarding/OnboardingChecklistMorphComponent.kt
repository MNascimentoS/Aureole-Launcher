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
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.checklistCollapseDelayMillis
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.checklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorphHistory
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorphTarget
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistSharedModifiers
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.atRest
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.nextMorphTarget
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CARD_KEY = "onboarding-checklist"
private const val HALO_KEY = "onboarding-halo"
private const val PROGRESS_TEXT_KEY = "onboarding-progress-text"

/**
 * The "First steps" list and its minimized pill, as one shared element: minimizing shrinks the card into the
 * pill (the rows fade first, the halo and the progress text travel, the scrim fades) and tapping the pill grows
 * it back. After a task the pill stays while the home shows alone with the new item highlighted (the peek bubble
 * is gone, onboarding-redesenho RF-D2), and then grows into the list the same way. Showing or hiding the layer for
 * anything else (a picker, another overlay, the end of the tour) keeps the plain crossfade of the other onboarding
 * surfaces. With animations off the swap is immediate.
 * Pill ↔ list is a ~300 ms emphasized tween with the blur off while it moves (D-19); a tapped step first shrinks
 * the list into the pill and only then opens its screen (RF-B5).
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
    // Out here, not in the list: the list leaves the composition while it shrinks, and the launch must survive it.
    val scope = rememberCoroutineScope()
    val listActions = remember(actions) {
        actions.copy(
            onTaskClick = { task ->
                actions.onSetChecklistExpanded(false)
                scope.launch {
                    delay(checklistCollapseDelayMillis())
                    actions.onTaskClick(task)
                }
            }
        )
    }
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = target,
            contentKey = { it.morph },
            transitionSpec = { checklistMorphTransform() },
            label = "checklistMorph"
        ) { state ->
            val shared = checklistSharedModifiers(this, state)
            val stateGlass = glass.atRest(isMoving = transition.isRunning)
            when (state.morph) {
                ChecklistMorph.EXPANDED -> OnboardingChecklistComponent(
                    onboarding = onboarding,
                    actions = listActions,
                    glass = stateGlass,
                    shared = shared
                )
                ChecklistMorph.MINIMIZED -> OnboardingChecklistPill(
                    onboarding = onboarding,
                    glass = stateGlass,
                    onExpand = { actions.onSetChecklistExpanded(true) },
                    shared = shared
                )
                ChecklistMorph.HIDDEN -> Unit
            }
        }
    }
}

/**
 * List and pill into one another: no crossfade of their own, the shared elements animate. List ↔ nothing: the
 * surfaces' crossfade.
 */
private fun AnimatedContentTransitionScope<ChecklistMorphTarget>.checklistMorphTransform(): ContentTransform {
    val ends = setOf(initialState.morph, targetState.morph)
    val isShownOrHidden = ChecklistMorph.HIDDEN in ends && ChecklistMorph.EXPANDED in ends
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
    val boundsTransform = BoundsTransform { _, _ ->
        tween(OnboardingMotionSpec.CHECKLIST_MORPH_MS, easing = OnboardingMotionSpec.CHECKLIST_MORPH_EASING)
    }
    val fadeSpring = spring<Float>(stiffness = OnboardingMotionSpec.CHECKLIST_STIFFNESS)
    val listFade = tween<Float>(OnboardingMotionSpec.CHECKLIST_LIST_FADE_MS)
    val listEnter = fadeIn(listFade)
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
        animateEntrance = !target.cameFromPill
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
