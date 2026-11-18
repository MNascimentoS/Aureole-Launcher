# polimento-ui: plano

**Base:** `release/0.9.44` · **Spec:** `SPEC.md` · **Decisões:** `DECISOES.md` (todas respondidas) ·
**Protótipo:** `../prototipo-lote-17-11.html` · **Como rodar:** `../EXECUTAR-LOTE-17-11.md`

## Regras de execução

1. Fases na ordem da tabela, cada uma num subagente com o modelo indicado, uma de cada vez.
2. **Nenhum gate nesta spec.** Os gates rodam uma vez só, na fase final do lote (`../EXECUTAR-LOTE-17-11.md`, fase G).
   Nas fases daqui: só `./gradlew compileDebugKotlin` e os testes da própria fase com `--tests`. Um comando Gradle por
   chamada, nome por extenso, em segundo plano; nunca editar com build rodando.
3. Commit no fim de cada fase com a mensagem indicada. **Sem** `Co-Authored-By`, sem "Generated with Claude Code", sem
   link de sessão. **Nunca** `git push`.
4. Regras do projeto (`AGENTS.md` + `agents/`): MAX-500 linhas (800 em testes), sem `@Suppress`, `AureoleDS`, strings
   em `values/` e `values-pt-rBR/`, `@AureolePreview` privado, testes unitários junto do código novo, lógica de
   ViewModel testada em JVM puro.
5. Notas em `PROGRESSO.md`; dúvida nova vira D-nova em `DECISOES.md` com a opção conservadora, e segue.

| # | Fase | Modelo | Requisitos | Tempo |
|---|---|---|---|---|
| 1 | **Linhas e menus** | **Sonnet** | RF-04, RF-08 a RF-11, RF-18, RF-19 | 1–1,5 h |
| 2 | **Fundo dentro da Aparência** | **Opus** | RF-01 a RF-03 | 1,5–2,5 h |
| 3 | **Logo, anel e pasta aberta** | **Opus** | RF-05 a RF-07, RF-05b, RF-12 a RF-17 | 2–3 h |

---

## Fase 1: linhas e menus (Sonnet)
1. `composable/menu/`: `menuRowTitleColor(isDestructive)` = `onSurfaceMedium` ou `error`; usar em `AureoleMenuRows`
   (`MenuRowTexts`) e `AureoleSwitchRow` (RF-10). Teste unitário.
2. `MenuIconColumn` sem `tileModifier` (RF-11). Screenshots das linhas destrutivas atualizados na fase G.
3. Indicador de widgets (RF-08/09): tirar o switch de `SettingsBehaviorScreen`/seções novas, `showWidgetDots` de
   `SettingsUiState`, `SettingsToggleManager`, `MainUiState`, `HomeViewModel`, `HomeScreenContent`, `AppsList`,
   `WidgetConfigs`; string `settings_behavior_show_widget_indicator` sai. Migração única em `SettingsRepository`:
   global `false` → grava `false` em `widgetStackDots` para cada pilha de retrato e paisagem; apaga a chave; flag de
   feito. Teste JVM.
4. Logo fora das sheets dos Ajustes (RF-04): `onSettingsClick = null` e sem logo em toda sheet aberta pelos Ajustes
   (via `OverlayHostSpec`/config das sheets filhas).
5. "Personalizar home" (RF-18) em `AppItemMenus` (antes do destrutivo), `WidgetStackBottomSheet` (visível sem rolar) e
   `EditContainerMainContent` (fim de OPÇÕES, salvando o que estiver em edição). Título longo com slots de 48 dp e
   reticências; container vira "Container" (RF-19; string nova PT/EN).

**Commit:** `feat(polimento-ui): cor única dos títulos, linha destrutiva, sem indicador global, Personalizar home nos menus (fase 1)`

## Fase 2: Fundo dentro da Aparência (Opus)
1. Definir plano de fundo passa a ser conteúdo trocado **dentro** da sheet de Aparência, como Paleta e Fonte
   (mesmo mecanismo do RF-14/D-09 da `ajustes-redesign`), sem botão de voltar: voltar do sistema / arrastar devolvem
   para Aparência. Prévia de cima mostra cor ou foto na hora. Editor de foto segue em tela cheia por cima. Paisagem
   com prévia à esquerda.
2. Espaçamentos (RF-02): mesmo respiro do RF-15 da `ajustes-redesign` antes de cada rótulo; switch da tela de bloqueio
   como linha normal com margem própria.
3. Vivas fora (RF-03) + migração única: cor do fundo salva que pertence às Vivas → `BACKGROUND` da paleta atual. Teste.

**Commit:** `feat(polimento-ui): Definir plano de fundo dentro da Aparência, espaçamentos e sem Vivas (fase 2)`

## Fase 3: logo, anel e pasta aberta (Opus)
1. `OverlaySheetBody`/`OverlaySheetHeader`: logo na **mesma Row do título**, ~16 dp, toque 48 dp, centralizada na
   linha do texto, slot vazio simétrico (RF-05, RF-19). Editores de item (pasta, botão rápido) sem logo (RF-05b).
2. Animação enquanto pressiona (RF-06): `pointerInput`/`interactionSource` → escala 0,86 + rotação −18° em 180 ms;
   soltar → mola com overshoot e `onSettingsClick`; cancelar só volta. Sem animações do sistema: nada se mexe.
3. `core/designsystem/components/AureoleLogoRing(progress: Float?)` extraído do anel do onboarding (RF-12), com
   semântica de progresso; aplicar na raiz dos Ajustes (verificar atualização / download do in-app update) e no fim do
   onboarding (RF-13; vitrine e espiada são da `ajustes-onboarding`). Ícone interno do checklist maior (RF-14).
4. Pasta aberta (RF-15 a RF-17): função pura `folderPopupPlacement(anchorTopPx, popupHeightPx, screenHeightPx,
   statusInsetPx, navInsetPx, marginPx)` → topo e altura máxima; usar `WindowInsets.navigationBars`; medir a altura do
   popup. Testes JVM (topo, meio, fim; 3 botões e gestos).

**Commit:** `feat(polimento-ui): logo no título com animação de toque, AureoleLogoRing e pasta aberta acima da barra (fase 3)`
