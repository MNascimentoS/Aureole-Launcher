package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType

internal val releaseNotes0930: List<ReleaseNoteSection> = listOf(
    ReleaseNoteSection(
        title = "Novidades",
        items = listOf(
            ReleaseNoteItem(
                title = "Lista de Apps Única",
                description = "\"Favoritos\" e \"Todos os apps\" viraram um só componente, a Lista de apps, " +
                    "que você pode adicionar quantas vezes quiser. No menu da lista, o botão " +
                    "\"Mostrar todos os apps\" liga e desliga a lista completa; com ele ligado, os favoritos " +
                    "ficam no topo, sem se repetir abaixo. Suas listas atuais foram migradas sozinhas e " +
                    "continuam iguais.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Personalizar com Lista de Apps e Configurações",
                description = "No \"Adicionar à home\", a Lista de apps substitui Favoritos e Todos os apps, " +
                    "e o último quadro agora abre as Configurações do Aureole. Os ícones dos quadros ficaram " +
                    "sem a caixa escura e o logo do cabeçalho saiu só dessa tela.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Segure o Topo para Personalizar",
                description = "O topo da tela inicial ganhou uma faixa vazia maior. Segure nela, ou na " +
                    "barra de status, para abrir o Personalizar, na vertical ou na horizontal, no modo " +
                    "destro ou canhoto.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Personalizar Home em Cada Menu",
                description = "Os menus de edição do item da grade, do grupo de widgets, do botão rápido, " +
                    "da pasta e da Lista de apps ganharam a linha \"Personalizar home\".",
                type = ReleaseNoteType.FEATURE
            )
        )
    ),
    ReleaseNoteSection(
        title = "Aparência",
        items = listOf(
            ReleaseNoteItem(
                title = "O Raio dos Cantos Vale para Tudo",
                description = "O raio escolhido em \"Cartões e vidro\" agora vale para cartões, sheets, " +
                    "diálogos, menus, quadros, chips, campos de busca, pastas, pré-visualizações e botões. " +
                    "Com raio 0, tudo fica reto, até os botões em formato de pílula. Pontos, rádios, " +
                    "amostras de cor e o switch continuam redondos.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Ícones dos Apps Seguem o Raio",
                description = "Atenção: os ícones dos apps passam a seguir o raio do ajuste, em todo lugar " +
                    "onde aparecem (listas, gaveta, containers, pastas, seletores, menus e botões rápidos). " +
                    "Ícones adaptativos ganham o novo recorte; os antigos ficam como estão. Se preferir o " +
                    "visual de antes, ajuste o raio em \"Cartões e vidro\".",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Primeiros Passos",
        items = listOf(
            ReleaseNoteItem(
                title = "Itens Salvos Direto no Onboarding",
                description = "Os itens adicionados na apresentação inicial são salvos na hora, sem passar " +
                    "pelo editor. \"Escolha seus favoritos\" abre direto a escolha de apps.",
                type = ReleaseNoteType.IMPROVEMENT
            ),
            ReleaseNoteItem(
                title = "Novo Passo \"Ajuste o Layout\"",
                description = "A apresentação ganhou um passo final para ajustar o layout da home e o " +
                    "\"Tudo pronto\" ficou mais fácil de ler.",
                type = ReleaseNoteType.FEATURE
            ),
            ReleaseNoteItem(
                title = "Pule as Etapas que Não Quiser",
                description = "As tarefas de itens e o passo de layout podem ser puladas, e contam como " +
                    "resolvidas ao concluir a apresentação.",
                type = ReleaseNoteType.IMPROVEMENT
            )
        )
    ),
    ReleaseNoteSection(
        title = "Correções",
        items = listOf(
            ReleaseNoteItem(
                title = "Cada Container com os Seus Itens",
                description = "Cada container, inclusive dentro de um grupo rolável, tem a própria " +
                    "configuração e os próprios itens: editar ou remover um não mexe nos outros. " +
                    "Um container novo nasce vazio, e remover apaga tudo dele, com \"Desfazer\".",
                type = ReleaseNoteType.FIX
            )
        )
    )
)
