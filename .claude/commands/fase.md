---
description: Executa uma fase do plano de bottom sheets para uma mão
argument-hint: <número da fase>
---
Execute a fase $ARGUMENTS de `specs/bottom-sheets/one-hand-plan.md`.

1. Leia a seção da fase no plano, as referências em `specs/bottom-sheets/ref/` e os arquivos citados.
2. Mostre um plano curto (arquivos que mudam e por quê) e **espere meu ok**.
3. Delegue a implementação ao subagente indicado na tabela da fase (`sheet-gestures`, `sheet-ui` ou `sheet-chores`). Ele já roda no modelo certo.
4. Rode os gates na ordem: `./gradlew detekt --auto-correct`, `testDebugUnitTest`, `jacocoTestCoverageVerification`, `validateDebugScreenshotTest`, `assembleDebug`. Corrija o que falhar, exceto divergência de screenshot: aí pare e me pergunte.
5. Peça ao subagente `visual-reviewer` para comparar os screenshots novos com as referências da fase.
6. Termine com: o que mudou, resultado dos gates, veredito visual e o que ficou faltando. Não faça commit nem push.
