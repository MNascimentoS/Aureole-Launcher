# ajustes-onboarding: plano

**Base:** `release/0.9.44` · **Depende de:** `polimento-ui` (logo no título e `AureoleLogoRing`) · **Spec:** `SPEC.md` · **Decisões:** `DECISOES.md` (todas respondidas) ·
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
| 1 | **Paleta/fonte, relógio e container** | **Sonnet** | RF-05 a RF-12 | 1–1,5 h |
| 2 | **Vitrine, espiada e shared element** | **Opus** | RF-01 a RF-04, RF-13 a RF-15 | 2,5–3,5 h |
| 3 | **Ajustar layout: pílula Salvar** | **Sonnet** | RF-16 a RF-20 | 1–1,5 h |

---

## Fase 1: paleta/fonte, relógio e container (Sonnet)
1. `dismissAppearancePicker()` (RF-05 a RF-07, D-02): se houve prévia, confirma a da prévia; senão aplica Frostbite /
   Istok Web; nos dois casos completa a tarefa como o confirmar. O `OnboardingAppearancePickerComponent` informa se
   houve prévia. Testes JVM.
2. `onboardingDefaultSize`/`placeOnboardingItem` (RF-08 a RF-10): relógio em retrato com tamanho e posição do layout
   padrão (20×6, linha 0); paisagem igual a hoje. Testes.
3. Container do onboarding (RF-11/12): ao criar por `placeSilently`, salvar config `isBackgroundEnabled=false`,
   `showFolderLabels=false`, `isGridFolderEnabled=true`. Teste.

**Commit:** `feat(ajustes-onboarding): paleta e fonte ao fechar, relógio do tamanho padrão e container limpo (fase 1)`

## Fase 2: vitrine, espiada e shared element (Opus)
1. Vitrine (RF-01 a RF-04): parâmetro `isShowcase` no Personalizar (sem toques, sem arrastar, voltar ignorado) e
   `AureoleLogoRing` na logo do título fechando em 3 s (D-01); fecha sozinho e o reducer segue. Testes do reducer.
2. Espiada (RF-13 a RF-15): `ONBOARDING_HOME_PEEK_MS` = 1400 (D-04); bolha de vidro com logo + anel no lugar da pílula,
   item novo com contorno; ao fechar o anel, **shared element** bolha → cartão da lista (ou pílula) com
   `SharedTransitionLayout` (`sharedBounds` bolha↔cartão, `sharedElement` logo) ou estendendo o
   `OnboardingChecklistMorphComponent`. Sem animações: troca direta.

**Commit:** `feat(ajustes-onboarding): Personalizar em vitrine e espiada com anel que abre Primeiros passos (fase 2)`

## Fase 3: Ajustar layout (Sonnet)
1. `OnboardingEditHintComponent` embaixo (RF-16), encolhe para pílula "✓ Salvar" após 3 s ou 1º gesto (RF-17), 25% e
   sem interceptar durante o gesto (RF-18), troca para o topo se houver item embaixo (RF-19, função pura testada),
   TalkBack (RF-20).

**Commit:** `feat(ajustes-onboarding): Salvar do Ajustar layout embaixo e em pílula (fase 3)`
