# Progresso: ajustes de sheets e Ajustes

Atualizado ao fim de cada fase. "Feita" marca o commit da fase; Gates e Visual só fecham na fase 4 (gates rodam uma
vez, no fim). Aparelho é o teste do Mateus.

| # | Fase | Modelo | Estimativa | Feita | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|---|
| 1 | Polimento visual: Personalizar, cabeçalho, Ajustes, Avançado, coach, ícones Add New e Home | Sonnet | 1,5–2,5 h | [ ] | [ ] | [ ] | [ ] |
| 2 | Salvar ao fechar + Desfazer nas sheets de edição; picker de cor do fundo | Sonnet | 2,5–4 h | [ ] | [ ] | [ ] | [ ] |
| 3 | Movimento: sheet sobe e estica, checklist → pílula, lista pela borda ou por baixo | Opus | 2,5–4 h | [ ] | [ ] | [ ] | [ ] |
| 4 | Gates, correções e revisão visual | Sonnet + Opus | 1–2 h | [x] | [ ] | [ ] | [ ] |
| 5 | Exploração dos Ajustes (sem código): propostas da IA | Opus | 20–40 min | [ ] | — | — | — |

## Notas por fase

(cada fase escreve aqui: o que mudou, testes da fase, o que ficou faltando)

### Fase 4: gates e correções

| # | Gate | Resultado | Tempo |
|---|------|-----------|-------|
| 1 | `detekt --auto-correct` | passou (3 rodadas até 0 issues, mais 3 após ajustes de testes) | 16-19 s cada |
| 2 | `lintDebug` | passou (0 erros, 0 avisos) | 39 s (1 min 7 s na rodada com erros) |
| 3 | `testDebugUnitTest` | passou (1665 testes) | 32 s |
| 4 | `jacocoTestCoverageVerification` | passou | 2 s (sobre os dados do gate 3) |
| 5 | `updateDebugScreenshotTest` | passou (referências regeneradas) | 81 s |
| 6 | `validateDebugScreenshotTest` | passou | 82 s |
| 7 | `assembleDebug` | passou | 4 s |

- **Bug real achado nos testes (fase 3):** `sheetHeight` pedia a altura intrínseca do conteúdo a cada medição; perguntar a intrínseca invalida o layout, então uma sheet com conteúdo animado (esqueleto de lista carregando, ex. Favoritos em `MainScaffoldTest`) remedia sem parar (218 mil vezes, OOM no JVM de testes e 183 testes em cascata). Corrigido: só pergunta quando o tamanho medido muda (`OverlaySheetMotion.contentMeasuredSize`). Vale conferir no aparelho a sheet de Favoritos com a lista carregando.
- **Detekt:** extraídas `animateHeight`/`animateSpringBack` para `OverlaySheetAnimations.kt`; `canExpand` do controller virou propriedade; `AnchoredSheetState` e `GestureZoneState` (model) cortam parâmetros; `animateEntrance` entrou em `ChecklistSharedModifiers`; `FolderFormDialog` encurtado; atalhos de seleção movidos para `ShortcutSelectionExt.kt`; `AppearanceUndo.kt` renomeado `SeedSnapshot.kt`; constantes da rampa de fundo.
- **Lint:** `LocalOverlayBackdropProvided` (CompositionLocal novo) saiu: virou `hasExternalBackdrop` em `AureoleEditOverlayConfig`/`AutoSaveOverlayParams`/`ThemePreviewOverlayParams`/`ThemePickerSheetOptions` (sem mexer no `lint.xml`); `fadeWith` virou extensão de `Modifier`; logo do Personalizar usa o `modifier` recebido; strings `core_common_save` e `settings_appearance_background_color` removidas (sem uso).
- **Testes atualizados** (salvar ao fechar, sem botões): helper `testing/OverlayTestExt.kt` (`closeOverlayWithBack`, usa o dispatcher de voltar da raiz mais acima); ColorPickerDialog, FolderFormDialog (+2 testes), PickThemeSheets, PickFont, FolderAppPicker Grid/Search, FolderBranchCoverage, FolderComponents, HomeScreenFolderOverlays, QuickButtonAppearance, SettingsAppearanceDialogs/Controls, SettingsScreen, OnboardingAppearancePicker, OnboardingHost, EditContainerManageItems, FactoryResetBottomSheet (a sheet agora sobe: avança o relógio), ScrubberHomeHandoff (usa `onAllAppsDrawerOpenFromEdge`). No grid da pasta tirou-se o `LocalInspectionMode` (sem animação não há botão voltar).
- **Ficou faltando:** testes JVM puros para as extensões de ViewModel com Desfazer e `WallpaperUndoStash.keepCopy` (o jacoco passou sem eles); revisão visual (outro agente); o checklist do aparelho; "um só halo" sem asserção direta.
