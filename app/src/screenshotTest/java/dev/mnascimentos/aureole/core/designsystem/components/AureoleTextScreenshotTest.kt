package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleTextScreenshotTest() {
    AureoleLauncherTheme {
        Column {
            AureoleText(
                text = "Title Medium Text",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium)
            )
            AureoleText(
                text = "Body Medium Text",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}
