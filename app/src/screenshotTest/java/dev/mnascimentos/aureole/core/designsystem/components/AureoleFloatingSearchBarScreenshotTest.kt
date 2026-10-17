package dev.mnascimentos.aureole.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme

@PreviewTest
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AureoleFloatingSearchBarScreenshotTest() {
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
