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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val INACTIVE_BACKGROUND_ALPHA = 0.5f

/** One segment of [AureoleIconSegmentedControl]: its [label] and the optional [icon], drawn in the content color. */
class SegmentedOption(val label: String, val icon: (@Composable (Color) -> Unit)? = null)

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
 * @param segmentRole What TalkBack calls each segment: a tab by default; [Role.RadioButton] for a choice of values.
 */
@Composable
fun AureoleSegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = AureoleDS.dimens.minTouchTarget,
    segmentRole: Role = Role.Tab
) {
    AureoleIconSegmentedControl(
        options = options.map { SegmentedOption(it) },
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        modifier = modifier,
        itemHeight = itemHeight,
        segmentRole = segmentRole
    )
}

/** [AureoleSegmentedControl] whose segments may have an icon before the label (see [SegmentedOption]). */
@Composable
fun AureoleIconSegmentedControl(
    options: List<SegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = AureoleDS.dimens.minTouchTarget,
    segmentRole: Role = Role.Tab
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
        options.forEachIndexed { index, option ->
            SegmentItem(
                label = option.label,
                isSelected = index == selectedIndex,
                height = itemHeight,
                icon = option.icon,
                role = segmentRole,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
private fun RowScope.SegmentItem(
    label: String,
    isSelected: Boolean,
    height: Dp,
    icon: (@Composable (Color) -> Unit)?,
    role: Role,
    onClick: () -> Unit
) {
    val container = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent
    val content = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceLow
    Box(
        modifier = Modifier
            .weight(1f)
            .height(height)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(container)
            .selectable(selected = isSelected, role = role, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.CenterHorizontally)
        ) {
            icon?.invoke(content)
            AureoleText(
                text = label,
                modifier = Modifier.weight(1f, fill = false),
                color = content,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            )
        }
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
