# Ajustes reorganizados (direção B): plano

**Base:** branch com `ajustes-sheets-settings` concluída · **Spec:** `SPEC.md` · **Protótipo:** `prototipo.html` (aba B) ·
**Decisões:** todas respondidas · **Como rodar:** `../EXECUTAR-AJUSTES-E-FUNDO.md` (roda esta spec e depois a `editar-foto-fundo`)

## Regras de execução

1. Por modelo: fase 1 (Sonnet) → fase 2 (Opus) → fase 3 (ajustes do teste no aparelho, Opus) → fase 4 (gates: Sonnet + revisão visual Opus).
2. **Gates só na fase 4.** Nas fases 1 a 3: `./gradlew compileDebugKotlin` e os testes da fase com `--tests`. Um
   comando Gradle por chamada, nome por extenso, em segundo plano; nunca editar com build rodando.
3. Commit no fim de cada fase: `feat(ajustes-redesign): <resumo> (fase N)`. **Sem** `Co-Authored-By`, sem
   "Generated with Claude Code", sem linha de atribuição. **Nunca** `git push`.
4. Regras do projeto (`AGENTS.md` + `agents/`): MAX-500 linhas, sem `@Suppress`, `AureoleDS`, strings EN e pt-BR,
   `@AureolePreview` privado, testes unitários junto do código novo.
5. **Nada sai do app.** Ao fim de cada fase, conferir o checklist do `IDEIA.md` (inventário) e anotar em `PROGRESSO.md`.
6. Dúvida nova: D-06+ em `DECISOES.md`, opção conservadora, continuar.

| # | Fase | Modelo | Requisitos | Tempo | Tokens |
|---|---|---|---|---|---|
| 1 | **Raiz em cartões e conteúdo reagrupado** (ainda nas telas atuais) | **Sonnet** | RF-01 a RF-06 | 1,5–2,5 h | 100–160k |
| 2 | **Áreas como bottom sheet:** host, troca no lugar, prévia, Colaboradores em sheet, paisagem, voltar | **Opus** | RF-07 a RF-11 | 2,5–4 h | 170–260k |
| 3 | **Ajustes do teste no aparelho:** sai Cores personalizadas, ícones temáticos na prévia, Paleta e Fonte dentro da sheet de Aparência, respiro entre seções, paisagem com prévia ao lado | **Opus** | RF-12 a RF-16 | 2–3 h | 120–190k |
| 4 | **Gates, correções e revisão visual** | **Sonnet** + `visual-reviewer` (**Opus**) | todos | 1–1,5 h | 60–110k |

**Total:** 7–11 h e 0,45–0,72 M de tokens.

---

## Fase 1: raiz em cartões e conteúdo reagrupado (Sonnet)

O conteúdo é reorganizado primeiro **dentro das telas atuais**, em seções reaproveitáveis; a fase 2 só troca o
recipiente (tela → sheet). Assim cada fase compila sozinha.

1. **Raiz** (`SettingsRootScreen.kt`): `SettingsAreaCard(icon, title, subtitle, onClick)` novo em
   `feature/settings/components/` (fundo `AureoleDS.colors.card`, `radiusLarge`, ícone `iconLarge`, título
   `titleMedium`, subtítulo `bodyMedium onSurfaceLow`, `minTouchTarget`, `Role.Button`, sem chevron). Cartões com
   `Arrangement.spacedBy(medium)`. Mantém deslocamento do cabeçalho, "Muito obrigado!" preso embaixo e os dois painéis
   em paisagem da spec anterior. Ícone do Comportamento: o mesmo de hoje (lista).
2. **Strings** (EN/pt-BR): os três subtítulos do RF-02 (`settings_root_*_subtitle`); rótulos de seção novos
   (Tema, Fundo, Cartões e vidro, Gestos e lista de apps, Tela inicial, Sistema, Sobre, Zona de cuidado) se ainda não
   existirem; "Hold to confirm"/"Segure para confirmar" se não existir.
3. **Seções reaproveitáveis** em `feature/settings/sections/` (um arquivo por área, < 500 linhas):
   `AppearanceSections(uiState, actions)`, `BehaviorSections(...)`, `AdvancedSections(...)`, cada uma uma `Column` de
   grupos com `SheetSectionLabel` + linhas dos componentes de menu já existentes (`AureoleMenuRows`,
   `AureoleSwitchRow`, a roda do deslocamento que já existe em `SettingsBehaviorScreen.kt`, movida para cá).
   - Aparência: Tema / Fundo / Cartões e vidro (RF-04). Subtítulos com valor atual: funções puras em
     `feature/settings/model/SettingsSummaries.kt` (`paletteSummary`, `fontSummary`, `backgroundSummary`,
     `glassSummary`) com teste.
   - Comportamento: Gestos e lista de apps / Tela inicial (RF-05, sem Verificar atualizações).
   - Avançado: Sistema (com Verificar atualizações, D-03) / Sobre (linhas com ícone e chevron: `Users`, `Doc`; criar
     ícones se faltarem) / Zona de cuidado (RF-06): `AureoleHoldToConfirm` nas duas linhas, cor `error`, divisor antes;
     Redefinir e Desinstalar disparam as ações de hoje ao completar o segurar (sem a sheet de confirmação extra; se o
     fluxo de desinstalar do sistema já pede confirmação, manter).
4. As telas `SettingsAppearanceScreen`/`SettingsBehaviorScreen`/`SettingsAdvancedScreen` passam a só chamar a seção
   (ficam finas; somem na fase 2).

**Testes da fase:** `SettingsSummariesTest`; `SettingsRootScreenTest` (3 cartões, sem chevron, toque navega);
`AdvancedSectionsTest` (ordem Sistema → Sobre → Zona de cuidado; Verificar atualizações em Sistema; segurar dispara
Redefinir e Desinstalar, toque curto não); `BehaviorSectionsTest` (sem Verificar atualizações); checklist do inventário
como teste de texto: as 27 strings de item aparecem somadas nas três seções.
**Commit:** `feat(ajustes-redesign): raiz em cartões e seções reagrupadas (fase 1)`

---

## Fase 2: áreas como bottom sheet (Opus)

1. **Estado puro** `feature/settings/model/SettingsSheetNav.kt`: `sealed interface SettingsSheet { Area(APPEARANCE |
   BEHAVIOR | ADVANCED); Child(kind, returnTo: Area?) }` e um redutor `reduce(state, event)` para abrir área, abrir
   filho (troca no lugar guardando `returnTo` e a rolagem), fechar filho (volta para `returnTo`), fechar área, voltar.
   Testes cobrindo cada caminho (inclusive voltar com filho aberto → volta para a área; voltar na área → raiz).
2. **Host** `feature/settings/components/SettingsSheetHost.kt`: um `AureoleEditOverlay` por vez, conteúdo da seção da
   fase 1. Aparência com `AureoleMiniHome` como prévia (D-04: só Aparência). Os diálogos de hoje (`SettingsDialogs`:
   paleta, cor, fundo, cartões e vidro, notas, fonte) viram os filhos e passam pelo redutor, para a troca no lugar.
   Rolagem da área guardada por `rememberSaveable` e devolvida ao voltar.
3. **Rotas** (`SettingsRoute.kt`/`SettingsNavGraph`): saem os destinos Aparência, Comportamento, Avançado e
   Colaboradores; fica a raiz e Licenças (tela cheia, D-02). Colaboradores vira filho em sheet (lista centralizada).
   Apagar as telas finas da fase 1 e o que ficar sem uso.
4. **Altura e paisagem:** Avançado usa repouso alto como o Personalizar (conteúdo rola dentro, elástico no topo);
   em paisagem, sheets com `widthIn(max = settingsContentMaxWidth)` centralizadas.
5. **Voltar e entradas:** `BackHandler` segue o redutor; entradas pelo Personalizar, pastas e pilha de widgets abrem a
   raiz como hoje. Canhoto espelha. TalkBack: o cartão anuncia "abre painel".

**Testes da fase:** `SettingsSheetNavTest`; UI (Robolectric): tocar em Aparência abre a sheet com a prévia; Paleta
troca no lugar e ao fechar volta Aparência na mesma rolagem; Colaboradores abre como sheet; Licenças navega tela
cheia; voltar com filho → área → raiz → sai. Screenshot tests (criar): raiz retrato e paisagem; as três sheets;
Avançado rolado até a Zona de cuidado; Colaboradores em sheet.
**Commit:** `feat(ajustes-redesign): Aparência, Comportamento e Avançado como bottom sheet (fase 2)`

---

## Fase 3: ajustes do teste no aparelho (Opus)

**Requisitos:** RF-12, RF-13, RF-14, RF-15, RF-16 · **Depende de:** fase 2 · **Decisões:** D-08, D-09

1. **Sai "Cores personalizadas"** (RF-12, `AppearanceSections.kt`): tirar a linha e o caminho que abria o
   `ColorPickerDialog` a partir dela (o seletor de cor continua usado onde mais for usado; se ficar sem uso, apagar
   com as strings órfãs). Atualizar o `SettingsInventoryTest` para 26 itens e o checklist do `IDEIA.md`.
2. **Ícones temáticos na prévia** (RF-13): o `AureoleMiniHome` ganha `themedIcons: Boolean`; ligado, desenha os
   ícones com o mesmo tint temático da home (reaproveitar a função que a home usa; não duplicar a regra). A prévia da
   sheet de Aparência lê o valor do switch ao vivo.
3. **Paleta e Fonte dentro da sheet** (RF-14): no `SettingsSheetNav`, um estado de **conteúdo interno** da área
   (`Area(APPEARANCE, inner = PALETTE | FONT)`) em vez de `Child`; o `SettingsSheetHost` troca só o conteúdo (com
   `AnimatedContent` deslizando para o lado) e mantém a mesma sheet e a mesma prévia, que passa a refletir a paleta ou
   a fonte escolhida ao vivo. Título da sheet acompanha. Voltar do sistema → lista de Aparência na mesma rolagem
   (salva a escolha, sem fechar); tocar fora ou arrastar → salva e fecha, com Desfazer. Reaproveitar o conteúdo de
   `PickPaletteBottomSheet`/`PickFontBottomSheet`/`ThemePickerGrid` sem o invólucro de sheet. Testes do redutor para
   os novos caminhos.
4. **Respiro entre seções** (RF-15): no `SettingsSectionGroup` (ou no rótulo de seção usado nas sheets de Ajustes),
   `padding(top = large)` em todo rótulo que não é o primeiro. Vale para Aparência, Comportamento, Avançado e as telas
   que usam o mesmo grupo.
5. **Paisagem com prévia ao lado** (RF-16, `AureoleEditOverlay`/`OverlaySheetBody`): quando `maxWidth > maxHeight` e
   a sheet tem prévia, layout em duas colunas: prévia à esquerda (centralizada, escala para caber na altura) e a
   sheet à direita com largura máxima `settingsContentMaxWidth`, altura toda útil e rolagem interna. Função pura
   `overlayLayoutFor(width, height, hasPreview)` com teste. Vale para todas as sheets com prévia do app (é o
   componente do DS), conferindo Personalizar → Paleta, Fundo, Botão rápido e Cartões e vidro. Retrato sem mudança.

**Testes da fase:** `SettingsSheetNavTest` (Paleta/Fonte internas: abrir, voltar para a lista, fechar salva);
`overlayLayoutFor`; `AureoleMiniHome` com `themedIcons`; `SettingsInventoryTest` com 26; screenshots novos (criar):
Aparência com Paleta interna, Aparência em paisagem com prévia ao lado, Avançado com o respiro entre seções.
**Commit:** `feat(ajustes-redesign): ajustes do teste no aparelho (fase 3)`

---

## Fase 4: gates, correções e revisão visual

1. Gates, um por vez, em segundo plano: `detekt --auto-correct` → `lintDebug` → `testDebugUnitTest` →
   `jacocoTestCoverageVerification` → `updateDebugScreenshotTest` → `validateDebugScreenshotTest` → `assembleDebug`.
2. `visual-reviewer` contra a aba B do `prototipo.html` (raiz, 3 sheets, Avançado no fim): igual / quase / diferente.
3. **Commit:** `chore(ajustes-redesign): gates e screenshots (fase 4)`. Sem atribuição. Sem push.

## Checklist do aparelho (Mateus)

- Raiz: cartões sem setinha e sem tags; "Sistema, sobre o app e créditos"; paisagem em dois painéis.
- Cada cartão abre uma sheet que sobe; Aparência com prévia; voltar, toque fora e arrastar fecham.
- Paleta, Fonte, Fundo, Cartões e vidro, Novidades, Colaboradores trocam no lugar e voltam para a área.
- Avançado: Verificar atualizações em Sistema; Sobre com ícones; Zona de cuidado no fim, segurar para Redefinir e Desinstalar.
- Licenças em tela cheia; entrar pelo Personalizar, por uma pasta e pela pilha de widgets; canhoto.
- Aparência sem "Cores personalizadas"; Ícones temáticos muda os ícones da prévia; Paleta e Fonte abrem dentro da
  sheet, com a mesma prévia, e voltar devolve a lista.
- Rótulos de seção com mais espaço acima em todas as sheets de Ajustes.
- Girar o aparelho com Aparência, Paleta, Fonte e Fundo abertos: prévia à esquerda, conteúdo à direita.
- Nenhum outro item sumiu (checklist do `IDEIA.md`).
