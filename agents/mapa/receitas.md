# Mapa: receitas

Caminhos relativos a `app/src/main/java/dev/mnascimentos/aureole/` (`M/`). Gates e regras: `agents/aureole_testing.md`. Gradle: um comando por chamada.

## Nova sheet de menu
Exemplo: `M/composable/appitem/AppItemMenus.kt` (`AppItemBottomSheet`).
1. Crie `*Sheet.kt` só com `@Composable`; models em `model/`.
2. Chame `AureoleMenuListSheet(title, onDismiss, params = MenuSheetParams(...)) { entries }` (`M/composable/menu/AureoleMenuSheet.kt`).
3. Monte `buildList<MenuEntry>` (`MenuEntry.Row/Section/Divider/Content`, `M/composable/menu/model/MenuEntry.kt`); destrutivo com `isDestructive = true`.
4. Textos em `R.string.<feature>_<comp>_<item>` (receita de string). Remoção usa `UndoController`; desinstalar usa `requiresHold`.
5. Abra a sheet a partir de uma flag no UiState (ver ordem em `features.md`). Adicione `@AureolePreview` privado e screenshot test.

## Nova sheet com abas
Exemplo: `M/feature/home/components/clock/EditClockBottomSheet.kt`; outro: `quickbutton/QuickButtonAppearanceBottomSheet.kt`.
1. Enum das abas em `model/` com `@StringRes labelRes`, `index`, `pageCount` (`clock/model/ClockTab.kt`).
2. `HorizontalPager` + `rememberPagerState`; barra **no rodapé** com `AureoleSheetTabBar` (`M/composable/controls/AureoleSheetTabBar.kt`), via wrapper como `EditClockTabBar`.
3. Cada página rolável usa `.fadingEdges(scrollState, AureoleDS.dimens.fadingEdge)` (`M/core/designsystem/utils/FadingEdges.kt`).
4. Clique na aba: `pagerState.animateScrollToPage(tab.index)`. Cabeçalho com `OverlaySheetHeader`.

## Nova string (EN + pt-rBR)
Exemplo de chave: `home_app_menu_uninstall`.
1. `app/src/main/res/values/strings.xml` e `app/src/main/res/values-pt-rBR/strings.xml`, mesma chave nos dois (hoje 539 entradas em cada).
2. Chave `<feature>_<componente>_<descrição>` em snake_case; placeholders posicionais (`%1$d`); plural em `<plurals>`.
3. Uso: `stringResource(R.string.x)`, `@StringRes` em models ou `TextResource.Res`. Não há `AureoleDS.strings`.

## Novo token do DS
- **Dimen:** `val x: Dp = N.dp` no `data class AureoleDimens` (`M/core/designsystem/theme/AureoleDimens.kt`). Se for `radius*`, edite também o factory `from(...)` no fim do arquivo. Testes: `AureoleDimensTokensTest`, `AureoleDimensCornerRadiusTest` (`app/src/test/.../core/designsystem/theme/`).
- **Cor:** campo em `AureoleColors` (`theme/AureoleColor.kt`) **com default**. Sem default, atualize todo `AureoleColors(`: `LocalAureoleColors`, `frostbiteColors`, `palette/ThemePaletteBase.kt` (claro, escuro, `buildSeedAureoleColors`) e o helper `presetPalette` em `palette/ThemePalettePresets.kt` (um campo novo vira mais um valor na string de hex).
- Cor referenciável pelo usuário: edite também `ThemeColorRole` e `ThemeColorRef.resolve` (`palette/ThemeColorRef.kt`).
- Mapeamento Material só em `AureoleTheme.kt` (`buildDarkColorScheme`/`buildLightColorScheme`), se precisar de role Material.

## Nova preferência persistida
Exemplo: `clockTextColorRef` em `M/core/data/repository/SettingsRepository.kt`.
1. Constante `KEY_X` e `DEFAULT_X` no `companion object` do repositório.
2. Propriedade `var x: T get() = prefs.getX(KEY_X, DEFAULT_X) set(v) = prefs.edit { putX(KEY_X, v) }`.
3. Se o reset deve limpar: `resetClockSettings` ou `performFactoryReset`/`wipeLocalData`. Migração de valor antigo: `SettingsRepositoryMigrations.kt`.
4. Exponha pelo UiState (`MainUiState` ou `SettingsUiState`), lendo no ViewModel (ex.: `clock/extensions/HomeViewModelClockSheetExt.kt`).
5. Cor do usuário: guarde `ThemeColorRef.toJson()`, leia com `parseThemeColorRef` (teste `ThemeColorRefTest`). Classe própria de prefs: `OnboardingPreferences.kt` + `OnboardingPreferencesTest`.

## Novo screenshot test
Exemplo: `app/src/screenshotTest/java/dev/mnascimentos/aureole/composable/menu/AureoleRadioSheetScreenshotTest.kt`.
1. `*ScreenshotTest.kt` no pacote espelho, máx. 800 linhas.
2. `@Composable private fun XCase(...)` com `ProvideHomeUiState(MainUiState(...)) { AureoleLauncherTheme { ... } }`.
3. Cada variante: `@PreviewTest @AureolePreview @Composable private fun XScreenshotTest() = XCase(...)` (`com.android.tools.screenshot.PreviewTest`).
4. `./gradlew updateDebugScreenshotTest` gera as referências em `app/src/screenshotTestDebug/reference/`; depois `./gradlew validateDebugScreenshotTest`.

## Novo teste de lógica de ViewModel em JVM puro
Exemplos: `app/src/test/.../composable/menu/model/MenuModelsTest.kt`, `.../feature/home/model/MainUiStateFavoritesTest.kt`.
1. Extraia a lógica para função pura em `model/` ou `extensions/`; o ViewModel só faz fiação.
2. Teste no pacote espelho em `app/src/test/`, só JUnit4 (`org.junit.Test`, `Assert`), sem Robolectric, sem `Context`/`Looper`/`Color` do Android.
3. Nomes de teste entre crases. Rode `./gradlew testDebugUnitTest --tests "*Nome*"`.
