package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal val releaseNotes0918: List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Personalização & Aparência",
        items = listOf(
            ReleaseNoteItem(
                title = "Novo Editor de Temas e Cores",
                description = "Nova interface para escolher paletas e fontes com " +
                    "pré-visualização ao vivo na Mini Tela Inicial.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Nova Tela de Edição de Relógio",
                description = "Configurar o formato e layout do relógio ficou muito " +
                    "mais fácil com os novos painéis.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Navegação e Busca",
        items = listOf(
            ReleaseNoteItem(
                title = "Busca Integrada ao Teclado",
                description = "A barra de pesquisa de aplicativos agora fica fixada junto ao " +
                    "teclado, colocando o melhor resultado logo em destaque.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Novo Seletor de Aplicativos",
                description = "Adicionar aplicativos aos atalhos e widgets agora está mais " +
                    "fluido com o novo seletor.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Interface & Consistência",
        items = listOf(
            ReleaseNoteItem(
                title = "Novas Janelas Inferiores (Bottom Sheets)",
                description = "Todos os painéis de configuração foram redesenhados para um visual " +
                    "mais moderno, espaçado e consistente em todo o sistema.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Fácil Alcance em Telas Grandes",
                description = "Menu de aplicativos, configurações e edição do relógio e pastas " +
                    "agora focam as ações perto do polegar na metade de baixo da tela, " +
                    "com suporte à mão esquerda.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    )
)

internal val releaseNotes0920: List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Experiência de Uso",
        items = listOf(
            ReleaseNoteItem(
                title = "Salvar sem Cliques Extras",
                description = "Ao personalizar relógio ou editar contêiners, os ajustes são " +
                    "salvos automaticamente ao fechar a janela, sem necessidade de confirmar.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Nova Ação de Desfazer",
                description = "Removeu um atalho sem querer, ou mexeu onde não devia? " +
                    "A nova barra de desfazer salva tudo com 1 clique antes de sumir.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Ajustes Finos",
        items = listOf(
            ReleaseNoteItem(
                title = "Visual de Favoritos e Widgets",
                description = "Refinamentos no editor de widgets da home, que agora " +
                    "mostra exatamente um painel completo. Favoritos revisado.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal val releaseNotes0924: List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades",
        items = listOf(
            ReleaseNoteItem(
                title = "Vários Relógios na Tela Inicial",
                description = "Adicione quantos relógios quiser, cada um com o seu estilo. " +
                    "O seu relógio atual continua igual.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Várias Listas de Favoritos",
                description = "Crie listas de favoritos independentes, cada uma com os seus apps.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Da Home Direto para a Letra",
                description = "Segure na borda do alfabeto da tela inicial para abrir a gaveta " +
                    "de apps já na letra escolhida, com a letra em destaque.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Layout Padrão em Paisagem",
                description = "Na horizontal, a tela inicial agora vem com os mesmos itens do retrato.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Redefinir Launcher Mais Seguro",
                description = "A confirmação agora é igual à de desinstalar: segure o botão " +
                    "para confirmar.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Primeiros Passos Mais Naturais",
                description = "Na apresentação inicial, \"Adicionar widget\" vem logo depois do relógio.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Criar Pasta em Tela Cheia",
                description = "Em \"Gerenciar itens\", \"Criar pasta\" agora abre em tela cheia.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções",
        items = listOf(
            ReleaseNoteItem(
                title = "Relógio Não Fecha Mais o App",
                description = "Corrigido o fechamento do app ao trocar o vidro ou o fundo do relógio.",
                type = ReleaseNoteType.FIX
            ),
            ReleaseNoteItem(
                title = "Alfabeto da Gaveta sem Voltar",
                description = "Deslizar no alfabeto da gaveta de apps não aciona mais o voltar " +
                    "do sistema, e o alfabeto fica no lugar de sempre.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0826(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Interface & Consistência",
        items = listOf(
            ReleaseNoteItem(
                title = "Títulos Mais Diretos no Botão Rápido",
                description = "As telas de configuração e de aparência do Botão Rápido " +
                    "ganharam títulos curtos e fáceis de ler.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Componentes Visuais Unificados",
                description = "Favoritos, criação de pastas, novidades e configurações " +
                    "passaram a usar os mesmos componentes visuais em todo o app.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Qualidade & Estabilidade",
        items = listOf(
            ReleaseNoteItem(
                title = "Mais de 800 Testes Automatizados",
                description = "Cada versão agora é verificada por mais de 800 testes automáticos " +
                    "cobrindo navegação, pastas, widgets, Botão Rápido e configurações.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Cobertura Mínima Garantida",
                description = "Nenhuma versão é publicada se os testes deixarem de cobrir " +
                    "ao menos 85% do código do launcher.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Testes Mais Confiáveis",
                description = "Corrigidos problemas que faziam parte dos testes ser ignorada " +
                    "ou falhar de forma intermitente.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0824(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Internacionalização & Suporte Multilíngue",
        items = listOf(
            ReleaseNoteItem(
                title = "Suporte Completo a Português e Inglês",
                description = "Todos os textos e menus da interface gráfica foram mapeados " +
                    "e organizados com suporte nativo a múltiplos idiomas.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Regras Estritas de Internacionalização",
                description = "Mapeamento rigoroso de recursos de string e atualização " +
                    "nas regras de steering de arquitetura.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Novos Recursos & Personalização",
        items = getRelease0824FeatureItems()
    ),
    ReleaseNoteSection(
        title = "Navegação & Usabilidade",
        items = getRelease0824UsabilityItems()
    ),
    ReleaseNoteSection(
        title = "Estabilidade & Desempenho",
        items = getRelease0824PerformanceItems()
    )
)

private fun getRelease0824FeatureItems(): List<ReleaseNoteItem> = listOf(
    ReleaseNoteItem(
        title = "Botão Rápido de Atalhos",
        description = "Adicione um botão personalizável na sua tela inicial para " +
            "acionar ações rápidas, abrir apps ou atalhos do sistema.",
        type = ReleaseNoteType.FEATURE
    ),
    ReleaseNoteItem(
        title = "Aparência dos Botões",
        description = "Escolha a cor de fundo, contorno e ícones para combinar " +
            "com o seu estilo na tela inicial.",
        type = ReleaseNoteType.FEATURE
    ),
    ReleaseNoteItem(
        title = "Caixas de Diálogo Aprimoradas",
        description = "Avisos e janelas na tela ganharam um visual mais limpo, " +
            "com cabeçalhos claros e ícones organizados.",
        type = ReleaseNoteType.FEATURE
    ),
    ReleaseNoteItem(
        title = "Novo Visual para Pastas Abertas",
        description = "O nome da pasta fica destacado no topo de forma clara, " +
            "com cantos arredondados suavizados e margens de tela confortáveis.",
        type = ReleaseNoteType.FEATURE
    ),
    ReleaseNoteItem(
        title = "Cores Mais Suaves nos Menus",
        description = "Ações nos menus e cartões agora utilizam cores padronizadas " +
            "para uma leitura visual mais agradável.",
        type = ReleaseNoteType.FEATURE
    ),
    ReleaseNoteItem(
        title = "Salvamento de Dados Confiável",
        description = "Suas pastas, favoritos e atalhos agora são salvos " +
            "com maior velocidade e segurança.",
        type = ReleaseNoteType.FEATURE
    )
)

private fun getRelease0824UsabilityItems(): List<ReleaseNoteItem> = listOf(
    ReleaseNoteItem(
        title = "Menus Mais Simples",
        description = "Opções redundantes foram removidas dos menus de aplicativos, " +
            "tornando as ações do dia a dia mais rápidas e diretas.",
        type = ReleaseNoteType.IMPROVEMENT
    ),
    ReleaseNoteItem(
        title = "Ajustes do Relógio Aprimorados",
        description = "Opções de layout e formato de data ficaram mais simples e fáceis de escolher.",
        type = ReleaseNoteType.IMPROVEMENT
    ),
    ReleaseNoteItem(
        title = "Toque Mais Preciso & Acessível",
        description = "Áreas de clique ampliadas para facilitar a navegação em botões e itens.",
        type = ReleaseNoteType.IMPROVEMENT
    ),
    ReleaseNoteItem(
        title = "Aviso de Atualização do Aplicativo",
        description = "Interface simplificada para avisar quando houver uma nova versão disponível.",
        type = ReleaseNoteType.IMPROVEMENT
    )
)

private fun getRelease0824PerformanceItems(): List<ReleaseNoteItem> = listOf(
    ReleaseNoteItem(
        title = "Maior Precisão na Organização da Tela Inicial",
        description = "Implementação de verificações avançadas para garantir que a grade " +
            "de aplicativos e atalhos responda com extrema fluidez e sem falhas em qualquer situação.",
        type = ReleaseNoteType.IMPROVEMENT
    ),
    ReleaseNoteItem(
        title = "Navegação Mais Fluida",
        description = "Melhorias internas para garantir respostas rápidas ao abrir janelas " +
            "e navegar pelas opções do aplicativo.",
        type = ReleaseNoteType.IMPROVEMENT
    ),
    ReleaseNoteItem(
        title = "Maior Estabilidade & Desempenho",
        description = "Otimizações na abertura da tela inicial e na lista de aplicativos " +
            "para um uso mais fluido, evitando travamentos.",
        type = ReleaseNoteType.FIX
    )
)

internal fun getSectionsForVersion0713(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Release 0.7.13",
        items = listOf(
            ReleaseNoteItem(
                title = "Manutenção e Desempenho",
                description = "Correções de estabilidade no carregamento de ícones e na lista de aplicativos.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0613(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Release 0.6.13",
        items = listOf(
            ReleaseNoteItem(
                title = "Organização da Tela",
                description = "Melhorias nos gestos de redimensionamento e reordenamento de painéis.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    )
)

internal fun getSectionsForVersion0513(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Release 0.5.13",
        items = listOf(
            ReleaseNoteItem(
                title = "Suporte a Pastas",
                description = "Criação, renomeação e suporte a exibição de ícones na tela inicial.",
                type = ReleaseNoteType.FEATURE
            )
        )
    )
)

internal fun getSectionsForVersion0313(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Release 0.3.13",
        items = listOf(
            ReleaseNoteItem(
                title = "Widgets na Tela Inicial",
                description = "Suporte a adicionar widgets e organizar grupos de widgets.",
                type = ReleaseNoteType.FEATURE
            )
        )
    )
)
