package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal fun getSectionsForVersion0824(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Internacionalização & Suporte Multilíngue",
        items = listOf(
            ReleaseNoteItem(
                title = "Suporte Completo a Português e Inglês",
                description = "Todos os textos e menus da interface gráfica foram mapeados e organizados com suporte nativo a múltiplos idiomas.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Regras Estritas de Internacionalização",
                description = "Mapeamento rigoroso de recursos de string e atualização nas regras de steering de arquitetura.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Novos Recursos & Personalização",
        items = listOf(
            ReleaseNoteItem(
                title = "Botão Rápido de Atalhos",
                description = "Adicione um botão personalizável na sua tela inicial para acionar ações rápidas, abrir apps ou atalhos do sistema.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Aparência dos Botões",
                description = "Escolha a cor de fundo, contorno e ícones para combinar com o seu estilo na tela inicial.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Caixas de Diálogo Aprimoradas",
                description = "Avisos e janelas na tela ganharam um visual mais limpo, com cabeçalhos claros e ícones organizados.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Novo Visual para Pastas Abertas",
                description = "O nome da pasta fica destacado no topo de forma clara, com cantos arredondados suavizados e margens de tela confortáveis.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Cores Mais Suaves nos Menus",
                description = "Ações nos menus e cartões agora utilizam cores padronizadas para uma leitura visual mais agradável.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Salvamento de Dados Confiável",
                description = "Suas pastas, favoritos e atalhos agora são salvos com maior velocidade e segurança.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Navegação & Usabilidade",
        items = listOf(
            ReleaseNoteItem(
                title = "Menus Mais Simples",
                description = "Opções redundantes foram removidas dos menus de aplicativos, tornando as ações do dia a dia mais rápidas e diretas.",
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
    ),
    ReleaseNoteSection(
        title = "Estabilidade & Desempenho",
        items = listOf(
            ReleaseNoteItem(
                title = "Maior Precisão na Organização da Tela Inicial",
                description = "Implementação de verificações avançadas para garantir que a grade de aplicativos e atalhos responda com extrema fluidez e sem falhas em qualquer situação.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Navegação Mais Fluida",
                description = "Melhorias internas para garantir respostas rápidas ao abrir janelas e navegar pelas opções do aplicativo.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Maior Estabilidade & Desempenho",
                description = "Otimizações na abertura da tela inicial e na lista de aplicativos para um uso mais fluido, evitando travamentos.",
                type = ReleaseNoteType.FIX
            )
        )
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
