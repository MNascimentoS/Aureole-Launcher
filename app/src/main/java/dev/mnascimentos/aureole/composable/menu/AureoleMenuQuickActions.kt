package dev.mnascimentos.aureole.composable.menu

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.composable.menu.model.MenuQuickAction
import dev.mnascimentos.aureole.composable.menu.model.thumbOrder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val IDLE_TILE_ALPHA = 0.6f
private const val DANGER_TILE_ALPHA = 0.18f
private const val FULL_CIRCLE = 360f
private const val ARC_START = -90f
private const val LABEL_MAX_LINES = 2
private const val DIVIDER_ALPHA = 0.5f

/**
 * Bottom bar of round buttons. [actions] come most used first: the first one sits at the thumb side (right
 * for right-handed users, left for left-handed users) and the last one at the opposite end.
 */
@Composable
fun AureoleMenuQuickActions(
    actions: List<MenuQuickAction>,
    modifier: Modifier = Modifier
) {
    val isLeftHanded = LocalHomeUiState.current.isLeftHandedMode
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = AureoleDS.colors.outline.copy(alpha = DIVIDER_ALPHA),
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            thumbOrder(actions, isLeftHanded).forEach { action ->
                QuickActionButton(action = action, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionButton(action: MenuQuickAction, modifier: Modifier = Modifier) {
    val labelColor = if (action.isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceMedium
    val progress = remember { Animatable(0f) }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .quickActionInput(action, progress)
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.xxSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        QuickActionCircle(action = action, progress = progress)
        AureoleText(
            text = action.label,
            color = labelColor,
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.minimal),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = LABEL_MAX_LINES,
                overflow = TextOverflow.Ellipsis
            )
        )
    }
}

@Composable
private fun QuickActionCircle(action: MenuQuickAction, progress: Animatable<Float, *>) {
    val tileColor = when {
        action.isActive -> AureoleDS.colors.onSurfaceHigh
        action.isDestructive -> AureoleDS.colors.error.copy(alpha = DANGER_TILE_ALPHA)
        else -> AureoleDS.colors.surfaceVariant.copy(alpha = IDLE_TILE_ALPHA)
    }
    val iconColor = when {
        action.isActive -> AureoleDS.colors.surface
        action.isDestructive -> AureoleDS.colors.error
        else -> AureoleDS.colors.onSurfaceMedium
    }
    val arcColor = AureoleDS.colors.error
    val strokeWidth = AureoleDS.dimens.borderMax
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.quickActionIconSize)
            .clip(CircleShape)
            .background(tileColor)
            .drawBehind {
                val sweep = progress.value * FULL_CIRCLE
                if (sweep > 0f) {
                    val stroke = strokeWidth.toPx()
                    drawArc(
                        color = arcColor,
                        startAngle = ARC_START,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = Offset(stroke / 2, stroke / 2),
                        size = Size(size.width - stroke, size.height - stroke),
                        style = Stroke(width = stroke)
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        QuickActionIcon(action = action, tint = iconColor)
    }
}

@Composable
private fun QuickActionIcon(action: MenuQuickAction, tint: Color) {
    action.icon(Modifier.size(AureoleDS.dimens.iconLarge), tint)
}

/** Tap for ordinary actions; hold with a draining ring for those that [MenuQuickAction.requiresHold]. */
@Composable
private fun Modifier.quickActionInput(action: MenuQuickAction, progress: Animatable<Float, *>): Modifier {
    if (!action.requiresHold) {
        return clickable(role = Role.Button, onClick = action.onTrigger)
    }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    return this
        .semantics {
            role = Role.Button
            onLongClick(label = action.label) {
                action.onTrigger()
                true
            }
        }
        .pointerInput(action) {
            awaitEachGesture {
                awaitFirstDown()
                val hold: Job = scope.launch {
                    progress.animateTo(1f, tween(MENU_HOLD_MS.toInt(), easing = LinearEasing))
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    action.onTrigger()
                }
                waitForUpOrCancellation()
                hold.cancel()
                scope.launch { progress.snapTo(0f) }
            }
        }
}

@AureolePreview
@Composable
private fun AureoleMenuQuickActionsPreview() {
    AureoleLauncherTheme {
        AureoleMenuQuickActions(
            actions = listOf(
                MenuQuickAction(
                    "Favorite",
                    { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) },
                    {},
                    isActive = true
                ),
                MenuQuickAction("Info", { m, c -> AureoleDS.icons.Info(modifier = m, tint = c) }, {}),
                MenuQuickAction(
                    "Hold to uninstall",
                    { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                    {},
                    isDestructive = true,
                    requiresHold = true
                )
            ),
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
