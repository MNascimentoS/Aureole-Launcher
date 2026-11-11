package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab

private val TabHeight = 44.dp

/** Segmented tab bar fixed in the sheet footer: the selected tab is light with dark text. */
@Composable
internal fun EditClockTabBar(
    selected: ClockTab,
    onTabSelected: (ClockTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val barDescription = stringResource(R.string.home_clock_tab_bar_description)
    AureoleSegmentedControl(
        options = ClockTab.entries.map { stringResource(it.labelRes) },
        selectedIndex = ClockTab.entries.indexOf(selected),
        onSelect = { onTabSelected(ClockTab.entries[it]) },
        modifier = modifier.semantics { contentDescription = barDescription },
        itemHeight = TabHeight
    )
}

@AureolePreview
@Composable
private fun EditClockTabBarPreview() {
    AureoleLauncherTheme {
        EditClockTabBar(selected = ClockTab.FORMAT, onTabSelected = {})
    }
}
