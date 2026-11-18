# teste-aparelho-17-11: plano

**Base:** `release/0.9.44` · **Spec:** `SPEC.md` · **Decisões:** `DECISOES.md` · **Protótipo:** `../prototipo-lote-17-11.html`
(aba 6) · **Como rodar:** `../EXECUTAR-TESTE-APARELHO-17-11.md`

## Regras de execução
1. Duas fases, cada uma num subagente com o modelo indicado. Para economizar tokens: sem revisão visual separada; a
   fase 1 lê só os arquivos que vai mudar.
2. **Gates só na fase 2.** Na fase 1: `./gradlew compileDebugKotlin` e os testes da fase com `--tests`. Um comando
   Gradle por chamada, em segundo plano; nunca editar com build rodando.
3. Commit no fim de cada fase com a mensagem indicada, **sem** linha de atribuição. **Nunca** `git push`.
4. Regras do projeto (`AGENTS.md` + `agents/`), testes junto do código novo.
5. Notas em `PROGRESSO.md`; dúvida nova vira D-03+ com a opção conservadora.

| # | Fase | Modelo | Requisitos | Tempo |
|---|---|---|---|---|
| 1 | **Tudo: onboarding, sheets, ícones, altura, pilha, orientação e rolagem** | **Opus** | RF-01 a RF-13 | 5,5–7,5 h |
| 2 | **Gates e prints** | **Sonnet** | todos | 1–1,5 h |

---

## Fase 1: implementação (Opus)
Na ordem, com `compileDebugKotlin` e os testes do item com `--tests` ao fim de cada bloco:
1. **Onboarding (RF-01 a RF-03):** achar e corrigir o caminho em que o Salvar fica no topo (anotar a causa);
   tamanhos/posições padrão por tipo e orientação numa função do `GridDefaults` usada pelo layout padrão e pelo
   onboarding (relógio, widgets, container); sem logo nas sheets do onboarding, exceto a vitrine.
2. **Sheets (RF-04, RF-05, RF-07):** favoritos sem lápis; título com ícone no início e logo no fim (app; pasta com o
   nome dela, D-01); Gerenciar itens com editor no lugar (lista ↔ botão ↔ pasta), item tocável com "›", adicionar
   botão abre o editor.
3. **Ícones (RF-06, RF-08):** `icons.AddNew` nos pontos de adicionar (D-02); logo em "Configurações do Aureole".
4. **Altura (RF-09, RF-10):** `previewBehavior(...)` (fica / some ao expandir / encolhe para o respiro) com testes;
   sheets curtas do tamanho do conteúdo.
5. **Pilha em paisagem (RF-11):** prévia da pilha (`EditWidgetGroupPreview`/`WidgetStackBottomSheet`) em paisagem
   como pager horizontal de uma página por vez (widget inteiro, centralizado), card de adicionar como última página,
   pontinhos embaixo.
6. **Preso em paisagem (RF-12):** procurar trava de orientação; `isLandscape` só pela `Configuration` atual
   (em `onConfigurationChanged` e `onStart`/`onResume`); `updateGridItemsOrientation` não decide paisagem. Testes.
7. **Rolagem horizontal (RF-13):** helper único de linha horizontal de ponta a ponta com `contentPadding` do sheet;
   aplicar em todas as `LazyRow`/`horizontalScroll` das sheets; rótulo de categoria alinhado no início.

**Commit único:** `feat(teste-aparelho-17-11): ajustes do teste no aparelho (fase 1)`

## Fase 2: gates e prints (Sonnet)
Gates na ordem, um por chamada: detekt --auto-correct, lintDebug, testDebugUnitTest, jacocoTestCoverageVerification,
updateDebugScreenshotTest, validateDebugScreenshotTest, assembleDebug. Corrigir na causa (sem @Suppress/baseline).
Copiar os screenshots das telas mudadas para `specs/lote-17-11-prints/teste-aparelho/` com um README curto (arquivo →
RF). Criar `specs/teste-aparelho-17-11-FEITO.md` com o relatório.

**Commit:** `chore(teste-aparelho-17-11): gates e prints (fase 2)`
