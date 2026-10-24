# 🤖 Aureole Launcher - Agent Configuration & Guidelines Index

Welcome to the **Aureole Launcher** agent documentation hub. This index guides AI agents and developers through the modular architectural guidelines, design system tokens, testing rules, and coding standards for this project.

## 📚 Modular Documentation Guides

1. **[Architecture & Package Structure](aureole_architecture.md)**
   - Clean Architecture, Package-by-Feature structure.
   - Strict separation of responsibilities (Activity vs Screen vs ViewModel vs Repository).
   - **MAX-500 Rule** (strict file length limit) and prohibition of `@Suppress`.

2. **[Components & UI Rules](aureole_components.md)**
   - Rules for Jetpack Compose UI files (`*Screen.kt`, `*Component.kt`, `*Dialog.kt`, `*Sheet.kt`, `*Popup.kt`).
   - Strict separation of UI and Models (all data classes, states, and actions must reside in `model/`).
   - Reusable components in the global `composable/` package.

3. **[Providers & Dependency Injection](aureole_providers.md)**
   - Koin dependency injection configuration (`di/`).
   - Lifecycle management (`core/lifecycle/`) using `DefaultLifecycleObserver`.
   - Single Source of Truth repositories.

4. **[Testing & Quality Assurance](aureole_testing.md)**
   - Mandatory unit tests (`app/src/test/`) and screenshot tests (`@PreviewTest`).
   - Mandatory `@AureolePreview` with private functions.
   - Detekt verification and quality gates.

5. **[Design System & Tokens](aureole_design_system.md)**
   - Semantic tokens: `AureoleDS.colors`, `AureoleDS.dimens`, typography, and `AureoleIcons`.
   - Prohibition of hardcoded numeric dimensions or color hexes.

---
*Always consult these modular guides when implementing or refactoring features in Aureole Launcher.*
