package dev.mnascimentos.aureole.composable.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * A small caps label ("LIVE PREVIEW") above the preview that sits anchored over an edit sheet.
 * The label is upper-cased here, so callers pass the plain string.
 */
@Composable
fun AureolePreviewFrame(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        AureoleText(
            text = label.uppercase(),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        )
        content()
    }
}

@AureolePreview
@Composable
private fun AureolePreviewFramePreview() {
    AureoleLauncherTheme {
        AureolePreviewFrame(label = "Live preview") {
            AureoleText(text = "Preview", color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
