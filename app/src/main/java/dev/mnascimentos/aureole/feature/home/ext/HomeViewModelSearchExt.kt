package dev.mnascimentos.aureole.feature.home.ext

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun HomeViewModel.onSearchQueryChanged(query: String) {
    updateUiState { it.copy(searchQuery = query) }
    applySearchFilter(query)
}

internal fun HomeViewModel.applySearchFilter(query: String) {
    val allApps = uiState.value.apps
    if (query.isBlank()) {
        val (alphabet, indexMap) = computeAlphabetAndIndexMap(allApps)
        updateUiState {
            it.copy(
                filteredApps = allApps,
                alphabet = alphabet,
                letterIndexMap = indexMap
            )
        }
    } else {
        val filtered = allApps.filter { it.label.contains(query, ignoreCase = true) }
        updateUiState {
            it.copy(
                filteredApps = filtered,
                alphabet = emptyList(),
                letterIndexMap = emptyMap()
            )
        }
    }
}

internal fun HomeViewModel.updateAppsState(apps: List<AppInfo>) {
    val (alphabet, indexMap) = computeAlphabetAndIndexMap(apps)

    val favoritePackages = uiState.value.favoriteAppPackages
    val favApps = apps.filter { favoritePackages.contains(it.packageName) }
        .sortedBy { favoritePackages.indexOf(it.packageName) }

    val shouldKeepLoading = apps.isEmpty() && uiState.value.isLoading

    updateUiState {
        it.copy(
            apps = apps,
            filteredApps = apps,
            favoriteApps = favApps,
            alphabet = alphabet,
            letterIndexMap = indexMap,
            isLoading = shouldKeepLoading,
        )
    }
}

private fun computeAlphabetAndIndexMap(apps: List<AppInfo>): Pair<List<Char>, Map<Char, Int>> {
    val fullAlphabet = listOf('☆', '#') + ('A'..'Z').toList()

    val firstOccurrenceMap = mutableMapOf<Char, Int>()
    apps.forEachIndexed { index, app ->
        val letter = app.firstLetter.uppercaseChar()
        firstOccurrenceMap.putIfAbsent(letter, index)
    }

    val indexMap = fullAlphabet.associateWith { char ->
        getAlphabetCharIndex(char, apps, firstOccurrenceMap)
    }

    return Pair(fullAlphabet, indexMap)
}

private fun getAlphabetCharIndex(
    char: Char,
    apps: List<AppInfo>,
    firstOccurrenceMap: Map<Char, Int>
): Int {
    val exactIndex = if (char == '☆' || char == '#') 0 else firstOccurrenceMap[char]
    return exactIndex ?: run {
        val nextIndex = apps.indexOfFirst { it.firstLetter.uppercaseChar() > char }
        if (nextIndex != -1) nextIndex else (apps.size - 1).coerceAtLeast(0)
    }
}
