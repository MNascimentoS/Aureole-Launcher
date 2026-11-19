package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSheetTabBar
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab

/** The clock sheet's tabs, on the DS tab bar fixed in the sheet footer. */
@Composable
internal fun EditClockTabBar(
    selected: ClockTab,
    onTabSelected: (ClockTab) -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleSheetTabBar(
        labels = ClockTab.entries.map { stringResource(it.labelRes) },
        selectedIndex = ClockTab.entries.indexOf(selected),
        description = stringResource(R.string.home_clock_tab_bar_description),
        onSelect = { onTabSelected(ClockTab.entries[it]) },
        modifier = modifier
    )
}

@AureolePreview
@Composable
private fun EditClockTabBarPreview() {
    AureoleLauncherTheme {
        EditClockTabBar(selected = ClockTab.FORMAT, onTabSelected = {})
    }
}
