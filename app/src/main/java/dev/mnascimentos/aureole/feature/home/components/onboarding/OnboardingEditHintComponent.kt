package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.EDIT_HINT_TEXT_MS
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.editHintBandCols
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.editHintBandRows
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.editHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintBand
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintSaveParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintSide
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import kotlinx.coroutines.delay

private const val GESTURE_PILL_ALPHA = 0.25f

/** The hint card folding into the Save pill: fade and resize (RF-03). */
private const val EDIT_HINT_FOLD_MS = 280

/** Width of the folded save pill with its margin, in touch targets: the part of the edge the pill takes. */
private const val PILL_WIDTH_IN_TOUCH_TARGETS = 4

/**
 * Shown while a checklist task keeps the grid in edit mode: how to adjust the new item, and the save that
 * completes the task. Sits in the thumb zone at the bottom (the top when items cover the bottom), and folds
 * into a "Save" pill after 3 s or the first drag or resize; during a gesture the pill fades and lets touches by.
 */
@Composable
fun OnboardingEditHintComponent(
    config: EditHintConfig,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGesturing = LocalHomeActions.current.gridGestureTracker.isGesturing
    var isFolded by remember { mutableStateOf(config.startsFolded) }
    LaunchedEffect(config.isVisible) {
        if (config.isVisible && !isFolded) {
            delay(EDIT_HINT_TEXT_MS)
            isFolded = true
        }
    }
    LaunchedEffect(isGesturing) { if (isGesturing) isFolded = true }
    if (!config.isVisible) return
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val navBarDp = with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
        val bandDp = AureoleDS.dimens.minTouchTarget + AureoleDS.dimens.medium * 2 + navBarDp
        val band = EditHintBand(
            rows = editHintBandRows(bandDp.value, maxHeight.value, config.grid.maxRows),
            cols = editHintBandCols(
                bandDp = (AureoleDS.dimens.minTouchTarget * PILL_WIDTH_IN_TOUCH_TARGETS).value,
                totalDp = maxWidth.value,
                maxCols = config.grid.maxCols,
                side = config.side
            )
        )
        val edge = editHintEdge(config.grid, band)
        Box(
            modifier = Modifier.fillMaxSize().then(
                if (edge == EditHintEdge.TOP) Modifier.statusBarsPadding() else Modifier.navigationBarsPadding()
            ),
            contentAlignment = config.alignment(edge)
        ) {
            EditHintBody(
                config = config,
                isFolded = isFolded,
                isGesturing = isGesturing,
                onSave = onSave
            )
        }
    }
}

private val EditHintConfig.side: EditHintSide
    get() = when {
        !isLandscape -> EditHintSide.CENTER
        isLeftHanded -> EditHintSide.START
        else -> EditHintSide.END
    }

private fun EditHintConfig.alignment(edge: EditHintEdge): Alignment = when {
    !isLandscape -> if (edge == EditHintEdge.TOP) Alignment.TopCenter else Alignment.BottomCenter
    isLeftHanded -> if (edge == EditHintEdge.TOP) Alignment.TopStart else Alignment.BottomStart
    else -> if (edge == EditHintEdge.TOP) Alignment.TopEnd else Alignment.BottomEnd
}

@Composable
private fun EditHintBody(
    config: EditHintConfig,
    isFolded: Boolean,
    isGesturing: Boolean,
    onSave: () -> Unit
) {
    val hint = stringResource(R.string.home_onboarding_edit_hint)
    val save = stringResource(R.string.home_onboarding_edit_save)
    val pillAlpha = Modifier.alpha(if (isGesturing) GESTURE_PILL_ALPHA else 1f)
    val foldMs = if (rememberOnboardingLoopsEnabled()) EDIT_HINT_FOLD_MS else 0
    AnimatedContent(
        targetState = isFolded,
        transitionSpec = {
            (fadeIn(tween(foldMs)) togetherWith fadeOut(tween(foldMs)))
                .using(SizeTransform(clip = false) { _, _ -> tween(foldMs) })
        },
        label = "editHintFold"
    ) { folded ->
        if (folded) {
            EditHintSaveButton(
                params = EditHintSaveParams(save, hint, isGesturing),
                onClick = onSave,
                modifier = pillAlpha.padding(AureoleDS.dimens.medium)
            )
        } else {
            EditHintCard(
                config = config,
                params = EditHintSaveParams(save, hint, isGesturing),
                onSave = onSave,
                modifier = pillAlpha
            )
        }
    }
}

@Composable
private fun EditHintCard(
    config: EditHintConfig,
    params: EditHintSaveParams,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingGlassCard(
        glass = config.glass,
        modifier = modifier.widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH).padding(AureoleDS.dimens.medium)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AureoleText(
                text = params.hint,
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(style = AureoleDS.typography.text)
            )
            EditHintSaveButton(params = params, onClick = onSave, modifier = Modifier.width(IntrinsicSize.Max))
        }
    }
}

/** The save pill; while a gesture runs it takes no touch, so the drag underneath is never stolen. */
@Composable
private fun EditHintSaveButton(params: EditHintSaveParams, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.onSurfaceHigh)
            .then(if (params.isGesturing) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
            .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.small)
            .semantics(mergeDescendants = true) { stateDescription = params.hint },
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Check(tint = AureoleDS.colors.background)
        AureoleText(
            text = params.text,
            color = AureoleDS.colors.background,
            format = AureoleTextFormatParams(style = AureoleDS.typography.text, fontWeight = FontWeight.Bold)
        )
    }
}

@AureolePreview
@Composable
private fun OnboardingEditHintPreview() {
    AureoleLauncherTheme {
        OnboardingEditHintComponent(config = EditHintConfig(isVisible = true), onSave = {})
    }
}
