package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec

/**
 * The soft outline of the item a task just left on the home, while the home peeks (RF-13). It fades in with the
 * peek and out with it, as the bubble opens into the list; nothing is drawn once it has faded.
 */
@Composable
fun Modifier.onboardingPeekOutline(isOutlined: Boolean): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (isOutlined) OnboardingMotionSpec.PEEK_OUTLINE_ALPHA else 0f,
        animationSpec = tween(OnboardingMotionSpec.PEEK_OUTLINE_FADE_MS),
        label = "peekOutline"
    )
    val color = AureoleDS.colors.onSurfaceHigh
    val radius = AureoleDS.dimens.radiusMedium
    val width = AureoleDS.dimens.borderMinimal * 2
    return drawWithContent {
        drawContent()
        if (alpha > 0f) {
            val stroke = width.toPx()
            drawRoundRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = Size(size.width - stroke, size.height - stroke),
                cornerRadius = CornerRadius(radius.toPx()),
                style = Stroke(stroke)
            )
        }
    }
}
