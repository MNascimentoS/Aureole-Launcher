# 🧭 Master Steering Index: Aureole Launcher

Este documento funciona como o **Índice Mestre de Regras** para Assistentes de IA e Desenvolvedores trabalhando no **Aureole Launcher**. Dependendo da tarefa solicitada, consulte e siga as especificações correspondentes:

---

## 🗺️ Guia de Navegação de Specs

| Se a sua tarefa for... | Leia e Siga estas Especificações |
| :--- | :--- |
| **Arquitetura Geral / Criar Telas ou Features** | [`aureole_architecture.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureole_architecture.md) & [`aureole_providers.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureole_providers.md) |
| **Mapear Figma / Tokens Visuais / Temas** | [`aureoleo_design_system.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureoleo_design_system.md) |
| **Criar Componentes do Design System (`core/designsystem/components`)** | [`aureole_components.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureole_components.md) & [`aureoleo_design_system.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureoleo_design_system.md) |
| **Gerenciar Ações e Estado na Árvore de UI (`CompositionLocalProvider`)** | [`aureole_providers.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureole_providers.md) |
| **Criar ou Atualizar Testes (Unitários & Screenshot Tests)** | [`aureole_testing.md`](file:///home/mateus/Workpace/AndroidStudioProjects/AureoleLauncher/.gemini/aureole_testing.md) |

---

## ⚡ Regra de Ouro e Checklist de Entrega do Projeto
Toda nova funcionalidade DEVE ser acompanhada de **Testes Unitários (`app/src/test/`)** e **Testes de Screenshot (`app/src/screenshotTest/`)**.

Antes de concluir qualquer tarefa ou PR, garanta a execução e aprovação dos comandos abaixo:
1. `./gradlew detekt --auto-correct` (Deve rodar com 0 a no máximo 5 issues).
2. `./gradlew testDebugUnitTest` (Obrigatório: 100% de aprovação).
3. `./gradlew validateDebugScreenshotTest` (Se falhar por divergência de imagens: executar `./gradlew updateDebugScreenshotTest` e **PARAR**. Notifique o usuário que novas imagens foram geradas em `app/src/screenshotTestDebug/reference/`, deixando claro que ele **DEVERÁ REVISAR E VALIDAR AS NOVAS IMAGENS MANUALMENTE** antes de realizar o commit).
4. `./gradlew assembleDebug` (Compilação limpa).
