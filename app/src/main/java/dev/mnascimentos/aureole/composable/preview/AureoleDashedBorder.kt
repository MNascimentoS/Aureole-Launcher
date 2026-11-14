package dev.mnascimentos.aureole.composable.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Dashed rounded border drawn over the content, used as the frame of the container preview. Defaults to the
 * design system outline color, [AureoleDS.dimens.borderMinimal] width and [AureoleDS.dimens.radiusMedium] corners.
 */
@Composable
fun Modifier.aureoleDashedBorder(
    color: Color = AureoleDS.colors.outline,
    width: Dp = AureoleDS.dimens.borderMinimal,
    cornerRadius: Dp = AureoleDS.dimens.radiusMedium
): Modifier {
    val dashLength = AureoleDS.dimens.dashLength
    return drawWithContent {
        drawContent()
        val strokePx = width.toPx()
        val dashPx = dashLength.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(strokePx / 2f, strokePx / 2f),
            size = Size(size.width - strokePx, size.height - strokePx),
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = strokePx, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, dashPx)))
        )
    }
}

@AureolePreview
@Composable
private fun AureoleDashedBorderPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.aureoleDashedBorder()) {
            AureoleText(
                text = "Dashed",
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(AureoleDS.dimens.medium)
            )
        }
    }
}
