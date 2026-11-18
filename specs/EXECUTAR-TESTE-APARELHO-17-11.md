# Executar: teste-aparelho-17-11

**Branch:** `release/0.9.44` (depois do lote 17/11) · **Spec:** `specs/teste-aparelho-17-11/` · 2 fases, ~6,5–9 h.

Retomada: igual ao lote. O prompt agenda um lembrete a cada 30 min na própria sessão para continuar se os tokens
acabarem; deixe o Claude Code aberto e as permissões de edição, `./gradlew` e `git` liberadas.

## Prompt (colar no Claude Code, na raiz do repo)

```
Execute specs/teste-aparelho-17-11/ do começo ao fim, sem parar para perguntar, na branch release/0.9.44.
Leia antes AGENTS.md, agents/, e SPEC.md, PLANO.md e DECISOES.md da spec. Referência visual: aba 6 de
specs/prototipo-lote-17-11.html (abrir só se precisar). Economize tokens: leia só os arquivos que vai mudar.

Antes da primeira fase: agende nesta sessão um lembrete recorrente a cada 30 minutos (CronCreate/ScheduleWakeup ou
/loop 30m, o que existir) com: "teste-aparelho-17-11: se specs/teste-aparelho-17-11-FEITO.md não existe e nenhuma fase
está rodando, retome de onde parou (git log + PROGRESSO.md), termine e commite uma fase pela metade e siga. Se já
terminou, apague este lembrete." Se não houver agendamento, avise e siga.

Cada fase em um subagente com o modelo do PLANO.md, uma de cada vez; a descrição começa com
"teste-aparelho-17-11 · Fase N:".

Regras:
- Gates só na fase 2. Na fase 1: ./gradlew compileDebugKotlin e os testes da fase com --tests. Um comando Gradle
  por chamada, nome por extenso, em segundo plano. Nunca editar com build rodando.
- Ao fim de cada fase: atualizar PROGRESSO.md e commitar com a mensagem do PLANO.md, SEM nenhuma linha de atribuição
  (sem Co-Authored-By, sem "Generated with Claude Code", sem link). NUNCA git push.
- Regras do projeto: MAX-500 linhas, sem @Suppress, AureoleDS, strings em values/ e values-pt-rBR/, testes junto.
- Limite de uso, rate limit (429), sobrecarga (529) ou rede: esperar (300 s, 600 s, depois 1200 s) e tentar de novo,
  sem pular fase.
- Dúvida nova: D-03+ em DECISOES.md com a opção conservadora; item que não fechar vai para "Ficou faltando".
- No fim da fase 2: criar specs/teste-aparelho-17-11-FEITO.md com o relatório, commitar e apagar o lembrete.

Relatório final: git log --oneline da spec; checagem de atribuição vazia; tabela dos 7 gates; caminho dos prints; causa encontrada no RF-01; quais sheets mudaram no RF-10; causa do preso em paisagem (RF-12); ficou faltando; o que testar no aparelho.
```
