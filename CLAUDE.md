# Aureole Launcher — Claude Code

As regras do projeto ficam em AGENTS.md e em agents/. Elas são carregadas aqui para valerem desde o primeiro turno:

@AGENTS.md
@agents/index.md
@agents/aureole_architecture.md
@agents/aureole_components.md
@agents/aureole_design_system.md
@agents/aureole_providers.md
@agents/aureole_testing.md

## Combinados com o Mateus
- Responder em PT-BR, com termos técnicos em inglês. Curto e direto, código primeiro.
- Em tarefa grande: mostrar um plano curto e esperar o ok.
- Mudança visual: sempre gerar print (screenshot test ou `@AureolePreview`) e comparar com a referência em `specs/bottom-sheets/ref/`.
- **Nunca** fazer `git commit` nem `git push`. O Mateus revisa e sobe na `release/0.9.13`.
- Se `validateDebugScreenshotTest` falhar: parar e perguntar antes de rodar `updateDebugScreenshotTest`.

## Trabalho atual: bottom sheets para uma mão
- Plano e critérios: `specs/bottom-sheets/one-hand-plan.md`
- Protótipo de referência: `specs/bottom-sheets/one-hand-prototype.html` (abrir no navegador)
- Rodar uma fase: `/fase <número>`
