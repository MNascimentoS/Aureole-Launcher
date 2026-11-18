# Spec: pasta-form

Sheet de criar/editar pasta (`composable/dialogs/FolderFormDialog.kt`, `FolderIconGrid.kt`, `FolderFormModels.kt`).
**Protótipo:** `../prototipo-lote-17-11.html`, aba "4 · pasta-form" (direção escolhida e a grade de todos os ícones) · **Decisões:** `DECISOES.md`

## Correções (valem para qualquer direção)
- [ ] **RF-01** **Um scroll só.** Some o `heightIn(max = 2,5 linhas)` + `verticalScroll` da grade e o `verticalScroll`
  do corpo: a grade é uma `LazyVerticalGrid` com todos os ícones (80, 5 colunas), agrupados por categoria, com o nome
  da categoria grudado no topo enquanto rola. `FOLDER_ICON_VISIBLE_ROWS` sai.
- [ ] **RF-02** Chips de categoria fixos acima da grade, com `contentPadding` horizontal; tocar rola a grade até a
  categoria; rolar a grade marca o chip da categoria visível e traz o chip para dentro da tela inteiro (nunca cortado).
  Ao abrir, a grade já está na categoria do ícone salvo.
- [ ] **RF-03** Mesmo respiro horizontal das outras sheets (nada encostado na borda), rótulos no estilo de seção.
- [ ] **RF-04** Sem buraco: a sheet mede o conteúdo até o limite de altura; "Criar" fica embaixo.
- [ ] **RF-04b** A sheet de pasta (nova, editar, todos os ícones) **não mostra a logo** do Aureole no título (`onSettingsClick = null`) (Mateus, 17/11).
- [ ] **RF-05** Ícone marcado **preenchido** (fundo claro, ícone escuro), não só contorno; 48 dp de toque.
- [ ] **RF-06** Editar: "Excluir pasta" vai para o fim, como linha destrutiva sem quadrado (`polimento-ui` RF-11).
- [ ] **RF-07** Paisagem: prévia à esquerda e sheet à direita (RF-16 da `ajustes-redesign`); canhoto espelha.

## Direção escolhida · Estilo livre com o nome primeiro (D-01)
- [ ] **RF-08** **Nome primeiro:** o nome fica num **campo em pílula** embaixo da prévia: fundo leve, borda fina e um botão
  redondo de lápis no fim, para ficar óbvio que se edita ali. Tocar na pílula ou no lápis foca o campo e abre o
  teclado; focado, a borda acende na cor de destaque, aparece o cursor e o lápis vira "×" (limpar). Vazio mostra
  "Dar um nome" (cor baixa). 48 dp de toque; TalkBack: "Nome, <valor>, editar". A sheet de **nova** pasta já abre com o campo
  focado e o teclado aberto; a de **editar** abre sem teclado. Some o campo "Nome da pasta" separado. Vazio ao criar =
  "Nova pasta".
- [ ] **RF-09** Segmentado **Ícone | Letra | Emoji** define o que a pasta mostra:
  - **Ícone:** "Sugeridos para '<nome>'" com 5 ícones de um dicionário local de palavras (PT/EN; "banco"/"bank" →
    carteira, cadeado…) e, na edição, pela categoria dos apps dentro; o primeiro já vem marcado. "Todos os ícones"
    troca o conteúdo da sheet pela grade da RF-01 (no lugar, como Paleta/Fonte). Sem rede; teste do dicionário.
  - **Letra:** a primeira letra do nome, na fonte do tema; acompanha o nome.
  - **Emoji:** grade curta de emojis comuns; tocar em "+" abre o teclado de emoji do sistema.
- [ ] **RF-10** **Cor da pasta**, em duas seções no estilo do Definir plano de fundo: **"Do seu tema"** (Fundo,
  Superfície, Variante, Contorno, Destaque do tema atual) e **"De outras paletas"** (pílulas Frias, Frescas, Neutras,
  Quentes com as cores das outras paletas). Reaproveita os componentes de cor do Fundo. Uma cor "do tema" é salva como
  papel (ex.: `VARIANT`) e acompanha a troca de tema; uma cor de outra paleta é salva fixa (ARGB) (D-03). Sem cor
  escolhida = a cor de pasta do container, como hoje. Ícone/letra ganham contraste automático sobre a cor.
- [ ] **RF-11** "Criar e escolher apps": cria e já abre o seletor de apps da pasta (D-02). Editar: fecha e salva, como hoje.
- [ ] **RF-12** Modelo: `AppFolder` ganha `iconType` (ICON, LETTER, EMOJI), `emoji` e `color` (papel do tema ou ARGB),
  com migração do Room (padrão: ICON e sem cor própria) e teste da migração. A pasta na home (container, popup e
  mini-grade) desenha os três tipos e a cor.
- [ ] **RF-13** Paisagem: prévia à esquerda com o nome editável, sheet à direita com as mesmas seções.

## Componentes compartilhados (usados também pela `botao-rapido`)
- [ ] **RF-14** `AureoleIconCatalog` (core/designsystem/icons): **catálogo único** com os 80 ícones da pasta e os 28 do
  botão rápido (D-04). Categorias: **Ações** (os do botão) + as 8 de hoje. Chave estável por ícone; os nomes antigos do
  enum `QuickButtonIcon` e as chaves antigas da pasta resolvem para a chave nova. Sem duplicados visuais (o mesmo
  drawable aparece uma vez). Teste: toda chave antiga resolve; todo ícone em exatamente uma categoria.
- [ ] **RF-15** `AureoleIconCatalogGrid`: a grade da RF-01/RF-02 como componente (chips + seções grudando + marcado
  preenchido), recebe a chave marcada e devolve a escolhida.
- [ ] **RF-16** `AureoleThemeColorPicker` + tipo `ThemeColorRef` (papel do tema `BACKGROUND|SURFACE|VARIANT|OUTLINE|
  ACCENT` ou ARGB fixo), com "Do seu tema" e "De outras paletas" (reaproveita os componentes de cor do Definir plano
  de fundo). Resolve para `Color` com o tema atual. Teste da resolução e do JSON.

## Fora do escopo
- Mudar a pasta aberta (popup) ou o seletor de apps da pasta.
