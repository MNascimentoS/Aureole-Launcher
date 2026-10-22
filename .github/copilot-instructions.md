# 🤖 System Prompt / AI Project Instructions

Antes de responder ou gerar código neste projeto, você DEVE ler e seguir estritamente as regras de arquitetura, design system, componentes, providers e testes definidas nos arquivos do diretório `.gemini/`:

1. Índice Mestre: `.gemini/index.md`
2. Arquitetura Geral: `.gemini/aureole_architecture.md`
3. Design System & Figma: `.gemini/aureoleo_design_system.md`
4. Engenharia de Componentes: `.gemini/aureole_components.md`
5. Scoped Providers & Actions: `.gemini/aureole_providers.md`
6. Estratégia de Testes: `.gemini/aureole_testing.md`

## 🚨 Guardrails Invioláveis para Gerações de Código
- **Tamanho Máximo de Arquivo:** NENHUM arquivo pode exceder 300 linhas (Exceção: `AureoleIcons.kt`). Se passar, divida em subcomponentes.
- **Detekt & Proibição de @Suppress:** É PROIBIDO usar `@Suppress("LongMethod")`, `@Suppress("ComplexMethod")`, `@Suppress("LongParameterList")`, `@Suppress("LongParamList")` ou alterar o `detekt.yml` para contornar limitações. Divida as funções, objetos de parâmetro ou arquivos.
- **Isolamento do Design System:** Componentes em `core/designsystem/components` NUNCA devem importar pacotes de `feature/*` (ex: `LocalHomeActions`) nem iniciar `Activity`s diretamente.
- **Tokens de Layout vs Motion/Physics:** Use `AureoleDS.dimens` para layout estático (paddings, margins, icon sizes). NUNCA use tokens de layout para blur (Haze blurRadius), física de gestos ou animações.
- **Complexidade:** Máximo de 5 níveis de indentação por função. Use Guard Clauses (Early Returns).
- **Testes Obrigatórios para Novas Features:** Toda nova funcionalidade, tela, diálogo ou componente DEVE possuir **Testes Unitários (`app/src/test/`)** e **Testes de Screenshot (`app/src/screenshotTest/`)**.
- **Falha em Validate Screenshots:** Se `./gradlew validateDebugScreenshotTest` falhar por divergências visuais: **PARAR**. Notifique o usuário e pergunte se deseja executar `./gradlew updateDebugScreenshotTest` para gerar as novas imagens em `app/src/screenshotTestDebug/reference/`, deixando claro que ele **DEVERÁ REVISAR E VALIDAR AS NOVAS IMAGENS MANUALMENTE** antes de realizar o commit.

Toda alteração de código deve respeitar a Clean Architecture, Package-by-Feature, limite do Detekt (max 5 issues) e ser validada pelos testes unitários e de screenshot.
