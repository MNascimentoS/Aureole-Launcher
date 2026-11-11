# Como rodar no Claude Code

1. Copie a configuração (a ponte não escreve em `.claude/`):
   ```bash
   cd ~/Workpace/AndroidStudioProjects/AureoleLauncher
   mkdir -p .claude && cp -r specs/bottom-sheets/claude-setup/.claude/. .claude/
   ```
2. Abra uma sessão **nova**: `claude` (comandos e subagentes só carregam ao abrir).
3. Veja onde está: `/estado`
4. Rode uma fase por vez, com espaço antes do número: `/fase 1`
5. Ele mostra o plano e espera o seu ok. Depois roda os gates e a revisão visual.
6. Quando terminar a fase, teste no aparelho, marque a caixa “Aparelho” em `progress.md`, faça o commit e o push você mesmo, e abra uma sessão nova para a próxima fase (contexto limpo, menos token).

## Dicas

- Modelo principal da sessão: `/model sonnet` está bom; os subagentes já trocam para Opus ou Haiku sozinhos.
- Se um gate de screenshot divergir, ele para e pergunta. Decida se a mudança é esperada e rode `./gradlew updateDebugScreenshotTest`.
- Para voltar só uma tela de uma fase grande: `/fase 5` e peça “só o relógio” no ok do plano.
