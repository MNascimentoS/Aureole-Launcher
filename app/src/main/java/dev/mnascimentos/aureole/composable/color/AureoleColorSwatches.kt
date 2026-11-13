package dev.mnascimentos.aureole.composable.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Round color swatches in a wrapping row; the [selected] one gets a ring and a check.
 * Stateless: the caller keeps the chosen color.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AureoleColorSwatches(
    colors: List<Int>,
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        colors.forEach { colorInt ->
            ColorSwatch(colorInt = colorInt, isSelected = selected == colorInt, onSelect = { onSelect(colorInt) })
        }
    }
}

@Composable
private fun ColorSwatch(colorInt: Int, isSelected: Boolean, onSelect: () -> Unit) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .background(Color(colorInt))
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                color = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onSelect, role = Role.RadioButton)
            .semantics { this.selected = isSelected },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            AureoleDS.icons.Check(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}

@AureolePreview
@Composable
private fun AureoleColorSwatchesPreview() {
    AureoleLauncherTheme {
        AureoleColorSwatches(colors = RecommendedColors, selected = RecommendedColors[1], onSelect = {})
    }
}
