# botao-rapido: plano

**Base:** `release/0.9.44` · **Depende de:** `pasta-form` (catálogo e seletor de cor) · **Spec:** `SPEC.md` · **Decisões:** `DECISOES.md` (todas respondidas) ·
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
| 1 | **Modelo e migração** | **Sonnet** | RF-07, RF-04 (JSON) | 45 min–1 h |
| 2 | **Sheet do botão rápido** | **Sonnet** | RF-01 a RF-06, RF-02b, RF-08, RF-09 | 1,5–2 h |

---

## Fase 1: modelo e migração (Sonnet)
1. `QuickButtonAppearance`: `label: String?`, `showLabel = false` (D-01), `icon` vira chave do catálogo (nome antigo do
   enum resolve), `backgroundColor`/`iconColor`/`strokeColor` como `ThemeColorRef` (ARGB antigos viram fixos).
   Leitura do JSON antigo testada; nenhum botão existente muda de visual.

**Commit:** `feat(botao-rapido): nome, ícone do catálogo e cores do tema no modelo (fase 1)`

## Fase 2: sheet (Sonnet)
1. `QuickButtonAppearanceBottomSheet`: nome em pílula com lápis na prévia (RF-01), sem logo (RF-02b), segmentado
   Ícone | Cores (RF-03), `AureoleIconCatalogGrid` (RF-04), cor do ícone/fundo/borda com `AureoleThemeColorPicker`
   (RF-05), aviso de contraste (RF-06), "Mostrar nome na home" (RF-02) e o nome como `contentDescription`.
2. Home: nome embaixo do botão quando ligado. Paisagem (RF-08). Testes (RF-09).

**Commit:** `feat(botao-rapido): sheet com nome, catálogo de ícones e cores do tema (fase 2)`
