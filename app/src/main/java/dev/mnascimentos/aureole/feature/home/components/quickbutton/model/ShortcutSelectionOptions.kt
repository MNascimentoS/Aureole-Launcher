package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.ShortcutAction

/**
 * Optional inputs of the shortcut selection sheet.
 *
 * @property hazeState Blur source behind the sheet; ignored when haze is off in settings.
 * @property initialSearchQuery Text the search field starts with; previews and tests use it.
 */
class ShortcutSelectionOptions(
    val hazeState: HazeState? = null,
    val initialSearchQuery: String = ""
)

/** What the sheet does in response to the user; grouped so the overlay stays small. */
internal class ShortcutSelectionCallbacks(
    val onQueryChange: (String) -> Unit,
    val onApplyShortcut: (ShortcutAction) -> Unit,
    val onShowAppPicker: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onDismiss: () -> Unit
)
