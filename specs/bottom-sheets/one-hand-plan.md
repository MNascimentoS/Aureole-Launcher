# Bottom sheets para uma mão: plano de implementação

**Base:** `release/0.9.13`
**Protótipo:** `specs/bottom-sheets/one-hand-prototype.html`
**Referências visuais:** `specs/bottom-sheets/ref/`

Rodar cada fase com `/fase <n>`. O comando mostra o plano, espera o ok, delega ao subagente certo, roda os gates e pede a revisão visual. Commit e push ficam com o Mateus.

## Fases e modelos

| # | Fase | Subagente (modelo) | Tokens (estimativa) |
|---|---|---|---|
| 1 | Base da sheet: alturas 60/90, alça (toque duplo e segurar), vibração, lado da mão | `sheet-gestures` (Opus) | 150–250k |
| 2 | Desfazer por 4 s no lugar de confirmação | `sheet-ui` (Sonnet) | 80–120k |
| 3 | Busca grudada no teclado, melhor resultado colado no campo | `sheet-ui` (Sonnet) | 120–180k |
| 4 | Seletor de apps: arrastar para marcar e faixa A–Z com balão | `sheet-gestures` (Opus) | 150–220k |
| 5 | Relógio: abas que trocam deslizando | `sheet-ui` (Sonnet) | 60–90k |
| 6 | Paleta e Fonte com mini-home e efeito na hora | `sheet-ui` (Sonnet) | 120–160k |
| 7 | Strings, previews e testes de screenshot que faltarem | `sheet-chores` (Haiku) | 40–80k |
| 8 | Fechamento: divergências da revisão visual, canhoto, `EditWidgetGroupScreen` e checklist no aparelho | `sheet-ui` (Sonnet) | 120–180k |
| — | Revisão visual ao fim de cada fase | `visual-reviewer` (Opus) | 30–50k por fase |

Diferenças aceitas em relação ao protótipo:
- fonte do sistema no lugar da fonte do navegador;
- conteúdo de exemplo;
- o selo TIC, a zona do polegar e a linha de alcance, que só existem no protótipo para explicar.

---

## Fase 1: base da sheet

**Escopo:** só `AureoleEditOverlay` em `core/designsystem/`. Ele é usado por:
- `EditClockBottomSheet`, `EditContainerScreen`, `EditWidgetGroupScreen`;
- as 3 sheets de `quickbutton/`;
- `FavoriteAppsDialog`.

As sheets em `ModalBottomSheet` (`SettingsBottomSheet`) ficam fora desta fase.

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
5. **Toque duplo na alça** alterna entre 60% e 90%, com tic.
6. **Segurar a alça** (cerca de 600 ms, sem mover mais que o touch slop) chama `onSettingsClick`, com `HapticFeedbackType.LongPress`. O logo no header continua igual.
7. **Prévia ancorada:** o `previewContent` acompanha o topo real da sheet (`top = sheetTop - medium - alturaDaPrévia`), e não mais uma altura fixa.
8. **Lado da mão:** a config recebe o lado do polegar. Nesta fase ele só define o lado do rótulo de acessibilidade e fica pronto para as próximas. O DS não importa nada de `feature/`; quem chama passa `isLeftHandedMode`.
9. **Acessibilidade:** a alça tem `contentDescription` e `customActions`: "Expandir" ou "Recolher", e "Ajustes do Aureole".

### Mudanças sugeridas

**Modelo e lógica** (`core/designsystem/model/`):
- Em `AureoleEditOverlayConfig`: trocar `maxSheetHeightFraction` por `restHeightFraction = 0.6f` e `expandedHeightFraction = 0.9f`. Adicionar `isLeftHanded: Boolean = false`.
- Hoje os callers passam 0.3, 0.4, 0.45, 0.65 e 0.8. Revisar cada um: a sheet agora abraça o conteúdo, então a maioria pode só remover o parâmetro.
- Novo `SheetDetents.kt` com funções puras e testes unitários. Exemplos:
  - `resolveDetent(height, rest, expanded, velocity): SheetDetentTarget`, que devolve `Rest`, `Expanded` ou `Dismiss`;
  - `crossedDetent(previous, current, detents): Boolean`.

**Componentes** (`core/designsystem/components/`):
- Novo `OverlaySheetHandle.kt`: `pointerInput` com `detectTapGestures(onDoubleTap, onLongPress)`, mais o drag.
- `AureoleEditOverlaySheetSurface.kt`: altura com `heightIn(max = rest)` em repouso e `height(expanded)` expandida, animada.
- `AureoleEditOverlaySheet.kt`: `PreviewSlot` ancorado ao topo medido da sheet, via `onGloballyPositioned` ou um layout próprio.

**Strings** (EN e pt-BR):
- `overlay_sheet_handle_description`
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

## Fases 2 a 7

O detalhe de cada uma é escrito ao fim da fase anterior, a partir do que a fase 1 deixar pronto. Resumo do que cada uma entrega, conforme o protótipo:

2. `AureoleUndoSnackbar` em `composable/`, com o botão "Desfazer" no lado do polegar e duração de 4 s. Um controlador puro com teste. Usado em: remover app da pasta, favoritar e trocar atalho. Segurar 1 s fica só para desinstalar.
3. `imePadding()` no rodapé; o campo, os escolhidos e o Salvar grudam no teclado. Lista ou grade com `reverseLayout = true` e ranqueamento em função pura (começa com o termo primeiro). ↵ escolhe o melhor. Vale em `ShortcutSelectionBottomSheet` e `FolderAppPickerDialog`.
4. `detectDragGesturesAfterLongPress` com hit-test na grade para marcar vários apps. Faixa A–Z na borda do polegar, com balão e tic por letra.
5. `HorizontalPager` no conteúdo de `EditClockBottomSheet`, com as abas no rodapé sincronizadas.
6. Mini-home ancorada e tema local aplicado na hora. Cancelar, arrastar ou tocar fora voltam ao anterior. Paletas em grade de 3 colunas e fontes em grade de 2, cada uma escrita na própria fonte.
7. O que faltar de strings, previews e screenshots.


## Fase 8: fechamento

**Modelo recomendado:** Sonnet 5.5 (`sheet-ui`) para o código. A revisão visual segue com `visual-reviewer` (Opus). O checklist no aparelho é feito por você.

**Origem:** revisão visual das fases 5, 6 e 7 (veredito "quase"), comparada com o CSS do protótipo, já que `ref/` só tem `fase1-*`, mais as pendências deixadas nas fases anteriores.

### Comportamento esperado

1. **Pager do relógio:** a altura acompanha a aba ativa. Hoje segue a página mais alta e sobra vão na aba Estilo (`EditClockBottomSheet`, `EditClockPager`).
2. **Linha de ações:** Restaurar/Cancelar contornado (flex 1) e Salvar/Aplicar principal (flex 1,4), 48 dp, largura toda. Vale para relógio, paleta e fonte (`ThemePreviewOverlay.kt`, `EditClockBottomSheet`).
3. **Relógio expandido (90%):** a prévia sai da tela ou fica acima da sheet. Hoje fica por baixo, só com a data esmaecida (`AureoleEditOverlaySheet.kt`).
4. **Logo do Aureole no header** de Paleta e Fonte, ligando `onSettingsClick` em `ThemePreviewOverlay.kt`.
5. **Mini-home:** proporção 300×168 (cerca de 22% da tela) e faixa de dock com 4 ícones e borda no topo (`AureoleMiniHome.kt`).
6. **Chip de fonte no canhoto:** espelhado (`row-reverse`, texto à direita). `ThemePickerChips.kt` passa a receber o lado da mão, com variante canhota no screenshot.
7. **Onboarding do seletor:** hierarquia dos botões corrigida; "Apply" principal e "Got it" secundário.
8. **Canhoto das listas:** linhas espelhadas no modo canhoto, como no protótipo.
9. **Letra do `CurvedAlphabetScrubber`:** letra única na posição certa.
10. **Bandeja canhota:** começa pela direita, como no protótipo. Não mudar.
11. **`EditWidgetGroupScreen`:** confirmar se `onSettingsClick` é necessário e ligar se for.
12. **Acabamentos menores:** rótulos de seção pequenos em caixa-alta com espaçamento e cor `low`; nome de cada paleta na cor de destaque dela; avaliar os 3 cartões de estilo e as bolinhas de 40 dp na aba Cores.

### Pronto quando

- [ ] Itens 1 a 7 corrigidos e baselines regravadas com `updateDebugScreenshotTest`.
- [ ] Canhoto das listas e letra do scrubber corrigidos.
- [ ] `EditWidgetGroupScreen` resolvido (ligado ou descartado com motivo).
- [ ] Nova revisão visual das fases 5, 6 e 7 sem divergência de layout.
- [ ] Checklist no aparelho sem bloqueio, nas duas mãos: gestos da fase 1, desfazer, busca no teclado, abas do relógio, mini-home e tema na hora.
- [ ] Gates finais verdes: detekt, testDebugUnitTest, jacocoTestCoverageVerification, validateDebugScreenshotTest, assembleDebug.
