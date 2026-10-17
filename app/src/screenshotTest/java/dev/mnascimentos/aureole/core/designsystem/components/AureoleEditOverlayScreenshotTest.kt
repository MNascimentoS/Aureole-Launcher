package dev.mnascimentos.aureole.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme

@PreviewTest
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AureoleEditOverlayScreenshotTest() {
    AureoleLauncherTheme {
        AureoleEditOverlay(
            onDismissRequest = {},
            title = "Edit Section",
            previewContent = {
                Text(
                    text = "Preview Slot",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) {
            Text(
                text = "Sheet Content Slot",
                color = Color.White
            )
        }
    }
}
