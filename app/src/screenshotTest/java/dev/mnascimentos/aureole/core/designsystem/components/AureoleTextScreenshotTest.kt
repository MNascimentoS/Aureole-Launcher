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
private fun AureoleTextCustomTypographyScreenshotTest() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AureoleText(
                text = "Huge Title (Custom DS)",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.hugeTitle)
            )
            AureoleText(
                text = "Mid Title (Custom DS)",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.midTitle)
            )
            AureoleText(
                text = "Small Title (Custom DS)",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.smallTitle)
            )
            AureoleText(
                text = "Text (Custom DS)",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.text)
            )
            AureoleText(
                text = "Small Text (Custom DS)",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.smallText)
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AureoleTextMaterialTypographyScreenshotTest() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AureoleText(
                text = "Display Large",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.displayLarge)
            )
            AureoleText(
                text = "Display Medium",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.displayMedium)
            )
            AureoleText(
                text = "Display Small",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.displaySmall)
            )
            AureoleText(
                text = "Title Large",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
            )
            AureoleText(
                text = "Title Medium",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium)
            )
            AureoleText(
                text = "Title Small",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall)
            )
            AureoleText(
                text = "Body Large",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
            AureoleText(
                text = "Body Medium",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
            AureoleText(
                text = "Body Small",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
            AureoleText(
                text = "Label Large",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge)
            )
            AureoleText(
                text = "Label Medium",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
            )
            AureoleText(
                text = "Label Small",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
            )
        }
    }
}
