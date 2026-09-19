package dev.mnascimentos.aureole.ui

import android.app.Application
import android.content.ComponentName
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.data.repository.AppRepository
import dev.mnascimentos.aureole.data.repository.WidgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val apps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val alphabet: List<Char> = emptyList(),
    val letterIndexMap: Map<Char, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val topWidgetIds: List<Int> = emptyList(),
    val widgetRowHeight: Dp = 160.dp,
    val showWidgetPicker: Boolean = false,
    val pendingWidgetId: Int = -1,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val widgetRepository = WidgetRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val pendingWidgetId: Int
        get() = _uiState.value.pendingWidgetId

    init {
        loadApps()
        loadWidgetSettings()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val appsList = appRepository.getInstalledApps()
            updateAppsState(appsList)
        }
    }

    fun launchApp(componentName: ComponentName) {
        appRepository.launchApp(componentName)
    }

    private fun loadWidgetSettings() {
        val savedIds = widgetRepository.getSavedWidgetIds()
        val savedHeight = widgetRepository.getSavedWidgetRowHeight()
        _uiState.update {
            it.copy(
                topWidgetIds = savedIds,
                widgetRowHeight = savedHeight.dp,
            )
        }
    }

    fun setWidgetRowHeight(height: Dp) {
        _uiState.update { it.copy(widgetRowHeight = height) }
        widgetRepository.saveWidgetRowHeight(height.value)
    }

    fun setShowWidgetPicker(show: Boolean) {
        _uiState.update { it.copy(showWidgetPicker = show) }
    }

    fun setPendingWidgetId(id: Int) {
        _uiState.update { it.copy(pendingWidgetId = id) }
    }

    fun addWidgetId(widgetId: Int) {
        val currentList = _uiState.value.topWidgetIds
        if (!currentList.contains(widgetId)) {
            val newList = currentList + widgetId
            _uiState.update { it.copy(topWidgetIds = newList) }
            widgetRepository.saveWidgetIds(newList)
        }
    }

    fun removeWidgetId(widgetId: Int) {
        val currentList = _uiState.value.topWidgetIds
        val newList = currentList - widgetId
        _uiState.update { it.copy(topWidgetIds = newList) }
        widgetRepository.saveWidgetIds(newList)
    }

    private fun updateAppsState(apps: List<AppInfo>) {
        val (alphabet, indexMap) = computeAlphabetAndIndexMap(apps)

        _uiState.update {
            it.copy(
                apps = apps,
                filteredApps = apps,
                alphabet = alphabet,
                letterIndexMap = indexMap,
                isLoading = false,
            )
        }
    }

    private fun computeAlphabetAndIndexMap(apps: List<AppInfo>): Pair<List<Char>, Map<Char, Int>> {
        val fullAlphabet = listOf('☆', '#') + ('A'..'Z').toList()

        val firstOccurrenceMap = mutableMapOf<Char, Int>()
        apps.forEachIndexed { index, app ->
            val letter = app.firstLetter.uppercaseChar()
            if (!firstOccurrenceMap.containsKey(letter)) {
                firstOccurrenceMap[letter] = index
            }
        }

        val indexMap = mutableMapOf<Char, Int>()
        fullAlphabet.forEach { char ->
            when (char) {
                '☆', '#' -> {
                    indexMap[char] = 0
                }
                else -> {
                    if (firstOccurrenceMap.containsKey(char)) {
                        indexMap[char] = firstOccurrenceMap[char]!!
                    } else {
                        val nextIndex = apps.indexOfFirst { it.firstLetter.uppercaseChar() > char }
                        indexMap[char] = if (nextIndex != -1) nextIndex else (apps.size - 1).coerceAtLeast(0)
                    }
                }
            }
        }

        return Pair(fullAlphabet, indexMap)
    }
}
