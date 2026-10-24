# 🧩 Aureole Components & UI Rules

## 1. UI File Restrictions
- Files ending in `*Screen.kt`, `*Component.kt`, `*Dialog.kt`, `*Sheet.kt`, or `*Popup.kt` **only** contain `@Composable` functions.
- **Prohibited in UI files:** `data class`, `class`, `enum`, `UiState`, `Actions`, `Config`, or `Params`. Place all models in `model/` packages.

## 2. Reusable Component Repository (`/composable`)
- Any generic or shared visual component used by more than one screen (custom buttons, selectors, toggles, reusable dialogs) **must** reside in the central `composable/` root package.
- All public reusable Design System composables must accept `modifier: Modifier = Modifier` immediately after required content parameters and be strictly stateless.

## 3. Previews (`@AureolePreview`)
- **Prohibited:** Native `@Preview` annotation directly.
- **Mandatory:** Always use `@AureolePreview` with strictly private functions (`private fun`).
- Must cover: 1) Default State, 2) Variants/Disabled, 3) Slots/Content.
