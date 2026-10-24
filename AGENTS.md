# 🤖 AGENTS.md - Aureole Launcher Instructions

## Projeto
**Aureole Launcher**: Launcher Android desenvolvido em Kotlin + Jetpack Compose, seguindo Clean Architecture e Package-by-Feature.

---

## 📂 Modular Agent Documentation & Steering Guides
This project uses modular steering documentation located in the [`agents/`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/) directory. All AI agents and developers must consult these guides:

- **[Master Index](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/index.md)**
- **[Architecture & Package Structure](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/aureole_architecture.md)** (Clean Architecture, MAX-500 rule, No `@Suppress`)
- **[Components & UI Rules](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/aureole_components.md)** (UI vs Model separation, `@Composable` only in views, shared `composable/`, Internationalization & strict prohibition of hardcoded strings)
- **[Providers & DI](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/aureole_providers.md)** (Koin DI, Lifecycle observers)
- **[Testing & Quality Assurance](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/aureole_testing.md)** (Unit tests, Screenshot tests, `@AureolePreview`)
- **[Design System & Tokens](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/agents/aureole_design_system.md)** (`AureoleDS.colors`, `AureoleDS.dimens`, `AureoleIcons`)

---

## 🚨 Regra de Leitura Preventiva Obrigatória para Agentes
Qualquer agente de IA ou sistema autônomo trabalhando neste repositório **DEVE** obrigatoriamente ler e carregar no contexto inicial **todos** os guias de steering (`agents/index.md` e todos os `.md` em `agents/`) **antes** de iniciar qualquer planejamento, análise ou alteração de código. A leitura sob demanda é proibida para estes guias de arquitetura, design system e testes, garantindo que restrições cruciais (como MAX-500, proibição de `@Suppress`, uso de `AureoleDS`, etc.) sejam conhecidas desde o primeiro segundo.

---

## ⚡ Comandos de Verificação Gradle
Antes de finalizar alterações complexas, valide com:
1. `./gradlew detekt --auto-correct` (máx. 5 issues)
2. `./gradlew testDebugUnitTest` (100% aprovação)
3. `./gradlew validateDebugScreenshotTest`
4. `./gradlew assembleDebug`
