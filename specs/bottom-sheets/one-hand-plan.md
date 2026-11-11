# Bottom sheets para uma mão: plano de desenvolvimento

**Base:** `release/0.9.20` · **Protótipo:** `specs/bottom-sheets/one-hand-prototype.html` (31 telas)
**Referências:** `ref/telas/<tela>.png` (todas as telas; `-canhoto` nas 6 principais), `ref/paleta.png`, `ref/fonte.png`
**Como rodar:** `/fase <n>` · progresso em `/estado` e `progress.md` (as notas de cada fase estão lá). Commit e push ficam com o Mateus.

## Onde estamos (11/11)

As fases 1 a 5 já rodaram: gates passando (detekt em 5 issues, 1285 testes, cobertura, screenshot e `assembleDebug`) e visual **quase** ou **igual**. Falta o teste no aparelho de todas elas.
Depois disso o Mateus testou no aparelho e pediu ajustes (fase 6). As pendências que as próprias fases deixaram viraram a fase 7. Restam também diálogos e acabamento.

## Resumo

| # | Fase | Modelo | Tempo (agente + gates) | Tokens | Estado |
|---|---|---|---|---|---|
| 1 | Base da sheet: faixa de gesto invisível, `ActionRow`, espelho | Opus | 1,5–2,5 h | 100–160k | feita |
| 2 | Lista reordenável, ✕, "+", desfazer | Opus | 2,5–3,5 h | 120–180k | feita |
| 3 | Menus e popup da pasta | Sonnet | 2–3 h | 100–150k | feita |
| 4 | Editores restantes, cartões e vidro numa sheet | Sonnet | 3–4,5 h | 180–260k | feita |
| 5 | Adicionar container e opções de posição | Sonnet | 1,5–2,5 h | 80–130k | feita |
| 6 | **Ajustes do teste no aparelho:** prévia sem rótulo, ícones nas linhas, cards da pilha, mover por botões | `sheet-ui` (Sonnet) | 2–3 h | 120–180k | a fazer |
| 6+ | (se aprovado) ícones de pasta do Material Symbols | `sheet-chores` (Haiku) + `sheet-ui` | 1,5–2,5 h | 80–120k | decidir |
| 7 | **Lacunas e dívidas das fases 1 a 5** | `sheet-ui` (Sonnet), autoscroll com `sheet-gestures` (Opus) | 3,5–5,5 h | 200–300k | a fazer |
| 8 | Diálogos e avisos | `sheet-ui` (Sonnet) | 1,5–2,5 h | 80–130k | a fazer |
| 9 | Acabamento: strings, previews, screenshots, varredura | `sheet-chores` (Haiku) | 1–1,5 h | 40–80k | a fazer |
| — | Revisão visual ao fim de cada fase | `visual-reviewer` (Opus) | 10–20 min | 30–50k por fase | |

**Falta:** cerca de 8–12,5 h de trabalho do agente (mais 1,5–2,5 h se aprovar os ícones de pasta) e 0,45–0,7 M de tokens. Calendário realista: 1 semana, a maior parte do tempo no seu teste no aparelho.
**Ordem:** 6 → 7 → 8 → 9. A 6 e a 7 vêm antes da 8 porque mexem em componentes que os diálogos reaproveitam (`AureolePreviewFrame`, `AureoleMenuSheet`, linhas de switch).

### Decisões que dependem do Mateus

1. **Menu do app, lado da mão:** o texto do protótipo manda a ação mais usada para o lado do polegar; a imagem `appmenu.png` destra mostra o oposto. A fase 3 seguiu o texto. Escolher uma.
2. **Favoritos no canhoto:** os cabeçalhos também espelham ("Favorites" à direita). Manter ou não.
3. **Seletor de cor:** matiz em barra (protótipo) ou em círculo (referência antiga). Sugestão: barra, mais fácil com o polegar.
4. **Cartões e vidro:** o protótipo usa "Aplicar/Cancelar" ou só "Pronto"? Hoje o código tem Aplicar/Cancelar.
5. **Ícones de pasta:** aprovar a proposta do Material Symbols (abaixo).
6. **"Pasta" e "Ícone" na fileira do app:** o código não tem essas ações. Sugestão: tirar do protótipo.

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

### Pronto quando

- [ ] Gates passam; screenshots regenerados e `visual-reviewer` **igual** ou **quase** contra as refs de `ref/telas/` (já sem rótulo e com ícones).
- [ ] Teste no aparelho: 2,5 cards visíveis em telas de 360 e de 412 dp; botões ‹ › com o polegar.

### 6+: ícones de pasta (decidir)

Hoje o `FolderIconRegistry` tem 26 chaves, mas só uns 20 ícones diferentes; várias caem no ícone errado (`lock` → olho riscado, `cart` → pasta, `play` → seta, `share` → menu, `warning` → info). Proposta:
- **Fonte:** Material Symbols (Google Fonts Icons), Rounded, peso 400, Apache 2.0.
- **Como trazer:** *File → New → Vector Asset → Clip Art* no Android Studio, ou SVGs de `google/material-design-icons` convertidos com o `download_and_convert_svgs.py` que já existe.
- **Quantos:** 50 a 60 escolhidos, por categoria (trabalho, social, mídia, jogos, finanças, saúde, viagem, ferramentas).
- **Onde:** `AureoleFolderIcons.kt` próprio (o `AureoleIcons.kt` não deve passar de 500 linhas). As chaves salvas continuam valendo; as que caem em ícone errado ganham o certo.
- **Tela:** grade de 5 colunas com chips de categoria no rodapé; busca só se passar de 60 ícones.
- **Evitar:** a dependência `material-icons-extended` (pesa no build e foge da regra de usar só `AureoleDS.icons`) e a fonte de ícones inteira.

---

## Fase 7: lacunas e dívidas das fases 1 a 5

Saiu das seções "Ficou faltando" do `progress.md`. Ordem por impacto.

**Funcionalidade que ficou incompleta** (`sheet-ui`)
- **Nível de vidro:** `hazeBlurLevel` só vale na prévia. Aplicar na Home: `Container`, `OpenedFolderLayouts`, `AureolePopup`, `ClockHeader`.
- **Desfazer do fundo:** `WallpaperManager.clear()` do sistema continua sendo chamado e não é revertido; desfazer precisa restaurar o papel de parede.
- **Menu do app dentro de container e pasta** aparece sem Favorito: ligar `onToggleFavorite` em `HomeScreenFolderOverlayComponents.kt`.
- **Data da prévia do relógio** com idiomas misturados ("Wednesday, 31 de December"): usar o locale do app.
- **Seletor de cor e de ícones** abertos pela aparência do botão rápido ficam empilhados (dois scrims): um só.

**Gestos** (`sheet-gestures`, Opus)
- **Autoscroll** na fileira reordenável quando o item arrastado chega na borda.

**Dívida de arquitetura** (`sheet-ui`)
- `LeftHandMirror`, `ThemePreviewOverlay`, `PickPalette` e `PickFont` importam `LocalHomeUiState` de `feature/home/model`. Passar `isLeftHanded` por parâmetro.
- Seta das linhas: hoje é `ArrowDown` girada e aponta para fora do texto no canhoto. Criar `ChevronRight` no catálogo e espelhar certo.
- Tirar strings órfãs (`home_app_menu_uninstall`, `home_app_menu_add_to_favorites`, `home_app_menu_remove_from_favorites`, `home_folder_opened_add_apps`, `home_folder_opened_edit`, `home_grid_config_title`) — `sheet-chores`.

**Telas que ainda diferem do protótipo** (`sheet-ui`)
- **Relógio:** títulos de seção em versalete, estilo em 3 cartões no lugar do dropdown, alinhamento em segmented, divisória antes das abas e "Restaurar" neutro (hoje vermelho e cortado em "Reset Defau…"). Sem rótulo de prévia.
- **Personalizar:** seção "AJUSTAR" com 3 botões e "ADICIONAR À HOME" em grade de 4 colunas com ✓ e estado esmaecido.
- **Formulário da pasta:** altura da sheet acima da ref (~47%); tirar o tile "letra" da grade.
- **Container:** prévia sem a moldura tracejada.
- **Fundo:** mini-home alinha os favoritos ao lado do polegar no canhoto.
- **Opções de posição:** fechar com os 300 ms de animação; título como na ref.
- **Cartões e vidro:** botões conforme a decisão 4.
- **Menu do app:** lado da ação mais usada, conforme a decisão 1; sem "Pasta" e "Ícone" na fileira (decisão 6).

**Pronto quando:** gates + visual + checklist do aparelho abaixo.

### Checklist do aparelho (fases 1 a 7, 30–60 min)

- Arrastar, toque duplo e segurar no topo da sheet; TalkBack.
- Arrastar com o polegar: pilha de widgets, bandeja da pasta, itens do container, favoritos.
- Campo de nome da pasta com o teclado aberto (os prints não injetam IME).
- Cartões e vidro, opacidade e blur ao vivo; desfazer do fundo.
- Paleta e Fonte: Aplicar/Cancelar em destro e canhoto.
- Menus do app, da pasta e do item em destro e canhoto.

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
