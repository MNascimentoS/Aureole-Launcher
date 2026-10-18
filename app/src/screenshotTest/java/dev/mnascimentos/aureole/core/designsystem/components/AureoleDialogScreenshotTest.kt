package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleDialogScreenshotTest() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            title = "Sample Dialog Title"
        ) {
            AureoleText(
                text = "This is sample content inside AureoleDialog.",
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}
