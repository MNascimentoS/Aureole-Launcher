package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.CloseOutcome
import dev.mnascimentos.aureole.composable.sheets.model.closeOutcome
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Edit overlay with no button bar: the preview shows the choice at once and closing the sheet saves it (tapping
 * outside, the system back and swiping down all count). When something changed the caller offers an "Undo" from
 * `onCommit`; when nothing changed the sheet just closes. [bottomExtra] sits below the sheet, for a field that has
 * to stay next to the keyboard.
 */
@Composable
fun AureoleAutoSaveOverlay(
    params: AutoSaveOverlayParams,
    modifier: Modifier = Modifier,
    preview: (@Composable BoxScope.() -> Unit)? = null,
    bottomExtra: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val overlayState = rememberAureoleEditOverlayState()
    val current by rememberUpdatedState(params)
    var committed by remember { mutableStateOf(false) }
    AureoleEditOverlay(
        modifier = modifier,
        state = overlayState,
        // Every way of closing ends here, once the sheet is gone: save first, then tell the caller.
        onDismissRequest = {
            val outcome = closeOutcome(isChanged = current.isChanged(), alreadyCommitted = committed)
            if (outcome == CloseOutcome.COMMIT) {
                committed = true
                current.onCommit()
            }
            current.onDismissRequest()
        },
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = params.title,
                hazeState = params.hazeState,
                isLeftHanded = params.isLeftHanded,
                onSettingsClick = params.onSettingsClick,
                hasExternalBackdrop = params.hasExternalBackdrop,
                host = params.host
            ),
            previewContent = preview,
            bottomBarContent = bottomExtra,
            sheetContent = content
        )
    )
}

@AureolePreview
@Composable
private fun AureoleAutoSaveOverlayPreview() {
    AureoleLauncherTheme {
        AureoleAutoSaveOverlay(
            params = AutoSaveOverlayParams(
                title = "Title",
                isChanged = { false },
                onCommit = {},
                onDismissRequest = {}
            )
        ) {
            AureoleText(text = "Content", color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
