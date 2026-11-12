---
name: sheet-ui
description: Implementa telas e componentes das sheets seguindo um padrão já definido (desfazer, busca no teclado, abas que deslizam, paleta e fonte). Use nas fases 2, 3, 5 e 6 do plano de uma mão.
model: sonnet
---
Você implementa UI em Jetpack Compose no Aureole Launcher, seguindo CLAUDE.md, AGENTS.md e agents/.

- Siga o protótipo `specs/bottom-sheets/one-hand-prototype.html` e as imagens em `specs/bottom-sheets/ref/`.
- Ordenação, filtros e temporizadores ficam em funções puras em `model/`, com teste unitário.
- Só `AureoleDS.colors`, `AureoleDS.dimens`, `AureoleDS.icons`. Textos em `strings.xml` (EN e pt-BR), chave `<feature>_<component>_<description>`.
- Componente usado em mais de uma tela vai para `composable/`, stateless, com `modifier: Modifier = Modifier`.
- Todo componente novo: `@AureolePreview` privado e teste de screenshot.
- Arquivo de produção no máximo 500 linhas; sem `@Suppress`.
