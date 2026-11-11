---
description: Executa uma fase do plano de bottom sheets para uma mão
argument-hint: <número da fase, de 1 a 9>
---
Execute a fase $ARGUMENTS de `specs/bottom-sheets/one-hand-plan.md`.

1. Leia a seção da fase, `specs/bottom-sheets/progress.md` e as referências citadas em `specs/bottom-sheets/ref/`. Se uma fase anterior (a ordem está no plano) não estiver marcada como concluída em `progress.md`, avise e pare.
2. **Antes de planejar, leia o código que já existe** para esta fase (a seção "Já existe" do plano lista os arquivos). Reaproveite; só crie o que falta. Trabalhe na branch atual.
3. Mostre um plano curto (arquivos que mudam e por quê, subagente e modelo, estimativa) e **espere meu ok**.
4. Delegue a implementação ao subagente indicado na tabela do plano (`sheet-gestures`, `sheet-ui` ou `sheet-chores`). Ele já roda no modelo certo. Se o escopo for grande, divida por tela e rode uma de cada vez.
5. Rode os gates na ordem: `./gradlew detekt --auto-correct`, `testDebugUnitTest`, `jacocoTestCoverageVerification`, `validateDebugScreenshotTest`, `assembleDebug`. Corrija o que falhar, exceto divergência de screenshot: aí pare e me pergunte.
6. Peça ao subagente `visual-reviewer` para comparar os screenshots novos com as referências da fase.
7. Atualize `specs/bottom-sheets/progress.md`: marque as caixas feitas, anote o resultado dos gates, o veredito visual e o que ficou faltando.
8. Termine com: o que mudou, resultado dos gates, veredito visual, o que ficou faltando e o que eu preciso testar no aparelho. Não faça commit nem push.
