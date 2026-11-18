# Spec: teste-aparelho-17-11

**Base:** `release/0.9.44` com o lote 17/11 concluído (`specs/lote-17-11-FEITO.md`). **Protótipo:**
`../prototipo-lote-17-11.html`, aba "6 · teste-aparelho" · **Decisões:** `DECISOES.md`

## Onboarding
- [ ] **RF-01** **Salvar embaixo em toda edição do onboarding.** No aparelho o Salvar ainda aparece no topo. Antes de
  mexer, achar qual caminho mostra o Salvar no topo (o `OnboardingEditHintComponent` da `ajustes-onboarding` RF-16 a
  RF-19, ou outro controle de edição da grade usado pelo passo "Ajustar layout", em retrato e em paisagem) e anotar a
  causa no PROGRESSO. Regra: no onboarding, o Salvar fica embaixo (pílula "✓ Salvar"), só sobe para o topo se um item
  ocupar a faixa de baixo. Teste do caminho que falhou.
- [ ] **RF-02** **Widgets e container com o tamanho do layout padrão** (o mesmo do "Não mostrar mais"), como o relógio
  (RF-08 da `ajustes-onboarding`): em retrato, grupo de widgets 20×6 na linha 6 e container 4×18 na coluna 16, linha 12
  (constantes `PORTRAIT_*` do `GridDefaults`; canhoto espelha); em paisagem, as posições do layout v4
  (`LANDSCAPE_*`). Se o lugar estiver ocupado, segue a regra de hoje. `onboardingDefaultSize` perde
  `CONTAINER_COLS/ROWS`; os tamanhos vêm de uma função do `GridDefaults` usada pelo layout padrão e pelo onboarding
  (sem números duplicados). Testes.
- [ ] **RF-03** **Sem logo nas sheets do onboarding que não têm o anel.** Enquanto o onboarding está ativo, sheets
  abertas por ele (seletor de paleta, de fonte, widgets, favoritos, container, etc.) não mostram a logo do Aureole no
  título; só a vitrine do Personalizar (logo com anel) mostra. Depois do onboarding, volta a regra normal.

## Sheets
- [ ] **RF-04** **Escolher favoritos sem o lápis** de "Personalizar home" no título (da home e dos Ajustes). A logo
  continua no fim nas sheets da home; nos Ajustes, sem logo.
- [ ] **RF-05** **Cabeçalho com ícone alinhado no início:** quando a sheet tem ícone no título (sheet do app ao
  segurar, Configurações da pasta), ícone + título ficam no **início** da linha, a logo no fim; título longo corta com
  reticências antes da logo. A sheet da pasta usa o nome da pasta como título (com o ícone dela), como a do app usa o
  nome do app (D-01). Sheets sem ícone continuam com o título centralizado.
- [ ] **RF-06** **Ícone de adicionar do container** (`AureoleDS.icons.AddNew`, `ic_aureole_add_new`) no lugar do "+"
  em caixa (`icons.Add`/`icons.Plus`) nos botões e áreas de **adicionar**: `AddWidgetButton`, Pasta e Botão rápido em
  `EditContainerManageItems`, `HomeScreenScrollContent` (adicionar), `EditWidgetGroupPreview` (adicionar),
  `EditContainerDialogSections` (adicionar) e o tile "Widget" do Personalizar (`CustomizeItemIcons`). Não muda: o "+"
  redondo das listas de apps (adicionar app a favoritos/pasta), o ícone "add" do catálogo e os ícones que representam
  tipo de item em `ScrollViewChildItemRow` (D-02). Screenshots atualizados.
- [ ] **RF-07** **Gerenciar itens edita ali mesmo:** tocar em "Botão rápido" adiciona o botão e **abre o editor dele
  no lugar** (troca de conteúdo na mesma sheet, como Paleta/Fonte; voltar do sistema/arrastar devolve para a lista).
  Cada item da lista (pasta, botão rápido) fica tocável e abre o editor correspondente do mesmo jeito (pasta → sheet
  da `pasta-form`; botão → sheet da `botao-rapido`), com "›" e o "×" de remover separado. Pasta nova continua com
  "Criar e escolher apps". Testes do estado de navegação da sheet.
- [ ] **RF-08** **"Configurações do Aureole"** no fim da lista de apps (`AppsListSectionGrid.settingsListItem`): a
  engrenagem vira a logo do Aureole (`AureoleDS.icons.Logo`) no mesmo círculo.

## Altura das sheets
- [ ] **RF-09** **A prévia só some quando o conteúdo precisa do espaço.** Regra no motor das sheets
  (`OverlaySheetMotion`/`OverlaySheetBoundsFactory`/`OverlaySheetUtils`), função pura testada:
  - **Conteúdo cabe** no espaço abaixo da prévia: rolar não expande a sheet e a prévia **fica**.
  - **Conteúdo não cabe** (precisa de mais espaço para aparecer todo): rolar/puxar expande e a prévia **some, como
    hoje**.
  - **Respiro:** entre o fim da prévia (incluindo a etiqueta "Ao tocar: …") e o topo da sheet, no mínimo
    `AureoleDS.dimens.medium` (~16 dp); se não couber, a prévia encolhe (escala) antes de encostar, nunca cola na sheet.
  Vale para todas as sheets com prévia (atalho e aparência do botão rápido, pasta, favoritos, Aparência). Testes:
  cabe / não cabe / respiro mínimo.
- [ ] **RF-10** **Sheets com pouco conteúdo ficam do tamanho do conteúdo.** Atualização pronta / Reiniciar
  (`InAppUpdateDialog`, "Reiniciar agora"/"Depois") e outras sheets de confirmação curtas: altura = conteúdo + margens
  (sem altura mínima, sem espaço vazio), botões lado a lado embaixo. Conferir também `AureoleConfirmDialog`/
  `AureoleConfirmOverlay` e as demais sheets curtas (listar no PROGRESSO quais mudaram). Screenshots.

## Pilha de widgets em paisagem
- [ ] **RF-11** Na sheet Pilha de widgets em **paisagem**, a prévia (à esquerda) mostra **um widget por vez**, inteiro e
  centralizado, sem o vizinho cortado ao lado (hoje aparece o widget pela metade e o card de adicionar cortado). A
  troca entre widgets é só na horizontal (deslizar ou as setas de "Mover"), com os pontinhos de posição embaixo.
  O card de adicionar entra como mais uma página, com o ícone de adicionar do container (RF-06). Retrato continua como
  hoje. Screenshot em paisagem com 1 e 3 widgets.

## Rolagem horizontal cortada nas sheets
- [ ] **RF-13** Linhas que rolam na horizontal dentro das sheets (chips de categoria do catálogo no Botão rápido e na
  pasta, pílulas Frias/Frescas/…, bolinhas de cor, etc.) hoje são cortadas no padding da sheet (o chip some numa
  borda reta antes do fim da sheet). Regra: a linha ocupa **a largura toda da sheet** e o recuo vira
  **`contentPadding`** horizontal igual ao padding da sheet, então o conteúdo rola até a borda e começa/termina
  alinhado com o resto. Criar um helper único (ex.: `Modifier`/parâmetro `edgeToEdgeRow` ou um
  `AureoleHorizontalRow` com `contentPadding` do sheet) e usar em **todas** as linhas horizontais das sheets
  (`grep` por `LazyRow`/`horizontalScroll` em sheets e anotar no PROGRESSO quais mudaram). Ao abrir, o chip marcado
  rola para dentro da tela inteiro, sem colar na borda. O rótulo da categoria na grade (ex.: "Actions") fica alinhado
  no **início**, como os outros rótulos de seção (no print aparece à direita). Screenshots.

## Bug: app às vezes fica preso em paisagem
- [ ] **RF-12** Às vezes a home fica em paisagem com o aparelho em retrato (e não volta). Não há `requestedOrientation`
  na `release/0.9.44` publicada, mas há **duas fontes** para `isLandscape` que podem divergir:
  (a) `MainLifecycleObserver.onConfigurationChanged` → `landscapeChange` → `loadGridItems`, que só roda quando chega um
  `onConfigurationChanged` (uma rotação feita com a home parada, por exemplo dentro de um vídeo em paisagem, pode não
  chegar ou chegar antes do retorno); (b) `updateGridItemsOrientation(maxCols, maxRows)` decide paisagem por
  `maxCols == LANDSCAPE_MAX_COLS`, a partir da medida do layout, que pode vir de um frame transitório.
  Correção:
  1. Antes de mudar: `grep -rn "requestedOrientation\|screenOrientation" app/` na branch atual (pode ter entrado
     algo nas specs locais, ex.: editor de foto) e anotar no PROGRESSO; nenhuma tela pode travar a orientação (ou, se
     travar, devolve `SCREEN_ORIENTATION_UNSPECIFIED` ao sair em todos os caminhos, inclusive cancelar e voltar).
  2. **Uma fonte só:** `isLandscape` vem da `Configuration` atual (`resources.configuration.orientation`), conferida
     em `onConfigurationChanged` **e** em `onStart`/`onResume` (rotação feita com a home parada); a medida do layout
     só ajusta `gridLimits`, nunca decide paisagem sozinha.
  3. Testes JVM: `landscapeChange` com orientação indefinida; retorno de outro app em paisagem com o aparelho em
     retrato → volta para retrato; medida transitória com colunas de paisagem não muda `isLandscape`.

## Fora do escopo
- Mudar outras telas do onboarding além das citadas.
