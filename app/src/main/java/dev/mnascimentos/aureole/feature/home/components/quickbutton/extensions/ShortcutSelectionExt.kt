package dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions

import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
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

/** The action that differs between two shortcut sets, or null when nothing changed. */
fun changedShortcut(before: QuickButtonShortcuts, after: QuickButtonShortcuts): ShortcutAction? = listOf(
    before.click to after.click,
    before.press to after.press,
    before.swipeUp to after.swipeUp,
    before.swipeLeft to after.swipeLeft,
    before.swipeRight to after.swipeRight,
).firstOrNull { (old, new) -> old != new }?.second
