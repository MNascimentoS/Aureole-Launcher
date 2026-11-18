# Editar foto do fundo: plano

**Base:** branch com `ajustes-sheets-settings` e `ajustes-redesign` concluídas · **Spec:** `SPEC.md` · **Protótipo:** `prototipo.html` ·
**Decisões:** `DECISOES.md` (todas respondidas) · **Como rodar:** `../EXECUTAR-AJUSTES-E-FUNDO.md`

## Regras de execução

1. Por modelo, nesta ordem: fase 1 (Sonnet) → fase 2 (Opus) → fase 3 (tela de bloqueio, Sonnet) → fase 4 (gates, Sonnet + revisão visual Opus).
2. **Gates só na fase 4.** Nas fases 1 a 3: `./gradlew compileDebugKotlin` e os testes da fase com `--tests`. Um
   comando Gradle por chamada, nome por extenso, em segundo plano; nunca editar com build rodando.
3. Commit no fim de cada fase: `feat(editar-foto-fundo): <resumo> (fase N)`. **Sem** `Co-Authored-By`, sem
   "Generated with Claude Code", sem nenhuma linha de atribuição. **Nunca** `git push`.
4. Regras do projeto (`AGENTS.md` + `agents/`): MAX-500 linhas, sem `@Suppress`, `AureoleDS`, strings EN e pt-BR,
   `@AureolePreview` privado, testes unitários junto do código novo.
5. Notas de cada fase em `PROGRESSO.md`; dúvida nova vira D-04+ em `DECISOES.md`, segue com a opção conservadora.

| # | Fase | Modelo | Requisitos | Tempo | Tokens |
|---|---|---|---|---|---|
| 1 | **Ajustes salvos e desenho do fundo** | **Sonnet** | RF-07 a RF-11 | 1,5–2,5 h | 90–150k |
| 2 | **Editor:** enquadrar com gestos, escurecer, desfoque, abrir pela galeria e pelo "Editar foto", salvar + Desfazer | **Opus** | RF-01 a RF-06 | 2,5–4 h | 160–250k |
| 3 | **Tela de bloqueio:** switch, bitmap final, aplicar com `FLAG_LOCK`, avisos e Desfazer | **Sonnet** | RF-13 a RF-17 | 1,5–2 h | 80–130k |
| 4 | **Gates, correções e revisão visual** | **Sonnet** + `visual-reviewer` (**Opus**) | todos | 1–1,5 h | 50–100k |

**Total:** 6,5–10 h e 0,38–0,63 M de tokens.

---

## Fase 1: ajustes salvos e desenho do fundo (Sonnet)

1. **Modelo** `core/data/model/WallpaperAdjustments.kt`: `data class WallpaperAdjustments(zoom: Float = 1f,
   focusX: Float = 0.5f, focusY: Float = 0.5f, rotation: Int = 0 /* 0, 90, 180, 270 */, dim: Float = 0.3f,
   blurDp: Float = 0f)` com `DEFAULT`, `coerced()` (zoom 1..5, foco 0..1, dim 0..0,7, blur 0..25) e `isDefault`.
2. **Persistência** (`SettingsRepository.kt`): `wallpaperAdjustments` em JSON numa chave nova; ausente = `DEFAULT`
   (RF-11). Trocar a foto zera os ajustes. `WallpaperUndoStash` guarda também os ajustes, para o Desfazer.
3. **Geometria pura** `core/data/model/WallpaperGeometry.kt`: `minCoverZoom(imageSize, viewSize, rotation)`,
   `visibleRect(imageSize, viewSize, adjustments): Rect` (fração da imagem visível, sempre dentro dela) e
   `clampFocus(...)`. É o que o desenho (fase 1) e os gestos (fase 2) usam. Testes cobrindo retrato, paisagem,
   foto vertical e horizontal, rotação 90°, zoom máximo e foco nas bordas.
4. **Desenho** (`WallpaperBackground.kt`): com ajustes salvos, desenhar a foto pela `visibleRect` (Canvas/`Modifier.graphicsLayer`
   com escala, translação e rotação; ou `AsyncImage` + transformação, o que for mais leve), escurecer com `dim` no
   lugar do `0.3f` fixo, desfoque com `RenderEffect.createBlurEffect` na API 31+ e, na 28–30, a cópia desfocada (D-02):
   gerar ao salvar em `filesDir/wallpaper_blur.jpg` com um StackBlur em Kotlin puro (`core/image/StackBlur.kt`, com
   teste), invalidada quando a foto ou o `blurDp` mudam. O `hazeSource` continua no mesmo nó.
5. **Escala da imagem** (D-01, padrão "o enquadramento manda"): sem ajustes salvos, a Escala vale como hoje; com
   ajustes salvos, o desenho usa a `visibleRect` e a linha "Escala da imagem" mostra "Enquadramento personalizado"
   (tocar nela continua alternando e zera o enquadramento, mantendo escurecer e desfoque).

**Testes da fase:** `WallpaperAdjustmentsTest` (coerção, JSON ida e volta, ausente = padrão), `WallpaperGeometryTest`,
`StackBlurTest` (raio 0 devolve igual; imagem lisa continua lisa), `SettingsRepository` (trocar foto zera ajustes),
`WallpaperUndoStash` (desfazer volta ajustes).
**Commit:** `feat(editar-foto-fundo): ajustes salvos e desenho do fundo com enquadramento, escurecer e desfoque (fase 1)`

---

## Fase 2: editor (Opus)

1. **Tela** `feature/settings/components/wallpaper/WallpaperEditorOverlay.kt` (+ `model/WallpaperEditorState.kt`),
   tela cheia por cima dos Ajustes. Abre a partir da sheet filha Definir plano de fundo (da `ajustes-redesign`); ao
   fechar, volta para ela pelo `SettingsSheetNav` (registrar o editor como um filho com `returnTo`).
   - Foto em tela cheia desenhada pela mesma `visibleRect` da fase 1, home fantasma por cima (relógio + 4 apps,
     ~40% de opacidade, sem toque) e moldura da tela; fora da moldura, esmaecido (RF-03).
   - Gestos (`detectTransformGestures` + toque duplo): zoom 1x–5x relativo ao `minCoverZoom`, arraste com
     `clampFocus`, toque duplo 1x ↔ 2x no ponto, soltar fora do limite volta com mola. Sem buraco nunca (RF-04).
   - Sheet de ajustes com `AureoleAutoSaveOverlay` (sobe de baixo; título "Editar foto"): abas `AureoleSegmentedControl`
     Enquadrar / Escurecer / Desfoque; Enquadrar com "Centralizar" e "Girar 90°" (D-03); sliders com valor; "Redefinir".
     Na API 28–30 o desfoque da prévia usa uma versão reduzida da foto com StackBlur (rápido) e a cópia final é
     gerada ao salvar (D-02).
2. **Fluxo** (`SettingsRoute.kt`, `SettingsAppearanceDialogs.kt`, `BackgroundTypeCards.kt`): o retorno do
   `PickVisualMedia` salva a foto (como hoje) e abre o editor (RF-01). Com foto aplicada, o cartão "Imagem
   personalizada" mostra miniatura + dois botões de ícone, lápis (Editar foto, preenchido) e imagem com setas (Trocar
   foto), com `contentDescription` e `minTouchTarget` (RF-02). Ícones novos em `AureoleIcons` se faltarem (`Edit` já existe). Os cartões ficam com a mesma altura
   (`IntrinsicSize.Max` na `Row`) e o "Cor sólida" centraliza ícone e texto na vertical (`Arrangement.Center`).
3. **Salvar e Desfazer** (RF-06): qualquer fechamento salva os ajustes se mudaram; `showUndo("Foto do fundo
   ajustada")` volta os ajustes anteriores; se a foto foi trocada nesta abertura, o Desfazer volta a foto anterior
   pelo `WallpaperUndoStash`. Strings novas (EN/pt-BR): "Edit photo"/"Editar foto", "Change photo"/"Trocar foto",
   "Frame"/"Enquadrar", "Dim"/"Escurecer", "Blur"/"Desfoque", "Center"/"Centralizar", "Rotate 90°"/"Girar 90°",
   "Reset"/"Redefinir", "Background photo adjusted"/"Foto do fundo ajustada", "Custom framing"/"Enquadramento personalizado".
4. **Acessibilidade:** sliders com `stateDescription`; ações do TalkBack para zoom (+/−) e mover (cima, baixo,
   esquerda, direita) na área da foto; canhoto espelha a sheet.

**Testes da fase:** `WallpaperEditorState` (pinça, arraste com limite, toque duplo, girar, redefinir, `isChanged`);
UI com Robolectric: escolher foto abre o editor; fechar com mudança chama salvar e mostra Desfazer; sem mudança não
mostra; Desfazer volta os ajustes. Screenshot tests (criar): editor nas três abas, destro e canhoto; cartão com miniatura.
**Commit:** `feat(editar-foto-fundo): editor da foto do fundo com gestos, escurecer e desfoque (fase 2)`

---

## Fase 3: tela de bloqueio (Sonnet)

1. **`SystemWallpaper`** (`core/data/repository/SystemWallpaper.kt`): novos `applyLock(bitmap: Bitmap): Boolean`
   (`setBitmap(bitmap, null, true, FLAG_LOCK)`), `clearLock(): Boolean` (`clear(FLAG_LOCK)`) e
   `canSetWallpaper(): Boolean` (`isWallpaperSupported && isSetWallpaperAllowed`), no mesmo `guarded`. Fake nos testes.
2. **Bitmap final** `core/image/WallpaperRenderer.kt`: `renderLockBitmap(source, adjustments, size): Bitmap` usando a
   `WallpaperGeometry` da fase 1 (recorte da `visibleRect`, rotação, escala para o tamanho da tela em retrato via
   `WindowMetrics`), escurecer como camada preta com `dim` e desfoque com o `StackBlur` (D-04); `renderSolid(color, size)`.
   Gera em `Dispatchers.Default`, aplica em `IO`; guarda o último em `filesDir/lock_wallpaper.png` para o Desfazer.
3. **Preferência** `applyToLockScreen` no `SettingsRepository` (padrão D-05). Ligada: salvar cor sólida, salvar foto,
   salvar ajustes do editor e Desfazer aplicam também na tela de bloqueio. O Desfazer reaplica o `lock_wallpaper`
   anterior (guardar o anterior no `WallpaperUndoStash` antes de sobrescrever).
4. **UI** (`SettingsAppearanceDialogs.kt`/`BackgroundTypeCards.kt`): switch `AureoleSwitchRow` "Usar também na tela de
   bloqueio" abaixo dos cartões de tipo; primeira vez que liga → `AureoleConfirmDialog` (RF-15); desligar →
   `AureoleConfirmDialog` com "Manter como está" / "Voltar ao fundo do sistema" (RF-16); sem permissão de trocar
   papel de parede → desabilitado com o texto explicativo (RF-17).
5. **Strings** (EN/pt-BR): "Also use on lock screen"/"Usar também na tela de bloqueio"; aviso "Android doesn't let
   Aureole keep your current lock screen image, so turning this off won't bring it back." / "O Android não deixa o
   Aureole guardar a imagem atual da tela de bloqueio, então desligar não volta a anterior."; "Use anyway"/"Usar mesmo
   assim"; "Not now"/"Agora não"; "Keep as is"/"Manter como está"; "Back to system wallpaper"/"Voltar ao fundo do
   sistema"; "Your device doesn't allow changing the wallpaper."/"Este aparelho não permite trocar o papel de parede.";
   "Couldn't change the lock screen"/"Não deu para mudar a tela de bloqueio".

**Testes da fase:** `WallpaperRenderer` (tamanho de saída, cor sólida lisa, recorte respeita a `visibleRect`, dim
escurece); `SettingsRepository`/ViewModel com `SystemWallpaper` fake: ligado aplica na cor e na foto; desligado não
aplica; "Voltar ao fundo do sistema" chama `clearLock`; Desfazer reaplica o anterior; `canSetWallpaper = false`
desabilita; falha mostra a mensagem. Screenshot: sheet com o switch ligado, desligado e desabilitado.
**Commit:** `feat(editar-foto-fundo): fundo também na tela de bloqueio (fase 3)`

---

## Fase 4: gates, correções e revisão visual

1. Gates, um por vez, em segundo plano: `detekt --auto-correct` → `lintDebug` → `testDebugUnitTest` →
   `jacocoTestCoverageVerification` → `updateDebugScreenshotTest` → `validateDebugScreenshotTest` → `assembleDebug`.
   Falhou: corrigir na causa, rodar de novo o que falhou e os seguintes.
2. `visual-reviewer` compara os screenshots com `prototipo.html` (igual / quase / diferente) e ajusta o que for diferente.
3. **Commit:** `chore(editar-foto-fundo): gates e screenshots (fase 4)`. Sem atribuição. Sem push.

## Checklist do aparelho (Mateus)

- Escolher foto abre o editor; pinça, arraste e toque duplo; nunca aparece borda vazia; girar e centralizar.
- Escurecer e desfoque mudam a prévia na hora; fechar salva; Desfazer volta; abrir e fechar sem mudar não mostra snackbar.
- "Editar foto" reabre com os ajustes; "Trocar foto" zera os ajustes e oferece desfazer a troca.
- Home e paisagem com o mesmo foco; vidro dos cartões ok; Android 9–11 (emulador) com desfoque pela cópia.
- Tela de bloqueio: ligar o switch (aviso na primeira vez), trocar cor e foto e conferir a tela de bloqueio; editar a
  foto e conferir; Desfazer volta; desligar com "Manter" e com "Voltar ao fundo do sistema". Testar num Samsung
  (One UI) e num Pixel/AOSP, porque cada fabricante trata a tela de bloqueio de um jeito.
- Foto antiga (de antes da atualização) aparece igual a antes.
