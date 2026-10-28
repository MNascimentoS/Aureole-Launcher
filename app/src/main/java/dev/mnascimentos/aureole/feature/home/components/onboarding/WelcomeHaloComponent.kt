package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.WelcomeMotion
import kotlin.random.Random

private const val RING_STROKE_FRACTION = 0.025f
private const val LOGO_FRACTION = 0.5f
private const val GLOW_RADIUS_FACTOR = 1.6f
private const val GLOW_ALPHA = 0.55f
private const val START_ANGLE = -90f
private const val FULL_SWEEP = 360f
private const val STAR_COUNT = 36
private const val STAR_SEED = 7
private const val STAR_MAX_RADIUS = 1.6f
private const val STAR_MIN_ALPHA = 0.15f
private const val STAR_ALPHA_RANGE = 0.5f

/**
 * Ring, radial glow and logo of the welcome. [motion] is read in the draw and layer phases,
 * so the ignition animates without recomposing.
 */
@Composable
fun WelcomeHaloComponent(
    motion: WelcomeMotion,
    modifier: Modifier = Modifier,
    size: Dp = AureoleDS.dimens.sizeExtraLarge
) {
    val ringColor = AureoleDS.colors.onSurfaceHigh
    val glowColor = AureoleDS.colors.outline
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val glowRadius = this.size.minDimension / 2 * GLOW_RADIUS_FACTOR * motion.glow.value
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = GLOW_ALPHA), glowColor.copy(alpha = 0f)),
                    center = center,
                    radius = glowRadius.coerceAtLeast(1f)
                ),
                radius = glowRadius
            )
            val stroke = this.size.minDimension * RING_STROKE_FRACTION
            drawArc(
                color = ringColor,
                startAngle = START_ANGLE,
                sweepAngle = FULL_SWEEP * motion.ring.value,
                useCenter = false,
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(this.size.width - stroke, this.size.height - stroke),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        AureoleDS.icons.Logo(
            modifier = Modifier
                .size(size * LOGO_FRACTION)
                .graphicsLayer {
                    alpha = motion.logo.value
                    scaleX = motion.logoScale
                    scaleY = motion.logoScale
                },
            tint = ringColor
        )
    }
}

/** Twinkling star field behind the welcome; [twinkle] is read in the draw phase. */
@Composable
fun WelcomeStarsComponent(
    twinkle: () -> Float,
    modifier: Modifier = Modifier
) {
    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) { Triple(random.nextFloat(), random.nextFloat(), random.nextFloat()) }
    }
    val starColor = AureoleDS.colors.onSurfaceHigh
    Canvas(modifier = modifier) {
        val phase = twinkle()
        stars.forEach { (x, y, depth) ->
            val shimmer = (phase + depth) % 1f
            drawCircle(
                color = starColor.copy(alpha = STAR_MIN_ALPHA + STAR_ALPHA_RANGE * shimmer),
                radius = STAR_MAX_RADIUS * density * depth,
                center = Offset(x * this.size.width, y * this.size.height)
            )
        }
    }
}

@AureolePreview
@Composable
private fun WelcomeHaloPreview() {
    AureoleLauncherTheme {
        Box {
            WelcomeStarsComponent(twinkle = { 0.5f }, modifier = Modifier.matchParentSize())
            WelcomeHaloComponent(motion = remember { WelcomeMotion(isSettled = true) })
        }
    }
}
