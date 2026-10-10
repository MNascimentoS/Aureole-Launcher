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
            getVersion0513(),
            getVersion0313(),
            getVersion0213(),
            getVersion0113()
        )
    }

    private fun getVersion0513(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.5.13",
            releaseDate = "Março 2025",
            isCurrent = true,
            sections = listOf(
                createSection0513Features(),
                createSection0513Improvements(),
                createSection0513Fixes()
            )
        )
    }

    private fun getVersion0313(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.3.13",
            releaseDate = "Março 2025",
            isCurrent = false,
            sections = listOf(
                createSection0313Features(),
                createSection0313Improvements(),
                createSection0313Fixes()
            )
        )
    }

    private fun getVersion0213(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
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
        )
    }

    private fun getVersion0113(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
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
    }
}

private fun createSection0513Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Novo Diálogo de Gerenciamento de Pastas",
            description = "Formulário renovado para criação e edição de pastas, " +
                "com interface mais limpa e suporte a ícones atualizados.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0513Improvements() = ReleaseNoteSection(
    title = "Melhorias de Interface & Experiência",
    items = listOf(
        ReleaseNoteItem(
            title = "Navegação e Scroll Fluido em Bottom Sheets",
            description = "Aprimoramento no scroll aninhado e remoção de travamentos " +
                "nas telas de seleção de fonte, bordas e paletas.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Ajustes de Layout e Botões de Escolha",
            description = "Melhorias no espaçamento, padding e quebra de texto " +
                "nos botões do painel de edição do relógio.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Preservação da Paleta de Cores do Tema",
            description = "Maior consistência ao aplicar novos temas e gerenciamento " +
                "do papel de parede dinâmico.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Refatoração de Código e Estabilidade",
            description = "Melhorias estruturais na arquitetura do código e refatoração de " +
                "componentes para melhor manutenibilidade.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0513Fixes() = ReleaseNoteSection(
    title = "Correções & Estabilidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Correção no Scroll Aninhado",
            description = "Eliminação do travamento de gestos entre o conteúdo interno " +
                "e o encerramento das Bottom Sheets.",
            type = ReleaseNoteType.FIX
        ),
        ReleaseNoteItem(
            title = "Formatadores do Relógio",
            description = "Restauração da formatação correta de hora e minutos " +
                "no componente de relógio.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0313Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Menu de Personalização Rápida",
            description = "Pressione e segure em qualquer área livre da tela inicial " +
                "para abrir o novo menu suspenso de personalização.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Configurações em Bottom Sheets",
            description = "Os diálogos de edição de contêineres e pastas foram convertidos " +
                "para Bottom Sheets modernas e intuitivas.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Gerenciamento em Massa de Favoritos",
            description = "Adicione e remova múltiplos aplicativos favoritos " +
                "com facilidade no novo seletor de favoritos.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0313Improvements() = ReleaseNoteSection(
    title = "Melhorias de Interface & Design",
    items = listOf(
        ReleaseNoteItem(
            title = "Cantos Arredondados Personalizáveis",
            description = "Novo ajuste dinâmico para os cantos arredondados " +
                "de cartões e painéis nas configurações de aparência.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Ajuste de Escala do Papel de Parede",
            description = "Escolha entre recortar (Crop), preencher (Fill) " +
                "ou ajustar (Fit) a imagem do seu papel de parede.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "AureoleSpacing Tokens",
            description = "Padronização de espaçamentos em todo o launcher " +
                "para maior consistência visual e performance.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Ícones e Temas Aprimorados",
            description = "Ícones monocromáticos atualizados e melhorias " +
                "no contraste dos temas claro e escuro.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0313Fixes() = ReleaseNoteSection(
    title = "Correções & Estabilidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Persistência de Contêineres e Pastas",
            description = "Correção na sincronização e salvamento " +
                "do layout de pastas e contêineres.",
            type = ReleaseNoteType.FIX
        ),
        ReleaseNoteItem(
            title = "Otimização do Efeito Borrado (Haze)",
            description = "Melhoria na renderização do desfoque no relógio " +
                "e painéis para aparelhos compatíveis.",
            type = ReleaseNoteType.FIX
        )
    )
)
