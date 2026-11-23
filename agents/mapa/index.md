# Mapa do código (leitura sob demanda)

Resumo do que já existe no código, para não reler arquivos. Descreve o código, não manda nele: as regras estão nos guias `agents/aureole_*.md`.
Leia este índice primeiro e depois só o mapa da área que a tarefa toca. Antes de criar componente, token ou repositório, procure aqui.
Ao criar ou remover algo público, atualize a linha do mapa no mesmo passo (`aureole_workflow.md` §5).

| Arquivo | Leia quando |
|---|---|
| [componentes.md](componentes.md) | for criar ou mexer em sheet, menu, botão, seletor, dialog, lista, relógio ou widget |
| [design-system.md](design-system.md) | precisar de um token (dimens, colors, typography), ícone ou paleta |
| [features.md](features.md) | mexer em `home`, `settings` ou `secretgame`: ViewModel, UiState, e a ordem em que sheets e dialogs abrem |
| [dados.md](dados.md) | tocar em repositório, Room, preferência, Koin ou lifecycle observer |
| [receitas.md](receitas.md) | fizer uma tarefa recorrente: sheet, string, token, preferência, screenshot test, teste JVM |

Caminhos nos mapas são relativos a `app/src/main/java/dev/mnascimentos/aureole/`, salvo quando começam com `app/`.

## Conflitos (código x regras de `agents/`)
Os itens "Resolvidos" saíram da spec local `specs/alinhar-codigo-regras/`. Os "Aceitos" ficam como estão por decisão do Mateus.

### Resolvidos pela spec `alinhar-codigo-regras` (sem mudar comportamento)
| # | Era | Agora |
|---|---|---|
| 1 | `@file:Suppress` em `AureoleIcons.kt`, `ThemePaletteBase.kt`, `ThemePalettePresets.kt`; `@SuppressLint` em `GridItemContent.kt` | Removidos. Ícones divididos em `AureoleIconsPart1..7.kt`; paletas em linhas `presetPalette(...)` |
| 2 | `ThemePalettePresets.kt` com 1324 linhas | 135 linhas |
| 3 | dp/hex inline fora do DS | Tokens onde havia valor igual; o resto são constantes de `model/*Metrics.kt` e valores de preview (permitidos em `aureole_design_system.md` §1) |
| 4 | Repositórios criavam DB/`IconCache` por dentro | `AppRepository` e `SettingsRepository` recebem DAO/`IconCache`/banco do Koin. `Container/Favorite/FolderRepository` já faziam isso; os construtores com `Context` ficam só como atalho de testes |
| 5 | `OverlaySheetHeader` sem `modifier` | Tem `modifier: Modifier = Modifier` |
| 6 | Nomes com "Preview" | `FolderFormGlyphCard`, `EditWidgetGroupCarousel` |

### Aceitos (não corrigir)
| # | Regra | O que o código faz | Motivo |
|---|---|---|---|
| 7 | `SmallText` 13 sp (`aureole_design_system.md` §3) | `smallText` tem 14 sp | Mudar altera o texto do app todo; decisão do Mateus: ignorar |
| 8 | Migrações Room | Sem 1→2 nem `1.json`/`2.json`; versão antiga cai em migração destrutiva | Mudaria contrato dos dados; ignorar |
| 9 | Strings só em resources | Fallback `"0.3.13"` em `core/lifecycle/MainLifecycleObserver.kt` | Só vale se a leitura da versão falha; trocar muda comportamento |
| 10 | Strings só em resources | Literais em previews | Virou exceção registrada em `aureole_components.md` §4 |
| 11 | `OverlaySheetHeader` e `AureoleMenuEntries` | São `internal` | Intencional: só usados no módulo |
