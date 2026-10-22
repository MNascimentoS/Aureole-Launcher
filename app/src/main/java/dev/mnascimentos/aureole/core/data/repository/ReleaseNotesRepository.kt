@file:Suppress("LongMethod")

package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion

object ReleaseNotesRepository {

    fun getLatestVersionNotes(): ReleaseNoteVersion {
        return getReleaseNotes().first { it.isCurrent }
    }

    fun getReleaseNotes(): List<ReleaseNoteVersion> {
        return listOf(
            ReleaseNoteVersion(
                versionName = "0.8.16",
                releaseDate = "Março 2025",
                isCurrent = true,
                sections = getSectionsForVersion0816()
            ),
            ReleaseNoteVersion(
                versionName = "0.8.13",
                releaseDate = "Março 2025",
                isCurrent = false,
                sections = getSectionsForVersion0813()
            ),
            ReleaseNoteVersion(
                versionName = "0.7.13",
                releaseDate = "Março 2025",
                isCurrent = false,
                sections = getSectionsForVersion0713()
            ),
            ReleaseNoteVersion(
                versionName = "0.6.13",
                releaseDate = "Março 2025",
                isCurrent = false,
                sections = getSectionsForVersion0613()
            ),
            ReleaseNoteVersion(
                versionName = "0.5.13",
                releaseDate = "Março 2025",
                isCurrent = false,
                sections = getSectionsForVersion0513()
            ),
            ReleaseNoteVersion(
                versionName = "0.3.13",
                releaseDate = "Março 2025",
                isCurrent = false,
                sections = getSectionsForVersion0313()
            ),
            ReleaseNoteVersion(
                versionName = "0.2.13",
                releaseDate = "Fevereiro 2025",
                isCurrent = false,
                sections = listOf(
                    ReleaseNoteSection(
                        title = "Novidades & Recursos",
                        items = listOf(
                            ReleaseNoteItem(
                                title = "Painéis Laterais Independentes",
                                description = "Múltiplos painéis com suporte a grade expandida " +
                                    "e alinhamentos verticais personalizados.",
                                type = ReleaseNoteType.FEATURE
                            ),
                            ReleaseNoteItem(
                                title = "Efeito Borrado Sincronizado (Haze)",
                                description = "Desfoque em tempo real do papel de parede " +
                                    "no relógio e nos painéis laterais.",
                                type = ReleaseNoteType.FEATURE
                            )
                        )
                    ),
                    ReleaseNoteSection(
                        title = "Melhorias",
                        items = listOf(
                            ReleaseNoteItem(
                                title = "Controles Avançados da Gaveta de Apps",
                                description = "Opções para exibir/ocultar a barra de pesquisa, " +
                                    "botão de configurações e posições personalizadas.",
                                type = ReleaseNoteType.IMPROVEMENT
                            ),
                            ReleaseNoteItem(
                                title = "Barra Alfabética Inteligente",
                                description = "Ocultamento automático da barra alfabética " +
                                    "ao abrir o teclado virtual.",
                                type = ReleaseNoteType.IMPROVEMENT
                            )
                        )
                    )
                )
            ),
            ReleaseNoteVersion(
                versionName = "0.1.13",
                releaseDate = "Janeiro 2025",
                isCurrent = false,
                sections = listOf(
                    ReleaseNoteSection(
                        title = "Lançamento Inicial",
                        items = listOf(
                            ReleaseNoteItem(
                                title = "Aureole Launcher",
                                description = "Tela inicial minimalista para Android com suporte " +
                                    "a Widgets, Pastas, Grade Dinâmica e relógio inteligente.",
                                type = ReleaseNoteType.FEATURE
                            )
                        )
                    )
                )
            )
        )
    }
}
