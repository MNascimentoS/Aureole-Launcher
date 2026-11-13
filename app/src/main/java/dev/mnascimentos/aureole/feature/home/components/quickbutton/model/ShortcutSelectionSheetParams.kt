package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal data class ShortcutSelectionSheetParams(
    val category: ShortcutCategory,
    val currentAction: ShortcutAction,
    val defaultShortcutOptions: List<ShortcutAction>,
    val uiState: MainUiState,
    val editingType: String? = null,
    val isVisible: Boolean = true,
    val searchQuery: String = "",
    val searchResults: List<ShortcutSearchCandidate> = emptyList()
)
