package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.newItemDashes

/**
 * The highlight of the item a step just added (onboarding-redesenho RF-D1, D-01): a dashed outline in the palette's
 * primary tone, ~6 dp outside the item, that draws itself in ~500 ms and then marches. With the system animations
 * off it is drawn whole and still (RF-D6). Nothing is drawn when [isHighlighted] is false; the chip that goes with
 * it is `OnboardingNewItemChip`.
 */
@Composable
fun Modifier.onboardingNewItemOutline(isHighlighted: Boolean): Modifier {
    if (!isHighlighted) return this
    val isAnimated = rememberOnboardingLoopsEnabled()
    val draw = remember { Animatable(if (isAnimated) 0f else 1f) }
    LaunchedEffect(Unit) {
        if (isAnimated) draw.animateTo(1f, tween(OnboardingMotionSpec.NEW_ITEM_DRAW_MS, easing = LinearEasing))
    }
    val march = if (isAnimated) {
        rememberInfiniteTransition(label = "newItemMarch").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(OnboardingMotionSpec.NEW_ITEM_MARCH_MS, easing = LinearEasing),
                RepeatMode.Restart
            ),
            label = "newItemMarchPhase"
        )
    } else {
        null
    }
    val color = AureoleDS.colors.onSurfaceHigh
    val width = AureoleDS.dimens.borderMinimal * 2
    val dash = AureoleDS.dimens.dashLength
    val radius = AureoleDS.dimens.radiusMedium + OnboardingMotionSpec.NEW_ITEM_MARGIN
    val margin = OnboardingMotionSpec.NEW_ITEM_MARGIN
    return drawWithContent {
        drawContent()
        val dashPx = dash.toPx()
        val dashes = newItemDashes(draw.value, march?.value ?: 0f, dashPx)
        val outset = margin.toPx()
        val outline = Path().apply {
            addRoundRect(
                RoundRect(
                    left = -outset,
                    top = -outset,
                    right = size.width + outset,
                    bottom = size.height + outset,
                    cornerRadius = CornerRadius(radius.toPx())
                )
            )
        }
        val measure = PathMeasure().apply { setPath(outline, forceClosed = true) }
        val shown = if (dashes.visibleFraction >= 1f) {
            outline
        } else {
            Path().also { measure.getSegment(0f, measure.length * dashes.visibleFraction, it, startWithMoveTo = true) }
        }
        drawPath(
            path = shown,
            color = color,
            style = Stroke(
                width = width.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, dashPx), dashes.phasePx)
            )
        )
    }
}
