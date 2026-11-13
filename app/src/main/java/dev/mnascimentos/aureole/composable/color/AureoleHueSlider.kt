package dev.mnascimentos.aureole.composable.color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.color.model.MAX_HUE
import dev.mnascimentos.aureole.composable.color.model.colorForHue
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val HUE_STOPS = 12

/** A slider over a rainbow bar for the hue of a custom color. Stateless. */
@Composable
fun AureoleHueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbow = Brush.horizontalGradient(
        List(HUE_STOPS + 1) { Color(colorForHue(MAX_HUE * it / HUE_STOPS)) }
    )
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.xxSmall)
                .height(AureoleDS.dimens.medium)
                .clip(CircleShape)
                .background(rainbow)
        )
        Slider(
            value = hue,
            onValueChange = onHueChange,
            valueRange = 0f..MAX_HUE,
            colors = SliderDefaults.colors(
                thumbColor = AureoleDS.colors.onSurfaceHigh,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleHueSliderPreview() {
    AureoleLauncherTheme {
        AureoleHueSlider(hue = 200f, onHueChange = {})
    }
}
