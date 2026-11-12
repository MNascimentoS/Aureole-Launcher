package dev.mnascimentos.aureole.feature.home.components.scrubber

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberMotion
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.prominentBadgeOffset
import kotlin.math.roundToInt

private const val GLASS_FILL_ALPHA = 0.92f
private const val GLASS_EDGE_ALPHA = 0.6f

/**
 * Large letter of the drawer scrub (RF-21): a glass bubble inward of and above the finger at [touchYPx], scaling and
 * fading in when the scrub starts, swapping letters quickly and fading out when the finger lifts. [selectedChar] is
 * null while nothing is selected; the last letter stays on the bubble during its exit.
 */
@Composable
fun BoxScope.ScrubberProminentBadge(
    selectedChar: Char?,
    touchYPx: Float,
    alignment: Alignment,
    isLeftHandedMode: Boolean
) {
    val density = LocalDensity.current
    var lastChar by remember { mutableStateOf(selectedChar) }
    var lastTouchYPx by remember { mutableFloatStateOf(touchYPx) }
    if (selectedChar != null) {
        lastChar = selectedChar
        lastTouchYPx = touchYPx
    }
    val offset = with(density) {
        prominentBadgeOffset(
            touchYPx = lastTouchYPx,
            badgeSizePx = ScrubberDimensions.ProminentBadgeSize.toPx(),
            insetPx = ScrubberDimensions.ProminentBadgeInset.toPx(),
            liftPx = ScrubberDimensions.ProminentBadgeLift.toPx(),
            isLeftHanded = isLeftHandedMode
        )
    }

    AnimatedVisibility(
        visible = selectedChar != null,
        enter = fadeIn(tween(ScrubberMotion.BADGE_ENTER_MS)) +
            scaleIn(tween(ScrubberMotion.BADGE_ENTER_MS), initialScale = ScrubberMotion.BADGE_START_SCALE),
        exit = fadeOut(tween(ScrubberMotion.BADGE_EXIT_MS)) +
            scaleOut(tween(ScrubberMotion.BADGE_EXIT_MS), targetScale = ScrubberMotion.BADGE_START_SCALE),
        modifier = Modifier
            .align(alignment)
            .offset { IntOffset(offset.xPx.roundToInt(), offset.yPx.roundToInt()) }
            // The scrubber column is narrower than the badge: measure it unbounded, anchored on the alphabet edge.
            .wrapContentSize(alignment, unbounded = true)
    ) {
        ProminentBadgeBubble(char = lastChar)
    }
}

@Composable
private fun ProminentBadgeBubble(char: Char?) {
    val colors = AureoleDS.colors
    Surface(
        shape = CircleShape,
        color = colors.surfaceVariant.copy(alpha = GLASS_FILL_ALPHA),
        contentColor = colors.onSurfaceHigh,
        border = BorderStroke(AureoleDS.dimens.borderMinimal, colors.outline.copy(alpha = GLASS_EDGE_ALPHA)),
        shadowElevation = ScrubberDimensions.BadgeElevation,
        modifier = Modifier.size(ScrubberDimensions.ProminentBadgeSize)
    ) {
        AnimatedContent(
            targetState = char,
            transitionSpec = {
                (
                    fadeIn(tween(ScrubberMotion.LETTER_SWAP_MS)) +
                        scaleIn(tween(ScrubberMotion.LETTER_SWAP_MS), ScrubberMotion.LETTER_SWAP_START_SCALE)
                    ) togetherWith fadeOut(tween(ScrubberMotion.LETTER_SWAP_MS))
            },
            contentAlignment = Alignment.Center,
            label = "scrubberBadgeLetter",
            modifier = Modifier.fillMaxSize()
        ) { letter ->
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                AureoleText(
                    text = letter?.toString().orEmpty(),
                    color = colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun ScrubberProminentBadgePreview() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .size(ScrubberDimensions.ProminentBadgeInset + ScrubberDimensions.ProminentBadgeSize)
                .background(AureoleDS.colors.background)
        ) {
            ScrubberProminentBadge(
                selectedChar = 'P',
                touchYPx = with(LocalDensity.current) { ScrubberDimensions.ProminentBadgeSize.toPx() * 2f },
                alignment = Alignment.TopEnd,
                isLeftHandedMode = false
            )
        }
    }
}
