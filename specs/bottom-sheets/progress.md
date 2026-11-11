# Progresso: bottom sheets para uma mão

Atualizado por `/fase`. Marque `[x]` quando os gates passarem e o veredito visual for **igual** ou **quase** (com ajustes feitos).

## Já feito (antes deste plano, até a release/0.9.18 e commits locais)

- [x] Base da sheet: 60/90%, tic ao cruzar, toque duplo e segurar, prévia ancorada, acessibilidade (ainda com pílula visível)
- [x] Desfazer por 4 s (`UndoController`, `AureoleUndoSnackbar`): pasta, favoritos, botão rápido
- [x] Busca grudada no teclado com melhor resultado perto do campo
- [x] Seletor de apps: arrastar para marcar e faixa A–Z
- [x] Relógio com abas que deslizam
- [x] Paleta e Fonte com mini-home e efeito na hora (grade de chips)
- [~] Commits locais ainda sem push: `AureoleActionRow`, `LeftHandMirror`, ajustes de Paleta e Fonte

## Fases restantes

| # | Fase | Modelo | Estimativa | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|
| 1 | Ajustes da base: faixa de gesto invisível, `ActionRow`/espelho, base do `SettingsBottomSheet` | Opus | 1,5–2,5 h | [x] | [x] quase | [ ] |
| 2 | Lista reordenável com ✕ e "+" (e desfazer em widgets e reordenar) | Opus | 2,5–3,5 h | [x] | [x] quase | [ ] |
| 3 | Menus e popup da pasta | Sonnet | 2–3 h | [ ] | [ ] | [ ] |
| 4 | Editores restantes | Sonnet | 3–4,5 h | [ ] | [ ] | [ ] |
| 5 | Adicionar container e opções de posição | Sonnet | 1,5–2,5 h | [ ] | [ ] | [ ] |
| 6 | Diálogos e avisos | Sonnet | 1,5–2,5 h | [ ] | [ ] | [ ] |
| 7 | Acabamento | Haiku | 1–1,5 h | [ ] | [ ] | [ ] |

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
