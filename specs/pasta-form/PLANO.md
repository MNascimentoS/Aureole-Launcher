# pasta-form: plano

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
| 1 | **Catálogo, cores e modelo** | **Sonnet** | RF-14 a RF-16, RF-12 | 1,5–2 h |
| 2 | **Sheet da pasta** | **Opus** | RF-01 a RF-11, RF-13, RF-04b | 2,5–3,5 h |

---

## Fase 1: catálogo, cores e modelo (Sonnet)
1. `core/designsystem/icons/AureoleIconCatalog` (RF-14): 80 da pasta + 28 do botão (categoria Ações), chaves estáveis,
   resolução das chaves antigas (pasta e enum `QuickButtonIcon`). Testes.
2. `AureoleIconCatalogGrid` (RF-15): `LazyVerticalGrid` com seções, `stickyHeader`, chips fixos que rolam a grade e
   acendem pela rolagem, marcado preenchido, canhoto espelhado.
3. `ThemeColorRef` + `AureoleThemeColorPicker` (RF-16), resolvendo com o tema atual. Testes.
4. `AppFolder` (RF-12): `iconType` (ICON/LETTER/EMOJI), `emoji`, `color: ThemeColorRef?`; Room migration + teste;
   container, popup e mini-grade desenham os três tipos e a cor.

**Commit:** `feat(pasta-form): catálogo único de ícones, seletor de cor do tema e pasta com tipo e cor (fase 1)`

## Fase 2: sheet da pasta (Opus)
1. `FolderFormDialog`: nome em pílula com lápis na prévia (RF-08; nova pasta abre com teclado, editar sem), sem
   campo separado, sem logo (RF-04b).
2. Segmentado Ícone | Letra | Emoji (RF-09): sugeridos pelo nome (dicionário local PT/EN, testado) + "Todos os ícones"
   trocando o conteúdo pela `AureoleIconCatalogGrid`; letra; emoji.
3. Cor (RF-10) com `AureoleThemeColorPicker`; contraste automático do ícone/letra.
4. "Criar e escolher apps" (RF-11, D-02) abre o seletor de apps da pasta; editar salva ao fechar.
5. Um scroll só, sem buraco, respiro lateral, excluir no fim sem quadrado, paisagem (RF-01 a RF-07, RF-13).

**Commit:** `feat(pasta-form): pasta com nome primeiro, ícone/letra/emoji e cor do tema (fase 2)`
