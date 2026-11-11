package dev.mnascimentos.aureole.composable.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Wrapper of the preview that floats over an edit sheet. It draws no text and reserves no height: the 16 dp gap to
 * the sheet comes from the anchored layout that hosts it.
 */
@Composable
fun AureolePreviewFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        content()
    }
}

@AureolePreview
@Composable
private fun AureolePreviewFramePreview() {
    AureoleLauncherTheme {
        AureolePreviewFrame {
            AureoleText(text = "Preview content", color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
