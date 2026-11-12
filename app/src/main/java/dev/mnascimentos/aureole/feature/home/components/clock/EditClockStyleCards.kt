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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_STYLE_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockStyleLine
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockStyleOption

private object StyleCardMetrics {
    /** Height of the style miniature; a drawing proportion of this card only, not a shared spacing. */
    val MiniatureHeight = 44.dp

    /** Thickness of the big line in the miniature (the time); a drawing proportion of this card only. */
    val LargeLineHeight = 14.dp
    const val SMALL_LINE_FRACTION = 0.45f
    const val LARGE_LINE_FRACTION = 0.7f
}

/**
 * Clock style picker: one card per [ClockStyleOption], three per row, each with a miniature of the clock.
 * Stateless: the tapped style is reported through [onSelect]. The rows are not mirrored in the left-handed mode.
 */
@Composable
internal fun EditClockStyleCards(
    selectedKey: String,
    onSelect: (ClockStyleOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        ClockStyleOption.rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
                row.forEach { option ->
                    StyleCard(
                        option = option,
                        isSelected = option.key == selectedKey,
                        onClick = { onSelect(option) }
                    )
                }
                repeat(CLOCK_STYLE_COLUMNS - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun RowScope.StyleCard(option: ClockStyleOption, isSelected: Boolean, onClick: () -> Unit) {
    AureoleOptionTile(
        sample = { StyleMiniature(lines = option.lines) },
        label = stringResource(option.labelRes),
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun StyleMiniature(lines: List<ClockStyleLine>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(StyleCardMetrics.MiniatureHeight),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start
    ) {
        lines.forEach { line ->
            val isLarge = line == ClockStyleLine.LARGE
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (isLarge) StyleCardMetrics.LARGE_LINE_FRACTION else StyleCardMetrics.SMALL_LINE_FRACTION
                    )
                    .height(if (isLarge) StyleCardMetrics.LargeLineHeight else AureoleDS.dimens.xxSmall)
                    .clip(RoundedCornerShape(AureoleDS.dimens.minimal))
                    .background(AureoleDS.colors.onSurfaceHigh)
            )
        }
    }
}

@AureolePreview
@Composable
private fun EditClockStyleCardsPreview() {
    AureoleLauncherTheme {
        EditClockStyleCards(
            selectedKey = ClockStyleOption.DATE_ON_TOP.key,
            onSelect = {},
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
        )
    }
}
