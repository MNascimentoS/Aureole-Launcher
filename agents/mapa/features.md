# Mapa: features

Caminhos relativos a `app/src/main/java/dev/mnascimentos/aureole/` (`M/`). Só existem 3 pastas em `feature/`: `home`, `settings`, `secretgame`. A `MainActivity.kt` fica na raiz de `M/`, fora de `feature/`.

## home
| Item | Onde |
|---|---|
| Activity | `MainActivity.kt` (chama `MainRoute` e registra 3 observers); helpers em `feature/home/extensions/MainActivityNavExt.kt`, `MainActivityUpdateExt.kt` |
| Rota | `feature/home/MainRoute.kt`: `rememberHomeActions`, `WidgetHostManager`, `BackHandler`, provê `LocalHomeUiState`/`LocalLeftHanded`/`LocalHomeActions` |
| Scaffold | `feature/home/MainScaffold.kt`: `HomeScreen` + overlays globais |
| Screen | `feature/home/HomeScreen.kt` (350 linhas); layout em `HomeScreenContent.kt` (`MainHomeLayout`) |
| ViewModel | `feature/home/HomeViewModel.kt`; extensões em `feature/home/extensions/HomeViewModel*Ext.kt` e `components/*/extensions` |
| Repositórios | via `model/HomeRepositories.kt`: App, Widget, Settings, Folder, FavoriteContainer, Container, Grid, ClockConfig (+ `UndoController`) |
| UiState | `model/MainUiState.kt`: apps/busca, widgets, relógio, tema/vidro, wallpaper, pastas, favoritos, grade, containers, flags de sheets, `undoSnackbar`, `onboarding` |
| Actions | `model/HomeScreenActions.kt` (data class de lambdas `onXxx`); params em `model/HomeScreenConfigs.kt` |
| Outros models | `MainUiStateFavorites.kt`, `AllAppsOpenSource.kt`, `GestureExclusion.kt`, `HomeLocalProvider.kt` |
- `updateUiState` passa pelo `OnboardingReducer`. Há também `components/widgets/WidgetPickerViewModel.kt`.
- Estados `showWidgetPopup`, `showWidgetResizeDialog` e `showSettingsDialog` estão no `MainUiState` mas nenhum composable os consome.

### Subpastas de `feature/home/components/`
| Pasta | Conteúdo |
|---|---|
| appsdrawer | Lista/drawer de apps, `appitem/`, busca |
| clock | `ClockContainer`, `HomeClockHeader`, `EditClockBottomSheet` (pager, abas, estilos, fontes, cores) |
| common | Overlays de pasta e de sheets de app, `WallpaperBackground`, `WallpaperFramedImage` |
| containers | `Container`, `EditContainerScreen`; `folder/` com `OpenedFolderPopup` |
| content | `GridItemContent` (despacha o tipo do item), `HomeScreenScrollContent` |
| favorites | `AppsList`, `FavoritesListRow` |
| onboarding | `OnboardingHostComponent`, checklist, `WelcomeScreen`, `GestureCoach`, `DefaultHomeStep` |
| overlays | `HomeOverlays`, `CustomizeBottomSheet`, `HomeDragGestures`, `InAppUpdateDialog` |
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
| 10 | `EditWidgetGroupScreen`/`EditGridItemDialog`, `EditContainerScreen` | `editingGridItem`, `isEditContainerDialogVisible` | idem |
| 11 | `AddContainerDialog` (por último, sobre as sheets de edição) | `showAddContainerDialog` | idem |
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
