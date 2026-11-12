package dev.mnascimentos.aureole.core.search

import java.text.Normalizer
import java.util.Locale

private val DIACRITICS = Regex("\\p{Mn}+")

/** Lowercases [text] and strips accents, so "Câmera" and "camera" compare equal. */
fun normalizeForSearch(text: String): String {
    val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
    return DIACRITICS.replace(decomposed, "").lowercase(Locale.ROOT)
}

/**
 * Keeps the [items] whose normalized label contains the normalized [query]. Labels that start with the
 * query come first; ties are broken alphabetically by the normalized label. A blank [query] returns
 * [items] untouched. The best match is the first element.
 */
fun <T> rankByQuery(items: List<T>, query: String, label: (T) -> String): List<T> {
    val normalizedQuery = normalizeForSearch(query.trim())
    if (normalizedQuery.isEmpty()) return items
    return items
        .map { item -> item to normalizeForSearch(label(item)) }
        .filter { (_, normalizedLabel) -> normalizedLabel.contains(normalizedQuery) }
        .sortedWith(
            compareBy<Pair<T, String>> { (_, normalizedLabel) -> !normalizedLabel.startsWith(normalizedQuery) }
                .thenBy { (_, normalizedLabel) -> normalizedLabel }
        )
        .map { (item, _) -> item }
}
