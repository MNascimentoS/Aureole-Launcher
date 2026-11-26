package dev.mnascimentos.aureole.composable.tips

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.composable.tips.model.TipCardMetrics
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.rememberSystemAnimationsEnabled

/**
 * Draws [content] with a ring that pulses around it while [isActive] (grupo-apps-pastas RF-F3): the target of the
 * tip shown. The ring fades from 0.7 to 0 and grows a little, every 1.6 s; with the system's "Remove animations" it
 * is not drawn at all. It takes no touch: what is below still gets the taps.
 */
@Composable
fun TipPulse(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = AureoleDS.dimens.radiusMedium,
    content: @Composable BoxScope.() -> Unit
) {
    val showsPulse = isActive && rememberSystemAnimationsEnabled()
    if (!showsPulse) {
        Box(modifier = modifier, content = content)
        return
    }
    val transition = rememberInfiniteTransition(label = "tipPulse")
    val beat = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(TipCardMetrics.PULSE_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "tipPulseBeat"
    )
    val color = AureoleDS.colors.onSurfaceHigh
    val stroke = AureoleDS.dimens.borderMax
    Box(
        modifier = modifier.drawBehind {
            val progress = beat.value
            val grow = size.minDimension * TipCardMetrics.PULSE_GROWTH * progress
            val radius = cornerRadius.toPx() + grow
            drawRoundRect(
                color = color.copy(alpha = TipCardMetrics.PULSE_START_ALPHA * (1f - progress)),
                topLeft = Offset(-grow, -grow),
                size = Size(size.width + grow * 2, size.height + grow * 2),
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = stroke.toPx())
            )
        },
        content = content
    )
}

@AureolePreview
@Composable
private fun TipPulsePreview() {
    AureoleLauncherTheme {
        TipPulse(isActive = true, modifier = Modifier.padding(AureoleDS.dimens.large)) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.sizeLarge)
                    .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            )
        }
    }
}
