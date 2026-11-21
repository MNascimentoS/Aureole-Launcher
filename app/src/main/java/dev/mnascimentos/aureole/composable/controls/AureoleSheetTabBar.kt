package dev.mnascimentos.aureole.composable.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * The one tab bar of every sheet with pages (clock: Style | Format | Colors; quick button: Icon | Colors): a
 * segmented control fixed at the bottom of the sheet, the selected tab light with dark text (RF-E1, RF-E2).
 * A target selector inside a page ("Icon color | Background color") is not this bar and stays in the content.
 */
@Composable
fun AureoleSheetTabBar(
    labels: List<String>,
    selectedIndex: Int,
    description: String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleSegmentedControl(
        options = labels,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        modifier = modifier.semantics { contentDescription = description },
        itemHeight = AureoleDS.dimens.controlHeight
    )
}

@AureolePreview
@Composable
private fun AureoleSheetTabBarPreview() {
    AureoleLauncherTheme {
        AureoleSheetTabBar(
            labels = listOf("Icon", "Colors"),
            selectedIndex = 0,
            description = "Tabs",
            onSelect = {}
        )
    }
}
