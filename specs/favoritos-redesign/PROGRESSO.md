# Progresso: favoritos-redesign

Gates e revisão visual: na fase G do lote (`../EXECUTAR-LOTE-17-11.md`).

| # | Fase | Modelo | Estimativa | Feita |
|---|---|---|---|---|
| 1 | Lista única (direção A) | Opus | 2–3 h | [x] |

## Notas por fase

### Fase 1 (17/11)
- RF-01: a sheet tem uma `LazyColumn` só (chips, aviso, "Favoritos · N", favoritos, "Todos os apps · N", apps); a
  prévia virou `LazyColumn(userScrollEnabled = false)`.
- RF-A1: `FavoriteAppsListHeader` são dois `FilterChip` numa linha ("Mostrar todos os apps", "Rótulos"), primeiro
  item da lista (como no protótipo, rolam junto); "Rótulos" fica desabilitado com "Mostrar todos" desligado (antes
  sumia). Sem switches no Ajustes. O subtítulo do switch (`favoritesMenuSubtitle`) saiu, o aviso cobre a informação.
- RF-A2: `stickyHeader` "Favoritos · N" (com "segure ⋮⋮ para ordenar" a partir de 2, fora da busca, e "Remover
  todos") e "Todos os apps · N" (N = apps fora dos favoritos, filtrados). Fundo `surface` opaco.
- RF-02: novo slot `headerLeadingAction` no `AureoleEditOverlayConfig`/`OverlaySheetHeader`, do lado oposto à logo
  e espelhado no canhoto; o lápis (`FavoriteAppsCustomizeHomeButton`, 48 dp) entra só com `onCustomizeHome`. A linha
  "Personalizar home" saiu da lista. Ajustes: sem lápis nem logo (RF-07 da polimento-ui mantido).
- RF-03: `favoritesEmptyNotice` (puro, testado) + `FavoriteAppsEmptyNotice` numa linha (`maxLines = 1`), só com 0
  favoritos e fora da busca; texto segue "Mostrar todos" (mostra todos os N apps × lista fica vazia na Home).
- RF-04: busca segue fixa no rodapé; filtro extraído para `favoriteAppsLists`/`filterAppsByQuery`/`filteredBy`.
- RF-05: paisagem já usa o `SideBySidePreviewLayout` (prévia à esquerda); com os chips dentro da lista, o topo fixo é
  só a linha do título (~56 dp).
- RF-06: reordenar, remover, adicionar, Remover todos, busca e Ajustes mantidos; testes antigos ajustados.
- Strings sem uso removidas (contador antigo, banner, dica de arrastar, subtítulos do switch, "Mostrar rótulos").
- Testes: `FavoriteAppsListRulesTest` (novo), `FavoriteAppsDialogSectionsTest`, `FavoriteAppsDialogTest`,
  `FavoriteAppsListTest` (38 testes verdes com `--tests`); `FavoritesMenuSubtitleTest` removido com a função.
- Screenshot: casos novos em `FavoriteAppsManageSheetScreenshotTest` (retrato 0 e 3 favoritos, canhoto, paisagem 0
  e 3) e peças atualizadas em `FavoriteAppsManageListScreenshotTest`.

## Ficou faltando
- Referências de screenshot (novas e alteradas) não foram geradas nem validadas, e o source set `screenshotTest` não
  foi compilado (fase sem gates): gerar/validar na fase G.
- Os ~70 dp do topo em paisagem não foram medidos num aparelho; conferir na revisão visual da fase G.

## Revisão visual 17/11 (fase G, 2ª passada)
- RF-05: **bug real**: em paisagem o topo da sheet (título + chips) tinha ~125 dp e a lista mal aparecia. Em paisagem (lado a lado) os chips saem da lista e vão para a linha da busca, no rodapé (`FavoriteAppsBottomBar(options)`, `FavoriteAppsSheetContent(showOptions = false)`, `FavoriteAppsOverlayState.isSideBySide`); o topo fica só com o título (~80 dp com a faixa de gesto de 20 dp) e os apps aparecem sem rolar com 3 e com 0 favoritos. O placeholder da busca é de uma linha (reticências).
- RF-03: **bug real**: com 0 favoritos aparecia o aviso e também "Nenhum app favorito selecionado". Agora só o aviso; o texto fica para favoritos salvos que não estão instalados.
- RF-A1: chip "Rótulos" sumido era **artefato do teste** (sem `showLabels`; a home sempre passa os dois). O check invisível no chip marcado era **bug real** (o ícone tem cor própria e ignorava a do chip): agora usa a cor `surface`.

### Ficou faltando
- O topo em paisagem ficou ~80 dp, não ~70 dp: o resto é a faixa de gesto (20 dp) e os slots de 48 dp do título. Com a faixa de 10 dp em andamento no working tree, fica ~70 dp.
