@file:Suppress("TooManyFunctions") // Static release notes repository containing version note definitions

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
            getVersion0733(),
            getVersion0728(),
            getVersion0713(),
            getVersion0613(),
            getVersion0513(),
            getVersion0313(),
            getVersion0213(),
            getVersion0113()
        )
    }

    private fun getVersion0733(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.7.33",
            releaseDate = "Março 2025",
            isCurrent = true,
            sections = listOf(
                createSection0733Features(),
                createSection0733Improvements(),
                createSection0733Fixes()
            )
        )
    }

    private fun getVersion0728(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.7.28",
            releaseDate = "Março 2025",
            isCurrent = false,
            sections = listOf(
                createSection0728Features(),
                createSection0728Improvements(),
                createSection0728Fixes()
            )
        )
    }

    private fun getVersion0713(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.7.13",
            releaseDate = "Março 2025",
            isCurrent = false,
            sections = listOf(
                createSection0713Features(),
                createSection0713Improvements(),
                createSection0713Fixes()
            )
        )
    }

    private fun getVersion0613(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.6.13",
            releaseDate = "Março 2025",
            isCurrent = false,
            sections = listOf(
                createSection0613Features(),
                createSection0613Improvements(),
                createSection0613Fixes()
            )
        )
    }

    private fun getVersion0513(): ReleaseNoteVersion {
        return ReleaseNoteVersion(
            versionName = "0.5.13",
            releaseDate = "Março 2025",
            isCurrent = false,
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

private fun createSection0733Features() = ReleaseNoteSection(
    title = "Novidades & Design System",
    items = listOf(
        ReleaseNoteItem(
            title = "Padronização por Tokens no Design System",
            description = "Refatoração completa do consumo de dimensões e espaçamentos (AureoleDS.dimens), " +
                "garantindo consistência visual em todas as telas sem valores hardcoded.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Extensão de Tokens do AureoleDimens",
            description = "Inclusão de novos tokens padronizados para tamanhos de botões de pasta, " +
                "cards de preview de widgets e popups de contêineres.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0733Improvements() = ReleaseNoteSection(
    title = "Melhorias de Arquitetura & Qualidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Modularização da UI da Home",
            description = "Decomposição e extração de componentes como ContainerFolderMiniGrid, " +
                "promovendo maior manutenibilidade e reuso de código.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Documentação Atualizada e Completa",
            description = "Documentação geral do projeto e README.md atualizados com guias " +
                "arquiteturais, regras de convenção e instruções de build.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0733Fixes() = ReleaseNoteSection(
    title = "Qualidade de Código & Linter",
    items = listOf(
        ReleaseNoteItem(
            title = "Conformidade Estrita com Linter Detekt",
            description = "Resolução nativa de avisos do linter sem alterar configurações nem " +
                "adicionar supressões, mantendo o código limpo e seguro.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0728Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Migração Completa para Room DB",
            description = "Sincronização e persistência de dados de Contêineres, Favoritos e Pastas " +
                "migradas para Room ORM com suporte nativo a Coroutines e integridade relacional.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Preservação de Modelos no R8/ProGuard",
            description = "Inclusão de regras de preservação de código (@Keep) para modelos de dados, " +
                "evitando perda de componentes e falhas de desserialização em builds de release.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0728Improvements() = ReleaseNoteSection(
    title = "Melhorias de Interface & Experiência",
    items = listOf(
        ReleaseNoteItem(
            title = "Atualizações In-App Modernizadas",
            description = "Interface e diálogo de atualização do aplicativo refinados " +
                "para uma transição mais fluida ao verificar novas versões.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Limpeza de Camada de Dados",
            description = "Remoção de Helpers SQLite legados e unificação dos repositórios " +
                "para uma arquitetura mais limpa e performática.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0728Fixes() = ReleaseNoteSection(
    title = "Correções & Estabilidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Redimensionamento e Rotação de Tela em Release",
            description = "Correção do erro de desserialização que removia o ScrollView e " +
                "componentes aninhados ao alterar a orientação do dispositivo.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0713Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Animações Fluídas em Overlays de Edição",
            description = "Suporte a animação de entrada e saída deslizando a partir do fundo da tela " +
                "nas telas de edição de Relógio, Container, Aplicativos e Widgets.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Acesso Rápido às Configurações",
            description = "Atalho para as configurações do aplicativo diretamente pelo ícone " +
                "Aureole no cabeçalho das telas de edição.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0713Improvements() = ReleaseNoteSection(
    title = "Melhorias de Usabilidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Interatividade com Efeito Haze",
            description = "Controle dinâmico do efeito de desfoque (blur) nas telas de personalização.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0713Fixes() = ReleaseNoteSection(
    title = "Correções de Bugs",
    items = listOf(
        ReleaseNoteItem(
            title = "Comportamento do Botão Voltar do Sistema",
            description = "Tratamento correto do evento de voltar ao fechar diálogos e bottom sheets.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0613Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Ajuste do Tamanho do Ícone e Texto dos Apps",
            description = "Agora é possível alterar o tamanho dos ícones e textos na tela inicial " +
                "e na gaveta de aplicativos.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Controle de Exibição dos Rótulos",
            description = "Opção para ocultar completamente os nomes dos aplicativos na tela inicial.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0613Improvements() = ReleaseNoteSection(
    title = "Melhorias",
    items = listOf(
        ReleaseNoteItem(
            title = "Responsividade da Grade",
            description = "Ajuste na distribuição de espaço das células na tela principal.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0613Fixes() = ReleaseNoteSection(
    title = "Correções",
    items = listOf(
        ReleaseNoteItem(
            title = "Sobreposição de Ícones na Grade",
            description = "Correção que impedia ícones de se sobreporem ao alterar o tamanho da fonte.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0513Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Suporte a Múltiplas Páginas no Painel Lateral",
            description = "Adicione mais de um painel lateral e navegue entre eles deslizando.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0513Improvements() = ReleaseNoteSection(
    title = "Melhorias",
    items = listOf(
        ReleaseNoteItem(
            title = "Desempenho da Gaveta de Apps",
            description = "Carregamento e rolagem da lista de aplicativos otimizados.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0513Fixes() = ReleaseNoteSection(
    title = "Correções",
    items = listOf(
        ReleaseNoteItem(
            title = "Fechamento Involuntário de Pastas",
            description = "Correção de bug onde a pasta fechava ao tentar arrastar um aplicativo.",
            type = ReleaseNoteType.FIX
        )
    )
)

private fun createSection0313Features() = ReleaseNoteSection(
    title = "Novidades & Recursos",
    items = listOf(
        ReleaseNoteItem(
            title = "Menu de Personalização Rápida",
            description = "Pressione e segure na tela inicial para acessar opções rápidas de personalização.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun createSection0313Improvements() = ReleaseNoteSection(
    title = "Melhorias",
    items = listOf(
        ReleaseNoteItem(
            title = "Animações de Transição",
            description = "Transições mais suaves ao abrir e fechar aplicativos e pastas.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun createSection0313Fixes() = ReleaseNoteSection(
    title = "Correções",
    items = listOf(
        ReleaseNoteItem(
            title = "Atualização do Relógio",
            description = "Correção de travamento ocasional do ponteiro dos minutos do relógio.",
            type = ReleaseNoteType.FIX
        )
    )
)
