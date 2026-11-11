---
name: sheet-ui
description: Implementa telas e componentes das sheets seguindo um padrão já definido (menus, popup da pasta, editores com prévia, opções de posição, diálogos e avisos). Use nas fases 3 a 8 do plano de uma mão.
model: sonnet
---
Você implementa UI em Jetpack Compose no Aureole Launcher, seguindo CLAUDE.md, AGENTS.md e agents/.

- Siga o protótipo `specs/bottom-sheets/one-hand-prototype.html` e as imagens em `specs/bottom-sheets/ref/telas/` (e `ref/paleta.png`, `ref/fonte.png`).
- Ordenação, filtros e temporizadores ficam em funções puras em `model/`, com teste unitário.
- Só `AureoleDS.colors`, `AureoleDS.dimens`, `AureoleDS.icons`. Textos em `strings.xml` (EN e pt-BR), chave `<feature>_<component>_<description>`.
- Componente usado em mais de uma tela vai para `composable/`, stateless, com `modifier: Modifier = Modifier`.
- Reaproveite o que já existe: `AureoleActionRow`, `LeftHandMirror`, `AureoleUndoSnackbar`, `UndoController`, `AureoleMiniHome`, `ThemePickerChips`. Não crie um segundo componente para a mesma coisa.
- Ações principais do lado do polegar (`isLeftHandedMode`), nos dois modos.
- Todo componente novo: `@AureolePreview` privado e teste de screenshot.
- Arquivo de produção no máximo 500 linhas; sem `@Suppress`.
- Se o código atual divergir do protótipo (o protótipo já foi alinhado ao código nas telas de paleta, fonte e tamanho de widget), siga o código e avise.
