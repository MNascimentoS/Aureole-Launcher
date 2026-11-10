package dev.mnascimentos.aureole.composable.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val COLOR_ANIMATION_DURATION_MS = 150

@Composable
fun WidgetPositionIndicatorBar(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = AureoleDS.dimens.xxSmall, bottom = AureoleDS.dimens.xxSmall)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = (index == currentPage)
            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) AureoleDS.dimens.medium else AureoleDS.dimens.xxSmall,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "indicator_width"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) {
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.25f)
                },
                animationSpec = tween(COLOR_ANIMATION_DURATION_MS),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = AureoleDS.dimens.minimal)
                    .height(AureoleDS.dimens.xxSmall)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

@AureolePreview
@Composable
private fun WidgetPositionIndicatorBarPreview() {
    AureoleLauncherTheme {
        WidgetPositionIndicatorBar(
            pageCount = 4,
            currentPage = 1
        )
    }
}
