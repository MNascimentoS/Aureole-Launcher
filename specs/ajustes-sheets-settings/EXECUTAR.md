# Como executar: ajustes-sheets-settings

Tudo decidido (D-01 a D-06). O Claude Code roda as 4 fases em sequência, sem parar: Sonnet nas fases 1 e 2, Opus na
fase 3, gates só na fase 4. Um commit por fase, sem atribuição, sem push. Você só verifica no fim.

## Antes de rodar (1 min)

1. Árvore limpa: `git status` sem mudanças (fora `specs/ajustes-sheets-settings/`, que entra no commit da fase 1).
2. Branch: o prompt cria `feat/ajustes-sheets-settings` se você estiver na `main`; em outra branch, ele usa a atual.
3. Opcional, para garantir commits sem coautor mesmo que o agente esqueça: em `.claude/settings.local.json` do repo,
   `{ "includeCoAuthoredBy": false }` (se a sua versão usa `attribution`, `{ "attribution": { "commit": "", "pr": "" } }`).
4. No painel: `/spec ajustes-sheets-settings` para o Órbita acompanhar este plano.

## Prompt (colar no Claude Code, na raiz do repo)

```
Execute a spec specs/ajustes-sheets-settings/ do começo ao fim, sem parar para perguntar.

Leia antes: AGENTS.md, todos os .md de agents/, e em specs/ajustes-sheets-settings/ os arquivos SPEC.md, PLANO.md,
DECISOES.md e EXECUTAR.md. O protótipo de referência visual é specs/ajustes-sheets-settings/prototipo.html. Todas as
decisões estão respondidas em DECISOES.md: siga-as.

Branch: se estiver na main, crie e use feat/ajustes-sheets-settings. Caso contrário, use a branch atual.

Ordem e modelo (cada fase em um subagente com o modelo indicado, uma de cada vez, esperando a anterior terminar):
  1. Fase 1 – Polimento visual ............ subagente com model "sonnet"
  2. Fase 2 – Salvar ao fechar + Desfazer . subagente com model "sonnet"
  3. Fase 3 – Movimento .................... subagente com model "opus"
  4. Fase 4 – Gates, correções, visual ..... subagente com model "sonnet"; a revisão visual em um subagente "opus"
Passe a cada subagente: o texto da fase no PLANO.md, os RF dela no SPEC.md, as decisões, e as Regras de execução.

Regras (valem para todas as fases):
- Gates SÓ na fase 4. Nas fases 1 a 3 não rode detekt, lintDebug, a suíte inteira de testDebugUnitTest,
  jacoco, screenshot tests nem assembleDebug. Dentro da fase, só ./gradlew compileDebugKotlin e os testes da
  própria fase com --tests. Um comando Gradle por chamada, com o nome do task por extenso, em segundo plano.
  Nunca editar arquivos com build rodando.
- Ao fim de cada fase: atualizar specs/ajustes-sheets-settings/PROGRESSO.md (notas da fase e coluna "Feita"),
  git add dos arquivos da fase e git commit com a mensagem indicada no PLANO.md.
- Commits SEM nenhuma linha de atribuição: sem "Co-Authored-By", sem "Generated with Claude Code", sem link de
  sessão. Apenas o título "feat(ajustes-sheets-settings): ... (fase N)" (ou "chore(...)" na fase 4) e, se quiser,
  um corpo curto com o que mudou. NUNCA git push.
- Regras do projeto: MAX-500 linhas (800 em testes), sem @Suppress, sem baseline, sem afrouxar regra, AureoleDS para
  cores/dimens/ícones, toda string nova em values/ e values-pt-rBR/, @AureolePreview privado em componente novo.
- Dúvida de produto nova: registre em DECISOES.md como D-07+, siga com a opção mais conservadora e continue.
- Se uma fase não conseguir terminar um item, registre em PROGRESSO.md ("Ficou faltando"), faça o commit do que
  ficou pronto e siga para a próxima fase. Não pare a execução.

Fase 4: gates na ordem do PLANO.md (detekt --auto-correct, lintDebug, testDebugUnitTest,
jacocoTestCoverageVerification, updateDebugScreenshotTest, validateDebugScreenshotTest, assembleDebug), corrigindo na
causa até todos passarem. Depois, revisão visual dos screenshots contra as telas 1, 3, 4, 5, 6 e 11 do protótipo
(igual / quase / diferente), ajustes do que for "diferente", gates afetados de novo, commit da fase 4.

No fim, confira e me entregue:
1. git log --oneline <ponto de partida>..HEAD (4 commits, um por fase, mais commits de correção se houver);
2. git log --format=%B <ponto de partida>..HEAD | grep -iE "co-authored|generated with|claude" deve sair vazio;
3. tabela dos 7 gates com passou/falhou e tempo;
4. veredito visual por tela;
5. o que ficou faltando por fase e decisões novas (D-07+), se houver;
6. o checklist do aparelho do PLANO.md, para eu testar.
```

## Depois que terminar

- Conferir o relatório final e `PROGRESSO.md`.
- Rodar no aparelho o checklist do fim do `PLANO.md` e marcar a coluna "Aparelho".
- O push fica com você (ex.: junto da próxima `release/0.9.x`).
