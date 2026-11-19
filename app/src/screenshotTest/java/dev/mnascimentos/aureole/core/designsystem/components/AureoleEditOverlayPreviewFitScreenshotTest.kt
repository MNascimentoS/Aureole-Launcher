package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val TALL_SHEET_ROWS = 40
private const val PREVIEW_ROWS = 16

/** A short sheet: the preview has room and shows (RF-A1, RF-A4). */
@PreviewTest
@AureolePreview
@Composable
private fun PreviewFitShortSheetScreenshotTest() {
    PreviewFitOverlay(rows = 1)
}

/**
 * A tall sheet: the preview takes only the room left above it, never reaching the top padding (RF-A2). Past that
 * (the sheet dragged up) the fade to alpha 0 is covered by `PreviewFitTest`.
 */
@PreviewTest
@AureolePreview
@Composable
private fun PreviewFitTallSheetScreenshotTest() {
    PreviewFitOverlay(rows = TALL_SHEET_ROWS)
}

@Composable
private fun PreviewFitOverlay(rows: Int) {
    AureoleLauncherTheme {
        AureoleEditOverlay(
            onDismissRequest = {},
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(title = "Clock style", animated = false),
                previewContent = {
                    // A tall preview: it fits above a short sheet and has no room above a tall one.
                    Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                        repeat(PREVIEW_ROWS) { Text(text = "Preview line ${it + 1}", color = Color.White) }
                    }
                },
                sheetContent = {
                    Column { repeat(rows) { Text(text = "Option ${it + 1}", color = Color.White) } }
                }
            )
        )
    }
}
