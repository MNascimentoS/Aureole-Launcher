package dev.mnascimentos.aureole.composable.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val INACTIVE_BACKGROUND_ALPHA = 0.5f

/**
 * Segmented control: a rounded bar with one equal-width segment per option; the selected one is light with dark
 * text. Stateless: it only reports the tapped index through [onSelect]. The caller adds the accessibility
 * description of the bar through [modifier].
 *
 * @param options Segment labels, in order.
 * @param selectedIndex Index of the selected segment, or -1 when none matches.
 * @param onSelect Called with the index of the tapped segment.
 * @param modifier Modifier applied to the bar.
 * @param itemHeight Height of each segment.
 */
@Composable
fun AureoleSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = AureoleDS.dimens.minTouchTarget
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = INACTIVE_BACKGROUND_ALPHA))
            .padding(AureoleDS.dimens.xxSmall)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        options.forEachIndexed { index, label ->
            SegmentItem(
                label = label,
                isSelected = index == selectedIndex,
                height = itemHeight,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
private fun RowScope.SegmentItem(label: String, isSelected: Boolean, height: Dp, onClick: () -> Unit) {
    val container = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent
    val content = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceLow
    Box(
        modifier = Modifier
            .weight(1f)
            .height(height)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(container)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = label,
            color = content,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleSegmentedControlPreview() {
    AureoleLauncherTheme {
        AureoleSegmentedControl(
            options = listOf("None", "Light", "Medium", "Max"),
            selectedIndex = 2,
            onSelect = {}
        )
    }
}
