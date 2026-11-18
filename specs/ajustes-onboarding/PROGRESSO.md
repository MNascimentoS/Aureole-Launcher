# Progresso: ajustes-onboarding

Gates e revisão visual: na fase G do lote (`../EXECUTAR-LOTE-17-11.md`).

| # | Fase | Modelo | Estimativa | Feita |
|---|---|---|---|---|
| 1 | Paleta/fonte, relógio e container | Sonnet | 1–1,5 h | [x] |
| 2 | Vitrine, espiada e shared element | Opus | 2,5–3,5 h | [x] |
| 3 | Ajustar layout: pílula Salvar | Sonnet | 1–1,5 h | [x] |

## Notas por fase

**Fase 1:** fechar o seletor sem Aplicar chama `dismissAppearancePicker(hadPreview)`: com toque fica a prévia (o sheet já grava
quando difere do atual), sem toque aplica Frostbite / Istok Web; nos dois casos confirma a tarefa. `ThemePickerSheetOptions.onPreview`
avisa o toque. Relógio do onboarding em retrato = 20×6 do layout padrão (`CLOCK_COLS/ROWS` removidos). Container do checklist salvo com
`onboardingContainerModel` (sem fundo, sem rótulos, pastas em grade). Testes JVM: `OnboardingDismissDefaultTest`, `OnboardingItemPlacementTest`,
`OnboardingAppearancePickerComponentTest`, `OnboardingActionsFactoryTest`.

**Fase 2** (compile + testes da fase verdes; sem gates):
- RF-01 a RF-04: `CustomizeBottomSheet(isShowcase)`, ligado por `MainUiState.isCustomizeShowcase` (GESTURES + `succeededStep == CUSTOMIZE`, lido uma vez ao abrir). `AureoleEditOverlayConfig.isLocked` (voltar, arrastar, tocar fora, zona de gesto e logo não fazem nada; sem nestedScroll/drag) e `headerAccessory` (slot da logo no título, sem clique: é o "parâmetro de anel ~30 dp" que a polimento-ui deixou pendente). Anel de 28 dp (`iconXLargeMedium`) enche linear em 3 s por relógio de frames (`customizeShowcaseProgress`), então com animações desligadas fecha no mesmo tempo; tiles bloqueados (`blockingTouches` + callbacks vazios). Ao fechar, o reducer de hoje limpa `succeededStep` e segue. `AureoleLogoRing(isProgressAnimated = false)` para o progresso linear.
- RF-13/14: `ONBOARDING_HOME_PEEK_MS` = 1400. `ChecklistMorph.PEEK` (`isPeekBubbleShown`): `OnboardingPeekBubbleComponent` (vidro 56 dp, logo + anel 0→1 linear na espiada; sem animações fica cheio e parado), sem clique. Ao fim da espiada vira o cartão (ou a pílula) pelo `OnboardingChecklistMorphComponent` estendido: `sharedBounds` do cartão e `sharedElement` do halo, `tween(550, FastOutSlowIn)`, linhas com fade a partir de 250 ms (`ChecklistMorphTarget.cameFromBubble`/`usesBubbleMotion`). Contorno suave do item (`peekItemId`, via `taskItemId`; `onboardingPeekOutline` no grid) some com a espiada; na falha não há contorno.
- RF-15: screenshots da bolha 0/50/100% em `OnboardingScreenshotTest` (não rodados, fase G). Testes: `CustomizeShowcaseTest` (reducer: abrir → feito, fechar → checklist, voltar → nada), `CustomizeBottomSheetTest` (vitrine sem resposta e fecha em 3 s), `OnboardingHomePeekTest`, `ChecklistMorphTest`, `OnboardingChecklistMorphComponentTest` (bolha sem nó clicável, abre a lista).

**Fase 3** (compile + testes da fase verdes; sem gates):
- RF-16 a RF-20: `OnboardingEditHintComponent(config: EditHintConfig, onSave)` embaixo (acima da barra de navegação), vira pílula "✓ Salvar" (48 dp, sem cartão de vidro) após 3 s (`EDIT_HINT_TEXT_MS`) ou no 1º gesto; o texto não volta (estado lembrado fora do `isVisible`). Gesto = `GridGestureTracker` (contador em `grid/model`, `LocalGridGestureTracker` provido no `MainScaffold`, cada célula do `DynamicGridContainer` avisa início/fim de arraste ou resize): pílula a 25% e sem `clickable` durante o gesto. Borda por função pura `editHintEdge(grid, bandRows)` (faixa com menos linhas cobertas; empate = embaixo) + `editHintBandRows`; paisagem usa o canto do polegar (direita; esquerda no canhoto). TalkBack: `stateDescription` da pílula = texto de ajuda. Testes: `OnboardingEditHintExtTest`, `GridGestureTrackerTest`, `OnboardingEditHintComponentTest` (3 s e gesto).

## Ficou faltando
- Fase 3: screenshot da pílula (dobrada, 25%) e revisão visual: fase G. A sobreposição conta só linhas (ignora colunas) e a faixa de baixo estima a altura da pílula + barra de navegação.


- Fase 2: na bolha → cartão o `sharedElement` do halo mostra o anel do cartão (progresso das tarefas) viajando, não a logo sozinha com o anel sumindo; e a logo vai para o halo do cabeçalho (início do título), não para o canto direito como no protótipo. Ajustar na revisão visual (fase G) se incomodar.
- Fase 2: screenshot da vitrine do Personalizar e revisão visual da bolha/transform: fase G.

- RF-12: screenshot test do container (4 pastas) não feito (sem gate de screenshot nesta fase; fica para a revisão visual do lote).

## Revisão visual 17/11 (fase G, 2ª passada)
- RF-11/RF-12: o print do container com fundo, rótulos e pasta como ícone era **artefato do teste** (passava `showFolderLabels`/`isBackgroundEnabled = true` à mão). O código (`onboardingContainerModel`) já estava certo; o `ContainerFourFoldersScreenshotTest` agora monta a config pelo `onboardingContainerModel` + `containerConfigFor`, com apps dentro das pastas (mini-grade).
- RF-17..RF-19: novos screenshots da pílula só-pílula, a ~25% durante o gesto e no topo (`OnboardingEditHintStatesScreenshotTest`; `EditHintConfig.startsFolded` abre já dobrada).
- RF-01/RF-02: screenshot do Personalizar em vitrine com o anel na logo (`CustomizeSheetShowcase`).

### Ficou faltando
- O screenshot da vitrine mostra o anel a 0% (o relógio de frames não avança no layoutlib); um quadro a 50% exigiria expor o progresso como parâmetro.
- Bolha → cartão (logo indo para o canto direito, como no protótipo): segue como anotado na fase 2.
