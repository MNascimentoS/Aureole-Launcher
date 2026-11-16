# Progresso: ajustes de sheets e Ajustes

Atualizado ao fim de cada fase. "Feita" marca o commit da fase; Gates e Visual só fecham na fase 4 (gates rodam uma
vez, no fim). Aparelho é o teste do Mateus.

| # | Fase | Modelo | Estimativa | Feita | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|---|
| 1 | Polimento visual: Personalizar, cabeçalho, Ajustes, Avançado, coach, ícones Add New e Home | Sonnet | 1,5–2,5 h | [x] | [ ] | [ ] | [ ] |
| 2 | Salvar ao fechar + Desfazer nas sheets de edição; picker de cor do fundo | Sonnet | 2,5–4 h | [x] | [ ] | [ ] | [ ] |
| 3 | Movimento: sheet sobe e estica, checklist → pílula, lista pela borda ou por baixo | Opus | 2,5–4 h | [ ] | [ ] | [ ] | [ ] |
| 4 | Gates, correções e revisão visual | Sonnet + Opus | 1–2 h | [ ] | [ ] | [ ] | [ ] |

## Notas por fase

(cada fase escreve aqui: o que mudou, testes da fase, o que ficou faltando)

### Fase 1 (complemento): RF-21 ícone Home

- `ic_aureole_home` agora é vetor de traço (`res/drawable/ic_aureole_home.xml`, copiado de `assets/`); apagados os 5 `.webp`. `AureoleIcons.Home` não mudou (usa o mesmo id e aplica `tint`). Usos: Launcher padrão (`SettingsAdvancedScreen`) e "Definir como app da home" (`OnboardingChecklistItemComponent`); `QuickButtonIcon.HOME` também aponta para o mesmo recurso e passa a ter o desenho novo. Nenhum teste dependia do desenho; screenshots de Ajustes/checklist precisam regenerar referências (fase 4).

### Fase 2: salvar ao fechar + Desfazer, picker de cor do fundo

- `AureoleAutoSaveOverlay` + `AutoSaveOverlayParams` + `closeOutcome`: o ponto único de fechamento é o `onDismissRequest` do `AureoleEditOverlay` (tocar fora, voltar e arrastar passam por ele depois da saída); salva uma vez se `isChanged()`, depois chama `onDismissRequest`. Sem mexer na animação.
- Convertidas: Paleta e Fonte (`ThemePreviewOverlay`), Fundo, Seletor de cor, Botão rápido, Cartões e vidro, Editar pasta (D-03: Criar pasta continua só com "Criar") e Apps da pasta (D-05).
- Desfazer: `changeManualSeedColor` (cor e fundo; usa o `wallpaperStash` para devolver a imagem que a cor substitui), `changeTheme`, `changeFont` nos Ajustes; `changePalette`/`changeFont` na Home; `onUpdateQuickButtonAppearance`, `renameFolder`, `saveFolderApps` com undo no `HomeViewModel`. Imagem personalizada: o cartão abre o seletor na hora; `setCustomWallpaper` guarda cópia da imagem anterior (`WallpaperUndoStash.keepCopy`) e oferece Desfazer (volta à imagem anterior ou à cor sólida).
- RF-22: `BackgroundSwatches.kt` (`themeSwatches`, `paletteSwatches`, `toneRamp`, `isLightBackground`, `initialPaletteCategory`), `PaletteCategory` em `ThemePalette` (marcada em cada preset pela seção; Frostbite = COLD), UI em `BackgroundColorSections.kt` (Do seu tema, Paletas com chips e LazyRow, Tons + matiz, Vivas, aviso de tema claro). D-07 sem resposta: usado o padrão, tom "variante" (`surfaceVariant`), em `PaletteTone.VARIANT`.
- Strings novas `undo_*` e `settings_background_*` (EN e pt-BR); removidas as órfãs (`settings_appearance_apply`, `settings_cards_glass_done`, `core_common_confirm`, `home_folder_picker_save_count`, `settings_cards_glass_changed`).
- Onboarding: fechar o picker de paleta/fonte sem mudar continua sendo "desistir" (`onDismissAppearancePicker`); mudando, aplica e confirma, como antes.
- Testes novos (passam): `AureoleAutoSaveOverlayTest`, `AutoSaveOverlayParamsTest`, `BackgroundSwatchesTest`, `BackgroundChoiceTest` (reescrito), `AppearanceUndoTest`, `FolderFormModelsTest`, `ThemePreviewStateTest`.
- Ficou faltando (fase 4): testes de UI antigos ainda clicam nos botões que saíram (Apply/Save/Done/Confirm/"Save N apps"/Cancel das sheets convertidas) e não foram atualizados nem rodados: `ColorPickerDialogTest`, `FolderFormDialogTest` (modo edição), `PickThemeSheetsTest`, `PickFontBottomSheetTest`, `FolderAppPickerGridTest`, `FolderAppPickerSearchTest`, `FolderBranchCoverageTest`, `FolderComponentsTest`, `HomeScreenFolderOverlaysTest`, `QuickButtonAppearanceBottomSheetTest`, `SettingsAppearanceDialogsTest`, `SettingsAppearanceControlsTest`, `SettingsScreenTest`, `OnboardingAppearancePickerComponentTest`. Também sem teste: os extensores de ViewModel com undo (`changeManualSeedColor` etc.) e `WallpaperUndoStash.keepCopy`. Screenshot tests de Cartões só ganharam `onCommit = {}`; referências a regenerar.
