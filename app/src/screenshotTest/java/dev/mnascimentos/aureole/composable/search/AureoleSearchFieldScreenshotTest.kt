package dev.mnascimentos.aureole.composable.search

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleSearchFieldEmptyScreenshotTest() {
    AureoleLauncherTheme {
        AureoleSearchField(
            query = "",
            onQueryChange = {},
            placeholder = "Search app…",
            onSearch = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AureoleSearchFieldFilledScreenshotTest() {
    AureoleLauncherTheme {
        AureoleSearchField(
            query = "cam",
            onQueryChange = {},
            placeholder = "Search app…",
            onSearch = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
