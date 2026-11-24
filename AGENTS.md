# 🤖 AGENTS.md - Aureole Launcher Instructions

## Projeto
**Aureole Launcher**: Launcher Android desenvolvido em Kotlin + Jetpack Compose, seguindo Clean Architecture e Package-by-Feature.

---

## 📂 Modular Agent Documentation & Steering Guides
This project uses modular steering documentation located in the [`agents/`](agents/) directory. All AI agents and developers must consult these guides:

- **[Master Index](agents/index.md)**
- **[Architecture & Package Structure](agents/aureole_architecture.md)** (Clean Architecture, MAX-500 rule — 800 lines for tests, No `@Suppress`)
- **[Components & UI Rules](agents/aureole_components.md)** (UI vs Model separation, `@Composable` only in views, shared `composable/`, Internationalization & strict prohibition of hardcoded strings)
- **[Providers & DI](agents/aureole_providers.md)** (Koin DI, Lifecycle observers)
- **[Testing & Quality Assurance](agents/aureole_testing.md)** (Unit tests, Screenshot tests por tela/fluxo/catálogo conforme [`aureole_screenshots.md`](agents/aureole_screenshots.md), `@AureolePreview`)
- **[Design System & Tokens](agents/aureole_design_system.md)** (`AureoleDS.colors`, `AureoleDS.dimens`, `AureoleIcons`)
- **[Product Context](agents/aureole_product.md)** (o que é o app, privacidade, contatos, site em `docs/`, premium planejado)
- **[Workflow: Specs, Fases e Git](agents/aureole_workflow.md)** (specs em `specs/<nome>/`, decisões D-xx, fases, commit só no nome do Mateus, nunca push)
- **[Mapa do código](agents/mapa/index.md)** (o que já existe e onde: componentes, design system, features, dados, receitas; leitura sob demanda)
- **[Estado das specs](specs/index.md)** (o que está aberto, pendências e decisões transversais; pasta local, fora do git)

---

## 🚨 Regra de Leitura Preventiva Obrigatória para Agentes
Qualquer agente de IA ou sistema autônomo trabalhando neste repositório **DEVE** obrigatoriamente ler e carregar no contexto inicial **todos** os guias de steering (`agents/index.md` e todos os `.md` em `agents/`) **antes** de iniciar qualquer planejamento, análise ou alteração de código. A leitura sob demanda é proibida para estes guias de arquitetura, design system e testes, garantindo que restrições cruciais (como MAX-500 — 800 linhas para testes —, proibição de `@Suppress`, uso de `AureoleDS`, etc.) sejam conhecidas desde o primeiro segundo.

**Dois níveis de leitura:**
- Os guias de regra `agents/*.md` continuam obrigatórios no início de toda tarefa.
- [`agents/mapa/`](agents/mapa/index.md) é lido **sob demanda**: primeiro `agents/mapa/index.md`, depois só o mapa da área que a tarefa toca. Antes de criar um componente, token ou repositório, consulte o mapa para reaproveitar o que já existe.

---

## 🧠 Memória do Projeto
A memória do projeto vive no repo, não num `CLAUDE.md`:
- **Regra durável** (arquitetura, UI, testes, produto, processo) → o guia certo em `agents/` + linha em `agents/index.md`.
- **Estado do trabalho** (fases, pendências, branch, testes no aparelho) → `specs/index.md` e o `PROGRESSO.md` da spec.
- Ao fechar uma fase ou spec, atualizar `specs/index.md` no mesmo passo. Exceção a uma regra só vale registrada no
  `DECISOES.md` da spec.
- **Git:** nunca `git push`; commit só no nome do Mateus, sem `Co-Authored-By` (detalhes em `agents/aureole_workflow.md` §4).

---

## ⚡ Comandos de Verificação Gradle
Antes de finalizar alterações complexas, valide com:
1. `./gradlew detekt --auto-correct` (**0 issues**; `maxIssues: 0`, sem baseline)
2. `./gradlew lintDebug` (**0 erros e 0 avisos**; `abortOnError` e `warningsAsErrors` ligados)
3. `./gradlew testDebugUnitTest` (100% aprovação)
4. `./gradlew jacocoTestCoverageVerification` (linhas ≥ 85%, instruções ≥ 70%, branches de lógica ≥ 60%)
5. `./gradlew validateDebugScreenshotTest`
6. `./gradlew assembleDebug`

Detekt e lint se corrigem no código: sem `@Suppress`, sem baseline, sem `tools:ignore` ou `severity="ignore"` novos e sem afrouxar regra (detalhes em `agents/aureole_testing.md` §3).

---

## 🛠️ Como Agentes Rodam os Gates
- **Um comando Gradle por chamada, com o nome do task escrito por extenso** (`./gradlew testDebugUnitTest`, nunca `for t in ...; do ./gradlew $t`). O painel de atividades reconhece o gate pelo texto do comando.
- Rodar o Gradle em segundo plano (`run_in_background`) e esperar a notificação de término, sem ficar lendo o arquivo de saída em loop. Ao terminar a tarefa, não deixar nenhum build rodando.
- **Nunca editar arquivos com um build rodando.** Editar durante o `lintDebug` já deixou a análise em loop por 18 min.
- Durações normais neste projeto: `lintDebug` ~30–60 s, `testDebugUnitTest` ~40 s a quente. Um `lintDebug` acima de 5 min ou um teste parado com CPU baixa está travado: matar o processo (não o daemon do Android Studio), achar a causa (`jcmd <pid> Thread.print`) e relatar.
- Testes nunca podem travar a suíte: seguir `agents/aureole_testing.md` §5 (sem `runBlocking`/`Thread.sleep` na main do Robolectric) e §6 (lógica de ViewModel testada em JVM puro).
