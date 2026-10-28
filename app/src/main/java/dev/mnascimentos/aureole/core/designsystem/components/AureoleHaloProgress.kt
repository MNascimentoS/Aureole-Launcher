package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val STROKE_FRACTION = 0.08f
private const val LOGO_FRACTION = 0.5f
private const val TRACK_ALPHA = 0.35f
private const val START_ANGLE = -90f
private const val FULL_SWEEP = 360f

/**
 * Progress ring around the Aureole logo: the halo itself fills up as [progress] grows.
 *
 * @param progress Completed fraction, clamped to 0..1 and animated with a spring.
 * @param modifier The modifier to be applied to the layout.
 * @param size Outer diameter of the ring.
 * @param contentDescription Accessibility label announced with the progress value.
 */
@Composable
fun AureoleHaloProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = AureoleDS.dimens.iconHuge,
    contentDescription: String? = null,
) {
    val target = progress.coerceIn(0f, 1f)
    val animated = animateFloatAsState(
        targetValue = target,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "haloProgress"
    )
    val isInspection = LocalInspectionMode.current
    val trackColor = AureoleDS.colors.outline.copy(alpha = TRACK_ALPHA)
    val progressColor = AureoleDS.colors.onSurfaceHigh
    Box(
        modifier = modifier
            .size(size)
            .semantics {
                progressBarRangeInfo = ProgressBarRangeInfo(target, 0f..1f)
                contentDescription?.let { this.contentDescription = it }
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Read in the draw phase so the spring redraws without recomposing.
            val shown = if (isInspection) target else animated.value
            val stroke = this.size.minDimension * STROKE_FRACTION
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            drawArc(trackColor, 0f, FULL_SWEEP, false, topLeft, arcSize, style = Stroke(stroke))
            drawArc(
                color = progressColor,
                startAngle = START_ANGLE,
                sweepAngle = FULL_SWEEP * shown,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        AureoleDS.icons.Logo(modifier = Modifier.size(size * LOGO_FRACTION), tint = progressColor)
    }
}

@AureolePreview
@Composable
private fun AureoleHaloProgressPreview() {
    AureoleLauncherTheme {
        Row {
            AureoleHaloProgress(progress = 0f)
            AureoleHaloProgress(progress = 0.5f)
            AureoleHaloProgress(progress = 1f)
        }
    }
}
