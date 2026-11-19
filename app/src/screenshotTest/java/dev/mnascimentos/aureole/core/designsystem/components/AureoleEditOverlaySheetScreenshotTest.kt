package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.OverlaySheetDecorations
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun AureoleEditOverlaySheetScreenshotTest() {
    AureoleLauncherTheme {
        OverlaySheetBody(
            title = "Preview Overlay Sheet",
            onSettingsClick = {},
            bottomBarContent = {
                Text(
                    text = "Bottom Bar Content",
                    color = AureoleDS.colors.onSurfaceMedium
                )
            },
            sheetContent = {
                Text(
                    text = "Sheet Body Content",
                    color = AureoleDS.colors.onSurfaceHigh
                )
            }
        )
    }
}

/**
 * RF-05: the logo shares the title line and is centered on it. The red ruler crosses the middle of that line
 * (body padding 4 dp + header top padding 4 dp + half of the 48 dp slot = 32 dp): the text and the logo must sit
 * on it, here and mirrored for the left hand.
 */
@PreviewTest
@AureolePreview
@Composable
private fun OverlaySheetHeaderRulerScreenshotTest() {
    AureoleLauncherTheme {
        Column {
            HeaderWithRuler(isLeftHanded = false)
            HeaderWithRuler(isLeftHanded = true)
        }
    }
}

@Composable
private fun HeaderWithRuler(isLeftHanded: Boolean) {
    val rulerColor = AureoleDS.colors.error
    Box(
        modifier = Modifier.drawWithContent {
            drawContent()
            val y = TITLE_LINE_CENTER.toPx()
            drawLine(rulerColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
        }
    ) {
        OverlaySheetBody(
            title = "Container settings for a long title",
            onSettingsClick = {},
            bottomBarContent = null,
            sheetContent = null,
            decorations = OverlaySheetDecorations(isLeftHanded = isLeftHanded)
        )
    }
}

/** RF-19: a title longer than the line is cut with an ellipsis between the 48 dp slots, never under the logo. */
@PreviewTest
@AureolePreview
@Composable
private fun OverlaySheetHeaderLongTitleScreenshotTest() {
    AureoleLauncherTheme {
        Column {
            listOf(false, true).forEach { isLeftHanded ->
                OverlaySheetBody(
                    title = "Settings of the container on the left side of the home screen",
                    onSettingsClick = {},
                    bottomBarContent = null,
                    sheetContent = null,
                    decorations = OverlaySheetDecorations(isLeftHanded = isLeftHanded)
                )
            }
        }
    }
}

private val TITLE_LINE_CENTER = 32.dp

