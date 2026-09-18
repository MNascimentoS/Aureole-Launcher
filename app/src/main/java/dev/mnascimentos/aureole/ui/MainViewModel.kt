package dev.mnascimentos.aureole.ui

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.data.repository.AppRepository
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
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val appsList = repository.getInstalledApps()
            updateAppsState(appsList)
        }
    }

    fun launchApp(componentName: ComponentName) {
        repository.launchApp(componentName)
    }

    private fun updateAppsState(apps: List<AppInfo>) {
        val (alphabet, indexMap) = computeAlphabetAndIndexMap(apps)

        _uiState.update {
            it.copy(
                apps = apps,
                filteredApps = apps,
                alphabet = alphabet,
                letterIndexMap = indexMap,
                isLoading = false
            )
        }
    }

    private fun computeAlphabetAndIndexMap(apps: List<AppInfo>): Pair<List<Char>, Map<Char, Int>> {
        val indexMap = mutableMapOf<Char, Int>()
        apps.forEachIndexed { index, app ->
            val letter = app.firstLetter
            if (!indexMap.containsKey(letter)) {
                indexMap[letter] = index
            }
        }
        val alphabet = indexMap.keys.sorted()
        return Pair(alphabet, indexMap)
    }
}
