package dev.mnascimentos.aureole.core.designsystem.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme

@PreviewTest
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AureoleSectionHeaderScreenshotTest() {
    AureoleLauncherTheme {
        AureoleSectionHeaderPreview()
    }
}
