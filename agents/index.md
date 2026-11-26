# 🤖 Aureole Launcher - Agent Configuration & Guidelines Index

Welcome to the **Aureole Launcher** agent documentation hub. This index guides AI agents and developers through the modular architectural guidelines, design system tokens, testing rules, and coding standards for this project.

## 📚 Modular Documentation Guides

1. **[Architecture & Package Structure](aureole_architecture.md)**
   - Clean Architecture, Package-by-Feature structure.
   - Strict separation of responsibilities (Activity vs Screen vs ViewModel vs Repository).
   - **MAX-500 Rule** (strict file length limit: 500 lines for production code, 800 for tests) and prohibition of `@Suppress`.

2. **[Components & UI Rules](aureole_components.md)**
   - Rules for Jetpack Compose UI files (`*Screen.kt`, `*Component.kt`, `*Dialog.kt`, `*Sheet.kt`, `*Popup.kt`).
   - Strict separation of UI and Models (all data classes, states, and actions must reside in `model/`).
   - Reusable components in the global `composable/` package.
   - Action sheets, `OverlaySheetHeader` (`sheetHeaderGap`, título e subtítulo centrados), alça visível com alvo de 48 dp, abas embaixo com fading edge, `AureoleColorField` / `AureoleColorPickerDialog` (grade de cartões) + `ThemeColorRef`; exceção do relógio: seletor de cor como página na mesma sheet, cartão "Nenhuma".

3. **[Providers & Dependency Injection](aureole_providers.md)**
   - Koin dependency injection configuration (`di/`).
   - Lifecycle management (`core/lifecycle/`) using `DefaultLifecycleObserver`.
   - Single Source of Truth repositories.

4. **[Testing & Quality Assurance](aureole_testing.md)**
   - Mandatory unit tests (`app/src/test/`) and screenshot tests (`@PreviewTest`) by screen, flow and catalog (see 8).
   - Mandatory `@AureolePreview` with private functions.
   - Quality gates: detekt with 0 issues, lintDebug with 0 errors/warnings, coverage, screenshots, assemble.
   - Tests that never hang (no blocking the Robolectric main thread) and ViewModel logic tested on the plain JVM.

5. **[Design System & Tokens](aureole_design_system.md)**
   - Semantic tokens: `AureoleDS.colors`, `AureoleDS.dimens`, typography, and `AureoleIcons`.
   - Prohibition of hardcoded numeric dimensions or color hexes.
   - `SmallText` 13 sp; vetor que falha no lint/Robolectric vira PNG em `drawable-nodpi/`.

6. **[Product Context](aureole_product.md)**
   - O que é o app, pacote, "Teu Apps", créditos e contatos.
   - Promessa de privacidade (só Crashlytics sai do aparelho); toda permissão do manifest listada na política.
   - Site em `docs/` (aureolelauncher.app, privacidade e termos PT/EN) e premium planejado (compra única, sem login).

7. **[Workflow: Specs, Fases e Git](aureole_workflow.md)**
   - Trabalho novo = spec nova em `specs/<nome>/` (IDEIA → SPEC → PLANO → DECISOES → PROGRESSO → EXECUTAR).
   - Formato das decisões D-xx, execução por fases, branch `release/x.y.z`, commit só no nome do Mateus, nunca push.
   - Onde guardar a memória do projeto.

8. **[Screenshots: telas, fluxos e catálogo](aureole_screenshots.md)**
   - Contrato v1 das referências: tela inteira (até 3 estados), fluxo em passos `P01…`, catálogo por grupo do DS.
   - Pastas `telas/`, `fluxos/`, `catalogo/`, manifesto `app/src/screenshotTest/screenshots.json`, chave estável.
   - Guarda `ScreenshotContractTest` no `testDebugUnitTest`; componente isolado não tem screenshot próprio.

## 🗺️ Mapa do código (leitura sob demanda)
- Os guias acima são obrigatórios no início de toda tarefa. **[`mapa/index.md`](mapa/index.md)** não: leia o índice e depois só o mapa da área tocada
  (`componentes`, `design-system`, `features`, `dados`, `receitas`).
- Antes de criar um componente, token ou repositório, consulte o mapa para reaproveitar o que já existe.
- A seção "Conflitos" do índice do mapa lista onde o código diverge dos guias.

## 📋 Estado do trabalho
- **[`specs/index.md`](../specs/index.md)**: specs abertas e concluídas, pendências e decisões que valem além da spec
  (pasta local, fora do git).

---
*Always consult these modular guides when implementing or refactoring features in Aureole Launcher.*
