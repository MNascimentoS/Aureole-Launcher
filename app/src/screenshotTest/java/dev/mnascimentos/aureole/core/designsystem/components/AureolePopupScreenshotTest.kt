package dev.mnascimentos.aureole.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme

@PreviewTest
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AureolePopupScreenshotTest() {
    AureoleLauncherTheme {
        AureolePopupBox(
            modifier = Modifier.width(260.dp)
        ) {
            AureolePopupHeader(
                title = "Folder Name",
                sessionTitle = "Session Subtitle"
            )
        }
    }
}
