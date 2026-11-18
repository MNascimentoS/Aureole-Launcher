# Progresso: pasta-form

Gates e revisão visual: na fase G do lote (`../EXECUTAR-LOTE-17-11.md`).

| # | Fase | Modelo | Estimativa | Feita |
|---|---|---|---|---|
| 1 | Catálogo, cores e modelo | Sonnet | 1,5–2 h | [x] |
| 2 | Sheet da pasta | Opus | 2,5–3,5 h | [x] |

## Notas por fase

### Fase 1 (RF-12, RF-14, RF-15, RF-16)
Só `compileDebugKotlin` e os testes da fase (catálogo, ThemeColorRef, modelos da grade e do seletor, migração Room,
BackgroundSwatches) rodaram, todos verdes. Gates completos ficam para a fase G.
- **Componentes compartilhados (públicos):**
  - `core/designsystem/icons/AureoleIconCatalog` (`entries`, `categories`, `resolveKey(raw)`, `resFor`, `categoryOf`,
    `byCategory`, `actionKey(QuickButtonIcon)`), `AureoleIconCategory` (ACTIONS + 8), `AureoleIconEntry`. 107 ícones: 80 da
    pasta (chaves antigas) + 27 de Ações (`action_<nome>`; CREATE e EDIT dividem `action_edit`, mesmo drawable).
    `resolveKey` aceita chave nova, chave antiga da pasta (inclui legadas `add`, `edit`, `more_vert`, `place`...) e o nome
    do enum `QuickButtonIcon` em maiúsculas (`FAVORITE` -> `action_favorite`; `favorite` continua o coração da pasta).
  - `composable/icons/AureoleIconCatalogGrid(selectedKey, onSelect, modifier, isLeftHanded)` + modelos em
    `composable/icons/model/IconCatalogGridModels.kt`. Exige altura limitada do pai (`heightIn(max = ...)`), sem scroll
    vertical em volta.
  - `core/designsystem/palette/ThemeColorRef` (`Role(ThemeColorRole)` | `Fixed(argb)`, `resolve(AureoleColors)`,
    `toJson()`, `parseThemeColorRef(json)`, `readableOn(background, colors)` para contraste). ACCENT = `onSurfaceMedium`.
  - `composable/color/AureoleThemeColorPicker(selected: ThemeColorRef?, onSelect, modifier)` e as linhas extraídas do
    Fundo, agora públicas: `AureoleThemeSwatchRow`, `AureolePaletteSwatchRow` (o Fundo usa as mesmas).
- **Modelo (RF-12):** `AppFolder` ganhou `iconType: FolderIconType` (ICON/LETTER/EMOJI, padrão ICON), `emoji` e
  `color: ThemeColorRef?`. Room v3 -> v4 (`MIGRATION_3_4`: `icon_type TEXT NOT NULL DEFAULT 'ICON'`, `emoji TEXT`);
  `color` já existia em `folders` (sempre nulo) e passou a guardar o JSON do `ThemeColorRef`. `schemas/4.json` gerado.
  `AppDatabaseMigrationTest` ajustado (volta também `folders` para a forma antiga) e com 3 testes novos.
- **Home:** `FolderGlyph` (feature/home/.../folder) desenha ícone, letra e emoji; o botão da pasta no container e a
  mini-grade usam a cor própria da pasta (fundo) e contraste automático do glifo.

### Fase 2 (RF-01 a RF-11, RF-13, RF-04b)
Só `compileDebugKotlin` e os testes da fase (`composable.dialogs.*`, `composable.icons.*`, `FolderDetailsTest`,
`FolderRepositoryTest`, `feature.home.components.containers.*`, `ContainerFolderReactivityTest`: 258 testes) rodaram,
todos verdes. Gates completos, screenshots e revisão visual ficam para a fase G.
- **Sheet** (`composable/dialogs/FolderFormDialog.kt`): nome na pílula embaixo da prévia (`FolderNamePill` em
  `FolderFormComponents.kt`: fundo leve, borda que acende focada, lápis que vira "×", "Dar um nome", TalkBack "Nome,
  <valor>, editar"); nova pasta abre focada com teclado, editar sem. Sem campo separado, sem logo (RF-04b já valia:
  `onSettingsClick = null`). Corpo num scroll só (`FolderFormSections.kt`): segmentado Ícone | Letra | Emoji, cor,
  "Excluir pasta" no fim depois de um divider (linha destrutiva, sem quadrado). Paisagem e canhoto vêm do
  `AureoleEditOverlay` (prévia de um lado, sheet do outro).
- **Ícone:** "Sugeridos para "<nome>"" com 5 ícones de um dicionário local PT/EN
  (`model/FolderIconSuggestions.kt`, sem acento/caixa, plural com "s"); o primeiro vem marcado; o ícone atual entra em
  primeiro só se não estiver entre os sugeridos (tocar num sugerido não reordena). "Todos os ícones" troca o conteúdo
  da sheet pela `AureoleIconCatalogGrid` (título "Todos os ícones", linha "Voltar para a pasta", voltar do sistema
  volta ao formulário); escolher volta ao formulário.
- **Letra:** primeira letra do nome, na fonte do tema. **Emoji:** 11 comuns + "+" (abre um campo com teclado; vale o
  último emoji digitado, `lastGrapheme`); emoji digitado entra em primeiro na grade.
- **Cor (RF-10):** `AureoleThemeColorPicker` reaproveitado; tocar de novo na cor marcada volta à cor do container (D-08).
  Contraste automático pelo `FolderGlyph`/`readableOn`.
- **Criar (RF-11):** na home, "Criar e escolher apps" cria, abre a pasta e já abre o seletor de apps
  (`MainUiState.afterFolderCreated`). Em "Editar container" e em Ajustes o botão é "Criar" (D-06). Editar salva ao
  fechar só se algo mudou (compara com o rascunho inicial; pasta de ícone sem ícone abre em Letra, como a home desenha).
- **Dados:** `FolderDetails` (nome, ícone, tipo, emoji, cor) + `AppFolder.details()/withDetails()`.
  `FolderDao.updateFolderDetails(FolderDetailsUpdate)` (update parcial de `folders`) grava tudo; `renameFolder` só o
  nome. Intents `SubmitFolderName(details, pickApps)` e `RenameFolder(folderId, details)`; o desfazer da edição volta
  todos os campos. `EditContainerState.addFolder(details)` e `SettingsViewModel.createFolder(details)` também.
- **Ficou-faltando da fase 1, resolvido:** escrita de tipo/emoji/cor na edição; `FolderIconRegistry` desenha pelo
  catálogo único (`folderIconCatalogKey`: chave do catálogo, chave antiga, nome do enum ou nome aceito de pasta) e só
  guarda o lixo (`delete`) desenhado pelo ícone do DS; `FolderIconGrid` e os modelos antigos da grade saíram; a linha
  da pasta em "Editar container" desenha a pasta como na home (`FolderBadge`). `FolderGlyph` foi para
  `composable/dialogs` (compartilhado).
- **Bug achado e corrigido:** a migração das pastas antigas (JSON em prefs) quebrava com `iconType` nulo (Gson ignora o
  padrão do Kotlin); agora lê por `LegacyFolderJson`.
- Strings novas em `values/` e `values-pt-rBR/`; `home_folder_name_label` e `home_folder_icon` saíram (sem uso).

## Ficou faltando
- Screenshots: `FolderFormScreenshotTest` foi reescrito (nova, editar, emoji, prévia) e as chamadas dos outros testes
  de tela ajustadas, mas o source set de screenshot não foi compilado aqui e as referências precisam ser geradas de novo
  na fase G.
- Revisão visual na fase G: com o teclado aberto em retrato a prévia (pasta + pílula) fica entre o topo e a sheet e
  pode ficar apertada em telas baixas; conferir também a altura da grade em "Todos os ícones" (em janela de teste baixa
  ela ficou sem espaço, por isso o teste usa janela alta).
- Sugestões pela categoria dos apps dentro da pasta (RF-09, edição): `AppInfo` não tem categoria; segue só pelo nome
  (D-07).
- O popup da pasta aberta não desenha ícone/cor da pasta (fora do escopo da spec; nada mudou ali).

## Revisão visual 17/11 (fase G, 2ª passada)
- RF-01/RF-02/RF-05: os prints `FolderIconGrid*` eram **referências órfãs** do `FolderIconGrid` antigo (código e testes já removidos na fase 2). "Todos os ícones" usa a `AureoleIconCatalogGrid` compartilhada (`FolderAllIconsBody`). Referências apagadas; novo screenshot da grade dentro da sheet (`FolderAllIcons*`, via `FolderFormParams.initialMode = ALL_ICONS`).
- RF-06: screenshot novo do fim do formulário com "Excluir pasta" depois do divider (`FolderFormDeleteAtEndScreenshotTest`).
- RF-10: **bug real** pequeno: "Cor da pasta" e os rótulos do seletor ("Do seu tema", "De outras paletas") tinham recuo e estilo diferentes. O `AureoleThemeColorPicker` passa a usar o `AureoleMenuSectionLabel` (vale também para o botão rápido).

### Ficou faltando
- Chevron de "Todos os ícones" no canhoto: a linha inteira espelha (chevron à esquerda, apontando para fora), como todas as linhas de menu no canhoto. É a convenção atual; mudar só aqui seria inconsistente.
- Subtítulo "107 ícones…" cortado pelo "Criar" em paisagem: é o corpo rolável passando por baixo do botão fixo (não é corte de layout). Um fade na borda seria polimento à parte.
