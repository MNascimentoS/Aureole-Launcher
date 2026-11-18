# Executar: lote 17/11 (5 specs)

Cinco specs em sequência, num só prompt do Claude Code. Todas as decisões estão respondidas. Cada fase roda num
subagente com o modelo da tabela; **gates uma vez só, na fase G, depois de todas as specs**; um commit por fase,
sem atribuição, **sem push**.

**Branch:** `release/0.9.44` · **Protótipo:** `prototipo-lote-17-11.html`

| Ordem | Spec | Fase | Modelo | Tempo |
|---|---|---|---|---|
| 1 | `polimento-ui` | 1 · Linhas e menus | **Sonnet** | 1–1,5 h |
| 2 | `polimento-ui` | 2 · Fundo dentro da Aparência | **Opus** | 1,5–2,5 h |
| 3 | `polimento-ui` | 3 · Logo, anel e pasta aberta | **Opus** | 2–3 h |
| 4 | `ajustes-onboarding` | 1 · Paleta/fonte, relógio e container | **Sonnet** | 1–1,5 h |
| 5 | `ajustes-onboarding` | 2 · Vitrine, espiada e shared element | **Opus** | 2,5–3,5 h |
| 6 | `ajustes-onboarding` | 3 · Ajustar layout: pílula Salvar | **Sonnet** | 1–1,5 h |
| 7 | `favoritos-redesign` | 1 · Lista única | **Opus** | 2–3 h |
| 8 | `pasta-form` | 1 · Catálogo, cores e modelo | **Sonnet** | 1,5–2 h |
| 9 | `pasta-form` | 2 · Sheet da pasta | **Opus** | 2,5–3,5 h |
| 10 | `botao-rapido` | 1 · Modelo e migração | **Sonnet** | 45 min–1 h |
| 11 | `botao-rapido` | 2 · Sheet do botão rápido | **Sonnet** | 1,5–2 h |
| G | lote | Gates, correções, revisão visual e prints | **Sonnet** (+ revisão **Opus**) | 1,5–2,5 h |

**Total:** ~19–27 h de agente, ~1,1–1,6 M de tokens.

## Antes de rodar

1. `git checkout release/0.9.44` e `git status` limpo (fora `specs/`).
2. Opcional, para garantir commits sem coautor: `.claude/settings.local.json` com `{ "includeCoAuthoredBy": false }`.
3. No painel: nada a fazer; o foco segue a spec da sessão. Se tinha fixado uma com `/spec <nome>`, rode `/spec auto`.

## Se os tokens acabarem: rodar com retomada automática

Quando o limite de uso acaba, a sessão do Claude Code para e não volta sozinha; quem retoma é um laço de fora.
Em vez de colar o prompt, rode na raiz do repo, num terminal que fique aberto (ou dentro de `tmux`/`screen`):

```
CLAUDE_FLAGS="--permission-mode acceptEdits" bash specs/rodar-lote-17-11.sh
```

- Faz o checkout de `release/0.9.44`, roda o prompt abaixo e, se a sessão parar antes do fim, espera **20 min**
  (`ESPERA_MIN`) e retoma com `claude -c` ("continue de onde parou", lendo git log e PROGRESSO.md), até 24 h
  (`MAX_TENTATIVAS`).
- Para quando existir `specs/lote-17-11-FEITO.md` (a fase G cria no fim, com o relatório) ou `specs/lote-17-11-PARAR`.
- Log em `specs/lote-17-11-execucao.log`.
- `CLAUDE_FLAGS`: sem permissões liberadas, a sessão para esperando aprovação e o laço não resolve isso. Use o modo
  de permissão ou a allowlist que você já usa no projeto.

Dentro da sessão vale a regra do prompt: limite de taxa ou sobrecarga de um subagente → esperar e tentar de novo.

## Prompt (colar no Claude Code, na raiz do repo)

```
Execute o lote de specs em specs/EXECUTAR-LOTE-17-11.md, do começo ao fim, sem parar para perguntar, na branch
release/0.9.44. Ordem: polimento-ui (fases 1-3) → ajustes-onboarding (1-3) → favoritos-redesign (1) → pasta-form (1-2)
→ botao-rapido (1-2) → fase G (gates do lote).

Leia antes: AGENTS.md, todos os .md de agents/, e em cada spec SPEC.md, PLANO.md e DECISOES.md (todas respondidas:
siga-as). Use specs/prototipo-lote-17-11.html como referência visual (abas 1 a 5).

Cada fase roda em um subagente com o modelo da tabela do EXECUTAR, uma de cada vez, esperando a anterior terminar.
Passe a cada subagente: o texto da fase no PLANO.md, os RF dela no SPEC.md, as decisões e as Regras de execução.
A descrição (description) de cada subagente começa com o nome da spec e "Fase N:", por exemplo
"polimento-ui · Fase 2: Fundo dentro da Aparência"; a fase final é "lote-17-11 · Fase G: gates".
Antes de cada fase, leia o PLANO.md da spec dela.

Regras (todas as fases):
- NENHUM gate nas fases 1-11. Só ./gradlew compileDebugKotlin e os testes da própria fase com --tests. Um comando
  Gradle por chamada, nome do task por extenso, em segundo plano. Nunca editar com build rodando.
- Ao fim de cada fase: atualizar o PROGRESSO.md da spec (notas e coluna "Feita") e commitar com a mensagem do PLANO.md.
  Commits SEM nenhuma linha de atribuição: sem "Co-Authored-By", sem "Generated with Claude Code", sem link de sessão.
  NUNCA git push.
- Regras do projeto: MAX-500 linhas (800 em testes), sem @Suppress, AureoleDS para cores/dimens/ícones, strings em
  values/ e values-pt-rBR/, @AureolePreview privado, testes unitários junto do código novo.
- Dependências: ajustes-onboarding usa a logo no título e o AureoleLogoRing da polimento-ui; botao-rapido usa o
  AureoleIconCatalog, o AureoleIconCatalogGrid e o AureoleThemeColorPicker da pasta-form. Não duplicar.
- Dúvida de produto nova: registrar em DECISOES.md da spec (D-nova), seguir com a opção mais conservadora e continuar.
- Item que não fechar: anotar em "Ficou faltando" no PROGRESSO.md, commitar o que ficou pronto e seguir.
- Retomada: se um subagente ou comando falhar por limite de uso, limite de taxa ("rate limit", 429), sobrecarga
  (529/"overloaded") ou queda de rede, NÃO desista nem pule a fase: espere (sleep 300, depois 600, depois 1200 e
  segue em 1200) e tente de novo, quantas vezes precisar. Commite sempre ao fim de cada fase e mantenha o PROGRESSO.md
  em dia, para que uma sessão nova possa continuar do ponto exato.
- Ao terminar a fase G, crie specs/lote-17-11-FEITO.md com o relatório final abaixo (é o sinal de fim para o script
  specs/rodar-lote-17-11.sh) e commite.

Fase G (subagente "sonnet"; revisão visual em subagente "opus"):
- Gates na ordem, um por chamada: ./gradlew detekt --auto-correct, ./gradlew lintDebug, ./gradlew testDebugUnitTest,
  ./gradlew jacocoTestCoverageVerification, ./gradlew updateDebugScreenshotTest, ./gradlew validateDebugScreenshotTest,
  ./gradlew assembleDebug. Corrigir na causa até todos passarem (sem @Suppress, baseline ou regra afrouxada).
  Correções viram commits "fix(<spec>): ... (fase G)".
- Prints: copiar para specs/lote-17-11-prints/<spec>/ os screenshots de referência das telas novas/alteradas (retrato e
  paisagem, canhoto quando houver), com um README.md listando cada arquivo e o RF que ele mostra.
- Revisão visual (opus): comparar os prints com o protótipo e com os RF; veredito por tela.

No fim (e também em specs/lote-17-11-FEITO.md), me entregue, separado por spec:
1. git log --oneline dos commits da spec;
2. a checagem de atribuição: git log --format=%B release/0.9.44@{1}..HEAD | grep -iE "co-authored|generated with|claude"
   (vazio = ok; use o primeiro commit do lote como início se o reflog não servir);
3. tabela dos 7 gates (passou/falhou, tempo) da fase G;
4. veredito visual por tela e o caminho dos prints;
5. o que ficou faltando e as decisões novas;
6. o que testar no aparelho (lista curta por spec).
```

## Se a execução já estiver rodando e uma spec mudar

```
Specs do lote 17/11 foram atualizadas. Releia SPEC.md, PLANO.md e DECISOES.md das specs ainda não concluídas (versões
em disco) antes de continuar. Se uma fase já commitada não cobre um RF novo, implemente o que falta num commit
complementar "(fase N)". Depois siga a ordem e as regras de specs/EXECUTAR-LOTE-17-11.md.
```
