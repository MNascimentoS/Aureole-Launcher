package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal fun getSectionsForVersion0741(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Conformidade Arquitetural & Modularização",
        items = listOf(
            ReleaseNoteItem(
                title = "Centralização de Lifecycle Observers",
                description = "Relocação de todos os observadores de ciclo de vida da aplicação " +
                    "para o pacote core/lifecycle em conformidade com as diretrizes do projeto.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Isolamento do Modelo do Design System",
                description = "Separação de modelos de UI e classes de estado em relação aos componentes visuais, " +
                    "garantindo responsabilidade única e desacoplamento.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Divisão de Componentes Extensos & Testes BDD",
                description = "Modularização de diálogos extensos e padronização declarativa BDD " +
                    "nas suítes de testes unitários.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0740(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Arquitetura & Organização de Código",
        items = listOf(
            ReleaseNoteItem(
                title = "Separação Estrita de Modelos de UI",
                description = "Mapeamento e extração de todos os parâmetros, configurações e modelos " +
                    "para pacotes dedicados de modelo (model), garantindo alinhamento de arquitetura.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Modularização de Lifecycle Observers",
                description = "Relocação dos observadores de ciclo de vida para suas respectivas camadas " +
                    "de funcionalidade, eliminando acoplamentos do pacote core.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Estabilidade em Testes de Interface & Screenshots",
                description = "Atualizações nas suítes de testes de regressão visual de telas e " +
                    "componentes para garantir conformidade completa.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0738(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Acessibilidade & Padrão BDD de Testes",
        items = listOf(
            ReleaseNoteItem(
                title = "Conformidade Declarativa BDD em Testes",
                description = "Padronização estrita de todas as funções de teste unitário " +
                    "para o formato BDD declarativo 'when...should...'.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Melhorias de Acessibilidade (A11y)",
                description = "Adicionados alvos de toque mínimos, roles e mesclagem de " +
                    "descrição semântica nos componentes interativos.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Conformidade Estrita com Linter Detekt",
                description = "Modularização de componentes extensos e eliminação nativa " +
                    "de todas as regras de linter sem supressões.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0733(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
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
)

internal fun getSectionsForVersion0728(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
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
    ),
    ReleaseNoteSection(
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
