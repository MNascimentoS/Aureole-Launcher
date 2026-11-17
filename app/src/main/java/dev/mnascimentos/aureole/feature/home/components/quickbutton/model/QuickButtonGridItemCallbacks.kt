package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction

data class QuickButtonGridItemCallbacks(
    val onAction: (ShortcutAction) -> Unit,
    val onLongPress: () -> Unit
)

/**
 * How a quick button cell is drawn besides its model: a null [shape] is the medium radius; [appInfo] is the app a
 * click opens (its icon replaces the button icon); [outlineIcon] draws the readable copy behind a low contrast icon;
 * a null [iconSize] is the home size (`iconXLarge`), the sheet preview gives a smaller one, as the folder tile.
 */
data class QuickButtonGridItemStyle(
    val shape: Shape? = null,
    val appInfo: AppInfo? = null,
    val outlineIcon: Boolean = false,
    val iconSize: Dp? = null
)

/** Callbacks of a cell that does nothing, for previews of the button. */
val NoQuickButtonCallbacks = QuickButtonGridItemCallbacks(onAction = {}, onLongPress = {})
