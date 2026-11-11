# Polimento das action sheets: plano de implementação

- **Base:** `release/0.9.20`, depois das fases 1 a 9 de `one-hand-plan.md`. Os caminhos citados vêm da `0.9.18`: antes de editar, localize cada arquivo e símbolo na branch atual. O `progress.md` lista o que já existe.
- **Já existe e deve ser reaproveitado:**
  - `AureoleMenuSheet` e `AureoleRadioSheet` (`composable/menu/`);
  - `AureoleSegmentedControl`;
  - `AureoleUndoSnackbar` e `UndoController`;
  - `AureoleActionRow` e `LeftHandMirror`, junto com o `LocalLeftHanded`;
  - `AureoleOverlayWindow`;
  - os tiles de estilo do relógio ("relógio em cartões", da fase 7).
- **Progresso:** `specs/bottom-sheets/actionsheets-polish-progress.md`.
- **Protótipo:** `specs/bottom-sheets/actionsheets-polish-prototype.html` (abrir no navegador).
- **Referências:** `specs/bottom-sheets/ref/polish-*.png`, uma por item.
- **Canvas de design:** https://claude.ai/artifact/KXq56rawtbsUCnNWt4pcyj

Para rodar cada fase, use `/polish <n>`. Antes, copie o `claude-setup` para `.claude/` (passo 1 do `COMO-RODAR.md`) e abra uma sessão nova. O comando:
1. mostra o plano da fase e espera o ok;
2. delega a implementação ao subagente da fase;
3. roda os gates;
4. pede a revisão visual.

Commit e push ficam com o Mateus.

## Fases e modelos

| # | Fase | Itens | Subagente (modelo) | Tokens (estimativa) |
|---|---|---|---|---|
| 1 | Layout do editor do relógio: divider, prévia sem fundo, pager de largura total | 1, 2, 3 | `sheet-ui` (Sonnet) | 60–100k |
| 2 | `AureoleOptionTile` e tiles de hora e data | 5 | `sheet-ui` (Sonnet) | 80–120k |
| 3 | Fonte do relógio segue o tema, e tiles de fonte | 4 | `sheet-ui` (Sonnet) | 70–110k |
| 4 | Menus: lista vertical, destrutivo no fim, desfazer | 6, 7 | `sheet-ui` (Sonnet) | 80–130k |
| 5 | Strings, regra no steering, previews e screenshots | — | `sheet-chores` (Haiku) | 30–60k |
| — | Revisão visual ao fim de cada fase | — | `visual-reviewer` (Opus) | 30–50k por fase |

Nenhuma fase mexe na física das sheets, por isso não tem Opus implementando. A exceção possível é a fase 3: se a conversão de `FontFamily` para `Typeface` travar, rode a fase de novo com `sheet-gestures` (Opus).

### Diferenças aceitas em relação ao protótipo

- Os PNGs foram renderizados sem internet. Por isso a fonte do tema aparece como Courier no lugar de Special Elite, e a Istok Web aparece como uma sans genérica.
- O conteúdo é de exemplo, como os atalhos do app 99.
- Os rótulos ANTES e DEPOIS só existem no protótipo.
- No item 1 vale a opção **B**: o divider fica logo após o conteúdo.

---

## Fase 1: layout do editor do relógio (itens 1, 2 e 3)

**Escopo:**
- `core/designsystem/components/AureoleEditOverlaySheet.kt` (`OverlaySheetBody`);
- `core/designsystem/model/` (`AureoleEditOverlayConfig`);
- `feature/home/components/clock/EditClockPager.kt`;
- `feature/home/components/clock/EditClockBottomSheet.kt` (`EditClockPreviewCard`).

**Referências:** `ref/polish-1-separator.png`, `ref/polish-2-preview.png` e `ref/polish-3-pager.png`.

### Comportamento esperado

1. **Divider logo após o conteúdo.** O divider fica colado no fim do conteúdo, com `AureoleDS.dimens.medium` de respiro.
   - **Expandida:** a altura do conteúdo acompanha a aba atual, e o espaço livre fica entre o divider e o rodapé.
   - **Em repouso,** quando o conteúdo é maior que a sheet: o divider continua logo acima do rodapé, marcando o corte do scroll.
   - **Na 0.9.20:** o divider não existe na 0.9.18, então localize onde ele é desenhado. Ele deve ficar no grupo de cima (`Column(Modifier.weight(1f, fill = false))`), depois do `sheetContent`, e não solto entre os grupos do `Arrangement.SpaceBetween`.
   - **No `EditClockPager`:**
     - aplicar `heightIn(min = tallestPage)` só quando `!overlayState.isExpanded`, porque em repouso isso evita o pulo ao trocar de aba;
     - expandida, deixar a altura livre e usar `animateContentSize()`, para o divider não saltar ao trocar de aba;
     - a regra vira uma função pura em `model/`, como `pagerMinHeight(isExpanded, tallestPx): Int`, com teste.
2. **Prévia sem fundo.**
   - Tirar o `.clip(...)` e o `.background(surfaceVariant.copy(alpha = 0.85f))` do `EditClockPreviewCard` e manter o padding.
   - O cartão só aparece quando `isClockBackgroundEnabled`. Nesse caso é o cartão do próprio `ClockHeader`, igual ao da home.
3. **Pager com a largura toda.**
   - Adicionar `AureoleEditOverlayConfig.edgeToEdgeContent: Boolean = false`. Quando for `true`, o `OverlaySheetBody` não aplica `padding(horizontal = small)` no `sheetContent`.
   - O editor do relógio liga a flag. Cada página do `EditClockPager` aplica `padding(horizontal = AureoleDS.dimens.small)` dentro do `verticalScroll`, e o pager fica com `pageSpacing` padrão.
   - Resultado: no meio do swipe, as páginas não encostam e o conteúdo não é cortado na margem.

### Testes

- **Unitário:** `pagerMinHeight`.
- **Screenshot:**
  - overlay expandido, nas abas Estilo e Formato;
  - em repouso, com cartão de fundo e sem.
- **No aparelho:** fazer um swipe lento entre as abas.

### Pronto quando

- [ ] Os 5 gates passam.
- [ ] O `visual-reviewer` diz **igual** ou **quase** contra `ref/polish-1..3`.
- [ ] O divider não fica solto no meio da sheet expandida em nenhuma aba.

---

## Fase 2: tiles de hora e data (item 5)

**Escopo:**
- novo `composable/optiontile/AureoleOptionTile.kt`;
- `EditClockSections.kt` (`EditClockFormatSection`);
- `composable/clock/ClockHeaderUtils.kt`;
- strings.

**Referência:** `ref/polish-5-format-tiles.png`.

### Comportamento esperado

1. **`AureoleOptionTile`** em `composable/optiontile/`.
   - Assinatura: `sample: @Composable () -> Unit`, `label: String`, `selected: Boolean`, `onClick: () -> Unit` e `modifier: Modifier = Modifier`.
   - Visual: stateless, cantos `radiusMedium`, fundo `surfaceVariant`. Quando selecionado, borda de 2 dp em `onSurfaceHigh`.
   - Acessibilidade: `Role.RadioButton` e `selected` nos semantics.
   - O tile de Estilo ("relógio em cartões", fase 7) já existe. Generalize ele para `AureoleOptionTile` em vez de criar um segundo componente, e faça o Estilo usar o componente novo.
2. **Hora:** grade de 3 colunas (Sistema, 12 h e 24 h). O sample é a hora de agora formatada. Sistema segue `DateFormat.is24HourFormat`.
3. **Data:** grade de 2 colunas com 4 tiles (Completo, Curto, Médio e Numérico). O sample é a data de hoje, com o mesmo formato do relógio da home.
4. **Bugs a corrigir junto.** A fase 7 já tratou a data no locale, então confirme na branch atual o que ainda acontece:
   - `resolveClockDatePattern` não trata `"MEDIUM"`, que cai no Completo.
   - O pattern Completo tem `'de'` fixo, então em EN sai "Wednesday, 11 de November".
   - **Correção:** trocar por skeletons resolvidos por locale com `DateFormat.getBestDateTimePattern(locale, skeleton)`. Os skeletons são `EEEEdMMMM`, `EEEdMMM`, `dMMMM` e `ddMMyyyy`. A escolha key → skeleton é uma função pura com teste.
5. **Strings:**
   - Tirar os exemplos dos rótulos. Fica Completo/Curto/Médio/Numérico e Full/Short/Medium/Numeric, sem "(Tue, 22 Sep)".
   - Mudar "Formato de Hora:" para "Hora" e "Formato de Data:" para "Data".
6. **Limpeza:** se `EditClockDropdown.kt` e `EditClockDropdownConfig` ficarem sem uso, remover.

### Testes

- **Unitário:** key → skeleton, e key de hora → 12 h, 24 h ou sistema.
- **Screenshot:**
  - `AureoleOptionTile` selecionado e não selecionado;
  - aba Formato com data e hora fixas, injetando `now` no lugar de ler o relógio.

### Pronto quando

- [ ] Os 5 gates passam.
- [ ] O `visual-reviewer` diz **igual** ou **quase** contra `ref/polish-5`.
- [ ] A prévia da data no tile bate com o relógio da home, em EN e em pt-BR.

---

## Fase 3: fonte do relógio segue o tema (item 4)

**Escopo:**
- `composable/clock/` (`ClockHeaderUtils`, `ClockHeaderContent` e o modelo);
- `EditClockSections.kt` (`EditClockFontSection`);
- strings.

**Referência:** `ref/polish-4-font.png`.

**Causa atual:**
- A hora é um `TextClock` com `Typeface.create("sans-serif-medium")` fixo.
- A data e a saudação usam `AureoleText` com `fontSize` solto, sem a família do tema.

### Comportamento esperado

1. **Modelo puro `ClockFontSpec`** em `composable/clock/model/`, com teste.
   - Mapeia a key para `THEME`, `BOLD`, `SERIF`, `MONO` ou `ROUNDED`.
   - `"SANS_SERIF"`, `"DEFAULT"` e keys desconhecidas viram `THEME`.
2. **Hora (`TextClock`):** com `THEME`, usa o `Typeface` da fonte do tema (`AureoleDS.typography`), via `LocalFontFamilyResolver.current.resolve(fontFamily, FontWeight.Bold).value as Typeface`, com `remember` pela família. As outras opções mantêm os typefaces de sistema de hoje.
3. **Data e saudação:** usam a família do mesmo spec.
   - `THEME` → a família do tema;
   - `SERIF` → `FontFamily.Serif`;
   - `MONO` → `FontFamily.Monospace`;
   - `ROUNDED` → a família criada a partir de `sans-serif-rounded`.
4. **Troca do tema:** ao mudar a fonte em Paleta e Fonte, o relógio da home e o da prévia mudam na hora.
5. **Tiles de fonte:** usam o `AureoleOptionTile` da fase 2, em 2 colunas. O sample é a hora atual escrita na própria fonte, com o rótulo embaixo.
6. **String:** `home_clock_font_default` passa a ser "Theme default" / "Padrão do tema".

### Testes

- **Unitário:** `ClockFontSpec`.
- **Screenshot:**
  - o relógio com uma fonte de tema diferente da padrão;
  - os tiles de fonte.

### Pronto quando

- [ ] Os 5 gates passam.
- [ ] O `visual-reviewer` diz **igual** ou **quase** contra `ref/polish-4`.
- [ ] Testado no aparelho: trocar a fonte do tema e ver hora, data e saudação mudarem.

---

## Fase 4: menus em lista, destrutivo no fim (itens 6 e 7)

**Escopo:**
- `composable/menu/AureoleMenuSheet.kt` e os modelos dele, que hoje põem "Remover" em vermelho no topo e desenham a fileira de ações redondas;
- os menus que usam a `AureoleMenuSheet`: app, pasta, item da grade, grupo e pilha de widgets, botão rápido e adicionar container.

**Referências:** `ref/polish-6-destructive-last.png` e `ref/polish-7-app-menu.png`.

### Comportamento esperado

1. **Destrutivo no fim, na base.** A regra fica na `AureoleMenuSheet`, para todos os menus de uma vez.
   - As linhas destrutivas (Remover, Remover da pasta, Desinstalar) vão para o fim da lista, depois de um divider, em vermelho.
   - Não podem ficar no topo nem soltas no meio.
   - A ordem vem de uma função pura em `model/`, `destructiveLast(items)`, com teste. Ela é estável: preserva a ordem dentro de cada grupo.
2. **Menu do app volta a ser lista vertical.**
   - Sai a fileira de ações redondas com o rodapé de botões ("Segure p/ desinstalar" e "Informações do App"). Cada ação vira uma linha de 56 dp da própria `AureoleMenuSheet`.
   - Ordem:
     1. atalhos do app, na seção "Atalhos";
     2. divider;
     3. Informações do app;
     4. Favorito;
     5. Configurações do container ou da pasta;
     6. divider;
     7. Remover da pasta, quando houver;
     8. Desinstalar, com o subtítulo "Segure para confirmar".
   - O "Desinstalar" mantém o `requiresHold` de 1 s, agora na linha.
   - A sheet abraça a lista até 60%: sem pager, sem `bottomBarContent` e sem espaço vazio entre o conteúdo e o rodapé.
   - Isso substitui a decisão de 11/11 ("menu do app com ação no lado do polegar") e encerra a pendência sobre o lado da mão na `appmenu.png`.
   - Se a fileira de ações redondas ficar sem uso em todos os menus, remova o código e as strings dela.
3. **Desfazer.** Pasta e componente da grade já têm desfazer. Confira o widget stack e o botão rápido: o que ainda remover direto passa a usar o `UndoController`, com 4 s. Desinstalar continua com segurar, porque o sistema já confirma.
4. **Janela própria:** o menu do app continua dentro do `AureoleOverlayWindow`, que corrigiu o crash do long click na lista de favoritos.

### Testes

- **Unitário:** `destructiveLast` e o desfazer que for adicionado.
- **Screenshot:**
  - menu do app com e sem atalhos, dentro e fora de pasta, destro e canhoto;
  - configurações da pasta, pilha de widgets e botão rápido.

### Pronto quando

- [ ] Os 5 gates passam.
- [ ] O `visual-reviewer` diz **igual** ou **quase** contra `ref/polish-6..7`.
- [ ] Nenhum menu tem ação destrutiva antes de uma não destrutiva.
- [ ] O menu do app não tem espaço vazio.

---

## Fase 5: chores (Haiku)

1. Strings que faltarem, em EN e pt-BR, no formato `<feature>_<component>_<description>`.
2. Mover para strings os nomes de cor fixos em PT em `PRESET_TEXT_COLORS` e `PRESET_BG_COLORS` (`EditClockBottomSheet.kt`), com chaves como `home_clock_color_ice_blue`.
3. Adicionar ao `agents/aureole_components.md` uma seção "6. Action sheets e menus" com estas regras, para o problema não voltar:
   - menu é lista vertical, sem pager nem rodapé fixo;
   - a sheet abraça o conteúdo até 60%;
   - ação destrutiva fica no fim, depois de um divider, em vermelho;
   - remoção usa desfazer de 4 s, e desinstalar usa segurar.
4. `@AureolePreview` e screenshots que faltarem.
