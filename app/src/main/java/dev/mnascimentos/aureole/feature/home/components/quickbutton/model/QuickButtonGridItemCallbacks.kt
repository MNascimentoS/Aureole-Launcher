package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.ShortcutAction

data class QuickButtonGridItemCallbacks(
    val onAction: (ShortcutAction) -> Unit,
    val onLongPress: () -> Unit
)
