package dev.mnascimentos.aureole.composable.undo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.model.asString
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

private const val ANIMATION_MS = 180
private const val ENTER_OFFSET_DIVISOR = 3
private const val FULL = 1f
private const val EMPTY = 0f
private const val CENTER = 0.5f

/**
 * Snackbar with a thumb-side "Undo" button and a 3 dp bar that drains over [UndoSnackbarState.durationMs].
 * Right-handed: button on the right, bar drains toward the left origin. Left-handed: mirrored.
 * [onTimeout] receives the id of the offer that ran out, so the owner can ignore stale ones.
 */
@Composable
fun AureoleUndoSnackbar(
    state: UndoSnackbarState?,
    isLeftHanded: Boolean,
    onUndo: () -> Unit,
    onTimeout: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Keeps the last content on screen while the exit animation runs.
    var lastState by remember { mutableStateOf(state) }
    if (state != null) lastState = state

    state?.let { current ->
        LaunchedEffect(current.id) {
            delay(current.durationMs)
            onTimeout(current.id)
        }
    }

    AnimatedVisibility(
        visible = state != null,
        modifier = modifier,
        enter = fadeIn(tween(ANIMATION_MS)) + slideInVertically(tween(ANIMATION_MS)) { it / ENTER_OFFSET_DIVISOR },
        exit = fadeOut(tween(ANIMATION_MS)) + slideOutVertically(tween(ANIMATION_MS)) { it / ENTER_OFFSET_DIVISOR },
    ) {
        lastState?.let { UndoSnackbarBody(it, isLeftHanded, onUndo) }
    }
}

@Composable
private fun UndoSnackbarBody(
    state: UndoSnackbarState,
    isLeftHanded: Boolean,
    onUndo: () -> Unit,
) {
    val dimens = AureoleDS.dimens
    val message = state.message
    val label = stringResource(R.string.home_undo_action)
    // Padding is smaller on the button side; the layout is mirrored explicitly, not by LayoutDirection.
    val buttonSide = dimens.xxSmall
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = dimens.itemHeightExtraLarge)
            .shadow(dimens.floatingElevation, RoundedCornerShape(dimens.radiusSmall))
            .clip(RoundedCornerShape(dimens.radiusSmall))
            .background(AureoleDS.colors.onSurfaceHigh)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.xSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isLeftHanded) buttonSide else dimens.medium,
                    end = if (isLeftHanded) dimens.medium else buttonSide,
                )
        ) {
            if (isLeftHanded) {
                UndoAction(label, onUndo)
                UndoMessage(message, TextAlign.End)
            } else {
                UndoMessage(message, TextAlign.Start)
                UndoAction(label, onUndo)
            }
        }
        UndoProgressBar(state, isLeftHanded, Modifier.align(Alignment.BottomStart))
    }
}

@Composable
private fun RowScope.UndoMessage(message: TextResource, align: TextAlign) {
    Text(
        text = message.asString(),
        color = AureoleDS.colors.background,
        style = AureoleDS.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        textAlign = align,
        modifier = Modifier
            .weight(FULL)
            .padding(vertical = AureoleDS.dimens.small)
            .semantics { liveRegion = LiveRegionMode.Polite },
    )
}

@Composable
private fun UndoAction(label: String, onUndo: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .clickable(role = Role.Button, onClick = onUndo)
            .padding(horizontal = AureoleDS.dimens.medium),
    ) {
        Text(
            text = label,
            color = AureoleDS.colors.surface,
            style = AureoleDS.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun UndoProgressBar(state: UndoSnackbarState, isLeftHanded: Boolean, modifier: Modifier = Modifier) {
    val progress = remember(state.id) { Animatable(FULL) }
    LaunchedEffect(state.id) {
        progress.animateTo(EMPTY, tween(state.durationMs.toInt(), easing = LinearEasing))
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.borderMax)
            .graphicsLayer {
                scaleX = progress.value
                transformOrigin = TransformOrigin(if (isLeftHanded) FULL else EMPTY, CENTER)
            }
            .background(AureoleDS.colors.surface)
    )
}

@AureolePreview
@Composable
private fun AureoleUndoSnackbarPreview() {
    AureoleLauncherTheme {
        AureoleUndoSnackbar(
            state = UndoSnackbarState(1, TextResource.fromString("Navegador saiu dos favoritos")),
            isLeftHanded = false,
            onUndo = {},
            onTimeout = {},
        )
    }
}
