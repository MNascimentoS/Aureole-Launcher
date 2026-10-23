# 🤖 System Prompt / AI Project Instructions

# 🛑 REGRA INVIOLÁVEL DE INICIALIZAÇÃO DE CHAT (STARTUP WORKFLOW)
No PRIMEIRO turno de cada conversa (início de um novo chat), ANTES de responder à solicitação do usuário, planejar ou executar qualquer ação/código:
1. Você DEVE ler e carregar o arquivo índice das steerings:
   - `.gemini/index.md`
2. Você DEVE ler e carregar o contexto de TODOS os arquivos de diretivas do projeto:
   - `.gemini/aureole_architecture.md`
   - `.gemini/aureole_providers.md`
   - `.gemini/aureole_components.md`
   - `.gemini/aureoleo_design_system.md`
   - `.gemini/aureole_testing.md`
3. Somente após concluir a leitura prévia de todas as steerings, prossiga com a tarefa solicitada pelo usuário.

Antes de responder ou gerar código neste projeto, você DEVE ler e seguir estritamente as regras de arquitetura, design system, componentes, providers e testes definidas nos arquivos do diretório `.gemini/`:

1. Índice Mestre: `.gemini/index.md`
2. Arquitetura Geral: `.gemini/aureole_architecture.md`
3. Design System & Figma: `.gemini/aureoleo_design_system.md`
4. Engenharia de Componentes: `.gemini/aureole_components.md`
5. Scoped Providers & Actions: `.gemini/aureole_providers.md`
6. Estratégia de Testes: `.gemini/aureole_testing.md`

## 🚨 Guardrails Invioláveis para Gerações de Código
- **Tamanho Máximo de Arquivo:** NENHUM arquivo pode exceder 500 linhas (Exceção: `AureoleIcons.kt`). Se passar, divida em subcomponentes.
- **Detekt & Proibição de @Suppress:** É PROIBIDO usar `@Suppress("LongMethod")`, `@Suppress("ComplexMethod")`, `@Suppress("LongParameterList")`, `@Suppress("LongParamList")` ou alterar o `detekt.yml` para contornar limitações. Divida as funções, objetos de parâmetro ou arquivos.
- **Isolamento do Design System:** Componentes em `core/designsystem/components` NUNCA devem importar pacotes de `feature/*` (ex: `LocalHomeActions`) nem iniciar `Activity`s diretamente.
- **Tokens de Layout vs Motion/Physics:** Use `AureoleDS.dimens` para layout estático (paddings, margins, icon sizes). NUNCA use tokens de layout para blur (Haze blurRadius), física de gestos ou animações.
- **Complexidade:** Máximo de 5 níveis de indentação por função. Use Guard Clauses (Early Returns).
- **Testes Obrigatórios para Novas Features:** Toda nova funcionalidade, tela, diálogo ou componente DEVE possuir **Testes Unitários (`app/src/test/`)** e **Testes de Screenshot (`app/src/screenshotTest/`)**.
- **Release Notes em Linguagem Leiga (Sem Jargões Técnicos):** Antes de commitar/fazer push de uma release, a IA DEVE atualizar os textos do **Release Notes** (`ReleaseNotesRepository.kt` e `ReleaseNotesData.kt`) em linguagem simples, curta e compreensível por usuários leigos (focando em benefícios práticos). É **estritamente proibido** utilizar termos técnicos (como R8, ProGuard, Room, DB, A11y, Lifecycle Observers, Coroutines, Refatoração, Tokenização, KSP, etc.).
- **Testes sem Hardcode de Versões:** Testes unitários do `ReleaseNotesRepositoryTest` NUNCA devem hardcodear strings de versão (como "0.8.16"); devem testar invariantes e estrutura de contrato para não quebrar a cada release.
- **Deploy no CI Condicional ao PR Check:** No workflow do CI (`deploy_internal.yml`), a etapa de deploy interno só pode iniciar se a validação do PR (`pr_check`) for concluída com sucesso (`needs.pr_check.result == 'success'`).
- **Proibição de Merge com Checks em Execução:** O repositório exige Branch Protection Rules ativas no GitHub para que o botão "Merge pull request" fique bloqueado enquanto os checks (`PR Validation Check` e `Build & Deploy Internal Release`) estiverem pendentes, em execução ou com falha.
- **Falha em Validate Screenshots:** Se `./gradlew validateDebugScreenshotTest` falhar por divergências visuais: **PARAR**. Notifique o usuário e pergunte se deseja executar `./gradlew updateDebugScreenshotTest` para gerar as novas imagens em `app/src/screenshotTestDebug/reference/`, deixando claro que ele **DEVERÁ REVISAR E VALIDAR AS NOVAS IMAGENS MANUALMENTE** antes de realizar o commit.

Toda alteração de código deve respeitar a Clean Architecture, Package-by-Feature, limite do Detekt (max 5 issues) e ser validada pelos testes unitários e de screenshot.
