package dev.mnascimentos.aureole.feature.home.components.clock

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab

private val TabHeight = 44.dp
private const val INACTIVE_BACKGROUND_ALPHA = 0.5f

/** Segmented tab bar fixed in the sheet footer: the selected tab is light with dark text. */
@Composable
internal fun EditClockTabBar(
    selected: ClockTab,
    onTabSelected: (ClockTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val barDescription = stringResource(R.string.home_clock_tab_bar_description)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = INACTIVE_BACKGROUND_ALPHA))
            .padding(AureoleDS.dimens.xxSmall)
            .selectableGroup()
            .semantics { contentDescription = barDescription },
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        ClockTab.entries.forEach { tab ->
            EditClockTabItem(
                tab = tab,
                isSelected = tab == selected,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun RowScope.EditClockTabItem(tab: ClockTab, isSelected: Boolean, onClick: () -> Unit) {
    val container = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent
    val content = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceLow
    Box(
        modifier = Modifier
            .weight(1f)
            .height(TabHeight)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(container)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AureoleText(
            text = stringResource(tab.labelRes),
            color = content,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

@AureolePreview
@Composable
private fun EditClockTabBarPreview() {
    AureoleLauncherTheme {
        EditClockTabBar(selected = ClockTab.FORMAT, onTabSelected = {})
    }
}
