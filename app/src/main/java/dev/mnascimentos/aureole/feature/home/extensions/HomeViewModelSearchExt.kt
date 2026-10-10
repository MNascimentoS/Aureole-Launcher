package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import java.text.Normalizer

fun HomeViewModel.onSearchQueryChanged(query: String) {
    updateUiState { it.copy(searchQuery = query) }
    applySearchFilter(query)
}

private fun String.removeAccents(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
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
        val normalizedQuery = query.removeAccents().lowercase().trim()
        val queryTerms = normalizedQuery.split("\\s+".toRegex())

        val synonyms = mapOf(
            "browser" to listOf("chrome", "edge", "firefox", "brave", "opera", "navegador", "internet"),
            "navegador" to listOf("chrome", "edge", "firefox", "brave", "opera", "browser", "internet"),
            "email" to listOf("gmail", "outlook", "mail", "e-mail"),
            "e-mail" to listOf("gmail", "outlook", "mail", "email"),
            "maps" to listOf("maps", "mapa", "waze", "navegacao", "gps"),
            "mapa" to listOf("maps", "mapa", "waze", "navegacao", "gps"),
            "music" to listOf("spotify", "deezer", "music", "musica", "audio"),
            "musica" to listOf("spotify", "deezer", "music", "musica", "audio"),
            "video" to listOf("youtube", "netflix", "prime", "video", "player"),
            "social" to listOf("facebook", "instagram", "twitter", "x", "tiktok"),
            "chat" to listOf("whatsapp", "telegram", "messenger", "mensagem", "sms", "zap"),
            "mensagem" to listOf("whatsapp", "telegram", "messenger", "chat", "sms", "zap"),
            "foto" to listOf("gallery", "galeria", "photos", "fotos", "camera"),
            "galeria" to listOf("gallery", "galeria", "photos", "fotos"),
            "banco" to listOf(
                "bank", "nubank", "itau", "bradesco", "caixa", "inter",
                "pagbank", "picpay", "c6", "mercado pago"
            ),
            "bank" to listOf(
                "banco", "nubank", "itau", "bradesco", "caixa", "inter",
                "pagbank", "picpay", "c6", "mercado pago"
            )
        )

        val expandedTerms = queryTerms.flatMap { term ->
            synonyms[term]?.plus(term) ?: listOf(term)
        }

        val filtered = allApps.filter { app ->
            val normalizedLabel = app.label.removeAccents().lowercase()
            val normalizedPackage = app.packageName.lowercase()

            val matchesLabel = expandedTerms.any { term -> normalizedLabel.contains(term) }
            val matchesPackage = expandedTerms.any { term -> normalizedPackage.contains(term) }

            matchesLabel || matchesPackage
        }

        val sortedFiltered = filtered.sortedBy { app ->
            val normalizedLabel = app.label.removeAccents().lowercase()
            when {
                normalizedLabel == normalizedQuery -> 0
                normalizedLabel.startsWith(normalizedQuery) -> 1
                else -> 2
            }
        }

        updateUiState {
            it.copy(
                filteredApps = sortedFiltered,
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
    val firstOccurrenceMap = mutableMapOf<Char, Int>()
    apps.forEachIndexed { index, app ->
        val letter = if (app.firstLetter.uppercaseChar() in 'A'..'Z') {
            app.firstLetter.uppercaseChar()
        } else {
            '#'
        }
        firstOccurrenceMap.putIfAbsent(letter, index)
    }

    val presentAlphabet = (listOf('#') + ('A'..'Z').toList()).filter { char ->
        firstOccurrenceMap.containsKey(char)
    }

    val indexMap = presentAlphabet.associateWith { char ->
        firstOccurrenceMap[char] ?: 0
    }

    return Pair(presentAlphabet, indexMap)
}
