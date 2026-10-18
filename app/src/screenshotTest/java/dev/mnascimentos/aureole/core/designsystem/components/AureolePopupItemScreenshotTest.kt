package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureolePopupItemScreenshotTest() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(260.dp)
        ) {
            AureolePopupHeader(
                title = "Folder name",
                params = AureolePopupHeaderParams(
                    sessionTitle = "Folder actions",
                    onSettingsClick = {},
                    onCloseClick = {}
                )
            )
            AureolePopupMenuItem(
                title = "Edit shortcuts",
                onClick = {}
            )
            AureolePopupMenuItem(
                title = "Delete folder",
                params = AureolePopupMenuItemParams(isDestructive = true),
                onClick = {}
            )
        }
    }
}
