package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WidgetPickerViewModel(
    private val widgetRepository: WidgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WidgetSelectorState(isLoading = true))
    val state: StateFlow<WidgetSelectorState> = _state.asStateFlow()

    private var allGroups: List<WidgetAppGroup> = emptyList()

    fun processIntent(intent: WidgetSelectorIntent) {
        when (intent) {
            is WidgetSelectorIntent.LoadWidgets -> {
                loadWidgets()
            }

            is WidgetSelectorIntent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                filterWidgets(intent.query)
            }

            is WidgetSelectorIntent.ToggleAppGroup -> {
                _state.update { currentState ->
                    val expanded = currentState.expandedAppIds.toMutableSet()
                    if (expanded.contains(intent.appId)) {
                        expanded.remove(intent.appId)
                    } else {
                        expanded.add(intent.appId)
                    }
                    currentState.copy(expandedAppIds = expanded)
                }
            }
        }
    }

    private fun loadWidgets() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val groups = widgetRepository.getAvailableWidgets()
            allGroups = groups
            _state.update { it.copy(isLoading = false, widgetGroups = groups) }
        }
    }

    private fun filterWidgets(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(widgetGroups = allGroups) }
        } else {
            val lowerQuery = query.lowercase()
            val filtered = allGroups.mapNotNull { group ->
                if (group.appName.lowercase().contains(lowerQuery)) {
                    group
                } else {
                    val matchingWidgets = group.availableWidgets.filter {
                        it.title.lowercase().contains(lowerQuery)
                    }
                    if (matchingWidgets.isNotEmpty()) {
                        group.copy(availableWidgets = matchingWidgets)
                    } else {
                        null
                    }
                }
            }
            _state.update { it.copy(widgetGroups = filtered) }
        }
    }
}
