package dev.mnascimentos.aureole.composable.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleDashedBorderScreenshotTest() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.padding(AureoleDS.dimens.medium).aureoleDashedBorder()) {
            AureoleText(
                text = "Dashed",
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(AureoleDS.dimens.medium)
            )
        }
    }
}
