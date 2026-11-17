package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal val releaseNotes0938: List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades",
        items = listOf(
            ReleaseNoteItem(
                title = "Salvar ao Fechar, com Desfazer",
                description = "Fundo, paleta, fonte, cor, botão rápido, cartões e vidro, pasta e apps da pasta " +
                    "não têm mais botão de confirmar: o que você mudou é salvo ao fechar a janela, por toque " +
                    "fora, voltar ou arrastando para baixo. Se algo mudou, aparece o Desfazer.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Novo Seletor de Cor do Fundo",
                description = "Escolha o fundo entre os tons do seu tema, um tom de cada paleta (filtradas por " +
                    "frias, frescas, neutras e quentes), tons da cor escolhida com barra de matiz e as cores " +
                    "vivas. Um aviso aparece quando a cor gera um tema claro.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Lista de Apps pela Borda",
                description = "Ao puxar pela borda direita ou esquerda, a lista de apps vem da própria borda " +
                    "e sai por ela. Deslizar para cima, Início e botão rápido continuam abrindo de baixo.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Checklist vira Pílula",
                description = "Ao minimizar o checklist de primeiros passos, o cartão se transforma na pílula, " +
                    "e a pílula volta a ser o cartão ao tocar.",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Melhorias",
        items = listOf(
            ReleaseNoteItem(
                title = "Janelas que Sobem e Esticam",
                description = "As janelas inferiores sobem de baixo ao abrir e descem ao fechar. Quando o " +
                    "conteúdo cabe, elas só expandem até ele e dão um leve efeito elástico ao puxar.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Ajustes em Dois Painéis na Horizontal",
                description = "Na horizontal, os Ajustes mostram o logo à esquerda e as opções à direita. " +
                    "Em pé, o \"Muito obrigado!\" fica preso ao fim da tela. O Avançado ganhou nova ordem, " +
                    "links maiores e o ícone de casa novo no Launcher padrão.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Visual Mais Cuidado",
                description = "Títulos maiores com mais respiro nas janelas, logo no atalho das Configurações " +
                    "do Personalizar, novo ícone de adicionar nos containers, dica de gesto da borda mais " +
                    "clara e Colaboradores e Licenças centralizados.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    )
)
