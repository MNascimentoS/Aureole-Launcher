# Bottom sheets para uma mão: plano de desenvolvimento

**Base:** `release/0.9.20` (criada a partir da `release/0.9.18` + commits locais) · **Protótipo:** `specs/bottom-sheets/one-hand-prototype.html` (31 telas)
**Referências:** `ref/telas/<tela>.png` (todas as telas; `-canhoto` nas 6 principais), `ref/paleta.png`, `ref/fonte.png`
**Como rodar:** `/fase <n>` · progresso em `/estado` e `progress.md`. Commit e push ficam com o Mateus.

## Onde estamos

Em 10/11 já estavam no código (ver `progress.md`): base da sheet 60/90%, desfazer por 4 s, busca grudada no teclado, seletor de apps com arrastar para marcar e A–Z, relógio com abas que deslizam, Paleta e Fonte com mini-home. Este plano cobre só o que falta para o app ficar igual ao protótipo v5.

## Resumo das fases restantes

| # | Fase | Modelo | Tempo (agente + gates) | Tokens |
|---|---|---|---|---|
| 1 | Ajustes da base: faixa de gesto invisível, `ActionRow` e espelho, base do `SettingsBottomSheet` | `sheet-gestures` (Opus) | 1,5–2,5 h | 100–160k |
| 2 | Lista reordenável com ✕ e "+", desfazer em widgets e reordenar | `sheet-gestures` (Opus) | 2,5–3,5 h | 120–180k |
| 3 | Menus e popup da pasta (grade centrada, lista ancorada) | `sheet-ui` (Sonnet) | 2–3 h | 100–150k |
| 4 | Editores restantes: container, favoritos, pilha de widgets, plano de fundo, cor, pasta, botão rápido, cartões e vidro | `sheet-ui` (Sonnet) | 3–4,5 h | 180–260k |
| 5 | Adicionar container e opções de posição | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k |
| 6 | Diálogos e avisos: confirmações, atualização, novidades | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k |
| 7 | Acabamento: strings, previews, screenshots, varredura | `sheet-chores` (Haiku) | 1–1,5 h | 40–80k |
| — | Revisão visual ao fim de cada fase | `visual-reviewer` (Opus) | 10–20 min | 30–50k por fase |

**Total:** cerca de 13–20 h de trabalho do agente e 0,9–1,4 M de tokens, já contando as revisões.
**Calendário realista:** 1,5 a 2 semanas, 2 a 3 fases por semana. O ritmo é limitado por você: ok no plano de cada fase, olhar os prints e testar no aparelho (30–60 min por fase). Os tempos são estimativas.
**O que costuma estourar o tempo:** divergência de screenshot test (a decisão é sua), detekt com mais de 5 issues, arquivo acima de 500 linhas, cobertura abaixo de 85/70/60.

### Ordem e dependências

1 → 2 → 3, 4, 5 e 6 (qualquer ordem entre elas; 4 antes de 5 aproveita mais) → 7.
As fases 3 a 6 dependem da 1 (base) e da 2 (reordenar e desfazer). Não pule.

### Por que cada modelo

- **Opus:** gestos e física (fases 1 e 2) e a revisão visual. Erra caro e é difícil de testar à mão.
- **Sonnet:** telas com padrão e referência definidos (3 a 6).
- **Haiku:** strings, previews e esqueleto de testes (7).

### Diferenças aceitas em relação ao protótipo

- fonte do sistema no lugar da fonte do navegador;
- conteúdo de exemplo;
- selo TIC, zona do polegar e linha de alcance, que só existem no protótipo para explicar.

### Decisões do protótipo v5

- Sem pílula de arraste: o gesto vive numa faixa invisível de 20 dp no topo; dica de acessibilidade "Segure o topo da sheet".
- "Cartões e vidro" e "Opacidade do desfoque" viram uma sheet só (`BlurBottomSheet`, `HazeOpacityDialog`, `PickBorderRadiusBottomSheet`).
- Pasta aberta em modo grade: popup centralizado, só com os apps. Em modo lista: popup ancorado perto da pasta, como hoje. Sem Adicionar Apps nem Editar Pasta no popup.
- Sem sheet de tamanho de widget: `EditGridItemDialog` só mostra "Tamanho atual" (leitura); "Redimensionar Grupo" abre o editor da pilha.
- Pilha de widgets: a prévia lista os widgets com ✕ (com desfazer), arrastar após segurar e cartão "+".
- Paleta e Fonte: grade de chips, mini-home só com relógio, data e 4 apps (do lado oposto ao polegar), Aplicar/Cancelar do lado da mão. **Já implementadas.**

### Fora do escopo (decidir depois)

Telas cheias de Settings, onboarding, `AureoleFloatingSearchBar`, tema claro, paisagem e tablet. Os screenshot tests devem cobrir tema claro nas sheets novas.

---

## Fase 1: ajustes da base

**Já existe:** `core/designsystem/model/SheetDetents.kt`, `OverlaySheetMotion.kt`, `SheetHandleAction.kt`, `components/OverlaySheetHandle.kt`, `AureoleEditOverlaySheet*.kt`, `composable/actions/AureoleActionRow.kt`, `composable/mirror/LeftHandMirror.kt`.
**Referências:** `ref/fase1-*.png` (a pílula nelas é da versão antiga; a nova não tem pílula) e `ref/telas/clock.png`, `customize.png`.

### Entrega

1. **Faixa de gesto invisível:** `OverlaySheetHandle` deixa de desenhar a pílula e vira uma faixa transparente de 20 dp no topo da sheet. Mantém arrastar, toque duplo, segurar, vibração e `customActions`. Se o nome ficar confuso, renomear para `OverlaySheetGestureZone` (atenção às strings `overlay_sheet_handle_*`).
2. **Lado da mão:** conferir que `AureoleActionRow` e `LeftHandMirror` cobrem o que o protótipo faz (ação principal no lado do polegar, listas espelhadas) e que o DS não importa nada de `feature/`.
3. **`SettingsBottomSheet`:** hoje é `ModalBottomSheet`. Decidir, lendo o código, se as sheets de Paleta, Fonte, Cartões e opções passam para a mesma base do `AureoleEditOverlay` (alturas 60/90 e prévia ancorada) ou se o `ThemePreviewOverlay` já resolve. Mostrar a conclusão no plano curto antes de mexer.
4. **Constantes de gesto:** `private const val`; funções puras com teste (`resolveDetent`, `crossedDetent` já têm; completar o que faltar).

### Pronto quando

- [ ] Os 5 gates passam: detekt com no máximo 5 issues, testes unitários, cobertura 85/70/60, screenshot e `assembleDebug`.
- [ ] `visual-reviewer` diz **igual** ou **quase** contra `ref/telas/clock.png` e `customize.png`.
- [ ] Testado no aparelho: arrastar, toque duplo e segurar no topo sem pílula; TalkBack.

## Fase 2: lista reordenável

**Já existe:** `UndoController`, `AureoleUndoSnackbar`, `onReorderWidgets` em `HomeScreenActions`, `EditWidgetGroupScreen`.
**Referências:** `ref/telas/container.png`, `widgetgroup.png`, `picker.png`, `favorites.png` (+ canhoto).
**Entrega:**
- `ReorderableRow` e `ReorderableColumn` em `composable/`: arrastar após segurar (`detectDragGesturesAfterLongPress`), ✕ remover e tic a cada troca.
- Cálculo da troca em função pura (`model/ReorderMath.kt`) com teste: para cima, para baixo, limites, lista de 1 item.
- Usado em: itens do container, bandeja de apps da pasta, favoritos e a lista da prévia da pilha de widgets (que também ganha o cartão "+").
- Desfazer ligado a remover widget e a reordenar, pelo `UndoController` existente.
- Acessibilidade: `customActions` "Mover para cima/baixo".
**Pronto quando:** gates + visual + teste no aparelho do arrastar com o polegar.

## Fase 3: menus e popup da pasta

**Já existe:** `AppItemMenus`, `ContainerFolderBottomSheet`, `OpenedFolderPopup` (com `isGridMode`, `onAddAppsClick`, `onEditFolderClick`).
**Referências:** `ref/telas/appmenu.png`, `itemmenu.png`, `widgetstack.png`, `foldermenu.png`, `qbsettings.png`, `folderpopup.png`, `folderlist.png` (+ `folderpopup-canhoto.png`).
**Entrega:**
- Menus como linhas de 56 dp com a ação mais usada perto do polegar; "Remover" em vermelho no topo, com desfazer.
- `EditGridItemDialog`: só "Tamanho atual: N x M células" (leitura) e as ações.
- `OpenedFolderPopup`: modo grade → popup centralizado sem botões; modo lista → ancorado perto da pasta, como hoje. Tirar `onAddAppsClick` e `onEditFolderClick` do popup; as duas ações ficam no menu da pasta (segurar a pasta).
**Pronto quando:** gates + visual.

## Fase 4: editores restantes

**Já existe:** relógio com abas (`EditClockPager`), `AureoleEditOverlay` com prévia ancorada.
**Referências:** `ref/telas/container.png`, `favorites.png`, `widgetgroup.png`, `background.png`, `colorpicker.png`, `folderform.png`, `folderedit.png`, `qbappearance.png`, `cards.png` (+ canhoto).
**Entrega:**
- Prévia ancorada e Salvar/Cancelar do lado da mão em todos.
- Cartões e vidro numa sheet só, com cantos, vidro e opacidade do desfoque e efeito na hora.
- Formulário da pasta: nome colado no teclado, com a prévia do nome ao digitar.
- Pilha de widgets: prévia com a lista (✕, arrastar, "+") vinda da fase 2.
**Pronto quando:** gates + visual; revisar com o teclado aberto.

## Fase 5: adicionar container e opções de posição

**Já existe:** `AddContainerDialog`, `SettingsDialogComponents` (com "Esquerda (Left)" escrito no código).
**Referências:** `ref/telas/addcontainer.png`, `optbtn.png`, `optsearch.png`, `optpanel.png`.
**Entrega:**
- Adicionar container: lista ao alcance.
- Opções de posição: rádio com linhas de 56 dp, opção marcada no lado do polegar, escolher aplica e fecha, com "Desfazer".
- **Tirar o "Esquerda (Left)" fixo** para `strings.xml` (EN e pt-BR).
**Pronto quando:** gates + visual.

## Fase 6: diálogos e avisos

**Referências:** `ref/telas/confirmlayout.png`, `confirmfactory.png`, `confirmwall.png`, `updateavail.png`, `updateready.png`, `releasenotes.png`.
**Entrega:**
- Diálogos flutuantes no polegar; "Redefinir launcher" exige segurar; "Restaurar fundo" sem diálogo, com desfazer.
- Atualização disponível e pronta; notas de versão com chips de versão.
**Pronto quando:** gates + visual.

## Fase 7: acabamento

- Todas as strings novas em EN e pt-BR.
- `@AureolePreview` privado em todo componente novo.
- Screenshot tests de cada tela nova (destro e canhoto nas 6 principais, e tema claro nas novas).
- Varredura final: arquivos acima de 500 linhas, `@Suppress`, imports de `feature/` no DS.
**Pronto quando:** os 5 gates passam do zero e o `visual-reviewer` percorre `ref/telas/` inteiro.
