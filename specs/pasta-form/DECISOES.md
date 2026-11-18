# Decisões: pasta-form

<!-- Todas respondidas em 17/11. Dúvida nova durante a execução: D-05+, opção conservadora. -->

## D-01 Direção
opções: A · Grade única | B · Nome primeiro | C · Estilo livre | Estilo livre com o nome primeiro
trava: fase 1
resposta: Estilo livre com o nome primeiro; cor com as cores do tema ou de outras paletas (Mateus, 17/11)

## D-02 "Criar" já abre o seletor de apps?
opções: Sim, "Criar e escolher apps" | Não, só "Criar"
trava: fase 2
resposta: Sim, "Criar e escolher apps" (Mateus, 17/11)

## D-03 Cor "do seu tema" acompanha a troca de tema?
opções: Acompanha o tema (salva o papel) | Fica fixa (salva a cor)
trava: fase 1
resposta: Acompanha o tema; cor de outra paleta fica fixa (Mateus, 17/11)

## D-04 Catálogo de ícones
opções: Só os da pasta | Catálogo único com o botão rápido
trava: fase 1
resposta: Catálogo único: os 80 da pasta + os 28 do botão rápido, numa categoria "Ações" (decidido no botao-rapido D-03, Mateus, 17/11)

## D-05 Cabeçalho de categoria grudado na grade de ícones
opções: Overlay fixo sobre a grade | Trocar a grade por LazyColumn de linhas (stickyHeader nativo)
trava: fase 1
resposta: Overlay fixo (conservadora): `LazyVerticalGrid` não tem `stickyHeader`; o nome da categoria visível é desenhado
por cima, opaco, e troca conforme `firstVisibleItemIndex`. (decidido na fase 1, sem consulta)

## D-06 "Criar e escolher apps" também fora da home?
Na home a pasta é criada na hora e o seletor de apps abre em seguida. Em "Editar container" a pasta só existe quando o
container é salvo, e em Ajustes a tela fecha logo depois de criar. Seguindo com a primeira opção até a resposta.
opções: Só "Criar" nesses dois lugares | Abrir o seletor também (exige salvar a pasta antes)
trava: fase 2

## D-07 Sugestões na edição pela categoria dos apps dentro da pasta
`AppInfo` não guarda categoria. Seguindo só pelo nome (e com o ícone atual em primeiro) até a resposta.
opções: Só pelo nome | Ler `ApplicationInfo.category` dos apps (mudança no carregamento de apps)
trava: fase 2

## D-08 Como voltar para "sem cor própria" (cor do container)?
Seguindo com a primeira opção até a resposta.
opções: Tocar de novo na cor marcada desmarca | Uma bolinha "Do container" no começo de "Do seu tema"
trava: fase 2

## D-09 O "+" do Emoji
O Compose não abre o painel de emoji do teclado direto. Seguindo com a primeira opção até a resposta.
opções: Abre um campo com o teclado (a pessoa troca para o painel de emoji; vale o último emoji digitado) | Seletor de emoji completo dentro da sheet (androidx.emoji2 EmojiPickerView)
trava: fase 2
