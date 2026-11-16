package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

class AureoleOverlayBackdropScreenshotTest {

    @PreviewTest
    @AureolePreview
    @Composable
    private fun AureoleOverlayBackdropWithoutBlur() {
        AureoleLauncherTheme {
            AureoleOverlayBackdrop(hazeState = null, modifier = Modifier.fillMaxSize())
        }
    }
}
