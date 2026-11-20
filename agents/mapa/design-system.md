# Mapa: design system

Base: `app/src/main/java/dev/mnascimentos/aureole/core/designsystem/`. Acesso: `AureoleDS.colors | dimens | typography | icons` (`theme/AureoleTheme.kt`). O tema é provido por `AureoleLauncherTheme(config)`. Regras em `agents/aureole_design_system.md`.

## dimens (`theme/AureoleDimens.kt`, tudo em dp; `LocalAureoleDimens`)
| Grupo | Tokens → valor |
|---|---|
| Espaço | zero 0, minimal 2, xxSmall 4, xSmall 8, smallCompact 10, small 12, medium 16, large 24, xLarge 32, xxLarge 40 |
| Layout | minTouchTarget 48, sheetHeaderGap 12, fadingEdge 24, swatchLabelWidth 64, homeTopGrabZone 40 |
| Tamanhos | itemHeightExtraLarge 52, menuRowHeight 56, avatarSize 64, quickActionIconSize 50, sizeLarge 68, sizeExtraLarge 120, controlHeight 44, dragHandleWidth 44, dragHandleHeight 38, clockLineHeightLarge 14, dashLength 6, floatingElevation 8, previewListMaxHeight 300, shortcutTileHeight 92, settingsContentMaxWidth 480, backgroundThumbHeight 84 |
| Ícones | iconXSmall 12, iconSmallCompact 14, iconSmall 16, iconMediumSmall 18, sheetHeaderLogo 20, iconMedium 20, iconLargePlus 22, iconLarge 24, iconXLargeSmall 26, iconXLargeMedium 28, iconXLarge 36, iconHuge 40 |
| Bordas | borderMinimal 1, borderMax 3 |
| Raios | radiusMinimal 4, radiusXSmall 8, radiusSmallCompact 10, radiusSmall 12, radiusMedium 16, radiusMediumLarge 20, radiusLarge 24, radiusAppIcon 12, radiusFull 100 |
- Raios escalam em runtime por `AureoleDimens.forCornerRadius(userDp)` (padrão 20, `SettingsRepository.DEFAULT_CORNER_RADIUS_DP`). Não há tokens em sp aqui.
- Valores repetidos com nomes diferentes: 40 (xxLarge/iconHuge), 12 (small/radiusSmall/radiusAppIcon). Escolha pelo papel, não pelo número.

## colors (`theme/AureoleColor.kt`, `LocalAureoleColors`)
| Token | Papel |
|---|---|
| onSurfaceHigh / Medium / Low | Texto e ícones: ênfase alta, secundária, baixa/desabilitada |
| outline | Contornos e divisores |
| surfaceVariant | Superfície alternativa; semente do tema |
| surface | Superfície base de cartões e vidro |
| background | Fundo da tela |
| error / onError | Erro e conteúdo sobre erro (padrão 0xFFCF6679 / branco) |
| badgeContainer / badgeText | Selo (verde) |
| info | Informativo (azul 0xFF64B5F6) |
- Paleta padrão: Frostbite (`frostbiteColors`, fundo 0xFF12181D). Os 7 primeiros não têm default; `error`...`info` têm.
- Só existe `AureoleDS.colors`; `AureoleTheme` é acessor antigo duplicado.

## typography (`theme/AureoleTypography.kt`; fonte padrão Istok Web, troca via `AvailableFonts` em `Type.kt`)
| Estilo | sp (linha) / peso | Uso |
|---|---|---|
| hugeTitle | 20 (28) Bold | Títulos grandes |
| midTitle | 16 (22.4) Bold | Títulos médios |
| smallTitle | 16 (22.4) Normal | Subtítulos |
| text | 16 (22.4) Normal | Corpo |
| smallText | 14 (18.2) Normal | Texto pequeno (a regra do guia diz 13 sp: ver Conflitos) |
| displayLarge/Medium/Small | 57 / 45 / 36 Bold | Fallbacks Material3 |

## Strings e helpers
- **Não existe `AureoleDS.strings`.** Texto vem de `stringResource(R.string.x)` ou `@StringRes` em models.
- `model/TextResource.kt`: `Res(id, args)` ou `Raw(text)`, para texto que vem de resource ou literal.
- `LocalLeftHanded` (model/), `LocalHazeState` (`utils/HazeUtils.kt`).

## Ícones (`icons/`)
- `AureoleIcons.kt`: classe vazia + `AureoleIcon` + `LocalAureoleIcons`; os ícones ficam em `AureoleIconsPart1..7.kt` (máx. 10 por arquivo, por causa do detekt `TooManyFunctions`); cada ícone é extension `AureoleIcons.PascalCase(modifier, tint)` sobre `R.drawable.ic_aureole_<snake_case>`. 68 ícones, seções por comentário (Base, Set A..D).
- `AureoleIconCatalog.kt`: `AureoleIconCategory` (ACTIONS, WORK, SOCIAL, MEDIA, GAMES, MONEY, HEALTH, TRAVEL, TOOLS) e `AureoleIconEntry(key, category, res)`.
- `AureoleFolderIcons.kt`: 80 ícones `ic_folder_<chave>` com `FolderIconCategory`.

## palette/ e utils/
| Arquivo | O que é |
|---|---|
| palette/ThemePaletteBase.kt | `ThemePalette`, `PaletteCategory` (COLD/COOL/NEUTRAL/WARM), `toLight()`, `getPaletteByName`, `frostbitePalette` |
| palette/ThemePalettePresets.kt | `presetPalette(name, category, "7 hex")` (ordem: High, Medium, Low, outline, variant, surface, background) e a lista `presetPalettes` (100) |
| palette/ThemeColorRef.kt | `ThemeColorRole` (BACKGROUND, SURFACE, VARIANT, OUTLINE, ACCENT) e `ThemeColorRef` (Role ou Fixed), `resolve(colors)`, `toJson`/`parseThemeColorRef` |
| palette/ThemeSeedColor.kt, VividBackgroundMigration.kt | Semente do tema; migração de fundos "Vivid" |
| utils/AureolePreview.kt | `@AureolePreview` (dark) e `@AureoleLandscapePreview` (891x411) |
| utils/ | HazeUtils, FadingEdges (`Modifier.fadingEdges`), GlassBlur, LongPressViewConfiguration, OverlaySheetUtils, SharedTransitionUtils, SystemMotion |
