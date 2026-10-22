package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal fun getSectionsForVersion0816(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades Visuais",
        items = listOf(
            ReleaseNoteItem(
                title = "Novo Visual para Pastas Abertas",
                description = "O nome da pasta fica destacado no topo de forma clara, " +
                    "com cantos arredondados suavizados e margens de tela confortáveis.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Cores Mais Suaves nos Menus",
                description = "Ações nos menus e cartões agora utilizam " +
                    "cores padronizadas para uma leitura visual mais agradável.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Navegação & Usabilidade",
        items = listOf(
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
            )
        )
    ),
    ReleaseNoteSection(
        title = "Facilidade de Uso & Estabilidade",
        items = listOf(
            ReleaseNoteItem(
                title = "Toque Mais Preciso",
                description = "Áreas de clique ampliadas para facilitar a navegação em botões e itens.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Maior Estabilidade",
                description = "Otimizações na abertura da tela inicial e na lista de aplicativos para um uso mais fluido.",
                type = ReleaseNoteType.FIX
            )
        )
    )
)

internal fun getSectionsForVersion0813(): List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades & Experiência",
        items = listOf(
            ReleaseNoteItem(
                title = "Salvamento de Dados Mais Confiável",
                description = "Suas pastas, favoritos e atalhos agora são salvos com maior velocidade e segurança.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Visual Mais Consistente",
                description = "Ajustes de tamanhos e espaçamentos em todas as telas para um visual padronizado.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Toque Mais Fácil e Acessível",
                description = "Botões e cartões interativos ficaram mais fáceis de tocar e usar.",
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
        title = "Estabilidade",
        items = listOf(
            ReleaseNoteItem(
                title = "Melhorias de Desempenho",
                description = "Ajustes gerais para evitar travamentos e deixar o aplicativo mais estável.",
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
