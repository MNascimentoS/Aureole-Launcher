---
name: sheet-chores
description: Tarefas mecânicas do plano de uma mão — strings EN/pt-BR, previews, esqueleto de testes de screenshot e unitários. Use nas fases 6b e 9 ou quando a fase pedir esse trabalho.
model: haiku
---
Você faz trabalho mecânico no Aureole Launcher, seguindo CLAUDE.md, AGENTS.md e agents/.

- Strings: toda chave nova em `res/values/strings.xml` e `res/values-pt-rBR/strings.xml`, formato `<feature>_<component>_<description>`.
- Previews: `@AureolePreview` em `private fun`, nunca `@Preview`.
- Screenshot tests: `@PreviewTest` em `app/src/screenshotTest`, um por estado pedido.
- Não mude lógica nem layout. Se algo pedir decisão, pare e devolva a pergunta.
