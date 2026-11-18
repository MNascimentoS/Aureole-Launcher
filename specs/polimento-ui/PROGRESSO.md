# Progresso: polimento-ui

Gates e revisão visual: na fase G do lote (`../EXECUTAR-LOTE-17-11.md`).

| # | Fase | Modelo | Estimativa | Feita |
|---|---|---|---|---|
| 1 | Linhas e menus | Sonnet | 1–1,5 h | [x] |
| 2 | Fundo dentro da Aparência | Opus | 1,5–2,5 h | [x] |
| 3 | Logo, anel e pasta aberta | Opus | 2–3 h | [x] |

## Notas por fase

**Fase 1** (compile + testes da fase verdes; sem gates):
- RF-10: `menuRowTitleColor` em `composable/menu/MenuRowColors.kt`, usada em `MenuRowTexts` e `AureoleSwitchRow`; teste `MenuRowColorsTest`.
- RF-11: `MenuIconColumn` sem quadrado.
- RF-08/09: indicador global removido (UI, estado, toggle, string); migração única `SettingsRepository.consumeLegacyWidgetDotsOff` + `WidgetRepository.turnDotsOff`, chamada em `loadWidgetSettings` sobre `top_widgets` + grupos do grid. Teste `WidgetDotsMigrationTest`. `SettingsInventoryTest` ajustado (22 itens).
- RF-04: `OverlayHostSpec.showHeaderAction` (false nos Ajustes) + `LicenseDetailBottomSheet` sem logo.
- RF-18: linha Personalizar home no menu do app (`AppItemRowActions.onCustomizeHome`) e na sheet do Container (fim de OPÇÕES, salva e abre). A sheet da Pilha de widgets já tinha a linha (depois do indicador).
- RF-19: título com slots de 48 dp, 1 linha com reticências; título do container passou a "Container".

**Fase 2** (compile + testes da fase verdes; sem gates):
- RF-01: `SettingsInnerKind.BACKGROUND`; a linha Definir plano de fundo abre dentro da sheet de Aparência (`AppearanceAreaSheet.kt`, novo), título "Definir plano de fundo", sem voltar. Voltar do sistema salva a cor (se mudou) e devolve a lista; arrastar/tocar fora também devolvem a lista (`areaDismissReturnsToList`: a sheet desce e sobe na lista, o fundo desfocado fica). Fechar a sheet salva paleta, fonte e fundo. Prévia: `BackgroundMiniHome` com a cor do rascunho ou a foto (enquadramento e escurecer, `WallpaperPhotoLayer`; `AureoleMiniHome` ganhou `backdrop`). Editor de foto abre em tela cheia e volta para o Fundo (`SettingsSheet.Child.returnInner`, salvo na rotação). Paisagem: a mesma divisão prévia/conteúdo das outras pickers. `SettingsChildKind.BACKGROUND` saiu; os diálogos da tela de bloqueio ficam ao lado da sheet.
- RF-02: `SheetSectionHeading` (`large` antes de cada rótulo menos o primeiro, `xSmall` até o conteúdo) em TIPO, DO SEU TEMA, PALETAS, TONS; switch da tela de bloqueio como linha comum com `small` acima.
- RF-03: Vivas fora (rótulo, cores e a string `settings_background_section_vivid` PT/EN). Migração única `SettingsRepository.migrateVividBackground(RecommendedColors)` (chamada no `loadSettings` da Home e dos Ajustes): tema "Custom" com semente das Vivas → Frostbite (D-11). Testes: `VividBackgroundMigrationTest`, `VividBackgroundRepositoryMigrationTest`.
- Testes: `AppearanceBackgroundInnerTest` (fluxo na sheet), `BackgroundInnerTest`, `AppearanceDraftTest`, `SettingsSheetNavTest`/`WallpaperEditorNavTest` atualizados; os dois testes do fundo como sheet filha em `SettingsAppearanceControlsTest` saíram (cobertos pelo novo).

**Fase 3** (compile + testes da fase verdes; sem gates):
- RF-05/RF-19: cabeçalho novo em `core/designsystem/components/OverlaySheetHeader.kt`: uma Row com slot de 48 dp, título (centralizado, 1 linha, reticências) e outro slot de 48 dp; a logo (16 dp) entra num dos slots, centralizada na linha do título; canhoto espelha (`OverlaySheetDecorations.isLeftHanded`). O slot `TopEnd` antigo saiu do `OverlaySheetBody`. `headerIcon` segue o mesmo tamanho e lugar. Regra única `hasHeaderAction`: sem `onSettingsClick` (e sem `headerIcon`) não há logo, em vez da logo morta de antes.
- RF-05b: sem logo no menu da pasta, `FolderFormDialog` (já sem clique), Escolher apps da pasta, menu do botão rápido, Aparência do botão rápido e Escolher atalho (D-13). O segurar no puxador continua abrindo os Ajustes nos menus. **Personalizar voltou a ter a logo** (RF-05b, protótipo e `ajustes-onboarding` pedem; tinha saído em fc9e705e): tocar abre os Ajustes e fecha a sheet; `CustomizeBottomSheetTest` atualizado.
- RF-06: `OverlaySheetHeaderLogo` com `interactionSource`: pressionado → 0,86 e −18° em 180 ms (ease-out) + círculo `surfaceVariant`; soltar → mola com overshoot (damping 0,45) e o clique roda no soltar; cancelar só volta. Pose em função pura `headerLogoPose` (sem animações do sistema: fica parada). `rememberSystemAnimationsEnabled` novo em `core/designsystem/utils/`.
- RF-07: a sheet Escolher favoritos dos Ajustes não passa `onSettingsClick` nem `onCustomizeHome` → sem logo e sem Personalizar home (teste em `FavoriteAppsDialogTest`).
- RF-12: `AureoleHaloProgress` virou `AureoleLogoRing(progress: Float?)` (mesmo lugar, `core/designsystem/components`, como o SPEC; D-14): `null` gira (estático sem animações), semântica `Indeterminate`/valor; lógica em `core/designsystem/model/LogoRing.kt`. Usos do onboarding migrados. Screenshot 0/50/100/indeterminado.
- RF-13: raiz dos Ajustes: `SettingsUpdateRingObserver` (core/lifecycle, `AppUpdateManager` próprio da `SettingsActivity`) põe `SettingsUiState.updateRing` em Checking ao verificar no resume e segue o download real pelo `InstallStateUpdatedListener` (`settingsUpdateRingFor`, JVM). A logo grande vira o anel (68 dp) no mesmo espaço, com crossfade. Fim do onboarding: a celebração já fecha o anel (agora `AureoleLogoRing`). Vitrine e espiada ficam para `ajustes-onboarding`.
- RF-14: logo do cabeçalho do checklist maior (`LOGO_RING_LOGO_FRACTION_LARGE` = 0,62 do anel, que segue com 36 dp).
- RF-15/16/17: `folderPopupPlacement`/`gridFolderPopupPlacement` + `FolderPopupBounds` (model, JVM, testes topo/meio/fim, 3 botões e gestos); `OpenedFolderPlacementLayout` mede o popup com a altura útil (tela − status − navegação − 16 dp, teto 400 dp na lista) e põe o topo; a grade centraliza no espaço útil. `calculateClampedTopDp`/`resolvePopupAlignment` saíram; âncora em `folderAnchorTopPx`.
- Screenshots novos (não rodados, fase G): `OverlaySheetHeaderRulerScreenshotTest` (régua no centro da linha do título, destro e canhoto), `WhenFolderOpensFromTheLastItem_...` em `FolderScreenshotTest`, `AureoleLogoRingScreenshotTest` (renomeado; a referência antiga `AureoleHaloProgressScreenshotTestKt` precisa ser apagada/regerada).

## Ficou faltando
- Fase 2: `SetBackgroundBottomSheet` (sheet avulsa) continua no código só porque os testes de tela e os screenshots (`SettingsBackground`, `SettingsLockScreen`, `WallpaperEditor`, `SettingsDialogs`) a usam; ela já mostra o conteúdo novo. Na fase G, trocar esses screenshots para `SettingsSheet.Area(APPEARANCE, inner = BACKGROUND)` e apagar a sheet avulsa. Screenshots do Fundo (retrato/paisagem) também na fase G.
- Fase 2: D-11 e D-12 abertas (seguiu a opção conservadora).
- Migração do RF-09 só alcança grupos conhecidos no carregamento (top_widgets + itens do grid); grupo inexistente no grid não é afetado.
- RF-07 e o restante do cabeçalho (logo na Row do título) ficam na fase 3.
- Screenshots das linhas destrutivas/sheets: fase G.
- Fase 3: anel de ~30 dp na logo do título (RF-05 "quando houver") não tem parâmetro próprio; a `ajustes-onboarding` pode passar `headerIcon` ou ganhar um slot de anel no `OverlaySheetHeaderLogo`.
- Fase 3: `OpenedFolderOverlayParams.screenHeightPx/screenDensity` ficaram sem uso (a altura vem do Layout); limpar quando mexer ali.
- Fase 3: screenshots e referências (cabeçalho com régua, pasta no fim, anel) e revisão visual da altura do cabeçalho com a logo: fase G.

## Revisão visual 17/11 (fase G, 2ª passada)
- RF-18: o menu do app sem "Personalizar home" era **artefato do teste** (`AppItemRowActions` sem `onCustomizeHome`; na home `HomeAppItemRow`/`HomeScreenAppSheetOverlays` sempre passam). Teste corrigido; a linha aparece antes do grupo destrutivo.
- MenuRows com "Remove" no topo: **artefato do teste** (galeria de componentes); reordenado para a destrutiva vir por último, como nos menus reais.
- RF-14: **bug real**: logo do checklist com ~22 dp. Anel passa de 36 dp para 64 dp (`avatarSize`), logo a 0,62 = ~40 dp, como no protótipo.
- RF-19: screenshot novo do título longo com reticências (`OverlaySheetHeaderLongTitleScreenshotTest`).

### Ficou faltando
- Fundo em paisagem canhoto: rótulos e switch espelham, mas cartões de tipo, amostras e a posição prévia/sheet não. Ambíguo (o RF-16 fixa a prévia à esquerda); não mexido.
- `OverlaySheetGestureZone.kt` tinha no working tree uma mudança não commitada (faixa de 20 → 10 dp) que mexia em ~180 referências; foi posta de lado durante os gates (patch em `.claude/gesturezone-wip.patch`) e reaplicada depois. Quem commitar essa mudança precisa regerar as referências.
