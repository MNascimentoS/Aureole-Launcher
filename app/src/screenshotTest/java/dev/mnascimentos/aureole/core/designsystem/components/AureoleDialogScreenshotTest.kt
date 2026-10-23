package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.model.AureoleDialogConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleDialogDefaultScreenshotTest() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            title = "Sample Dialog Title",
        ) {
            AureoleText(
                text = "This is sample content inside AureoleDialog.",
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AureoleDialogWithIconAndLogoScreenshotTest() {
    AureoleLauncherTheme {
        AureoleDialog(
            onDismissRequest = {},
            config = AureoleDialogConfig(
                title = "Dialog With Icon",
                icon = { m -> AureoleDS.icons.Smile(m) },
                onLogoClick = {},
            ),
        ) {
            AureoleText(
                text = "Dialog content with leading icon and logo callback.",
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium),
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AureoleDialogHeaderSlotScreenshotTest() {
    AureoleLauncherTheme {
        AureoleDialogHeader(
            title = "Header Composition Title",
            icon = { m -> AureoleDS.icons.Smile(m) },
            onLogoClick = {},
        )
    }
}
