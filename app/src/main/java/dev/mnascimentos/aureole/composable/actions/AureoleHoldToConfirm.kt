package dev.mnascimentos.aureole.composable.actions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val PROGRESS_ALPHA = 0.28f
private const val LABEL_MAX_LINES = 2

/**
 * Makes an element fire [onTrigger] only after the finger stays down for [holdMs]. [progress] fills from 0 to 1
 * while holding (the caller draws the ring or bar) and goes back to 0 when the finger lifts early. Fires a long
 * press haptic when it completes. For TalkBack, exposes a long click action named [label] that fires at once.
 */
@Composable
fun Modifier.holdToConfirm(
    label: String,
    holdMs: Long,
    progress: Animatable<Float, *>,
    onTrigger: () -> Unit
): Modifier {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val trigger = rememberUpdatedState(onTrigger)
    return this
        .semantics {
            role = Role.Button
            onLongClick(label = label) {
                trigger.value()
                true
            }
        }
        .pointerInput(holdMs) {
            awaitEachGesture {
                awaitFirstDown()
                val hold: Job = scope.launch {
                    progress.animateTo(1f, tween(holdMs.toInt(), easing = LinearEasing))
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    trigger.value()
                }
                waitForUpOrCancellation()
                hold.cancel()
                scope.launch { progress.snapTo(0f) }
            }
        }
}

/**
 * Pill button of the error color for actions that cannot be undone: it only fires after [holdMs] of holding, with
 * a progress fill that grows from the start edge. A quick tap does nothing; the label says to hold.
 */
@Composable
fun AureoleHoldActionButton(
    label: String,
    onTrigger: () -> Unit,
    modifier: Modifier = Modifier,
    holdMs: Long = HOLD_CONFIRM_MS
) {
    val progress = remember { Animatable(0f) }
    val fillColor = AureoleDS.colors.surface.copy(alpha = PROGRESS_ALPHA)
    Box(
        modifier = modifier
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.error)
            .drawBehind {
                drawRect(color = fillColor, size = Size(size.width * progress.value, size.height))
            }
            .holdToConfirm(label = label, holdMs = holdMs, progress = progress, onTrigger = onTrigger)
            .padding(horizontal = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = AureoleDS.colors.surface,
            format = AureoleTextFormatParams(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = LABEL_MAX_LINES,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleHoldActionButtonPreview() {
    AureoleLauncherTheme {
        AureoleHoldActionButton(
            label = "Hold to reset",
            onTrigger = {},
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
