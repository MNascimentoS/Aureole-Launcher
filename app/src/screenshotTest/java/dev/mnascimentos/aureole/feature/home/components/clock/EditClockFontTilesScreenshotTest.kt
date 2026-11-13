package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val NOW_MILLIS = 1_794_000_000_000L

@PreviewTest
@AureolePreview
@Composable
private fun EditClockFontTilesThemeSelectedScreenshotTest() {
    AureoleLauncherTheme {
        EditClockFontTiles(
            selectedKey = "SANS_SERIF",
            nowMillis = NOW_MILLIS,
            onSelect = {},
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditClockFontTilesSerifSelectedScreenshotTest() {
    AureoleLauncherTheme {
        EditClockFontTiles(
            selectedKey = "SERIF",
            nowMillis = NOW_MILLIS,
            onSelect = {},
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
        )
    }
}
