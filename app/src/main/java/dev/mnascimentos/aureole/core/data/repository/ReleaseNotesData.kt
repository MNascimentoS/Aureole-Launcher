package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal fun getSectionsForVersion0813(): List<ReleaseNoteSection> = listOf(
    getNovidadesArquiteturaSection(),
    getMelhoriasExperienciaSection(),
    getQualidadeEstabilidadeSection()
)

private fun getNovidadesArquiteturaSection() = ReleaseNoteSection(
    title = "Novidades & Arquitetura",
    items = listOf(
        ReleaseNoteItem(
            title = "Migração Completa para Room DB",
            description = "Sincronização e persistência de dados de Contêineres, Favoritos e Pastas " +
                "migradas para Room ORM com suporte nativo a Coroutines e integridade relacional.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Padronização do Design System & Tokens",
            description = "Refatoração completa do consumo de dimensões e espaçamentos (AureoleDS.dimens) " +
                "garantindo consistência visual em todas as telas e inclusão de novos tokens.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Modularização e Separação de Modelos",
            description = "Mapeamento e extração de parâmetros e modelos para pacotes dedicados, " +
                "incluindo isolamento dos modelos e estados do Design System.",
            type = ReleaseNoteType.FEATURE
        )
    )
)

private fun getMelhoriasExperienciaSection() = ReleaseNoteSection(
    title = "Melhorias & Experiência",
    items = listOf(
        ReleaseNoteItem(
            title = "Preservação de Modelos no R8/ProGuard",
            description = "Inclusão de regras de preservação de código (@Keep) para modelos de dados, " +
                "evitando perda de componentes em builds de release.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Melhorias de Acessibilidade (A11y)",
            description = "Adicionados alvos de toque mínimos, roles e mesclagem de " +
                "descrição semântica nos componentes interativos.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Refatoração de Componentes e Ciclo de Vida",
            description = "Centralização de Lifecycle Observers, decomposição da UI da Home " +
                "e limpeza geral da camada de dados para uma arquitetura mais limpa.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Atualizações In-App Modernizadas",
            description = "Interface e diálogo de atualização do aplicativo refinados " +
                "para uma transição mais fluida ao verificar novas versões.",
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

private fun getQualidadeEstabilidadeSection() = ReleaseNoteSection(
    title = "Qualidade & Estabilidade",
    items = listOf(
        ReleaseNoteItem(
            title = "Testes BDD e Regressão Visual",
            description = "Padronização declarativa BDD nas suítes de testes unitários e " +
                "melhorias na estabilidade dos testes de regressão visual (Screenshots).",
            type = ReleaseNoteType.FIX
        ),
        ReleaseNoteItem(
            title = "Conformidade Estrita com Linter Detekt",
            description = "Resolução nativa de avisos do linter e modularização de componentes " +
                "extensos sem necessidade de supressões.",
            type = ReleaseNoteType.FIX
        ),
        ReleaseNoteItem(
            title = "Correção de Rotação de Tela em Release",
            description = "Correção do erro de desserialização que removia componentes aninhados " +
                "ao alterar a orientação do dispositivo.",
            type = ReleaseNoteType.FIX
        )
    )
)

internal fun getSectionsForVersion0713(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
        title = "Melhorias de Usabilidade",
        items = listOf(
            ReleaseNoteItem(
                title = "Interatividade com Efeito Haze",
                description = "Controle dinâmico do efeito de desfoque (blur) nas telas de personalização.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções de Bugs",
        items = listOf(
            ReleaseNoteItem(
                title = "Comportamento do Botão Voltar do Sistema",
                description = "Tratamento correto do evento de voltar ao fechar diálogos e bottom sheets.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0613(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Responsividade da Grade",
                description = "Ajuste na distribuição de espaço das células na tela principal.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções",
        items = listOf(
            ReleaseNoteItem(
                title = "Sobreposição de Ícones na Grade",
                description = "Correção que impedia ícones de se sobreporem ao alterar o tamanho da fonte.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0513(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades & Recursos",
        items = listOf(
            ReleaseNoteItem(
                title = "Suporte a Múltiplas Páginas no Painel Lateral",
                description = "Adicione mais de um painel lateral e navegue entre eles deslizando.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Desempenho da Gaveta de Apps",
                description = "Carregamento e rolagem da lista de aplicativos otimizados.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções",
        items = listOf(
            ReleaseNoteItem(
                title = "Fechamento Involuntário de Pastas",
                description = "Correção de bug onde a pasta fechava ao tentar arrastar um aplicativo.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0313(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades & Recursos",
        items = listOf(
            ReleaseNoteItem(
                title = "Menu de Personalização Rápida",
                description = "Pressione e segure na tela inicial para acessar opções rápidas de personalização.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Animações de Transição",
                description = "Transições mais suaves ao abrir e fechar aplicativos e pastas.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções",
        items = listOf(
            ReleaseNoteItem(
                title = "Atualização do Relógio",
                description = "Correção de travamento ocasional do ponteiro dos minutos do relógio.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)
