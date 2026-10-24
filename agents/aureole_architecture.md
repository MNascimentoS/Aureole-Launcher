# 🏗️ Aureole Architecture & Package Structure

## 1. Clean Architecture & Package-by-Feature
Aureole Launcher follows Clean Architecture principles organized by feature packages (`feature/<feature_name>/`).

### Mandatory Feature Subpackage Structure
```text
feature/<feature_name>/
├── screens/         # Composables for full screens (e.g., SettingsScreen.kt)
├── components/      # Feature-specific dialogs, sheets, and UI components
├── extensions/      # ViewModel extensions and state helpers
├── model/           # UiStates, Actions, Params, and Data Classes
├── <Feature>Activity.kt
└── <Feature>ViewModel.kt
```

---

## 2. Separation of Responsibilities

### Activity (`*Activity.kt`)
- Entry point for the Android system.
- **Responsibility:** Initialize theme, instantiate ViewModel, invoke main Screen in `setContent`.
- **Lifecycle Isolation:** System services/APIs tied to lifecycle (e.g., Widget management, BroadcastReceivers) must be decoupled into `DefaultLifecycleObserver` classes inside `core/lifecycle/` and registered in `onCreate`.
- **Prohibited:** Business logic, network calls, complex state management, or overriding lifecycle methods (`onStart`, `onResume`, `onStop`, `onDestroy`) for logic that belongs in an observer.

### Screen & Views (`*Screen.kt`, `*Dialog.kt`, `*BottomSheet.kt`, `*Popup.kt`)
- Stateless Compose UI consuming immutable `UiState` and dispatching actions via callbacks.
- **Strict UI vs Model Separation:** **Prohibited** to declare `data class`, `enum`, or parameter interfaces inside UI `.kt` files. All models must reside in `model/`.

### ViewModel (`*ViewModel.kt`)
- Exposes immutable `StateFlow<UiState>` and handles user actions.
- Communicates exclusively with Repositories.
- **Dependency Injection:** Must receive dependencies via constructor (Koin). Prohibited to instantiate repositories directly in body or `init`.
- **Prohibited:** Importing Android framework UI references (`Context`, `View`, `Activity`).

### Repository & Data Layer
- Single Source of Truth for feature data.
- Maps local Room entities / remote DTOs into domain models.

---

## 🛑 3. Strict Rules (MAX-500 & Suppress)

### REGRA MAX-500 (File Size Limit)
- **NO file can exceed 500 lines** (Single exception: `AureoleIcons.kt`).
- If an edit exceeds 500 lines, **STOP** and extract logic into new files or subcomponents.

### Proibição Absoluta de `@Suppress`
- It is **strictly prohibited** to use `@Suppress("LongMethod")`, `@Suppress("ComplexMethod")`, `@Suppress("LargeClass")`, or similar suppressions. Refactor the code instead.
