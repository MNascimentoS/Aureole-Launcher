# Bottom sheets para uma mão: plano de desenvolvimento

**Base:** `release/0.9.20` · **Protótipo:** `specs/bottom-sheets/one-hand-prototype.html` (31 telas)
**Referências:** `ref/telas/<tela>.png` (todas as telas; `-canhoto` nas 6 principais; `appmenu`, `favoritos-*` atualizadas em 11/11), `ref/paleta.png`, `ref/fonte.png`
**Como rodar:** `/fase <n>` · progresso em `/estado` e `progress.md` (as notas de cada fase estão lá). Commit e push ficam com o Mateus.

## Onde estamos (11/11)

As fases 1 a 5 rodaram (gates passando, visual **quase** ou **igual**) e você já resolveu, fora das fases (ver `progress.md`): nível de vidro na Home (`GlassBlur.kt`), desfazer do fundo (`system_wallpaper_undo.jpg`), Favorito no menu do app dentro de container e pasta, e o crash do menu do app em listas com rolagem (`AureoleOverlayWindow`). O que falta: ajustes do teste no aparelho (6), a regra nova de favoritos (6b), os ícones de pasta (6c), as dívidas restantes (7), diálogos (8) e acabamento (9).

## Resumo

| # | Fase | Modelo | Tempo (agente + gates) | Tokens | Estado |
|---|---|---|---|---|---|
| 1 a 5 | Base, reordenável, menus, editores, container e posições | Opus / Sonnet | — | — | feitas |
| — | Vidro na Home, desfazer do fundo, Favorito em container/pasta, crash do menu | você | — | — | feitas (conferir no aparelho) |
| 6 | **Ajustes do teste no aparelho:** prévia sem rótulo, ícones nas linhas, cards da pilha, mover por botões, Cartões e vidro só com "Pronto", menu do app sem "Pasta"/"Ícone" | `sheet-ui` (Sonnet) | 2–3 h | 120–180k | a fazer |
| 6b | **Favoritos sem duplicação:** sem favoritos = todos os apps; tela de gerenciar com alça de arrastar clara | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k | a fazer |
| 6c | **Ícones de pasta** (aprovado; arquivos prontos em `folder-icons/`) | `sheet-chores` (Haiku) + `sheet-ui` | 1–2 h | 60–100k | a fazer |
| 7 | **Dívidas restantes das fases 1 a 5** | `sheet-ui` (Sonnet), autoscroll com `sheet-gestures` (Opus) | 2,5–4 h | 150–240k | a fazer |
| 8 | Diálogos e avisos | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k | a fazer |
| 9 | Acabamento: strings, previews, screenshots, varredura | `sheet-chores` (Haiku) | 1–1,5 h | 40–80k | a fazer |
| — | Revisão visual ao fim de cada fase | `visual-reviewer` (Opus) | 10–20 min | 30–50k por fase | |

**Falta:** cerca de 9,5–15,5 h de trabalho do agente e 0,55–0,85 M de tokens. Calendário realista: 1 semana, a maior parte no seu teste no aparelho.
**Ordem:** 6 → 6b → 6c → 7 → 8 → 9. A 6 e a 6b vêm antes da 8 porque mexem em componentes que os diálogos reaproveitam (`AureolePreviewFrame`, `AureoleMenuSheet`, linhas de switch). Rode `/fase 6b` e `/fase 6c` normalmente (o argumento é só o nome da fase).

### Decisões já tomadas (11/11)

1. **Menu do app:** a ação mais usada fica no lado do polegar (como a fase 3 fez). Vale o texto do protótipo, não a `appmenu.png` antiga.
2. **Favoritos no canhoto:** cabeçalhos espelhados, como está.
3. **Seletor de cor:** matiz em **barra** (já é o que o código tem).
4. **Cartões e vidro:** só **"Pronto"** (o código hoje tem Aplicar/Cancelar: trocar na fase 6).
5. **Ícones de pasta:** aprovados; os arquivos estão prontos (fase 6c).
6. **Menu do app:** sem "Pasta" e "Ícone" (o protótipo já foi atualizado).

### Diferenças aceitas em relação ao protótipo

- fonte do sistema no lugar da fonte do navegador;
- conteúdo de exemplo;
- selo TIC, zona do polegar e linha de alcance, que só existem no protótipo para explicar.

### Decisões do protótipo v5

- Sem pílula de arraste: o gesto vive numa faixa invisível de 20 dp no topo; dica de acessibilidade "Segure o topo da sheet".
- "Cartões e vidro" e "Opacidade do desfoque" viram uma sheet só (`BlurBottomSheet`, `HazeOpacityDialog`, `PickBorderRadiusBottomSheet`).
- Pasta aberta em modo grade: popup centralizado, só com os apps. Em modo lista: popup ancorado perto da pasta, como hoje. Sem Adicionar Apps nem Editar Pasta no popup.
- Sem sheet de tamanho de widget: `EditGridItemDialog` só mostra "Tamanho atual" (leitura); "Redimensionar Grupo" abre o editor da pilha.
- Pilha de widgets: a prévia lista os widgets com ✕ (com desfazer), arrastar após segurar e cartão "+".
- Prévia sem rótulo: sem "PRÉVIA", "PRÉVIA AO VIVO", "LIVE PREVIEW" nem "PREVIEW" em nenhuma tela (decisão de 11/11, vale mais que as referências antigas). A prévia flutua acima da sheet sem texto.
- Linhas de menu com ícone numa coluna fixa e centralizada, como no app hoje (Aparência, Comportamento, Gerenciar Itens, Configurações do App…), mantendo subtítulo e seta. A linha "Mostrar indicador de posição" também ganha o ícone de pontos.
- Pilha de widgets: cards da prévia maiores (116 x 96 dp) para aparecer 2,5 cards e indicar que dá para rolar; reordenar além de arrastar: botões ‹ › "Mover <widget> · 2 de 3" na sheet, ao alcance do polegar (com desfazer), e `customActions` de acessibilidade.
- Favoritos: **sem favoritos configurados, a Home mostra todos os apps**; não existe mais o switch "Mostrar todos os apps na Home" nem "Ativar todos". Tela de gerenciar com alça de arrastar visível, número da posição e dica "Segure e arraste o botão para ordenar" (decisão de 11/11).
- Menu do app: ações do rodapé são Favorito, Info e Desinstalar (segurar 1 s). Sem "Pasta" e "Ícone".
- Paleta e Fonte: grade de chips, mini-home só com relógio, data e 4 apps (do lado oposto ao polegar), Aplicar/Cancelar do lado da mão. **Já implementadas.**

### Fora do escopo (decidir depois)

Telas cheias de Settings, onboarding, `AureoleFloatingSearchBar`, tema claro, paisagem e tablet. Os screenshot tests devem cobrir tema claro nas sheets novas.

---

## Fases 1 a 5 (feitas)

Detalhes, gates, veredito visual e o que ficou faltando de cada uma estão em `progress.md`. O que entrou no código: `OverlaySheetGestureZone` (faixa de 20 dp sem pílula), `AureoleActionRow`, `LeftHandMirror`, `composable/reorder/` (`ReorderableRow`, `ReorderableColumn`, `ReorderMath`), `composable/menu/` (`AureoleMenuSheet`, `AureoleRadioSheet`), `AureoleConfirmOverlay`, `AureolePreviewFrame`, `AureoleSwitchRow`, `AureoleSegmentedControl`, `CardsGlassBottomSheet` e o desfazer do fundo.

---

## Fase 6: ajustes do teste no aparelho

**Já existe:** `AureolePreviewFrame` (desenha o rótulo), `AureoleMenuSheet` e linhas de menu, `AureoleSwitchRow`, `ReorderableRow` e o editor da pilha (`EditWidgetGroupScreen`, cartões de 110 x 90 dp).
**Referências:** `ref/telas/container.png`, `widgetgroup.png`, `widgetstack.png`, `foldermenu.png`, `qbsettings.png`, `customize.png` (+ canhoto).

### Entrega

1. **Prévia sem rótulo:** `AureolePreviewFrame` deixa de desenhar texto. Remover as strings "PRÉVIA", "PRÉVIA AO VIVO", "LIVE PREVIEW" e "PREVIEW" e a altura que reservavam. A prévia continua ancorada 16 dp acima da sheet. Conferir as margens em todas as telas com prévia (relógio, container, favoritos, pilha, pasta, cor, fundo, botão rápido, cartões, paleta e fonte).
2. **Ícones nas linhas, em coluna fixa e centralizada:** o slot de ícone vai para a linha de `AureoleMenuSheet` (coluna de 36 dp, ícone de 22 dp centralizado, o texto alinha após ela) e para `AureoleSwitchRow` (opcional). Subtítulo e seta continuam. Espelhar no canhoto. Mapa:

| Tela | Linha | Ícone |
|---|---|---|
| Container | Aparência / Comportamento / Gerenciar Itens | `ColorFill` / `Click` / `AllApps` |
| Personalizar | Editar Painel Lateral / Botão Rápido / Favoritos e Configurações / Relógio | `Layout` / `Click` / `Star` / `Calendar` |
| Pilha de widgets | Configurações do App / Redimensionar Grupo | `Settings` / `MultipleView` |
| Pilha de widgets | Mostrar indicador de posição (switch) | `Dots` |
| Menu da pasta | Container / Ícone / Renomear / Editar atalhos | `VerticalContainer` / `Smile` / `Edit` / `AllApps` |
| Botão rápido | Atalho de clique / Abrir app / Aparência | `Click` / `AllApps` / `ColorFill` |
| Favoritos | Editar atalhos / Aparência / Gerenciar e ordem | `Click` / `ColorFill` / `Favorite` |

   Os nomes são os do protótipo; usar o que já existir em `AureoleIcons` e só criar o que faltar.
3. **Cards da pilha de widgets:** largura calculada, não fixa: `cardWidth = (larguraDaFileira − margens − 2 · gap) / 2,5`, de modo que apareçam 2 cards e meio e o terceiro fique cortado para indicar rolagem. Altura de cerca de 0,82 da largura. Função pura com teste. O "+" fica depois dos cards e não conta.
4. **Reordenar sem arrastar:** linha na sheet, ao alcance do polegar: "Mover **<widget>** · n de m" com botões ‹ › de 48 dp, desabilitados nos extremos, usando a mesma `ReorderMath` e o mesmo desfazer "Ordem alterada". Os `customActions` de mover já existem; manter.

5. **Cartões e vidro só com "Pronto":** `CardsGlassBottomSheet` aplica na hora (prévia ao vivo) e fecha com um botão "Pronto" do lado da mão, no lugar de Aplicar/Cancelar. Referência: `ref/telas/cards.png`. Manter o desfazer (4 s) se a mudança precisar voltar.
6. **Menu do app sem "Pasta" e "Ícone":** a fileira do rodapé fica com Favorito, Info e Desinstalar (segurar 1 s). Ref nova: `ref/telas/appmenu.png`.

### Pronto quando

- [ ] Gates passam; screenshots regenerados e `visual-reviewer` **igual** ou **quase** contra as refs de `ref/telas/` (já sem rótulo e com ícones).
- [ ] Teste no aparelho: 2,5 cards visíveis em telas de 360 e de 412 dp; botões ‹ › com o polegar.

---

## Fase 6b: favoritos sem duplicação

**Referências:** `ref/telas/favoritos-lista.png`, `favoritos-gerenciar.png`, `favoritos-sem-favoritos.png` · **Detalhes:** `specs/bottom-sheets/favoritos-gerenciar.md`
**Já existe:** `FavoriteAppsDialog` (menu principal e gerenciar), `ReorderableColumn`, `AureoleSwitchRow`, desfazer "Remover todos".

### Entrega

1. **Regra:** `visibleApps(allApps, favorites) = if (favorites.isEmpty()) allApps else favorites`. Função pura com teste (vazio, 1 favorito, favorito que foi desinstalado). A Home e a lista usam só ela.
2. **Remover duplicação:** apagar o switch "Mostrar todos os apps na Home" (flag `showAll`) e a ação "Ativar todos". Migração: se `showAll` estava ligado, limpar os favoritos salvos (comportamento igual); se estava desligado, manter os favoritos. Tirar as strings órfãs.
3. **Menu principal:** subtítulo de "Gerenciar aplicativos e ordem" = "N favoritos, o resto fica oculto" ou "Sem favoritos: mostra todos os apps".
4. **Tela de gerenciar:** dica "Segure e arraste o botão para ordenar" (só com 2+ favoritos); banner "Sem favoritos: a Home mostra todos os N apps" quando vazio; cada linha = número + ícone + nome + ✕ (40 dp) + **alça de 6 pontos** (44 x 38 dp, destaca ao arrastar) no lado da mão; "Remover todos" (com desfazer); seção "Outros aplicativos (N)" com botão "+" no ícone de cada app. Ações de acessibilidade "mover para cima/baixo".

### Pronto quando

- [ ] Gates passam; `visual-reviewer` **igual** ou **quase** nas 3 refs (destro e canhoto).
- [ ] Teste no aparelho: limpar favoritos mostra todos os apps na Home; arrastar pela alça com o polegar; desfazer de "Remover todos".

---

## Fase 6c: ícones de pasta (aprovado)

**Arquivos prontos:** `specs/bottom-sheets/folder-icons/` (80 VectorDrawables `ic_folder_*.xml` do Material Symbols Rounded, Apache 2.0, em 8 categorias; `AureoleFolderIcons.kt`; `strings-folder-icons.xml`; `folder-icons-contact-sheet.png`; `README.md`).

### Entrega

1. Copiar `drawable/ic_folder_*.xml` para `core/designsystem/.../res/drawable/` e o `AureoleFolderIcons.kt` para o pacote de ícones, ajustando `package` e `R`. Strings das categorias em EN e pt-BR. NOTICE da licença Apache 2.0.
2. `FolderIconRegistry`: manter as 26 chaves antigas (compatibilidade com o que já foi salvo) e mapear as que caíam no ícone errado: `lock`→`lock`, `cart`→`shopping_cart`, `play`→`play_circle`, `share`→`share`, `warning`→`warning`; chave desconhecida → `folder`.
3. Seletor da pasta: grade de 5 colunas com chips de categoria (`byCategory`), item marcado destacado; sem busca.
4. Testes: toda chave antiga resolve para um drawable; `resFor(null)` é nulo; categorias cobrem os 80 ícones.

### Pronto quando

- [ ] Gates passam; `visual-reviewer` confere a grade contra `folder-icons-contact-sheet.png`.
- [ ] `./gradlew assembleDebug` sem aumento relevante do APK (80 vetores pequenos).

---

## Fase 7: dívidas restantes das fases 1 a 5

Saiu das seções "Ficou faltando" do `progress.md`, já sem o que você resolveu. Ordem por impacto.

**Conferir no aparelho o que já foi feito** (não é código novo)
- Desfazer do fundo: validar se `getWallpaperFile` funciona sem permissão de storage; se falhar, o desfazer devolve só a imagem.
- Nível de vidro na Home: ajustar os fatores (Leve 0,4x, Máx. 1,6x são palpite).
- Menu do app em `AureoleOverlayWindow`: fechar pelo botão voltar e por arrastar, insets, "Desinstalar" por segurar, engrenagem e o menu sem o blur do Haze.

**Funcionalidade que falta** (`sheet-ui`)
- **Data da prévia do relógio** com idiomas misturados ("Wednesday, 31 de December"): usar o locale do app.
- **Seletor de cor e de ícones** abertos pela aparência do botão rápido ficam empilhados (dois scrims): um só.

**Gestos** (`sheet-gestures`, Opus)
- **Autoscroll** na fileira reordenável quando o item arrastado chega na borda.

**Dívida de arquitetura** (`sheet-ui`)
- `LeftHandMirror`, `ThemePreviewOverlay`, `PickPalette` e `PickFont` importam `LocalHomeUiState` de `feature/home/model`. Passar `isLeftHanded` por parâmetro.
- Seta das linhas: hoje é `ArrowDown` girada e aponta para fora do texto no canhoto. Criar `ChevronRight` no catálogo e espelhar certo.
- Tirar strings órfãs (`home_app_menu_uninstall`, `home_app_menu_add_to_favorites`, `home_app_menu_remove_from_favorites`, `home_folder_opened_add_apps`, `home_folder_opened_edit`, `home_grid_config_title`) — `sheet-chores`.

**Telas que ainda diferem do protótipo** (`sheet-ui`)
- **Relógio:** títulos de seção em versalete, estilo em 3 cartões no lugar do dropdown, alinhamento em segmented, divisória antes das abas e "Restaurar" neutro (hoje vermelho e cortado em "Reset Defau…").
- **Personalizar:** seção "AJUSTAR" com 3 botões e "ADICIONAR À HOME" em grade de 4 colunas com ✓ e estado esmaecido.
- **Formulário da pasta:** altura da sheet acima da ref (~47%); tirar o tile "letra" da grade.
- **Container:** prévia sem a moldura tracejada.
- **Fundo:** mini-home alinha os favoritos ao lado do polegar no canhoto.
- **Opções de posição:** fechar com os 300 ms de animação; título como na ref.
- **Menu do botão rápido:** linhas que abrem seletores em vez de grades inline.

**Fora do código (só se sobrar tempo):** botões "Pasta" e "Ícone" por app seriam features novas (seletor de pasta de destino e troca de ícone por app); ficam fora deste plano.

**Pronto quando:** gates + visual + checklist do aparelho abaixo.

### Checklist do aparelho (fases 1 a 7, 30–60 min)

- Arrastar, toque duplo e segurar no topo da sheet; TalkBack.
- Arrastar com o polegar: pilha de widgets, bandeja da pasta, itens do container, favoritos.
- Campo de nome da pasta com o teclado aberto (os prints não injetam IME).
- Cartões e vidro, opacidade e blur ao vivo; desfazer do fundo.
- Paleta e Fonte: Aplicar/Cancelar em destro e canhoto.
- Menus do app, da pasta e do item em destro e canhoto.
- Favoritos: limpar a lista mostra todos os apps na Home; arrastar pela alça; ícones de pasta na grade nova.

---

## Fase 8: diálogos e avisos

**Referências:** `ref/telas/confirmlayout.png`, `confirmfactory.png`, `confirmwall.png`, `updateavail.png`, `updateready.png`, `releasenotes.png`.
**Entrega:**
- Diálogos flutuantes no polegar; "Redefinir launcher" exige segurar; "Restaurar fundo" já está sem diálogo e com desfazer (fase 4).
- Atualização disponível e pronta; notas de versão com chips de versão.
- Reaproveitar `AureoleConfirmOverlay`.
**Pronto quando:** gates + visual.

## Fase 9: acabamento

- Todas as strings novas em EN e pt-BR.
- `@AureolePreview` privado em todo componente novo.
- Screenshot tests de cada tela nova (destro e canhoto nas 6 principais, e tema claro nas novas).
- Varredura final: arquivos acima de 500 linhas, `@Suppress`, imports de `feature/` no DS e em `composable/`.
**Pronto quando:** os 5 gates passam do zero e o `visual-reviewer` percorre `ref/telas/` inteiro.
