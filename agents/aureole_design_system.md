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
