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
- **Mandatory:** All strings must be extracted to `res/values/strings.xml` and referenced using `stringResource(R.string.<key>)` (or `TextResource.Res` in models).
- **Exceção registrada: previews.** Texto de exemplo dentro de funções `@AureolePreview` (e de screenshot tests) pode ser literal; não precisa de string resource nem de tradução. Vale só para texto que nunca aparece em tela de produção.
- **Mandatory:** Every new string added to `res/values/strings.xml` must have a corresponding translation in `res/values-pt-rBR/strings.xml`.
- **Key Naming Convention:** Strings must follow the format `<feature>_<component>_<description>` (e.g., `customize_add_clock`, `edit_container_orientation`).

## 5. Read-Only Composables (`@ReadOnlyComposable`)
- **Mandatory:** Any `@Composable` function or helper method that only reads composition/resources (such as `stringResource(...)`, theme tokens, or `CompositionLocal` values) without emitting UI layout nodes or modifying state MUST be annotated with `@ReadOnlyComposable` alongside `@Composable`.

## 6. Action sheets e menus
- **Menu é lista vertical.** Os menus usam `AureoleMenuListSheet` (ou `AureoleMenuEntries` dentro de `AureoleMenuSheet`). Sem pager e sem `bottomBarContent`: nada de fileira de botões redondos no rodapé.
- **Altura:** a sheet abraça o conteúdo, até 60% da tela. Não sobra espaço vazio entre a última linha e a borda inferior.
- **Ação destrutiva no fim:** Remover, Remover da pasta e Desinstalar (`isDestructive = true`) ficam depois de um divider, em vermelho. Nunca no topo nem no meio da lista. A ordem vem de `withDestructiveDivider`, que é estável.
- **Remoção usa desfazer de 4 s** (`UndoController` com `AureoleUndoSnackbar`). **Desinstalar usa segurar** (`requiresHold = true`), porque o sistema já pede confirmação.
- **Exceção registrada:** o recorde do jogo secreto usa popup central (`Dialog`), não sheet (`specs/jogo-secreto` D-06). Outra exceção a esta seção só vale registrada no `DECISOES.md` da spec.

## 7. Sheets: cabeçalho e abas
- **Cabeçalho:** toda sheet usa `OverlaySheetHeader`. O espaço abaixo do título é o token `sheetHeaderGap` (~12 dp sem ação; 0 com ação, porque o slot de 48 dp já dá folga). O ícone do cabeçalho tem +4 dp em relação ao tamanho antigo. Ajuste se faz no `OverlaySheetHeader` (vale para todas as sheets), nunca numa sheet só.
- **Título em sentence case, nas strings:** só a primeira palavra em maiúscula ("Customize clock", "Widget group"); nomes próprios ficam ("Aureole settings"). Corrige-se na string de `values/` e `values-pt-rBR/`, nunca com `lowercase()` no código.
- **Abas embaixo:** sheets com abas têm a barra de abas fixa **no rodapé**, nunca no topo.
- **Margem da barra de abas:** `hasTabBottomBar = true` no config da sheet dá `xSmall` (8 dp) acima e abaixo da barra, em vez do respiro de uma fileira de botões; o inset de navegação do sistema continua. Vale para toda sheet com a barra no rodapé.
- **Fading edge:** o conteúdo que rola sob a barra fixa some num fading edge onde encontra a barra, em vez de ser cortado seco.

## 8. Seletor de cor
- Cor escolhida pelo usuário usa o `AureoleThemeColorPicker` (cores do tema + outras paletas), sem seletor de matiz próprio.
- Persistir como `ThemeColorRef` (JSON), não como ARGB solto, para acompanhar a troca de tema. Valor ARGB antigo migra para `Fixed` na leitura (0 ou desconhecido = Padrão do tema); ninguém perde o que já configurou.
