# Plano de implementação: Primeira Órbita (FUE)

Spec: [`README.md`](README.md). Cada tarefa abaixo é um commit (ou PR pequeno) que **compila, passa nos testes e não muda nenhuma referência de screenshot existente** até a FUE-12, quando o onboarding passa a aparecer de fato.

## Regras que valem para todas as tarefas

Lidas de `AGENTS.md`, `agents/*.md`, `ARCHITECTURE.md` e `.github/copilot-instructions.md`:

- Nenhum arquivo de produção acima de **500 linhas**; testes até **800**. Se passar, parar e extrair.
- **Proibido `@Suppress`** (incluindo `LongMethod`, `ComplexMethod`, `LongParameterList`) e proibido mexer no `detekt.yml`. Limites do Detekt: `LongMethod` 60, `CyclomaticComplexMethod` 15, `NestedBlockDepth` 5, linha 120. Máximo de 5 níveis de indentação; usar guard clauses.
- Arquivos `*Screen.kt`, `*Component.kt`, `*Dialog.kt`, `*Sheet.kt`, `*Popup.kt` só contêm `@Composable`. `data class`, `enum`, `Params`, `Actions` vão para `model/`.
- Preview só com `@AureolePreview` em `private fun`. Todo componente novo tem `@PreviewTest` em `app/src/screenshotTest`.
- Helpers que só leem recursos/tema (`stringResource`, tokens) levam `@ReadOnlyComposable`.
- Sem strings hardcoded; toda string nova em `values/strings.xml` **e** `values-pt-rBR/strings.xml`, chave `home_onboarding_*`.
- Sem `dp`/`sp`/hex soltos na UI: `AureoleDS.colors`, `AureoleDS.dimens`, `AureoleDS.typography`, `AureoleDS.icons`. `dp` hardcoded só em animação/offset/restrição de layout, como constante nomeada. Nunca usar token de layout para blur, física ou animação.
- Componentes em `core/designsystem/components` não importam `feature/*`, têm prefixo `Aureole`, KDoc (regra `UndocumentedPublicFunction`) e `modifier: Modifier = Modifier` como 1º opcional.
- ViewModel sem `Context`/`View`/`Activity`; fala só com repositórios.
- Testes de `HomeViewModel` com `UnconfinedTestDispatcher` cancelam e aguardam `viewModelScope` antes de `Dispatchers.resetMain()` (padrão do `HomeViewModelReleaseNotesExtTest`). Nada pode encerrar a JVM de teste (`terminateProcess` substituído).
- Se `validateDebugScreenshotTest` falhar por diferença visual: **parar**, avisar e perguntar antes de rodar `updateDebugScreenshotTest`; as novas imagens precisam de revisão manual antes do commit.

**Gate rápido** (fim de cada tarefa): `./gradlew detekt --auto-correct` e `./gradlew testDebugUnitTest`.
**Gate completo** (fim de cada marco): os 5 comandos do `AGENTS.md`, incluindo `jacocoTestCoverageVerification` (linhas ≥ 85%, instruções ≥ 70%, branches de lógica ≥ 60%), `validateDebugScreenshotTest` e `assembleDebug`.

## Marcos

| Marco | Tarefas | Entrega | Visível ao usuário? |
|---|---|---|---|
| M1 Fundação | FUE-01 a FUE-05 | estado, persistência, lógica pura, correções de release notes e update | Só a correção de P2 (release notes não abrem na 1ª instalação) |
| M2 Componentes | FUE-06 a FUE-11 | DS, strings e todas as telas com previews e screenshots | Não (componentes ainda não plugados) |
| M3 Integração | FUE-12 a FUE-14 | host no `MainScaffold`, Settings, release notes, QA | Sim |
| M4 Ajustes (28/10) | FUE-15 a FUE-24 | home vazia, layout padrão v3, concluir ao salvar, widgets, Settings e celebração | Sim |

---

## M1 Fundação

### FUE-01 Persistência do onboarding ✅ escrita
**Arquivos**
- `core/data/model/OnboardingProgress.kt`: enums `OnboardingStage`, `GestureCoachStep`, `OnboardingTask` e `data class OnboardingProgress` (o que é persistido). Fica em `core` para o repositório não importar `feature/`.
- `core/data/repository/OnboardingPreferences.kt`: recebe `prefsProvider: () -> SharedPreferences`; `read(): OnboardingProgress?`, `write(progress)`, `restart()`. Parse tolerante (estágio desconhecido → `COMPLETED`; nomes desconhecidos nos sets são ignorados).
- `SettingsRepository.kt`: `val onboarding: OnboardingPreferences by lazy { OnboardingPreferences { prefs } }` (+2 linhas).
- `feature/home/components/onboarding/model/OnboardingUiState.kt`: `AppearancePicker`, `OnboardingUiState`, `isBlocking`, `blocksHome`, `taskProgress`, `visibleGestureSteps(...)`, mappers `toProgress()` / `toUiState(isDefaultLauncher)`.

**Testes** `OnboardingPreferencesTest` (Robolectric): vazio → `null`; ida e volta; sets vazios; estágio inválido → `COMPLETED`; nomes inválidos ignorados; `restart()`; `performFactoryReset()` (com `terminateProcess` substituído) limpa o progresso. `OnboardingUiStateTest` (JUnit puro): padrões, `isBlocking`, `blocksHome` em cada estágio, `taskProgress`, `visibleGestureSteps` e mappers.

**Pronto quando** gate rápido verde. **Commit** `feat(onboarding): add onboarding progress models and preferences`

### FUE-02 Reducer puro
**Arquivo** `feature/home/components/onboarding/extensions/OnboardingReducer.kt` (`internal object`), dividido em funções pequenas para ficar abaixo de 60 linhas e complexidade 15 cada:
- `reduce(previous: MainUiState, next: MainUiState): MainUiState`: retorna `next` sem cópia se o estágio não for `GESTURES` nem `CHECKLIST`.
- `reduceGestures(...)`: transições gaveta e Personalizar `false → true` concluem o passo correspondente; superfície fechando com `succeededStep != null` avança; todos os visíveis feitos → `CHECKLIST`.
- `deriveCompletedTasks(state)`: favoritos, containers com itens ou pastas, widgets válidos, `isDefaultLauncher`. **Substituído na FUE-17:** só `isDefaultLauncher` continua derivado; as tarefas de grid concluem ao salvar (ver M4).
- `reduceChecklist(...)`: une tarefas derivadas às já concluídas (sticky); tarefa nova concluída → `isChecklistExpanded = true` (a lista reabre para mostrar o check); todas concluídas → `COMPLETED` + `isCelebrating = true`.
- `applySignal(state, signal)` para sinais explícitos (`ALPHABET_SCRUBBED`, `PICKER_CONFIRMED`, `SETTINGS_OPENED`, `DEFAULT_HOME_RESULT`).

**Testes** `OnboardingReducerTest` (JUnit puro, sem Robolectric), um teste por branch: identidade em `COMPLETED`/`WELCOME`/`DEFAULT_HOME`; cada passo concluindo fora de ordem; avanço só quando a superfície fecha; omissão do passo alfabético; cada tarefa derivada; sticky (favorito removido não desmarca); conclusão total → celebração; sinal ignorado em estágio errado. Meta: 100% de branches neste arquivo (ele sustenta o mínimo de 60% de lógica do projeto).

**Commit** `feat(onboarding): add pure onboarding reducer`

### FUE-03 Estado no Home
**Arquivos**
- `MainUiState.kt`: `val onboarding: OnboardingUiState = OnboardingUiState()`; em `isOverlayActive`, um `val isOnboardingActive = onboarding.blocksHome` somado à expressão final (sem aumentar a complexidade da getter).
- `HomeViewModel.kt`: `updateUiState` passa pelo `OnboardingReducer.reduce` e persiste a diferença **fora** do lambda do `update` (ver spec §10.4).
- `extensions/HomeViewModelOnboardingExt.kt`: `syncOnboarding()` (lê `checkDefaultLauncher()` do repositório; resolve `null` → `WELCOME` se `lastSeenVersionCode == 0`, senão `COMPLETED`; carrega o persistido; pula `DEFAULT_HOME` se já for padrão), `startOnboarding()`, `skipIntro()`, `postponeDefaultHome()`, `refreshDefaultLauncherStatus()`, `skipGestures()`, `showMeGesture()`, `onOnboardingSignal(signal)`, `onTaskClick` (recolhe a lista para a pílula antes de abrir o editor; a navegação fica nas Actions), `openAppearancePicker`, `selectPalette(name)`, `selectFont(name)`, `confirmAppearancePicker()`, `setChecklistExpanded`, `dismissChecklist()`, `finishCelebration()`, `onOnboardingBack(): Boolean`.
- `core/designsystem/palette/ThemeSeedColor.kt`: `fun resolveThemeSeedColor(themeName: String): Int`, extraída de `SettingsViewModelValueHandlers.handleSelectedTheme`; o Settings passa a usá-la (refactor sem mudança de comportamento, coberto pelos testes existentes do Settings).
- `selectPalette`/`selectFont` gravam em `settingsRepository.selectedThemeName`, `manualSeedColor`, `selectedFontName` e atualizam `MainUiState`, o que re-tematiza o `MainActivityRootContent` ao vivo.

**Testes** `HomeViewModelOnboardingExtTest` (Robolectric, com o teardown do `viewModelScope`): instalação nova → `WELCOME`; usuário antigo → `COMPLETED`; já padrão pula `DEFAULT_HOME`; persistência sobrevive a um novo `HomeViewModel`; seleção de paleta e fonte persiste e atualiza o estado; `onOnboardingBack` em cada estágio. `ThemeSeedColorTest`. `MainUiStateOverlayTest` (ou ampliar o existente): `blocksHome` entra em `isOverlayActive`.

**Pronto quando** a suíte **inteira** existente continua verde sem alterações (o padrão `COMPLETED` garante isso). **Commit** `feat(onboarding): wire onboarding state into HomeViewModel`

### FUE-04 Integrações de sistema
**Arquivos**
- `MainLifecycleObserver.onResume`: `viewModel.syncOnboarding()` **antes** de `checkReleaseNotes`. A extensão lê `settingsRepository.checkDefaultLauncher()`, que já encapsula o `RoleManager`; o ViewModel continua sem `Context`.
- `HomeViewModelReleaseNotesExt.checkReleaseNotes`: só mostra se `lastSeen > 0` **e** onboarding `COMPLETED`; continua gravando `lastSeenVersionCode`.
- `MainActivityUpdateExt.checkAppUpdate`: guard clause `if (viewModel.uiState.value.onboarding.isBlocking) return`.
- `MainActivityNavExt.handleBackNavigation`: primeiro `if (viewModel.onOnboardingBack()) return`.
- `MainActivity.onNewIntent`: ignora a alternância da gaveta se `onboarding.isBlocking`.
- `HomeOverlays.ScrubberOverlay`: em `onLetterSelected`, `config.actions.onboarding.onAlphabetScrubbed()`.

**Testes** ampliar `HomeViewModelReleaseNotesExtTest` (novo: `lastSeen == 0` não mostra e grava; onboarding ativo não mostra), `MainActivityUpdateExtTest` (bloqueante não chama o `AppUpdateManager`), `MainActivityNavExtTest` (voltar no onboarding), `MainLifecycleObserverTest` (`onResume` em instalação nova resolve `WELCOME`). Os testes existentes não mudam.

**Commit** `fix(onboarding): suppress release notes and update prompts on first run`

### FUE-05 Ações e papel de tela inicial
**Arquivos**
- `model/OnboardingActions.kt`: data class com callbacks, todos com padrão `{}`.
- `HomeScreenActions.kt`: `val onboarding: OnboardingActions = OnboardingActions()`.
- `OnboardingActionsFactory.kt` (pacote raiz, ao lado do `HomeActionsFactory`): mapeia cada `OnboardingTask` para as ações existentes (`onOpenFavoritePicker`, `onOpenEditContainerDialog`/`onAddGridItem`, `onSetIsAddingSingleWidget` + `onAddWidgetClick`, `onSettingsClick`) e chama o sinal correspondente. `HomeActionsFactory` só delega (+2 linhas).
- `util/DefaultHomeRoleUtils.kt`: `createRequestRoleIntent(context): Intent?` (API 29+, papel disponível e não detido); `null` → fallback `SettingsIntentUtils.openDefaultLauncherSettings`.
- `MainRoute.kt`: `rememberLauncherForActivityResult(StartActivityForResult)` que, no retorno, chama `viewModel.refreshDefaultLauncherStatus()`, ignorando o `resultCode`.

**Testes** `OnboardingActionsFactoryTest` (cada tarefa chama a ação certa; sem container → `onAddGridItem`); `DefaultHomeRoleUtilsTest` com `@Config(sdk = [28])` → `null` e `@Config(sdk = [33])` com `ShadowRoleManager` (`addAvailableRole(ROLE_HOME)`) → intent não nulo; papel já detido → `null`; `HomeActionsFactoryTest` continua verde.

**Commit** `feat(onboarding): add onboarding actions and default home role request`

**Gate completo do M1.**

---

## M2 Componentes

Todos com `LocalInspectionMode` renderizando o estado final assentado, `@AureolePreview` privado e screenshot test em `app/src/screenshotTest/java/dev/mnascimentos/aureole/feature/home/components/onboarding/OnboardingScreenshotTest.kt` (dividir em dois arquivos se passar de 800 linhas).

### FUE-06 `AureoleHaloProgress` (Design System)
`core/designsystem/components/AureoleHaloProgress.kt`: anel de progresso com o logo no centro, `progress: Float` animado com spring, tamanhos via parâmetro `size: Dp` (padrão `AureoleDS.dimens.iconHuge`). Sem dependência de `feature/*`. KDoc obrigatório.
**Testes** unit (renderiza, `progress` limitado a 0..1, semântica `ProgressBarRangeInfo`) e screenshot (0, 0,5 e 1). **Commit** `feat(ds): add AureoleHaloProgress component`

### FUE-07 Strings e motion
- Todas as strings da spec §5 em EN e PT-BR, mais `settings_advanced_replay_onboarding`.
- `model/OnboardingMotionSpec.kt` com as constantes da spec §6.
- Teste `OnboardingStringsTest`: para cada chave `home_onboarding_*` em `values/`, existe em `values-pt-rBR/` (lê os XML de `src/main/res`).
**Commit** `feat(onboarding): add onboarding strings and motion spec`

### FUE-08 Welcome
`WelcomeScreen.kt` + `WelcomeHaloComponent.kt` (anel com `drawArc`, brilho radial, estrelas em um `Canvas` lendo a animação na fase de draw; loops desligados se `!ValueAnimator.areAnimatorsEnabled()`).
**Testes** unit: botões chamam `onStart`/`onSkipIntro`. Screenshot: estado assentado.
**Commit** `feat(onboarding): add animated welcome screen`

### FUE-09 Tela inicial padrão
`DefaultHomeStepComponent.kt` (ilustração com o mini-aparelho e o botão Início pulsando; link "Abrir configurações do sistema" quando `defaultHomeAttempted && !isDefaultLauncher`).
**Testes** unit: callbacks e visibilidade do link. Screenshot: inicial e após tentativa.
**Commit** `feat(onboarding): add default home step`

### FUE-10 Coach de gestos
`GestureCoachComponent.kt` + `GestureCoachHandComponent.kt`. Camada sem `pointerInput`; só o cartão recebe toque. `liveRegion` no cartão; "Mostrar para mim" após 8 s ou com TalkBack.
**Testes** unit: **teste de passagem de toque** (com o coach visível sobre um `Box` que tem `detectDragGestures`, um `swipeUp` chega no `Box`); "Mostrar para mim" aparece com `mainClock.advanceTimeBy(8_000)`; modo canhoto troca o título. Screenshot: 3 passos + estado de sucesso.
**Commit** `feat(onboarding): add interactive gesture coach`

### FUE-11 Lista, pickers, pílula e celebração
- Mover `PickPaletteBottomSheet` (com `PaletteRowItem` e `PaletteColorSwatches`) de `feature/settings/components/SettingsAppearanceDialogs.kt` para `composable/sheets/PickPaletteBottomSheet.kt`, e `PickFontBottomSheet.kt` para `composable/sheets/`. Só imports mudam no Settings e nos testes; os screenshot tests ficam no mesmo arquivo (`SettingsDialogsScreenshotTest`), então as referências não mudam.
- `OnboardingChecklistComponent.kt`, `OnboardingChecklistItemComponent.kt` (texto via `AnnotatedString.fromHtml`), `OnboardingChecklistPillComponent.kt`, `OnboardingAppearancePickerComponent.kt` (hospeda os dois pickers; seleção chama `selectPalette`/`selectFont`; botão "Ficou bom"), `OnboardingCelebrationComponent.kt`.
**Testes** unit: clique em cada item dispara `onTaskClick` com a tarefa certa; pílula não aparece quando `isOverlayActive` vem de outra sobreposição; contagem "x de 7"; minimizar/expandir; item concluído não é clicável; celebração chama `onFinished` após o tempo. Screenshot: lista 1/7, lista 4/7, pílula, picker de paleta sobre a home, celebração.
**Commit** `feat(onboarding): add first steps checklist, appearance pickers and celebration`

**Gate completo do M2.** As referências novas são geradas com `updateDebugScreenshotTest` **somente com o seu OK** e revisadas uma a uma.

---

## M3 Integração

### FUE-12 Host no `MainScaffold`
- `OnboardingHostComponent.kt`: roteia por estágio com `AnimatedContent`; recebe `OnboardingUiState`, `OnboardingActions`, `HazeState`, `isHazeEnabled`.
- `MainScaffold.kt`: `OnboardingOverlay(uiState, actions)` como última camada (acima de widget picker, favoritos, update e release notes). Fica fora do `if (!isLoading)`, para o welcome cobrir a primeira sincronização.
- Haptic `Confirm` em `LaunchedEffect` quando `completedSteps`/`completedTasks` crescem.

**Testes** `MainScaffoldTest`: com `onboarding.stage = WELCOME`, o título aparece; com `COMPLETED`, não. Teste de integração Robolectric em `HomeScreenTest`: estágio `GESTURES`, `swipeUp` na raiz → `onAllAppsDrawerOpen` chamado.
**Pronto quando** os screenshot tests **existentes** passam sem mudança (padrão `COMPLETED`). **Commit** `feat(onboarding): show first user experience on home`

### FUE-13 Rever boas-vindas no Settings
`SettingsAdvancedScreen.kt`: `SettingsActionItem` "Rever boas-vindas" (ícone `Logo` ou `RefreshCcw`). `SettingsViewModel` ganha `replayOnboarding()` em uma extension (`settingsRepository.onboarding.restart()` + `shouldFinishActivity = true`); nova action em `SettingsScreenActions` com padrão `{}`.
**Testes** unit da extension; teste de UI do item; o screenshot `SettingsAdvancedScreenScreenshotTest` **vai mudar** (item novo): parar, avisar e revisar a nova referência com você.
**Commit** `feat(settings): add replay welcome tour option`

### FUE-14 Release, documentação e QA
- `ReleaseNotesData.kt` / `ReleaseNotesRepository.kt`: nova versão em linguagem simples, sem termos técnicos. Rascunho: *"Boas-vindas guiadas: quem instala o Aureole agora é recebido com uma apresentação rápida que ensina os gestos e ajuda a montar a tela. Já usa o app? Veja em Configurações › Avançado › Rever boas-vindas."* `ReleaseNotesRepositoryTest` continua sem versões hardcoded.
- `README.md`: bullet "Boas-vindas guiadas" em Principais Funcionalidades.
- Gate completo + matriz de QA abaixo.
**Commit** `docs(release): announce guided welcome`

---

## M4 Ajustes (28/10)

Detalhes, tabelas e testes em [`CHANGES_2026-10-28.md`](CHANGES_2026-10-28.md), que vence onde conflitar com este plano. Ordem de execução: FUE-19, FUE-20, FUE-15, FUE-16, FUE-17, FUE-21, FUE-22, FUE-23, FUE-24 e FUE-18.

| Tarefa | Entrega |
|---|---|
| FUE-19 | área de widgets vazia mostra "Adicionar widget"; widget único vira `SINGLE_APP_WIDGET`; favoritos e todos os apps respeitam o modo canhoto |
| FUE-20 | `GridDefaults` com o layout v3 (retrato, paisagem e canhoto espelhado); `container_item` do layout padrão nasce sem fundo; "Redefinir layout" recarrega os containers |
| FUE-15 | instalação nova grava `[]` (home vazia); `[]` salvo deixa de cair no padrão; "Não mostrar mais" aplica o v3 onde a home estiver vazia |
| FUE-16 | 8 tarefas (`ADD_CLOCK` novo); `startGridTask` coloca o item na posição do v3 (ou no maior espaço livre) em modo de edição e abre o editor |
| FUE-17 | `pendingTask` avaliada só no ponto de confirmação; falha remove o item criado (favoritos viram `ALL_APPS_LIST`); container conclui no Salvar do editor; chip de dica com **Salvar** |
| FUE-21 | Settings conclui na volta à home; a celebração espera a home em primeiro plano (`isCelebrationPending`) |
| FUE-22 | ao concluir, a home sempre fica com acesso aos apps (`ALL_APPS_LIST` no lugar de favoritos vazios ou na posição padrão) |
| FUE-23 | Personalizar com "Adicionar favoritos", "Adicionar todos os apps", ícones novos e "grupo rolável" |
| FUE-24 | dedo do coach e alfabeto-fantasma na faixa real do scrubber; passo omitido em paisagem; esqueleto de carregamento na gaveta, no `ALL_APPS_LIST` e no seletor de favoritos, no lugar do spinner global |
| FUE-18 | referências de screenshot e documentação |

## Matriz de QA manual (aparelho real ou emulador)

| Cenário | API 28 | API 30 | API 33+ | Observação |
|---|---|---|---|---|
| Instalação limpa → fluxo completo | ✓ | ✓ | ✓ | API 28 usa configurações do sistema; API 30 sem blur |
| Atualização a partir da 0.8.26 | | | ✓ | sem onboarding; release notes aparecem |
| Diálogo do papel em Samsung / Xiaomi / Pixel | | | ✓ | resultado lido de `checkDefaultLauncher()` |
| Recusar o papel e definir depois pelo sistema | | | ✓ | item marca no próximo `onResume` |
| Landscape e tablet | | | ✓ | largura máxima da lista |
| Modo canhoto | | | ✓ | dica da borda esquerda |
| Barra alfabética desativada | | | ✓ | 2 passos |
| TalkBack | | | ✓ | "Mostrar para mim" desde o início; anúncios |
| Fonte do sistema em 200% | | | ✓ | lista rola; pílula com reticências |
| Animações desligadas (opções do desenvolvedor) | | | ✓ | sem loops nem partículas |
| Matar o processo em cada estágio | | ✓ | ✓ | retoma no mesmo estágio |
| Reset de fábrica do launcher | | | ✓ | fluxo recomeça |
| Rever boas-vindas | | | ✓ | não altera layout, paleta, fonte |
| Troca de paleta e fonte ao vivo | ✓ | ✓ | ✓ | home inteira re-tematiza atrás do sheet |

## Riscos e mitigação

| Risco | Mitigação |
|---|---|
| Reducer no `updateUiState` afeta todo o Home | Retorno imediato fora de `GESTURES`/`CHECKLIST`; padrão `COMPLETED`; FUE-03 exige a suíte inteira verde sem alterações. |
| Lista cobrir o editor que ela mesma abriu | `onTaskClick` recolhe para a pílula; pílula escondida durante outras sobreposições; reabre na conclusão (teste em FUE-11). |
| Overlay do coach engolir gestos | Camada sem `pointerInput` + teste automatizado de passagem de toque (FUE-10, FUE-12). |
| Diálogo do papel de tela inicial inconsistente entre fabricantes | Não confiar no `resultCode`; revalidar em todo `onResume`; link para as configurações do sistema após uma tentativa. |
| Mover os pickers quebrar o Settings | Só imports mudam; screenshot tests no mesmo arquivo; `PickFontBottomSheetTest` e `SettingsAppearanceDialogsTest` continuam cobrindo. |
| `HomeScreen.kt` ou `MainScaffold.kt` crescerem demais | Toda a UI nova vive em `components/onboarding/`; o `MainScaffold` ganha uma chamada (~15 linhas). |
| Cobertura cair com muita UI nova | Lógica concentrada em funções puras com 100% de branches (FUE-02) e extensões testadas (FUE-03); UI coberta por testes Compose. |
| Celebração repetir após reinício | `COMPLETED` é gravado no momento da conclusão; `isCelebrating` não é persistido. |
