# Ajustes de sheets e Ajustes: plano

**Base:** `main` em `c0b2ee8` (0.9.36.327) · **Spec:** `SPEC.md` · **Protótipo:** `prototipo.html` (11 telas) ·
**Decisões:** `DECISOES.md` (D-01 a D-06 respondidas em 16/11; D-07 com padrão) · **Como rodar:** `EXECUTAR.md`

## Regras de execução (valem para todas as fases)

1. **Por modelo:** primeiro o bloco Sonnet (fases 1 e 2), depois o bloco Opus (fase 3), e por fim a fase 4 (gates).
   Cada fase roda num subagente com o modelo da tabela.
2. **Sem gates no meio.** Nas fases 1 a 3 não rodar `detekt`, `lintDebug`, `testDebugUnitTest` (suíte inteira),
   `jacoco…`, `…ScreenshotTest` nem `assembleDebug`. Dentro da fase vale só compilar (`./gradlew compileDebugKotlin`)
   e rodar os testes novos/alterados da fase com `--tests`, para não seguir com código quebrado.
   Os gates rodam **uma vez**, na fase 4, depois de todas as fases.
3. **Commit no fim de cada fase:** `git add` dos arquivos da fase e
   `git commit -m "feat(ajustes-sheets-settings): <resumo> (fase N)"`.
   **Sem** `Co-Authored-By`, sem "Generated with Claude Code", sem nenhuma linha de atribuição. **Nunca** `git push`.
4. Ler `AGENTS.md` e todos os guias de `agents/` antes da primeira fase (regra do projeto): MAX-500 linhas, sem
   `@Suppress`, `AureoleDS` para cores/dimens/ícones, strings em EN e pt-BR, `@AureolePreview` privado.
5. Ao fim de cada fase, escrever as notas dela em `PROGRESSO.md` (o que mudou, o que ficou faltando) e marcar "Feita".
   Gates e Visual fecham na fase 4; Aparelho é o teste do Mateus.
6. Dúvida de produto nova: escrever em `DECISOES.md` (`## D-07 …`, `opções:`, `trava:`), seguir com a opção mais
   conservadora e citar no relatório final. Não parar a execução.

## Resumo

| # | Fase | Modelo | Agente | Requisitos | Tempo | Tokens |
|---|---|---|---|---|---|---|
| 1 | **Polimento visual:** Personalizar, cabeçalho das sheets, Ajustes (rodapé e paisagem), Avançado, coach (texto e mão), ícones Add New e Home | **Sonnet** | `sheet-ui` | RF-01 a RF-07, RF-17, RF-19, RF-20, RF-21 | 1,5–2,5 h | 100–150k |
| 2 | **Salvar ao fechar + Desfazer** nas sheets de edição; picker de cor do fundo novo | **Sonnet** | `sheet-ui` | RF-11 a RF-14, RF-22 | 2,5–4 h | 160–250k |
| 3 | **Movimento:** sheet sobe ao abrir, sheet elástica, checklist → pílula, lista de apps pela borda ou por baixo | **Opus** | `sheet-gestures` | RF-08 a RF-10, RF-15, RF-16, RF-18 | 2,5–4 h | 160–250k |
| 4 | **Gates, correções e revisão visual** (todas as fases juntas) | **Sonnet** + `visual-reviewer` (**Opus**) | `sheet-chores` | todos | 1–2 h | 60–120k |

**Total:** 7,5–12,5 h de agente e 0,48–0,77 M de tokens. **Ordem:** 1 → 2 → 3 → 4.
A fase 2 vem antes da 3 para o Sonnet fazer os dois blocos seguidos. As duas mexem em partes diferentes das sheets
(a 2 nos editores, `AureoleConfirmOverlay` e `ThemePreviewOverlay`; a 3 no `AureoleEditOverlay` e na física), e os
screenshots só são regenerados uma vez, na fase 4.

### Decisões (16/11)

- **D-01** Título 20 sp e respiro no topo em **todas** as sheets.
- **D-02** Ajustes em paisagem com **dois painéis**.
- **D-03** Criar pasta mantém **só o botão "Criar"** (sem Cancelar).
- **D-04** **Todo fechamento salva** (tocar fora, voltar, arrastar para baixo).
- **D-05** Editar pasta e Apps da pasta **salvam ao fechar** com Desfazer.
- **D-06** Ícone Add New **só no botão de adicionar do container**.
- **D-07** Picker de cor do fundo: tom de cada paleta = **variante** (padrão, se não houver resposta).

---

# Bloco Sonnet

## Fase 1: polimento visual

**Modelo:** Sonnet · **Requisitos:** RF-01, RF-02, RF-03, RF-04, RF-05, RF-06, RF-07, RF-17, RF-19, RF-20, RF-21 · **Depende de:** nada

### Entrega

1. **Personalizar** (`CustomizeSections.kt`): `Spacer(large)` entre `CustomizeAdjustSection` e `CustomizeAddSection`
   (ou `top = large` só no rótulo de "Adicionar à home"; não mexer no `AureoleMenuSectionLabel` compartilhado).
2. **Logo no tile** (`CustomizeItemIcons.kt`): `CustomizeAddItem.SETTINGS -> icons.Logo(...)`. O logo é largo
   (128:73): largura `iconHuge` (40 dp) e altura proporcional, sem distorcer, tint `onSurfaceHigh`.
3. **Cabeçalho das sheets** (`AureoleEditOverlaySheet.kt`, `OverlaySheetHeader`): `titleLarge` + `FontWeight.Bold` e
   `padding(top = small)`, para todas as sheets (D-01). Conferir que o slot do logo no canto (`OverlaySheetSettingsSlot`)
   continua alinhado ao título.
4. **Rodapé dos Ajustes** (`SettingsRootScreen.kt`): dentro do `BoxWithConstraints`, a coluna rolável ganha
   `heightIn(min = maxHeight)`; trocar o `Spacer(bottomSpacerHeight)` por `Spacer(Modifier.weight(1f))` e o padding do
   "Muito obrigado!" por `bottom = xLarge`. Apagar `BOTTOM_SPACER_PERCENT` se ficar sem uso.
5. **Paisagem** (`SettingsRootScreen.kt` e telas irmãs): detectar por `maxWidth > maxHeight` no `BoxWithConstraints`
   (sem `Configuration`). Função pura `settingsLayoutFor(widthDp, heightDp)` (`SINGLE_COLUMN`/`TWO_PANES`) com teste.
   - Raiz (D-02): dois painéis. Esquerda: logo, nome e versão (tocável, abre as notas como hoje). Direita: as opções
     e "Muito obrigado!" a `xLarge` do fundo. Os dois centralizados na vertical, sem rolar em 891 x 411 dp.
   - Avançado, Aparência e Comportamento: coluna centralizada com largura máxima. Token novo
     `settingsContentMaxWidth = 480.dp` em `AureoleDimens.kt`.
6. **Avançado** (`SettingsAdvancedScreen.kt`): `AdvancedItemsGroup` na ordem Informações do app, Novidades, Launcher
   padrão, Redefinir, Desinstalar. Links do rodapé em `bodyMedium` com `heightIn(min = minTouchTarget)` e texto
   centralizado na vertical.
7. **Strings** (`values/strings.xml`, `values-pt-rBR/strings.xml`):
   - `settings_advanced_whats_new` = "What's new" / "Novidades";
   - `settings_advanced_set_default_launcher` = "Default launcher" / "Launcher padrão" (só é usada no Avançado);
   - `home_onboarding_coach_apps_done` = "Your apps live here. Press back to close." / "Seus apps estão aqui. Toque em
     voltar para fechar." (o `OnboardingReducer` já conclui o passo em qualquer fechamento).
8. **Mão do coach na borda** (`GestureCoachHandDrawing.kt`, `OnboardingMotionSpec.kt`): em `drawEdgeScrub`, o puxar
   usa `HAND_EDGE_PULL_TRAVEL = 56.dp` (não mais `EdgeGestureStartWidth`), começa em `x = borda` (dedo meio fora), com
   `FastOutSlowInEasing`; pausa de ~8% do ciclo entre puxar e descer; rastro em dois segmentos (borda → ponto puxado →
   ponto atual). `HAND_EDGE_LOOP_MS` de 1900 para ~2300. A posição sai de uma função pura
   `edgeScrubPoint(phase, …): Offset` com teste.
9. **Ícone Add New**: copiar `specs/ajustes-sheets-settings/assets/ic_aureole_add_new.xml` para
   `app/src/main/res/drawable/ic_aureole_add_new.xml` e criar `AureoleIcons.AddNew` em `AureoleIcons.kt` (vetor de
   traço, aceita `tint`). Trocar **só** no botão de adicionar do container (`ContainerFolderComponents.kt`, D-06).
   `ic_aureole_add` continua nos outros lugares e no `FolderIconRegistry`.
10. **Ícone Home** (RF-21): substituir o drawable de `AureoleIcons.Home` pelo de
    `specs/ajustes-sheets-settings/assets/ic_aureole_home.xml` (vetor de traço, aceita `tint`; hoje é
    `ic_aureole_home.webp` em 5 densidades: apagar os `.webp` e pôr `res/drawable/ic_aureole_home.xml`). Aparece no
    Launcher padrão (`SettingsAdvancedScreen.kt`) e em "Definir como app da home" (`OnboardingChecklistItemComponent.kt`).

### Testes da fase (rodar só estes, com `--tests`)

- `settingsLayoutFor`: retrato, paisagem, quadrado.
- `edgeScrubPoint`: começa na borda; ao fim do puxar está 56 dp para dentro na mesma altura; parado na pausa; termina
  no `endY`; canhoto espelha o x.
- `SettingsAdvancedScreenTest`: ordem dos 5 itens pelo texto; links clicáveis.
- `SettingsRootScreenTest`: "Muito obrigado!" abaixo das opções em retrato e em paisagem.
- `CustomizeBottomSheetTest`/`CustomizeItemsTest`: o tile de Configurações continua abrindo os Ajustes.
- Screenshot tests novos (criar, sem regenerar ainda): Ajustes raiz e Avançado em paisagem (com o ícone Home novo)
  (`device = "spec:width=891dp,height=411dp"`), container com o botão Add New.

**Commit:** `feat(ajustes-sheets-settings): polimento do Personalizar, Ajustes, Avançado e coach (fase 1)`

---

## Fase 2: salvar ao fechar + Desfazer

**Modelo:** Sonnet · **Requisitos:** RF-11, RF-12, RF-13, RF-14, RF-22 · **Depende de:** fase 1

### Entrega

1. **Componente** (`composable/sheets/AureoleAutoSaveOverlay.kt`): mesma prévia e conteúdo do `AureoleConfirmOverlay`,
   sem barra de botões (o `bottomExtra` continua para campo com teclado). Parâmetros em
   `model/AutoSaveOverlayParams.kt`: `title`, `isChanged: () -> Boolean`, `onCommit: () -> Unit`, `onDismissRequest`,
   `isLeftHanded`, `onSettingsClick`, `hazeState`. Qualquer fechamento (tocar fora, voltar, arrastar, D-04) chama
   `onCommit` uma vez se `isChanged()`, depois `onDismissRequest`. Função pura `closeOutcome(initial, current)`
   (`Commit`/`NoChange`) com teste. Se o `AureoleEditOverlay` não expõe um ponto único de fechamento, criar um
   (`onClosing`) sem mexer na animação (a fase 3 mexe nela).
2. **`ThemePreviewOverlay.kt`** (Paleta e Fonte): tirar o `AureoleActionRow`; commit ao fechar se a escolha difere da atual.
3. **Desfazer:** quem abre a sheet guarda o valor anterior e chama o `showUndo` da sua tela (`HomeViewModelUndoExt.kt`
   na Home, `SettingsViewModelUndoExt.kt` nos Ajustes). Strings novas (EN/pt-BR):
   `undo_background_changed` "Background changed"/"Fundo alterado", `undo_palette_changed` "Palette changed"/"Paleta
   alterada", `undo_font_changed` "Font changed"/"Fonte alterada", `undo_color_changed` "Color changed"/"Cor alterada",
   `undo_quick_button_changed` "Quick button updated"/"Botão rápido atualizado", `undo_cards_glass_changed` "Cards and
   glass updated"/"Cartões e vidro atualizados", `undo_folder_changed` "Folder updated"/"Pasta atualizada".
4. **Telas:**
   - `SetBackgroundBottomSheet` (`SettingsAppearanceDialogs.kt`): cor sólida salva ao fechar; o cartão "Imagem
     personalizada" abre o seletor de fotos na hora; o desfazer da imagem usa o `wallpaperStash`.
   - `ColorPickerDialog.kt`, `QuickButtonAppearanceBottomSheet.kt`, `CardsGlassBottomSheet.kt` (sai o "Pronto").
   - `FolderFormDialog.kt`: modo edição salva ao fechar; modo criar mantém **só** o botão "Criar" (D-03), sem Cancelar.
   - `FolderAppPickerDialog.kt`: salva ao fechar com Desfazer (D-05).
5. **Fica com botão:** `AureoleConfirmDialog` (Redefinir, Desinstalar, com segurar) e Criar pasta.
6. **Picker de cor do fundo** (RF-22, `SettingsAppearanceDialogs.kt`, `BackgroundTypeCards.kt`, `composable/color/`):
   - Dados puros em `composable/color/model/BackgroundSwatches.kt`: `themeSwatches(colors: AureoleColors)` (4, com
     rótulo), `paletteSwatches(category, tone)` a partir de `allPalettes` (sem o Frostbite repetido; categoria por
     paleta: criar `PaletteCategory { COLD, COOL, NEUTRAL, WARM }` e marcar cada preset em `ThemePalettePresets.kt`,
     que hoje só tem comentários de seção), `toneRamp(hue)` = 7 tons com saturação 0,32 e luminosidade
     0,08 / 0,14 / 0,22 / 0,32 / 0,45 / 0,62 / 0,80, e `isLightBackground(color)` (luminância ≥ 0,5, a mesma regra do
     `buildSeedAureoleColors`). Testes para as quatro.
   - UI: seções "Do seu tema", "Paletas" (chips de categoria + `LazyRow`), "Tons" (+ `AureoleHueSlider` que move a
     rampa), "Vivas" (`RecommendedColors`). Rótulos de seção com `SheetSectionLabel`. Ao tocar numa paleta, o nome
     aparece numa linha abaixo das bolinhas (não em tooltip). Aviso de tema claro com `onSurfaceLow`.
   - A categoria aberta ao abrir a sheet é a da cor atual, se ela for o tom de alguma paleta; senão, Frias.
   - Strings (EN/pt-BR): seções, categorias, rótulos do tema (Background/Fundo, Surface/Superfície, Variant/Variante,
     Outline/Contorno) e o aviso "This color makes a light theme" / "Essa cor gera um tema claro".
   - Continua salvando ao fechar (item 4) e passando pelo `onSelectManualSeedColor`.
7. **Limpeza:** tirar strings órfãs (`settings_appearance_apply`, `settings_cards_glass_done`, `core_common_confirm`,
   `home_folder_picker_save_count`, se ninguém mais usar). `AureoleConfirmOverlay` fica para os casos do item 5.

### Testes da fase

- `closeOutcome`/`AureoleAutoSaveOverlay`: mudou → um commit; não mudou → nada; fechar duas vezes → um commit.
- Por tela: trocar valor + fechar chama o salvar e oferece desfazer; desfazer devolve o anterior
  (`ThemePreviewStateTest`, `ColorPickerDialogTest`, `QuickButtonAppearanceBottomSheetTest`, `CardsGlassModelsTest`,
  `UndoControllerTest`, testes de pasta).
- Fundo: desfazer de cor sólida volta a cor; desfazer de imagem usa o `wallpaperStash` e expirar descarta.
- `BackgroundSwatches`: 4 tons do tema; 100 paletas somadas nas 4 categorias; rampa com 7 tons crescentes na mesma
  matiz; `isLightBackground` (branco sim, `#12181D` não).

**Commit:** `feat(ajustes-sheets-settings): sheets de edição salvam ao fechar com desfazer e picker de fundo novo (fase 2)`

---

# Bloco Opus

## Fase 3: movimento

**Modelo:** Opus · **Requisitos:** RF-08, RF-09, RF-10, RF-15, RF-16, RF-18 · **Depende de:** fase 2

### Entrega

1. **Saber se há conteúdo abaixo.** No `OverlaySheetBody`, medir a altura que o conteúdo pediria sem limite por
   `maxIntrinsicHeight` (funciona com `verticalScroll`; `LazyColumn` não tem intrínseca: cair no comportamento de
   hoje). Guardar em `OverlaySheetMotion` como `contentFullHeight` (px, nulo = desconhecido).
2. **Alvo de expansão** (`SheetDetents.kt`): `expandTarget(rest, expanded, contentFull): Float?` = `null` quando o
   conteúdo cabe no repouso (folga de 8 dp), senão `min(expanded, contentFull)`. `resolveDetent` recebe `canExpand` e
   nunca devolve `Expanded` quando é falso.
3. **Elástico** (`OverlaySheetMotion.kt`): `rubberBand(overshootPx, limitPx)` =
   `limit * (1 - 1 / (overshoot / limit * 0,55 + 1))` (limite ≈ 64 dp, constante de animação). `dragBy` usa o elástico
   acima do repouso quando não pode expandir. Volta com `Spring.DampingRatioMediumBouncy` + `StiffnessMedium`; o caminho
   que expande segue com a mola sem quique de hoje.
4. **Controlador** (`OverlaySheetController`): `toggle()` sem poder expandir faz um quique de 24 dp e volta; fling
   para cima idem. Sem `onTick` no elástico; `crossedDetent` não conta o topo elástico.
5. **Prévia e acessibilidade:** `expandFractionOf` fica em 0 no elástico; `sheetHandleActions` só oferece "Expandir"
   quando `canExpand`.
6. **Sheet sobe ao abrir** (`AureoleEditOverlay.kt`, `AureoleEditOverlaySheet.kt`): o `AnimatedVisibility` de fora
   deixa de fazer fade na sheet. Fundo escuro: `fadeIn`/`fadeOut`. Sheet:
   `animateEnterExit(slideInVertically { it }, slideOutVertically { it })` no `SheetSurface`, entrada com
   `spring(DampingRatioNoBouncy, StiffnessMediumLow)` e saída ~250 ms. Prévia: sem `scaleIn`, acompanha o topo da
   sheet + `fadeIn`.
   - Fechar arrastando: a saída parte da `translation` atual, sem pular de volta ao repouso.
   - Troca Personalizar ↔ Paleta/Fonte (`CustomizeBottomSheet.kt`): o fundo não pisca; uma desce e a outra sobe.
   - Teclado aberto (Editar pasta): sobe junto do `imePadding`, sem salto.
   - `effectiveAnimated = false` (animações desligadas, inspeção): aparece direto.
   - O commit ao fechar da fase 2 continua disparando uma vez só.
7. **Checklist → pílula** (`OnboardingHostComponent.kt`, `OnboardingChecklistComponent.kt`,
   `OnboardingChecklistPillComponent.kt`): `SharedTransitionLayout` envolvendo cartão e pílula, alternados por
   `isChecklistExpanded`.
   - Contorno: `sharedBounds("onboarding-checklist")` no `OnboardingGlassCard` dos dois, `RemeasureToBounds`, clipe
     morfando de `radiusLarge` para `radiusFull`.
   - Halo: `sharedElement("onboarding-halo")` no `AureoleHaloProgress` do cabeçalho e da pílula.
   - Texto: `sharedBounds("onboarding-progress-text")` cruzando "0 de 9 passos" e o texto da pílula.
   - Lista, título e rodapé: `fadeOut` ~120 ms antes de encolher; na volta, o escalonamento atual (`CHECKLIST_STAGGER_MS`).
   - Fundo escuro esmaece junto. Mola `spring(PILL_DAMPING, CHECKLIST_STIFFNESS)`.
   - O vidro (Haze) não pode piscar; se o `hazeEffect` não acompanhar o `sharedBounds`, desenhar o vidro na camada
     de fora e animar só o conteúdo.
   - Animações desligadas: troca direta. Não mudar `isOtherOverlayActive`, o espiar a home e o `OnboardingCardEntrance`.
8. **Lista de apps pela origem** (`MainUiState.kt`, `HomeViewModelUiExt.kt`, `HomeOverlays.kt`, `HomeDragGestures.kt`,
   `MainActivity.kt`): `isAllAppsOpenedFromBottom` vira `allAppsOpenSource: AllAppsOpenSource` (`BOTTOM`, `EDGE`),
   padrão `BOTTOM`. `setAllAppsDrawerOpen(open, source = BOTTOM)`; só o scrub da borda passa `EDGE` (`runEdgeScrub`,
   `onInteractionStarted`/`onLetterSelected` do `ScrubberOverlay`). Sai o `fromHomeButton`.
   - `BOTTOM`: `slideInVertically { it }` + `fadeIn`; saída `slideOutVertically { it }` + `fadeOut`.
   - `EDGE`: `slideInHorizontally { ±it }` da borda do polegar (largura inteira) + `fadeIn`; sai pela mesma borda.
   - A origem fica guardada até a lista fechar. Função pura `drawerMotionFor(source, isLeftHanded)` com teste.

### Testes da fase

- `SheetDetentsTest`: `expandTarget` (cabe, cabe com folga, um pouco maior, muito maior, desconhecido); `resolveDetent`
  com `canExpand = false`.
- `OverlaySheetMotionTest`: `rubberBand` monotônica, abaixo do limite, 0 em 0; `dragBy` não passa de repouso + limite.
- `OverlaySheetGestureZoneTest`/`AureoleEditOverlaySheetTest`: sem "Expandir" quando não pode; toque duplo não muda
  `isExpanded`; abertura com `mainClock` manual (primeiro quadro abaixo da tela, último no repouso).
- Onboarding: minimizar e reabrir com `mainClock` manual: um só halo na árvore durante a troca; fim com a pílula.
- `drawerMotionFor` e `HomeViewModel`: deslizar para cima, botão rápido e Início abrem com `BOTTOM`; scrub com `EDGE`.

**Commit:** `feat(ajustes-sheets-settings): sheet sobe e estica, checklist vira pílula, lista pela borda (fase 3)`

---

# Final

## Fase 4: gates, correções e revisão visual

**Modelo:** Sonnet (correções) + `visual-reviewer` Opus · **Depende de:** fases 1, 2 e 3

1. Rodar os gates **um por chamada, com o nome por extenso, em segundo plano**, nesta ordem (regras do `AGENTS.md`):
   `./gradlew detekt --auto-correct` → `./gradlew lintDebug` → `./gradlew testDebugUnitTest` →
   `./gradlew jacocoTestCoverageVerification` → `./gradlew updateDebugScreenshotTest` (regenera as referências:
   cabeçalho de todas as sheets, Personalizar, Ajustes, Avançado, container, sheets sem botões) →
   `./gradlew validateDebugScreenshotTest` → `./gradlew assembleDebug`.
2. Falhou: corrigir na causa (sem `@Suppress`, baseline ou regra afrouxada), rodar de novo o gate que falhou e os
   seguintes. Nunca editar com build rodando.
3. `visual-reviewer` compara os screenshots regenerados com as telas 1, 3, 4, 5, 6, 11 e 12 do protótipo e dá
   **igual**/**quase**/**diferente** por tela; ajustar o que for **diferente** e repetir os gates afetados.
4. **Commit:** `chore(ajustes-sheets-settings): gates e screenshots (fase 4)`. Sem atribuição. Sem push.
5. Atualizar `PROGRESSO.md` (Gates e Visual) e entregar o relatório final (formato em `EXECUTAR.md`).

## Checklist do aparelho (Mateus, depois da fase 4)

- Personalizar: espaço maior, título maior, logo no tile Configurações.
- Ajustes: "Muito obrigado!" preso embaixo; girar a tela na raiz (dois painéis) e no Avançado; fonte do sistema no
  máximo sem sobrepor o rodapé. Avançado na ordem nova, links maiores, "Novidades" e "Launcher padrão" com o ícone de casa novo (também no checklist).
- Coach: texto "Toque em voltar para fechar"; a mão da borda entra visivelmente antes de descer (destro e canhoto).
- Container: botão de adicionar com o ícone Add New.
- Picker de cor do fundo: tons do tema, paletas por grupo, tons + matiz, vivas; aviso de tema claro.
- Fundo, Paleta, Fonte, Cor, Botão rápido, Cartões, Editar pasta, Apps da pasta: mudar e fechar por toque fora, voltar
  e arrastar; Desfazer em cada um; abrir e fechar sem mudar não mostra snackbar. Criar pasta só com "Criar".
- Sheets sobem de baixo e descem ao fechar; elástico no Personalizar; expandir só até o conteúdo; nada quebrou em
  Relógio (abas), seletor de apps, Pilha de widgets, pasta com teclado.
- Checklist minimiza para a pílula e volta (vidro ligado e desligado; animações 0,5x e desligadas).
- Lista de apps: pela borda direita/esquerda vem da borda; deslizar para cima, Início e botão rápido vêm de baixo.
