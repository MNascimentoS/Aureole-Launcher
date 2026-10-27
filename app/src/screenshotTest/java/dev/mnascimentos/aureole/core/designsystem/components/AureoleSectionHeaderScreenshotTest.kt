package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleSectionHeaderScreenshotTest() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AureoleSectionHeader(
                title = "A",
                color = AureoleDS.colors.onSurfaceMedium
            )
            AureoleSectionHeader(
                title = "Favorites",
                color = AureoleDS.colors.onSurfaceHigh
            )
            AureoleSectionHeader(
                title = "Recent Apps",
                color = AureoleDS.colors.onSurfaceLow
            )
            AureoleSectionHeader(
                title = "Alerts",
                color = AureoleDS.colors.error
            )
        }
    }
}
