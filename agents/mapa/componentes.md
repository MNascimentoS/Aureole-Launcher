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
| AureoleEditOverlay | core/designsystem/components/AureoleEditOverlay.kt | Overlay base de edição | AureoleEditOverlayParams | Sheet fora do padrão de menu |
| AureoleOverlayBackdrop | core/designsystem/components/AureoleOverlayBackdrop.kt | Fundo com haze do overlay | hazeState | Backdrop de overlay |
| AureoleAutoSaveOverlay | composable/sheets/AureoleAutoSaveOverlay.kt | Overlay que salva sozinho | AutoSaveOverlayParams | Edição sem botão Salvar |
| AureoleConfirmOverlay | composable/sheets/AureoleConfirmOverlay.kt | Confirmação em estilo sheet | — | Confirmação visual de sheet |
| AureoleOverlayWindow | composable/overlay/AureoleOverlayWindow.kt | Janela de overlay | — | Base de janelas de overlay |
| SettingsBottomSheet | composable/sheets/SettingsBottomSheet.kt | Sheet de configurações | — | Sheets de ajustes |
| PickFontBottomSheet / PickPaletteBottomSheet | composable/sheets/ | Escolher fonte / paleta. Fonte com `ThemePickerSheetOptions.componentFont` (`ComponentFontOptions`, `model/FontPickerModels.kt`) vira o seletor de um componente (`ComponentFontSheet`): título/subtítulo próprios, switch Negrito, "Launcher font" (`FONT_KEY_LAUNCHER`) primeiro, amostra própria nos tiles (`FontChipLook`), prévia acima, salva na hora | — | Só esses seletores; fonte de componente sempre por aqui |
| ReleaseNotesBottomSheet | composable/sheets/ReleaseNotesBottomSheet.kt | Notas da versão | — | Só notas de versão |
| RenderModalBottomSheetOrInspection | composable/sheets/RenderModalBottomSheetOrInspection.kt | Modal sheet, ou inline no preview | sheetContent | Previews de sheet |
| AppItemBottomSheet | composable/appitem/AppItemMenus.kt | Menu de ações do app (exemplo de `AureoleMenuListSheet`); sem o logo do Aureole settings no cabeçalho (`showHeaderAction = false`) | — | Menu de app |
| AureoleSheetTabBar | composable/controls/AureoleSheetTabBar.kt | Barra de abas fixa no rodapé | — | Sheet com abas |
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
| AureoleThemeColorPicker | composable/color/AureoleThemeColorPicker.kt | Cor do tema ou fixa; `selected`, `onSelect`, `defaultOption` | Toda cor do usuário, persistida como `ThemeColorRef` |
| AureoleColorSwatches / Swatch | composable/color/AureoleColorSwatches.kt | Grade de cores | Cor crua |
| AureoleThemeSwatchRow, AureolePaletteSwatchRow | composable/color/AureolePaletteRows.kt | Amostras de tema e paletas | Peças do picker |
| AureoleHueSlider | composable/color/AureoleHueSlider.kt | Slider de matiz | Cor livre (evitar, ver regra §8) |
| AureoleSegmentedControl, AureoleIconSegmentedControl | composable/controls/AureoleSegmentedControl.kt | Controle segmentado | Poucas opções exclusivas |
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
| ClockHeader | composable/clock/ClockHeader.kt | Relógio do cabeçalho |
| ClockContainer, HomeClockHeader, EditClockBottomSheet | feature/home/components/clock/ | Relógio e edição na home; aba Style com Layout (`ClockLayoutOption`, miniaturas de barras), Message (`ClockMessageOption`), Alinhamento e "Change font" (`EditClockFontSection` → `EditClockFontPicker`); Format › Date desabilitada sem data. Espaços do cartão: `clockSpacing` / `ClockSpace` (`composable/clock/model/ClockSpacing.kt`) |
| ClockFontChoice, `resolveClockFont`, `withMigratedFont` | core/data/model/ClockFontMigration.kt | Fonte do relógio (chave do catálogo ou `FONT_KEY_LAUNCHER`) + negrito; lê as chaves antigas (SANS_SERIF, BOLD, SERIF, MONOSPACE, ROUNDED) |
| ClockLayout, ClockLine, ClockMessage, `resolveClockLayout` | core/data/model/ClockLayout.kt | 7 layouts (linhas de cima para baixo), mensagem saudação/texto e leitura dos estilos antigos (migração) |
| WidgetPositionIndicatorBar | composable/widgets/WidgetPositionIndicatorBar.kt | Indicador de posição |
| WidgetPickerScreen, WidgetStackBottomSheet, EditWidgetGroupScreen (carrossel: `EditWidgetGroupCarousel`) | feature/home/components/widgets/ | Widgets na home |
| AureoleText (2 overloads) | core/designsystem/components/AureoleText.kt | Texto do DS, no lugar de `Text` |
| AureoleLogoRing | core/designsystem/components/AureoleLogoRing.kt | Anel do logo |
| AureoleUndoSnackbar | composable/undo/AureoleUndoSnackbar.kt | Snackbar de desfazer (state, isLeftHanded, onUndo, onTimeout); estado em `composable/undo/model` |
| UndoController | core/undo/UndoController.kt | Não é composable: `show(msg, undo)`, `undo()`, `expire(id)`; o timer fica na UI |
| LeftHandMirror | composable/mirror/LeftHandMirror.kt | Espelha o layout para canhotos |
| AureolePreviewFrame | composable/preview/AureolePreviewFrame.kt | Moldura de previews |
| AureoleMiniHome | composable/minihome/AureoleMiniHome.kt | Mini preview da home |
| ProvideHomeUiState | composable/HomeUiStateProvider.kt | Provê `MainUiState` em previews/testes |
| SettingsToggleItem, SettingsMenuItem, SettingsActionItem, SettingsHugeTitle | composable/SettingsComponents.kt | Itens da tela de Configurações |

## Só de uma feature (não reutilizar)
onboarding (`Onboarding*`, `WelcomeScreen`, `GestureCoach*`), containers (`Container*`, `EditContainerScreen`), quickbutton (`QuickButton*Sheet`, `ShortcutSelectionBottomSheet`), `CustomizeBottomSheet`, settings (`CardsGlassBottomSheet`, `ContributorsBottomSheet`, `LicenseDetailBottomSheet`, `WallpaperEditorOverlay`), secretgame.
