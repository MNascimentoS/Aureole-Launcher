package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal fun getSectionsForVersion0913(): List<ReleaseNoteSection> = listOf(
    welcomeSection0913(),
    customizeSection0913(),
    fixesSection0913()
)

private fun welcomeSection0913(): ReleaseNoteSection = ReleaseNoteSection(
    title = "Boas-vindas Guiadas",
    items = listOf(
        ReleaseNoteItem(
            title = "Primeiros Passos na Sua Própria Tela",
            description = "Quem instala o Aureole agora é recebido com uma apresentação rápida " +
                "que ensina os gestos e ajuda a montar a tela inicial passo a passo.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Cada Passo Monta um Pedaço da Tela",
            description = "Relógio, apps, container e widgets aparecem na tela conforme você " +
                "avança, e cada passo só conta depois que você salva.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Rever Quando Quiser",
            description = "Já usa o app? Veja a apresentação em Configurações › Avançado › Rever boas-vindas.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun customizeSection0913(): ReleaseNoteSection = ReleaseNoteSection(
    title = "Personalizar",
    items = listOf(
        ReleaseNoteItem(
            title = "Adicionar Favoritos e Todos os Apps",
            description = "O menu Personalizar ganhou opções para colocar seus favoritos ou a " +
                "lista com todos os apps direto na tela inicial.",
            type = ReleaseNoteType.FEATURE
        ),
        ReleaseNoteItem(
            title = "Novo Layout Padrão",
            description = "O layout inicial ficou mais organizado, com espaço para widgets e " +
                "uma lista de apps maior.",
            type = ReleaseNoteType.IMPROVEMENT
        ),
        ReleaseNoteItem(
            title = "Carregamento Mais Suave",
            description = "A tela inicial aparece na hora e as listas de apps mostram uma " +
                "prévia enquanto carregam.",
            type = ReleaseNoteType.IMPROVEMENT
        )
    )
)

private fun fixesSection0913(): ReleaseNoteSection = ReleaseNoteSection(
    title = "Correções",
    items = listOf(
        ReleaseNoteItem(
            title = "Widgets Funcionando de Novo",
            description = "A área de widgets vazia volta a mostrar o botão de adicionar, e o " +
                "widget escolhido aparece no lugar certo.",
            type = ReleaseNoteType.FIX
        ),
        ReleaseNoteItem(
            title = "Modo Canhoto nos Favoritos",
            description = "A lista de favoritos da tela inicial agora respeita o modo canhoto.",
            type = ReleaseNoteType.FIX
        )
    )
)
