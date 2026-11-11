# Bottom sheets para uma mão: plano de desenvolvimento

**Base:** `release/0.9.13` · **Protótipo:** `specs/bottom-sheets/one-hand-prototype.html` (31 telas)
**Referências:** `ref/telas/<tela>.png` (todas as telas; `-canhoto` nas 6 principais) e `ref/fase1-*.png`
**Como rodar:** `/fase <n>` · progresso em `/estado` · acompanhamento em `progress.md`. Commit e push ficam com o Mateus.

## Resumo

| # | Fase | Modelo | Tempo (agente + gates) | Tokens |
|---|---|---|---|---|
| 1 | Base da sheet: 60/90, faixa de gesto, vibração, prévia ancorada, lado da mão (+1b `SettingsBottomSheet`) | `sheet-gestures` (Opus) | 4–5,5 h | 200–300k |
| 2 | Desfazer por 4 s no lugar de confirmação | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–120k |
| 3 | Lista reordenável: arrastar, ✕ remover, “+” adicionar | `sheet-gestures` (Opus) | 2,5–3,5 h | 120–180k |
| 4 | Menus e popup da pasta (grade centrada, lista ancorada) | `sheet-ui` (Sonnet) | 2–3 h | 100–150k |
| 5 | Editores com prévia: relógio com abas que deslizam, container, favoritos, pilha de widgets, plano de fundo, cor, pasta, botão rápido, cartões e vidro | `sheet-ui` (Sonnet) | 3,5–5 h | 180–260k |
| 6 | Listas e busca: atalho e apps da pasta (busca no teclado, arrastar para marcar, A–Z), adicionar container, opções de posição | `sheet-gestures` (Opus) | 3,5–5 h | 200–300k |
| 7 | Paleta e Fonte com mini-home ao vivo | `sheet-ui` (Sonnet) | 1,5–2,5 h | 100–150k |
| 8 | Diálogos e avisos: confirmações, atualização, novidades | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k |
| 9 | Strings, previews, screenshots e acabamento | `sheet-chores` (Haiku) | 1,5–2 h | 50–90k |
| — | Revisão visual ao fim de cada fase | `visual-reviewer` (Opus) | 10–20 min | 30–50k por fase |

**Total:** cerca de 22–32 h de trabalho do agente e 1,4–2,2 M de tokens, já contando as revisões.
**Calendário realista:** 2 a 3 semanas, de 2 a 3 fases por semana. O ritmo é limitado por você: ok no plano de cada fase, olhar os prints e testar no aparelho (cerca de 30–60 min por fase).
**O que costuma estourar o tempo:** divergência de screenshot test (a decisão é sua), detekt com mais de 5 issues, arquivo acima de 500 linhas, cobertura abaixo de 85/70/60.

### Ordem e dependências

1 → 2 e 3 → 4, 5, 6, 7 e 8 (qualquer ordem entre elas, mas 5 antes de 6 reaproveita mais) → 9.
As fases 4 a 8 dependem de 1 (base), 2 (desfazer) e 3 (reordenar). Não pule.

### Por que cada modelo

- **Opus:** gestos e física (fases 1, 3 e 6), e a revisão visual. Erra caro e é difícil de testar à mão.
- **Sonnet:** telas e componentes com padrão e referência definidos (2, 4, 5, 7 e 8).
- **Haiku:** trabalho mecânico de strings, previews e esqueleto de testes (9).

### Diferenças aceitas em relação ao protótipo

- fonte do sistema no lugar da fonte do navegador;
- conteúdo de exemplo;
- selo TIC, zona do polegar e linha de alcance, que só existem no protótipo para explicar.

### Decisões do protótipo v5

- Sem pílula de arraste: o gesto vive numa faixa invisível de 20 dp no topo; dica de acessibilidade “Segure o topo da sheet”.
- “Cartões e vidro” e “Opacidade do desfoque” viram uma sheet só (`BlurBottomSheet`, `HazeOpacityDialog`, `PickBorderRadiusBottomSheet`).
- Pasta aberta em modo grade: popup centralizado, só com os apps. Em modo lista: popup ancorado perto da pasta, como hoje. Sem Adicionar Apps nem Editar Pasta no popup.
- Sem sheet de tamanho de widget: `EditGridItemDialog` só mostra “Tamanho atual” (leitura); “Redimensionar Grupo” abre o editor da pilha.
- Pilha de widgets: a prévia lista os widgets com ✕ (com desfazer), arrastar após segurar e cartão “+”.
- Paleta e Fonte: grade de chips (paleta em 3 colunas, fonte em 2), mini-home só com relógio, data e 4 apps, Aplicar/Cancelar do lado da mão.
- Pendência: `SettingsDialogComponents` tem “Esquerda (Left)” escrito no código; vai para `strings.xml` (fase 6).

### Fora do escopo (decidir depois)

Telas cheias de Settings, onboarding, `AureoleFloatingSearchBar`, tema claro, paisagem e tablet. Os screenshot tests devem cobrir tema claro nas sheets novas.

---

## Fase 1: base da sheet (DS)

**Escopo:** só `AureoleEditOverlay` em `core/designsystem/`. Ele é usado por:
- `EditClockBottomSheet`, `EditContainerScreen`, `EditWidgetGroupScreen`;
- as 3 sheets de `quickbutton/`;
- `FavoriteAppsDialog`.

As sheets em `ModalBottomSheet` (`SettingsBottomSheet`: paleta, fonte, cartões, opções) entram na **etapa 1b**, no fim desta fase: `SettingsBottomSheet` passa a usar a mesma base, para ter alturas 60/90, faixa de gesto e prévia ancorada. Sem isso, as fases 7 e 8 não têm onde ancorar a mini-home.

**Referências:**
- `ref/fase1-clock-rest.png`: em repouso, 60%, com a prévia logo acima da sheet;
- `ref/fase1-clock-expanded.png`: expandida, 90%, conteúdo sobe junto;
- `ref/fase1-dragging-detents.png`: durante o arraste, alturas 60% e 90%;
- `ref/fase1-shortcut-rest.png`: sheet com lista longa, limitada a 60%;
- `ref/fase1-clock-left.png`: modo canhoto.

### Comportamento esperado

1. **Altura em repouso:** abraça o conteúdo até no máximo 60% da tela.
2. **Expandida:** 90% da tela. O conteúdo fica no topo da sheet e sobe junto; nada fica preso embaixo.
3. **Soltar o arraste** encaixa na altura mais próxima, 60% ou 90%. Abaixo de cerca de 70% da altura de repouso, fecha. Velocidade forte para cima expande; forte para baixo recolhe e, se já estiver em repouso, fecha.
4. **Vibração:** um tic (`HapticFeedbackType.SegmentTick`) ao cruzar 60% ou 90% durante o arraste e ao encaixar.
5. **Toque duplo no topo da sheet** alterna entre 60% e 90%, com tic.
6. **Segurar o topo da sheet** (cerca de 600 ms, sem mover mais que o touch slop) chama `onSettingsClick`, com `HapticFeedbackType.LongPress`. O logo no header continua igual.
7. **Prévia ancorada:** o `previewContent` acompanha o topo real da sheet (`top = sheetTop - medium - alturaDaPrévia`), e não mais uma altura fixa.
8. **Lado da mão:** a config recebe o lado do polegar. Nesta fase ele só define o lado do rótulo de acessibilidade e fica pronto para as próximas. O DS não importa nada de `feature/`; quem chama passa `isLeftHandedMode`.
9. **Acessibilidade:** a faixa de gesto tem `contentDescription` e `customActions`: "Expandir" ou "Recolher", e "Ajustes do Aureole".

### Mudanças sugeridas

**Modelo e lógica** (`core/designsystem/model/`):
- Em `AureoleEditOverlayConfig`: trocar `maxSheetHeightFraction` por `restHeightFraction = 0.6f` e `expandedHeightFraction = 0.9f`. Adicionar `isLeftHanded: Boolean = false`.
- Hoje os callers passam 0.3, 0.4, 0.45, 0.65 e 0.8. Revisar cada um: a sheet agora abraça o conteúdo, então a maioria pode só remover o parâmetro.
- Novo `SheetDetents.kt` com funções puras e testes unitários. Exemplos:
  - `resolveDetent(height, rest, expanded, velocity): SheetDetentTarget`, que devolve `Rest`, `Expanded` ou `Dismiss`;
  - `crossedDetent(previous, current, detents): Boolean`.

**Componentes** (`core/designsystem/components/`):
- Novo `OverlaySheetGestureZone.kt`: faixa invisível de 20 dp no topo da sheet (sem pílula desenhada), com `pointerInput` e `detectTapGestures(onDoubleTap, onLongPress)`, mais o drag.
- `AureoleEditOverlaySheetSurface.kt`: altura com `heightIn(max = rest)` em repouso e `height(expanded)` expandida, animada.
- `AureoleEditOverlaySheet.kt`: `PreviewSlot` ancorado ao topo medido da sheet, via `onGloballyPositioned` ou um layout próprio.

**Strings** (EN e pt-BR):
- `overlay_sheet_gesture_zone_description`
- `overlay_sheet_action_expand`
- `overlay_sheet_action_collapse`
- `overlay_sheet_action_settings`

**Limites:** todos os arquivos com menos de 500 linhas; constantes de gesto como `private const val`.

### Testes

- **Unitários** (`app/src/test/`): `resolveDetent`, cobrindo encaixe por proximidade, velocidade e fechar; `crossedDetent`.
- **Screenshot** (`app/src/screenshotTest/`): overlay em repouso, expandido e canhoto. Usar `EditClockBottomSheet` como caso real.

### Pronto quando

- [ ] Os 5 gates passam: detekt com no máximo 5 issues, testes unitários, cobertura 85/70/60, screenshot e `assembleDebug`.
- [ ] O `visual-reviewer` diz **igual** ou **quase**, com ajustes feitos, contra `ref/fase1-*`.
- [ ] Testado no aparelho: arrastar, toque duplo, segurar, vibração e TalkBack.

---

## Fase 2: desfazer por 4 s

**Referências:** `ref/telas/foldermenu.png`, `picker.png`, `favorites.png`.
**Entrega:**
- `AureoleUndoSnackbar` em `composable/`, stateless, com o botão “Desfazer” no lado do polegar e barra de 4 s.
- `UndoController` puro em `model/` (fila, prazo, desfazer), com teste unitário de prazo e substituição.
- Usado em: remover app da pasta, favoritar, trocar atalho, remover widget e reordenar. Segurar 1 s fica só para desinstalar.
- Strings: `undo_snackbar_action`, `undo_snackbar_removed`, `undo_snackbar_reordered`.
**Pronto quando:** gates + `visual-reviewer` + o snackbar nunca cobre o botão Salvar.

## Fase 3: lista reordenável

**Referências:** `ref/telas/container.png`, `widgetgroup.png`, `picker.png`, `favorites.png`.
**Entrega:**
- `ReorderableRow` e `ReorderableColumn` em `composable/`, com arrastar após segurar (`detectDragGesturesAfterLongPress`), ✕ remover e vibração a cada troca.
- Cálculo de troca em função pura (`model/ReorderMath.kt`), com teste: arrastar para cima, para baixo, limites e lista de 1 item.
- Usado em: itens do container, bandeja de apps da pasta, favoritos e a lista de widgets da prévia da pilha, que também ganha o cartão “+”.
- Acessibilidade: `customActions` “Mover para cima/baixo”.
**Pronto quando:** gates + teste no aparelho do arrastar com o polegar.

## Fase 4: menus e popup da pasta

**Referências:** `ref/telas/appmenu.png`, `itemmenu.png`, `widgetstack.png`, `foldermenu.png`, `qbsettings.png`, `folderpopup.png`, `folderlist.png` (+ `folderpopup-canhoto.png`).
**Entrega:**
- Menus como lista de linhas de 56 dp com a ação mais usada perto do polegar; “Remover” em vermelho no topo, com desfazer.
- `EditGridItemDialog`: só “Tamanho atual: N x M células” (leitura) e as ações.
- `OpenedFolderPopup`: grade → popup centralizado sem botões; lista → ancorado perto da pasta.
**Pronto quando:** gates + visual.

## Fase 5: editores com prévia

**Referências:** `ref/telas/clock.png`, `container.png`, `favorites.png`, `widgetgroup.png`, `background.png`, `colorpicker.png`, `folderform.png`, `folderedit.png`, `qbappearance.png`, `cards.png` (+ canhoto).
**Entrega:**
- Relógio: `HorizontalPager` no conteúdo, com as abas no rodapé sincronizadas.
- Prévia ancorada em todos (vem da fase 1); Salvar e Cancelar do lado da mão.
- Cartões e vidro numa sheet só, com efeito na hora.
- Formulário da pasta: nome colado no teclado.
**Pronto quando:** gates + visual; revisar com o teclado aberto.

## Fase 6: listas e busca

**Referências:** `ref/telas/shortcut.png`, `picker.png`, `addcontainer.png`, `optbtn.png`, `optsearch.png`, `optpanel.png` (+ `shortcut-canhoto.png`).
**Entrega:**
- Busca grudada no teclado (`imePadding()`), lista com `reverseLayout = true`, ranqueamento em função pura (começa com o termo primeiro), ↵ escolhe o melhor.
- Arrastar para marcar vários apps (`detectDragGesturesAfterLongPress` com hit-test na grade).
- Faixa A–Z na borda do polegar, com balão e tic por letra.
- Opções de posição: rádio ao alcance, com desfazer; **tirar o “Esquerda (Left)” fixo** de `SettingsDialogComponents` para `strings.xml`.
**Pronto quando:** gates + testes puros de ranking e de hit-test + teste no aparelho.

## Fase 7: Paleta e Fonte

**Referências:** `ref/paleta.png`, `ref/fonte.png`.
**Entrega:**
- Mini-home ancorada com relógio, data e 4 apps; tema local aplicado na hora. Cancelar, arrastar ou tocar fora voltam ao anterior.
- Paletas em 3 colunas com faixa de cores; fontes em 2 colunas, cada nome na própria fonte.
- Os apps da prévia ficam do lado oposto ao polegar.
**Pronto quando:** gates + visual em modo destro e canhoto.

## Fase 8: diálogos e avisos

**Referências:** `ref/telas/confirmlayout.png`, `confirmfactory.png`, `confirmwall.png`, `updateavail.png`, `updateready.png`, `releasenotes.png`.
**Entrega:**
- Diálogos flutuantes no polegar; “Redefinir launcher” exige segurar; “Restaurar fundo” sem diálogo, com desfazer.
- Atualização disponível e pronta; notas de versão com chips de versão.
**Pronto quando:** gates + visual.

## Fase 9: acabamento

- Todas as strings novas em EN e pt-BR.
- `@AureolePreview` privado em todo componente novo.
- Screenshot tests de cada tela nova (destro e canhoto nas 6 principais, e tema claro nas novas).
- Varredura final: arquivos acima de 500 linhas, `@Suppress`, imports de `feature/` no DS.
**Pronto quando:** os 5 gates passam do zero e o `visual-reviewer` percorre `ref/telas/` inteiro.
