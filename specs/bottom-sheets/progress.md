# Progresso: bottom sheets para uma mão

Atualizado por `/fase`. Marque `[x]` quando os gates passarem e o veredito visual for **igual** ou **quase** (com ajustes feitos).

## Já feito (antes deste plano, até a release/0.9.18 e commits locais)

- [x] Base da sheet: 60/90%, tic ao cruzar, toque duplo e segurar, prévia ancorada, acessibilidade (ainda com pílula visível)
- [x] Desfazer por 4 s (`UndoController`, `AureoleUndoSnackbar`): pasta, favoritos, botão rápido
- [x] Busca grudada no teclado com melhor resultado perto do campo
- [x] Seletor de apps: arrastar para marcar e faixa A–Z
- [x] Relógio com abas que deslizam
- [x] Paleta e Fonte com mini-home e efeito na hora (grade de chips)
- [x] Fases 1 a 5 do plano rodaram em 10 e 11/11 (gates ok, visual quase/igual). Falta o teste no aparelho.
- [~] O teste no aparelho gerou a fase 6; as pendências das notas abaixo viraram a fase 7 (ver `one-hand-plan.md`). Já resolvidas fora das fases: vidro na Home, desfazer do fundo, Favorito em container/pasta e o crash do menu do app.
- Decisões de 11/11: menu do app com ação no lado do polegar, cabeçalhos de favoritos espelhados, matiz em barra, Cartões e vidro só com "Pronto", ícones de pasta aprovados, sem "Pasta"/"Ícone" no menu do app.

## Fases restantes

| # | Fase | Modelo | Estimativa | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|
| 1 | Ajustes da base: faixa de gesto invisível, `ActionRow`/espelho, base do `SettingsBottomSheet` | Opus | 1,5–2,5 h | [x] | [x] quase | [x] |
| 2 | Lista reordenável com ✕ e "+" (e desfazer em widgets e reordenar) | Opus | 2,5–3,5 h | [x] | [x] quase | [x] |
| 3 | Menus e popup da pasta | Sonnet | 2–3 h | [x] | [x] quase | [x] |
| 4 | Editores restantes | Sonnet | 3–4,5 h | [x] | [x] quase | [x] |
| 5 | Adicionar container e opções de posição | Sonnet | 1,5–2,5 h | [x] | [x] igual | [x] |
| 6 | Ajustes do teste no aparelho: prévia sem rótulo, ícones nas linhas, cards da pilha, mover por botões, Cartões e vidro só com "Pronto", menu do app sem "Pasta"/"Ícone" | Sonnet | 2–3 h | [x] | [x] quase | [x] |
| 6b | Favoritos sem duplicação (sem favoritos = todos os apps) e alça de arrastar clara | Sonnet | 1,5–2,5 h | [x] | [x] quase | [x] |
| 6c | Ícones de pasta do Material Symbols (aprovado; arquivos em `folder-icons/`) | Haiku + Sonnet | 1–2 h | [x] | [x] quase | [x] |
| 7 | Dívidas restantes das fases 1 a 5 (sem o que já foi resolvido) | Sonnet + Opus (autoscroll) | 2,5–4 h | [x] | [x] quase | [x] |
| 8 | Diálogos e avisos | Sonnet | 1,5–2,5 h | [x] | [x] quase | [x] |
| 9 | Acabamento | Haiku | 1–1,5 h | [x] | [x] quase | [x] |

## Notas por fase

(o `/fase` escreve aqui: o que mudou, resultado dos gates, o que ficou faltando)

### Fase 1: ajustes da base (10/11/2026)

**O que mudou**
- `OverlaySheetHandle` virou `OverlaySheetGestureZone`: faixa transparente de 20 dp, sem pílula. Arrastar, toque duplo, segurar 600 ms, vibração e `customActions` continuam iguais. O slot `OverlaySheetDecorations.handle` passou a se chamar `gestureZone`. A altura é uma constante privada do arquivo, não um token do DS.
- String: `overlay_sheet_handle_description` virou `overlay_sheet_gesture_zone_description` ("Hold the top of the sheet" / "Segure o topo da sheet").
- Cabeçalho do `OverlaySheetBody`: título centralizado e engrenagem na mesma linha, com padding `small` nas laterais e respiro `medium` embaixo da barra de ações (ajustes pedidos pelo visual-reviewer).
- `CustomizeBottomSheet` foi do `SettingsBottomSheet` para o `AureoleEditOverlay`, sem prévia. A engrenagem abre os ajustes.
- Decisão sobre o `SettingsBottomSheet`: Paleta e Fonte já usam o `ThemePreviewOverlay` e não mudaram. Cartões e vidro ficam na fase 4, as opções na fase 5, as confirmações e notas na fase 6. Até lá o `SettingsBottomSheet` continua igual nessas telas.
- `AureoleActionRow` e `LeftHandMirror` cobrem a ação principal do lado do polegar e o espelho, e `core/designsystem/` não importa nada de `feature/`. **Dívida:** em `composable/`, `LeftHandMirror`, `ThemePreviewOverlay`, `PickPalette` e `PickFont` importam `LocalHomeUiState` de `feature/home/model`.

**Gates**
- Detekt: 5 issues, no limite. Todas são anteriores e de fora da fase: `ContainerRepository`, `ReleaseNotesData`, `QuickButtonGridItem`, `DynamicGridContainer` e `TextResource`.
- Testes unitários: 1240, todos passando. Há o novo `OverlaySheetGestureZoneTest`, com 7 testes, incluindo arrastar a faixa para expandir.
- Cobertura e `assembleDebug`: passaram.
- Screenshot: 32 referências regeneradas, todas de sheets em cima do `AureoleEditOverlay` (sem pílula e com o cabeçalho novo): clock (One-hand e HomeDialogs), Customize, EditContainer (horizontal e vertical), EditWidgetGroup, FavoriteApps, QuickButton (Appearance e Settings), ShortcutSelection e ShortcutSearch, PickPalette e PickFont (Settings e ThemePreview, destro e canhoto), OnboardingPalettePicker, HomeScreenWidgetStack, AureoleEditOverlay, Sheet e SheetSurface. Também foi criado o `OverlaySheetGestureZoneScreenshotTest` (imagem vazia de propósito) e removida a referência do `OverlaySheetHandleScreenshotTest`.

**Visual** (`clock.png` e `customize.png`): a base está **quase**, sem pílula e com cabeçalho, alturas, cantos e lado da mão certos. O respiro embaixo e a margem da engrenagem já foram corrigidos.

**Ficou faltando (fora do escopo da fase 1)**
- Conteúdo do relógio: rótulo "PRÉVIA", títulos de seção em versalete, estilo em 3 cartões no lugar do dropdown, alinhamento em segmented, divisória antes das abas e "Restaurar" neutro (hoje vermelho e cortado em "Reset Defau…").
- Data da prévia do relógio com idiomas misturados ("Wednesday, 31 de December"): possível bug de locale.
- Conteúdo do Personalizar: seção "AJUSTAR" com 3 botões e "ADICIONAR À HOME" em grade de 4 colunas com ✓ e estado esmaecido.
- Teste no aparelho (pendente).

### Fase 2: lista reordenável (11/11/2026)

**O que mudou**
- Novos em `composable/reorder/`: `ReorderableRow` e `ReorderableColumn` (arrastar após segurar, tic a cada troca, ✕, slot final "+", `customActions` de mover e remover) e a lógica pura `ReorderMath` e `ReorderDragState`, com testes. O selo ✕ sobrepõe a borda por offset, sem encolher a linha.
- **Pilha de widgets:** a prévia virou uma fileira reordenável com ✕, destaque do cartão atual e cartão "+" (cartões de 110x90 dp). O `EditWidgetGroupScreen` caiu de 409 para 152 linhas e perdeu 2 `@Suppress`.
- **Desfazer:** remover widget ("Widget removido") e reordenar a pilha ("Ordem alterada", um só por gesto).
- **Bandeja "Escolhidos" da pasta**, **itens do container** e **favoritos** passaram a usar os componentes reordenáveis. As setas ↑↓ e as strings fixas "Arrow Up/Down" saíram. Essas três telas são rascunho até Salvar/Concluído e por isso não têm desfazer.
- O seletor de apps da pasta segue o protótipo: a grade mostra todos os apps com ✓ e os escolhidos ficam só na bandeja.

**Gates**
- Detekt: 5 issues, no limite (as mesmas de antes). Testes unitários: 1285 passando. Cobertura (inclusive de branches de lógica), `assembleDebug` e screenshot: passaram.
- Screenshot: referências regeneradas de pilha de widgets (destro, canhoto e 2 widgets), `HomeScreenWidgetStackBottomSheet`, `FavoriteAppsDialog`, `FolderAppPickerDialog`, picker (grade e busca), container (gerenciar itens), favoritos (gerenciar lista) e `composable/reorder/`.

**Visual** (`widgetgroup`, `picker`, `container`, `favorites`): **quase**. Corrigidos depois da revisão: cartão "+" fora do print, ordem da bandeja invertida no canhoto, espelho pela metade em favoritos e linhas com ✕ mais estreitas.

**Ficou faltando**
- Rolagem automática quando o item arrastado chega na borda da fileira.
- Em favoritos no canhoto, os cabeçalhos também espelham ("Favorites" à direita, "Disable all" à esquerda). Decidir se isso fica assim.
- Fora do escopo da fase 2, ficam para a fase 4: botão "Adicionar widget" ao lado de Salvar e rótulo "PRÉVIA AO VIVO" na pilha; subtítulos, chevron, "OPÇÕES" e Cancelar no container; menu principal de favoritos (toggle, atalhos, aparência, gerenciar).
- Teste no aparelho (pendente): arrastar com o polegar nas 4 telas, widget real nos cartões de 110x90 dp e destaque de página no carrossel.

### Fase 3: menus e popup da pasta (11/11/2026)

**O que mudou**
- Base de menu nova em `composable/menu/`: `AureoleMenuSheet` (sobre `AureoleEditOverlay`), linhas de 56 dp com "Remover" em vermelho no topo, rótulo de seção em versalete e fileira de ações redondas (a ação com `requiresHold` exige 1 s de pressão). Tokens `menuRowHeight` e `quickActionIconSize` em `AureoleDimens`.
- Menus do app, da pasta e do item da grade migrados. `EditGridItemDialog` só mostra "Tamanho atual: N x M células" e as ações.
- `OpenedFolderPopup`: modo grade centralizado e sem botões, com o título dentro do cartão; modo lista ancorado, só com o título pequeno. `onAddAppsClick` e `onEditFolderClick` saíram do popup (as ações já estavam no menu da pasta como "Editar atalhos" e "Renomear/Ícone").
- Desfazer ao remover pasta ("Pasta removida") e componente da grade ("Componente removido").

**Gates:** detekt 5 issues (as de antes), testes, cobertura, screenshot e `assembleDebug` passando. Um teste de `HomeViewModelUndoTest` falhou uma vez em rodada conjunta e passou na seguinte (possível flaky por causa do Room compartilhado).

**Visual:** **quase**. Corrigidos depois da revisão: ícone de Favorito (agora estrela), seta das linhas menor, título do popup em grade, header do popup em lista, divisória acima da fileira de ações e logo no preview da pasta.

**Ficou faltando / para decidir**
- **Lado da mão no menu do app:** o texto do protótipo manda a ação mais usada para o lado do polegar. A `appmenu.png` destra mostra o oposto (Favorito à esquerda, "Segure p/ desinstalar" à direita). Foi seguido o texto, então o destro sai espelhado em relação à imagem.
- Faltam "Pasta" e "Ícone" na fileira do app (não existem ações no código).
- Menu de app dentro de container e de pasta aparece sem Favorito (`HomeScreenFolderOverlayComponents.kt` não liga `onToggleFavorite`).
- Sem ícone chevron no catálogo: a seta é `ArrowDown` girada, e no canhoto ela aponta para fora do texto.
- No canhoto o espelho chega ao texto das linhas, e não há referência canhota para comparar.
- O ícone de "Editar atalhos" no menu da pasta passou a ser a estrela de favoritos.
- Strings órfãs: `home_app_menu_uninstall`, `home_app_menu_add_to_favorites`, `home_app_menu_remove_from_favorites`, `home_folder_opened_add_apps`, `home_folder_opened_edit`, `home_grid_config_title`.
- Menu do botão rápido (`qbsettings`) não foi migrado (entra na fase 4).
- Os screenshots dos popups são recortados e não mostram a posição na tela. Teste no aparelho pendente.

### Fase 4: editores restantes (11/11/2026)

**O que mudou**
- **Componentes novos:** `AureoleConfirmOverlay` (prévia ancorada + Cancelar/Confirmar do lado da mão), `AureolePreviewFrame` (rótulo da prévia), `AureoleSwitchRow`, `AureoleSegmentedControl`, `AureolePrimaryActionButton`, seletor de cor (`composable/color/`: swatches e barra de matiz) e subtítulo nas linhas de menu.
- **Container:** menu principal "OPÇÕES" com subtítulo e chevron, prévia ao vivo e Cancelar/Salvar. Cancelar, voltar, arrastar e tocar fora agora descartam o rascunho (antes, fechar salvava).
- **Favoritos:** menu principal (toggle, Editar atalhos, Aparência, Gerenciar e ordem) e botão "Concluído". As subtelas de atalhos e aparência foram restauradas depois de uma regressão da rodada 4A.
- **Pilha de widgets:** barra com "Adicionar widget" e "Salvar" do lado da mão, e rótulo "PRÉVIA AO VIVO".
- **Fundo e cor:** fundo com tipo (cor sólida ou imagem), swatches e mini-home com a cor escolhida; seletor de cor em overlay. "Restaurar fundo" passou a existir na UI, sem diálogo e com desfazer (o arquivo da imagem vai para `WallpaperUndoStash` em vez de ser apagado).
- **Pasta:** formulário com prévia do nome ao digitar, campo de nome colado na barra de baixo.
- **Botão rápido:** aparência com rascunho (Salvar grava, Cancelar descarta) e menu como `AureoleMenuSheet`.
- **Cartões e vidro:** uma sheet só (`CardsGlassBottomSheet`) no lugar de `BlurBottomSheet`, `HazeOpacityDialog` e `PickBorderRadiusBottomSheet`, com cantos, vidro e opacidade e efeito na prévia. Novo `hazeBlurLevel` persistido.
- Logo sempre desenhado no header das sheets com título; número dos subtítulos corrigido no canhoto.

**Gates:** detekt 5 issues (as de antes; o `--auto-correct` arrumou um `ImportOrdering` novo), testes, cobertura, screenshot e `assembleDebug` passando.

**Visual:** **quase** em todas as telas. Corrigidos depois da revisão: número invertido no canhoto, logo ausente, margens das prévias do container e de favoritos, cor do fundo na mini-home, tile e prévia do formulário da pasta, prévia de cartões espelhada no canhoto e rótulos e swatches espelhados.

**Ficou faltando**
- **Nível de vidro:** `hazeBlurLevel` só vale na prévia e na linha "Efeito de vidro". A Home ainda usa o raio fixo (`Container`, `OpenedFolderLayouts`, `AureolePopup`, `ClockHeader`).
- **Desfazer do fundo:** `WallpaperManager.clear()` do sistema continua sendo chamado e não é revertido.
- Formulário da pasta: altura da sheet ainda acima da ref (~47%); o tile "letra" continua na grade.
- Prévia do container sem a moldura tracejada; botão rápido com linhas que abrem seletores em vez de grades inline.
- Prévias não conferidas lado a lado com a ref depois das correções: container, pasta e destro de cartões e fundo.
- Mini-home do fundo ainda alinha os favoritos ao lado do polegar no canhoto.
- Seletor de cor e seletor de ícones abertos pela aparência do botão rápido ficam empilhados (dois scrims).
- **Teclado:** os prints não injetam IME. Conferir no aparelho o campo de nome da pasta.
- Teste no aparelho (pendente): prévias e Aplicar/Cancelar de todas as sheets, desfazer do fundo, blur e opacidade ao vivo.

### Fase 5: adicionar container e opções de posição (11/11/2026)

**O que mudou**
- `AddContainerDialog` virou `AureoleMenuSheet` com uma linha de 56 dp por container (ícones do catálogo: Calendário e Estrela para relógio e lista de apps).
- Novo `AureoleRadioSheet` (`composable/menu/`): linhas de 56 dp, linha marcada destacada e marcador no lado do polegar (espelhado no canhoto), escolher aplica e fecha, folga embaixo.
- Posição do botão, da busca e do painel usam essa sheet, com desfazer ("Posição alterada") em `SettingsViewModel.selectPosition`. Escolher a opção já marcada não faz nada.
- Saíram os textos fixos "Esquerda (Left)", "Direita (Right)", "Superior (Top)" e similares; entraram 8 chaves `settings_position_option_*` e `settings_position_changed` em EN e pt-BR.
- Correções finais das fases 4 e 5: switch de `AureoleSwitchRow` sempre LTR (no canhoto parecia desligado quando estava ligado), rótulos de seção sempre à esquerda, grade de ícones da pasta só com ícones distintos (`FolderIconRegistry.pickerKeys`, 7 colunas, 3 linhas).

**Gates:** detekt 5 issues (as de antes; duas de `MaximumLineLength` de um teste da fase 4 foram corrigidas), testes, cobertura, screenshot e `assembleDebug` passando. Nenhum `@Suppress` novo; nenhum arquivo de produção novo acima de 500 linhas.

**Visual:** **igual** em Adicionar container e nas três posições, destro e canhoto.

**Ficou faltando**
- O fechamento ao escolher uma opção é imediato, sem os 300 ms de animação do `AureoleMenuSheet`.
- Título centralizado (a `optpanel.png` o mostra à esquerda) e sem fundo de mini-home no preview.
- Ícones de container de grade e widget não existem no catálogo (`AureoleIcons`).
- Os valores de posição continuam em inglês no armazenamento ("Left", "Space Between"), por compatibilidade com o `Container.kt`.
- `FolderIconRegistry` tem vários fallbacks para o mesmo ícone; ícones próprios são trabalho de catálogo.
- Seletor de cor: matiz em barra (a ref usa círculo) e rótulo "PREVIEW" à esquerda (a ref centraliza).
- Cartões e vidro: Aplicar/Cancelar, enquanto a `cards.png` tem só "Pronto".
- Teste no aparelho (pendente).

### Pendências das fases 3 a 5 resolvidas (11/11/2026)

- **Nível de vidro na Home:** `GlassBlur.kt` (`LocalGlassLevel` e `glassBlurRadius`) aplica o nível de "Cartões e vidro" em todos os pontos de blur da Home e na prévia. Médio mantém o raio de hoje; Leve é 0,4x e Máx. 1,6x (palpite visual, ajustar no aparelho). `GlassLevel` foi para `core/designsystem/model/`.
- **Desfazer do fundo:** o app nunca aplicava a imagem personalizada no `WallpaperManager`; o `clear()` apagava o fundo de tela do Android. Agora o fundo do sistema é salvo antes de limpar (`system_wallpaper_undo.jpg`) e reaplicado ao desfazer. `clear()` só roda quando há imagem personalizada. Validar no aparelho se `getWallpaperFile` funciona sem permissão de storage; se falhar, o desfazer devolve só a imagem.
- **Menu do app:** Favorito aparece dentro de container e de pasta (na pasta usa a lista de favoritos padrão, a pasta não tem lista própria).
- **Não feitos (features novas):** botão "Pasta" (falta seletor de pasta de destino, criar pasta e ação "adicionar app à pasta" com desfazer) e botão "Ícone" (não existe troca de ícone por app).

### Crash do menu do app na lista de favoritos (11/11/2026)

- **Sintoma:** long click em um item de favoritos fechava o app com `Can't represent a width of 802 and height of 2147483095 in Constraints` (`AnchoredPreviewLayout`).
- **Causa:** `AppItemRow` compunha o `AppItemBottomSheet`, que desde a fase 3 é um `AureoleEditOverlay` em `fillMaxSize`, dentro da linha de uma lista com rolagem (altura ilimitada). Antes era um `ModalBottomSheet`, que abre numa janela própria.
- **Correção:** `AureoleOverlayWindow` (`composable/overlay/`) é um `Dialog` em tela cheia e envolve o menu em `AppItemRow`. O `AnchoredPreviewLayout` também passou a aceitar altura ilimitada. Testes novos (long click dentro de `LazyColumn` e de `Column` com rolagem) falham sem a correção.
- **Validado no aparelho:** long click em item de favoritos abre o menu sem crash.
- **Ainda a conferir no aparelho:** fechar pelo botão voltar e por arrastar, insets dentro da janela, "Desinstalar" por segurar e a engrenagem, e o menu sem o blur do Haze (outra janela).

### Fechamento das fases 6 a 9 (11/11/2026)

Commits (sem push), na `release/0.9.20`: `263e1c5` (6), `a00eed1` (6b), `11b0c41` (6c), `4d66329` (7a), `353fa64` (7b), `8c06137` (7c), `1c5329a` (7d), `117e5e4` (8), `89399cd` (9) e os de correção da revisão visual final.

**Gates (do zero, no fim):** detekt 5 issues (as de antes); `testDebugUnitTest` completo; `jacocoTestCoverageVerification` (branches de lógica 75,5%); `updateDebugScreenshotTest` + `validateDebugScreenshotTest`; `assembleDebug`. Nenhum `@Suppress` novo; nenhum arquivo de produção novo acima de 500 linhas; `composable/` e `core/designsystem/` sem import de `feature/`.

**Visual:** revisão de todas as 41 telas de `ref/telas/`: 17 iguais, 13 quase e 5 diferentes. Os 5 diferentes (seletor de apps da pasta, seletor de atalho, gerenciar favoritos, menu do grupo de widgets, "Mover" no canhoto) foram corrigidos em duas rodadas e reconferidos: ficaram **quase**. Uma última rodada tratou as regressões da reconferência (divisor de favoritos sobre a busca, busca encostada nos botões do seletor da pasta, texto do atalho no canhoto, posição do "Mover", ícone de lápis duplicado). **Essa última rodada não foi reconferida visualmente depois de regenerar os prints.**

**O que mudou na 6 a 9**
- 6: prévia sem rótulo; ícones em coluna fixa nas linhas; cards da pilha em 2,5 por tela; "Mover" por botões; "Cartões e vidro" aplica na hora e fecha com "Pronto".
- 6b: favoritos sem duplicação (sem favoritos = todos os apps), alça de arrastar, migração do flag legado.
- 6c: 80 ícones do Material Symbols, grade de 5 colunas com chips de categoria.
- 7: `LocalLeftHanded` no DS, `ChevronRight`, data do relógio no locale, relógio em cartões, Personalizar em Ajustar + grade de 4 colunas, moldura tracejada, aparência do botão rápido inline, fechamento animado das opções, autoscroll na fileira reordenável.
- 8: diálogos de confirmação (redefinir launcher exige segurar 1,5 s), atualização, notas de versão com chips.
- 9: textos fixos movidos para strings, previews e varredura de regras.
- Revisão final: seletor de apps da pasta e seletor de atalho sobre a base padrão, menu do grupo de widgets em `AureoleMenuSheet`.

**Pendências e decisões para você**
- Teste no aparelho de tudo (nenhuma caixa "Aparelho" marcada, exceto o crash do long click que foi validado).
- `ReleaseNotesData` e `ReleaseNotesRepository` ainda têm ~140 textos fixos em pt-BR nos modelos (exige trocar o tipo para `TextResource`); nomes de paleta são dados (precisam de id estável para traduzir).
- 19 `@Suppress` antigos (5 `LongMethod` em `HomeScreen.kt`) e 5 issues de detekt antigas: refatoração à parte.
- `settings_advanced_replay_onboarding` não tem uso: a opção "Rever boas-vindas" em Configurações › Avançado parece não existir.
- Mini-home de Paleta, Fonte e Fundo: os apps ficam do lado do polegar, e o plano pede o lado oposto; decidir qual vale.
- Menu do app: o plano manda a ação mais usada no lado do polegar, e a `appmenu.png` mostra o oposto.
- Seletor de atalho: chips Aureole/Sistema/Apps (sem "Outro app"); "Aparência" removida do menu de favoritos; subtela de atalhos de favoritos igual à de gerenciar.
- Botões "Pasta" e "Ícone" por app não foram feitos (features novas).
- Tema claro nos screenshots: o projeto não tem convenção; não criado.
- O chevron no canhoto aponta para a esquerda e pode ser lido como "voltar".
- `ShortcutAction.displayName` ainda tem texto fixo em inglês.
- Fator de blur Leve (0,4x) e Máx. (1,6x), `WallpaperManager` ao desfazer o fundo e velocidade do autoscroll (800 dp/s, faixa de 48 dp): conferir no aparelho.

### Teste no aparelho (11/11/2026)

O Mateus validou no aparelho os itens da lista de testes das fases 1 a 9 (arrastar com o polegar e autoscroll, "Redefinir launcher" por segurar, desfazer do fundo, blur, campo de nome da pasta com teclado, TalkBack, menu do app em janela própria). Coluna "Aparelho" marcada em todas as fases. As decisões abertas listadas em "Fechamento das fases 6 a 9" continuam valendo.
