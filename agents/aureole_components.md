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
## 4. Internationalization (i18n) & Strings
- **Prohibited:** Hardcoding any user-facing text strings directly in `.kt` files.
- **Mandatory:** All strings must be extracted to `res/values/strings.xml` and referenced using `stringResource(R.string.<key>)` or `AureoleDS.strings`.
- **Mandatory:** Every new string added to `res/values/strings.xml` must have a corresponding translation in `res/values-pt-rBR/strings.xml`.
- **Key Naming Convention:** Strings must follow the format `<feature>_<component>_<description>` (e.g., `customize_add_clock`, `edit_container_orientation`).
