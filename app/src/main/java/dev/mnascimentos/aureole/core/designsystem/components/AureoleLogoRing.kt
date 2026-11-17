package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.model.LOGO_RING_LOGO_FRACTION
import dev.mnascimentos.aureole.core.designsystem.model.isLogoRingSpinning
import dev.mnascimentos.aureole.core.designsystem.model.logoRingRangeInfo
import dev.mnascimentos.aureole.core.designsystem.model.logoRingSweepFraction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.rememberSystemAnimationsEnabled

private const val STROKE_FRACTION = 0.08f
private const val TRACK_ALPHA = 0.35f
private const val START_ANGLE = -90f
private const val FULL_SWEEP = 360f
private const val SPIN_MS = 1100

/**
 * The Aureole logo inside its ring, the one way the app shows that something is on its way (RF-12).
 *
 * @param progress Completed fraction, clamped to 0..1 and animated with a spring; `null` is a wait with no known
 * end, and the arc spins. With the system animations off the arc stands still.
 * @param modifier The modifier to be applied to the layout.
 * @param size Outer diameter of the ring.
 * @param logoFraction Logo width as a share of [size].
 * @param contentDescription Accessibility label announced with the progress value.
 * @param isProgressAnimated False draws [progress] as given, for a caller that already moves it on a clock (a
 * countdown that must fill linearly); true eases each change with a spring.
 */
@Composable
fun AureoleLogoRing(
    progress: Float?,
    modifier: Modifier = Modifier,
    size: Dp = AureoleDS.dimens.iconHuge,
    logoFraction: Float = LOGO_RING_LOGO_FRACTION,
    contentDescription: String? = null,
    isProgressAnimated: Boolean = true,
) {
    val target = logoRingSweepFraction(progress)
    val animated = animateFloatAsState(
        targetValue = target,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "logoRingProgress"
    )
    val drawsTarget = LocalInspectionMode.current || !isProgressAnimated
    val spin = rememberRingSpin(isLogoRingSpinning(progress, rememberSystemAnimationsEnabled()))
    val trackColor = AureoleDS.colors.outline.copy(alpha = TRACK_ALPHA)
    val progressColor = AureoleDS.colors.onSurfaceHigh
    Box(
        modifier = modifier
            .size(size)
            .semantics {
                progressBarRangeInfo = logoRingRangeInfo(progress)
                contentDescription?.let { this.contentDescription = it }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Read in the draw phase so the spring and the spin redraw without recomposing.
            val shown = if (drawsTarget) target else animated.value
            val stroke = this.size.minDimension * STROKE_FRACTION
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(trackColor, 0f, FULL_SWEEP, false, topLeft, arcSize, style = Stroke(stroke))
            drawArc(
                color = progressColor,
                startAngle = START_ANGLE + (spin?.value ?: 0f),
                sweepAngle = FULL_SWEEP * shown,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        AureoleDS.icons.Logo(modifier = Modifier.size(size * logoFraction), tint = progressColor)
    }
}

/** The turning angle of a spinning ring, or null when it stands still. */
@Composable
private fun rememberRingSpin(isSpinning: Boolean): State<Float>? {
    if (!isSpinning) return null
    return rememberInfiniteTransition(label = "logoRingSpin").animateFloat(
        initialValue = 0f,
        targetValue = FULL_SWEEP,
        animationSpec = infiniteRepeatable(tween(SPIN_MS, easing = LinearEasing), RepeatMode.Restart),
        label = "logoRingAngle"
    )
}

@AureolePreview
@Composable
private fun AureoleLogoRingPreview() {
    AureoleLauncherTheme {
        Row {
            AureoleLogoRing(progress = 0f)
            AureoleLogoRing(progress = 0.5f)
            AureoleLogoRing(progress = 1f)
            AureoleLogoRing(progress = null)
        }
    }
}
