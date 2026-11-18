# Progresso: ajustes reorganizados (direção B)

| # | Fase | Modelo | Estimativa | Feita | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|---|
| 1 | Raiz em cartões e conteúdo reagrupado | Sonnet | 1,5–2,5 h | [x] | [x] | [x] | [x] |
| 2 | Áreas como bottom sheet | Opus | 2,5–4 h | [x] | [x] | [x] | [x] |
| 3 | Ajustes do teste no aparelho | Opus | 2–3 h | [x] | [x] | [x] | [x] |
| 4 | Gates, correções e revisão visual | Sonnet + Opus | 1–1,5 h | [x] | [x] | [x] | [x] |

## Notas por fase

### Fase 1
- `SettingsAreaCard` (components/), seções em `feature/settings/sections/` (`AppearanceSections`, `BehaviorSections`,
  `AdvancedSections`, `SettingsSectionGroup`, `HeaderOffsetWheel`), `model/SettingsSummaries.kt`. As três telas
  antigas ficaram finas (título + seção) e somem na fase 2. `AppearanceSections` recebe um 3º parâmetro
  (`AppearanceMenuDialogActions`) porque o dono das sheets é a tela/host.
- Ícones novos `Users` e `Doc` (vetores). Strings de seção e subtítulos EN/pt-BR.
- Verificar atualizações foi para Avançado › Sistema (D-03). Reset/Desinstalar são linhas `requiresHold` (`holdToConfirm`).
- Decisões novas: D-06 (Desinstalar direto, sem sheet de retenção), D-07 (cor do cartão).
- Inventário do IDEIA.md conferido por teste (`SettingsInventoryTest`, 27 itens: 3 cartões + 24 linhas): nada sumiu.
  O "Escala da imagem"/"Restaurar fundo" continuam só com foto própria.
- Ficou faltando: nada da fase. Observação: o `card` do plano não existe como token (D-07); Reset hoje chama
  `onConfirmFactoryReset` direto (`onOpenFactoryResetDialog` não é mais usado pela tela).

### Fase 2
- Estado puro `model/SettingsSheetNav.kt`: `SettingsArea`, `SettingsChildKind` (Paleta, Cores personalizadas, Fonte,
  Fundo, Cartões e vidro, Novidades, Colaboradores), `SettingsSheet` (`Area` / `Child(kind, returnTo)`),
  `SettingsSheetEvent` e `reduceSettingsSheet`. Eventos de fechar levam o nome da sheet, então um fechar atrasado não
  mexe em nada. O estado sobrevive a rotação e à ida às Licenças (`SettingsSheetSaver`, texto).
- `components/SettingsSheetHost.kt`: raiz como `hazeSource`, um fundo desfocado só para todas as sheets (a troca não
  pisca), área em `AureoleEditOverlay` (título centralizado, sem voltar, sem logo), mini-home só na Aparência (D-04).
  Os filhos ficam em `components/SettingsChildSheets.kt` (`SettingsChildSheet`, que substitui `AppearanceDialogs`);
  Colaboradores virou `ContributorsBottomSheet` (lista centralizada). Cores personalizadas agora também troca no lugar.
- Rolagem: guardada por `rememberSaveable` por abertura de área e devolvida depois do layout (`RestoreAreaScroll`:
  a primeira medida na volta permite menos rolagem e cortava o valor). A sheet volta no repouso mesmo se estava
  expandida antes do filho.
- Design system: `LocalOverlaySheetMaxWidth` e `LocalOverlayExternalBackdrop` (`core/designsystem/model/
  OverlayHostLocals.kt`); `SheetSurface` usa `widthIn(max)` e o layout centraliza a sheet. O host fornece
  `settingsContentMaxWidth` para todas as sheets dele (área e filhos), em paisagem ficam estreitas e centralizadas.
- Rotas: só `Root` e `Licenses` (tela cheia, D-02). Apagadas as telas finas Appearance/Behavior/Advanced/Contributors,
  `AppearanceDialogFlags`, `AppearanceDialogs`, `SettingsScreenActions.onNavigateToLicenses` e as referências de
  screenshot das telas que sumiram. Voltar das Licenças reabre o Avançado na mesma rolagem (D-10).
- Avançado usa a mesma regra do Personalizar (repouso 60%, rolagem interna, expande até o conteúdo, elástico).
- Voltar: a sheet aberta trata o voltar; filho fecha (salvando) e a área volta; na área, fecha para a raiz; na raiz,
  sai. O `BackHandler` do host só pega um segundo voltar enquanto a área desce. Canhoto: o host fornece
  `LocalLeftHanded`. TalkBack: cartão com `onClickLabel` "open panel"/"abrir painel".
- Entradas (Personalizar, pasta, pilha de widgets) abrem a `SettingsActivity`, que começa na raiz: nada mudou.
- Testes: `SettingsSheetNavTest` (JVM, todos os caminhos + salvar/ler), `SettingsSheetHostTest` (Robolectric: Aparência
  com prévia; Comportamento/Avançado sem; Paleta troca e volta; rolagem preservada; Cores personalizadas; Colaboradores
  em sheet; Novidades; Licenças tela cheia e volta; voltar filho → área → raiz → sai; rótulo do TalkBack),
  `SettingsAppearanceControlsTest` adaptado a `SettingsChildSheet`, `SettingsInventoryTest` com um teste a mais que
  abre as três sheets pelo host. Screenshot tests novos (não rodados): `SettingsSheetsScreenshotTest` (Aparência,
  Comportamento, Avançado, Avançado rolado até a Zona de cuidado, Avançado canhoto, Colaboradores) e Avançado em
  paisagem; raiz retrato/paisagem mantidas.
- Inventário do IDEIA.md: os 27 itens continuam (teste de seções + teste pelo host).
- Ficou faltando (fase 3):
  - Rodar `updateDebugScreenshotTest`/`validate` (referências novas; as das telas apagadas já saíram).
  - Código morto que ficou: `FactoryResetBottomSheet`, `UninstallBottomSheet` (têm screenshot tests próprios),
    `onOpenFactoryResetDialog`/`showFactoryResetDialog`, e agora também `SettingsDialog.COLOR_PICKER`/
    `showColorPickerDialog`/`onOpenColorPickerDialog`/`onDismissColorPickerDialog` (Cores personalizadas não passa mais
    pelo ViewModel; o diálogo antigo ainda é desenhado em `SettingsScreen` se a flag ligar, e tem teste).
  - D-08 da `ajustes-sheets-settings`: aqui (e no Personalizar, que usa o mesmo mecanismo) a volta filho → área já é
    em sequência, porque o filho só avisa que fechou depois de descer. A ida área → filho é simultânea (uma desce
    enquanto a outra sobe), como no Personalizar.
  - O toque no cabeçalho da raiz (notas da versão) continua com a sheet própria da raiz, com o fundo preto de antes
    (não passa pelo host).


### Fase 3
- RF-12: saiu "Cores personalizadas" da Aparência e todo o caminho morto: `ColorPickerDialog` (+ teste e screenshots),
  `SettingsChildKind.CUSTOM_COLORS`, `SettingsDialog.COLOR_PICKER`, `showColorPickerDialog`,
  `onOpen/onDismissColorPickerDialog`, `onSelectManualSeedColor`, `AppearanceMenuDialogActions.onShowCustomColorsDialog`,
  `formatHex` e as strings órfãs EN/pt-BR (`settings_appearance_custom_colors`, `..._color_picker_primary`,
  `..._colors_recommended`, `..._color_picker_custom_hue`, `undo_color_changed`). A cor base segue em Definir plano de
  fundo → Cor sólida.
- Código morto da fase 2 removido: `FactoryResetBottomSheet`, `UninstallBottomSheet` (com teste, screenshots e PNGs de
  referência), `SettingsDialog.FACTORY_RESET`, `showFactoryResetDialog`, `onOpen/onDismissFactoryResetDialog` e as
  strings `settings_advanced_reset_dialog_*`, `settings_advanced_uninstall_dialog_title`, `settings_advanced_contact_us`.
  `AppLinks.SUPPORT_EMAIL`/`mailto` ficaram (têm teste próprio e são a lista de contatos do app).
- RF-13: `AureoleMiniHome(themedIcons)`. A regra do ícone temático virou `ThemedIconStyle`/`themedIconStyleOf`
  (`composable/appitem/model/`), usada pelo `AureoleAppIcon` da home e pela mini-home (`miniHomeIconFill`, função pura).
  A prévia da Aparência lê `isThemedAppIconsEnabled` ao vivo.
- RF-14: `SettingsSheet.Area(area, inner = PALETTE | FONT)` e `SettingsSheetEvent.OpenInner`; voltar com picker aberto
  volta para a lista (estado salvo como `area:APPEARANCE:PALETTE`). No host, a mesma sheet troca o conteúdo
  (`AreaSheetContentSwitch`, `AnimatedContent` deslizando) e o título (`settingsSheetTitle`). O que se toca fica num
  rascunho (`AppearanceDraft`, salvo na rotação) desenhado ao vivo na sheet e na prévia (`AppearanceDraftTheme`);
  voltar do picker salva só aquela escolha e mantém a sheet; tocar fora/arrastar salva o resto e fecha
  (`saveOnLeaving`), com o Desfazer de sempre (`changeTheme`/`changeFont`). Conteúdo reaproveitado:
  `PalettePickerGrid`/`FontPickerGrid` extraídos de `PickPaletteBottomSheet`/`PickFontBottomSheet` (que seguem no
  Personalizar e no onboarding, sem mudança). Paleta e Fonte saíram de `SettingsChildKind`.
- RF-15: `SettingsSectionGroup(isFirst)`: `large` acima de todo rótulo menos o primeiro (Aparência, Comportamento,
  Avançado); o divisor antes da Zona de cuidado também ganhou `large` em cima.
- RF-16: `overlayLayoutFor(width, height, hasPreview)` e `sideBySideColumns` (`core/designsystem/model/OverlayLayout.kt`)
  + `SideBySidePreviewLayout`: em paisagem com prévia, prévia centralizada à esquerda e sheet à direita (até
  `LocalOverlaySheetMaxWidth`, ou `settingsContentMaxWidth`), repouso até 90% da altura, respeitando o recorte seguro.
  Vale para todo `AureoleEditOverlay` com prévia: Personalizar → Paleta, Fonte, Fundo, Botão rápido, Cartões e vidro
  passam por ele (conferido no código). Retrato igual. Decisão nova: D-11 (sheet presa embaixo, não centralizada).
- Testes: `SettingsSheetNavTest` (caminhos internos, título, salvar/ler), `AppearanceDraftTest`, `OverlayLayoutTest`,
  `MiniHomeIconFillTest`, `SettingsInventoryTest` com 26, `SettingsSheetHostTest` (Paleta dentro da sheet com a prévia;
  voltar salva e mantém a sheet; tocar fora na Fonte salva e fecha; Cores personalizadas sumiu), testes antigos
  ajustados. 15 classes / 92 testes verdes (`--tests`). Screenshot tests novos (só fontes, não rodados): Aparência com
  Paleta e com Fonte internas, Aparência com ícones temáticos, Avançado com o respiro, Aparência e Paleta em paisagem,
  mini-home com ícones temáticos.
- Inventário: 26 itens conferidos (teste de seções + host).
- Ficou faltando (fase 4): rodar `updateDebugScreenshotTest`/`validate` (referências novas e as que mudam com o respiro,
  a mini-home e a paisagem de todas as sheets com prévia); detekt/lint (o `SettingsSheetHost` já tinha 7 parâmetros
  desde a fase 2: conferir `LongParameterList`); revisão visual de D-11.

### Fase 4
- Gates (um comando por chamada, em segundo plano; nenhum acima de 1,5 min):

| Gate | Resultado | Tempo |
|---|---|---|
| `detekt --auto-correct` | passou (0 issues) | 15 s |
| `lintDebug` | passou (0 erros, 0 avisos) | 36 s |
| `testDebugUnitTest` | passou | 34 s |
| `jacocoTestCoverageVerification` | passou (branches de lógica 76,37%) | 2 s |
| `updateDebugScreenshotTest` | passou | 1 min 20 s |
| `validateDebugScreenshotTest` | passou | 1 min 20 s |
| `assembleDebug` | passou | 2 s |

- Corrigido no detekt: `SettingsSheetHostParams` (host e `OpenArea` com menos parâmetros), `reduceSettingsSheet` dividido
  em funções, `SettingsSheetSaver.kt` separado, `AppearanceMenuDialogActions.kt` renomeado, constante `GAPS_ACROSS`,
  formatação automática de imports e argumentos.
- Corrigido no lint (D-13): `LocalOverlaySheetMaxWidth` e `LocalOverlayExternalBackdrop` saíram; `OverlayHostSpec`
  (`maxSheetWidth`, `hasExternalBackdrop`) entra por `AureoleEditOverlayConfig.host` e pelas sheets filhas das Ajustes
  (`MenuSheetParams`, `AutoSaveOverlayParams`, `SetBackgroundParams`, `CardsGlassParams`, `AreaSheetSpec`). O padrão é
  neutro: Home e Personalizar não mudaram. `lint.xml` intacto.
- Visual (amostragem): paisagem da Aparência/Paleta/raiz com prévia à esquerda e sheet à direita, legíveis. A "ponta
  arredondada" no topo é a raiz atrás da sheet (o screenshot não desenha o desfoque/escurecimento) e o corte embaixo é a
  lista rolando dentro da sheet presa embaixo com 90% (D-11): não é bug de layout. Em retrato a prévia da Paleta aparece
  sobre o logo da raiz pelo mesmo motivo. Revisão formal fica com o `visual-reviewer`.
- Ficou faltando: revisão visual formal; checklist do aparelho; código morto conhecido (`FactoryResetBottomSheet`,
  `UninstallBottomSheet`, `COLOR_PICKER`, `AppLinks.SUPPORT_EMAIL`).

### Complemento da fase 4 (revisão visual)
- Avançado em paisagem: `OverlayHostSpec.isTallInLandscape` (ligado em `settingsOverlayHost()`) + `sheetRestFraction` (`OverlayLayout.kt`): em paisagem, sheet sem prévia das Ajustes descansa até 90%. Retrato e Personalizar iguais (padrão desligado).
- Prévia pulando na Fonte: `AreaSheetContentSwitch` passou a `fillMaxSize`, então lista, Paleta e Fonte dão a mesma altura à sheet da Aparência. Teste Robolectric (`SettingsSheetHostTest`: prévia na mesma posição na lista, Paleta e Fonte) e referência da Fonte regravada.
- Canhoto: `SheetSectionLabel` espelha (LeftHandMirror + alinhamento explícito, `TextDirection.Content` como nas linhas de rádio); sem CompositionLocal novo. `SheetSectionLabelTest`.
- Colaboradores: bloco centralizado (`IntrinsicSize.Max`) com linhas à esquerda; logo do canto removido só nesta sheet (`MenuSheetParams.showHeaderAction`).
- Testes: `OverlayLayoutTest` (+3, `sheetRestFraction`), `SheetSectionLabelTest` (2), `SettingsSheetHostTest` (+1).

| Gate | Resultado | Tempo |
|---|---|---|
| `detekt --auto-correct` | passou (0 issues) | 15 s |
| `lintDebug` | passou | 39 s |
| `testDebugUnitTest` | passou | 29 s |
| `jacocoTestCoverageVerification` | passou | 2 s |
| `updateDebugScreenshotTest` | passou | 1 min 20 s |
| `validateDebugScreenshotTest` | passou | 1 min 22 s |
| `assembleDebug` | passou | 2 s |

### Polimento futuro
- Divisor antes da Zona de cuidado.
- Estilo dos rótulos de Paleta e Fonte.
- Ícones repetidos nas linhas.
- Chevron em Info e Novidades.
- Screenshots extras.
- Sheet centralizada na vertical em paisagem (D-11).
