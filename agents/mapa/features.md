# Mapa: features

Caminhos relativos a `app/src/main/java/dev/mnascimentos/aureole/` (`M/`). Só existem 3 pastas em `feature/`: `home`, `settings`, `secretgame`. A `MainActivity.kt` fica na raiz de `M/`, fora de `feature/`.

## home
| Item | Onde |
|---|---|
| Activity | `MainActivity.kt` (chama `MainRoute` e registra 3 observers); helpers em `feature/home/extensions/MainActivityNavExt.kt`, `MainActivityUpdateExt.kt` |
| Rota | `feature/home/MainRoute.kt`: `rememberHomeActions`, `WidgetHostManager`, `BackHandler`, provê `LocalHomeUiState`/`LocalLeftHanded`/`LocalHomeActions` |
| Scaffold | `feature/home/MainScaffold.kt`: `HomeScreen` + overlays globais |
| Screen | `feature/home/HomeScreen.kt` (373 linhas); layout em `HomeScreenContent.kt` (`MainHomeLayout`) |
| ViewModel | `feature/home/HomeViewModel.kt`; extensões em `feature/home/extensions/HomeViewModel*Ext.kt` e `components/*/extensions` |
| Repositórios | via `model/HomeRepositories.kt`: App, Widget, Settings, Folder, FavoriteContainer, Container, Grid, ClockConfig (+ `UndoController`) |
| UiState | `model/MainUiState.kt`: apps/busca, widgets, relógio, tema/vidro, wallpaper, pastas, favoritos, grade, containers, flags de sheets, `undoSnackbar`, `onboarding` |
| Actions | `model/HomeScreenActions.kt` (data class de lambdas `onXxx`); params em `model/HomeScreenConfigs.kt` |
| Outros models | `MainUiStateFavorites.kt`, `AllAppsOpenSource.kt`, `GestureExclusion.kt`, `HomeLocalProvider.kt`, `SwipeDownNotifications.kt`, `ActiveHomeGestures.kt` (gestos ligados, faixas do sistema), `GestureLock.kt` (trava de direção, limite de 48 dp), `DrawerDrag.kt` (progresso, `drawerSettleOf`, `DrawerDragState`) (`verticalSwipeOf`, `SwipeDownConditions.opensNotifications`, `SwipeUpConditions.opensAllApps`, `VerticalSwipe.opensHome`, `SwipeScrollTracker (direção DOWN ou UP)`: quando o deslizar para baixo abre as notificações) |
- `updateUiState` passa pelo `OnboardingReducer`. Há também `components/widgets/WidgetPickerViewModel.kt`.
- Estados `showWidgetPopup`, `showWidgetResizeDialog` e `showSettingsDialog` estão no `MainUiState` mas nenhum composable os consome.

### Subpastas de `feature/home/components/`
| Pasta | Conteúdo |
|---|---|
| appsdrawer | Lista/drawer de apps, `appitem/`, busca |
| clock | `ClockContainer`, `HomeClockHeader`, `EditClockBottomSheet` (pager, abas, estilos, fontes, cores) |
| common | Overlays de pasta e de sheets de app, `WallpaperBackground`, `WallpaperFramedImage` |
| containers | `Container`, `EditContainerScreen` ("Personalizar grupo": abas Itens/Layout/Aparência no rodapé, páginas na mesma sheet via `GroupSheetNav`/`GroupSheetPage`, prévia no tamanho da home por `containerSizes`/`onContainerMeasured`, dicas `GroupTipFlow`, fechar decide por `groupCloseOutcome`); `folder/` com `OpenedFolderPopup` |
| content | `GridItemContent` (despacha o tipo do item), `HomeScreenScrollContent` |
| favorites | `AppsList`, `FavoritesListRow` |
| onboarding | `OnboardingHostComponent`, checklist, `WelcomeScreen`, `GestureCoach`, `DefaultHomeStep`. Etapas do coach (`GestureCoachStep`, ordem em `visibleGestureSteps`): todos os apps → alfabeto → tocar e segurar; o passo das notificações foi removido. Paisagem do onboarding: `onboardingLandscapeLayout` + `withOwnLandscapeContent` (container e widgets com id próprio, vazios). Checklist em 8 etapas (`OnboardingTask`, `isExtra`): seções `onboardingHomeTasks`/`onboardingExtraTasks`, extras travados até `isHomeBuilt`, `isClickable` (feitas reabrem); Visual = uma sheet `OnboardingVisualSheetComponent` (abas Paleta | Fonte no rodapé, `ThemePreviewOverlay` com `PalettePickerGrid`/`FontPickerGrid` e mini-home com hora/data reais via `visualPreviewTime`; fechar chama `onCloseVisual(VisualChoice)` → `closeVisualSheet`, padrões só na 1ª vez por `resolveVisualClose`). Destaque do item novo: `Modifier.onboardingNewItemOutline` + `OnboardingNewItemChip` ("Novo · Relógio", `newItemChipRes`) enquanto `highlightedItemId`; no peek fica a pílula (bolha removida), 2 s (`ONBOARDING_HOME_PEEK_MS`). Conclusão: favoritos só com 1+ favorito, grupo só com 1+ item (`savingContainerTask`). "Tudo pronto" 2,4 s, scrim leve, sem blur, espera `homeReturnSettleMillis` ao voltar de Configurações (`returningToHome`/`showingHome`). Widgets pré-carregados ao abrir o checklist (`preloadWidgetsOnChecklist`). Pílula `OnboardingChecklistPill` ("Próximo:", "Continuar"); morph 300 ms (`CHECKLIST_MORPH_MS`), sem blur em movimento (`OnboardingGlass.atRest`), etapa abre após `checklistCollapseDelayMillis`. Boas-vindas: "Começar do zero", "Usar um layout pronto" (`OnboardingOutlinedButton`, só com grade vazia: `canTakeLayoutPreset`) e "Pular introdução". Layouts prontos (`LayoutPreset` em `core/data/model/LayoutPreset.kt`): `OnboardingSurface.PRESETS` → `OnboardingPresetScreen` (cartões com `OnboardingPresetThumbnail`); seleção pura em `model/LayoutPresetSelection.kt` (`rankedPackages`, `categoryFolders`, `planFor`/`planAll` sobre `PresetCatalog` lido por `AppRepository.readPresetCatalog`), `LayoutPresetPlan`/`spec`/`completedTasks` em `model/LayoutPresetPlan.kt`, grade em `extensions/OnboardingPresetLayout.kt` (`presetPortraitItems`, `presetContainerModel`); `HomeViewModelOnboardingPresetExt` (`openPresets`, `selectPreset`, `applyPreset` com Desfazer na camada do onboarding, D-35); sub-rótulo "LAYOUT <NOME> APLICADO" por `presetAppliedTo`. Coach: "Mostrar para mim" desde o início, "Isso aí!" vira o chip "◁ Volte para continuar" após `COACH_SUCCESS_CARD_MS`; o coach sai sem fade (`surfaceExitMillis`). Screenshots: fluxos `onboarding` (17 passos) e `layoutpronto`, casos em `screenshotTest/.../fixtures/OnboardingFixtures.kt` (`OnboardingCase`; o chip do coach é `GestureCoachBackChip`, a hora da sheet Visual vem de `nowMillis` fixo) |
| overlays | `HomeOverlays`, `CustomizeBottomSheet`, `HomeDragGestures` e `HomeVerticalGesture` (detector vertical só com o gesto ligado; a gaveta acompanha o dedo), `AppsDrawerPull` (efeito na home e fechar arrastando), `HomeNestedSwipes` (o delta que sobra de uma lista no topo abre as notificações ou a gaveta), `InAppUpdateDialog` |
| quickbutton | Sheets de Settings, Appearance e ShortcutSelection |
| scrubber | `CurvedAlphabetScrubber` |
| widgets | `WidgetPickerScreen`, `WidgetHostManager`, `EditWidgetGroupScreen`, `WidgetStackBottomSheet` |
- `feature/home/grid/`: `DynamicGridContainer`, `AddContainerDialog`, `EditContainerDialog*`, `EditGridItemDialog`, drag e resize.

### Ordem de composição (baixo para cima; o último desenha por cima)
| # | Composable | Condição | Arquivo |
|---|---|---|---|
| 1 | `WallpaperBackground` + `MainHomeLayout` | sempre | HomeScreen.kt (`HomeScreenBody`) |
| 2 | `AppsDrawerOverlay` | `isAllAppsDrawerOpen` | components/overlays/HomeOverlays.kt |
| 3 | `ScrubberOverlay` | scrubber ligado e teclado oculto | idem |
| 4 | `CreateFolderDialog` | `isCreateFolderDialogVisible` | components/common/HomeScreenFolderOverlays.kt |
| 5 | `OpenedFolderPopup` | `activeFolder != null` e sem dialog de pasta aberto | idem |
| 6 | `FolderAppPickerDialog`, `EditFolderDialog` | `isAddAppToFolderDialogVisible`, `isRenameFolderDialogVisible` | idem |
| 7 | `AppItemBottomSheet` (container/pasta), `ContainerFolderBottomSheet`, `WidgetStackBottomSheet` | `activeContainerAppBottomSheet`, `activeFolderAppBottomSheet`, `activeContainerFolderBottomSheet`, `activeWidgetStackBottomSheet` | HomeScreenFolderOverlayComponents.kt |
| 8 | `QuickButtonSettings/Appearance` e `ShortcutSelection` sheets | `showQuickButton*`, `showShortcutSelectionBottomSheet` | components/quickbutton/HomeScreenQuickButtonOverlays.kt |
| 9 | `CustomizeBottomSheet`, `EditClockBottomSheet` | `showCustomizeBottomSheet`, `showEditClockBottomSheet` | HomeScreen.kt (`HomeOverlaysDialogsAndErrors`) |
| 10 | `EditWidgetGroupScreen`/`EditGridItemDialog`, `EditContainerScreen` (sheet do grupo; abre sozinha ao adicionar um grupo, `editingContainerIsNew`) | `editingGridItem`, `isEditContainerDialogVisible` | idem |
| 11 | `AddContainerDialog` ("Adicionar componente", subtítulos, "Comece aqui" com `!isContainerTipsSeen`; por último, sobre as sheets de edição) | `showAddContainerDialog` | idem |
| 12 | `HomeUndoSnackbar` | `undoSnackbar` | HomeScreen.kt |
| 13 | `WidgetPickerScreen` | `showWidgetPicker` | MainScaffold.kt |
| 14 | `FavoriteAppsDialog` | `showFavoritePickerDialog` | idem |
| 15 | `UpdateAvailableDialog`, `UpdateDownloadedDialog` | flags + `canShowUpdatePrompt` | idem |
| 16 | `ReleaseNotesBottomSheet` | `showReleaseNotesBottomSheet` | idem |
| 17 | `OnboardingHostComponent` | sempre; decide sozinho se bloqueia toque | idem |

## settings
| Item | Onde |
|---|---|
| Activity | `feature/settings/SettingsActivity.kt` (observa `shouldFinishActivity`; registra `SettingsLifecycleObserver`) |
| Navegação | `feature/settings/SettingsRoute.kt` (`SettingsNavGraph`: Root e Licenses) e `screens/SettingsRoute.kt` |
| Screens | `screens/SettingsRootScreen.kt` (via `SettingsSheetHost`), `SettingsLicensesScreen.kt`, `SettingsScreen.kt` (dialogs de tela) |
| ViewModel | `feature/settings/SettingsViewModel.kt` + `extensions/SettingsViewModel*Ext.kt`, `SettingsToggleManager` |
| Repositórios | Settings, App, Folder, Grid |
| UiState | `model/SettingsUiState.kt` (50+ campos; `SettingValue` no mesmo arquivo) |
| Actions | `model/SettingsConfigs.kt` (`SettingsScreenActions`), `SettingsLayout.kt`, `AppearanceMenuDialogActions.kt` |
| Navegação de sheets | `model/SettingsSheetNav.kt`: `SettingsSheet` (Area/Child), `SettingsSheetEvent`, enums `SettingsArea` (APPEARANCE, BEHAVIOR, ADVANCED), `SettingsChildKind`, `SettingsInnerKind` |
- Sheet de área: `components/SettingsSheetHost.kt` (estado em `rememberSaveable`). Sheets filhos: `components/SettingsChildSheets.kt` (CARDS_GLASS, RELEASE_NOTES, CONTRIBUTORS, WALLPAPER_EDITOR).
- Seções em `sections/` (Appearance, Behavior, Advanced); editor de wallpaper em `components/wallpaper/`.
- Dialogs de tela em `screens/SettingsScreen.kt`: CreateFolder, ResetGrid, FavoriteApps, ContainerPosition, SettingsButtonPosition, SearchIconPosition (condicionados a `show*`). Lock screen: `components/BackgroundLockScreenRow.kt`.
- `ReleaseNotesBottomSheet` abre em `SettingsRootScreen.kt` ao tocar a versão; `LicenseDetailBottomSheet` em `SettingsLicensesScreen.kt`.

## secretgame
- Sem Activity, ViewModel ou repositório; hospedado por `SettingsRootScreen.kt` via `SecretGameLayer` (`components/SecretGameLayer.kt`).
- Estado: `model/SecretGameHostState.kt` (`isGameOpen`, `showBestScore`, `open()`, `finish()`); engine em `model/SecretGameEngine.kt`.
- `SecretGameOverlay` abre com `isGameOpen`; `SecretBestScorePopup` (Dialog) com `showBestScore`. O recorde persiste via `SettingsViewModel.onSecretGameOver` e `SettingsRepository.updateSecretBestScore`.
- Áudio: `audio/SecretSoundPlayer.kt` (SoundPool, `res/raw/secret_*.ogg`; desligado em preview), criado por `rememberSecretSoundPlayer()` em `SettingsRootScreen`; sons por evento em `model/SecretSounds.kt` (`secretSoundsFor`). Mudo na pausa: `components/SecretSoundButton.kt` + `SettingsViewModel.onSecretSoundToggled` (`secretSoundOn`).
- Brilho do "Muito obrigado!" (só até abrir o jogo uma vez, `secretGamePlayed`): `components/SecretShine.kt` (`rememberSecretShine`, `SecretThankYou`) e `model/SecretShineState.kt`; o gatilho cancela no toque. `SettingsViewModel.onSecretGameOpened` grava `played`.
- Estrelas de fundo (46 pontos, desenhadas primeiro no canvas): `model/SecretStars.kt` (`starSprites`, `starsAlpha`, `streaksAlpha`); acendem com as molduras e, no "VAI", fazem warp (`WARP_MS` 1600 ms): aceleram e viram riscos (`tail` > 0, `drawWarpStar` em `SecretGameCanvas.kt`) até as linhas de velocidade assumirem, sem fade.
- Toque nos botões: `move` registra `leftPressMs`/`rightPressMs` mesmo na borda (sem troca de faixa nem som); o frame traz `leftPress`/`rightPress` e `leftSqueeze`/`rightSqueeze` (`model/SecretGamePress.kt`), desenhados em `SecretGameButtons.kt` (fundo, anel, seta deslocada). A inclinação da nave persegue a distância até a faixa (`secretTiltChase`, pico ~5°).
- Desenho: `SecretGameTrail.kt` (rastro), `SecretGameButtons.kt` (molduras com `PathMeasure`). Retrato travado só com o jogo aberto, em celular (exceção de lint `SourceLockedOrientationActivity`, `jogo-secreto-ajustes` D-08); em tela grande o sistema ignora e o jogo usa a arena.
- Telas largas (`jogo-secreto-telas-grandes`): `model/SecretArena.kt` (`secretArenaOf`: janela com altura ≥ 1,75 × largura = arena inteira, senão coluna centrada com `REFERENCE_ASPECT` 772/360; `secretLayoutOf` mede o layout na arena, com `sideSpan` e `buttonsOnSides` quando cada lateral tem ≥ `SIDE_CONTROL_MIN_DP`; `rescaledTo` reescala o estado quando a janela muda e o overlay pausa). `components/SecretGameStage.kt` põe o canvas na arena, sem recorte: estrelas e riscos também nas laterais (`spreadX`/`spreadCount`), duas linhas na borda da arena durante a partida. `model/SecretControls.kt`: `secretButtonTopLeft` (setas embaixo ou no meio das laterais; com laterais, o alvo de toque é a lateral inteira), `secretKeyAction`/`onKey` (setas, A/D, Espaço). Previews `AureoleFoldLandscapePreview`/`AureoleFoldPreview` (841 × 701 dp) nos passos P08/P09 do fluxo `jogosecreto`.
