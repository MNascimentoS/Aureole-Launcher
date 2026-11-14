package dev.mnascimentos.aureole.feature.settings.model

import java.util.Locale

private const val LEGAL_BASE_URL = "https://aureole.mnascimentos.dev"
private const val LANGUAGE_PT = "pt"
private const val SEGMENT_PT = "pt-br"
private const val SEGMENT_EN = "en"

enum class LegalPage(val path: String) {
    PRIVACY("privacy"),
    TERMS("terms")
}

fun legalPageUrl(page: LegalPage, locale: Locale): String {
    val segment = if (locale.language == LANGUAGE_PT) SEGMENT_PT else SEGMENT_EN
    return "$LEGAL_BASE_URL/${page.path}/$segment/"
}
