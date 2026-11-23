# 🔁 Aureole Workflow: Specs, Fases e Git

Como o trabalho anda neste repo. Complementa os gates de `aureole_testing.md` §3 e "Como Agentes Rodam os Gates"
no `AGENTS.md`.

## 1. Specs (`specs/`, local, fora do git)
- `specs/` está no `.gitignore`: nunca entra em commit.
- **Trabalho novo = spec nova com pasta própria** em `specs/<nome-em-kebab-case>/`. Nunca reabrir ou estender uma
  spec já concluída; um ajuste em algo entregue vira outra spec (ex.: `ajustes-botao-relogio`).
- Arquivos de uma spec, nesta ordem:
  - `IDEIA.md`: o pedido bruto do Mateus, com prints em `prints/` quando houver.
  - `SPEC.md`: requisitos numerados (`RF-xx` ou `RF-A1`, `RF-B2`...).
  - `PLANO.md`: fases, arquivos por fase e mensagem de commit de cada fase.
  - `DECISOES.md`: perguntas abertas (formato abaixo).
  - `PROGRESSO.md`: tabela de fases e "Ficou faltando".
  - `EXECUTAR-<NOME>.md`: prompt pronto para colar no Claude Code.
  - Opcionais: protótipos/mockups `.html` e `codigo/` com código escrito e testado fora do repo.
- O estado de todas as specs fica em [`specs/index.md`](../specs/index.md).

## 2. Decisões (`DECISOES.md`)
```text
## D-01 pergunta curta
opções: A ... | B ...
trava: fase N
resposta: A (dono) | A (padrão proposto) | A (padrão conservador, registrado pelo agente)
```
- "(dono)" = decidido pelo Mateus. "Padrão proposto" pode ser trocado por ele antes da fase que trava.
- Dúvida nova durante a execução: registrar como D-nova, seguir a opção mais conservadora e continuar.
- **Exceção a uma regra de `agents/` só vale registrada numa decisão**, citando a regra (ex.: `jogo-secreto` D-06,
  popup central em vez de bottom sheet).
- Decisão que vira regra para o projeto todo sobe para o guia certo em `agents/`.

## 3. Execução por fases
- Uma sessão do Claude Code executa o `PLANO.md` fase a fase, sem parar para perguntar.
- Leitura inicial: `AGENTS.md`, todos os `.md` de `agents/`, e `SPEC.md`, `PLANO.md`, `DECISOES.md` da spec.
  Depois, só os arquivos que vai mudar. Protótipos `.html` e drawables grandes de `codigo/` não são abertos: `cp`.
- Durante as fases: só `./gradlew compileDebugKotlin` e testes filtrados (`--tests "*Nome*"`). Gates completos na
  fase final (G).
- Ao fim de cada fase: marcar o `PROGRESSO.md`. Item que não fechou vai para "Ficou faltando" e a execução segue.
- `PROGRESSO.md` tem colunas Gates, Visual e Aparelho. **Aparelho é marcado só pelo Mateus**, depois de testar no
  celular.
- Mudança visual sempre termina com prints (screenshot tests ou `prints-resultado/`) para validar.

## 4. Git
- Trabalho na branch da release em andamento: `release/x.y.z` (ex.: `release/0.9.48`).
- Commit por fase, com a mensagem do `PLANO.md`, **só no nome do Mateus**: sem `Co-Authored-By`, sem menção a Claude.
- `git add` com pathspec explícito; nunca adicionar `specs/`, `.claude/` ou `.atividades/`.
- **Nunca `git push`.** O push é sempre do Mateus, quando ele sobe a release.

## 5. Memória do projeto
- Regra durável (arquitetura, UI, testes, produto, processo) → o guia certo em `agents/` + linha em `agents/index.md`.
- Estado de trabalho (fases, pendências, branch) → `specs/index.md` e o `PROGRESSO.md` da spec.
- Ao fechar uma spec ou fase, atualizar `specs/index.md` no mesmo passo.
- Ao criar ou remover um componente público, token, feature, repositório ou chave persistida, atualizar a linha do
  mapa em `agents/mapa/` no mesmo passo.
