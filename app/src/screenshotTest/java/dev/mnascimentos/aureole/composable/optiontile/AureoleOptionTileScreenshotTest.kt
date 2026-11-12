package dev.mnascimentos.aureole.composable.optiontile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Option tile selected and not selected. */
class AureoleOptionTileScreenshotTest {

    @Composable
    private fun Tile(selected: Boolean) {
        AureoleLauncherTheme {
            AureoleOptionTile(
                sample = { AureoleText(text = "17:29", color = AureoleDS.colors.onSurfaceHigh) },
                label = "24 h",
                selected = selected,
                onClick = {},
                modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
            )
        }
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun AureoleOptionTileSelected() {
        Tile(selected = true)
    }

    @PreviewTest
    @AureolePreview
    @Composable
    private fun AureoleOptionTileUnselected() {
        Tile(selected = false)
    }
}
