---
description: Executa uma fase do plano de polimento das action sheets
argument-hint: <número da fase, de 1 a 5>
---
Execute a fase $ARGUMENTS de `specs/bottom-sheets/actionsheets-polish-plan.md`.

1. Leia a seção da fase, `specs/bottom-sheets/actionsheets-polish-progress.md` e as referências `specs/bottom-sheets/ref/polish-*` da fase. Se uma fase anterior não estiver marcada como concluída no progresso, avise e pare.
2. **Antes de planejar, leia o código que já existe.** O plano foi escrito sobre a `0.9.18`: localize cada arquivo e símbolo na branch atual e reaproveite o que o plano lista em "Já existe".
3. Mostre um plano curto (arquivos que mudam e por quê, subagente e modelo, estimativa) e **espere meu ok**.
4. Delegue a implementação ao subagente da tabela do plano (`sheet-ui` ou `sheet-chores`). Diga a ele que o protótipo desta fase é `specs/bottom-sheets/actionsheets-polish-prototype.html` e que as referências são `ref/polish-*`, e não as do `one-hand-prototype.html`.
5. Rode os gates na ordem: `./gradlew detekt --auto-correct`, `testDebugUnitTest`, `jacocoTestCoverageVerification`, `validateDebugScreenshotTest`, `assembleDebug`. Corrija o que falhar, exceto divergência de screenshot: aí pare e me pergunte.
6. Peça ao `visual-reviewer` para comparar os screenshots novos com as referências `polish-*` da fase, considerando as diferenças aceitas no plano.
7. Atualize `actionsheets-polish-progress.md`: marque as caixas feitas e anote o resultado dos gates, o veredito visual e o que ficou faltando.
8. Termine com: o que mudou, resultado dos gates, veredito visual, o que ficou faltando e o que eu preciso testar no aparelho. Não faça commit nem push.
