package dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions

import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction

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
