package dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions

import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun resolveEditingShortcut(type: String?, model: QuickButtonModel): ShortcutAction {
    return when (type) {
        "click" -> model.shortcuts.click
        "press" -> model.shortcuts.press
        "swipeUp" -> model.shortcuts.swipeUp
        "swipeLeft" -> model.shortcuts.swipeLeft
        "swipeRight" -> model.shortcuts.swipeRight
        else -> ShortcutAction.None
    }
}

/** The action that differs between two shortcut sets, or null when nothing changed. */
fun changedShortcut(before: QuickButtonShortcuts, after: QuickButtonShortcuts): ShortcutAction? = listOf(
    before.click to after.click,
    before.press to after.press,
    before.swipeUp to after.swipeUp,
    before.swipeLeft to after.swipeLeft,
    before.swipeRight to after.swipeRight,
).firstOrNull { (old, new) -> old != new }?.second

/** Copy of these shortcuts with the gesture named by [type] set to [action]; unknown types change nothing. */
fun QuickButtonShortcuts.withShortcut(type: String?, action: ShortcutAction): QuickButtonShortcuts = when (type) {
    "click" -> copy(click = action)
    "press" -> copy(press = action)
    "swipeUp" -> copy(swipeUp = action)
    "swipeLeft" -> copy(swipeLeft = action)
    "swipeRight" -> copy(swipeRight = action)
    else -> this
}

fun HomeViewModel.onOpenShortcutSelection(type: String) {
    updateUiState { state ->
        state.copy(
            showQuickButtonSettingsBottomSheet = false,
            showShortcutSelectionBottomSheet = true,
            editingShortcutType = type
        )
    }
}

fun HomeViewModel.onCloseShortcutSelection() {
    updateUiState { state ->
        state.copy(
            showShortcutSelectionBottomSheet = false,
            editingShortcutType = null,
            showQuickButtonSettingsBottomSheet = true
        )
    }
}
