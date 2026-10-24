# 🎨 Aureole Design System & Tokens

## 1. Design Tokens (`core/designsystem/`)
- **Prohibited:** Hardcoded numeric dimensions (`dp`/`sp`) or raw hex color values in UI code.
- **Mandatory:** Always use semantic theme tokens:
  - Colors: `AureoleDS.colors`
  - Dimensions: `AureoleDS.dimens` (e.g., `AureoleDS.dimens.medium`, `AureoleDS.dimens.xSmall`, `AureoleDS.dimens.iconMedium`, `AureoleDS.dimens.radiusSmall`)
  - Icons: `AureoleDS.icons`
- Hardcoded `dp` values are only permitted for animations, offsets, elevations, or layout restrictions not covered by semantic spacing/icon/radius scales.

## 2. Structure of `core/designsystem/`
- `theme/`: Theme base config (`AureoleTheme.kt`, `AureoleColor.kt`, `AureoleTypography.kt`, `AureoleDimens.kt`)
- `palette/`: Unified color palettes (`ThemePaletteBase.kt` with documented suppression)
- `icons/`: Unified icon catalog (`AureoleIcons.kt`)
- `utils/`: Visual utilities and preview annotations (`HazeUtils.kt`, `AureolePreview.kt`)
