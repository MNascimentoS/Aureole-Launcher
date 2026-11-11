package dev.mnascimentos.aureole.composable.overlay

import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Hosts a full screen overlay (such as an [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay]
 * based menu) in its own window, so it fills the screen and sits above everything even when the caller is
 * composed inside a scrolling list or a size-limited container. The window is transparent and does not dim:
 * the overlay draws its own scrim. Composition locals flow through the dialog. Back and dismissal are handled
 * by the overlay itself (its BackHandler), and the window also forwards the system back to [onDismissRequest].
 */
@Composable
fun AureoleOverlayWindow(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
            dismissOnClickOutside = false
        )
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
        Box(modifier = modifier.fillMaxSize()) { content() }
    }
}

@AureolePreview
@Composable
private fun AureoleOverlayWindowPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.fillMaxSize())
    }
}
