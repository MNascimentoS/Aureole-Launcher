package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Configuration parameters for Aureole Popup Header.
 *
 * @param sessionTitle The optional subtitle or session name displayed under the main title.
 * @param iconBitmap Optional bitmap image to be rendered as the header icon badge.
 * @param showAureoleLogo Whether the top-right Aureole logo button should be shown.
 * @param onSettingsClick Callback invoked when the settings icon is clicked.
 * @param onCloseClick Callback invoked when the close icon is clicked.
 * @param customIcon Optional custom `@Composable` slot for rendering a custom icon badge.
 */
class AureolePopupHeaderParams(
    val sessionTitle: String? = null,
    val iconBitmap: ImageBitmap? = null,
    val showAureoleLogo: Boolean = true,
    val onSettingsClick: (() -> Unit)? = null,
    val onCloseClick: (() -> Unit)? = null,
    val customIcon: (@Composable () -> Unit)? = null
)
