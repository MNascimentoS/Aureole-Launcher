# 🎨 Aureole Design System & Tokens

## 1. Design Tokens (`core/designsystem/`)
- **Prohibited:** Hardcoded numeric dimensions (`dp`/`sp`) or raw hex color values in UI code.
- **Mandatory:** Always use semantic theme tokens:
  - Colors: `AureoleDS.colors`
  - Dimensions: `AureoleDS.dimens` (e.g., `AureoleDS.dimens.medium`, `AureoleDS.dimens.xSmall`, `AureoleDS.dimens.iconMedium`, `AureoleDS.dimens.radiusSmall`)
  - Icons: `AureoleDS.icons`
- Hardcoded `dp` values are only permitted for animations, offsets, elevations, or layout restrictions not covered by semantic spacing/icon/radius scales.
- Layout restrictions without a token live as named constants in `model/` files (`*Metrics.kt`, `*Configs.kt`, `*Spec.kt`), never inline in a composable. Preview-only sizes and colors inside `@AureolePreview` functions and the colors that are data (e.g. the swatch list in `ColorPickerModels.kt`) are allowed.
- Before adding a constant, check `mapa/design-system.md`: if a token already has the same value and role, use the token.

## 2. Structure of `core/designsystem/`
- `theme/`: Theme base config (`AureoleTheme.kt`, `AureoleColor.kt`, `AureoleTypography.kt`, `AureoleDimens.kt`)
- `palette/`: Unified color palettes (`ThemePaletteBase.kt`, `ThemePalettePresets.kt`: each preset is one `presetPalette(name, category, "hex x7")` line; no `@Suppress`)
- `icons/`: Unified icon catalog (`AureoleIcons.kt`)
- `utils/`: Visual utilities and preview annotations (`HazeUtils.kt`, `AureolePreview.kt`)

## 3. Tipografia: tamanhos mínimos
- `SmallText` tem **13 sp** (antes 12). Texto pequeno usa o token, nunca `fontSize` próprio; mudar o tamanho é mudar o token, que vale para o app todo.

## 4. Vetores e PNG
- Preferir vector drawables. Se o `lintDebug` acusar `VectorPath` ou o vetor não carregar no Robolectric (o parser do Compose acusa "Unknown command", por exemplo com números em notação científica como `6.59706e-05`), trocar por PNG em `res/drawable-nodpi/`.
- Nunca resolver com `tools:ignore`.

## 5. Fontes e cores de componente vêm do launcher
- **Princípio:** fonte e cor de um componente (relógio, e os que migrarem) podem diferir das do launcher, mas sempre saem do mesmo catálogo de fontes (`AureoleFontCatalog`) e das mesmas paletas, escolhidas pelos mesmos seletores (`PickFontBottomSheet`, `PickPaletteBottomSheet`). Sem lista de fontes ou seletor de cor próprios.
- **Fonte nova entra pelo catálogo:** TTF no peso Regular em `res/font/<nome>_regular.ttf` (estático; variável só se o google/fonts não tiver estático, fixada em wght 400 com `variableFamily`), licença OFL, nome em `OpenSourceLicenses.kt` e `NOTICE`, chave em `AureoleFontCatalog` e família em `AvailableFonts`. A chave é persistida: nunca renomear.
