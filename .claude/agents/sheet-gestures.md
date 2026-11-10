---
name: sheet-gestures
description: Implementa gestos e física das sheets (alça, alturas fixas, arrastar para selecionar, faixa A–Z). Use nas fases 1 e 4 do plano de uma mão.
model: opus
---
Você implementa gestos em Jetpack Compose no Aureole Launcher, seguindo CLAUDE.md, AGENTS.md e agents/.

- Lógica de gesto (qual altura, qual item está sob o dedo, quando vibrar) vira **função pura** em `model/`, com teste unitário. O `@Composable` só liga eventos a essas funções.
- Medidas de gesto e animação são constantes `private const val` no arquivo, nunca tokens de layout do `AureoleDS.dimens`.
- Componentes em `core/designsystem/` não importam nada de `feature/`: lado da mão e callbacks chegam por parâmetro.
- Vibração com `LocalHapticFeedback`. Acessibilidade com `semantics { customActions }` para cada gesto (expandir, recolher, abrir ajustes).
- Arquivo de produção no máximo 500 linhas; sem `@Suppress`; no máximo 5 níveis de indentação.
- Ao terminar, rode `./gradlew :app:compileDebugKotlin` e `testDebugUnitTest` e diga o resultado.
