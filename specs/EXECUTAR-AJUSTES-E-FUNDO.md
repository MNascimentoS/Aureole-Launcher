# Executar: ajustes-redesign → editar-foto-fundo

Duas specs, uma depois da outra, num só prompt do Claude Code. Todas as decisões estão respondidas. Cada fase roda num
subagente com o modelo da tabela; gates só no fim de **cada** spec; um commit por fase, sem atribuição, sem push.

| Ordem | Spec | Fase | Modelo | Tempo |
|---|---|---|---|---|
| 1 | `ajustes-redesign` | 1 · Raiz em cartões e conteúdo reagrupado | **Sonnet** | 1,5–2,5 h |
| 2 | `ajustes-redesign` | 2 · Áreas como bottom sheet | **Opus** | 2,5–4 h |
| 3 | `ajustes-redesign` | 3 · Ajustes do teste no aparelho | **Opus** | 2–3 h |
| 4 | `ajustes-redesign` | 4 · Gates, correções, revisão visual | **Sonnet** (+ revisão **Opus**) | 1–1,5 h |
| 5 | `editar-foto-fundo` | 1 · Ajustes salvos e desenho do fundo | **Sonnet** | 1,5–2,5 h |
| 6 | `editar-foto-fundo` | 2 · Editor (gestos, escurecer, desfoque, fluxo, Desfazer) | **Opus** | 2,5–4 h |
| 7 | `editar-foto-fundo` | 3 · Tela de bloqueio | **Sonnet** | 1,5–2 h |
| 8 | `editar-foto-fundo` | 4 · Gates, correções, revisão visual | **Sonnet** (+ revisão **Opus**) | 1–1,5 h |

**Total:** 13,5–21 h de agente, ~0,8–1,4 M de tokens.

## Antes de rodar

1. A `ajustes-sheets-settings` tem de estar com as fases commitadas (está: gates passaram na fase 4, fase 5 entregue).
   `git status` limpo, fora `specs/`.
2. Branch: continuar na mesma da spec anterior, ou criar `feat/ajustes-e-fundo` a partir dela.
3. Opcional, para garantir commits sem coautor: `.claude/settings.local.json` com `{ "includeCoAuthoredBy": false }`
   (ou `{ "attribution": { "commit": "", "pr": "" } }`, conforme a versão).
4. No painel: nada a fazer. O foco segue a sessão sozinho (arquivos da spec, commits `feat(<spec>)`, prompt que cita
   `specs/<nome>/`). Se você tinha fixado uma spec com `/spec <nome>`, rode `/spec auto` uma vez.

## Prompt (colar no Claude Code, na raiz do repo)

```
Execute duas specs em sequência, do começo ao fim, sem parar para perguntar:
  A) specs/ajustes-redesign/      (fases 1, 2, 3 e 4)
  B) specs/editar-foto-fundo/     (fases 1, 2, 3 e 4) — só começa depois que A terminar com os gates passando.

Leia antes: AGENTS.md, todos os .md de agents/, e em cada spec os arquivos SPEC.md, PLANO.md, DECISOES.md (todas as
decisões estão respondidas: siga-as) e o prototipo.html como referência visual (em ajustes-redesign, a aba
"B · Cartões ✓"). Em ajustes-redesign leia também IDEIA.md (inventário que não pode perder item) e PROPOSTAS-IA.md
(crítica da fase anterior, só como contexto).

Cada fase roda em um subagente com o modelo indicado, uma de cada vez, esperando a anterior terminar:
  A1 Raiz em cartões e conteúdo reagrupado ............ model "sonnet"
  A2 Áreas como bottom sheet ........................... model "opus"
  A3 Ajustes do teste no aparelho ..................... model "opus"
  A4 Gates, correções e revisão visual ................ model "sonnet"; revisão visual em subagente "opus"
  B1 Ajustes salvos e desenho do fundo ................ model "sonnet"
  B2 Editor da foto ..................................... model "opus"
  B3 Tela de bloqueio ................................... model "sonnet"
  B4 Gates, correções e revisão visual ................ model "sonnet"; revisão visual em subagente "opus"
Passe a cada subagente: o texto da fase no PLANO.md, os RF dela no SPEC.md, as decisões e as Regras de execução.
A descrição (description) de cada subagente começa com o nome da spec e "Fase N:", por exemplo
"ajustes-redesign · Fase 2: áreas como bottom sheet" e "editar-foto-fundo · Fase 3: tela de bloqueio": o painel
Órbita usa isso para mostrar a fase em execução. Antes de começar cada fase, leia o PLANO.md da spec dela (o painel
troca o foco para a spec que a sessão está usando).

Regras (todas as fases):
- Gates SÓ nas fases de gates (A4 e B4). Nas outras, só ./gradlew compileDebugKotlin e os testes da própria fase com
  --tests. Um comando Gradle por chamada, com o nome do task por extenso, em segundo plano. Nunca editar com build
  rodando. Gates na ordem: detekt --auto-correct, lintDebug, testDebugUnitTest, jacocoTestCoverageVerification,
  updateDebugScreenshotTest, validateDebugScreenshotTest, assembleDebug; corrigir na causa até todos passarem
  (sem @Suppress, baseline ou regra afrouxada).
- Ao fim de cada fase: atualizar o PROGRESSO.md da spec (notas e coluna "Feita") e commitar com a mensagem do PLANO.md.
  Commits SEM nenhuma linha de atribuição: sem "Co-Authored-By", sem "Generated with Claude Code", sem link de sessão.
  NUNCA git push.
- Regras do projeto: MAX-500 linhas (800 em testes), AureoleDS para cores/dimens/ícones, strings em values/ e
  values-pt-rBR/, @AureolePreview privado, testes unitários junto do código novo.
- Em ajustes-redesign, nada pode sumir do app: conferir o inventário do IDEIA.md no fim de cada fase.
- Dúvida de produto nova: registrar em DECISOES.md da spec (D-06+), seguir com a opção mais conservadora e continuar.
- Item que não fechar: anotar em "Ficou faltando" no PROGRESSO.md, commitar o que ficou pronto e seguir.

No fim, me entregue, separado por spec:
1. git log --oneline dos commits da spec;
2. git log --format=%B <início>..HEAD | grep -iE "co-authored|generated with|claude" vazio;
3. tabela dos 7 gates (passou/falhou, tempo);
4. veredito visual por tela;
5. o que ficou faltando e decisões novas;
6. o checklist do aparelho do PLANO.md.
```

## Recado para a execução em andamento (17/11 00:40: fase 3 nova na ajustes-redesign)

```
A spec specs/ajustes-redesign/ ganhou uma fase nova vinda do teste no aparelho: a fase 3 agora é "Ajustes do teste
no aparelho" (Opus, RF-12 a RF-16, decisões D-08 e D-09) e os gates passaram para a fase 4. Termine a fase 2 como
está, commite, e antes de seguir releia SPEC.md, PLANO.md, DECISOES.md e PROGRESSO.md da ajustes-redesign e
specs/EXECUTAR-AJUSTES-E-FUNDO.md. Depois rode a fase 3 nova (subagente model "opus", descrição
"ajustes-redesign · Fase 3: ajustes do teste no aparelho") e a fase 4 de gates, e só então a editar-foto-fundo.
Mesmas regras: um commit por fase sem atribuição, sem push, gates só na fase de gates.
```

## Se a execução já estiver rodando e a spec mudar

```
As specs em specs/ajustes-redesign/ e specs/editar-foto-fundo/ foram atualizadas. Releia SPEC.md, PLANO.md e
DECISOES.md de cada uma (versões em disco) antes de continuar. Se uma fase já commitada não cobre um RF novo dela,
implemente o que falta num commit complementar "(fase N)". Depois siga a ordem e as regras de
specs/EXECUTAR-AJUSTES-E-FUNDO.md.
```
