# Mapa: componentes

Caminhos relativos a `app/src/main/java/dev/mnascimentos/aureole/` (`M/`). Nomes de params são os principais, não a lista completa.
Antes de criar um composable, procure aqui. Regras em `agents/aureole_components.md`.

## Sheets e menus
| Nome | Arquivo | Para que serve | Params-chave | Quando usar em vez de outro |
|---|---|---|---|---|
| AureoleMenuListSheet | composable/menu/AureoleMenuSheet.kt | Menu em lista vertical; destrutivos vão ao fim, em vermelho | title, onDismiss, params: MenuSheetParams, entries(dismiss) | Padrão para todo menu de ações |
| AureoleMenuSheet | composable/menu/AureoleMenuSheet.kt | Base do menu com header e conteúdo livre | title, onDismiss, params, content(dismiss) | Só se o conteúdo não for lista de `MenuEntry` |
| AureoleMenuEntries | composable/menu/AureoleMenuEntries.kt | Renderiza `MenuEntry` (Row, Section, Divider, Content); `internal` | entries | Já vem dentro dos dois acima |
| AureoleMenuRow / SectionLabel / Note | composable/menu/AureoleMenuRows.kt | Linha, rótulo de seção e nota | MenuRowParams, text | Via `MenuEntry`; avulso só em layout próprio |
| AureoleRadioSheet / AureoleRadioRow | composable/menu/AureoleRadioSheet.kt, AureoleRadioRow.kt | Sheet e linha de escolha única | RadioSheetModels (menu/model) | Escolha única em lista |
| AureoleSwitchRow | composable/menu/AureoleSwitchRow.kt | Linha com switch | SwitchRowParams | Toggle dentro de menu/sheet |
| OverlaySheetHeader (+Logo) | core/designsystem/components/OverlaySheetHeader.kt | Cabeçalho de toda sheet; `internal`, token `sheetHeaderGap` | título, modifier, ação opcional | Sempre; ajuste aqui, nunca numa sheet só |
| AureoleEditOverlay | core/designsystem/components/AureoleEditOverlay.kt | Overlay base de edição; alça visível no topo (`OverlaySheetGestureZone`, `internal`: barra `SheetHandleBarHeight`, alvo 48 dp, TalkBack "Fechar"). `showsDismissFeedback` no config: alça acesa e tick `SegmentFrequentTick` ao arrastar abaixo da altura de fechar (só a sheet do relógio) | AureoleEditOverlayParams | Sheet fora do padrão de menu |
| AureoleOverlayBackdrop | core/designsystem/components/AureoleOverlayBackdrop.kt | Fundo com haze do overlay | hazeState | Backdrop de overlay |
| AureoleAutoSaveOverlay | composable/sheets/AureoleAutoSaveOverlay.kt | Overlay que salva sozinho (`hasTabBottomBar` para barra de abas) | AutoSaveOverlayParams | Edição sem botão Salvar |
| AureoleConfirmOverlay | composable/sheets/AureoleConfirmOverlay.kt | Confirmação em estilo sheet | — | Confirmação visual de sheet |
| AureoleOverlayWindow | composable/overlay/AureoleOverlayWindow.kt | Janela de overlay | — | Base de janelas de overlay |
| PickFontBottomSheet / PickPaletteBottomSheet | composable/sheets/ | Escolher fonte / paleta. Fonte com `ThemePickerSheetOptions.componentFont` (`ComponentFontOptions`, `model/FontPickerModels.kt`) vira o seletor de um componente (`ComponentFontSheet`): título/subtítulo próprios, switch Negrito, "Launcher font" (`FONT_KEY_LAUNCHER`) primeiro, amostra própria nos tiles (`FontChipLook`), prévia acima, salva na hora. Paleta em modo cor: overload `PickPaletteBottomSheet(color = ComponentColorOptions)` (`ComponentColorSheet`, `model/ColorPickerSheetModels.kt`: `ComponentColorRole` TEXT/CARD, `componentColorEntries`, `selectedColorEntry`, `toColorRef`): "Theme" primeiro, "Current" para cor fora das paletas, tile com uma faixa só (`PaletteChip(strip=)`), salva `ThemeColorRef.Fixed` na hora e fecha. Corpos `PalettePickerGrid`/`FontPickerGrid` (internal, `fadesEdges` sobre barra de abas) e `ThemePreviewOverlay` (`miniHome`, `bottomBar`, `hasTabBottomBar`) servem a sheet Visual do onboarding | — | Só esses seletores; fonte de componente sempre por aqui |
| ReleaseNotesBottomSheet | composable/sheets/ReleaseNotesBottomSheet.kt | Notas da versão | — | Só notas de versão |
| RenderModalBottomSheetOrInspection | composable/sheets/RenderModalBottomSheetOrInspection.kt | Modal sheet, ou inline no preview | sheetContent | Previews de sheet |
| AppItemBottomSheet | composable/appitem/AppItemMenus.kt | Menu de ações do app (exemplo de `AureoleMenuListSheet`); sem o logo do Aureole settings no cabeçalho (`showHeaderAction = false`) | — | Menu de app |
| AureoleSheetTabBar | composable/controls/AureoleSheetTabBar.kt | Barra de abas fixa no rodapé | — | Sheet com abas |
| AureoleSheetSavedHint | composable/sheets/AureoleSheetSavedHint.kt | Linha "✓ Suas mudanças são salvas automaticamente ao fechar" (`overlay_sheet_saved_hint`) acima das abas; `onClick` opcional | modifier, onClick | Sheet sem botão de salvar (relógio, grupo) |
| `asSheetPage`, SheetPageBackButton | composable/sheets/AureoleSheetPage.kt | Página dentro de uma sheet com abas: `AureoleEditOverlayConfig.asSheetPage(title, subtitle, onBack, onClose)` põe voltar (`overlay_sheet_page_back`), título e subtítulo e esconde a barra | title, subtitle, onBack, onClose | Página de cor do relógio, páginas do grupo |
| AureolePopupBox/Header/MenuSection, AureoleHeaderBanner | core/designsystem/components/AureolePopup.kt | Peças de popup | — | Popups |
| AureolePopupMenuItem | core/designsystem/components/AureolePopupItem.kt | Item de popup | AureolePopupMenuItemParams | Item em popup |
| AppItemPopup | composable/appitem/AppItemPopupComponents.kt | Popup do app | — | Popup do app |

## Botões e ações
| Nome | Arquivo | Para que serve | Quando usar |
|---|---|---|---|
| AureoleActionRow, AureolePrimaryActionButton | composable/actions/AureoleActionRow.kt | Linha de ações e botão primário | Rodapé de ações |
| AureoleHoldActionButton | composable/actions/AureoleHoldToConfirm.kt | Segurar para confirmar (`requiresHold`) | Desinstalar |
| AddWidgetButton | composable/widgets/AddWidgetButton.kt | Adicionar widget | Linha de widgets |
| FavoriteAppRemoveButton | composable/favorites/FavoriteAppsDialogComponents.kt | Remover favorito | Lista de favoritos |

## Seletores e controles
| Nome | Arquivo | Para que serve | Quando usar |
|---|---|---|---|
| AureoleColorRow, ColorPageTileChip, ColorDot, NoneColorDot | composable/colorpage/ColorPageComponents.kt | Linha de cor (bolinha, título, valor, chevron; cor nula = "Nenhuma" tracejada) e tile da página de cor (`ColorTileLook`) | title, value, color, onClick | Cor de componente dentro da própria sheet (relógio, grupo) |
| ColorPageTile, `cardColorTiles`, `selectedCardColorTile`, `toCardColorRef`, ColorPageMetrics | composable/colorpage/model/ColorPageTiles.kt | Tiles de uma página de cor de cartão: "Nenhuma" (nula), "Tema" (= `themeRef` do componente), "Atual", paletas | saved, themeRef, isDarkTheme | Relógio (`CLOCK_CARD_THEME_REF`) e grupo |
| AureoleColorField, AureoleColorPickerDialog | composable/color/AureoleColorField.kt, AureoleColorPickerDialog.kt | Escolha de cor: a linha abre a grade de cartões (`ComponentColorSheet`, referência "Card color") numa janela própria; o dialog sozinho serve quem abre a grade por outro gatilho (cartão "Solid color" do fundo) | `ColorFieldParams` / `ComponentColorOptions` | Toda cor de componente (pasta, botão rápido, fundo). Sem bolinhas, chips nem matiz |
| AureoleSegmentedControl, AureoleIconSegmentedControl | composable/controls/AureoleSegmentedControl.kt | Controle segmentado; `style = SegmentStyle(itemHeight, role)` (`composable/controls/model/SegmentStyle.kt`; altura padrão 48 dp, papel padrão `Role.Tab`, `Role.RadioButton` para escolha de valor) | Poucas opções exclusivas |
| AureoleOptionTile / AureoleCenteredOptionTile | composable/optiontile/AureoleOptionTile.kt (+ `model/OptionTileContent.kt`) | Tile de opção visual (`onClick` nulo = desligado); a versão `Centered` centraliza amostra e rótulo (pills do Format do relógio) | Escolha visual |
| AureoleIconCatalogGrid | composable/icons/AureoleIconCatalogGrid.kt | Catálogo de ícones | Escolher ícone |
| AureoleSearchField | composable/search/AureoleSearchField.kt | Campo de busca | Busca em sheet |
| AureoleFloatingSearchBar | core/designsystem/components/AureoleFloatingSearchBar.kt | Busca flutuante | Busca principal |

## Dialogs
| Nome | Arquivo | Para que serve |
|---|---|---|
| AureoleDialog (2 overloads), AureoleDialogHeader | core/designsystem/components/AureoleDialog.kt | Dialog base |
| AureoleConfirmDialog | composable/sheets/AureoleConfirmDialog.kt | Confirmar sim/não |
| CreateFolderDialog, FolderFormDialog, FolderFormSheet | composable/dialogs/ | Criar/editar pasta (dialog e sheet) |
| FolderFormGlyphCard, FolderNamePill, FolderGlyph, FolderBadge | composable/dialogs/FolderFormComponents.kt, FolderGlyph.kt | Peças de pasta |
| FavoriteAppsDialog | composable/favorites/FavoriteAppsDialog.kt | Gerenciar favoritos |
| UpdateAvailableDialog, UpdateDownloadedDialog | feature/home/components/overlays/InAppUpdateDialog.kt | Update in-app (só home) |
| SecretBestScorePopup | feature/secretgame/components/SecretBestScorePopup.kt | Recorde do jogo (popup central, exceção D-06) |

## Listas e itens
| Nome | Arquivo | Para que serve |
|---|---|---|
| AppItemRow, AureoleAppIcon, AppListSkeletonComponent | composable/appitem/ | Linha de app, ícone, skeleton |
| FavoriteAppRowInfo, FavoriteAppDragHandle, RemainingAppRow | composable/favorites/FavoriteAppsDialogComponents.kt | Peças da lista de favoritos |
| AureoleSectionHeader | core/designsystem/components/AureoleSectionHeader.kt | Cabeçalho de seção |
| AureoleDivider | core/designsystem/components/AureoleDivider.kt | Divisor do DS (8 dp acima e abaixo); usado nos menus e antes de "Aureole settings" no drawer |
| AppsList, AppsListDrawer, HomeAppItemRow | feature/home/components/favorites/AppsList.kt, appsdrawer/ | Lista e drawer da home |

## Relógio, widgets, texto e outros
| Nome | Arquivo | Para que serve |
|---|---|---|
| ClockHeader | composable/clock/ClockHeader.kt | Relógio do cabeçalho. Só toque longo (`detectTapGestures`, sem ripple, haptic `LongPress`, semântica `onLongClick` "Customize clock", sem `Role.Button`). Tamanhos saem do espaço: `resolveClockSizing(lines, largura, altura)` (hora contínua 12–68 sp pela largura e altura) e `clockMetrics` (mensagem, data, borda, raio e espaços como fração da hora, pisos 11 sp/8 dp), em `composable/clock/model/ClockSizing.kt` e `ClockSpacing.kt`. `Modifier.scaledLayout` (`ClockTextTrim.kt`) desenha reduzido. `trimVerticalBands` mede o texto na própria altura (nunca espremido pela célula) e a coluna do relógio usa `wrapContentHeight(unbounded = true)`: a data nunca cai sobre a hora; altura da linha da hora calibrada no screenshot (1,41 em antes do corte) |
| ClockContainer, HomeClockHeader, EditClockBottomSheet | feature/home/components/clock/ | Relógio e edição na home. Aviso de onboarding `AureoleSheetSavedHint` acima das abas (`showsClockSaveHint`, `model/ClockEditorSession.kt`); fechar com mudança oferece Desfazer (`editingClockSnapshot`, `clockUndoTarget`, `saveClockConfig`); sem botão de salvar; aba Style com Alinhamento primeiro, Layout (`ClockLayoutOption`, miniaturas de barras alinhadas como o relógio), Message (`ClockMessageOption`) e "Change font" (`EditClockFontSection` → `EditClockFontPicker`); Format › Date desabilitada sem data. Aba Colors: linhas "Text color"/"Card color" sempre visíveis, cartão nulo = "None" com `NoneColorDot` (`EditClockColorsSection` via `AureoleColorRow`; `clockColorRefFor`, `clockColorTitleRes` em `model/ClockColorRows.kt`); a linha abre `EditClockColorPage` dentro da mesma sheet (abas escondidas, voltar no cabeçalho; tiles `ColorPageTile` (`composable/colorpage/`) via `clockColorTiles`/`toClockColorRef` em `model/ClockColorTiles.kt`, "None" primeiro no cartão, "Theme" do cartão = `CLOCK_CARD_THEME_REF`). Espaços do cartão: `clockSpacing` / `ClockSpace` (`composable/clock/model/ClockSpacing.kt`). Preview no tamanho da home: `ClockContainer` e o cabeçalho da lista medem (`onClockMeasured` → `MainUiState.clockSizes`, só em memória; `clockSizeOf`, `editingClockSize` em `extensions/MainUiStateClockSizeExt.kt`) e o preview reduz com `previewFitScale` (`composable/preview/model/PreviewFit.kt`). Partes da sheet (corpo, barra de abas, página de cor no cabeçalho) em `EditClockSheetParts.kt`; `EditClockOverlayOptions.initialColorRole` abre direto na página de cor (screenshots). `ComponentFontOptions.startsExpanded` abre o seletor de fonte expandido |
| PaletteChipLabel | composable/sheets/model/PaletteChipLabel.kt | Nome e faixa única de um `PaletteChip` (modo cor do seletor de paletas) |
| EditClockPickerOpeners, EditClockGreeting, EditClockPickersParams | feature/home/components/clock/model/EditClockPagerParams.kt | Agrupam abridores dos seletores, texto digitado e dados dos seletores do editor do relógio |
| ClockFontChoice, `resolveClockFont`, `withMigratedFont` | core/data/model/ClockFontChoice.kt | Fonte do relógio (chave do catálogo ou `FONT_KEY_LAUNCHER`) + negrito; lê as chaves antigas (SANS_SERIF, BOLD, SERIF, MONOSPACE, ROUNDED) |
| ClockLayout, ClockLine, ClockMessage, `resolveClockLayout` | core/data/model/ClockLayout.kt | 7 layouts (linhas de cima para baixo), mensagem saudação/texto e leitura dos estilos antigos (migração) |
| WidgetPositionIndicatorBar | composable/widgets/WidgetPositionIndicatorBar.kt | Indicador de posição |
| WidgetPickerScreen, WidgetStackBottomSheet, EditWidgetGroupScreen (carrossel: `EditWidgetGroupCarousel`) | feature/home/components/widgets/ | Widgets na home |
| AureoleText (2 overloads) | core/designsystem/components/AureoleText.kt | Texto do DS, no lugar de `Text` |
| AureoleLogoRing | core/designsystem/components/AureoleLogoRing.kt | Anel do logo |
| AureoleTipCard, TipPulse | composable/tips/ (+ `model/TipCardModels.kt`: `TipCardContent`, `TipCardMetrics`) | Cartão de dica guiada (traços de passo, "Pular dicas", título, texto, botão em pílula; região "Dica N de M") e anel que pulsa no alvo (sem pulso com "remover animações") |
| AureoleUndoSnackbar | composable/undo/AureoleUndoSnackbar.kt | Snackbar de desfazer (state, isLeftHanded, onUndo, onTimeout); estado em `composable/undo/model` |
| UndoController | core/undo/UndoController.kt | Não é composable: `show(msg, undo)`, `undo()`, `expire(id)`; o timer fica na UI |
| LeftHandMirror | composable/mirror/LeftHandMirror.kt | Espelha o layout para canhotos |
| AureolePreviewFrame | composable/preview/AureolePreviewFrame.kt | Moldura de previews |
| `previewFitScale` | composable/preview/model/PreviewFit.kt | Escala da prévia de um componente medido na home (1 se cabe, nunca cresce) |
| AureoleMiniHome | composable/minihome/AureoleMiniHome.kt | Mini preview da home |
| ProvideHomeUiState | composable/HomeUiStateProvider.kt | Provê `MainUiState` em previews/testes |
| SettingsToggleItem, SettingsMenuItem, SettingsActionItem, SettingsHugeTitle | composable/SettingsComponents.kt | Itens da tela de Configurações |

## Só de uma feature (não reutilizar)
onboarding (`Onboarding*`, `WelcomeScreen`, `OnboardingPresetScreen`, `GestureCoach*`), containers (`Container*`, `EditContainerScreen`, `Group*` da sheet do grupo), quickbutton (`QuickButton*Sheet`, `ShortcutSelectionBottomSheet`), `CustomizeBottomSheet`, settings (`CardsGlassBottomSheet`, `ContributorsBottomSheet`, `LicenseDetailBottomSheet`, `WallpaperEditorOverlay`), secretgame.
