# 💉 Aureole Providers & Dependency Injection

## 1. Koin Dependency Injection (`di/`)
- Dependency injection is configured via Koin in the `di/` package (e.g., `di/AppModule.kt`).
- Repositories and data sources are registered as `single` (Singletons).
- ViewModels are registered via `viewModel` (e.g., `viewModelOf(::HomeViewModel)`).
- Koin is initialized centrally in `AureoleApplication.kt#onCreate()`.
- Activities obtain ViewModels strictly via the Koin delegate `by viewModel()`.

## 2. Lifecycle Management (`core/lifecycle/`)
- Ecosystem logic dependent on Activity lifecycle events (start, stop, resume, destroy) must be encapsulated as `DefaultLifecycleObserver` implementations.
- Located in `core/lifecycle/` (e.g., `WidgetLifecycleObserver.kt`, `UpdateLifecycleObserver.kt`).
