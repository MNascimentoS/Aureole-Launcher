package dev.mnascimentos.aureole.composable.optiontile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private object OptionTileMetrics {
    val MinHeight = 56.dp
    const val UNSELECTED_ALPHA = 0.5f
}

/**
 * Selectable tile with a [sample] of the option on top and its [label] below. Stateless: the tap is reported through
 * [onClick]. The selected tile gets an outline in the high-emphasis color and is exposed as a radio button.
 */
@Composable
fun AureoleOptionTile(
    sample: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    val borderColor = if (selected) AureoleDS.colors.onSurfaceHigh else Color.Transparent
    Column(
        modifier = modifier
            .heightIn(min = OptionTileMetrics.MinHeight)
            .clip(shape)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = OptionTileMetrics.UNSELECTED_ALPHA))
            .border(AureoleDS.dimens.borderMax, borderColor, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) { sample() }
        AureoleText(
            text = label,
            color = if (selected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                textAlign = TextAlign.Start,
                maxLines = 2
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleOptionTilePreview() {
    AureoleLauncherTheme {
        AureoleOptionTile(
            sample = { AureoleText(text = "17:29", color = AureoleDS.colors.onSurfaceHigh) },
            label = "24 h",
            selected = true,
            onClick = {},
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        )
    }
}
