package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.ClockLine
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_LAYOUT_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_LAYOUT_TIME_BAR_TAG
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockLayoutOption

private object StyleCardMetrics {
    const val SMALL_LINE_FRACTION = 0.45f
    const val LARGE_LINE_FRACTION = 0.7f
}

/**
 * Clock layout picker: one card per [ClockLayoutOption], three per row, each with a miniature of its lines (the
 * time is the thick bar; the date and the message are thin, the message lighter). Stateless: the tapped layout is
 * reported through [onSelect]. The rows are not mirrored in the left-handed mode. The bars follow the clock's
 * [alignment] key (left, center, right; RF-D5); the tile label stays at the start.
 */
@Composable
internal fun EditClockStyleCards(
    selectedKey: String,
    onSelect: (ClockLayoutOption) -> Unit,
    modifier: Modifier = Modifier,
    alignment: String = ClockConfig.DEFAULT_ALIGNMENT
) {
    val barsAlignment = ClockHeaderUtils.resolveClockAlignments(alignment).first
    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        ClockLayoutOption.rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                row.forEach { option ->
                    StyleCard(
                        option = option,
                        isSelected = option.key == selectedKey,
                        barsAlignment = barsAlignment,
                        onClick = { onSelect(option) }
                    )
                }
                repeat(CLOCK_LAYOUT_COLUMNS - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun RowScope.StyleCard(
    option: ClockLayoutOption,
    isSelected: Boolean,
    barsAlignment: Alignment.Horizontal,
    onClick: () -> Unit
) {
    AureoleOptionTile(
        sample = { StyleMiniature(lines = option.layout.lines, alignment = barsAlignment) },
        label = stringResource(option.labelRes),
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun StyleMiniature(lines: List<ClockLine>, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.controlHeight),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.CenterVertically),
        horizontalAlignment = alignment
    ) {
        lines.forEach { line ->
            val isTime = line == ClockLine.TIME
            val colors = AureoleDS.colors
            val color = if (line == ClockLine.MESSAGE) colors.onSurfaceMedium else colors.onSurfaceHigh
            Box(
                modifier = Modifier
                    .then(if (isTime) Modifier.testTag(CLOCK_LAYOUT_TIME_BAR_TAG) else Modifier)
                    .fillMaxWidth(
                        if (isTime) StyleCardMetrics.LARGE_LINE_FRACTION else StyleCardMetrics.SMALL_LINE_FRACTION
                    )
                    .height(if (isTime) AureoleDS.dimens.clockLineHeightLarge else AureoleDS.dimens.xxSmall)
                    .clip(RoundedCornerShape(AureoleDS.dimens.minimal))
                    .background(color)
            )
        }
    }
}

@AureolePreview
@Composable
private fun EditClockStyleCardsPreview() {
    AureoleLauncherTheme {
        EditClockStyleCards(
            selectedKey = ClockLayoutOption.DATE_ON_TOP.key,
            onSelect = {},
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
        )
    }
}
