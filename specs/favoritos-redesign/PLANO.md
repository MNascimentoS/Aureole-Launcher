# favoritos-redesign: plano

**Base:** `release/0.9.44` · **Spec:** `SPEC.md` · **Decisões:** `DECISOES.md` (todas respondidas) ·
**Protótipo:** `../prototipo-lote-17-11.html` · **Como rodar:** `../EXECUTAR-LOTE-17-11.md`

## Regras de execução

1. Fases na ordem da tabela, cada uma num subagente com o modelo indicado, uma de cada vez.
2. **Nenhum gate nesta spec.** Os gates rodam uma vez só, na fase final do lote (`../EXECUTAR-LOTE-17-11.md`, fase G).
   Nas fases daqui: só `./gradlew compileDebugKotlin` e os testes da própria fase com `--tests`. Um comando Gradle por
   chamada, nome por extenso, em segundo plano; nunca editar com build rodando.
3. Commit no fim de cada fase com a mensagem indicada. **Sem** `Co-Authored-By`, sem "Generated with Claude Code", sem
   link de sessão. **Nunca** `git push`.
4. Regras do projeto (`AGENTS.md` + `agents/`): MAX-500 linhas (800 em testes), sem `@Suppress`, `AureoleDS`, strings
   em `values/` e `values-pt-rBR/`, `@AureolePreview` privado, testes unitários junto do código novo, lógica de
   ViewModel testada em JVM puro.
5. Notas em `PROGRESSO.md`; dúvida nova vira D-nova em `DECISOES.md` com a opção conservadora, e segue.

| # | Fase | Modelo | Requisitos | Tempo |
|---|---|---|---|---|
| 1 | **Lista única (direção A)** | **Opus** | RF-01 a RF-06, RF-A1, RF-A2 | 2–3 h |

---

## Fase 1: lista única (Opus)
1. `FavoriteAppsDialogSections`: um único `LazyColumn` (RF-01) com `stickyHeader` "Favoritos · N" (dica de ordenar) e
   "Todos os apps · N" (RF-A2); prévia sem scroll.
2. `FavoriteAppsListHeader`: os dois switches viram chips numa linha (RF-A1); "Rótulos" desabilitado sem "Mostrar todos".
3. Título: lápis "Personalizar home" no início da linha e logo no fim (RF-02); some a linha "Personalizar home" da
   lista; na sheet dos Ajustes nenhum dos dois.
4. Aviso só com 0 favoritos, numa linha (RF-03); busca fixa embaixo (RF-04); paisagem com prévia à esquerda (RF-05).
5. Nada some (RF-06): reordenar, remover, adicionar, busca, comportamento dos Ajustes. Testes do filtro e do aviso.

**Commit:** `feat(favoritos-redesign): Escolher favoritos com lista única, chips e cabeçalhos fixos (fase 1)`
