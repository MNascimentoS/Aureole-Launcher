# Progresso: editar foto do fundo

| # | Fase | Modelo | Estimativa | Feita | Gates | Visual | Aparelho |
|---|---|---|---|---|---|---|---|
| 1 | Ajustes salvos e desenho do fundo | Sonnet | 1,5–2,5 h | [x] | [x] | [x] | [x] |
| 2 | Editor: gestos, escurecer, desfoque, fluxo, salvar + Desfazer | Opus | 2,5–4 h | [x] | [x] | [x] | [x] |
| 3 | Tela de bloqueio: switch, bitmap final, FLAG_LOCK, avisos, Desfazer | Sonnet | 1,5–2 h | [x] | [x] | [x] | [x] |
| 4 | Gates, correções e revisão visual | Sonnet + Opus | 1–1,5 h | [x] | [x] | [x] | [x] |

## Notas por fase

### Fase 1 (feita)
- `WallpaperAdjustments` (JSON escrito à mão, sem org.json, para testar em JVM puro), `WallpaperGeometry` (zoom 1 = cobre a tela;
  foco = centro da parte visível, em fração da foto girada), `StackBlur` (2 passadas de caixa por eixo, custo independente do raio).
- Persistência em `SettingsRepository.wallpaperAdjustments` (ausente = DEFAULT, RF-11); trocar/remover a foto zera ajustes e cópia
  desfocada; `WallpaperUndoStash` guarda os ajustes em `wallpaper_adjustments_undo.json`.
- Desenho: com `hasFraming` usa `FramedWallpaperImage` (Canvas pela `visibleRect`); sem enquadramento, `AsyncImage` com a Escala como hoje.
  Dim salvo no lugar do 0.3f; desfoque `BlurEffect` (API 31+) ou cópia `wallpaper_blur.jpg` (API 28–30, `BlurredWallpaperCache`).
- Decisão de detalhe (sem D novo): "Enquadramento personalizado" aparece só quando zoom/foco/rotação mudam (`hasFraming`), não quando só
  escurecer/desfoque mudam; assim a linha não fica presa. Tocar nela alterna a Escala e zera o enquadramento mantendo dim/desfoque.
- Ficou faltando: a cópia desfocada é gerada no primeiro desenho após salvar (em Default), não dentro do "salvar" do editor (fase 2 pode
  chamar `BlurredWallpaperCache.obtain`). Sem teste de tela do `FramedWallpaperImage` (screenshot fica para a fase 4/2); não verificado em aparelho.

### Fase 2 (feita)
- Editor `feature/settings/components/wallpaper/`: `WallpaperEditorOverlay` (estado + mola + fechar), `WallpaperEditorStage`
  (foto pela `visibleRect` com `drawFramedWallpaper`, extraído do `FramedWallpaperImage`; desfoque `BlurEffect` na 31+;
  escurecer; home fantasma a 40%; moldura tracejada com faixa esmaecida fora; etiqueta de zoom), `WallpaperEditorGestures`
  (arraste/pinça com touch slop + fim do gesto; toque duplo), `WallpaperEditorControls` (abas `AureoleSegmentedControl`,
  Centralizar/Girar 90°, sliders com valor, Redefinir), `WallpaperEditorPainters` (foto pelo Coil, mesma requisição da home;
  na API 28–30 cópia pequena desfocada por `WallpaperPreviewBlur` + `StackBlur`, `core/image/PreviewBlurMath.kt`).
- Lógica pura em `model/`: `WallpaperEditorState` (abas, `isChanged`, dim/blur arredondados, centralizar, girar 90° em volta
  do meio, redefinir, `Saver` para rotação), `WallpaperEditorGestures` (zoom no ponto, arraste com `clampFocus`, pinça com
  `overshoot` além de 5x, toque duplo 1x↔2x, passos do TalkBack, `lerpAdjustments` da mola), `WallpaperEditorClose`
  (o que salvar e qual Desfazer), `WallpaperEditorModels`.
- Navegação: `SettingsChildKind.WALLPAPER_EDITOR` e `SettingsSheet.Child.parent` (só o editor volta para a sheet de onde
  abriu: Fundo → editor → Fundo → Aparência); saver com o `parent`.
- Fluxo: `setCustomWallpaper` guarda `pendingPhotoSwap` e liga `isWallpaperEditorRequested`; o host abre o editor
  (`OpenEditorOnRequest`) e avisa `onWallpaperEditorOpened`. Fechar chama `finishWallpaperEditor(before, after)`: salva se
  mudou, Desfazer dos ajustes ou da foto anterior (D-06), e gera a cópia desfocada da API 28–30 ao salvar
  (`SettingsRepository.prepareBlurredWallpaper` → `BlurredWallpaperCache.obtain`), fechando o "ficou faltando" da fase 1.
- Cartão "Imagem personalizada" com foto: miniatura (enquadramento + escurecer), botões redondos de 40 dp em área de 48 dp,
  Editar foto (lápis, preenchido) e Trocar foto (ícone novo `ImageSwap`); cartões com a mesma altura (`IntrinsicSize.Max`),
  "Cor sólida" centralizado. Sem foto, tocar no cartão abre a galeria como antes; com foto, só seleciona.
- Design system: `AureoleEditOverlayConfig.dismissOnTapOutside` / `AutoSaveOverlayParams.dismissOnTapOutside` (o toque em
  volta da sheet chega na foto) e `AutoSaveOverlayParams.overlayState` (a foto some junto com a sheet); token
  `backgroundThumbHeight`; ícones `Crosshair`, `RotateClockwise`, `ImageSwap` (vetores); limites públicos `WALLPAPER_*`.
- Acessibilidade: sliders com `contentDescription` + `stateDescription` ("30%", "12 dp"); área da foto com zoom como estado
  e ações Aproximar/Afastar/Mover (cima, baixo, esquerda, direita); canhoto espelha botões, linha do slider e Redefinir
  (o slider fica com 0 à esquerda).
- Strings EN/pt-BR novas (`settings_wallpaper_*`, `undo_wallpaper_adjusted`). Decisões novas: D-06, D-07.
- Testes (`--tests`, 13 classes / 107 testes verdes): `WallpaperEditorStateTest` (14), `WallpaperEditorCloseTest`,
  `WallpaperEditorNavTest`, `PreviewBlurMathTest`, `WallpaperEditorFlowTest` (Robolectric, ViewModel trocado por fake com
  `UndoController` real: escolher foto abre o editor; fechar com mudança salva e mostra Desfazer; sem mudança não mostra;
  Desfazer volta os ajustes), mais os existentes da navegação, host e sheet de fundo. Screenshot tests novos (só fontes,
  compilados): `WallpaperEditorScreenshotTest` (3 abas × destro/canhoto, cartão com miniatura destro/canhoto).
- Ficou faltando (fase 4/aparelho): rodar `updateDebugScreenshotTest`/`validate` (novos e o `SetBackgroundCustomImage`, que
  muda com os cartões de mesma altura); detekt/lint; `WallpaperPreviewBlur` (API 28–30) sem teste nem conferência em
  emulador; nos screenshots a foto é um fundo liso (sem arquivo); a troca Fundo → editor é seca (a sheet de Fundo sai sem
  deslizar, a foto entra em fade); conferir gestos no aparelho.

### Fase 3 (feita)
- `SystemWallpaper` ganhou `applyLock` (`setBitmap(..., FLAG_LOCK)`), `clearLock` e `canSetWallpaper`, no mesmo `guarded`.
- `core/image/WallpaperRenderer`: `renderSolid` e `renderLockBitmap` (mesma `WallpaperGeometry` da home, rotação, desfoque com `StackBlur`
  a 1/4 da resolução, escurecer como camada preta). Feito em matemática de pixels, sem `Canvas`, porque o Canvas do Robolectric NATIVE
  não desenhou nos testes (drawColor/drawBitmap com Rect deram pixel 0); assim os testes conferem pixels de verdade. A "Escala da imagem"
  (Fit/Fill) é respeitada enquanto não há enquadramento, como na home.
- `core/data/repository/LockScreenWallpaper` (`settingsRepository.lockScreen`): `applyCurrent` (gera em Default, aplica em IO, `Mutex`),
  `applyPrevious` (Desfazer), `clear` (Voltar ao fundo do sistema), `canSet`. Tamanho = tela em retrato (`maximumWindowMetrics` na API 30+,
  `displayMetrics` abaixo). Cor sólida = fundo da paleta atual (claro/escuro do sistema). `lock_wallpaper.png` = último aplicado; o anterior vai
  para `lock_wallpaper_undo.png` no `WallpaperUndoStash` (`keepLockImage`/`takeLockImageBack`, apagado no `discard`).
- Preferências `applyToLockScreen` (padrão desligado, D-05) e `isLockScreenNoticeSeen`. Aplica ao salvar cor sólida, ao fechar o editor
  (foto nova ou ajustes), ao "Restaurar fundo" e no Desfazer de cada um (`applyLockScreen(undo)` em `SettingsViewModelLockScreenExt`).
  Ligar aplica o fundo atual na hora. A foto recém-escolhida só vai para a tela de bloqueio quando o editor fecha (uma aplicação só, o
  Desfazer volta o fundo certo). Falha: `errorMessage` "Couldn't change the lock screen".
- UI: `AureoleSwitchRow` (novo `enabled`) abaixo dos cartões de tipo; `AureoleConfirmDialog` no primeiro uso (Usar mesmo assim / Agora não)
  e ao desligar (principal "Manter como está", secundário "Voltar ao fundo do sistema"). Para distinguir o secundário de arrastar para
  fechar, `ConfirmDialogParams.onCancel`/`ConfirmOverlayParams.onSecondary` (novos, opcionais). Sem permissão: desligado, desabilitado e
  com o texto explicativo. Os diálogos ficam ao lado da sheet (Box), não dentro da coluna rolável.
- Testes (75 verdes nas classes tocadas): `WallpaperRendererTest` (10), `LockScreenWallpaperTest` (Robolectric, `SystemWallpaper` fake, sem
  ViewModel: ligado/desligado, cor e foto, falha, Desfazer, `clearLock`, `canSetWallpaper`), `LockScreenOptionTest` (JVM puro),
  `BackgroundLockScreenRowTest` (Compose: avisos, Manter/Voltar, desabilitado). Screenshots novos (só fontes, compilados):
  `SettingsLockScreenScreenshotTest` (ligado, desligado, indisponível).
- Ficou faltando: screenshots não gerados/validados (fase 4; o `SetBackground*` existente muda com a nova linha); detekt/lint não rodados;
  trocar de paleta (Pick palette) não atualiza a tela de bloqueio (fora do RF-13); Desfazer sem imagem anterior redesenha o fundo restaurado
  em vez de limpar; não verificado em aparelho (Samsung/Pixel), nem o desempenho do renderer em 1080x2400.

### Fase 4 (gates feitos; revisão visual pendente)
| Gate | Resultado | Tempo |
|---|---|---|
| detekt --auto-correct | falhou (15) na 1ª; corrigido; 2ª = 0 | 18 s / 17 s |
| lintDebug | falhou (ComposeMultipleContentEmitters em CustomWallpaper); corrigido; passou | 42 s / 35 s |
| testDebugUnitTest | passou | 29 s |
| jacocoTestCoverageVerification | passou (branches de lógica 75,44%) | 2 s |
| updateDebugScreenshotTest | passou | 80 s |
| validateDebugScreenshotTest | passou | 84 s |
| assembleDebug | passou | 4 s |

- Correções na causa: imports reordenados (autocorrect); `OverlayContentBox` encurtado (`sheetBoundsFor` em `OverlaySheetBoundsFactory.kt`);
  `StackBlur` condição simplificada; `WallpaperBackground` sem `isCustomWallpaperSet` (a HomeScreen passa o caminho só quando há foto);
  `SettingsSheetNav` dividido (títulos em `SettingsSheetTitles.kt`); constantes `NUMBER`, `PNG_QUALITY`, `PARENT_INDEX`;
  `if/else` do `rememberPreviewBlurPainter` virou `takeIf/let`; teste de geometria sem laços aninhados; `CustomWallpaper` com Box raiz.
- Screenshots regravados: SetBackground* (linha do switch) e novos de editor (3 abas, destro/canhoto), cartão com miniatura, tela de bloqueio.
  Amostragem sem corte, sobreposição ou tela quebrada. O palco do editor e a miniatura do cartão aparecem escuros (foto de teste sem pixels).
- Ficou faltando: `WallpaperPreviewBlur` (API 28–30) sem teste; revisão visual formal; checklist do aparelho.

### Complemento da fase 4 (correções da revisão visual)
- Canhoto: valor do slider ("8 dp", "30%") com `TextDirection.Ltr`, sem inverter no espelho (`WallpaperEditorControlsTest`).
- Cartão "Imagem personalizada" sem foto: ícone novo `AureoleIcons.Image` (galeria); o lápis fica só no botão Editar foto.
- Etiqueta de zoom no editor com margem `large` (afasta do canto arredondado da moldura).

| Gate | Resultado | Tempo |
|---|---|---|
| detekt --auto-correct | falhou (1, linha longa no teste novo); corrigido; 2ª = 0 | 17 s / 17 s |
| lintDebug | passou | 41 s |
| testDebugUnitTest | passou (inclui WallpaperEditorControlsTest) | 30 s |
| jacocoTestCoverageVerification | passou | 2 s |
| updateDebugScreenshotTest | passou | 83 s |
| validateDebugScreenshotTest | passou | 83 s |
| assembleDebug | passou | 1 s |

#### Polimento futuro
- Subtítulo permanente na linha do switch "Usar também na tela de bloqueio" (item 3 da revisão visual, não feito).
