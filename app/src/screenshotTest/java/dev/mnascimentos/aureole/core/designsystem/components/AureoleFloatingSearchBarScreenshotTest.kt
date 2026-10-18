package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleFloatingSearchBarScreenshotTest() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AureoleFloatingSearchBar(
                state = AureoleSearchBarState(
                    query = "",
                    onQueryChange = {},
                    isExpanded = false,
                    onExpandedChange = {}
                )
            )
            AureoleFloatingSearchBar(
                state = AureoleSearchBarState(
                    query = "Search text",
                    onQueryChange = {},
                    isExpanded = true,
                    onExpandedChange = {}
                )
            )
        }
    }
}
