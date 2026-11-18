# Progresso: botao-rapido

Gates e revisão visual: na fase G do lote (`../EXECUTAR-LOTE-17-11.md`).

| # | Fase | Modelo | Estimativa | Feita |
|---|---|---|---|---|
| 1 | Modelo e migração | Sonnet | 45 min–1 h | [x] |
| 2 | Sheet do botão rápido | Sonnet | 1,5–2 h | [x] |

## Notas por fase

### Fase 1 (RF-07, RF-04 JSON)
Só `compileDebugKotlin` e os testes da fase (JSON do modelo, ColorTarget, Sections, BottomSheet, GridContent, catálogo)
rodaram, todos verdes.
- `QuickButtonAppearance`: `icon: String` (chave do `AureoleIconCatalog`; `iconRes` derivado), `backgroundColor`/
  `iconColor`/`strokeColor: ThemeColorRef`, `label: String?`, `showLabel = false` (D-01).
- Leitura do JSON antigo em `core/data/model/QuickButtonJsonAdapters.kt` (`@JsonAdapter` nos campos): ARGB vira `Fixed`;
  fundo 0 ou 0xFF455A64 (antigo "padrão") vira `Role(VARIANT)`, que é o que a home já desenhava; nome do enum
  (`STAR`) resolve por `resolveKey`; valor inválido cai no padrão (ícone Adicionar, cores brancas, fundo variante).
- Home (`QuickButtonGridItem`) resolve as cores com `AureoleDS.colors`. A sheet atual foi só adaptada (grade de 28
  ícones grava `actionKey`, swatches gravam `Fixed`); a fase 2 a substitui.
- `label`/`showLabel` ficaram para a fase 2.

### Fase 2 (RF-01 a RF-06, RF-02b, RF-08, RF-09)
Só `compileDebugKotlin` e os testes da fase (`*quickbutton*`, JSON do modelo, `*ContainerFolder*`) rodaram, todos verdes.
- Sheet (`QuickButtonAppearanceBottomSheet`): prévia do botão com o nome em pílula embaixo (reusa `FolderNamePill`, sem
  teclado ao abrir), sem logo (`onSettingsClick` nulo). Salvar normaliza o nome (trim; vazio vira null).
- Corpo (`QuickButtonAppearanceSections`): segmentado Ícone | Cores; Ícone = `AureoleIconCatalogGrid`; Cores = segmentado
  Cor do ícone | Cor do fundo + `AureoleThemeColorPicker`, Borda (com o mesmo seletor) e "Mostrar nome na home".
- Contraste (RF-06): `isLowContrast` (< 3:1, JVM puro) em `model/QuickButtonSheetModels.kt`; a prévia mostra o aviso e o
  ícone ganha contorno (`QuickButtonGridItem(outlineIcon)`; cópia maior em cor legível atrás do ícone).
- Home: `contentDescription` do botão = nome (ou app/ação do toque); com `showLabel` o nome aparece embaixo (grade e
  container, via `ContainerFolderLabel`). Corrigido o `QuickButtonGridItem` da fase 1 (`appearance.iconRes`).
- Removido o código morto da sheet antiga: grade de 28 ícones, swatches, `QuickButtonIconOptions`, colunas e a altura da
  caixa de prévia. Paisagem (RF-08) vem do overlay de auto-save, como na pasta.
- Testes: `QuickButtonSheetModelsTest` (contraste, nome, índices), `QuickButtonAppearanceSectionsTest`,
  `QuickButtonAppearanceBottomSheetTest` (aviso, nome mostrado/escondido, salvar, sem mudança). Screenshots novos
  (aba Cores, nome mostrado) escritos, não compilados nem rodados aqui.

## Ficou faltando
- Fase G: gerar referências de screenshot (aba Ícone, aba Cores, nome mostrado/escondido, cor do tema em dois temas,
  paisagem) e fazer a revisão visual; o source set de screenshot não foi compilado aqui.
- Fase G: conferir a altura da grade do catálogo na sheet e o contorno do ícone na prévia (tamanho do contorno é palpite).
- Teste de "todo ícone em exatamente uma categoria" já existe no catálogo (`AureoleIconCatalogTest`, pasta-form).

## Revisão visual 17/11 (fase G, 2ª passada)
- **Bug real**: Borda e "Mostrar nome na home" eram checkbox caseiro; agora são `AureoleSwitchRow` (o `AppearanceToggleRow` saiu).
- **Bug real**: ícone grande demais na prévia (36 dp num tile de 72 dp). `QuickButtonGridItemStyle.iconSize`: a prévia da sheet usa 24 dp (~1/3, como a pasta); a home segue com 36 dp.
- RF-06: screenshot novo do aviso de contraste (`QuickButtonAppearanceLowContrastScreenshotTest`). Aba Cores com altura maior no teste para mostrar os dois switches.

### Ficou faltando
- O tamanho do ícone na home (36 dp fixo) não foi mexido; se a home também deve ficar em ~1/3 da célula, é uma decisão à parte.
