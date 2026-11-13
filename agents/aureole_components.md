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

## 5. Read-Only Composables (`@ReadOnlyComposable`)
- **Mandatory:** Any `@Composable` function or helper method that only reads composition/resources (such as `stringResource(...)`, theme tokens, or `CompositionLocal` values) without emitting UI layout nodes or modifying state MUST be annotated with `@ReadOnlyComposable` alongside `@Composable`.

## 6. Action sheets e menus
- **Menu é lista vertical.** Os menus usam `AureoleMenuListSheet` (ou `AureoleMenuEntries` dentro de `AureoleMenuSheet`). Sem pager e sem `bottomBarContent`: nada de fileira de botões redondos no rodapé.
- **Altura:** a sheet abraça o conteúdo, até 60% da tela. Não sobra espaço vazio entre a última linha e a borda inferior.
- **Ação destrutiva no fim:** Remover, Remover da pasta e Desinstalar (`isDestructive = true`) ficam depois de um divider, em vermelho. Nunca no topo nem no meio da lista. A ordem vem de `withDestructiveDivider`, que é estável.
- **Remoção usa desfazer de 4 s** (`UndoController` com `AureoleUndoSnackbar`). **Desinstalar usa segurar** (`requiresHold = true`), porque o sistema já pede confirmação.

