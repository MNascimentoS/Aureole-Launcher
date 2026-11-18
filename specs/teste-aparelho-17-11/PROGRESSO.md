# Progresso: teste-aparelho-17-11

| # | Fase | Modelo | Estimativa | Feita | Gates |
|---|---|---|---|---|---|
| 1 | Tudo: onboarding, sheets, ícones, altura, pilha, orientação e rolagem | Opus | 5,5–7,5 h | [x] | — (só compileDebugKotlin + testes do item) |
| 2 | Gates e prints | Sonnet | 1–1,5 h | [ ] | [ ] |

## Notas
- Causa do Salvar no topo (RF-01): não era outro controle de edição, era o próprio `OnboardingEditHintComponent`.
  `editHintEdge` somava a sobreposição de **todos** os itens da grade nas faixas de cima e de baixo. No layout
  padrão a lista de apps e o container ocupam as linhas 12–30 (duas sobreposições na faixa de baixo) e em cima só
  o relógio (uma), então em retrato o Salvar ia sempre para o topo, em qualquer tarefa. Agora só conta o item da
  tarefa pendente (`MainUiState.editHintItems`, pelo `taskItemId`; "Ajustar layout" não tem item, fica embaixo) e
  só nas colunas que a pílula ocupa (centro em retrato, lado do polegar em paisagem: `editHintBandCols`). O
  container 4×18 na coluna 16 fica ao lado da pílula e o Salvar fica embaixo. Testes em `OnboardingEditHintExtTest`.
- RF-02: `onboardingDefaultSize` perdeu `CONTAINER_COLS/ROWS` e o tamanho mínimo em paisagem; relógio, widgets e
  container vêm de `GridDefaults.getDefaultItem` (a mesma fonte do layout padrão) na orientação pedida. Retrato:
  widgets 20×6 na linha 6, container 4×18 na coluna 16 linha 12 (canhoto espelha); paisagem: posições v4.
- RF-03: `OnboardingUiState.showsSheetLogo` + `showsHomeSheetLogo()` (lê o `LocalHomeUiState`, sem CompositionLocal
  novo). Sem logo durante o onboarding: paleta e fonte (sem `onSettingsClick`), relógio, favoritos, container e pilha
  de widgets. O Personalizar (vitrine com anel) não mudou.
- RF-04: lápis "Personalizar home" removido do título de Escolher favoritos (`FavoriteAppsCustomizeHomeButton` e
  `FavoriteAppsDialogActions.onCustomizeHome` apagados). Na home a logo segue no fim; nos Ajustes não há logo
  (sem `onSettingsClick`).
- RF-05: `OverlaySheetHeader` com `titleIcon`: ícone + título no início, sem slot vazio antes, logo no fim, título
  longo com reticências. Vale para app (segurar), pasta, botão rápido, menu de item da grade e pilha (as que têm
  ícone). A sheet da pasta usa o nome e o ícone da pasta (`FolderGlyph`) e passou a ter a logo (D-01).
- RF-06: `icons.AddNew` em `AddWidgetButton`, Pasta/Botão rápido do Gerenciar itens, `HomeScreenScrollContent`,
  card de adicionar da pilha, `EditContainerDialogSections` e tile "Widget" do Personalizar. Ficaram o "+" redondo
  das listas de apps, o "add" do catálogo e os ícones de tipo do `ScrollViewChildItemRow` (D-02).
- RF-07: Gerenciar itens com editor no lugar (`ManageItemsPage`/`ManageItemsEditor`, `EditContainerItemEditors.kt`):
  "Botão rápido" adiciona e abre o editor do botão; tocar numa pasta ou num botão da lista (com "›", o ✕ continua à
  parte) abre o editor; a prévia de cima vira a do item (pasta ou botão, com a pílula do nome); voltar do sistema
  e o botão Voltar devolvem à lista. Edição grava no rascunho do container, salvo ao fechar como o resto. Pasta nova
  continua pelo `CreateFolderDialog`.
- RF-08: "Configurações do Aureole" com `icons.Logo` no círculo.
- RF-09: `previewFit` (`model/PreviewFit.kt`): fica (cabe com respiro `medium`) / encolhe ancorada embaixo até 50% /
  some (fade contínuo). Aplicado no `AnchoredPreviewLayout` (layout empilhado) com `placeRelativeWithLayer`; o fade
  por `expandFraction` ficou só no lado a lado. `anchoredPreviewTop` apagado.
- Sheets curtas que mudaram (RF-10): a causa era o `OverlaySheetBody`, que dava `weight(1f, fill = true)` ao conteúdo
  de toda sheet com barra de baixo; em repouso isso esticava a sheet até 60% da tela. Agora `fill = false`: em
  repouso toda sheet com barra de baixo abraça o conteúdo (Atualização disponível/pronta, `AureoleConfirmDialog`,
  `AureoleConfirmOverlay` e quem usa: pasta nova, reset de layout/launcher, cores, fundo, etc.); expandida, o
  `SpaceBetween` ainda põe o espaço livre acima da barra. Botões lado a lado continuam do `AureoleActionRow`.
- Causa do preso em paisagem e o que o grep achou (RF-12): `grep -rn "requestedOrientation\|screenOrientation" app/`
  não achou nada (nenhuma tela trava a orientação). Causa: `MainLifecycleObserver.onConfigurationChanged` comparava a
  orientação nova com `uiState.isLandscape`, que fica atrás de um `loadGridItems` assíncrono em andamento. Ex.: o
  `onResume` volta com a `Configuration` ainda em paisagem e pede a grade de paisagem; a rotação para retrato chega
  antes do load terminar, compara com o estado (ainda retrato) e não recarrega; o load de paisagem termina e a home
  fica em paisagem com o aparelho em retrato. Além disso `updateGridItemsOrientation` decidia paisagem pela medida
  (`maxCols == 30`). Correção: `OrientationSync` (uma fonte só, a `Configuration`, conferida em
  `onConfigurationChanged`, `onStart` e `onResume`, comparando com o último load pedido); a medida só ajusta
  `gridLimits` (`withMeasuredLimits`/`isMeasureChange`), exceto durante a edição da grade, em que os loads ficam
  parados; `withLoadedGrid` grava os limites da orientação. Testes em `OrientationSyncTest` e
  `GridMeasuredLimitsTest`.
- Linhas horizontais que mudaram (RF-13): helper `Modifier.edgeToEdgeRow` + `edgeToEdgeRowPadding` +
  `sheetContentInset()` (`composable/sheets/SheetEdgeToEdgeRow.kt`). Aplicado em: chips de categoria do
  `AureoleIconCatalogGrid` (botão rápido e pasta), bolinhas de cor do `AureolePaletteSwatchRow`, rampa de tons do
  `BackgroundColorSections` e chips de versão do `ReleaseNotesBottomSheet`. Ficaram: `PaletteCategoryChips`
  (Frias/Frescas/…; não rola, é Row com peso), `WidgetPickerAppList` (já tinha `contentPadding` próprio),
  `ReorderableRow` da pilha (prévia, não sheet) e `ContainerLayouts`/`HomeScreenScrollContent` (home). O rótulo da
  categoria na grade do catálogo usa a direção de leitura (no canhoto o `LeftHandMirror` jogava para a direita).
- RF-11: em paisagem a prévia da pilha é um `HorizontalPager` (`EditWidgetGroupLandscapePager`): um widget por página,
  inteiro e centralizado (largura limitada pela altura disponível), card de adicionar como última página, pontinhos
  embaixo, ✕ no canto; setas de "Mover" viram a página para o selecionado e assentar numa página seleciona o widget.
  Retrato igual a antes. Sem screenshot nesta fase.

## Ficou faltando
- Screenshots (RF-06, RF-10, RF-11, RF-13, cabeçalhos do RF-05) e gates completos: fase 2. As referências mudam em
  muitas sheets por causa do RF-10 e do RF-05.
- Verificação no aparelho do RF-12 (o caminho do vídeo em paisagem) e do RF-11 (pager com widgets reais).
- RF-13: o recuo usado é o padrão do corpo da sheet (`xxSmall + small`); linha dentro de um conteúdo com padding
  extra só sangra até esse recuo. Conferir nos prints da fase 2.
